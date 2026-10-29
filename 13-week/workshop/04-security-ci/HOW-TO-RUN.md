# CP4 - Security gates and negative authorization test

Boundary: `multi-tour-api-gateway` to `multi-tour-security`. The test covers story
[#584](https://github.com/code-corhuila/multi-tour-docs/issues/584) (STRIDE row E, privilege escalation across tenants).

## What the test checks

Service `multi-tour-security`, endpoint `POST /api/v1/security/internal/tenants/{tenantId}/administrators`
(reserved to the `SERVICE` role). A valid token of tenant A, as Tenant Administrator or as Final Customer, calls the route
of tenant B. Expected: `403 FORBIDDEN`, the use case never runs, and the answer does not repeat the id of tenant B.

`/security/users` and the memberships routes are in the contract but not in the service yet, so the story criterion
"a token of tenant A gets `404` or `403` on tenant B" is covered here on a route that exists, and moves to the memberships
routes when they are built.

## How to run it

Locally, from a clone of `multi-tour-security` (`develop`):

```bash
cp authorization_negative_test.go internal/adapter/http/
go test -run TestW13 -v ./internal/adapter/http/
```

In GitHub Actions: `security.yml` clones `multi-tour-security`, copies the test and runs the same command in the job
`authorization`. The other two jobs are `secrets` (gitleaks over the whole history) and `vulnerabilities`
(`govulncheck ./...`). It runs on `pull_request` and by hand with `workflow_dispatch`, because the organization's
Actions are locked by billing and my fork has its own.

## Result

First manual run (`workflow_dispatch` on `main`): https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682

| Job | Result |
|---|---|
| `secrets` (gitleaks) | success, no leaks |
| `authorization` (negative test) | success, `TestW13_ATokenOfTenantACannotCreateAnAdministratorInTenantB` passes |
| `vulnerabilities` (govulncheck) | **failure**, a real finding |

`govulncheck` found 3 vulnerabilities that the code of `multi-tour-security` calls: GO-2026-6629 and GO-2026-5970 in
`golang.org/x/text@v0.24.0` (fixed in v0.41.0 and v0.39.0) and GO-2026-5004 in `github.com/jackc/pgx/v5@v5.7.6`
(SQL injection through dollar-quoted strings, fixed in v5.9.2; reached from `postgres.Accounts.RolesOf`). The gate did its job.
Raising those two dependencies belongs to the owner of `multi-tour-security` and is not done in this workshop.
