# <abbr>-worker (Java)

Background jobs, three-module Maven build. The **inbound adapter** is a
scheduler instead of HTTP.

```
worker-core/       Job, Result, the jobs themselves and their ports — no Spring
worker-adapters/   in/scheduler: @Scheduled runner · out/orders: HTTP client with bounded retry
worker-app/        Spring Boot application with scheduling enabled
```

Every job must be **safe to run twice**. A failure on one item never stops the batch.
The worker reaches other domains only through their APIs — never their databases.

## What this template already does

| Concern | Rule |
|---|---|
| Credentials | Every call carries `Authorization: Bearer $SERVICE_TOKEN`: the API validates the worker like any other caller. The token is issued by the identity service and never versioned; `<abbr>-infra/scripts/dev-keys.sh` writes a development one into `.env`. |
| Correlation | One `X-Correlation-Id` per run, sent with every call and written in every log line: a run can be followed across the whole system. |
| Bounded runs | A run reads at most `BATCH_SIZE` items (the API's `limit`) and stops when its run timeout passes; what is left waits for the next run. |
| Timeouts | Every request has its own timeout. A call without one can hang a run forever. |
| Retries | Only network errors, 429 and 5xx, a bounded number of attempts, exponential backoff with **full jitter**. A 4xx is never retried: it will fail the same way. |
| Contract | Reads lists as `{data, meta}` and reports the `error` code and `traceId` of the shared error envelope. |

```bash
SERVICE_TOKEN=$(../<abbr>-infra/scripts/dev-token.sh svc-worker 60) ORDERS_API_URL=http://localhost:8080 java -jar worker-app/target/worker-app-0.1.0.jar
```
