# CP4 - Golden signals and metric catalog

Journey of CP1: a Platform Administrator inactivates a tenant and the `TenantInactivated` event reaches
`security`. One table per service. The four golden signals are latency, traffic, errors and saturation.

**State.** Only the two metrics of `worker-event-relay` marked "built" exist, and only in the module of
`03-instrumentation/` (not yet in the real relay). Every other row is a design: the services expose no
metrics today (checked on 2026-10-19). The names follow the Prometheus conventions: `snake_case`, a unit
suffix (`_seconds`), `_total` for counters.

## `multi-tour-api-gateway` (NGINX, source: its JSON access log)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `gateway_request_duration_seconds` | histogram | `route`, `method` | `histogram_quantile(0.95, sum by (le, route) (rate(gateway_request_duration_seconds_bucket[5m])))` |
| Traffic | `gateway_requests_total` | counter | `route`, `method`, `status_class` | `sum by (route) (rate(gateway_requests_total[5m]))` |
| Errors | `gateway_requests_total` | counter | same | `sum(rate(gateway_requests_total{status_class="5xx"}[5m])) / sum(rate(gateway_requests_total[5m]))` |
| Saturation | `gateway_rate_limited_total` | counter | `zone` | `sum by (zone) (rate(gateway_rate_limited_total[5m]))` |

`route` is the name of the route file (`identity-audit`, `security`, `sagas`...), never the raw URL.
Saturation of the gateway is how often it refuses with `429` (`limit_req zone=general burst=100`).

## `multi-tour-identity-audit-api` (Java, Spring Boot)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `identity_audit_request_duration_seconds` | histogram | `route`, `method` | `histogram_quantile(0.95, sum by (le) (rate(identity_audit_request_duration_seconds_bucket{route="/tenants/{tenantId}/inactivate"}[5m])))` |
| Traffic | `identity_audit_requests_total` | counter | `route`, `method`, `outcome` | `sum(rate(identity_audit_requests_total{route="/tenants/{tenantId}/inactivate"}[5m]))` |
| Errors | `identity_audit_requests_total` | counter | same | `sum(rate(identity_audit_requests_total{outcome="server_error"}[5m]))` |
| Saturation | `identity_audit_outbox_pending_events` | gauge | none | `identity_audit_outbox_pending_events` |

`route` is the template of the contract (`/tenants/{tenantId}/inactivate`), not the path with the id.
`outcome` is `success`, `client_error` (a `4xx` such as the `422 INVALID_STATUS_TRANSITION` of a repeated
inactivation) or `server_error`. The pending outbox events are the saturation of this service: if the relay
falls behind, the number grows.

## `multi-tour-worker-event-relay` (Go)

| Signal | Metric | Type | Labels | PromQL | State |
|---|---|---|---|---|---|
| Latency | `relay_delivery_duration_seconds` | histogram | `consumer` | `histogram_quantile(0.95, sum by (le, consumer) (rate(relay_delivery_duration_seconds_bucket[5m])))` | built, `metrics/` |
| Traffic | `relay_deliveries_total` | counter | `consumer`, `outcome` | `sum by (consumer) (rate(relay_deliveries_total[5m]))` | built |
| Errors | `relay_deliveries_total` | counter | same | `sum(rate(relay_deliveries_total{outcome!="ok"}[5m])) / sum(rate(relay_deliveries_total[5m]))` | built |
| Saturation | `relay_oldest_pending_event_age_seconds` | gauge | `source` | `max by (source) (relay_oldest_pending_event_age_seconds)` | design |

`outcome` is `ok`, `rejected`, `timeout` or `error`; `consumer` is `security` today. Saturation of a relay is
not CPU: it is how old the oldest event it has not delivered is. That value is the freshness of the journey.

## `multi-tour-security` (Go)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `security_request_duration_seconds` | histogram | `route`, `method` | `histogram_quantile(0.95, sum by (le) (rate(security_request_duration_seconds_bucket{route="/security/internal/tenants/{tenantId}/events"}[5m])))` |
| Traffic | `security_requests_total` | counter | `route`, `method`, `outcome` | `sum by (route) (rate(security_requests_total[5m]))` |
| Errors | `security_requests_total` | counter | same | `sum(rate(security_requests_total{route="/security/internal/tenants/{tenantId}/events",outcome="server_error"}[5m]))` |
| Saturation | `security_db_pool_in_use_connections` | gauge | none | `security_db_pool_in_use_connections / security_db_pool_max_connections` |

The saturation of `security` is its PostgreSQL pool: the first symptom of a slow database is requests waiting
for a connection (the Annex C limit is a pool of 10 with a 5 s wait).

## Cardinality

| Label | Values | Series it creates | Decision |
|---|---|---|---|
| `route` (gateway) | about 10 route files | 10 | allowed |
| `route` (services) | the paths of each contract: 14 in `identity-audit` and 14 in `security` | 14 and 14 | allowed, only the template |
| `method` | `GET`, `POST`, `PUT`, `PATCH`, `DELETE` | 5 | allowed |
| `status_class` | `2xx`, `3xx`, `4xx`, `5xx` | 4 | allowed |
| `outcome` | 3 in the APIs, 4 in the relay | 3 or 4 | allowed |
| `consumer` | `security` (one consumer today, at most a handful) | 1 to 5 | allowed |
| `source` | `identity-audit`, `security` | 2 | allowed |

Worst case, the gateway counter has 10 routes x 5 methods x 4 classes = 200 series, and the histogram of a
service has 14 routes x 5 methods x 9 buckets = 630 series. Both are small.

**Forbidden labels.** They are unbounded, so they would multiply series without limit:
- `tenant_id` and `tenant identifier`: one value per company, and the platform is meant to grow by companies.
  What they would answer ("which tenant fails") is answered by the logs, where `tenant_id` is a field of
  CP2.
- `event_id`, `trace_id`, `correlation_id`: one value per request. They belong in the logs and in the trace.
- The raw URL (`/tenants/0b8c6a3e-.../inactivate`) and the query string: they hold ids. The route template
  replaces them.
- `actor_id`, e-mail or any personal data: unbounded and personal (CP2, "never log").
- The free text `reason` of an inactivation.

## Histogram buckets

`0.05, 0.1, 0.25, 0.5, 1, 2.5, 5` seconds, the buckets of the built module.

- The requirement of the project is **p95 of 3 seconds or less** (NFR-006 in Docs). The buckets `2.5` and `5`
  sit on both sides of that limit, so "how many requests are above 3 s" can be read from the histogram.
- An inactivation is a write of one document and one outbox insert; I expect it to answer in well under
  100 ms, so `0.05`, `0.1` and `0.25` separate a healthy answer from a slow one that is still under the
  limit.
- The relay gives up a delivery after 5 seconds (the timeout of `deliver`), so `5` is the last finite bucket:
  everything above it is a timeout and falls in `+Inf`.
- The Prometheus default buckets go up to 10 s; that range is not useful for a call that is cut at 5 s.
