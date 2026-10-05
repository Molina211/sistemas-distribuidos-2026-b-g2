# Weekly Challenge, week 09, Session 1: secure configuration

> Harden your config: `.env.example` + startup validation of required vars, secrets in a
> store/injected (never in git), a pre-commit secret scan, and at least one feature flag
> guarding a new capability.

The challenge is built on a small stand-in for `multi-tour-security`, the security
microservice of Multitour (Go, as decided in ADR-004 D2). It uses only the Go standard
library. It is an exercise: the real service has not been started yet, and this folder
is the model its configuration will follow.

## What each requirement maps to

| Requirement | Where | How |
|---|---|---|
| `.env.example` | [`.env.example`](./.env.example) | Lists every variable the service reads, with placeholders only |
| Startup validation of required vars | [`config.go`](./config.go) | `LoadConfig` checks every variable before the server starts and reports **all** problems at once; the process exits with code 1. A malformed flag (`ture`) is an error, not a silent `false`. Error messages name the variable, never its value |
| Secrets injected, never in git | [`config.go`](./config.go), [`.gitignore`](./.gitignore) | Each secret is read from `NAME` or from a file path in `NAME_FILE`, which is how Docker secrets and secret stores mount a value. `.env`, `*.pem`, `keys/` and `secrets/` are ignored |
| Pre-commit secret scan | [`githooks/pre-commit`](./githooks/pre-commit) | Runs `gitleaks git --pre-commit --staged --redact`. If gitleaks is not installed the commit is blocked, not skipped |
| Feature flag guarding a new capability | [`main.go`](./main.go) | `FEATURE_TOKEN_REVOCATION` (default `false`). When it is off, `POST /api/v1/tokens/revocations` is not registered and answers `404`. When it is on, it answers `202` |

## How to run it

```sh
go test -v ./...                      # 7 tests
cp .env.example .env                  # then edit the placeholders
set -a; . ./.env; set +a; go run .    # no dotenv library: the shell loads the file

# enable the hook once per clone, from the repository root
git config core.hooksPath 09-week/hu-status/WeeklySummary/WeeklyChallenge/secure-config/githooks
```

## Evidence (Go 1.27.0, gitleaks 8.30.1)

`gofmt -l .` printed nothing, `go vet ./...` passed, and `go test -v ./...` passed 7 of 7 tests:
the missing variables are all reported at once, the flag defaults to off, a malformed flag is
rejected, a secret is read from `NAME_FILE`, no secret value appears in an error, the route
follows the flag, and an empty `tokenId` gives `400`.

Start with an empty environment:

```text
invalid configuration, not starting:
HTTP_ADDR is required
DATABASE_URL is required
JWT_PRIVATE_KEY is required
exit code: 1
```

Start with `FEATURE_TOKEN_REVOCATION=ture`:

```text
invalid configuration, not starting:
FEATURE_TOKEN_REVOCATION must be true or false
exit code: 1
```

Feature flag, same binary, two starts:

```text
FEATURE_TOKEN_REVOCATION=false  GET /health -> 200  POST /api/v1/tokens/revocations -> 404
FEATURE_TOKEN_REVOCATION=true   GET /health -> 200  POST /api/v1/tokens/revocations -> 202
```

Pre-commit hook, run in a throwaway repository with two fake AWS keys staged, then with
`.env.example` staged:

```text
WRN leaks found: 2        hook exit code (secret staged): 1
INF no leaks found        hook exit code (.env.example staged): 0
```

A full scan of this repository's history (`gitleaks git .`) checked 28 commits and found no
leaks. This folder (`gitleaks dir`) also has none. The hook is active in the author's clone
(`core.hooksPath`).

## Not covered here

- No secret store is configured in GitHub. The real repositories receive their secrets as
  GitHub environment secrets and Docker secrets (norm §5.9.2: in `qa` and `main` the keys and
  service tokens come from the identity service as environment secrets). Setting them is a
  manual step in the GitHub settings, and it waits for the real service.
- The flag is read once at startup. Switching it means a restart, with no runtime toggle,
  which is enough for a capability that is either released or not.
- The secure-config and rollout plan for MVP 2 is the Session 2 part of the challenge:
  [MVP2-Secure-Config-Rollout-Plan-Multitour.md](../MVP2-Secure-Config-Rollout-Plan-Multitour.md).
