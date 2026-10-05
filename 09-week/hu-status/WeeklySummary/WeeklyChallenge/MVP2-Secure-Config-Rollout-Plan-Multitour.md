# Session 2 - Secrets plan, feature-flag policy, canary and the hardening stories for MVP 2

## Objective

> Write your secrets plan (owners + rotation), a feature-flag policy (naming, owner,
> removal), and a canary + rollback plan for one MVP 2 feature. Slice the hardening stories
> with testable acceptance criteria.

This document builds on the Session 1 work ([`secure-config/`](./secure-config/README.md)).

## 0. How to read this document

- **This is a plan. Nothing in it has been built yet**, except the Session 1 service, which is
  an exercise in the Weekly repository. The 19 code repositories still hold only their README
  and `CODEOWNERS`.
- **MVP 2 starts with security and tenants, not with Operations and Costs.**
  - The week 08 plan
    ([MVP2-Planning-Story-Map](../../../../08-week/hu-status/WeeklySummary/WeeklyChallenge/MVP2-Planning-Story-Map-Multitour.md))
    put `operations-cost` first.
  - ADR-004 was revised on 2026-09-29 to build every service from scratch in dependency
    order, **`multi-tour-security` and `identity-audit` first**, because every other piece
    needs their tokens and tenants.
  - This plan follows the ADR. The feature that gets the canary is a security story
    (HU-SEC-004).
- **Feature flags have no decision in Docs yet.** `05-architecture/cross-cutting.md`
  section 6 says "Pending (no ADR yet)". The policy in section 2 is a proposal, and it has to
  be recorded in Docs before the first flag reaches code (story HRD-08).
- **The team is two people.**
  - The Project Lead owns the security microservice, the infrastructure repositories and the
    board, so they own every backend secret in the course.
  - The interface owner holds no secret: the front container is public code and keeps no
    credential.
  - On AWS, operating the system and its secrets is the company's job (PDR §19).
- **Variable names.** They come from Docs and the norm where those name them
  (`JWT_PUBLIC_KEY_FILE`, `SERVICE_TOKEN`, `SMTP_PASSWORD`, `SMTP_FROM`, `PG_DATABASE`,
  `COMPOSE_PROJECT_NAME`). Names marked *(proposed)* are not written anywhere yet.
- **Sources.** ADR-004 D3, D5 and D9, ADR-005, `05-architecture/deployment.md`,
  `04-requirements/user-stories.md` (HU-SEC-001 to 007 and HU-IDA-001 to 003, all Cut 2), the
  course norm (§5.9.2, §11.3, §13) and its Annexes C, G, I and J, all on Docs `main` at commit
  `2e852dc`.

## 1. Secrets plan

### 1.1 Inventory, owners and rotation

**Owner** creates the secret, stores it, rotates it and answers for a leak. **Holder** is the
only piece allowed to read it. "PL" is the Project Lead.

| Secret | Owner (course / AWS) | Holder | Rotation: schedule | Rotation: also on | How to rotate | Effect of rotating |
|---|---|---|---|---|---|---|
| JWT private key (`JWT_PRIVATE_KEY_FILE`, *proposed*) | PL / company | `multi-tour-security` only (ADR-004 D5) | once per cut | leak, or a person with access leaves | new pair; new public key (`JWT_PUBLIC_KEY_FILE`) to every service; redeploy (no JWKS) | every open session signs in again |
| `SERVICE_TOKEN` | PL / company | worker and workflow only | before day 90 (ADR-004 D5) | leak | security issues a new one, environment updated, worker and workflow redeployed, old `sub` revoked | none if done before expiry |
| PostgreSQL superuser password (`POSTGRES_PASSWORD`, *proposed*) | PL / company | `infra-postgres` only | once per cut | leak | `ALTER ROLE`, update `env/<env>.env`, restart the instance | only the migrator and the init script use it |
| `<domain>_app` passwords, e.g. `security_app` (*proposed:* `PG_APP_PASSWORD`) | PL / company | the service of that domain | once per cut | leak | `ALTER ROLE … PASSWORD`, update `env/<env>.env`, restart that service | that service restarts |
| MongoDB root and `identity_audit_app` credentials (*names proposed*) | PL / company | `infra-mongo`; `identity-audit-api` | once per cut | leak | `db.updateUser`, update the env file, restart | that service restarts |
| `SMTP_USER`, `SMTP_PASSWORD` | not used in the course (Mailpit) / company (SES) | security, worker job 4 | provider's policy | leak | issued again by the provider | e-mail only |
| Gateway TLS certificate and key | company (`qa`/`main` host, AWS) | gateway | before expiry (ACM or Let's Encrypt renew on their own) | leak | new certificate, gateway reload | none |
| `PROJECT_TOKEN_CODE_CORHUILA` (organization secret for `env-tracking.yml`) | PL | GitHub Actions | the token's own expiry | leak | new fine-grained token, organization secret updated by hand | board automation only |

"Once per cut" means at the start of each course cut. The schedule is set by the course
rhythm, not by an outside rule. Every rotation and its date are recorded in the release PR
of that cut.

### 1.2 Where each secret lives

| Environment | Where | How it reaches the service |
|---|---|---|
| `develop` (laptop) | `scripts/dev-keys.sh` and `dev-token.sh` (Annex G) write to git-ignored `keys/` and `env/dev.env` | `NAME_FILE` pointing at `keys/`, or `env/dev.env` |
| `qa`, `main` (course host) | `env/qa.env` and `env/main.env` on the host only, readable only by the user that runs Compose; keys and the `SERVICE_TOKEN` are secrets of the environment (§5.9.2) | Compose `secrets:` mounted as files under `/run/secrets/`, read through `NAME_FILE` |
| AWS (the company) | AWS's secrets store (`deployment.md` §3) | same `NAME_FILE` contract, filled by the host before Compose starts, no code change |
| GitHub Actions | only `PROJECT_TOKEN_CODE_CORHUILA`; CI tests generate a throwaway key pair and need no real secret | organization secret, set by hand |

### 1.3 Rules

1. **A secret is never versioned.** `.gitignore` covers `.env`, `env/*.env`, `*.pem`,
   `keys/` and `secrets/`. Only `.example` files are committed, with placeholders.
2. **A secret arrives as a value or as a file** (`NAME` or `NAME_FILE`), as in Session 1.
3. **A secret is never printed.** Error messages name the variable, never its value.
   Session 1 has a test for this.
4. **Scanned twice.**
   - A gitleaks pre-commit hook in every clone.
   - A gitleaks job in `ci.yml`, run as a binary at a fixed version, because
     `gitleaks-action` asks for a license on organization accounts.
   - CI is the gate, since `git commit --no-verify` skips the hook.
5. **On a leak:**
   1. Rotate at once (§13).
   2. Remove the secret in a new commit.
   3. Record it in the PR.
   4. **Never rewrite published history**: that is a separate serious fault (§13). A pushed
      secret is public, whatever happens to the commit.

## 2. Feature-flag policy

1. **Naming.**
   - `FEATURE_<CAPABILITY>` in upper snake case. `<CAPABILITY>` is the noun of what it turns
     on, taken from the story title: `FEATURE_PASSWORD_RECOVERY`, not `FEATURE_NEW_FLOW` or
     `ENABLE_X`.
   - A tenant cohort for the same flag is `FEATURE_<CAPABILITY>_TENANTS`.
   - One flag guards one story. A story spread over two services uses the same name in both.
2. **Values.**
   - `true` or `false`, default `false`. Any other value stops the service at startup.
   - `_TENANTS` is either `all` or a comma-separated list of tenant identifiers (the
     `/t/{identifier}` of ADR-004 D5). It is required when the flag is `true`: an empty list
     is a startup error, never "everyone".
3. **Owner.**
   - Every flag has one named owner, the owner of the story it guards. For MVP 2 that is the
     Project Lead.
   - The owner is the only person who changes the flag in `qa` and `main`.
4. **Registry.** Each repository README has a "Feature flags" table: name, story, owner, date
   added, values per environment, and removal condition. A flag missing from the table fails
   review.
5. **What a flag may not do.**
   - Turn off authentication, token validation, tenant isolation, or any check the norm
     requires.
   - Hold configuration or a secret.
   - Change data on its own: switching a flag never runs a migration.
6. **Behaviour when off.** The route is not registered and answers `404`, so it is absent,
   not half-working. A tenant outside the cohort sees exactly the same `404`.
7. **Tests.** Every flagged capability is tested in three states: off, on for the tenant,
   and on but not for the tenant.
8. **Removal.**
   - A flag is removed **at most two releases after it reaches `all` in `main`**, by a
     `chore/` PR that deletes the flag, its registry row and the code of the off path.
   - Each release PR lists the live flags and their age.
   - A flag past its limit blocks the next release until it is removed or its owner writes
     why it stays.
9. **Limit.** At most **3 live flags per service**. A fourth waits until one is removed.

**MVP 2 flags under this policy:**

| Flag | Story | Owner | `develop` | `qa` | `main` at release | Removal condition |
|---|---|---|---|---|---|---|
| `FEATURE_PASSWORD_RECOVERY` (+`_TENANTS`) | HU-SEC-004 | PL | on, `all` | on, `all` | off, then the canary of section 3 | two releases after `all` in `main` |
| `FEATURE_SELF_REGISTRATION` (+`_TENANTS`) | HU-SEC-002, HU-SEC-003 | PL | on, `all` | on, `all` | off until SMTP is configured | two releases after `all` |
| `FEATURE_EMERGENCY_ACCESS` | HU-SEC-007 | PL | on | on | off until HU-IDA-002 (audit) is in `main` | two releases after on in `main` |

Session revocation (ADR-004 D5) is not flagged. It is a security check, so rule 5 applies:
until worker job 7 exists, the 15-minute token is the bound that ADR-004 D5 already
accepts.

## 3. Canary and rollback: password recovery (HU-SEC-004)

### 3.1 Why this feature and this method

- **Why this feature.**
  - Password recovery handles credentials and depends on e-mail, so a defect shows quickly:
    codes that do not arrive, or `5xx` errors.
  - Turning it off loses no data: a pending code just expires in 30 minutes (ADR-004 D5).
- **Why a canary by tenant.**
  - There is one instance per environment on one host (ADR-004 D9), so there is no second
    copy to send a share of the traffic to.
  - The product is multitenant, and every recovery request already names its tenant: before
    login, public endpoints take the identifier in the path (ADR-004 D5).
  - So the canary cohort is a list of tenants, and widening it is an edit to `env/main.env`
    and a restart of one service.
- **Not chosen:** a traffic split in the gateway (NGINX `split_clients` to a second
  container). It needs a duplicate service in Compose, and one person could land on two
  versions in the same recovery.

### 3.2 Stages

| Stage | `FEATURE_PASSWORD_RECOVERY_TENANTS` in `main` | Minimum stay | Exercised by |
|---|---|---|---|
| 0. Dark | flag `false` | until the release is deployed and `/health` is up | nobody |
| 1. Demo tenant | `travesia-natural` | 24 hours and at least 5 complete recoveries | the team, with test accounts |
| 2. Second tenant | `travesia-natural,<second-tenant>` | 72 hours | real users of that tenant |
| 3. Everyone | `all` | the flag is then removed under policy rule 8 | everyone |

Before stage 1, the same feature runs in `qa` with `all`, and its acceptance criteria
(HU-SEC-004) pass there.

### 3.3 Measures that decide go or no-go

They are read from the observability stack (ADR-004 D8: Prometheus, Loki, Grafana) and from
Mailpit or SES:

| Signal | Go | Stop and roll back |
|---|---|---|
| `5xx` answers on the recovery endpoints, per tenant | 0 in the stage | any `5xx` caused by recovery |
| E-mail delivery: codes requested vs. e-mails delivered | every code delivered within 5 minutes | one code missing or later than 5 minutes |
| Recoveries completed vs. started | every test recovery completes | a valid code is rejected |
| Tenant isolation: a recovery in tenant A changes only the account of A (PDR §9) | the isolation test passes in `qa` and at stage 1 | any change in another tenant's account (stops everything, see 3.4) |
| A recovery code or a password in a log line | none (a Loki search for the code format returns nothing) | any occurrence: roll back and treat it as a leak (section 1.3, rule 5) |
| Sessions after a reset | earlier sessions get `401` (`AccountSessionsRevoked`, ADR-004 D5) | an old session still works after its expected bound |

### 3.4 Rollback

| Level | When | Action | Time | Data |
|---|---|---|---|---|
| 1. Narrow the cohort | a stop signal at stage 2 or 3 | set `_TENANTS` back to the previous stage's list and restart `multi-tour-security` | seconds | pending codes of the removed tenants can no longer be used (`404`) and expire in 30 minutes; passwords already changed stay changed, which is correct |
| 2. Switch off | a stop signal at stage 1, or any isolation or leak signal | set `FEATURE_PASSWORD_RECOVERY=false` and restart | seconds | same as level 1, for every tenant |
| 3. Fix the code | the defect cannot wait for the next release | `hotfix/` from `main`, professor's approval, then re-applied to `qa` and `develop` with `cherry-pick -x` | hours | the flag stays off until the fix is in `main` |
| 4. Data | recovery corrupted account data (not expected) | restore the backup taken before the release's migration (J.5.4); never `docker compose down -v` | under 8 hours (ADR-004 D9) | loses what happened after the backup, within the RPO |

The table that stores recovery codes is created by an **expand-only** migration: the
previous version ignores it, so levels 1 to 3 never need a schema rollback.

**Who decides.** The flag's owner. A roll back is done first and explained afterwards, in a
comment on the story's issue with the signal that triggered it and the time. Widening to the
next stage needs the owner to record the measures of 3.3 in the same issue.

## 4. Hardening stories

The stories are sliced to fit one PR each (under 400 lines, §9.2). They are estimated with
the week 08 scale and reference story (HU-IAM-002 = 3 points).

- **IDs.** They are local to this challenge (`HRD-NN`). When they move to the board they take
  the `HU-<SERVICE>-NNN` convention of `04-requirements/user-stories.md`.
- **Format.** Every criterion is written so the test can be written before the code.

### HRD-01 - Required variables in the infrastructure (3 pts)

*As the Project Lead, I want the platform to refuse to start with a missing variable, so that
no environment runs with an empty password.* Repositories: `multi-tour-infra-postgres`,
`multi-tour-infra-mongo`.

- **Given** `env/dev.env` without the PostgreSQL superuser password, **when** `docker compose up`
  runs, **then** it exits with a non-zero code, the message names the variable, and no
  container is created.
- **Given** a fresh clone, **when** `git ls-files 'env/*'` runs, **then** it lists only
  `dev.env.example`, `qa.env.example` and `main.env.example`.
- **Given** a file `env/qa.env` in the working tree, **when** `git status --porcelain` runs,
  **then** the file is not listed (it is ignored).

### HRD-02 - Secret scan before commit and in CI (3 pts)

*As the Project Lead, I want every commit and every PR scanned for secrets, so that a secret
never reaches a remote.* Repositories: each MVP 2 repository, starting with the two
infrastructure ones and `multi-tour-security`.

- **Given** a staged file with a fake AWS key, **when** `git commit` runs with the hook
  enabled, **then** the commit is refused with exit code 1, and the finding is redacted.
- **Given** a PR that adds the same fake key, **when** `ci.yml` runs, **then** the gitleaks
  job fails and the PR cannot merge.
- **Given** a PR with no secret, **when** `ci.yml` runs, **then** the gitleaks job passes, using
  a gitleaks version pinned in the workflow, never `latest`.
- **Given** gitleaks is not installed, **when** `git commit` runs, **then** the hook refuses
  the commit and says how to install it.

### HRD-03 - Startup validation in the security microservice (3 pts)

*As the security microservice owner, I want the service to check its configuration before it
serves, so that a bad deploy fails at once and says why.* Repository: `multi-tour-security`
(Go). Refs HU-SEC-001.

- **Given** none of the required variables, **when** the service starts, **then** it exits with
  code 1 and names every missing variable in one message.
- **Given** `JWT_PRIVATE_KEY_FILE` pointing at a missing file, **when** the service starts,
  **then** it exits with code 1 and the message contains the variable name but not the path's
  contents.
- **Given** a secret supplied as `NAME_FILE`, **when** the service starts, **then** it uses the
  file's content, trimmed.
- **Given** any configuration error, **when** the message is printed, **then** no secret value
  appears in it, and a test covers this.

### HRD-04 - Startup validation in `identity-audit-api` (2 pts)

*Same goal, Java.* Repository: `multi-tour-identity-audit-api`. Refs HU-IDA-001.

- **Given** the MongoDB connection variable is missing, **when** the application starts, **then**
  the Spring context fails before the HTTP port opens and the log names the property.
- **Given** `JWT_PUBLIC_KEY_FILE` is missing, **when** it starts, **then** it fails the same way.

### HRD-05 - Feature flags with a tenant cohort (5 pts)

*As the flag owner, I want to turn a capability on for chosen tenants only, so that a new
feature reaches one tenant before all of them.* Repository: `multi-tour-security`. Refs
HU-SEC-004.

- **Given** `FEATURE_PASSWORD_RECOVERY=false`, **when** a recovery request arrives for any
  tenant, **then** the answer is `404`.
- **Given** the flag `true` and `_TENANTS=travesia-natural`, **when** a request arrives for
  `travesia-natural`, **then** it is processed; **when** it arrives for another tenant, **then**
  the answer is `404`, the same body as flag off.
- **Given** the flag `true` and `_TENANTS` empty, **when** the service starts, **then** it exits
  with code 1 naming `FEATURE_PASSWORD_RECOVERY_TENANTS`.
- **Given** `_TENANTS=all`, **when** a request arrives for any active tenant, **then** it is
  processed.
- **Given** the flag `ture` (malformed), **when** the service starts, **then** it exits with code 1.

### HRD-06 - Canary signals for password recovery (3 pts)

*As the flag owner, I want the measures of section 3.3 per tenant, so that each canary stage
is decided on data.* Repository: `multi-tour-security`. Refs HU-SEC-004.

- **Given** a recovery request, **when** it ends, **then** a counter labelled with the tenant
  and the outcome (`requested`, `completed`, `rejected`, `error`) increases by one.
- **Given** any recovery request, **when** its log lines are written, **then** none contains the
  code, the new password, or the e-mail in full, and a test searches the captured log for
  them.
- **Given** a recovery started in tenant A, **when** it completes, **then** no account of
  another tenant has changed (an isolation test with two tenants and the same e-mail).

### HRD-07 - Service token expiry guard (2 pts)

*As the Project Lead, I want the worker and the workflow to warn before their token expires,
so that the 90-day `SERVICE_TOKEN` is never found expired.* Repositories: `multi-tour-worker`,
`multi-tour-workflow`.

- **Given** a `SERVICE_TOKEN` that expires in 10 days, **when** the service starts, **then** it
  starts and logs one warning with the expiry date, not the token.
- **Given** an expired `SERVICE_TOKEN`, **when** the service starts, **then** it exits with
  code 1 naming the variable.

### HRD-08 - Record the configuration and flag decisions in Docs (2 pts)

*As the Project Lead, I want the secrets plan and the flag policy in the architecture
documents, so that the code follows a recorded decision.* Repository: `multi-tour-docs`
(folder 05).

- **Given** `05-architecture/cross-cutting.md` section 6, **when** the PR merges, **then** it no
  longer says "pending". It states the flag naming, the owner, the removal rule and the
  limit of section 2, and links the ADR that records them.
- **Given** the ADR, **when** it is read, **then** it has at least two real alternatives (for
  example, environment flags versus a flag service), the dominant criterion and the accepted
  cost (norm §4.2.3).

**Order:**

1. HRD-08 (the decision comes first).
2. HRD-01 and HRD-02 (stage 0, before any service).
3. HRD-03 and HRD-04 (with the first commit of each service).
4. HRD-05 and HRD-06 (before HU-SEC-004 reaches `qa`).
5. HRD-07 (with the first worker job).

Total: 23 points, under two sprints at the week 08 rate of about 14 points per sprint.

## 5. Path to the MVP 2 release

Promotion follows the norm only:

- A feature reaches `develop` through a `feat/` PR.
- It reaches `qa` by `cherry-pick -x` in a `qa/` branch.
- It reaches `main` through a `release/x.y.z` approved by the professor.
- There is no merge between permanent branches.

The course's next step is persistence, and then the MVP 2 release. So the hardening stories
go first, since every repository needs them from its first commit:

- **Before persistence:** stage 0 (HRD-08, HRD-01, HRD-02) lands, so the first migrations
  of `security` and `identity-audit` already run with required variables and scanned
  commits.
- **The MVP 2 release** ships HU-SEC-004 dark, with the flag off. The canary of section 3
  starts after the release, with no new release, by editing `env/main.env`.
- **The release PR** lists:
  - every new variable and secret (created on the host before deploying);
  - the live flags with their age;
  - the next `SERVICE_TOKEN` expiry date;
  - a gitleaks result for the release range.

## 6. Risks

| Risk | Likelihood | Impact | Response |
|---|---|---|---|
| A secret is pushed before CI has the gitleaks job | Medium (stage 0 not done) | High | Hook active in every clone from the first commit; rotate on any finding; never rewrite history |
| Flags pile up and both paths have to be kept | Medium | Medium | Limit of 3 per service; removal two releases after `all`; the release is blocked by an overdue flag |
| The canary's second tenant does not exist yet in `main` | High (only the demo tenant is planned) | Low | Stage 2 is skipped and recorded; stage 1 then lasts 72 hours before `all` |
| Low volume makes the canary measures weak | High | Medium | Stage 1 is driven by the team with test accounts and a minimum of 5 complete recoveries |
| The JWT private key or the `SERVICE_TOKEN` leaks | Low | High | Single holder; rotation steps in 1.1; HRD-07 warns before expiry |
