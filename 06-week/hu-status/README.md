<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       06-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 06

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Close the Weekly Challenge Semana 06 end to end (Session 1: real Docker healthcheck gating startup; Session 2: environment/orchestration planning for MVP2), and reflect the MVP1 baseline (ADR-002, v1.0.0) in the Docs repo.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-03 | Reflect ADR-002 backend stack resolution and the MVP1 `v1.0.0` release status in Docs `01-context/overview.md` and `01-context/scope.md` | done | commit `3f3d946` (Docs repo) |
| WCH-01 | Weekly Challenge Semana 06, Session 1: bring the whole system up with a single `docker compose up` (shared network, health checks gating startup, config via env, data in volumes) | done | Backend spec `028-actuator-health-endpoint`, PR #28; [WeeklySummary/WeeklyChallenge/Healthcheck-Docker-MVP1-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Healthcheck-Docker-MVP1-Travesia-Natural.md) |
| BRANCH-01 | Backend: found and fixed `develop` being 26 commits behind `main`/`qa` (the `develop -> qa -> main` promotion never merges back down) and a diverging per-branch README causing merge conflicts; synced and promoted through the full flow | done | Backend PR #29 (sync), #30 (README unification), #31 (`develop -> qa`), #32 (`qa -> main`) |
| WCH-02 | Weekly Challenge Semana 06, Session 2: define the three environments, a config matrix, confirm branch-environment mapping, slice orchestration stories for MVP2 | done | [WeeklySummary/WeeklyChallenge/Orchestration-Plan-MVP2-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Orchestration-Plan-MVP2-Travesia-Natural.md) |
| TECH-DEBT-01 | Backend: review/fix base64 image storage - the column is `VARCHAR(500)` and truncates images that exceed that encoded size once decoded; define a better storage approach | todo | `.claude/bitacora/semana-06.md` |
| TECH-DEBT-02 | Backend: review other forms for similar truncation/data-loss patterns | todo | Same bitácora |
| DOC-04 | Verify whether domain definitions are already covered in Docs `02-domain/`, or whether something specific is still missing | todo | Same bitácora |

## 2. My individual contribution
- Reflected the resolved Backend stack decision (ADR-002) and the MVP1 `v1.0.0` release status into the Docs repository's `01-context` folder (commit `3f3d946`), keeping the project context current for anyone reading Docs without having seen the delivery sessions.
- Closed the Weekly Challenge Semana 06, Session 1 requirement: added Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `show-details=never`) to the Backend, gave the `backend` service in `docker-compose.yml` a real healthcheck against it, and changed the `frontend` service's `depends_on` to `condition: service_healthy` so it only starts once the Backend is actually ready. Shared network, env-based config, and volume-backed Postgres data were already in place from Semana 05. Verified end to end with `docker compose up -d --build` (`PLAN-VERIFICACION.md` section 028, 2026-09-13).
- Found and fixed a real gap in the Backend's own branch flow while closing that out: `develop` was 26 commits behind `main`/`qa` (the Mockito/domain unit-test work that had landed in `qa`/`main` via PR #27 was missing from `develop`, because the promotion flow never merges back down). Synced it (PR #29), then unified the per-branch README (which had been diverging and causing merge conflicts on every cross-branch merge) into a single branch-agnostic file (PR #30), and promoted the whole thing through `develop -> qa -> main` (PR #31, #32).
- Marked the corresponding checklist item in the HU-04 backlog issue (`Molina211/Multitour-Monolito-Api` issue #5) as done, reflecting the real state after the Actuator/healthcheck work landed.
- Closed the Weekly Challenge Semana 06, Session 2 requirement: documented the three environments (dev/qa/prod) mapped to `develop`/`qa`/`main`, a configuration matrix with the Backend's real environment variables (`POSTGRES_*`, `APP_JWT_SECRET`, `SPRING_DATASOURCE_*`), confirmed the branch-environment mapping, and sliced four orchestration backlog stories for MVP2 (`ORCH-01` to `ORCH-04`) with verifiable acceptance criteria - planning only, nothing implemented yet.

## 3. Blockers and risks
- Corte 2 has not formally started yet; the only Backend commits this week are the Weekly Challenge Session 1 infra work (spec 028) and the branch-sync/README cleanup, not a new product HU.
- The base64 image storage issue (`VARCHAR(500)` truncation) is identified but not yet fixed - real data-loss risk for any image upload that exceeds the encoded size limit.
- Password recovery (HU-IAM-003) remains the one formally identified open user story from Corte 1, still without a retake date.
- QA and production environments (documented in `Orchestration-Plan-MVP2-Travesia-Natural.md`) exist only as git branches today - neither is actually deployed anywhere, so the config matrix's qa/prod columns are a plan, not a running system.

## 4. Plan for next week
- Fix the base64 image storage truncation and review other forms for the same pattern (bitácora `semana-06`).
- Close the open question on whether Docs `02-domain/` already covers domain definitions or needs an addition.
- Start Corte 2 work, prioritizing HU-IAM-003 (password recovery), each new task opening its `hu-back-{N}-dev` branch per the three-branch convention.
- Start on the ORCH-01..04 orchestration backlog for MVP2 once Corte 2 work is under way, beginning with per-environment `.env` examples (`ORCH-01`).

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` (Docs repo); Backend/Frontend/Weekly follow their own declared emoji+Spanish convention instead, by project decision (`CLAUDE.md` section 5)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - no new product HU branch was opened this week (spec 028 and the branch-sync work used the `chore/` convention, for infra/tooling work, per `git-conventions.md`)
- [x] Testable acceptance criteria - spec 028's 6 acceptance criteria are checklist-verifiable, all confirmed manually (`PLAN-VERIFICACION.md` section 028)
- [ ] Tests added/updated (unit / integration) - no new automated test; spec 028 is infra (Actuator/Docker) verified manually (`curl` + `docker compose up`), not unit-testable
- [x] DDD / hexagonal boundaries respected (domain has no I/O) - unchanged from Corte 1, no code touched this week
- [x] No secrets; config via environment variables - unchanged from Corte 1, and confirmed as the standard for qa/prod too in the Session 2 planning doc

## 6. Evidence links
- Weekly Challenge Semana 06, Session 1 writeup: [WeeklySummary/WeeklyChallenge/Healthcheck-Docker-MVP1-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Healthcheck-Docker-MVP1-Travesia-Natural.md)
- Weekly Challenge Semana 06, Session 2 writeup: [WeeklySummary/WeeklyChallenge/Orchestration-Plan-MVP2-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Orchestration-Plan-MVP2-Travesia-Natural.md)
- Docs context sync: commit `3f3d946` (`01-context/overview.md`, `01-context/scope.md`)
- Backend PR #28 (Actuator healthcheck): https://github.com/Molina211/Multitour-Monolito-Api/pull/28
- Backend PR #29 (sync `develop` with `main`): https://github.com/Molina211/Multitour-Monolito-Api/pull/29
- Backend PR #30 (README unification across branches): https://github.com/Molina211/Multitour-Monolito-Api/pull/30
- Backend PR #31 (`develop -> qa`) and #32 (`qa -> main`): https://github.com/Molina211/Multitour-Monolito-Api/pull/31, https://github.com/Molina211/Multitour-Monolito-Api/pull/32
- HU-04 backlog issue, healthcheck item marked done: https://github.com/Molina211/Multitour-Monolito-Api/issues/5
