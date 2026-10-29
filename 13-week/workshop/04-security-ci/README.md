# 04-security-ci

Security gates proposed for the boundary `multi-tour-api-gateway` to `multi-tour-security`.

| File | What it is |
|---|---|
| `security.yml` | Workflow with three jobs: gitleaks, govulncheck and the negative authorization test |
| `authorization_negative_test.go` | Test of story #584: a token of tenant A gets `403` on the service-only route of tenant B |
| `HOW-TO-RUN.md` | How to run the test locally and which story and criterion it covers |

Workflow run I saw (manual, `workflow_dispatch`):
https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682

Result: `secrets` success, `authorization` success, `vulnerabilities` failure (3 real findings, listed in `HOW-TO-RUN.md`).
