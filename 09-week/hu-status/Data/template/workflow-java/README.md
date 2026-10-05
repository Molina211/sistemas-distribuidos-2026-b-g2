# <abbr>-workflow (Java)

Orchestrates business processes that span several domains, as **sagas**.
Three-module Maven build.

```
workflow-core/       domain/saga (state machine) · application: ports + the orchestrator — no Spring
workflow-adapters/   in/http: start endpoint · out/participants: domain API clients · out/state: saga store
workflow-app/        Spring Boot application
```

1. **Every step has a compensation.**
2. **State is persisted after every step** — otherwise a restart leaves a saga half done.
3. **Compensations run in reverse order and are idempotent.**

Where the state lives in production is a decision the team records in an ADR.

## What this template already does

| Concern | Rule |
|---|---|
| Inbound contract | Same as a domain API: validates the JWT itself (RS256), validates the input, answers with the shared error envelope, camelCase JSON and `X-Correlation-Id`. |
| Idempotent start | `Idempotency-Key` header required. A retried request returns the same saga with 200 and runs nothing again. |
| Idempotent steps | Each do sends `Idempotency-Key: <sagaId>:<step>`, so a retried step never reserves or charges twice. Each undo is idempotent. |
| Credentials | Calls to the participants carry the workflow's own `SERVICE_TOKEN`, not the user's token, which may expire in the middle of a compensation. |
| Correlation | The `X-Correlation-Id` of the request travels to every participant. |
| Failure detail | The response names the `failedStep`; the full detail — hosts, statuses — stays in the saga state and the logs, never in the response. |
| Timeouts and retries | Every request has a timeout; only network errors, 429 and 5xx are retried, with exponential backoff and full jitter. |
