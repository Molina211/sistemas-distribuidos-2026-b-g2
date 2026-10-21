# CP6 - SLIs, SLOs and error budget

Journey of CP1: a Platform Administrator inactivates a tenant and the `TenantInactivated` event reaches
`multi-tour-security` through the outbox of `multi-tour-identity-audit-api` and `multi-tour-worker-event-relay`.
The three SLOs below measure the part of the journey that can fail without the person noticing: the event
delivery from the relay to `security`. If it fails, the tenant is inactive in `identity-audit` but its people can
still sign in, which breaks the rule of the PRD ("al inactivar un tenant deben revocarse de forma inmediata los
accesos", section 9, decisions A6 and E26).

**State.** The metrics used here are the ones of `04-metrics.md`. `relay_deliveries_total` and
`relay_delivery_duration_seconds` are built in the module of `03-instrumentation/`, not in the real relay.
`relay_oldest_pending_event_age_seconds` is a design. So nothing below is measured on a running system: the
targets are proposals of mine and the team has not approved them.

## Where the numbers come from

| Fact | Value | Source |
|---|---|---|
| Month used for every window | 30 days = 43,200 minutes | convention of the workshop |
| Platform availability required | 95 % per calendar month | PRD section 17, "Disponibilidad" (H14) |
| Response time required | p95 of 3 s or less | PRD section 17, "Rendimiento" (NFR-006) |
| Initial scenario | 10 active tenants, up to 20 internal users per tenant | PRD section 17, "Escalabilidad" |
| Delivery timeout of the relay | 5 s per event | `deliver` in `03-instrumentation/` |
| Normal delivery time | about 15 ms | line 6 of `02-log-contract.md` (`duration_ms` 14) |

## Traffic estimate of the journey

The relay delivers to `security` every outbox event of `identity-audit` that changes an account or a tenant:
tenant created, inactivated or reactivated, and collaborator registered, inactivated or changed.

- Staff accounts: 10 tenants x 20 internal users = 200.
- I assume each one changes once a week (registered, a permission changed, inactivated or reactivated):
  200 x 30 / 7 = about 857 events per month.
- Tenant events: 10 tenants x about 3 per month = 30.
- Total: about **880 events per month**, about 29 per day.

It is a low-volume journey on purpose, and that is the first thing the numbers show: a month has only a few
hundred events, so the budget is a handful of events. Customer accounts (5,000 per tenant in the PRD) are
created directly in `security` and do not use this path. The estimate is mine, not measured: the real number
comes from `identity_audit_outbox_pending_events` and the outbox collection once the services are running.

## SLO 1 - Availability of the delivery

- **SLI:** valid events = delivery attempts to `security`; good = attempts with `outcome="ok"`. A `rejected`,
  `timeout` or `error` attempt is bad, even if the next attempt works, because for that time the tenant was
  not cut.
- **Formula:** good attempts / all attempts in the window.
- **PromQL:**

```promql
sum(increase(relay_deliveries_total{consumer="security",outcome="ok"}[30d]))
/
sum(increase(relay_deliveries_total{consumer="security"}[30d]))
```

- **Target:** 99 % over 30 days. The platform promises 95 % to its customers (PRD), but this path is internal
  and has to be stricter, because the platform-wide number hides a failure that costs a security rule.

## SLO 2 - Latency of the delivery

- **SLI:** good = deliveries that finish in 1 s or less; valid = all deliveries to `security`.
- **PromQL** (`le="1"` is a bucket of the histogram of `04-metrics.md`):

```promql
sum(increase(relay_delivery_duration_seconds_bucket{consumer="security",le="1"}[30d]))
/
sum(increase(relay_delivery_duration_seconds_count{consumer="security"}[30d]))
```

- **Target:** 99 % of the deliveries in 1 s or less, over 30 days. The PRD limit of 3 s is for operations a
  person waits for; here nobody waits, and a normal delivery takes 15 ms, so 1 s is already 70 times the
  normal time. A delivery of 3 s is slow but still inside the 5 s timeout: only this SLO sees it.

## SLO 3 - Freshness of the cut (the one that fits my product)

- **SLI:** the share of one-minute slices in which the oldest event not yet delivered is 60 s old or less. It
  is time-based, not request-based, because with 29 events a day a ratio of requests is too coarse.
- **PromQL:**

```promql
avg_over_time((max(relay_oldest_pending_event_age_seconds{source="identity-audit"} <= bool 60))[30d:1m])
```

- **Target:** 99 % of the slices over 30 days. The relay polls every 5 s (`RELAY_INTERVAL`), so a healthy delay
  is under 10 s; 60 s leaves room for the retries of one failed delivery and answers "immediately" of the PRD
  with a number a person accepts.

## Error budget

Budget = 100 % minus the target. The three SLOs use 99 %, so the budget is 1 % of the window.

| SLO | Unit | Budget in the unit | Budget in minutes |
|---|---|---|---|
| 1 Availability | delivery attempts | 1 % x 880 = **8 failed attempts** per 30 days | 1 % x 43,200 = **432 min** of total failure |
| 2 Latency | deliveries | 1 % x 880 = **8 slow deliveries** per 30 days | 432 min if every delivery were slow |
| 3 Freshness | one-minute slices | 1 % x 43,200 = **432 slices** | **432 min** |

Two readings of the same budget. In minutes, the path may be down for 7.2 hours a month. In events, only 8
attempts may fail. The two do not match when traffic is not uniform (an onboarding day has far more events than
a quiet one), and that gap is what CP9 shows. The 8 events are the number I would watch.

## Error budget policy

| Budget consumed in the window | What the team does |
|---|---|
| 50 % (4 events or 216 min) | Warning in the team channel. The cause is reviewed in the next planning; no change of plan. |
| 75 % (6 events or 324 min) | Stop starting new features on the relay, `identity-audit-api` and `security`; the sprint takes the reliability stories first (retries, a cap on the delivery time, the metrics). The Project Lead is told. |
| 100 % (8 events or 432 min) | Feature freeze on those three services: only fixes, security work and the stories that give budget back. Releases to `main` need the Project Lead's explicit approval. The freeze ends when the 30-day window is back under 75 %. |

Owner of the policy: the Project Lead (me, in this workshop). The thresholds are mine; the team has not
approved them.
