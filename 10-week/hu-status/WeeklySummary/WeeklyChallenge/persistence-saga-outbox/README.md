# Weekly Challenge - Week 10, Session 1: persistence in distributed systems

**The challenge:** give each service its own database; implement one saga with a compensation and the
outbox for one critical event; make the consumers idempotent.

**Scope of this document.** The macrodomain 1 deliverable is not finished (see
[`../../README.md`](../../README.md)), so this is the documentation of what is implemented in the
repositories today, with its tests, and an explicit list of what is not done. Nothing here is a design
for later: every claim names the file or the test that backs it. The unit tests named below were run on
2026-10-13 with `go test` in each repository and all passed; the tests that need a real database
(`store_test.go`, `*_store_test.go`, the db-ci) were **not** run for this document.

## 1. One database per service

Annex J of the course norm fixes one instance per engine, so "a database per service" is realised as
**one isolated schema (PostgreSQL) or database (MongoDB) per service, with its own user and no access to
anyone else's**. There are no foreign keys between services: a reference to another service is an id.

| Service | Engine | Where its data lives | Owner repository of the structure |
|---|---|---|---|
| `identity-audit` | MongoDB | collections `tenants`, `outbox_events`, `idempotency_keys`, each with a `$jsonSchema` validator (`additionalProperties: false`, `validationLevel: strict`, `validationAction: error`) | [`multi-tour-identity-audit-db`](https://github.com/code-corhuila/multi-tour-identity-audit-db) (`01_ddl/00_collections/changelog.yaml`) |
| `security` | PostgreSQL | schema `security`: `outbox_event`, accounts, memberships, sessions, tenant copy, `idempotency_key` | [`multi-tour-security`](https://github.com/code-corhuila/multi-tour-security) (`db/01_ddl/`, `databasechangelog_security`) |
| saga `onboard-tenant` | PostgreSQL | schema `workflow_onboard_tenant`: `onboard_tenant_saga` and `onboard_tenant_saga_step` | [`multi-tour-workflow-onboard-tenant`](https://github.com/code-corhuila/multi-tour-workflow-onboard-tenant) (`db/01_ddl/03_tables/001_create_onboard_tenant_saga.sql`) |

Polyglot persistence is real here: the tenant (a document with its own lifecycle) lives in MongoDB; the
accounts and the sessions (relational, with uniqueness and locking) live in PostgreSQL. The saga keeps
`tenant_id` as a plain `uuid` reference to `identity-audit`, never a foreign key
(`001_create_onboard_tenant_saga.sql`).

**Not done.** `commercial-reservation` has its schema from the teammate; `operations-cost` and
`cash-reporting` have no structure yet.

## 2. One saga with its compensation: `onboard-tenant`

Repository: [`multi-tour-workflow-onboard-tenant`](https://github.com/code-corhuila/multi-tour-workflow-onboard-tenant),
file `internal/application/usecase/onboard_tenant.go`.

| # | Step | Participant | Compensation |
|---|---|---|---|
| 1 | `CREATE_TENANT` | `identity-audit`: registers a **pending** tenant | `FailOnboarding` on `identity-audit`: marks the onboarding failed and frees the identifier |
| 2 | `CREATE_ADMINISTRATOR` | `security`: creates the first administrator account | none: a clear rejection means nothing was created |
| 3 | `RECORD_ADMINISTRATOR` | `identity-audit`: completes the onboarding with the membership id | none: a failure ends `FAILED` for a person |

How it behaves (`abort`, `undo`, `Resume`):
- The state is saved **after every step** (`TestI_state_is_saved_after_each_step`).
- Only a **definitive** rejection (a 4xx) is compensated; compensations run in **reverse order** and each
  one is saved as it runs. A step with an unknown outcome (timeout, lost answer) ends `FAILED` and
  nothing is undone, because the work may exist.
- A compensation that fails also ends `FAILED`: a person decides.
- Every call carries `Idempotency-Key: <saga>:<step>` and the undo carries `...:undo`.
- The temporary password never reaches the state or the logs.

Tests (all pass, `go test ./internal/application/usecase/ -v`, 18 in total):

| Behaviour | Test |
|---|---|
| Happy path, three steps, `COMPLETED` | `TestA_happy_path_runs_three_steps_and_completes` |
| Step 2 fails: step 1 is compensated, `COMPENSATED` | `TestA_step_two_failure_compensates_step_one` |
| The compensation fails: `FAILED` | `TestA_failed_compensation_ends_failed` |
| Step 3 fails: `FAILED`, the account is not undone | `TestA_step_three_failure_ends_failed_without_undoing_the_account` |
| Unknown outcome at step 2 or 1: `FAILED`, not compensated | `TestA_unknown_outcome_at_step_two_ends_failed_without_compensating`, `TestA_unknown_outcome_at_step_one_ends_failed` |
| A caller that leaves does not cut the compensation | `TestA_cancelled_caller_does_not_stop_the_compensation` |
| Resume after a crash; a second resume does nothing | `TestA_saga_stopped_before_step_two_is_compensated_on_resume`, `TestA_second_resume_over_a_compensated_saga_does_nothing`, `TestA_rerun_skips_steps_already_compensated` |
| Repeating the start returns the same saga and runs nothing | `TestC_repeated_start_returns_same_saga_and_runs_nothing` |

**Declared debts** (written in the code of `Resume`): `F2`, the on-behalf-of actor and the membership id
are not persisted, so a resumed saga that already created the account ends `FAILED` for a person;
`F3`, there is no claim or lock, so the saga is safe with **one** instance only. The compensation path
has been proven with fakes of the participants; it has **not** yet been demonstrated against the real
`identity-audit` and `security` containers.

## 3. The outbox of a critical event

The critical event is the **change of status of a tenant** (`TenantCreated`, `TenantInactivated`,
`TenantReactivated`): `security` keeps a copy of each tenant's status and learns it only from these events.

- **Written with the change, in one transaction.** In `identity-audit-api`, PR
  [#16](https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/16) stores a new tenant with
  its repeat key in one transaction and PR
  [#17](https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/17) changes a tenant together
  with its events in one transaction; the events go to the `outbox_events` collection. `security` has its
  own `outbox_event` table for its events.
- **Published by a separate process.** The worker
  [`multi-tour-worker-event-relay`](https://github.com/code-corhuila/multi-tour-worker-event-relay)
  reads the pending events by service token, delivers each one to its consumers and marks it published
  **only after every consumer accepted it** (`TestA_event_marked_only_after_all_consumers_accept`).
- **At-least-once, bounded.** A failed delivery is counted with its error; the wait doubles from 5 s up to
  15 min and the 20th failure gives the event up (`TestD_TheWaitAfterAFailureDoublesFrom5SecondsUpTo15Minutes`,
  `TestD_TheTwentiethFailedAttemptGivesTheEventUp` in `security`; `TestA_twentieth_failure_marks_the_event_failed`
  in the relay). One failing event does not stop the batch (`TestA_one_failing_event_does_not_stop_the_batch`).
- **A late report cannot corrupt an event** (`TestD_AReportThatComesLateDoesNotTouchAnEventThatIsNoLongerPending`).

**Not done.** The relay has no consumer yet for the events that block an account or revoke its sessions.

## 4. Idempotent consumers

A consumer must survive the same event twice, and two events out of order.

| Consumer | Mechanism | Test |
|---|---|---|
| `security`, copy of the tenants (`internal/core/tenantevent`) | `Receive` checks the envelope and the copy **keeps the state only if the event is the newest**; an event it ignores is not applied and is not an error | `TestC_EachEventOfATenantBecomesTheCopyItDescribes`, `TestC_AnEventTheStoreIgnoresIsNotAppliedAndIsNotAnError` |
| Relay, redelivery | A repeated delivery sends the **same** `Idempotency-Key` | `TestC_repeat_delivery_sends_same_idempotency_key` |
| Saga entry | Same `Idempotency-Key` returns the same saga with `200` and runs no step | `TestC_repeated_start_returns_same_saga_and_runs_nothing` |
| Account creation in `security` | `idempotency_key` table written in the same transaction as the account | PR [#27](https://github.com/code-corhuila/multi-tour-security/pull/27) and [#29](https://github.com/code-corhuila/multi-tour-security/pull/29) |

## 5. CQRS and eventual consistency

CQRS as a pattern is **not implemented**. The nearest thing in the code is the copy of the tenants that
`security` keeps from the events of `identity-audit`: a read model owned by the consumer, fed by events,
that can be behind by one delivery cycle. Until the relay delivers the event, the copy still shows the
previous status; that window is the price of not calling `identity-audit` on every request. The lists of tenants and of pending events are served by indexes (`identity-audit-db` #5), not
by a separate read store.

## 6. What this challenge did not prove

- No end-to-end run of the injected failure against the real containers (that is the demo of Session 2).
- The persistence tests against the real databases were not executed for this document.
- Only one saga of the five exists; the other four repositories have no code.
