# How to run the instrumentation module

Module `event-relay`: the instrumentation of the `multi-tour-worker-event-relay` side of the journey of CP1
(a `TenantInactivated` event delivered to `multi-tour-security`). Go standard library only, no dependency to
install and no `go.sum`.

| Package | What it does |
|---|---|
| `trace` | parses, creates and formats the W3C `traceparent`; keeps it and the correlation id in the context |
| `logging` | writes the JSON lines of `../../02-log-contract.md`, with `trace_id`, `span_id` and `correlation_id` taken from the context |
| `middleware` | reads or creates the trace context of an incoming request and returns it in the answer headers |
| `metrics` | counter `relay_deliveries_total` and histogram `relay_delivery_duration_seconds`, in Prometheus text format |
| `deliver` | sends one event to a consumer copying the trace context to the call, and records the two metrics |
| `cmd/demo` | delivers one event to a local stand-in for `security` and prints the logs of both sides and `/metrics` |

## Requirements

Go 1.22 or newer. Checked on Windows 11 with:

```
$ go version
go version go1.27.0 windows/amd64
```

## Install

Nothing to install: the module has no external dependency.

## Test

From this folder (`11-week/workshop/03-instrumentation/event-relay`):

```
go vet ./...
go test -count=1 -v ./...
```

Output of my machine on 2026-10-19:

```
?   	.../event-relay/cmd/demo	[no test files]
--- PASS: TestTheTraceIdReceivedIsTheTraceIdSent (0.05s)
--- PASS: TestARefusalAndAnUnreachableConsumerAreCountedWithTheirOutcome (0.00s)
ok  	.../event-relay/deliver	0.145s
--- PASS: TestALineCarriesEveryRequiredFieldOfTheContract (0.00s)
ok  	.../event-relay/logging	0.194s
--- PASS: TestTheCounterAndTheHistogramAreWrittenInTheTextFormat (0.00s)
ok  	.../event-relay/metrics	0.146s
--- PASS: TestAnIncomingTraceIsContinuedAndReturned (0.00s)
--- PASS: TestARequestWithoutUsableIdsStartsANewTrace (0.00s)
ok  	.../event-relay/middleware	0.119s
--- PASS: TestParseAcceptsAWellFormedTraceparent (0.00s)
--- PASS: TestParseRefusesWhatIsNotATraceparent (0.00s)
--- PASS: TestChildKeepsTheTraceAndChangesTheSpan (0.00s)
--- PASS: TestOnlySafeCorrelationIdsAreKept (0.00s)
ok  	.../event-relay/trace	0.160s
```

The test that proves the requirement of CP3 is `TestTheTraceIdReceivedIsTheTraceIdSent` in `deliver/`: a
local stand-in for `security`, behind the middleware, receives the call and the test checks that the
`trace_id` it saw is the `trace_id` that was in the context of the sender, with a new `span_id`.

## Run the demo

```
go run ./cmd/demo
```

Output of my machine on 2026-10-19. The two log lines have the same `trace_id` and `correlation_id`, and the
`span_id` is different in each, as in the span tree of `../../01-journey.md`:

```
{"timestamp":"2026-10-19T14:19:27.567Z","level":"INFO","message":"tenant copy updated","service":"security","version":"2.0.0","environment":"dev","event":"tenant.event.applied","duration_ms":1,"event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","tenant_status":"INACTIVE","trace_id":"96b7051dfb8158ae2becb0a67e223ca4","span_id":"abd65a6ca2af4b86","correlation_id":"cor-5b02bc28ae81"}
{"timestamp":"2026-10-19T14:19:27.568Z","level":"INFO","message":"delivery to security: ok","service":"worker-event-relay","version":"2.0.0","environment":"dev","event":"event.delivery.ok","duration_ms":1,"event_id":"d41d8cd9-8f00-4b20-b204-e9800998ecf8","http_status":200,"trace_id":"96b7051dfb8158ae2becb0a67e223ca4","span_id":"e7b9cc678fb3b583","correlation_id":"cor-5b02bc28ae81"}
--- /metrics ---
# HELP relay_deliveries_total Deliveries of events to a consumer, by outcome.
# TYPE relay_deliveries_total counter
relay_deliveries_total{consumer="security",outcome="ok"} 1
# HELP relay_delivery_duration_seconds Duration of a delivery to a consumer.
# TYPE relay_delivery_duration_seconds histogram
relay_delivery_duration_seconds_bucket{consumer="security",le="0.05"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="0.1"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="0.25"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="0.5"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="1"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="2.5"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="5"} 1
relay_delivery_duration_seconds_bucket{consumer="security",le="+Inf"} 1
relay_delivery_duration_seconds_sum{consumer="security"} 0.0018025
relay_delivery_duration_seconds_count{consumer="security"} 1
```

## What is not covered

- The `timeout` outcome of `deliver` has no test; the tests cover `ok`, `rejected` and `error`.
- The module is not the real relay: it is the instrumentation that the relay would use. The real relay
  (`multi-tour-worker-event-relay`) does not use it yet, and the trace context is not yet stored inside the
  outbox event, so the demo starts the trace in the run of the relay.
- `middleware` is shown on the receiving side (the stand-in for `security`), not on a server of the relay.
