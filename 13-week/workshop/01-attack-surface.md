# CP1 - Attack surface of Multitour

Boundary I own: **TB2 `multi-tour-api-gateway` to `multi-tour-security`** (highlighted). Sources in
`multi-tour-docs`: `05-architecture/overview.md` (sections 2 to 4), `07-api/contracts/openapi/security.yaml`
and `06-data/models.md` ("Identity and access").

## Data flow and trust boundaries

```mermaid
flowchart TB
  subgraph Z0["Internet - untrusted"]
    PA["Platform Administrator"]
    TS["Tenant staff<br/>Tenant Administrator, Operational Collaborator,<br/>Manager, Accountant, Analyst"]
    FC["Final Customer"]
    BR["Web browser"]
  end
  subgraph Z1["Platform edge"]
    GW["multi-tour-api-gateway<br/>NGINX, single published port"]
    FRONT["multi-tour-front<br/>HTTP client and session"]
    PORT["4 portals, one container each<br/>identity-audit, commercial-reservation,<br/>operations-cost, cash-reporting"]
  end
  subgraph Z2["Internal network"]
    SEC["multi-tour-security<br/>Go, port 8086<br/>accounts, tokens, private key"]
    IDA["multi-tour-identity-audit-api<br/>Java"]
    COM["multi-tour-commercial-reservation-api<br/>Java"]
    OPS["multi-tour-operations-cost-api<br/>Java"]
    CASH["multi-tour-cash-reporting-api<br/>Java"]
    WRK["multi-tour-worker-*<br/>8 jobs, event relay included"]
    WF["multi-tour-workflow-*<br/>5 sagas, onboard-tenant included"]
    OBS["Observability stack<br/>in multi-tour-infra-postgres"]
  end
  subgraph Z3["Data zone"]
    PG[("PostgreSQL instance<br/>multi-tour-infra-postgres<br/>schema security, domain and workflow schemas")]
    MG[("MongoDB instance<br/>multi-tour-infra-mongo<br/>identity-audit database")]
  end
  subgraph Z4["External system"]
    MAIL["Email service over SMTP<br/>Mailpit in dev, Amazon SES in prod"]
  end
  NB["No message broker<br/>HTTP plus outbox, relayed by worker job 7"]

  PA & TS & FC --> BR
  BR -->|"TB1 HTTPS"| GW
  GW --> FRONT
  FRONT --- PORT
  GW ==>|"TB2 MINE: sessions, refresh, accounts, users, memberships"| SEC
  GW -->|"TB2"| IDA & COM & OPS & CASH
  SEC & COM & OPS & CASH & WF -->|"TB3 SQL"| PG
  IDA -->|"TB3"| MG
  SEC -->|"TB4 credential e-mail"| MAIL
  WRK -->|"TB4 job 4 notices"| MAIL
  WRK & WF -->|"TB5 SERVICE_TOKEN over HTTP"| SEC & IDA & COM & OPS & CASH
  SEC & IDA & COM & OPS & CASH -.->|"outbox events, read by the relay"| WRK
  SEC & IDA & COM & OPS & CASH -.->|"logs, metrics, traces"| OBS
  WRK -.- NB

  classDef mine fill:#fde68a,stroke:#b45309,stroke-width:3px
  class GW,SEC mine
```

| Boundary | Between | Who owns the analysis |
|---|---|---|
| TB1 | Internet and browser to the gateway | open |
| **TB2** | **Gateway to `multi-tour-security`** | **me (issue claimed)** |
| TB2 | Gateway to the four domain APIs | Fernanda takes gateway to `multi-tour-commercial-reservation-api` |
| TB3 | Services to their PostgreSQL schema or MongoDB database | open |
| TB4 | Services and workers to the SMTP service | open |
| TB5 | Workers and workflows to the service APIs (`SERVICE_TOKEN`) | open |

## Asset inventory (my boundary)

| Asset | Where it lives | Classification |
|---|---|---|
| RS256 private signing key | `multi-tour-security` only (ADR-004 D5) | restricted |
| `identities.passwordHash` (bcrypt, cost >= 12) | `security` schema in PostgreSQL | restricted |
| Access token (15 minutes) and refresh token (7 days, single use) | in transit gateway to security; `refresh_tokens.tokenHash` in `security` | restricted |
| Temporary credential of a new internal user (72 hours) | `security` schema, set by `POST /security/users` | restricted |
| E-mail verification link (24 hours) | in the e-mail to the Final Customer; hash in `security` | confidential |
| `identities.emailNormalized`, `memberships` and `membership_roles` | `security` schema in PostgreSQL | confidential |
| `AccountSessionsRevoked` event | outbox of `multi-tour-security` | internal |
| Tenant link identifier `/t/{identifier}` | URL given to customers | public |

## Entry points of TB2 (route, caller, authentication today)

| Route | Caller | Authentication today |
|---|---|---|
| `POST /security/public/tenants/{identifier}/sessions` | any browser | none; e-mail and password, one `401` for every refusal |
| `POST /security/public/platform/sessions` | Platform Administrator | none; e-mail and password |
| `POST /security/public/sessions/refresh` | any browser | none; opaque single-use refresh token |
| `PUT /security/sessions/password` | person with a temporary credential | Bearer, only `access: password-change` |
| `POST /security/public/tenants/{identifier}/accounts` | any visitor | none; `Idempotency-Key`, account created `PENDING` |
| `POST /security/public/tenants/{identifier}/email-verifications` | any visitor | none; always answers `202` |
| `POST .../email-verifications/confirmations` | page behind the e-mail link | link plus account password |
| `POST` and `GET /security/users` | Tenant Administrator | Bearer, role `TENANT_ADMINISTRATOR`, no access mark |
| `GET /tenants/{tenantId}/memberships`, `PATCH .../{membershipId}/status` | Platform or Tenant Administrator | Bearer, own tenant only |

Not routed by the gateway: `/security/internal/...` (tenant events, outbox, first administrator), reserved to the
`SERVICE` role and reached over TB5.

## Open question for CP2

`07-api/contracts/openapi/api-gateway.yaml` says the gateway adds `X-User-Id` and `X-User-Role` before forwarding,
while `overview.md` says each service validates the JWT by itself and the gateway only checks that a credential
exists. To check against the real gateway configuration before I score the spoofing row.
