# CP2 - Structured log contract

One JSON object per line, on standard output, the same fields in every piece of the journey of CP1
(`multi-tour-api-gateway`, `multi-tour-identity-audit-api`, `multi-tour-worker-event-relay`,
`multi-tour-security`). Searching `trace_id = X` returns everything that happened to one inactivation.

## Field table

| Field | Type | Required | Example | Why it exists |
|---|---|---|---|---|
| `timestamp` | string, ISO-8601 UTC with milliseconds | required | `2026-10-19T14:02:11.482Z` | order the lines of different services |
| `level` | string: `ERROR` `WARN` `INFO` `DEBUG` | required | `WARN` | filter by severity |
| `service` | string, the repository name without the `multi-tour-` prefix | required | `worker-event-relay` | know which piece wrote the line |
| `version` | string, semantic version of the release | required | `2.0.0` | tell a bug of the release from a bug of the previous one |
| `environment` | string: `dev` `qa` `main` | required | `qa` | the three course environments (`env/dev.env`, `qa.env`, `main.env`) |
| `trace_id` | string, 32 hex characters | required | `7c1e9a52d0b84f63a5e2c8b14d7f3096` | the id of the whole journey, from the W3C `traceparent` |
| `span_id` | string, 16 hex characters | required | `a1b2c3d4e5f60718` | the hop that wrote the line |
| `correlation_id` | string, `A-Za-z0-9._-`, 1 to 128 characters | required | `inact-3f9c1d20` | the `X-Correlation-Id` the person or the front can quote when they report an error; it is the `traceId` of the error body |
| `event` | string, `noun.verb` in lower case | required | `tenant.event.rejected` | a name stable enough to count and to alert on, unlike `message` |
| `message` | string | required | `security refused the event: the identifier is held` | a sentence for a person |
| `duration_ms` | integer | required (0 when it does not apply) | `5000` | the latency of what the line reports |
| `tenant_id` | string, UUID | optional, required when the request has a tenant | `0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10` | find everything of one company; ADR-003 isolates by tenant |
| `event_id` | string, UUID | domain, optional | `d41d8cd9-8f00-4b20-b204-e9800998ecf8` | the id of the outbox event; it is also the `Idempotency-Key` of the delivery |
| `event_type` | string | domain, optional | `TenantInactivated` | which event of `02-domain/domain-events.md` the line is about |
| `tenant_status` | string: `ACTIVE` `INACTIVE` | domain, optional | `INACTIVE` | the state the tenant has after the operation, to see a stale copy in `security` |
| `attempt` | integer | domain, optional | `2` | the delivery attempt of the relay; a high number is the symptom of a stuck event |
| `actor_id` | string, UUID | optional, required for an operation done by a person | `5a1e7c90-2b64-4f1b-8f3d-6e0c2a9b4d11` | who did it (the `sub` of the token), never the e-mail |

Domain fields explained, because they are the ones that make this contract mine and not a generic one:
- **`tenant_id`**: the system is multitenant, so every question in an incident starts with "which company".
- **`event_id`** and **`event_type`**: the journey crosses services through an outbox event, not a call. Without
  them a line of the relay cannot be tied to the row of `outbox_events` that caused it.
- **`tenant_status`**: the damage of this journey is a copy of the tenant in `security` that says `ACTIVE`
  when `identity-audit` says `INACTIVE`; the field shows which side a line saw.
- **`attempt`**: the relay retries; the attempt number tells a first failure from an event that is stuck.

## Never log

| Data | Where it appears in Multitour | How it is handled |
|---|---|---|
| Passwords, including the 72-hour temporary password of the first Administrator | the body that starts the `onboard-tenant` saga; sign-in and password change in `security` | never written, not even masked; the saga already keeps it out of its state |
| Access and refresh tokens, `SERVICE_TOKEN`, the JWT public or private key | the `Authorization` header, the environment of every service | never written; services log the header names, never the values |
| Password hashes | `security` accounts | never written |
| Personal data of customers: e-mail, name, phone, identity document | customer accounts in `security`, reservations in `commercial-reservation` | never written; use the account id (`actor_id`) |
| Payment data and receipts | `cash-reporting`, `commercial-reservation` | never written |
| The free text `reason` of an inactivation | body of `POST /tenants/{id}/inactivate` | not written; write `reason_length` if the size matters, because the text may name a person or a contract |
| Connection strings with a password | the environment files of each environment | never written; log the host and the database name only |

A value that must be correlated but must not be read (for example an e-mail in a rejected sign-in) is written
as a SHA-256 hash truncated to 12 characters.

## Seven lines of one request

One inactivation that goes wrong twice and then works. Same `trace_id` in all seven; the timestamps only go
forward; the numbers are illustrative, written by hand for this document.

```json
{"timestamp":"2026-10-19T14:02:11.412Z","level":"INFO","service":"api-gateway","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"b7e2d01a4c5f9e38","correlation_id":"inact-3f9c1d20","event":"http.request.routed","message":"POST routed to identity-audit-api","duration_ms":2,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10"}
{"timestamp":"2026-10-19T14:02:11.455Z","level":"INFO","service":"identity-audit-api","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"c3a9f1e07d2b6584","correlation_id":"inact-3f9c1d20","event":"tenant.inactivated","message":"tenant inactivated and TenantInactivated written to the outbox","duration_ms":18,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","tenant_status":"INACTIVE","actor_id":"5a1e7c90-2b64-4f1b-8f3d-6e0c2a9b4d11"}
{"timestamp":"2026-10-19T14:02:14.002Z","level":"INFO","service":"worker-event-relay","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"e5f60718a1b2c3d4","correlation_id":"inact-3f9c1d20","event":"outbox.event.read","message":"pending event read from identity-audit","duration_ms":12,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","attempt":1}
{"timestamp":"2026-10-19T14:02:19.015Z","level":"ERROR","service":"worker-event-relay","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"9d8c7b6a5f4e3d2c","correlation_id":"inact-3f9c1d20","event":"event.delivery.timeout","message":"security did not answer","duration_ms":5000,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","attempt":1}
{"timestamp":"2026-10-19T14:02:25.230Z","level":"WARN","service":"security","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"1f2e3d4c5b6a7988","correlation_id":"inact-3f9c1d20","event":"tenant.event.rejected","message":"the identifier is still held by another tenant; try again later","duration_ms":9,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","attempt":2}
{"timestamp":"2026-10-19T14:02:31.480Z","level":"INFO","service":"security","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"4a5b6c7d8e9f0a1b","correlation_id":"inact-3f9c1d20","event":"tenant.event.applied","message":"tenant copy updated; sessions of the tenant are now refused","duration_ms":14,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","tenant_status":"INACTIVE","attempt":3}
{"timestamp":"2026-10-19T14:02:31.512Z","level":"INFO","service":"worker-event-relay","version":"2.0.0","environment":"qa","trace_id":"7c1e9a52d0b84f63a5e2c8b14d7f3096","span_id":"6b7c8d9e0f1a2b3c","correlation_id":"inact-3f9c1d20","event":"outbox.event.published","message":"event marked published in identity-audit","duration_ms":11,"tenant_id":"0b8c6a3e-5f0a-4d1e-9a47-2c6d6c1f7b10","event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","event_type":"TenantInactivated","attempt":3}
```

Read in order, filtered by `trace_id`: the request is routed (line 1), the inactivation is saved (2), the relay
reads the event (3), the first delivery times out after 5 s (4), the second is refused by a rule of `security`
(5), the third works (6) and the relay closes the event (7). Between the person's `200` and the moment the
sessions stop working there are about 20 seconds, and that gap is what the freshness objective of CP6
measures.

## Levels

| Level | When the team uses it | Example in Multitour |
|---|---|---|
| `ERROR` | The operation failed and nobody asked for it to fail: needs a person or a retry | the relay cannot reach `security` (line 4); an unexpected `500` of `identity-audit-api` |
| `WARN` | A rule or a limit stopped the operation, or the system degraded but went on | `security` refuses an event because the identifier is still held (line 5); a token rejected; the gateway answering `429` |
| `INFO` | A business fact or a state change that someone will want to find later | a tenant inactivated (line 2); an event published (line 7); a sign-in |
| `DEBUG` | Detail for a developer, off in `qa` and `main` | the parsed `traceparent`, the page of the outbox that was read |

## Differences with what the code writes today

Written so CP3 and the next sprint have a clear list, and so this contract is not mistaken for the current
behaviour (checked in the code on 2026-10-19):
- the id is `correlationId` in the gateway and the worker runtime, `traceId` in `security`, the saga and the
  Java API; this contract uses `correlation_id` and `trace_id` as two different values;
- no service writes `trace_id` and `span_id` yet, because none reads `traceparent`;
- `service`, `version`, `environment`, `tenant_id` and `actor_id` are missing in most lines;
- the gateway writes its own JSON format (`nginx/nginx.conf`), with other field names.
