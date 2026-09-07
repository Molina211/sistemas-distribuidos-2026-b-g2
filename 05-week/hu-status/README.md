<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       05-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 05

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Ship MVP1 (Corte 1): promote Backend and Frontend to `main`, tag `v1.0.0` with GitHub Release, verify Definition of Done with real evidence, and reflect the delivered work in the Docs repository.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 | Prepare the AI-assisted development methodology presentation (Spec, Plan, Prompt, Skills, Governance) | done | [Data/Presentation/Metodologia_IA_Spec_Plan_Prompt_Skills.pptx](./Data/Presentation/Metodologia_IA_Spec_Plan_Prompt_Skills.pptx) |
| DOC-01 | Complete the Session 1 weekly challenge (Backend + Frontend containerization: Dockerfile, .dockerignore, docker-compose.yml) | done | [WeeklySummary/WeeklyChallenge/Containerization-MVP1-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Containerization-MVP1-Travesia-Natural.md) |
| DOC-02 | Complete the Session 2 weekly challenge (Ship MVP1: promote to main, tag v1.0.0, DoD verification, retrospective) | done | [WeeklySummary/WeeklyChallenge/Ship-MVP1-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/Ship-MVP1-Travesia-Natural.md) |
| HU-RES-001..010, HU-CUST-001..002, HU-CASH-001..004, HU-CAT-001..003, HU-IAM-001..002, HU-TEN-001..003, HU-EXEC-001 | Backend: 20 of 25 formal user stories delivered (Corte 1 MVP scope) | done | Traced spec-by-spec and test-by-test in the Docs repo, `04-requirements/traceability-matrix.md` |
| HU-RES-004, HU-RES-009, HU-EXEC-002, HU-IAM-003 | Backend: remaining 4 user stories, deliberately deferred to Corte 2 | todo | Same matrix, "Identified gaps" section |
| HU-RES-006 | Self-service reservation flow (Backend logic done, Frontend wiring built then reverted the same day - teammate-authored code, not touched without her) | doing | Backend spec `027-self-service-reservation-flow` |

## 2. My individual contribution
- Shipped MVP1 (Corte 1) end to end on the Backend: PR #27 (`qa -> main`) merged as commit `a867eb4`, tag `v1.0.0` with a real GitHub Release, 27 delivered specs, 375 passing tests (`mvn test` -> `BUILD SUCCESS`).
- Verified the Frontend's own promotion to `main` (commit `06fdefd`, tag `v1.0.0`, real GitHub Release) - authored by my teammate, read-only confirmation only, no changes made to her repo.
- Verified the full Definition of Done checklist (`00-governance/definition-of-done.md`, Docs repo) against real evidence, item by item, including honestly reporting what is NOT met yet (peer PR review, concurrency/overselling tests, WCAG evidence, <=3s performance evidence, OpenAPI contract currency) instead of marking everything done.
- Reflected the entire delivered Backend scope into the Docs repository (`code-corhuila/travesia-natural-docs`), only in the folders/files where I am the last real author (`04-requirements/traceability-matrix.md`, `04-requirements/user-stories.md`, `05-architecture/overview.md`, `03-product/discovery-brief.md`, `03-product/product-backlog.md`, `03-product/roadmap.md`, `03-product/vision.md`): moved 20 of 25 HUs and 21 of 25 FRs from "Pending" to "Done", each citing the real spec and test class that closed it, and rewrote the stale "Spring Boot bootstrap, zero entities" honesty notes that had gone unupdated for 27 specs.
- Ran `/discovery` to sync `CLAUDE.md`/`ESTADO-ACTUAL.md` with reality; found and reported a documentation-drift item: `CLAUDE.md` still said HU-IAM-001/002 (registration/login) were "parked, unimplemented" when in fact both were implemented and tested since 2026-09-02/03 (specs 003/004) - a correction was proposed to the human, not applied unilaterally.
- Wrote the Week 05 Session 2 weekly challenge document (Ship MVP1: promote to main, tag v1.0.0, DoD verification, running-system demo, retrospective).

## 3. Blockers and risks
- 5 of 25 formal user stories are still open going into Corte 2: password recovery (HU-IAM-003), accommodation capacity validation (HU-RES-004), execution-lock on ordinary adjustments (HU-EXEC-002), reservation/service reschedule (HU-RES-009), and the reverted Frontend self-service wiring (HU-RES-006).
- No peer code review exists for the Backend (single-developer module) - accepted as an explicit DoD exception for this cut, not hidden.
- No concurrency/overselling test exists yet for the last-available-slot scenario (NFR-011) - a known gap, not yet closed.
- No automated performance evidence (<=3s) or WCAG 2.1 AA accessibility evidence exists yet for the applicable flows.
- The OpenAPI contract does not cover the 25 real delivered specs; the only `openapi.yaml` on file documents an earlier weekly-challenge subset (Must-have only).
- `main` in the Backend repo came within one merge of losing 191 real files during the qa->main promotion (two earlier "branch preparation" commits had emptied it); caught before confirming, fixed with an `ours`-strategy merge instead of the default resolution.
- One Backend commit (`9ce8a21`, README update describing `main` correctly) is already committed and pushed to `qa`, waiting on the final `qa -> main` merge - left for manual execution by the Project Lead, by explicit prior decision to keep that specific promotion a human action.

## 4. Plan for next week
- Start Corte 2 with the 5 open user stories identified above, prioritizing HU-IAM-003 (password recovery) and reconnecting the reverted Frontend self-service flow with the Frontend teammate.
- Close the qa->main merge for the Backend README commit (`9ce8a21`) via GitHub.
- Build out the `12-ux-ui` folder in the Docs repo to hold mockup, MVP, and discovery/PDR evidence together (scoped and described, not yet built).
- Add a concurrency/overselling test for the last-available-slot scenario.
- Bring the OpenAPI contract up to date with the 25 real delivered specs.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` (Docs repo); Backend/Frontend/Weekly follow their own declared emoji+Spanish convention instead, by project decision (`CLAUDE.md` section 5)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- [AI-assisted development methodology presentation](./Data/Presentation/Metodologia_IA_Spec_Plan_Prompt_Skills.pptx)
- [Session 1 weekly challenge - Containerization](./WeeklySummary/WeeklyChallenge/Containerization-MVP1-Travesia-Natural.md)
- [Session 2 weekly challenge - Ship MVP1](./WeeklySummary/WeeklyChallenge/Ship-MVP1-Travesia-Natural.md)
- Backend `v1.0.0`: https://github.com/Molina211/Multitour-Monolito-Api/releases/tag/v1.0.0
- Frontend `v1.0.0`: https://github.com/Molina211/Multitour-Monolito-Portal/releases/tag/v1.0.0
- Docs reflection commits: `e89321e` (requirements/architecture), `71f57f0` (product)
