# CP8 - Runbook: fast burn of the tenant event delivery

Alerts: `TenantEventDeliveryFastBurn` (availability) and `TenantEventDeliveryLatencyFastBurn` (latency), both in
`06-alerts.yml`, severity `page`, team `errorcapa8-security`. SLOs in `05-slo.md`.

**What is broken for the person.** A Platform Administrator inactivated a tenant, but the `TenantInactivated`
event is not reaching `multi-tour-security`, or arrives slowly. Until it arrives, the people of that tenant can
still sign in, against the PRD (inactivation revokes the sessions immediately). The event is not lost: it stays
`PENDING` in the outbox of `multi-tour-identity-audit-api`.

**State.** Written for the target setup of ADR-004 D8. Today no service exports the metrics and there is no log
store, so the queries below are the ones the runbook will use once they exist; the log query works now with
`docker compose logs`.

## 1. Symptoms

- The page of one of the two alerts above.
- Lines of `worker-event-relay` with `event.delivery.timeout`, `event.delivery.slow` or `tenant.event.rejected`
  (event names of `02-log-contract.md`).
- `relay_oldest_pending_event_age_seconds` above 60 (freshness SLO).
- A report that an inactivated tenant can still sign in.

## 2. Confirm (two minutes)

1. Is it really failing now? Error ratio of the last 5 minutes (above 0.144 confirms the fast burn):

```promql
sum(rate(relay_deliveries_total{consumer="security",outcome!="ok"}[5m])) / sum(rate(relay_deliveries_total{consumer="security"}[5m]))
```

2. Is it slow and not failing? Share of deliveries over 1 s in the last 5 minutes (above 0.144 confirms it):

```promql
1 - sum(rate(relay_delivery_duration_seconds_bucket{consumer="security",le="1"}[5m])) / sum(rate(relay_delivery_duration_seconds_count{consumer="security"}[5m]))
```

3. How old is the oldest event nobody has delivered?

```promql
max(relay_oldest_pending_event_age_seconds{source="identity-audit"})
```

4. Which request, and where does the time go? Take the `trace_id` of one `event.delivery.slow` or
   `event.delivery.timeout` line and read everything that happened to it:

```bash
docker compose logs --no-log-prefix worker-event-relay security identity-audit-api | jq -c 'select(.trace_id=="<trace_id>")'
```

   Look at which service has the long `duration_ms` and at its `event` name (`db.pool.wait` points to the
   connection pool of `security`; `tenant.event.rejected` points to a rule; a timeout with no line from
   `security` points to the network or to `security` being down).

## 3. First mitigations, from lowest to highest risk

Only what the architecture supports today. A feature flag does not exist and a circuit breaker is not built in
the relay (both are action items in `08-incident.md`), so they are not steps.

| # | Mitigation | When | How | Risk |
|---|---|---|---|---|
| 1 | Scale out `security` to two replicas | `security` is slow or its pool is waiting (`db.pool.wait`) | `docker compose up -d --scale security=2` in the environment; the relay finds them by DNS name | Low: reversible with `--scale security=1`, changes no data. The two pools (10 + 10) must fit the PostgreSQL connection limit |
| 2 | Slow the relay down | `security` is overloaded and the relay adds to it | In the relay environment set `BATCH_SIZE=5` and `RELAY_INTERVAL=30s`, then restart it | Medium: events wait longer in the outbox, so freshness gets worse, but none is lost (delivery is idempotent by event id) |
| 3 | Roll back `security` to the previous release | the alert started right after a deployment of `security` | redeploy the previous tag (for example `v2.0.0`) with the same `env/<ambiente>.env` | High: a migration of the `security` schema may not go back; needs the approval of the Project Lead and, for `main`, the teacher |

Do not delete events from the outbox and do not mark them `published` by hand: that hides the damage and the
tenant keeps working.

## 4. Escalation

| Who | When |
|---|---|
| Owner of `security` and of the relay (Project Lead) | first responder; takes the page |
| Developer of the team (owner of the consumers, `commercial-reservation` and `cash-reporting`) | no acknowledgement in 15 minutes, or the cause is in a database or a service that is not mine |
| Teacher (approver of `main`) | the fix is a rollback or a `hotfix/` to `main` |

If the mitigation of step 1 does not change the ratio in 10 minutes, go to step 2; if two steps fail, escalate
and write down every action with its time.

## 5. Verify recovery

- The 5-minute error ratio and the 5-minute slow ratio are both below 0.01 (the budget) for 15 minutes.
- `max(relay_oldest_pending_event_age_seconds{source="identity-audit"})` is under 10 s.
- Every event that was `PENDING` during the incident is `published` in the outbox.
- One test inactivation in a tenant of the environment: the line `tenant.event.applied` shows
  `tenant_status` `INACTIVE` and a sign-in of that tenant is refused.
- Close the page, put back the settings changed in step 2, and open the postmortem if the budget spent was
  more than 20 % (`05-slo.md`).
