# Weekly Challenge - Week 11, Session 2: SLOs, error budget, alert and runbook

**The challenge:** 2 to 3 SLOs (availability, latency, freshness) with windows, an error-budget policy, a
symptom-based alert with owner and runbook, one runbook for a likely incident, and the work sliced as
observability stories with testable acceptance criteria.

**Scope of this document.** It is a **plan**. Nothing here is measured or alerting today: the metrics and
the Grafana stack that would feed it are not built (see
[`observability-logs-metrics-traces/`](./observability-logs-metrics-traces/README.md), sections 2 and 4).
The two first targets come from requirements the PDR already fixes (NFR-005 and NFR-006 in
`04-requirements/non-functional.md` of Docs, decided in ADR-004 D8); the third one, freshness, and every
number marked **proposed**, are mine and have not been approved by the team. The runbook commands were not
run against a live composition.

## 1. Service level indicators and objectives

| # | SLI (what a person feels) | How it is counted | SLO | Window | Error budget |
|---|---|---|---|---|---|
| 1 | **Availability** of the platform entry | Requests that reach the gateway on `/api/v1/*` and are not answered `5xx` (a `503 SERVICE_UNAVAILABLE` of the gateway counts as bad; `4xx` is the caller's mistake and counts as good), from the gateway access log (`status`) | **99 %** (proposed). The PDR floor is 95 % per month (NFR-005); the SLO is stricter so the floor is never touched | 30 days, rolling | 1 % = about 432 minutes of full failure per 30 days |
| 2 | **Latency** of the same requests | Share of requests with `durationMs` of the gateway log at or below 3000 ms | **95 %** of requests in 3 s or less, which is NFR-006 (p95 ≤ 3 s) | 30 days, rolling | 5 % of requests may be slower |
| 3 | **Freshness** of cross-domain events | Time from an outbox event being written (`occurredAt`) to the relay marking it published; the relay polls every 5 s (`RELAY_INTERVAL`, ADR-004 D10) | **99 %** of events published within 60 s (proposed) | 7 days, rolling | 1 % of events may be later |

Why these three: a person signing in or opening a screen feels the first two; the third protects the
consistency between domains that the system has instead of a distributed transaction (the account created by
the saga, the tenant status that `security` copies), so a stuck relay is silent damage, not an error page.

What is deliberately **not** an SLO: CPU, memory and pod-level numbers. They are causes, not symptoms.

## 2. Error-budget policy

The budget is read per SLO, over its window.

| Budget consumed | What the team does |
|---|---|
| under 50 % | Normal work and releases |
| 50 % to 100 % | Every `feat/` PR of the affected service states in its description what it does to the SLI; reliability fixes go first in the next sprint |
| 100 % (spent) | **Freeze of features** for the affected service: only `fix/` and `chore/` PRs that improve the SLI reach `develop` until the window recovers. A release (`release/`) needs the teacher's approval anyway; during a freeze it is requested only for a fix |
| a single incident that spends more than 25 % of a budget | A short written post-mortem in Docs within five working days, with the cause, how it was detected and the action that prevents it |

The policy is proportionate to a team of five students with no on-call rota: it changes what gets built, not
who gets paged at night (section 4).

## 3. The alert: symptom, burn rate, owner and runbook

**Alert `availability-fast-burn`** (SLO 1). A symptom of the person, not a cause of the machine.

| Field | Value |
|---|---|
| Fires when | The share of `5xx` answers at the gateway is above **14.4 %** (14.4 times the 1 % budget rate) over the last **1 hour** and also above 14.4 % over the last **5 minutes** |
| Why two windows | The long one means it is real, the short one means it is still happening, so it stops firing when the problem is fixed |
| Meaning | At this rate the whole 30-day budget is gone in about two days |
| Severity | High |
| Owner | **Jhon Molina**, owner of the gateway, `security`, `identity-audit` and the saga |
| Notified through | A Grafana alert to the team channel (the stack of ADR-004 D8; not built yet) |
| Runbook | Section 5 starts from this alert; the freshness runbook below is the worked one |

The same pattern gives the other alerts, each with its own owner and runbook before it is turned on:

| Alert | Fires when | Severity |
|---|---|---|
| `availability-slow-burn` | `5xx` above 6 % over 6 hours and above 6 % over 30 minutes | Medium |
| `latency-burn` | more than 30 % of requests above 3 s over 1 hour | Medium |
| `freshness-stale-event` | the oldest pending outbox event is older than 5 minutes for 10 minutes | High |

Not alerted: a single `401` or `403`; one failed login. ADR-004 D8 alerts when more than 50 of them arrive in
5 minutes, which is already a pattern and not a single event.

## 4. Who answers

There is no 24-hour on-call: the project is a course. The owner of the alert looks at it during the working
day; an alert that fires at night is read in the morning, and its severity decides whether the demo is
affected. This is a limit I accept and state, not a hidden gap.

## 5. Runbook: events are not reaching their consumers (`freshness-stale-event`)

**Why this incident is likely.** The relay (`multi-tour-worker-event-relay`) is a single process. It
depends on a service token that expires, on the outbox route of each source service, and on every consumer
being up. If any of these fails, events wait in the source's outbox and the consumers go on with old data
(for example `security` keeps an old tenant status).

**Symptoms.** The alert above; or a tenant inactivated in `identity-audit` whose accounts can still sign
in; or the gateway answers `503` for a route while its service is running.

**Confirm.**
1. Open the relay's `/health`. No answer: go to *Mitigate, relay down*.
2. Find the lines of its last run in the relay log: each run has one `correlationId`, and every call it makes
   carries it (the shared runtime does this; `TestC_log_lines_carry_the_run_correlation`). Look for the
   errors of the run: `401` means the service token; `5xx` or timeout means a consumer is down; `4xx` other
   than `401` means the event or route is wrong, and the relay does not retry a `4xx` (Annex D).
3. Read the pending events of the source with its outbox route (for `security`:
   `GET /api/v1/security/internal/outbox`, with the service token) and read the age of the oldest one and its
   recorded attempts (`.../outbox/{eventId}/attempts`).

**Mitigate.**
- *Relay down:* start it again from the root composition; its runs are idempotent, so a restart is safe
  (Annex D: a job is safe to run twice).
- *`401`:* the service token expired or was not issued. Issue a new token for the relay from `security`,
  set `SERVICE_TOKEN` in the environment file of the environment, restart the relay. Never put the token in
  a file that is committed.
- *A consumer is down (`5xx`, timeout):* bring that service back first; the relay retries with backoff and
  delivers on its next runs. Do not mark events published by hand.
- *A `4xx` that is not `401`:* the event does not fit the consumer. Leave the event in place, open a `fix/`
  PR for the consumer or the route, and record the event id in the PR; the failure destination of
  ADR-004 D4 holds what the relay gave up on.

**Verify the recovery.**
1. The oldest pending event of every source is under 60 s old on the next two runs.
2. The alert resolves by itself after its short window.
3. For the tenant-status case: sign in with an account of the inactivated tenant and expect it to be refused.

**Escalate.** If the cause is a consumer that the owner of the alert does not own (`commercial-reservation`,
`cash-reporting`), tell its owner with the event id, the `correlationId` of the failing run and the
time the oldest event was written. After the incident, add what was learned to this runbook.

**Not verified.** The steps were written from the code and the contracts; they were not run against a live
composition, and the exact names of the relay container and its failure destination must be checked against
`multi-tour-infra-postgres` and ADR-004 D4 before this is the runbook of record.

## 6. Observability stories, with acceptance criteria

Each is sized to one small PR and each criterion is a test or a measurable check. Ids are working names;
they are not yet on the board. The code PRs go to `develop` with the usual flow.

| Story | As a... I want... | Acceptance criteria |
|---|---|---|
| **OBS-01** One log key | As an operator, I want one key for the correlation id in every service, so a single search finds a request | **Given** a request with `X-Correlation-Id: abc-1` through the gateway **When** it reaches `security`, the saga and `identity-audit` **Then** the log line of each has the key `correlationId` equal to `abc-1`, and a test per service asserts it |
| **OBS-02** Who and where in each line | As an auditor, I want `service`, `tenantId` and the actor in each request line | **Given** an authenticated request **When** the service logs it **Then** the line has `service`, `tenantId` and `sub`, and never the token or a password (test over the line) |
| **OBS-03** Gateway metrics | As the owner, I want rate, `5xx` ratio and the 3 s latency share from the gateway | **Given** the smoke traffic of `tests/smoke.sh` **When** the metrics are read **Then** requests, `5xx` ratio and the share above 3 s match the counts of the access log of the same run |
| **OBS-04** Probe and availability | As the owner, I want a `/health` probe every 30 s | **Given** the gateway and each service **When** one is stopped **Then** the probe records failures within 60 s and the availability SLI drops by the failed share |
| **OBS-05** Outbox age | As the owner, I want the age of the oldest pending event per source | **Given** an event written and the relay stopped **When** 5 minutes pass **Then** the age value is at least 300 s; after the relay runs it is under 60 s |
| **OBS-06** Fast-burn alert | As the owner, I want `availability-fast-burn` with its runbook | **Given** the gateway answering `503` for a stopped service **When** the ratio stays above 14.4 % for the 1 h and 5 min windows **Then** the alert fires once and links to the runbook, and stops when the service returns |
| **OBS-07** One trace | As a developer, I want one trace of the onboarding saga | **Given** `POST` of the onboarding **When** it completes **Then** one trace has the gateway, saga, `identity-audit` and `security` spans, with the duration of each hop |

## 7. What is not done

- No SLI is measured and no alert exists; the stack of ADR-004 D8 is not built.
- The freshness SLO, the 99 % availability target and every burn-rate threshold are proposals, not approved
  by the team and not checked against real traffic, which does not exist yet.
- OBS-01 to OBS-07 are not on the board.
- The runbook was not rehearsed.
