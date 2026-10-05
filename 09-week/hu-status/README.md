<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       09-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 09

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Documentation-only week: bring the Docs repository in line with PDR v1.9 and with the course norm and its Annex J (one database per engine, a security microservice, 19 repositories), record the teacher's written answers, and write the 42 user stories of the four macrodomains.
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). Docs (`code-corhuila/multi-tour-docs`) only accepts changes through a branch and a pull request, so every PR below is the evidence of its row. The last rows are still to do.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-16 | Align the API docs, the architecture overview and the worker job list with ADR-004 to ADR-006 (opened Sunday night of week 08, reported there as open) | done | Docs PRs #55, #56 and #57, merged 2026-09-28 00:47: https://github.com/code-corhuila/multi-tour-docs/pull/55 |
| API-02 | Record the contract naming gap and clarify the status of the course restrictions; reject an out-of-range pagination limit instead of clipping it | done | Docs PRs #58 (merged 2026-09-28 06:23) and #59 (merged 2026-09-28 08:38): https://github.com/code-corhuila/multi-tour-docs/pull/58 |
| ADR-02 | Record the teacher's answers on frameworks, worker repositories and Go; apply the frontend owner's allocation; fix the review findings; decide that every service is built from scratch | done | Docs PRs #60, #62, #63, #64, #65 (merged 2026-09-28) and #66 (merged 2026-09-29 11:03): https://github.com/code-corhuila/multi-tour-docs/pull/62 |
| ADR-03 | Decide tenant resolution, identity model, tokens and sessions (#73); the outbox contract and the owner of a departure's capacity (#74); e-mail, contract tests, observability, backup and files (#75); the runtime parameters and versions (#76) | done | Docs PRs #73, #74 (merged 2026-10-01 08:23 and 10:01) and #75, #76 (merged 2026-10-01 15:50): https://github.com/code-corhuila/multi-tour-docs/pull/73 |
| API-03 | Align the API guidelines with the course norm and rename the platform contract to `identity-audit` | done | Docs PRs #77 and #78, merged 2026-10-01 15:50: https://github.com/code-corhuila/multi-tour-docs/pull/77 |
| ADR-04 | Apply Annex J: one instance per engine (#82), the security microservice and two infrastructure repositories (#83, #85), transport security and the membership constraint (#92), and the saga `onboard-tenant` (#93) | done | Docs PRs #82 (merged 2026-10-01 20:12), #83, #85, #92 (merged 2026-10-02 06:48) and #93 (merged 2026-10-02 11:45): https://github.com/code-corhuila/multi-tour-docs/pull/82 |
| DOC-17 | Align folder `01-context` (overview, scope, glossary, project profile and scope templates) with PDR v1.9 and the ADRs | done | Docs PRs #94, #95 and #99, merged 2026-10-02 (13:22 to 15:11): https://github.com/code-corhuila/multi-tour-docs/pull/94 |
| DOC-18 | Align folder `03-product` (problem framing, discovery brief, vision, roadmap by cuts and a backlog of 24 items) with PDR v1.9 and ADR-004 | done | Docs PRs #101 (merged 2026-10-02 15:32) and #104 (merged 2026-10-02 17:37): https://github.com/code-corhuila/multi-tour-docs/pull/101 |
| ADR-05 | Record the teacher's written answers (2026-10-02 and 2026-10-03): outbox contract, name of the security repository, infrastructure repositories, fallback for the frontend federation, `traceId` in errors, retirement of the template `auth-service` | done | Docs PRs #107 (merged 2026-10-03 12:59), #109, #110, #111 and #112 (merged 2026-10-03 18:09 to 18:10): https://github.com/code-corhuila/multi-tour-docs/pull/109 |
| REQ-01 | Folder `04-requirements`: archive the 25 stories of Cut 1, align the functional requirements (27 RF) and the non-functional ones with PDR v1.9, add the NFR template | done | Docs PRs #113 (merged 2026-10-03 18:10) and #117 (merged 2026-10-04 01:02): https://github.com/code-corhuila/multi-tour-docs/pull/117 |
| REQ-02 | New user-story backlog: the convention `HU-<SERVICE>-NNN`, the index of 42 stories with dependencies and the equivalence with earlier series, and the 3 stories of `identity-audit` | done | Docs PR #116, merged 2026-10-04 01:02: https://github.com/code-corhuila/multi-tour-docs/pull/116 |
| DOC-19 | Consistency review of folders 01 to 09 (dates, scope of the cut 2 services, review records, terms, message-broker leftovers, payment status note, `CONTRIBUTING.md`) | done | Docs PRs #118 (merged 2026-10-04 01:02), #119 and #120 (merged 2026-10-04 10:28): https://github.com/code-corhuila/multi-tour-docs/pull/119 |
| ADR-06 | Decide that an account can hold several roles in its tenant and that the administrator types the initial password, which closes two contradictions between the ADRs and the PDR | done | Docs PR #121, merged 2026-10-04 12:09: https://github.com/code-corhuila/multi-tour-docs/pull/121 |
| REQ-03 | Write the 7 user stories of the security microservice, add four stories the PDR requires and none covered, and record the first gaps | done | Docs PR #126, merged 2026-10-04 17:52: https://github.com/code-corhuila/multi-tour-docs/pull/126 |
| REQ-04 | Write the other 32 user stories (time zone of the tenant, `commercial-reservation`, `operations-cost`, `cash-reporting`) and the story-to-requirement-to-test matrix | doing | Docs PRs #127 to #132, six chained PRs opened on 2026-10-04 16:32 and waiting for review: https://github.com/code-corhuila/multi-tour-docs/pull/127 |
| BKL-01 | Create the 42 stories as issues of `multi-tour-docs` with labels by macrodomain, service and owner, and automate the board | todo | Not started. It needs the `project` scope of the GitHub token and the organization secret for `env-tracking.yml` |
| SPR-09 | The code stories committed for Sprint 09 in the week 08 challenge (C-02, C-03, OC-02, F-01) | todo | Not started: the week went to the Annex J alignment and the stories; no code repository received a commit |
| WCH-04 | Weekly Challenge week 09, Session 1: secure configuration (`.env.example`, startup validation of required variables, secrets injected by `NAME_FILE` and never in git, a gitleaks pre-commit hook, the flag `FEATURE_TOKEN_REVOCATION`) on a Go stand-in for the security microservice | done | [WeeklySummary/WeeklyChallenge/secure-config/](./WeeklySummary/WeeklyChallenge/secure-config/README.md) (2026-10-05, after the Sunday cutoff): 7 Go tests pass; with no variables the service exits with code 1 and names all three; flag off gives `404`, on gives `202`; the hook blocks two fake keys |
| WCH-05 | Weekly Challenge week 09, Session 2: secure-config and rollout plan for MVP 2 | todo | Not started |

## 2. My individual contribution
Chronological, local time (UTC-5).
- **2026-09-28.** Merged at 00:47 the three PRs opened on Sunday night (#55, #56, #57). Then answered the course review: recorded the contract naming gap and the pagination rule (#58, #59), fixed stale review statuses (#60), recorded the teacher's answers on frameworks, worker repositories and Go (#62, #63), applied the frontend owner's allocation and fixed the review findings (#64, #65).
- **2026-09-29.** Decided that every service is built from scratch in dependency order, `identity-audit` first, with no code copied from the monolith (#66).
- **2026-10-01.** Decided how the tenant is resolved on the web, the identity model, tokens and revocation (#73); the outbox contract and who owns a departure's capacity so that no seat is oversold (#74); e-mail over SMTP, contract tests, observability and backup (#75); and every runtime limit and version (#76). Aligned the API guidelines with the course norm (#77, #78). With the teacher's Annex J of the week, rewrote the data topology to one PostgreSQL and one MongoDB instance (#82) and added the security microservice and the two infrastructure repositories (#83, #85).
- **2026-10-02.** Decided transport security and the membership constraint (#92) and the saga `onboard-tenant` (#93). Aligned folders `01-context` (#94, #95, #99) and `03-product` (#101, #104) with PDR v1.9 and the ADRs.
- **2026-10-03.** Recorded the teacher's written answers: the outbox contract (#107), the security repository and the infrastructure repositories that he created, the federation fallback, `traceId`, and the retirement of the template `auth-service` (#109 to #112). Archived the Cut 1 stories (#113). Validated the teammate's proposal of 32 stories against PDR v1.9 and Annex J, and extended it to 42.
- **2026-10-04.** Merged the aligned requirements (#117), the backlog index and the three `identity-audit` stories (#116) and the consistency fixes (#118, #119, #120). Decided the roles per account and the administrator-typed password (#121). Wrote the 7 security stories (#126, merged at 17:52) and the other 32 stories with the traceability matrix (#127 to #132, open).
- **2026-10-05.** After the Sunday cutoff, did the Session 1 Weekly Challenge: a Go service (standard library only) that validates its configuration at startup and reports every missing variable at once, reads its secrets from `NAME` or from a mounted file (`NAME_FILE`), keeps token revocation behind `FEATURE_TOKEN_REVOCATION`, and has 7 tests. Also a gitleaks pre-commit hook, now active in this repository; a scan of its 28 commits found no leaks.

Not mine, so not counted here: the Docs PRs authored by a teammate this week (folders 00, 02, 06, 08, 12 and 16; for example #91, #96, #100, #105, #106, #114, #115, #122 to #125), and the teacher's material of `Data/Document/`, `spec/` and `template/`.

## 3. Blockers and risks
- PRs #75 and #76 were merged with the teacher's review dismissed, so ADR-004 D5, D7 to D10 and ADR-005 E6 have no recorded approval. The question is in the list for the teacher.
- Annex J arrived on 2026-10-01 and changed the architecture in the middle of the week (one database per engine, a security microservice, 19 repositories). The affected ADRs and folders had to be rewritten first, and the repositories were created by the teacher only on 2026-10-03.
- No product code this week: the 19 repositories hold only their README and `CODEOWNERS`, and the code stories committed for Sprint 09 were not started. The Sprint 09 process planned in week 08 (a board open from day 1, at most 2 stories in `doing` and a daily note) was not run: the board of the week 08 challenge was built only for that challenge, and the process was to start once folder `04-requirements` was finished.
- Nine gaps are open in the backlog and need a decision before their stories are Ready: three belong to the teammate's folders (`06-data`, `12-ux-ui`) and six to the architecture decisions (how the services recognize the exceptional access, the validity of the e-mail verification link, how the first Platform Administrator account is created, how the account is linked to the customer record, where images are stored, how an open cash box blocks a time-zone change).
- The six story PRs (#127 to #132) are chained: until each previous one is merged and `main` is brought into the next branch, its diff shows the lines of the previous ones, over the limit of 400.
- The four findings of the automatic review of PR #121 were not answered in the PR before it was merged; the norm asks for an answer to each.
- Part of the stories carry values that came from decisions without a recorded review (the limits of ADR-004 D10); they are marked as such in the stories.

## 4. Plan for next week
In order of dependency.
- Merge the story PRs in order (#127 to #132), bringing `main` into each branch after the previous merge, and answer the findings of the automatic review.
- Resolve the nine open gaps: the six architecture ones as ADR edits, and the three of the teammate's folders with her.
- Ask the teacher to review the ADR decisions that were merged without a recorded approval.
- Create the 42 stories as issues of `multi-tour-docs` with labels by macrodomain, service and owner, set the variables and the secret of `env-tracking.yml`, and move the stories to the board.
- Write the contracts of the security microservice and the gateway (`07-api/contracts/`) and the first spec of `multi-tour-security` and `identity-audit` in `07-api/contracts/specs-*`, then start the first service in dependency order.
- Update the local clones of the repositories the teacher renamed and created (`multi-tour-infra-postgres`, `multi-tour-infra-mongo`, `multi-tour-security`).

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - every PR title and commit of this week follows it (`docs(architecture)`, `docs(07-api)`, `docs(01-context)`, `docs(03-product)`, `docs(04-requirements)`, `docs(05-architecture)`, `docs(09-microservices)`)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - no code repository was touched; every Docs change went through a `docs/` branch and a PR into `main` (Docs has a single permanent branch): 46 PRs of mine active this week (3 of them opened on 2026-09-27, in week 08), 40 merged and 6 open
- [x] Testable acceptance criteria - the 42 stories carry more than 250 Given/When/Then criteria; 10 stories are merged (#116, #126) and 32 are in review (#127 to #132): https://github.com/code-corhuila/multi-tour-docs/blob/main/04-requirements/user-stories.md
- [x] Tests added/updated (unit / integration) - 7 unit tests in the Weekly Challenge service (`go test -v ./...`, all passing); the product repositories received no code or tests
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) - not applicable: no code was changed this week
- [x] No secrets; config via environment variables - the Weekly Challenge service reads everything from environment variables or mounted secret files, with an `.env.example` of placeholders; gitleaks found no leaks in the 28 commits of this repository, and its pre-commit hook is active

## 6. Evidence links
- Class summary image for week 09: [WeeklySummary/Week-09.png](./WeeklySummary/Week-09.png)
- Weekly Challenge week 09, Session 1 writeup and code: [WeeklySummary/WeeklyChallenge/secure-config/](./WeeklySummary/WeeklyChallenge/secure-config/README.md)
- Teacher's material of the week: the example project [Data/spec/](./Data/spec/) and the per-piece templates [Data/template/](./Data/template/)
- Annex J of the course norm (teacher's material): [Data/Document/3-Anexo-J-Base-de-Datos-Unica-Sistemas-Distribuidos-2026B.pdf](./Data/Document/3-Anexo-J-Base-de-Datos-Unica-Sistemas-Distribuidos-2026B.pdf)
- Docs PR #73 (identity model, tokens and sessions, merged 2026-10-01): https://github.com/code-corhuila/multi-tour-docs/pull/73
- Docs PR #82 (one instance per engine under Annex J, merged 2026-10-01): https://github.com/code-corhuila/multi-tour-docs/pull/82
- Docs PR #109 (teacher's written answers, merged 2026-10-03): https://github.com/code-corhuila/multi-tour-docs/pull/109
- Docs PR #116 (backlog index and `identity-audit` stories, merged 2026-10-04): https://github.com/code-corhuila/multi-tour-docs/pull/116
- Docs PR #117 (requirements aligned with PDR v1.9, merged 2026-10-04): https://github.com/code-corhuila/multi-tour-docs/pull/117
- Docs PR #121 (several roles per account and administrator-typed password, merged 2026-10-04): https://github.com/code-corhuila/multi-tour-docs/pull/121
- Docs PR #126 (stories of the security microservice, merged 2026-10-04): https://github.com/code-corhuila/multi-tour-docs/pull/126
- Docs PRs #127 to #132 (the other 32 stories and the matrix, open): https://github.com/code-corhuila/multi-tour-docs/pull/127
