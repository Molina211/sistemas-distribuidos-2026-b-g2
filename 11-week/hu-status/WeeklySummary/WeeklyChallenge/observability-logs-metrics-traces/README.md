# Weekly Challenge - Week 11, Session 1: instrument the services

**The challenge:** structured logs with a propagated `trace_id`, basic metrics (rate, errors, latency) and
one distributed trace across a multi-service request.

**Scope of this document.** Of the three parts, **only the structured logs with a propagated id are
implemented**. There is no metrics endpoint and no distributed trace in any repository today, and the
observability stack that ADR-004 D8 decides (OpenTelemetry Collector, Prometheus, Loki, Tempo, Grafana) is
not built: `multi-tour-infra-postgres` has no `observability/` folder. This document says what the code
does, names the file or test that backs each claim, lists the gaps, and gives the smallest change that
closes each one. The tests named below were **not** re-run for this document; they are the ones the
repositories already carry.

The id is called `correlation id` in the contract (`X-Correlation-Id`) and `traceId` in the error envelope
and in most log lines; both are the same value, a safe string of `A-Za-z0-9._-`, 1 to 128 characters, or a
UUID the service generates when the caller sends none or an unsafe one.

## 1. Structured logs with a propagated id (implemented)

| Piece | Where the id is created or reused | What the log line carries | Backing |
|---|---|---|---|
| `multi-tour-api-gateway` | `nginx/conf.d/10-security.conf` (`map` keeps a safe incoming id, else NGINX generates one); returned in `X-Correlation-Id` and forwarded to every service (`20-server.conf`) | One JSON object per request: `time`, `correlationId`, `method`, `uri`, `status`, `upstream`, `durationMs` (`nginx/nginx.conf`) | `tests/smoke.sh`: `correlation_is_created`, `correlation_is_kept`, `unsafe_correlation_is_replaced`, `correlation_reaches_the_service` |
| `multi-tour-security` (Go) | `internal/adapter/http/server.go`, middleware `correlate` | JSON through `slog`; a `request` line with `traceId`, `method`, `path`; the same id is the `traceId` of every error body | `TestC_CorrelationIdIsReusedOrGenerated`, `TestC_ACorrelationIdThatIsNotSafeIsReplacedAndTheBodyOfTheAnswerHasNoExtraKeys` |
| `multi-tour-identity-audit-api` (Java) | `CorrelationIdFilter`, first in the chain, also on the error dispatch | The id is put in the MDC key `traceId`, so every log line of the request has it; it is the `traceId` of the error envelope | Filter in `adapters/.../http/CorrelationIdFilter.java` |
| `multi-tour-workflow-onboard-tenant` (Go) | `withCorrelation` in `internal/adapter/in/httpapi/handler.go` | `traceId` on the error and token-refused lines; the id is also sent in `X-Correlation-Id` to `identity-audit` and `security` on every step call (`internal/adapter/out/participants/client.go`) | `TestC_ARequestWithoutAUsableCorrelationIdGetsANewOne`, `TestC_AnErrorCarriesTheCorrelationIdAsTraceId`, `TestC_ACallWithoutCorrelationIdGeneratesOne`, `TestC_AMalformedCorrelationIdIsReplacedNotSent` |
| `multi-tour-worker-runtime` (Go, shared by the workers) | `runner.go` creates **one id per run** and puts it in the context | `logging/logging.go` adds `correlationId` to every line; `httpclient/client.go` sends it in each call | `TestC_log_lines_carry_the_run_correlation`, `TestC_call_carries_token_and_correlation` |
| `multi-tour-front` | `src/app/core/client/api-client.ts` creates a fresh id per request | not a log: the id is attached to the request and the error shown to the person carries its `traceId` | `client.spec.ts`, `client-requests.spec.ts` |

So one request of a person can be followed by hand: the front creates the id, the gateway writes it in its
access log and forwards it, the service puts it in its lines, the saga forwards it to the participants, and
the id comes back in the response header and in the `traceId` of any error. A person with an error on the
screen can give that `traceId` and the log lines of every piece are found with one search.

**What a line does not carry yet.** ADR-004 D8 asks for time, level, service, correlation id, trace id,
`tenantId`, actor (`sub`), action and result. Today:
- the key is **not the same everywhere**: `correlationId` in the gateway and the runtime, `traceId` in
  `security`, the saga and the Java API; a log query has to look for both;
- no service writes `tenantId`, the actor or the service name in each line (the `security` request line has
  only id, method and path);

## 2. Basic metrics: rate, errors, latency (not implemented)

Nothing exposes metrics. What exists is the raw material, in the gateway access log: `status` and
`durationMs` per request, so rate, error ratio and a latency percentile **can be computed from the logs**
once something reads them, but no one does.

| Metric (ADR-004 D8) | Today | Smallest change that closes it |
|---|---|---|
| Requests per second | only countable from the gateway log | one counter in the gateway log pipeline, or `stub_status` + exporter; then the Go and Java services with the OpenTelemetry SDK / agent |
| Error rate | `status` of each gateway line | ratio of `status >= 500` over requests, per route file |
| p95 latency | `durationMs` of each gateway line | histogram of `durationMs`, p95 per route; the NFR-006 threshold is 3 s |
| Availability | `/health` exists in `security`, the saga and the gateway (`TestC_HealthAnswersWithoutToken`, `TestC_HealthNeedsNoTokenAndAnswers200`) | a probe that calls `/health` every 30 s and counts the result |
| Worker: age of the oldest pending outbox event | not measured | the relay already reads the pending events through the outbox route of each service; the age of the oldest one is a value it can write per run |

## 3. One distributed trace across a multi-service request (not implemented)

There is **no trace**: no span is created and no `traceparent` header is sent. What exists is the
correlation id, which links log lines but does not give timing per hop.

The request that crosses most services today is the first-administrator onboarding:
`front -> gateway -> workflow-onboard-tenant -> identity-audit (create tenant) -> security (create
administrator) -> identity-audit (record administrator)`. With the id above, the full path of one onboarding
is reconstructed from the logs of five pieces, but how long each hop took is only visible for the first one
(`durationMs` in the gateway). Closing the gap is the plan of ADR-004 D8: the OpenTelemetry Java agent for
the Java API (no change in the domain) and the Go SDK for `security` and the saga, with Tempo to store the
spans, and the W3C `traceparent` header added to the list that the gateway, the front client and the
saga participant client already forward for `X-Correlation-Id`.

## 4. What is not proven

- The log lines were not captured from a running composition for this document; the claims come from the
  code and from the tests the repositories carry. The demo composition has not been run with the final
  versions (see [`../../../README.md`](../../../README.md)).
- Nothing was measured: there are no latency or error numbers in this document, on purpose.
- The Docker composition has no collector, Prometheus, Loki, Tempo or Grafana.

## 5. Order in which I would close the gaps

1. Use one key (`correlationId`, as the contract and the ADR say) in the Go services and the Java MDC,
   and add `service`, `tenantId` and `sub` to the request line. Small, testable, no new tool.
2. Metrics from the gateway access log: requests, `5xx` ratio and p95, because every request already goes
   through it. This is what the SLOs of Session 2 need.
3. A `/health` probe and the age of the oldest outbox event, for availability and freshness.
4. Spans with OpenTelemetry on the onboarding saga, and `traceparent` forwarded.
