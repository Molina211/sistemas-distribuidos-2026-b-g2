# Weekly Challenge - Week 14, Session 1: set up your CI/CD

**The challenge:** pull requests run build, lint, type check, tests, coverage and security as blocking gates; protect
`main`; automate the deployment of the tagged image.

**Scope of this document:** it records the gates that the `ci.yml` of each repository already runs, read in the local
clones on 2026-11-14, and states what is missing. The gates were written story by story inside the team repositories,
each with its spec and pull request; this document adds no code to them.

## 1. Gates that exist today, by kind

| Gate | Java APIs (`identity-audit-api`, `operations-cost-api`) | Go (`security`, workers, sagas, `worker-runtime`) | Portals and `front` | Gateway, `-db`, infra |
|---|---|---|---|---|
| **Build** | `mvn -B verify` with Java 21 | `go test ./...` compiles every package | `npm ci` and `npm run build` | `nginx -t` (gateway); `docker compose config` (infra) |
| **Lint / format** | Spotless and the unused-imports check inside `verify` (`operations-cost-api` #19) | `test -z "$(gofmt -l .)"` and `go vet ./...` | `npm run lint` (ESLint) and `npm run format:check` (Prettier) in `operations-cost-portal` (#30) | — |
| **Type check** | The compiler of `verify` | The compiler of `go test` / `go vet` | `npm run build` with TypeScript strict | — |
| **Tests** | JUnit 5; integration against a real PostgreSQL by `TEST_DATABASE_URL`, and a step that fails if an integration test was skipped | `go test ./...` against a PostgreSQL service of the job (`TEST_DATABASE_URL`) | `npm test` (Vitest); `front` also runs Playwright against its production image | `tests/smoke.sh` (gateway); `db/tests/db-ci.sh`: build from empty, second run applies zero, roll back, rebuild |
| **Coverage** | JaCoCo with a floor: `COVEREDRATIO` 0.95 in the domain module of `operations-cost-api` (`core/pom.xml`, #18) | Not measured as a gate | Not measured as a gate | — |
| **Contract** | `swagger-request-validator` checks each HTTP answer against the OpenAPI of the repository (`app/src/test/.../Contract.java`) | `kin-openapi` in `internal/adapter/http/contract_test.go` | — | — |
| **Security** | — | — | `front`: a step fails if the production build carries the development sign-in (`scripts/check-main-build.sh`) and a second step proves the check detects it | — |
| **Image** | `deploy/tests/deploy-check.sh` builds the image, starts it and checks what it publishes | `docker build` of the service image | `front`: builds the production image and runs it | — |

Common rules of every `ci.yml` (ADR-004 D7 and the professor's notice of 2026-10-07): it runs only on pull requests
toward `develop` and `qa`, with `concurrency` and `cancel-in-progress: true`, `permissions: contents: read`,
`timeout-minutes: 10`, and no `schedule`.

## 2. What is missing, stated as it is

| Gap | Effect | What I would do |
|---|---|---|
| **No gate blocks today.** GitHub Actions of `code-corhuila` is locked by billing since 2026-10-14; every job fails in 2 s ("The job was not started because your account is locked due to a billing issue") | The gates exist but nothing stops a merge; the team merges with the CI red and records the local checks in each PR | Re-run every open PR when the professor unlocks Actions |
| **No security gate in the team CI.** No `ci.yml` runs `gitleaks`, `govulncheck`, `npm audit` or a dependency check | Known vulnerabilities can enter; in week 13 `govulncheck` found 3 in `security` | Add the four steps of my week 13 fork workflow (`13-week/workshop/04-security-ci/security.yml`), one repository per pull request |
| **Coverage floor only in one module** | A drop in coverage in the Go services or the portals is not seen | A JaCoCo floor in every Java module at the current baseline; `go test -cover` with a floor; Vitest `--coverage` with a threshold |
| **Lint only in one portal** | The other portals can merge unformatted code | Copy `lint` and `format:check` to the other portals of my domains |
| **Protection of `main`** | It is configured by the professor in two organization rulesets (one approval of `@ariel5253`, CODEOWNERS). I cannot read them with my token, and changing them is forbidden to the team (norm §13) | Nothing to do on the team side |
| **No automated deployment of the tagged image.** The image is built in CI only to prove it builds; it is not pushed to a registry, and the course runs everything with Docker Compose (ADR-004 D9: no cloud operated by the team) | A tag does not produce a deployable artifact | A workflow on `push` of a tag `v*` that builds and pushes the image to GHCR with the tag as its version; it must wait for Actions and for the professor's approval, since it is CI configuration of the organization |

## 3. The gates of my Definition of Done

The mapping of every DoD item to a gate is in the session 2 document,
[DoD-Gates-And-Pipeline-Stories-Multitour.md](./DoD-Gates-And-Pipeline-Stories-Multitour.md).

## 4. Practical run in my fork

Since Actions of the organization is locked, the gates were copied into my fork and run there on Saturday 2026-11-14,
on two reduced Go services (`14-week/challenge/services/booking`, money in minor units, and `notices`, the subject of a
business e-mail). The gates are written once in a reusable workflow, `.github/workflows/w14-go-gates.yml`, and every
step fails the run:

| Gate | Step |
|---|---|
| Lint | `gofmt -l .` must be empty |
| Lint and type check | `go vet ./...` (and the compiler) |
| Build | `go build ./...` |
| Tests | `go test -coverprofile=cover.out ./...` |
| Coverage | total coverage at least 80 % (`min-coverage`); the comparison was checked by hand: 79.9 fails, 80.0 and 100.0 pass |
| Security | `govulncheck ./...` (known vulnerabilities) and `gitleaks` (secrets in the history) |

First run, commit `777cd1b`: both pipelines green, coverage 100.0 % against the floor of 80 %, "No vulnerabilities
found", "no leaks found".
[w14-booking](https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/43664138153) ·
[w14-notices](https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/43664138132)

Not covered by this copy: a portal (its gates are `npm run lint`, `format:check`, `build` and `test`, already in
`operations-cost-portal`), the protection of `main` (the fork is a personal repository with direct commits, the rule of
the Weekly), and pushing a tagged image to a registry.
