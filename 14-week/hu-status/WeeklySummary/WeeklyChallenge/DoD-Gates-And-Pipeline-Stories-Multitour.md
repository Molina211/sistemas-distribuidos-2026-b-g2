# Weekly Challenge - Week 14, Session 2: DoD to gates, protections, per-service pipelines and MVP 3 slicing

**The challenge:** map every Definition of Done item to a blocking gate, enable branch protection on `develop`, `qa`
and `main`, add path-filtered per-service pipelines and cross-service contract tests, a smoke test, and slice the CI/CD
stories for MVP 3.

**Scope:** documentation of what exists on 2026-11-14 and of what I would build. Branch protection is not configured
by me: the rulesets belong to the professor, and altering them is a serious fault (norm §13).

## 1. Every DoD item and its gate

| DoD item (constitution and CLAUDE.md §4) | Gate | Blocks today? |
|---|---|---|
| The code builds | `mvn -B verify`, `go test ./...`, `npm run build`, `nginx -t` | Would block; Actions is locked |
| Format and lint clean | Spotless, `gofmt -l .`, `go vet`, ESLint, Prettier | Would block where present (§2 of session 1 lists the gaps) |
| Every acceptance criterion has its test, seen red then green | `test(...)` commit before `feat(...)` in the PR; the tests run in `verify` / `go test` / `npm test` | The order is visible in each PR; the run would block |
| Integration against the real engine | `TEST_DATABASE_URL` against a PostgreSQL of the job, and a step that fails if an integration test was skipped | Would block |
| The answer matches the contract (API First) | `swagger-request-validator` and `kin-openapi` in the tests | Would block |
| Coverage does not fall below the baseline | JaCoCo floor 0.95 in the domain of `operations-cost-api` | Would block there only |
| The migrations build from empty, rerun with zero and roll back | `db-ci.sh` / `db-ci.yml` | Would block |
| The image builds and starts | `docker build`, `deploy-check.sh` | Would block |
| No development sign-in in a production build | `check-main-build.sh` in `front` | Would block |
| No secrets | Not a gate yet (only `.gitignore` and review) | No |
| One approval from another member; promotion with `cherry-pick -x` | Organization rulesets and the reviewer of `qa` who checks the `cherry picked from` lines | Enforced by the professor's rulesets |
| PR under 400 visible lines, story cited | Review | No automated gate |

## 2. Branch protection on `develop`, `qa` and `main`

The organization `code-corhuila` protects the three branches with two rulesets of the professor (no direct commit,
pull request with one approval, CODEOWNERS on `main` with `@ariel5253`). The team does not edit them. The branch rules
endpoint answers an empty list to my token, so I do not claim more than that. `multi-tour-worker-runtime` was created by
the team and waits for the professor to add it to the rulesets.

## 3. Per-service pipelines with a path filter

The topology already gives one pipeline per service: there are 31 repositories, one per piece (norm §4.5, ADR-004 D1
and D3), and each has its own `ci.yml`. A change to `security` never runs the CI of `operations-cost-api`. A path
filter (`on.pull_request.paths`) would only matter inside one repository; the candidate is `-api` repositories where a
change to `README.md` or `docs/` still runs the whole `verify`:

```yaml
on:
  pull_request:
    branches: [develop, qa]
    paths-ignore: ["**.md"]
```

[PENDIENTE: if authorized, a copy in my fork with two services in two folders and `paths` filters, to show that only the touched one runs]

## 4. Cross-service contract tests and smoke test

- **Contract.** Each provider validates its own answers against its OpenAPI in `07-api/contracts/openapi/` (ADR-004
  D7, API First instead of Pact). What is missing is the consumer side: the workers and sagas call other services with
  hand-written clients and no test checks those calls against the provider's contract. I would add, in each saga, a
  test that validates every request its HTTP adapter builds against the provider's OpenAPI file.
- **Smoke.** `multi-tour-api-gateway/tests/smoke.sh` builds the image, runs `nginx -t` and checks the edge over HTTP
  against stubs that answer as the domains: health `200`, unknown route `404`, the `X-Correlation-Id` created, kept or
  replaced when unsafe, CORS only for the allowed origin, the security headers, and a stopped service answering `503`
  while the others keep working. A smoke test of the whole platform after
  `scripts/up.sh` does not exist: the health route of every service through the gateway would be its first version.

## 5. CI/CD stories for MVP 3

| Story | Acceptance criterion | Size |
|---|---|---|
| Security gates in CI | `gitleaks`, `govulncheck` / `npm audit` fail the PR on a finding; one repository per PR | S each |
| Coverage floor everywhere | Each repository fails if coverage drops below its baseline of the day it is added | S each |
| Consumer contract tests in the sagas | A request that the provider's OpenAPI rejects fails the saga's tests | M |
| Platform smoke test | After `up.sh`, the health of every service answers `200` through the gateway, or the run fails | M |
| Publish the tagged image | A tag `v*` builds and pushes the image to GHCR with that version | M, needs the professor |
| Re-run the CI of every open PR | Done when Actions is unlocked | S |
