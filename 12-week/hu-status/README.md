<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       12-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 12

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Start the third cut of Multitour working one story at a time, end to end: close HU-SEC-001 (sign-in) with its end-to-end evidence, build inactivate and reactivate of a tenant, close the demo debts of the onboard-tenant saga, deliver session revocations from `security` through the relay to `identity-audit`, and start the verification of the final customer's e-mail in `security`.
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). The window is Monday 2026-10-19 08:40 (the cut of week 11) to Monday 2026-10-26 08:40, delivered late on the morning of 2026-10-26. I merged no pull request between 2026-10-19 08:40 and 2026-10-23 03:10; the Weekly work of those days (the week 11 workshop, deliveries 1 and 2) is already counted in the week 11 README and is not repeated here. Docs (`code-corhuila/multi-tour-docs`) only accepts changes through a branch and a pull request; the code repositories receive `feat/` branches into `develop`. Status is the real state on the day of writing: every issue of the stories below is still open in the board repository, because an issue closes only when the story reaches `main`; I did not re-check the board cards for this README.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-22 | Bring Docs in line with the third cut: traceability matrix with the cut 3 gaps, missing domain events and repository status, context/roadmap/overview with release 2.0.0, project-control registers (risks, questions, debt, dependencies), quality folder (testing strategy, TDD guide, coverage, manual checks, review guide), BPMN 07 to 12 with the distributed status, signature of the front visual identity spec | done | Docs #471 to #482 (2026-10-23 03:10 to 03:32): https://github.com/code-corhuila/multi-tour-docs/pull/471 |
| HU-SEC-001 | Sign in and receive a token (`code-corhuila/multi-tour-docs#156`). Closing the story: its five specs brought up to date, the end-to-end check recorded with the missing build check, the identity portal README describing the real screens, and a test that the built bundle holds no HTTP client, token or gateway address | done | Docs #483 (2026-10-23 12:23), #484 (12:40), #485 (13:23): https://github.com/code-corhuila/multi-tour-docs/pull/485 · `multi-tour-identity-audit-portal` #48 (12:43) and #49 (12:52): https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/49 |
| HU-IDA-001 | Manage the tenant lifecycle (`#158`): inactivate and reactivate a tenant from its own screen (spec amended, then portal test), the link of an inactive tenant keeps its sign-in and mounts no area (front, with its own spec), raised JaCoCo floor of `core` and `adapters` in the API, and the end-to-end evidence of PT-06 and PT-07 | doing | Docs #486 (2026-10-23 14:08), #487 (14:52), #489 (15:22), #490 (16:06), #491 (17:57), #492 (18:01): https://github.com/code-corhuila/multi-tour-docs/pull/486 · portal #50 (14:52): https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/50 · `-api` #47 (15:19): https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/47 · `multi-tour-front` #44 (17:57): https://github.com/code-corhuila/multi-tour-front/pull/44 |
| HU-WFL-001 | Onboard a tenant and its first Administrator (`#205`): close the two demo debts of the saga. F2: keep what a resumed saga needs to finish step three (who asked, the membership id, the initial status). F3: claim a stale saga once and refuse a save from an older copy. Spec, plan and tasks, then the evidence | done | Docs #493 (2026-10-23 18:31) and #496 (19:00): https://github.com/code-corhuila/multi-tour-docs/pull/493 · `multi-tour-workflow-onboard-tenant` #11 (18:31) and #12 (18:54): https://github.com/code-corhuila/multi-tour-workflow-onboard-tenant/pull/12 |
| HU-WRK-007 | Relay events between domains (`#203`): session revocations from `security` to `identity-audit`. Relay: alert when an event fails for good or waits too long, a test that the relay imports no database driver, how the service token is issued, then delivery of `AccountSessionsRevoked` to identity-audit. Database: `processed_event` and the session cutoff. API: set the cutoff and never move it back, one transaction with the processed event id, the internal route, and refusing a token issued before the cutoff. Contract route, specs and end-to-end evidence in Docs | doing | Docs #497 to #499 (2026-10-23 20:46 to 21:25), #500 (10-24 00:14), #501 (03:05), #507 (15:53): https://github.com/code-corhuila/multi-tour-docs/pull/507 · relay #7 to #9 (2026-10-23 23:54) and #10 (10-24 15:56): https://github.com/code-corhuila/multi-tour-worker-event-relay/pull/10 · `-db` #9 (10-24 03:04): https://github.com/code-corhuila/multi-tour-identity-audit-db/pull/9 · `-api` #48 to #51 (10-24 14:24 to 15:10): https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/51 |
| PROC-01 | Process in Docs: verify each branch in the Docker stack of its own environment; answer open question Q-05 and note the naming left to align in 06-data | done | Docs #508 (2026-10-24 17:11): https://github.com/code-corhuila/multi-tour-docs/pull/508 · #509 (17:20): https://github.com/code-corhuila/multi-tour-docs/pull/509 |
| HU-SEC-003 | Verify the final customer's e-mail (`#160`), in `security`. Signed spec, plan, tasks and the verification routes in the security contract (Docs). Code: pending status, verification links and commercial name in the schema; account created pending with one link; a new link answered the same for every e-mail; verification with the link and the password; Postgres adapters under a lock on the account; the e-mail in Spanish with the link after the hash; SMTP with STARTTLS and PLAIN, a mail configuration that cannot send or would send in clear is refused; Mailpit in the development composition; `403 EMAIL_NOT_VERIFIED` for a pending account; the two public routes | doing | Docs #510 to #512 (2026-10-24 18:24): https://github.com/code-corhuila/multi-tour-docs/pull/510 · `multi-tour-security` #35 to #39 (2026-10-25 16:28 to 17:09), #40 to #44 (10-25 19:45 to 10-26 01:19), #45 to #51 (10-26 06:39 to 07:31): https://github.com/code-corhuila/multi-tour-security/pull/50 · not done: wiring into the service (security #52 to #54, merged after the cut, belong to week 13) and the portal page behind the link |
| REL-03 | Release 2.0.1 of the portal-side repositories (`identity-audit-portal`, `operations-cost-portal`, `front`, `infra-postgres`, `api-gateway`): the five open PRs were closed without merging on 2026-10-23 at 01:48; those changes stay in `develop` and `qa` for the release of week 15 | todo | Closed, not merged: https://github.com/code-corhuila/multi-tour-front/pull/35 |

## 2. My individual contribution
Chronological, local time (UTC-5). In the window I merged 67 PRs under my account: 34 in Docs and 33 in six code repositories (`security` 17, `identity-audit-api` 5, `worker-event-relay` 4, `identity-audit-portal` 3, `workflow-onboard-tenant` 2, `identity-audit-db` 1, `front` 1). All code PRs target `develop`. From 2026-10-19 to 2026-10-22 I merged nothing: those days went to the week 11 workshop deliveries.
- **2026-10-23, 03:10 to 03:32.** Brought Docs in line with the third cut (#471 to #482): traceability matrix, domain events, context and roadmap with release 2.0.0, project-control registers, the quality folder and the BPMN status.
- **2026-10-23, from 12:23.** Closed HU-SEC-001: updated its specs, recorded the end-to-end check (Docs #483 to #485) and added the bundle test and the real README to the identity portal (#48, #49). Then built inactivate and reactivate of a tenant (Docs #486, portal #50), raised the coverage floor of the identity-audit API (#47), and specified and built the inactive-tenant link in the front (Docs #491, front #44; evidence #490, #492).
- **2026-10-23, evening.** Closed the two demo debts of the onboard-tenant saga: spec (Docs #493), then the resume state (#11) and the single claim of a stale saga (#12), with the evidence (#496). Wrote the contract route and the specs of HU-WRK-007 (Docs #497 to #499).
- **2026-10-24.** Relay: alerts, the no-database-driver test and the service-token note (#7 to #9, evidence #500); identity-audit database collections (#9); in the API the cutoff, the transaction, the internal route and the token check (#48 to #51); relay delivery of session revocations (#10) and the end-to-end evidence (Docs #507). Added the Docs rule to verify each branch in the stack of its environment (#508), answered Q-05 (#509), and wrote the spec, contract routes, plan and tasks of HU-SEC-003 (#510 to #512).
- **2026-10-25 to 2026-10-26 (until 07:31).** HU-SEC-003 in `security`: the schema and core (#35 to #39), the adapters and the SMTP sender (#40 to #46), Mailpit in the composition (#47) and the HTTP layer (#48 to #51).
- **Not mine, so not counted:** ten Docs PRs of a teammate in the window (for example #470, #488, #495, #502 to #506, #513), and any PR merged after 08:40 on 2026-10-26.

## 3. Blockers and risks
- **GitHub Actions is locked by the billing of the organization.** CI is red on every PR of the window for that reason, not for the change; each PR documents the failed job, the local replacement (`gofmt -l .`, `go vet ./...`, `go test`) and that it will run again when Actions returns (for example `multi-tour-security` #50 and `multi-tour-worker-event-relay` #10). Until then no merge is backed by a green `ci.yml`.
- **No story is closed on the board.** All four issues (#156, #158, #203, #205) and HU-SEC-003 (#160) are still open; HU-SEC-001 and HU-WFL-001 are finished in `develop` but nothing is in `qa` or `main` from this week.
- **The race detector could not be run** on my machine (it needs cgo), so the concurrency tests of the Go services were run without `-race`.
- **HU-SEC-003 is half done.** The service is not wired to the new use cases when the window closes and the portal page behind the link does not exist, so no customer can verify an e-mail end to end yet.
- **Pending naming in 06-data** (`account_session_cutoff`, recorded in Docs #509) is owned by a teammate; the identity-audit side was built without waiting for it (Docs #501).
- **Release 2.0.1 was not delivered:** its fixes wait for the release of week 15.

## 4. Plan for next week
In order of dependency.
- Wire the e-mail verification into the `security` service and run it end to end against a real database and a mailbox (Mailpit), then record the evidence in Docs.
- Build the portal page behind the verification link, using the front client and session, never its own.
- Promote the finished stories (HU-SEC-001, HU-WFL-001) to `qa` with `qa/` branches and `cherry-pick -x`, and verify them in the `qa` stack.
- Finish HU-IDA-001 and HU-WRK-007: the criteria still open, then the board cards and checkboxes.
- Re-run all PRs of the window in CI as soon as Actions is unlocked.
- Start the next story of the cut, one at a time, with its spec signed first.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - the titles of the PRs of this week follow it (`feat(session)`, `feat(core)`, `docs(07-api)`, `test(portal)`)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - code PRs go from `feat/` branches to `develop`; Docs changes go by branch and PR into `main`. Nothing was promoted to `qa` or `main` this week
- [x] Testable acceptance criteria - the specs of each piece carry criteria and a "Tests" section (Docs #483, #486, #493, #499, #510)
- [x] Tests added/updated (unit / integration) - for example `identity-audit-portal` #49 and #50 are tests, `worker-event-relay` #8 pins that the relay imports no database driver, and the relay delivery PR names its failing test first; run locally, not in CI (see blockers)
- [x] DDD / hexagonal boundaries respected (domain has no I/O) - the verification use cases go in `core` (`security` #36 to #39) and the Postgres and SMTP code in `adapters` (#40 to #46, #45)
- [x] No secrets; config via environment variables - `security` #44 and the start-up check refuse a mail configuration that cannot send or would send the link in clear; the mail settings are passed to the service by environment (`security` #47)

## 6. Evidence links
- Class summary image for week 12 (cloud deployment and rollback): [WeeklySummary/Week-12.png](./WeeklySummary/Week-12.png)
- Weekly Challenge week 12, Session 1 (zero-downtime deployment; a design, nothing deployed): [WeeklySummary/WeeklyChallenge/Zero-Downtime-Deployment-Multitour.md](./WeeklySummary/WeeklyChallenge/Zero-Downtime-Deployment-Multitour.md)
- Weekly Challenge week 12, Session 2 (deployment plan for MVP 3; rollback not rehearsed, not timed): [WeeklySummary/WeeklyChallenge/Deployment-Plan-MVP3-Multitour.md](./WeeklySummary/WeeklyChallenge/Deployment-Plan-MVP3-Multitour.md)
- Docs, third cut alignment: https://github.com/code-corhuila/multi-tour-docs/pull/471
- HU-SEC-001 closing: https://github.com/code-corhuila/multi-tour-docs/pull/485
- Inactivate and reactivate a tenant, portal: https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/50
- Inactive-tenant link in the front: https://github.com/code-corhuila/multi-tour-front/pull/44
- Onboard-tenant saga, claim of a stale saga: https://github.com/code-corhuila/multi-tour-workflow-onboard-tenant/pull/12
- Session revocations, relay delivery: https://github.com/code-corhuila/multi-tour-worker-event-relay/pull/10
- Session revocations, identity-audit API (token refused before the cutoff): https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/51
- Session revocations, end-to-end evidence: https://github.com/code-corhuila/multi-tour-docs/pull/507
- HU-SEC-003 spec: https://github.com/code-corhuila/multi-tour-docs/pull/510
- HU-SEC-003, confirmation route: https://github.com/code-corhuila/multi-tour-security/pull/50
- Merged PRs of the window in `security`: https://github.com/code-corhuila/multi-tour-security/pulls?q=is%3Apr+is%3Amerged
