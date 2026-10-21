# CP7 - Alert policy

Rules in `06-alerts.yml`, for the SLOs of `05-slo.md`: delivery of the events of `identity-audit` to `security`,
99 % over 30 days, so the error budget is 1 %, which is 43,200 minutes x 1 % = 432 minutes, or about 8 events
(880 events a month x 1 %).

I wrote the rules by hand. On 2026-10-20 I parsed the four expressions of `06-alerts.yml` and the queries of
`05-slo.md` with the PromQL parser of Prometheus v0.55.1 (`promql/parser`, `ParseExpr`): all parse, so the
syntax is valid. That is a syntax check only: I did not load the rules in a Prometheus, and the metrics are not
exported by the real relay yet, so no rule has ever fired outside the drill of `08-incident.md`.

## How much budget each alert means before it fires

Burn rate 1 means the budget would last exactly the 30 days (720 hours). The share of the budget spent when an
alert fires is `burn rate x window / 720 h`.

| Alert | Burn rate | Long window | Budget spent when it fires | In minutes of total failure | In events |
|---|---|---|---|---|---|
| `...FastBurn` (page) | 14.4 | 1 h, confirmed in 5 min | 14.4 x 1 / 720 = **2 %** | 2 % x 432 = **8.6 min** | 0.18 |
| `...SlowBurn` (ticket) | 6 | 6 h, confirmed in 30 min | 6 x 6 / 720 = **5 %** | 5 % x 432 = **21.6 min** | 0.44 |

At those rates the whole budget lasts 30 days / 14.4 = **50 hours** (fast) or 30 days / 6 = **5 days** (slow),
so the fast alert wakes a person because waiting until morning could burn all the month; the slow one opens a
ticket for the next working day.

The same two alerts exist for the latency SLO (`...LatencyFastBurn` and `...LatencySlowBurn`), with the same
numbers, because a delivery slower than 1 s spends that budget and an availability alert never sees it.

**The weak point of my numbers.** The events are few, so a fraction of an event is not something that can happen.
With the minimum of 10 attempts in the long window (the `>= 10` of the rules), the fast alert needs at least two
failed attempts to pass 14.4 %: 2 of 10 is 20 %, which is already **23 % of the monthly event budget** (2 of 8.8).
In an hour with a normal volume of one or two events no alert can fire, and it is the freshness SLO (an event
older than 60 s) that finds the outage. The alerts are for busy hours, such as the onboarding of a new tenant.

## Cause-based alerts I will NOT create

| Alert | Why not |
|---|---|
| CPU of the `worker-event-relay` container above 80 % | The relay mostly waits for the network: it can be at 5 % CPU and failing, or at 90 % and delivering well. It says nothing about the person's experience, and it would page for a batch of events that finished correctly. The SLO alert covers the effect whatever the cause. |
| `identity_audit_outbox_pending_events` above 10 | One onboarding day creates more than 10 pending events in minutes and the relay drains them in seconds. The number is a cause (a queue) and not a damage; the damage is an old event, which the freshness SLO already measures with `relay_oldest_pending_event_age_seconds`. I keep the gauge as a dashboard panel, not as a page. |

Other alerts I considered and left out: the pool of `security` above 80 % (it only matters if requests wait, and
then the latency alert fires) and a disk alarm of MongoDB (that belongs to the platform, not to this journey).
