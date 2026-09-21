# Session 1 - Service communication decisions and an idempotent consumer for MVP2

## Objective

For every interaction between the four microservices defined in ADR-004: decide whether
it is synchronous or asynchronous and justify it, choose the technology (REST or gRPC for
the synchronous ones, topics or queues for the asynchronous ones), and design at least
one idempotent consumer.

This is a **design**. Nothing described here is implemented: there is no message broker
and no event consumer in the Backend code today (checked in Backend `main`; the only
idempotent piece is the platform administrator seeder). Section 5 lists exactly what is
done and what is not.

## 1. Where the interactions come from

Sources: ADR-004 (the four services), `02-domain/domain-events.md` (34 candidate events,
each with its origin and its consumers) and `02-domain/domain-map.md` (relationships
between contexts). Services and their bounded contexts:

| Service | Bounded contexts |
|---|---|
| `platform-service` | Tenant Management, Identity and Access, Audit and Traceability |
| `commercial-reservations-service` | Customers, Operational Catalog, Discounts and Commercial Rules, Reservations |
| `operations-costs-service` | Operational Execution, Operational Costs |
| `cash-reports-service` | Cash and Consolidation, Reports and Dashboard |

Of the 34 events, 8 have no consumer outside the publishing service (in-process, no
network) and 26 cross a service boundary. For the two establishment-association events
the catalog names a "customer-channel projection" as consumer, which is not a defined
service, so they are counted here as internal to `commercial-reservations-service`.
Only interactions that cross a boundary need a
decision here. Creating a reservation, for example, needs catalog, discounts and customer
data, and all of them live in `commercial-reservations-service`, so the main business
path makes no cross-service call at all.

## 2. Synchronous interactions

Rule used: an interaction is synchronous only if the caller cannot continue without the
answer, or a person is waiting for it. Everything else is asynchronous.

| # | Interaction | Decision | Justification |
|---|---|---|---|
| S1 | Portal (Angular) to each service's API: login, create reservation, cash, ... | Sync, REST | A person is waiting for the result. It is already how the MVP1 works. |
| S2 | `operations-costs-service` to `commercial-reservations-service`: read reservation data when recording an execution | Sync, REST | The caller needs the answer to continue, and it is a query, not a fact to broadcast. An HTTP `ReservationClient` for it already exists (local commit `9b7cfaf` in the Backend, not pushed). |
| S3 | Authentication and tenant scope check on every request | No network call: each service validates the JWT locally | A remote call per request would make `platform-service` a single point of failure for every operation. A tenant status change reaches the other services as an event (`TenantInactivated`, section 3). How the verification key is shared is left to each service's spec, as ADR-004 already states. |

### REST or gRPC: REST

1. The contracts will be published as OpenAPI (Session 2), and the project already has an
   OpenAPI contract from week 04 (`04-week/hu-status/WeeklySummary/WeeklyChallenge/MVP1-Contrato-API-openapi.yaml`).
2. The stack is Spring Boot with a team of two: REST needs no extra tooling and can be
   checked with `curl`.
3. There is one inter-service synchronous call today (S2) and no internal path with
   the throughput that would justify gRPC.

Accepted cost: JSON over HTTP is heavier than protobuf and typed less strictly than a
`.proto`. Contract tests (Session 2) compensate for the typing. gRPC is revisited only if
a measured need appears.

## 3. Asynchronous interactions

Rule used: a business fact that other services react to, where the publisher must not wait
for the consumers nor depend on them being up.

| # | Direction | Events | Why asynchronous |
|---|---|---|---|
| A1 | commercial to operations-costs | `ReservationConfirmed`, `ReservationRescheduled` | Operations reacts after the fact. A reservation must confirm even if `operations-costs-service` is down. |
| A2 | commercial to cash-reports | Cash: `PaymentValidated`, `ReservationCancelled`, `CreditBalanceRecorded`, `RefundAuthorized`. Reports: reservation, capacity and catalog events | Cash reconciles from validated facts. Reports builds its own read model, because "Reports do not replace transactional source records" (domain map), so it never queries other services at report time. |
| A3 | operations-costs to cash-reports | `OperationalCostRecorded`, `ExecutionRecorded`, `ExecutionStarted` | Same reasoning as A2. |
| A4 | operations-costs to commercial | `ExtraordinaryCancellationRecorded` | The emergency is recorded on the field. The reservation state updates when the event arrives. |
| A5 | cash-reports to commercial | `RefundExecuted` | A person registers the money outflow, so it cannot be a call in the middle of the authorization. The refund flow is authorize, execute, reflect: three steps across two services. |
| A6 | platform to cash-reports | `TenantInactivated` | The catalog lists Reports as a consumer. |
| A7 | every service to platform (Audit) | Audit-relevant events (most events in the catalog list Audit as a consumer) | Auditing must not slow down or block the audited operation, but its evidence must not be lost. See delivery semantics below. |

### Topics or queues: topics

- **Topics (publish/subscribe) for every domain event.** One event has several consumers
  (`ReservationConfirmed` goes to Operations, Reports and Audit) and the publisher must not
  know them. One topic per publishing service: `platform.events`, `commercial.events`,
  `operations.events`, `cash-reports.events`. The event type travels in the message and
  each consumer filters by type. A topic can be split per context later if a consumer needs
  different retention or scaling.
- **Queues: none needed for MVP2.** A queue fits point-to-point work where exactly one
  worker handles each message (for example sending an email). No interaction in scope
  is like that.
- **The broker product is not chosen here.** ADR-004 leaves it open. Docs
  `05-architecture/README.md` lists "choosing a message broker" and "choosing a
  communication pattern (REST vs gRPC vs events)" as reasons to write an ADR, so both
  this section and the broker choice are ADR material (see section 5).

### Delivery semantics: at-least-once

- At-most-once can lose a payment or an audit record, which is not acceptable here.
- Exactly-once end to end does not exist; it is obtained by combining at-least-once
  delivery with idempotent consumers (section 4). That is the rule for **every** consumer.
- Publisher side: a transactional outbox. The event is written to an `outbox` table in the
  same database transaction as the state change, and a relay publishes it. A crash between
  commit and publish cannot lose the fact.
- Ordering: the routing key is the aggregate id (the reservation id), so the events of one
  reservation reach a consumer in order.

## 4. Idempotent consumer design: Cash handling `PaymentValidated`

**Why this one:** it moves money. A duplicate delivery would register the same income twice
in the cash session. The event catalog already defines it: origin Reservations, consumers
Cash, Reports and Audit, minimum data tenant, reservation, amount, payment status, pending
balance and validator.

Common message envelope for all events (formalized as a JSON Schema in Session 2:
`contracts/events/envelope.v1.schema.json`):

```json
{
  "eventId": "uuid",
  "eventType": "PaymentValidated",
  "schemaVersion": 1,
  "occurredAt": "2026-09-21T10:15:00Z",
  "tenantId": "<tenant-id>",
  "aggregateId": "reservation-uuid",
  "actor": "validator-reference",
  "payload": { "amount": 0, "paymentStatus": "...", "pendingBalance": 0 }
}
```

Deduplication table in the consumer's own database (PostgreSQL syntax as an example; the
engine of each service is still left to its own spec, per ADR-004):

```sql
CREATE TABLE processed_event (
  consumer     VARCHAR(80)  NOT NULL,   -- e.g. 'cash.payment-validated'
  event_id     UUID         NOT NULL,
  tenant_id    TEXT         NOT NULL,   -- a string in the monolith, not a UUID
  processed_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
  PRIMARY KEY (consumer, event_id)
);
```

Handling, in a single database transaction:

```text
on message(event):
  BEGIN
    inserted = INSERT INTO processed_event (consumer, event_id, tenant_id)
               VALUES ('cash.payment-validated', event.eventId, event.tenantId)
               ON CONFLICT DO NOTHING
    if inserted == 0:            -- already processed
      COMMIT; ack; return
    register the income reconciliation entry for event.aggregateId
  COMMIT
  ack
```

The deduplication marker and the business effect commit together or not at all, and the
acknowledgement is sent only after the commit.

| Scenario | What happens | Result |
|---|---|---|
| The broker delivers the same event twice | The second insert conflicts and the event is skipped | One entry |
| Crash after commit, before the ack | The event is redelivered, conflicts, is skipped and acknowledged | One entry |
| Crash before commit | The transaction rolls back, marker and effect together; the redelivery applies it | One entry |
| Two consumer instances receive the same event at once | The primary key lets one insert win; the other skips | One entry |
| The same fact is published again with a new `eventId` | Not detected by this mechanism | Open point below |

Test scenarios to implement in MVP2 (designed, not run): deliver the same event twice and
expect one entry and one `processed_event` row; two concurrent deliveries and expect one
entry; fail after the effect and before the commit and expect zero entries, then the
redelivery produces one.

The same table serves every consumer, because the consumer name is part of the key: Audit,
Reports and Operations use the same pattern.

## 5. Status

- **Done:** the sync or async decision with its justification for every kind of
  cross-service interaction, the REST and topics choices, the delivery semantics, and the
  idempotent consumer design with schema, algorithm, failure analysis and test scenarios.
- **Not done:** no broker, topic, outbox or consumer is implemented, so the idempotent
  consumer is a design and not running code. It belongs to MVP2, with one spec per service.
- **Open points:**
  1. Broker product and the formal record of these decisions: an ADR in Docs, not written yet.
  2. How the services share the JWT verification key: each service's spec (ADR-004).
  3. A business key against republished facts: the catalog's minimum data for
     `PaymentValidated` has no payment attempt reference. Session 2 proposes adding
     `paymentAttemptRef` (`contracts/events/payment-validated.v1.schema.json`) for a
     second, business-level uniqueness check; it still has to be agreed with the
     Reservations side.
