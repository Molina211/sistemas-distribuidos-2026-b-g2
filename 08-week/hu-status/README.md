<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       08-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 08

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Documentation-only week to prepare MVP 2: sync the PDR to v1.8 and v1.9, model four more BPMN processes (BPMN-07 to BPMN-10), close the API contract decisions of the microservices, record the architecture decisions ADR-004 (revised), ADR-005 and ADR-006, and set up a board with a prioritized backlog for the sprint (Weekly Challenge).
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). Docs (`code-corhuila/multi-tour-docs`) only accepts changes through a branch and a pull request, so every PR below is the evidence of its row. The last rows are still to do.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-12 | Update the PDR to v1.8 (macrodomains, password recovery flow) | done | commit `dffbcb4` (2026-09-21); Docs PR #15, merged 2026-09-21: https://github.com/code-corhuila/multi-tour-docs/pull/15 |
| DOC-13 | Fix stale PDR version references in `01-context` and mark the affected requirements as pending v1.8 | done | commit `1f29380` (2026-09-21); Docs PR #17, merged 2026-09-21: https://github.com/code-corhuila/multi-tour-docs/pull/17 |
| BPMN-07 | BPMN process: register and query operational costs | done | commit `e4d3e88` (2026-09-23); Docs PR #24, merged 2026-09-23: https://github.com/code-corhuila/multi-tour-docs/pull/24 |
| BPMN-08 | BPMN process: operate and close the daily cash register (first PR #26 closed without merge and replaced by #31; review findings fixed in #43) | done | commits `4754afe` (2026-09-23), `3896689` (2026-09-24), `c863b2a` (2026-09-25); Docs PRs #31 (merged 2026-09-24) and #43 (merged 2026-09-25): https://github.com/code-corhuila/multi-tour-docs/pull/31 |
| API-01 | Close the API contract decisions of the reservations, operations-costs and cash-reports services (declared gaps, English translation, port conflict fix) | done | commits `b9fd69a`, `2f43593`, `ead5c42`, `f7216f6`, `47a0229`, `93b2fac` (all 2026-09-24); Docs PRs #32 (merged 2026-09-24), #35 (merged 2026-09-24), #40 (merged 2026-09-24): https://github.com/code-corhuila/multi-tour-docs/pull/32 |
| BPMN-09 | BPMN process: administer tenants (plus a follow-up clarifying which parts are implemented) | done | commits `007e2e7`, `568899c`, `d6cb749` (2026-09-25); Docs PRs #44 and #45, merged 2026-09-25: https://github.com/code-corhuila/multi-tour-docs/pull/44 |
| BPMN-10 | BPMN process: register and query operational collaborators | done | commits `a373356`, `f9af9aa` (2026-09-25); Docs PR #46, merged 2026-09-25: https://github.com/code-corhuila/multi-tour-docs/pull/46 |
| DOC-14 | Sync the PDR to v1.9 as the final functional baseline | done | commit `499eaab` (2026-09-27); Docs PR #50, merged 2026-09-27: https://github.com/code-corhuila/multi-tour-docs/pull/50 |
| ADR-01 | Revise ADR-004 with the real repositories and languages; add ADR-005 (database engines and saga state) and ADR-006 (frontend frameworks and micro frontends); align ADR-005 with one engine per domain | done | commits `ba647b2`, `dee2438`, `34c6324`, `44997b6` (2026-09-27); Docs PRs #52, #53 (merged 2026-09-27) and #54 (merged 2026-09-27): https://github.com/code-corhuila/multi-tour-docs/pull/52 |
| WCH-01 | Weekly Challenge Semana 08, Session 1: prioritized backlog of 10 stories with Gherkin acceptance criteria on a board, PR history, throughput measurement | done | [WeeklySummary/WeeklyChallenge/Sprint-Execution-Backlog-Multitour.md](./WeeklySummary/WeeklyChallenge/Sprint-Execution-Backlog-Multitour.md) (2026-09-27); board https://github.com/users/Molina211/projects/3 |
| WCH-02 | Weekly Challenge Semana 08, Session 1: WIP limit and daily sync | doing | Same file, sections 3 and 5: the policy is defined (at most 2 stories in doing; a written daily note), but neither was tracked or practiced during week 08 |
| DOC-15 | Align the Docs overview, API docs, requirements and worker job list with ADR-004 to ADR-006 (issue #10 of the board) | doing | commits `8ae7467` to `4cb0849` (2026-09-27); Docs PRs #55, #56 and #57, all still open on 2026-09-27: https://github.com/code-corhuila/multi-tour-docs/pull/56 |
| WCH-03 | Weekly Challenge Semana 08, Session 2 (planning: story mapping, estimation and MVP 2 commitment) | todo | [PENDIENTE: confirm the Session 2 challenge statement and whether any part was done; the only related material found is the class summary image in section 6] |
| STORY-01 | The five product stories of the sprint backlog (HU-RES-006, HU-RES-004, HU-EXEC-002, HU-IAM-003, HU-RES-009) | todo | Board issues #1 to #5, all `status:todo`: https://github.com/Molina211/Travesia-Natural-docs/issues/1 |

## 2. My individual contribution
Chronological, local time (UTC-5).
- **2026-09-21.** Updated the PDR to v1.8 with the four macrodomains of ADR-004 and the password recovery flow, then fixed the stale version references in `01-context` and marked the affected requirements as pending v1.8 (PRs #15 and #17, both merged the same day).
- **2026-09-23.** Modeled BPMN-07 (register and query operational costs; PR #24) and started BPMN-08 (operate and close the daily cash register), whose first PR #26 was closed without merge and replaced by #31.
- **2026-09-24.** Closed the API contract decisions for three of the microservices (reservations, operations-costs, cash-reports), declaring the real gaps of each contract, then translated the contracts to English and fixed a port conflict (PRs #32, #35, #40). Merged BPMN-08 (PR #31).
- **2026-09-25.** Applied the course review of BPMN-08 (PR #43), then modeled BPMN-09 (administer tenants; PRs #44 and #45) and BPMN-10 (register and query operational collaborators; PR #46).
- **2026-09-27.** Synced the PDR to v1.9 as the final functional baseline (PR #50). Revised ADR-004 with the real repositories and languages, and wrote ADR-005 (database engines and saga state) and ADR-006 (frontend frameworks and micro frontends), later aligned to one engine per domain (PRs #52, #53, #54). Opened three more PRs aligning the overview, the API docs and the worker job list with those ADRs (PRs #55, #56, #57), still open at the time of writing.
- **2026-09-27.** Wrote the Weekly Challenge of Session 1: a board (repository issues, project and milestone "Sprint 08"), a prioritized backlog of 10 stories with Gherkin acceptance criteria, and the throughput of the week measured from the PRs: 16 merged in 7 days, median cycle time 1.2 h. The board was created on that day, so the week's stories were entered retroactively and are marked as such.

Not mine, so not counted here: the material in `02-session/` (specification, ADRs and diagrams of the class exercise, published by the teacher) and the Docs PRs authored by a teammate this week (governance, data model, C4 diagrams; for example #14, #21, #28 and #51).

## 3. Blockers and risks
- Week 08 ran without a sprint process: no board, no backlog, no daily sync and no WIP limit while the work was being done. The only synchronization points were the two class sessions. The Session 1 document says so and reconstructs the week from the PR history instead of claiming otherwise.
- Three PRs (#55, #56, #57) were opened on Sunday evening and are waiting for the teacher's review, so the story that groups them stays in `doing`.
- No product code this week: the five product stories are all `todo`, and the Backend has no commit since 2026-09-16 (spec 029, merged on `main`). The week 07 item of the `operations-costs-service` scaffold (MS-01) was not advanced.
- The Session 2 challenge (planning) has no deliverable yet, so the stories have no story points and the MVP 2 commitment is not made from a real velocity.
- The course repository norm and its annexes A to I were added to `Data/Document/` on 2026-09-26. Part of the microservice work (database configuration per repository, stories per microservice) was waiting for the teacher's instructions; whether this norm is that file is not confirmed. [PENDIENTE: confirm]
- Story #10 and the retroactive board mean the throughput numbers describe documentation work only. They are not a velocity.

## 4. Plan for next week
In order of dependency.
- Get the teacher's review of PRs #55, #56 and #57 and merge them, which closes story #10.
- Do the Session 2 planning: story map, planning poker estimates for the backlog and the MVP 2 scope committed from a realistic velocity. [PENDIENTE: confirm what the Session 2 challenge requires]
- Run Sprint 09 with the process defined in Session 1: board open from day 1, at most 2 stories in `doing`, a written daily note on a pinned issue.
- Draft the user stories each microservice needs and document the database configuration of each service repository (documentation only), once the teacher's instructions are confirmed.
- Continue the BPMN set from the inventory in `Data/Document/INVENTARIO-BPMN-MULTI-TOUR.pdf`. [PENDIENTE: which diagram is next]
- Carry over from week 07: DOC-11 (the remaining "Multitour" normalization; teammate's PRs #14 and #19 touched those sections, not re-verified here) and moving the `operations-costs-service` database password to an environment variable before pushing it.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - all Docs commits and PR titles this week follow it (`docs(bpmn)`, `docs(api)`, `docs(product)`, `docs(architecture)`, `docs(07-api)`, `docs(context)`, `docs(requirements)`)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - no code repository was touched; every Docs change went through a branch and a PR into `main` (Docs has a single permanent branch), 20 PRs in total: 16 merged, 1 closed without merge (#26), 3 open
- [x] Testable acceptance criteria - the 10 stories of the board carry Given/When/Then scenarios (two each), e.g. issue #2: https://github.com/Molina211/Travesia-Natural-docs/issues/2
- [ ] Tests added/updated (unit / integration) - documentation-only week, no tests added or changed
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) - not applicable: no code was changed this week
- [x] No secrets; config via environment variables - documentation only; no code or configuration was added

## 6. Evidence links
- Class summary image for week 08 (Session 2: planning, story mapping, estimation and MVP 2 commitment): [Data/Image/ChatGPT Image 26 sept 2026, 10_38_02 a.m..png](<./Data/Image/ChatGPT Image 26 sept 2026, 10_38_02 a.m..png>). [PENDIENTE: the summary image of Session 1, and whether this one moves to `WeeklySummary/Week-08.png`]
- Weekly Challenge Semana 08, Session 1 writeup: [WeeklySummary/WeeklyChallenge/Sprint-Execution-Backlog-Multitour.md](./WeeklySummary/WeeklyChallenge/Sprint-Execution-Backlog-Multitour.md)
- Board: repository https://github.com/Molina211/Travesia-Natural-docs, project https://github.com/users/Molina211/projects/3, milestone https://github.com/Molina211/Travesia-Natural-docs/milestone/1
- Course norm and annexes A to I (teacher's material): [Data/Document/](./Data/Document/)
- BPMN inventory: [Data/Document/INVENTARIO-BPMN-MULTI-TOUR.pdf](./Data/Document/INVENTARIO-BPMN-MULTI-TOUR.pdf)
- Docs PR #15 (PDR v1.8, merged 2026-09-21): https://github.com/code-corhuila/multi-tour-docs/pull/15
- Docs PR #17 (PDR version references, merged 2026-09-21): https://github.com/code-corhuila/multi-tour-docs/pull/17
- Docs PR #24 (BPMN-07, merged 2026-09-23): https://github.com/code-corhuila/multi-tour-docs/pull/24
- Docs PR #31 (BPMN-08, merged 2026-09-24): https://github.com/code-corhuila/multi-tour-docs/pull/31
- Docs PR #32 (reservations contract, merged 2026-09-24): https://github.com/code-corhuila/multi-tour-docs/pull/32
- Docs PR #35 (cash-reports contract, merged 2026-09-24): https://github.com/code-corhuila/multi-tour-docs/pull/35
- Docs PR #40 (contracts in English, merged 2026-09-24): https://github.com/code-corhuila/multi-tour-docs/pull/40
- Docs PR #43 (BPMN-08 review findings, merged 2026-09-25): https://github.com/code-corhuila/multi-tour-docs/pull/43
- Docs PR #44 (BPMN-09, merged 2026-09-25): https://github.com/code-corhuila/multi-tour-docs/pull/44
- Docs PR #45 (BPMN-09 scope, merged 2026-09-25): https://github.com/code-corhuila/multi-tour-docs/pull/45
- Docs PR #46 (BPMN-10, merged 2026-09-25): https://github.com/code-corhuila/multi-tour-docs/pull/46
- Docs PR #50 (PDR v1.9, merged 2026-09-27): https://github.com/code-corhuila/multi-tour-docs/pull/50
- Docs PR #52 (ADR-004 revision, merged 2026-09-27): https://github.com/code-corhuila/multi-tour-docs/pull/52
- Docs PR #53 (ADR-005 and ADR-006, merged 2026-09-27): https://github.com/code-corhuila/multi-tour-docs/pull/53
- Docs PR #54 (ADR-005 one engine per domain, merged 2026-09-27): https://github.com/code-corhuila/multi-tour-docs/pull/54
- Docs PRs #55, #56, #57 (alignment with the ADRs, open): https://github.com/code-corhuila/multi-tour-docs/pull/55, https://github.com/code-corhuila/multi-tour-docs/pull/56, https://github.com/code-corhuila/multi-tour-docs/pull/57
- Docs PR #26 (BPMN-08, closed without merge, replaced by #31): https://github.com/code-corhuila/multi-tour-docs/pull/26
