<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       13-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 13

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Threat-model the boundary from the API gateway to the security service and turn its top risks into a prioritized security backlog with security gates in CI, while finishing the verification of the final customer's e-mail (HU-SEC-003) one story at a time.
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). The window starts on Monday 2026-10-26 08:40 (the cut of week 12) and this README describes the state on Thursday 2026-10-29, while the week is still running. Docs (`code-corhuila/multi-tour-docs`) only accepts changes through a branch and a pull request; the code repositories receive `feat/` branches into `develop`. Status is the real state on the day of writing. I did not re-check the board cards for this README.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-SEC-003 | Verify the final customer's e-mail (`code-corhuila/multi-tour-docs#160`), second part. Wiring into the `security` service: mail settings read at start-up, a configuration that cannot send or would send in clear is refused, work started after an answer is awaited when the server stops, and the verification use cases wired to the two public routes. README for trying it with Mailpit and the mail settings in the infra examples. Evidence of tasks VER-01 to VER-07 in Docs | done | `multi-tour-security` #52 (2026-10-26 08:45), #53 (08:48), #54 (09:07) and #55 (10:03): https://github.com/code-corhuila/multi-tour-security/pull/54 · `multi-tour-infra-postgres` #22 (10:03): https://github.com/code-corhuila/multi-tour-infra-postgres/pull/22 · Docs #514 (10:12): https://github.com/code-corhuila/multi-tour-docs/pull/514 |
| HU-SEC-003 | Portal side of the verification: spec, plan and tasks of the verify-the-e-mail screens (Docs), then the notice to check the e-mail after registering and the new-link form in the identity portal | doing | Docs #515 (2026-10-26 10:31): https://github.com/code-corhuila/multi-tour-docs/pull/515 · `multi-tour-identity-audit-portal` #51 (15:59): https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/51 · not done: the page behind the e-mail link, which I did not check end to end this week |
| W13-TM-01 | Workshop of week 13, threat model of the boundary `multi-tour-api-gateway` to `multi-tour-security`. Claim issue, attack surface map with trust boundaries and an asset inventory, then STRIDE with a likelihood x impact score per row, a risk ranking, the residual risk and a library book for the rows marked [B] | done | Claim issue https://github.com/code-corhuila/multi-tour-docs/issues/578 · fork commits `3a73d18` (2026-10-29 08:12), `0c9f5a3` (08:30), `7c8b181` (09:38): https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop · Docs PR https://github.com/code-corhuila/multi-tour-docs/pull/579 |
| W13-TM-02 | Review fixes of the STRIDE document: it said some routes were built that are not in the service yet, and the lockout of sign-in was described without the file that holds it. Corrected in a new pull request after the first one was merged | doing | https://github.com/code-corhuila/multi-tour-docs/pull/587 (opened 2026-10-29 10:14, still open) |
| W13-BKL-01 | Security backlog from the top risks: three stories with a STRIDE letter, a risk, testable criteria and a link to the threat model. #584 (E, risk 6, P1, 3 criteria), #585 (S, risk 6, P1, 4 criteria) and #586 (I, risk 6, P1, 4 criteria, cites the book). All in the board `code-corhuila/projects/28`, Backlog | todo | https://github.com/code-corhuila/multi-tour-docs/issues/584 · https://github.com/code-corhuila/multi-tour-docs/issues/585 · https://github.com/code-corhuila/multi-tour-docs/issues/586 · fork commit `6b61891` (09:52) |
| W13-CI-01 | Security gates and one negative authorization test: `security.yml` with gitleaks, govulncheck and the test, and the test itself (a token of tenant A gets `403` on the service-only route of tenant B in `multi-tour-security`). I saw the workflow run by hand: gitleaks and the test passed, govulncheck failed with 3 real findings | done | Fork commits `a2d6740`, `39b9d5b`, `343c1a0` (2026-10-29 14:46), `202ff77` (14:49), `9ef9a56` (15:06): https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop/04-security-ci · run https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682 |
| W13-REV-01 | Peer review of the threat model of the teammate who models the boundary gateway to `multi-tour-commercial-reservation-api`: a comment on a concrete row (denial of service) with a proposed change, without approving or merging | done | https://github.com/code-corhuila/multi-tour-docs/pull/580#discussion_r4414101698 |
| WCH-01 | Weekly Challenge session 1, secure your system: what the project already does for the edge, tokens, authorization, service-to-service calls, hashing and input validation, and what is missing (no mTLS, the gateway does not validate the token, 3 vulnerable dependencies). Documentation only, no code | done | [WeeklySummary/WeeklyChallenge/Secure-Your-System-Multitour.md](./WeeklySummary/WeeklyChallenge/Secure-Your-System-Multitour.md) |
| WCH-02 | Weekly Challenge session 2, threat model and security backlog: the work of the workshop mapped to the session, and the gaps (the gates are not in the team CI, threat D has no story, one boundary only). Documentation only, no code | done | [WeeklySummary/WeeklyChallenge/Threat-Model-And-Security-Backlog-Multitour.md](./WeeklySummary/WeeklyChallenge/Threat-Model-And-Security-Backlog-Multitour.md) |

## 2. My individual contribution
Chronological, local time (UTC-5). Since 2026-10-26 08:40 I merged 9 PRs under my account: 4 in `multi-tour-security`, 3 in Docs, 1 in `multi-tour-identity-audit-portal` and 1 in `multi-tour-infra-postgres`. The Docs PR #579 is counted there, and it was merged by mistake (see blockers).
- **2026-10-26, 08:45 to 10:12.** Finished the wiring of the e-mail verification into `security` (#52 to #54), explained how to try it with Mailpit (#55), added the mail settings to the infra examples (#22) and recorded the evidence of its tasks in Docs (#514).
- **2026-10-26, 10:31 and 15:59.** Specified the portal screens of the verification (Docs #515) and built the notice to check the e-mail and the new-link form (portal #51).
- **2026-10-29, workshop, 07:50 to 09:52.** Claimed the boundary (issue #578), drew the attack surface (`3a73d18`, `0c9f5a3`), went to the library for a physical book, wrote the STRIDE of the boundary (Docs #579) and created the three stories of the backlog (#584 to #586, `6b61891`).
- **2026-10-29, 10:14.** Opened a second pull request (#587) with fixes to the STRIDE: I had written that some routes were built when they are not in the service yet.
- **2026-10-29, 14:46 to 15:06.** Wrote the security gates and the negative test, ran the test in a temporary copy of the service, and ran the workflow by hand in my fork (`a2d6740` to `9ef9a56`).
- **2026-10-29, afternoon.** Wrote the two weekly challenges of the week as documents.
- **Peer review.** Left a concrete comment on a row of the teammate's STRIDE (PR #580) and did not approve or merge it.
- **Not counted:** the commits of the teammate, and any PR merged after the time of writing.

## 3. Blockers and risks
- **I merged my own pull request.** PR #579 (the STRIDE of the boundary) was merged under my account on 2026-10-29 at 09:31, which the rules of the workshop forbid: nobody merges their own PR. It was a mistake. I did not revert it or rewrite history; the fixes went in a new pull request, #587, which stays open, and I will tell the professor.
- **GitHub Actions of the organization is locked by billing.** CI is red on the PRs of the organization for that reason, not for the change. The gates of this week run only in my fork, by hand.
- **The security gates are not in the CI of the team repositories.** None of the 20 `ci.yml` files runs `gitleaks`, `govulncheck`, `npm audit` or a dependency check.
- **`govulncheck` found 3 vulnerabilities in `multi-tour-security`** (`golang.org/x/text@v0.24.0`: `GO-2026-6629`, `GO-2026-5970`; `github.com/jackc/pgx/v5@v5.7.6`: `GO-2026-5004`). They are not fixed: it is a code change that needs its story, spec and pull request.
- **The negative test was never seen failing.** It passes with the current code; I did not remove the protection to see it fail.
- **Routes in the contract that are not in the service yet:** `POST` and `GET /security/users` and the memberships routes. The authorization of those routes cannot be tested today.
- **Only one trust boundary is modeled.** The others are open in the attack surface map, and threat D (risk 6) has no story yet.
- **HU-SEC-003 is not closed:** the page behind the verification link was not checked end to end this week.

## 4. Plan for next week
In order of dependency.
- Answer the review of the STRIDE and finish the pull request #587; ask the professor about the merge of #579.
- Refine the three stories (a link between them, criteria that can be tested) and write the story of threat D.
- Upgrade `pgx` and `golang.org/x/text` in `multi-tour-security` through its own story, with the test first, and check that the negative test fails when the protection is removed.
- Propose the security gates for the CI of the team repositories, one repository per pull request.
- Finish HU-SEC-003: the page behind the e-mail link and its end-to-end evidence.
- Promote the finished stories to `qa` with `qa/` branches and `cherry-pick -x`, and re-run the PRs in CI when Actions is unlocked.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - the nine fork commits made before this one follow it (`docs(w13)`, `ci(w13)`, `test(w13)`, `chore(ci)`), and so do the titles of the code PRs (`feat(app)`, `docs(readme)`, `feat(portal)`)
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - the code PRs of the week go from `feat/` branches to `develop`. The STRIDE branch is `security/stride-Molina211`, the name the workshop guide requires, with a PR to `main`. Nothing was promoted to `qa` or `main`
- [x] Testable acceptance criteria - issues #584, #585 and #586 carry 3, 4 and 4 criteria, one of them an automated test
- [x] Tests added/updated (unit / integration) - the negative authorization test `TestW13_ATokenOfTenantACannotCreateAnAdministratorInTenantB` passes locally and in my fork's workflow run; it was not run in the organization's CI (locked)
- [x] DDD / hexagonal boundaries respected (domain has no I/O) - the HTTP adapter of `security` depends on interfaces (`AccountCreator`) and the test drives it with a fake; the wiring of this week is in the `app` layer
- [x] No secrets; config via environment variables - gitleaks passed on the fork's history; the mail settings come from the environment and `.env.example` holds placeholders only

## 6. Evidence links
- Class summary image for week 13 (security and threat modeling): [WeeklySummary/Week-13.png](./WeeklySummary/Week-13.png)
- Weekly Challenge week 13, Session 1 (secure your system; documentation of what exists, no code): [WeeklySummary/WeeklyChallenge/Secure-Your-System-Multitour.md](./WeeklySummary/WeeklyChallenge/Secure-Your-System-Multitour.md)
- Weekly Challenge week 13, Session 2 (threat model and security backlog; documentation only): [WeeklySummary/WeeklyChallenge/Threat-Model-And-Security-Backlog-Multitour.md](./WeeklySummary/WeeklyChallenge/Threat-Model-And-Security-Backlog-Multitour.md)
- Workshop folder in the fork: https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop
- Attack surface map: [workshop/01-attack-surface.md](../workshop/01-attack-surface.md)
- Threat model PR: https://github.com/code-corhuila/multi-tour-docs/pull/579 and its fixes https://github.com/code-corhuila/multi-tour-docs/pull/587
- Security stories: https://github.com/code-corhuila/multi-tour-docs/issues/584 , https://github.com/code-corhuila/multi-tour-docs/issues/585 , https://github.com/code-corhuila/multi-tour-docs/issues/586
- Security gates run: https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682
- Peer review comment: https://github.com/code-corhuila/multi-tour-docs/pull/580#discussion_r4414101698
- E-mail verification wiring in `security`: https://github.com/code-corhuila/multi-tour-security/pull/54
- Portal notice and new-link form: https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/51
