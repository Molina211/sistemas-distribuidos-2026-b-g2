# Session 2 - Versioned contracts, compatibility rules and a contract test for MVP2

## Objective

Publish the contracts of the interactions decided in Session 1, define how they are
versioned and what counts as a compatible change, design a consumer-driven contract test
with its CI job, and slice the MVP2 integration work into stories with testable
acceptance criteria.

As in Session 1, part of this is a design. The contracts and the rules are written; the
contract test and its CI job are **designed, not implemented**. Section 5 states exactly
what is done and what is not.

## 1. Published contracts

Files in `WeeklyChallenge/contracts/`:

| File | Type | Describes | Basis |
|---|---|---|---|
| `reservations-read.openapi.yaml` | OpenAPI 3.0.3 | The two read endpoints `operations-costs-service` calls on the reservations provider: `GET /api/tenants/{tenantId}/reservations/{reservationId}` and `GET /api/tenants/{tenantId}/reservations/pending-execution` | Real endpoints, read from `ReservationClient` (consumer) and `ReservationController`, `OperationController`, `ReservationResponse`, `ErrorResponse` (provider) |
| `events/envelope.v1.schema.json` | JSON Schema | Common envelope of every domain event | Proposed; fields required by the event rules in Docs `02-domain/domain-events.md` |
| `events/payment-validated.v1.schema.json` | JSON Schema | `PaymentValidated`, consumed by Cash (the idempotent consumer of Session 1) | Proposed; payload from the event catalog plus `paymentAttemptRef` |
| `events/reservation-confirmed.v1.schema.json` | JSON Schema | `ReservationConfirmed`, consumed by Operations, Reports and Audit | Proposed; payload from the event catalog |

There is no `.proto`: Session 1 chose REST for every synchronous interaction.

The OpenAPI file is **consumer-driven**: only the fields `operations-costs-service` really
reads are contractual (`reservationId`, `reservationStatus`, `finalizedBy`, `finalizedAt`),
although the provider response has 35 fields. Checked against the provider: the four
fields exist there with the same names and types. Two details taken from the code that
matter for compatibility: `reservationStatus` is serialized as a text label
(`Pendiente de pago`, `Confirmada`, `En ejecucion`, `Finalizada`, `Cancelada`), and `tenantId`
is a string, not a UUID.

The event schemas are proposals for a broker that does not exist yet. Two points are left
open on purpose and are marked inside the files: the inner structure of
`allocatedCapacity`, and `paymentAttemptRef`, which is not in the catalog's minimum data
for `PaymentValidated` and has to be agreed with the Reservations side.

Where they live: here, as delivery evidence. Docs `07-api/contracts/openapi/` holds
generic template files (an "Auth Service API"), not project contracts. When the service
repositories exist, each contract moves to its provider's repository.

## 2. Versioning and compatibility rules

**Versions.** REST contracts carry a semantic version in `info.version`. The endpoints that
exist today have no version prefix and are version 1. A breaking change is published under
a new path prefix (`/api/v2/...`) next to the old one. Events carry `schemaVersion` (the
MAJOR number) in the envelope; the topic does not change, and during a migration both
versions of an event are published.

| Change | Compatible? | Version |
|---|---|---|
| Add an optional field to a response or event payload | Yes | MINOR |
| Add an endpoint or a new event type | Yes | MINOR |
| Add a value to an enumeration (for example a new reservation status) | Yes, because consumers must treat unknown values as "unknown" (below) | MINOR |
| Fix documentation or a description | Yes | PATCH |
| Remove or rename a field | No | MAJOR |
| Change the type or the meaning of a field | No | MAJOR |
| Make an optional field required, or tighten validation | No | MAJOR |
| Remove an enumeration value or change an error code | No | MAJOR |

**Consumer rules (tolerant reader).** Read only the fields you need. Ignore fields you do
not know. Treat an unknown enumeration value as "unknown", never as an error. Never depend
on field order. The current `ReservationSummary` already works this way: it reads 4 of the
35 fields.

**Provider rules.** Inside one MAJOR version, changes are additive only. A breaking change
gets a new MAJOR, and the old one stays available until the deprecation is complete.

**Deprecation and removal.** A deprecated element is marked (`deprecated: true` in OpenAPI,
a note in the event schema) and announced. An old MAJOR is removed only when no consumer
contract targets it any more: the provider's verification of consumer contracts (section 3)
is what proves that.

**Process.** A contract change goes in the same pull request as the code that changes it,
and the provider's contract verification must pass before merge.

## 3. Consumer-driven contract test (design)

**Pair.** Consumer: `operations-costs-service` (`ReservationClient`). Provider: the monolith
today, `commercial-reservations-service` after the extraction. The consumer scaffold is a
folder inside the Backend repository today, so consumer and provider can share one
workflow at first.

**Interactions the consumer defines** (the contract it needs):

| # | Given (provider state) | Request | Expected response |
|---|---|---|---|
| 1 | The reservation exists in tenant T | `GET /api/tenants/T/reservations/{id}` | 200; JSON with `reservationId` (uuid string) and `reservationStatus` (string); `finalizedBy` and `finalizedAt` optional |
| 2 | The reservation does not exist in tenant T | `GET /api/tenants/T/reservations/{id}` | 404; JSON `{"error": "not_found", "message": <string>}` |
| 3 | Tenant T has reservations pending execution | `GET /api/tenants/T/reservations/pending-execution` | 200; JSON array of objects |

**Tool and flow.** Pact for the JVM with JUnit 5. The consumer test runs `ReservationClient`
against a mock server and writes a pact file with those three interactions. The provider
test replays the pact against the real provider, creating the "given" state first, and
fails if any response no longer satisfies it.

**What it catches.** If the provider removes or renames `finalizedAt`, or changes
`reservationId` to a non-UUID, the provider verification fails and the pull request stays
red, before the consumer breaks in production. Adding an unknown field or a new status
does not fail it, which is the tolerant-reader rule in action.

**CI job (sketch, not added to any repository):**

```yaml
name: contract-tests
on:
  pull_request:
    branches: [develop]
jobs:
  consumer:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21' }
      - run: mvn -B test                                     # writes the pact file
        working-directory: operations-costs-service          # independent Spring Boot project
      - uses: actions/upload-artifact@v4
        with: { name: pacts, path: operations-costs-service/target/pacts }
  provider:
    needs: consumer
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21' }
      - uses: actions/download-artifact@v4
        with: { name: pacts, path: pacts }
      - run: mvn -B verify                                   # verifies the pact against the provider
```

Once the services live in separate repositories, the pact file travels as an artifact or
through a Pact Broker, which also gives a `can-i-deploy` check.

**Why it is not implemented here.** No CI exists in any repository (the Backend has no
`.github` folder). Adding the workflow and the Pact dependency needs the team's explicit
approval, and the consumer code (`operations-costs-service`) is still unpushed local work.

## 4. MVP2 integration stories

Priorities are proposed (MoSCoW). Each story has acceptance criteria that can be checked.

**INT-01 - Versioned contracts published** (Must)
As a service developer, I want every REST and event contract published with its version,
so that consumers know what they can rely on.
- Each contract file declares its version (`info.version` or `schemaVersion`).
- The OpenAPI file and the JSON Schemas pass validation against their meta-schemas.
- A README lists each contract with its provider and its known consumers.

**INT-02 - Consumer-driven contract test for `ReservationClient`** (Must)
As the operations-costs team, I want the interactions of section 3 verified against the
provider, so that a provider change cannot silently break the client.
- The consumer test writes a pact with the three interactions.
- The provider verification passes against the monolith with the given states set up.
- Renaming `finalizedAt` in the provider's `ReservationResponse` makes the verification fail.

**INT-03 - CI gate for contract tests** (Must)
As the team, I want consumer and provider contract tests to run on every pull request to
`develop`, so that a breaking change is stopped before merge.
- The workflow runs the consumer test and then the provider verification.
- A pull request with the breaking change of INT-02 shows a failed check and cannot be merged.
- The workflow is added only after the team's explicit approval, since it is CI configuration.

**INT-04 - Broker decision recorded as an ADR** (Must)
As the team, I want the broker chosen and recorded, so that the topic design of Session 1
has a concrete platform.
- The ADR in Docs compares RabbitMQ, Kafka and Redis Streams against at-least-once delivery,
  one topic per publishing service, and ordering by aggregate id.
- The ADR is Accepted, with alternatives, consequences and its reviewers filled in.

**INT-05 - Outbox publisher in `commercial-reservations-service`** (Should)
As the Reservations owner, I want `PaymentValidated` and `ReservationConfirmed` published
through an outbox, so that a crash cannot lose a business fact.
- The state change and its outbox row are written in one transaction; a test that forces a
  failure after the state change finds neither persisted.
- A relay publishes pending rows and marks them; rows that fail are retried.
- Every published message validates against `contracts/events/*.schema.json`.

**INT-06 - Idempotent Cash consumer for `PaymentValidated`** (Must)
As the Cash owner, I want a duplicate delivery to change nothing, so that an income is never
registered twice.
- Delivering the same event twice leaves one entry and one `processed_event` row.
- Two concurrent deliveries of the same event leave one entry.
- A failure before the commit leaves no entry, and the redelivery then produces one.
- A republished fact with a new `eventId` but the same `paymentAttemptRef` is rejected.

**INT-07 - Identity and tenant checks between services** (Should)
As the team, I want each service to verify the caller's identity and tenant, so that
tenant isolation survives the move to separate services (ADR-004 risk).
- A spec states how each service verifies the JWT and where the verification key comes from.
- Every inbound request checks that the tenant in the token matches the `tenantId` in the path.
- A test shows that a token of tenant A cannot read a reservation of tenant B.

## 5. Status

- **Done:** four contract files (one real REST contract derived from code, three proposed
  event schemas); the versioning and compatibility rules; the design of the
  consumer-driven contract test, with its interactions and a CI job sketch; seven MVP2
  integration stories with testable acceptance criteria.
- **Not done:** the contract test is not implemented and no CI workflow exists, so the
  requirement of a contract test running in CI is met only as a design. No publisher,
  broker or consumer exists yet.
- **Open points:**
  1. Team approval to add the Pact dependency and the CI workflow (INT-03).
  2. `paymentAttemptRef` and the structure of `allocatedCapacity`, to agree with the
     Reservations side before the first publisher is written.
  3. Broker product and the ADR that records the Session 1 and Session 2 decisions (INT-04).
