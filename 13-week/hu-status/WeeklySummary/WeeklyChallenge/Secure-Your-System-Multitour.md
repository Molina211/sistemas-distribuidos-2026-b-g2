# Weekly Challenge - Week 13, Session 1: secure your system

**The challenge:** authenticate at the edge, validate tokens and authorize in each service, secure
service-to-service calls, hash secrets, and validate inputs.

**Scope of this document: it records what the project already does, it adds no code.** Every row of section 1 was
read in the local clones of the repositories on 2026-10-29 (`multi-tour-security` on `develop`, commit `d0e0f5c`; the
other repositories on the branch each clone had checked out). Changing code belongs to the user stories of each
repository, with their spec and pull request, so what is missing is listed in section 2 and not built here. No command
was run for this document other than reading files and the workflow run of the week 13 workshop.

## 1. What exists today, by topic of the challenge

| Topic | What the project does | Where (checked 2026-10-29) |
|---|---|---|
| **Authenticate at the edge** | The gateway is the only published port. A protected route with no `Authorization` header gets `401` with the common error envelope before it reaches a service; `/security/public/` routes are the only open ones and get a tighter rate limit (`credentials` zone). The gateway **does not validate the token**: it only filters that a credential exists (norm 5.6.2) | `multi-tour-api-gateway/nginx/snippets/require-credential.conf`, `nginx/conf.d/10-security.conf` |
| **Block internal routes at the edge** | Any `/api/v1/.../internal` path answers `404` from outside, and a regex location wins over the prefixes so no route can reopen it (ADR-004 D5) | `multi-tour-api-gateway/nginx/conf.d/20-server.conf` |
| **Validate tokens in each service** | `multi-tour-security` accepts exactly `RS256` (`jwt.WithValidMethods`), requires `exp` and `sub`, and answers one `401` for every rejection, logging the reason with the `traceId` and never returning it. The Java API of identity-audit does the same in its own filter: algorithm exactly RS256, signature with the public key of the security service | `multi-tour-security/internal/adapter/http/auth.go`, `server.go` (`protect`); `multi-tour-identity-audit-api/.../adapters/security/JwtAuthFilter.java` |
| **Authorize in each service** | `requireService` lets only a token with the single role `SERVICE`, no tenant and no access mark reach the internal routes; anyone else gets `403 FORBIDDEN`. The role and the tenant come from the token, never from the body. Tests cover person, platform administrator and `SERVICE` plus another role | `multi-tour-security/internal/adapter/http/outbox.go` (`requireService`), `firstadmin_test.go` (`TestC_OnlyTheTokenOfAServiceCreatesTheFirstAdministrator`) |
| **Service to service** | Workers and workflows call the APIs with their own `SERVICE_TOKEN`, read from the environment and never versioned (`.env.example` holds only placeholders). Only the gateway publishes a port; the services use `expose` | `multi-tour-worker-runtime/config/config.go`, `.env.example`; `compose.yml` of each repository |
| **Hash secrets** | Passwords with bcrypt at cost 12 (ADR-004 D5); a dummy comparison runs when the account does not exist, so the time does not tell the two cases apart. Refresh and verification tokens are stored only as SHA-256 hashes. `.env`, `*.pem` and `keys/` are ignored by Git, and only `.env.example` is versioned | `multi-tour-security/internal/adapter/bcrypt/hasher.go`, `internal/core/session/session.go` (`Hash`), `.gitignore` |
| **Validate inputs** | The sign-in body is read with `http.MaxBytesReader`, requires `Content-Type: application/json`, and rejects unknown fields (`DisallowUnknownFields`); the answer to a `400` names the invalid fields, never their values. `ReadHeaderTimeout` is set in the server. A path `tenantId` that is not a UUID is a `400` that never reaches the store | `multi-tour-security/internal/adapter/http/signin.go`, `cmd/security/main.go`, `firstadmin_test.go` |

## 2. What is missing, stated as it is

| Gap | Effect | Status |
|---|---|---|
| **No mTLS or TLS between services.** Nothing in the gateway configuration or the compositions uses it; the hop gateway to service is plain HTTP. The only protection of that hop is that the network is internal and that each service validates the token itself | Matches STRIDE row T of my threat model (risk 3), which I accepted for the course environment | Declared in `05-architecture/security/stride-Molina211.md` (Docs PR #579, corrected in PR #587) |
| **The gateway does not validate the token.** The slides show a gateway that validates; this design validates in each service instead, and the gateway only checks presence | Zero-trust inside the network is kept (each service verifies), but the edge does not drop a forged token | A design choice (norm 5.6.2), not an oversight |
| **Dependencies with known vulnerabilities.** `govulncheck` reports 3 that the code calls: `golang.org/x/text@v0.24.0` (`GO-2026-6629`, `GO-2026-5970`) and `github.com/jackc/pgx/v5@v5.7.6` (`GO-2026-5004`, reached from `postgres.Accounts.RolesOf`) | A known weakness in the code of the security service | Found by the workflow run below; the upgrade is a change to `multi-tour-security` and is not done |
| **Routes of the contract that are not in the service yet:** `POST` and `GET /security/users` and the memberships routes | The authorization of those routes cannot be tested today | Listed in the threat model; the negative test uses an existing route |
| **Rotation of service credentials** | The slides ask that credentials be rotated. I did not find a rotation mechanism for `SERVICE_TOKEN` in the files I read | Not verified beyond reading `config.go`; stated as unknown |
| **Input validation in the Java APIs** | I did not find Bean Validation annotations in `multi-tour-identity-audit-api`, and I did not check how its request bodies are validated | Not verified; stated as unknown |

## 3. Evidence produced this week (workshop, session 2 material)

| What | Link |
|---|---|
| Threat model of the boundary `multi-tour-api-gateway` to `multi-tour-security` (STRIDE, risk ranking) | [Docs PR #579](https://github.com/code-corhuila/multi-tour-docs/pull/579), corrections in [PR #587](https://github.com/code-corhuila/multi-tour-docs/pull/587) |
| Three security stories from the top risks | [#584](https://github.com/code-corhuila/multi-tour-docs/issues/584), [#585](https://github.com/code-corhuila/multi-tour-docs/issues/585), [#586](https://github.com/code-corhuila/multi-tour-docs/issues/586) |
| Security gates and a negative authorization test | `13-week/workshop/04-security-ci/` in this repository; workflow run [40943113682](https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682): gitleaks and the authorization test passed, govulncheck failed with the findings above |
| Attack surface and trust boundaries | `13-week/workshop/01-attack-surface.md` |

## 4. How each topic of the challenge would be checked (proposal, not run)

- **Edge:** `curl` to a protected route without `Authorization` expects `401` with the envelope; to `/api/v1/security/internal/...` expects `404`.
- **Token and authorization:** the negative test of the workshop (`TestW13_...`) plus the existing tests of the service; a token signed with `HS256` or `none` expects `401`.
- **Secrets:** `gitleaks` over the history (it passed in the run above) and a check that no `.env` is tracked.
- **Inputs:** a body with an unknown field and one over the size limit expect `400`.
- **Dependencies:** `govulncheck ./...` in the pipeline; a finding fails the build, as it did in the run above.

Closing the gaps of section 2 is work for the stories of the security backlog (#584 to #586) and for the repositories
involved, each with its spec and pull request.
