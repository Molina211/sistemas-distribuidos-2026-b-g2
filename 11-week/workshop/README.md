# Week 11 Workshop - Observability on Multitour

<!-- CONFIG-START -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- DOCS_REPO: https://github.com/code-corhuila/multi-tour-docs
- REFLECTION_DOC: https://docs.google.com/document/d/1RoRK_CF7u-JLvGfbYgE9dpKZtU5hSNhZPeknXTHsKeI/edit?usp=sharing
- JOURNEY: A Platform Administrator inactivates a tenant and the TenantInactivated event reaches security (front, gateway, identity-audit-api, outbox, event relay, security). See 01-journey.md
<!-- CONFIG-END -->

Individual workshop of week 11 (delivery 1 in class, delivery 2 take-home). It applies observability to one
critical journey of Multitour, the multitenant tourism platform of my team.

## What I own in the project

I am the Project Lead and the owner of the gateway, `multi-tour-security`, `multi-tour-identity-audit`
(api, db and portal), the saga `multi-tour-workflow-onboard-tenant`, the root composition
`multi-tour-infra-postgres` and the Docs folders 01, 03, 04, 05, 07 and 09.

## Files

| Checkpoint | File |
|---|---|
| CP1 | `01-journey.md` |
| CP2 | `02-log-contract.md` |
| CP3 | `03-instrumentation/` |
| CP4 | `04-metrics.md` |
| CP6 | `05-slo.md` |
| CP7 | `06-alerts.yml`, `06-alert-policy.md` |
| CP8 | `07-runbook.md` |
| CP9 | `08-incident.md` |
