# CP9 - Incident drill and blameless postmortem

A drill on paper, not a real outage: nothing was run against a live system, because the metrics and the alerts
of CP4 to CP7 are not deployed. Every number is computed from my files `01` to `07` and is an expected value.

## The incident

In the journey of CP1, the hop I marked is the asynchronous boundary: the relay (`multi-tour-worker-event-relay`)
delivers the `TenantInactivated` event to its downstream dependency, `multi-tour-security` (step 5). Assume that
`security` answers in **3 seconds for 25 % of the requests during 40 minutes** (from 15:00:00 UTC to 15:40:00).

Assumptions I add, all from `05-slo.md` or marked as mine:
- It is an onboarding day: the 10 pilot tenants are registering their 200 staff accounts, so the relay delivers
  26 events per hour (0.43 a minute) before, during and after. In 40 minutes that is about 17 deliveries, and
  25 % of them, about **4.3**, are slow.
- The relay gives up after 5 s, so a 3 s answer is slow but still `outcome="ok"`.
- Cause of the drill (assumed): the pool of 10 connections of `security` to PostgreSQL is exhausted by the
  onboarding writes and the sign-ins at the same time, so one request in four waits about 3 s for a connection.

## One slow request

`trace_id` `3e5a8f10c7b24d96a1f0e8b2c4d57913`, the delivery of one `TenantInactivated` event at 15:21:07 UTC.

```
relay.run                                                   3,112 ms   (worker-event-relay)
  GET /internal/outbox                                         14 ms   (relay to identity-audit-api)
  relay.deliver TenantInactivated                           3,041 ms   (relay)
    POST /security/internal/tenants/{id}/events             3,034 ms   (security, server span)
      security.db.acquire_connection                        2,998 ms   <- waiting for the pool
      postgres update of the tenant copy                       12 ms
  POST /internal/outbox/{eventId}/published                    21 ms   (relay to identity-audit-api)
```

Check: 14 + 3,041 + 21 = 3,076 ms plus 36 ms of the run itself is 3,112; 2,998 + 12 = 3,010 ms plus 24 ms of the
handler is 3,034. Nearly all the time is the wait for a connection, not the work.

## Four log lines of that trace

Format of `02-log-contract.md`, same `trace_id`, timestamps going forward.

```json
{"timestamp":"2026-10-20T15:21:07.210Z","level":"INFO","service":"worker-event-relay","version":"2.0.0","environment":"qa","trace_id":"3e5a8f10c7b24d96a1f0e8b2c4d57913","span_id":"1a2b3c4d5e6f7081","correlation_id":"inact-71c4a9e2","event":"outbox.event.read","message":"pending event read from identity-audit","duration_ms":14,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"9f3b7c21-4d85-4a06-b1e2-5c8a0d7e3f14","event_type":"TenantInactivated","attempt":1}
{"timestamp":"2026-10-20T15:21:10.222Z","level":"WARN","service":"security","version":"2.0.0","environment":"qa","trace_id":"3e5a8f10c7b24d96a1f0e8b2c4d57913","span_id":"8c9d0e1f2a3b4c5d","correlation_id":"inact-71c4a9e2","event":"db.pool.wait","message":"waited for a database connection","duration_ms":2998,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"9f3b7c21-4d85-4a06-b1e2-5c8a0d7e3f14"}
{"timestamp":"2026-10-20T15:21:10.258Z","level":"INFO","service":"security","version":"2.0.0","environment":"qa","trace_id":"3e5a8f10c7b24d96a1f0e8b2c4d57913","span_id":"8c9d0e1f2a3b4c5d","correlation_id":"inact-71c4a9e2","event":"tenant.event.applied","message":"tenant copy updated; sessions of the tenant are now refused","duration_ms":3034,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"9f3b7c21-4d85-4a06-b1e2-5c8a0d7e3f14","event_type":"TenantInactivated","tenant_status":"INACTIVE","attempt":1}
{"timestamp":"2026-10-20T15:21:10.265Z","level":"WARN","service":"worker-event-relay","version":"2.0.0","environment":"qa","trace_id":"3e5a8f10c7b24d96a1f0e8b2c4d57913","span_id":"1a2b3c4d5e6f7081","correlation_id":"inact-71c4a9e2","event":"event.delivery.slow","message":"security answered after more than 1 s","duration_ms":3041,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"9f3b7c21-4d85-4a06-b1e2-5c8a0d7e3f14","event_type":"TenantInactivated","attempt":1}
```

## Value of the SLIs (SLOs of `05-slo.md`)

| SLO | Inside the 40 minutes | Over the 30 days | Why |
|---|---|---|---|
| 1 Availability | 100 % (target 99 %) | 100 % | every attempt answered `ok`; 3 s is under the 5 s timeout |
| 2 Latency (1 s or less) | **75 %** (target 99 %) | 1 - 4.3 / 880 = **99.51 %** | 25 % of the deliveries took 3 s |
| 3 Freshness (60 s or less) | 100 % | 100 % | a slow event waits 3 s plus at most one 5 s poll, far under 60 s |

Only the latency SLO sees the incident, which is the reason for having it apart from availability.

## Which alert fires first, and when

Each minute of the incident adds 0.25 x 0.43 = 0.108 slow events to the one-hour window that holds 26.

| Alert | Condition | Minute it is true | Fires? |
|---|---|---|---|
| `TenantEventDeliveryFastBurn` (availability) | error ratio above 14.4 % | never, the ratio is 0 | no |
| `TenantEventDeliverySlowBurn` (availability) | same, 6 h | never | no |
| **`TenantEventDeliveryLatencyFastBurn`** | 1 h ratio = 0.108 x t / 26 above 14.4 %, and 5 min ratio (25 %) above 14.4 % | 0.004167 x t > 0.144, so **t = 34.6**: **minute 35 (15:35)** | **yes, first and the only one** |
| `TenantEventDeliveryLatencySlowBurn` | 6 h ratio = 0.108 x t / 156 above 6 % | t = 86.4 | no, the incident ends at minute 40 |

Check: at minute 35 the incident has spent 25 % x 35 = 8.75 minutes of total failure, 2.0 % of the 432-minute
budget, which is the 2 % the alert is designed to mean (`06-alert-policy.md`).

## Runbook step that stops the bleeding

Step 3.1 of `07-runbook.md`: **scale out `security` to two replicas** (`docker compose up -d --scale security=2`).
The second replica brings a second pool of 10 connections, so the waits end. The confirmation of section 2
(trace `3e5a8f...` shows `security.db.acquire_connection` of 2,998 ms and a `db.pool.wait` line) is what points
to this step and not to step 3.3 (rollback): nothing was deployed.

# Blameless postmortem

## Timeline (UTC, drill)

| Time | What happened |
|---|---|
| 15:00 | The pool of `security` is full during the onboarding of tenants; one delivery in four waits about 3 s |
| 15:35 | `TenantEventDeliveryLatencyFastBurn` fires and pages the owner of `security` |
| 15:36 | The owner acknowledges and opens `07-runbook.md` |
| 15:38 | Section 2 confirms: the 5-minute slow ratio is 25 %; the trace of one slow event shows a 2,998 ms wait for a connection |
| 15:39 | Step 3.1: `security` scaled to two replicas |
| 15:40 | The waits stop (end of the 40 minutes of the drill) |
| 15:43 | The 5-minute ratio falls under 14.4 % again and the alert resolves: it needs 2.1 of the 5 minutes to clear |

## Impact

- **Requests:** about 17 deliveries in 40 minutes, about **4.3 slow** (3 s instead of 15 ms). None failed or was
  lost, and each tenant was cut at most 3 s later than normal.
- **Budget in minutes:** 25 % x 40 = **10 minutes** of the 432 (**2.3 %**).
- **Budget in events:** 4.3 of 8.8 (**49 %**), just under the 50 % step of the policy of `05-slo.md`. The two
  readings differ 20 times because the incident was in the busiest hour of the month, and minutes assume an
  even traffic. For a path with 880 events a month the event reading is the honest one.
- **Not measured:** the same exhausted pool also slows sign-ins and other requests of `security`. No metric sees
  them, so the real impact on people is larger than the numbers above.

## Root cause

The pool of `security` (10 connections, wait up to 5 s, Annex C limit) is too small for an onboarding day that
adds event writes to the normal sign-ins, and nothing shows its saturation. (Assumed by the drill; in a real
incident the first thing to confirm is `security_db_pool_in_use_connections` against its maximum.)

## Contributing factors

- No metric or alert on the pool: only the latency SLO of the relay noticed it, 35 minutes in.
- The relay has no limit of concurrent deliveries and no circuit breaker, so it keeps sending to a slow consumer.
- `security` has no latency SLO of its own: its sign-ins can be slow without any page.
- Single replica of `security` in the composition; the runbook mitigation had to be done by hand.
- The 1 h window needs 35 minutes to cross 14.4 % when only 25 % of the requests are slow: an alert tuned for a
  total outage is slow for a partial one.

## Action items

**1. Observable pool in `security`.** As the on-call engineer, I want `security` to expose the use of its
database pool and log every long wait, so that the runbook confirms an exhausted pool in one query.
Acceptance criteria:
- Given a pool maximum of 10, when `GET /metrics` is read, then `security_db_pool_in_use_connections` and
  `security_db_pool_max_connections` are present and the first is never above the second.
- Given a test that holds all 10 connections, when another request waits more than 500 ms, then exactly one
  `WARN` line with `event` `db.pool.wait`, `duration_ms` and the `trace_id` of the request is written.

**2. Two replicas of `security` in the composition.** As the Project Lead, I want the root composition
(`multi-tour-infra-postgres`) to run `security` with two replicas in `qa` and `main`, so that one pool is not a
single point of slowness and step 3.1 of the runbook is already done.
Acceptance criteria:
- Given the `qa` composition started with `docker compose up -d`, when `docker compose ps` is read, then two
  `security` containers are healthy.
- Given one replica stopped, when 20 events are delivered, then all 20 end `outcome="ok"` with no attempt above 1 s.

**3. The alert really fires.** As the on-call engineer, I want the relay to export its metrics and the rules of
`06-alerts.yml` to be loaded and checked in CI, so that the page of this drill is real and not a table.
Acceptance criteria:
- Given the file `06-alerts.yml`, when CI runs `promtool check rules`, then it ends with exit code 0.
- Given a `dev` environment where `security` is made to answer in 3 s for 25 % of the deliveries, when the
  26-events-an-hour load of this drill runs, then `TenantEventDeliveryLatencyFastBurn` is `firing` no later than
  minute 36 and resolves within 5 minutes of the end.

## What went well and what I would check next

The event was never lost (the outbox kept it), and the trace pointed to the wait in one step. To check next: that
the volume estimate of 880 events a month is close to the real outbox, because the alert thresholds depend on it.
