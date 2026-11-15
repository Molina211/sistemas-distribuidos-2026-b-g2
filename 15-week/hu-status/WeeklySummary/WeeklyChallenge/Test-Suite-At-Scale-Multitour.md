# Weekly Challenge - Week 15, Session 1: complete your test suite

**The challenge:** integration tests with Testcontainers, contract tests in CI, a couple of end-to-end money-path tests,
a load test to your SLO, and one chaos experiment (kill a service, confirm graceful degradation).

**Scope:** what the suite has on 2026-11-14, read in the local clones, and what is missing.

## 1. What exists

| Kind | What the project does | Where |
|---|---|---|
| **Integration** | Against the real engine through `TEST_DATABASE_URL`: a PostgreSQL service of the job in the Go repositories; in `operations-cost-api`, the database that `operations-cost-db` builds, and a CI step that fails if any `*Postgres*Test` was skipped. Testcontainers is **not** used on purpose: the norm fixes `TEST_DATABASE_URL` (Annexes C and I) | `multi-tour-security/.github/workflows/ci.yml`, `operations-cost-api/.github/workflows/ci.yml` |
| **Contract in CI** | Every HTTP answer of the tests is validated against the OpenAPI of the repository (`swagger-request-validator` in Java, `kin-openapi` in Go) | `operations-cost-api/app/src/test/.../Contract.java`, `security/internal/adapter/http/contract_test.go` |
| **Migrations** | Build from empty, a second run applies zero, total rollback, rebuild; and this week a test that every migration runner of the root composition applies and a second run applies nothing | `db/tests/db-ci.sh`, `multi-tour-infra-postgres` #46 |
| **End to end** | Playwright on the production image of `multi-tour-front`; end-to-end runs on the dev stack recorded in the evidence of each story (for example Docs #816, password recovery) | `multi-tour-front/.github/workflows/ci.yml`, Docs |
| **Resilience by design** | The gateway resolves DNS per request (`resolver 127.0.0.11 valid=10s`), so a service that is down answers `503 SERVICE_UNAVAILABLE` on its routes and the others keep working (norm §5.6.3); a portal that fails to load shows a notice only in its area (ADR-007, `remote-loader.ts`); the relay retries only network, `429` and `5xx`, and a `4xx` is one attempt per run (relay #41) | `multi-tour-api-gateway/nginx/conf.d/00-resolver.conf`, `multi-tour-front` |

## 2. What is missing

| Gap | Why | What I would do |
|---|---|---|
| **Money path end to end** | Payments, refunds and cash belong to commercial-reservation and cash-reporting, the services of my teammate, which I may only read and test | Once her services are in `develop`: register a payment, then a refund, through the gateway, checking the amounts in minor units (`totalCents`) |
| **Load test to an SLO** | No SLO is written in Docs and no load tool is in the team repositories | Run once this week (section 3); next, write the SLO in Docs and load an authenticated route such as the departures list |
| **Chaos experiment** | In CI only at the edge: the gateway smoke test (`tests/smoke.sh`) stops the stubs of the sagas and checks that their routes answer `503` while the other domains keep answering | Run once this week against a real service (section 3); next, the same with `operations-cost-api` when the dev stack is free |
| **The CI does not run** | Actions locked by billing | Re-run when unlocked |

## 3. Practical run on the dev stack

Run on Saturday 2026-11-14 on the dev stack, through the gateway; scripts and full output in
[15-week/challenge/](../../../challenge/RESULTS.md).

| Run | What | Result |
|---|---|---|
| **Load test** (`load-tenant-profile.js`, k6 0.54.0) | `GET /api/v1/identity-audit/public/tenants/travesia-natural` (gateway -> identity-audit-api -> MongoDB), 40 requests per second for 1 minute; SLO p95 < 300 ms and < 1 % failed | **SLO met**: 2,399 requests, 0.00 % failed, p95 9.69 ms, median 5.75 ms |
| **Chaos experiment** (`chaos-stop-a-saga.sh`) | Stop the saga `onboard-tenant`, probe its route and identity-audit, start it again | Before `401` / `200`; during `503 SERVICE_UNAVAILABLE` with the common envelope and `traceId` / identity-audit still `200`; after `401` / `200`, the saga back to `healthy` in about 20 s with no gateway restart |

Limits of these runs: one path and one load level, on a laptop; the victim of the chaos is a saga and not
`operations-cost-api`, because other work was using operations-cost on the shared dev stack at that moment. The two runs
are not in CI: they need the whole stack up.
