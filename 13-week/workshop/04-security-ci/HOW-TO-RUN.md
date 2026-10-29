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

Filled in after the first real run.
