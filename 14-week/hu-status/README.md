<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       14-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 14

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Map our Definition of Done to the gates that each pull request already runs, and keep closing one user story at a time: the internal users, special permissions and password recovery of `security`, the audit of identity-audit, and the first stories of operations-cost (schedule a departure, its vehicles, its start and its end), which started from two empty repositories.
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). The window runs from Monday 2026-11-02 08:40 to Monday 2026-11-09 08:40 (the cut of this week). In that window I opened 250 pull requests in `code-corhuila`: 248 merged and 2 closed without merge (`gh search prs --author Molina211 --owner code-corhuila --created 2026-11-02..2026-11-09`, filtered by the hour). Docs only accepts changes through a branch and a pull request; the code repositories receive `feat/`, `fix/`, `chore/` and `docs/` branches into `develop`. The status column is the board (`code-corhuila/projects/28`) on the day of writing, Saturday 2026-11-14: `In Review` means my side is finished and a criterion waits on a service of my teammate, so I write it as `doing`.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-SEC-003 | Verify the final customer's e-mail (`code-corhuila/multi-tour-docs#160`), last part: the page behind the link of the verification e-mail, the notice at sign-in for an account that is still pending, and the evidence and debt of those screens | done | `multi-tour-identity-audit-portal` #52 to #54 (2026-11-02 17:17 to 17:42): https://github.com/code-corhuila/multi-tour-identity-audit-portal/pull/52 · Docs #672: https://github.com/code-corhuila/multi-tour-docs/pull/672 |
| HU-IDA-003 | Maintain the tenant's visual identity (`#165`): split the form, read every rejected error in one function, a type guard in the container, evidence and pillar review. The logo and the images were split into a new story, HU-IDA-005 (`#683`) | done | Portal #55 to #57, `multi-tour-front` #50, `multi-tour-identity-audit-api` #52 (2026-11-03 01:08 to 01:16) · Docs #674, #675, #684: https://github.com/code-corhuila/multi-tour-docs/pull/684 |
| HU-SEC-005 | Manage the tenant's internal users (`#161`): register an internal user, list them by role and status, inactivate and reactivate, protect the last active administrator, issue a new temporary credential, and answer the refusals in one place. Spec brought to the whole story and signed again, contract, evidence and pillar review. Its AC3 became a new story, HU-SEC-008 (`#687`) | doing | `multi-tour-security` #56 to #73 (2026-11-03 14:55 to 2026-11-04 01:13): https://github.com/code-corhuila/multi-tour-security/pull/69 · Docs #685, #686, #688, #689, #714, #726: https://github.com/code-corhuila/multi-tour-docs/pull/714 · In Review: AC4 waits on the teammate's services |
| HU-SEC-006 | Grant special permissions and optional roles (`#162`): store them, assign and remove a permission by the table of the account, enable and disable the optional roles of a tenant, put the permissions in the token, and a test that no route creates a permission or changes the role of a user | doing | `multi-tour-security` #74 to #86 (2026-11-04 12:42 to 20:24): https://github.com/code-corhuila/multi-tour-security/pull/84 · Docs #727, #742, #743, #754, #755: https://github.com/code-corhuila/multi-tour-docs/pull/755 · In Review: waits on the teammate's services |
| HU-IDA-002 | Record and consult the audit of sensitive actions (`#163`): the audit collections and the access settings in MongoDB, every delivered event recorded as an audit record, the audit lists of the platform and of a tenant, the route of the own tenant, the screens with pager and filters, and the access switches of the optional roles | doing | `multi-tour-identity-audit-db` #10 to #12, `multi-tour-identity-audit-api` #53 to #65, portal #58 to #70 (2026-11-06 14:04 to 2026-11-07 20:28): https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/59 · Docs #768 to #797: https://github.com/code-corhuila/multi-tour-docs/pull/797 |
| HU-WRK-007 | Relay events between domains (`#203`): a route of its own for the events with no tenant (ADR change, specs, plans), the audited security events routed to identity-audit | doing | `multi-tour-worker-event-relay` #11, #12 (2026-11-07 02:35 and 02:39), identity-audit-api #61 to #63 · Docs #781 to #785: https://github.com/code-corhuila/multi-tour-docs/pull/784 |
| W13-BKL-01 (story #586) | A test from the security backlog of week 13: a refusal of the registration is the common error envelope and never shows a driver message, SQL or a file path (malformed JSON `400`, an e-mail of 10,000 characters `413`, a store failure `500`) | done | `multi-tour-security` #87 (2026-11-07 21:12): https://github.com/code-corhuila/multi-tour-security/pull/87 · story https://github.com/code-corhuila/multi-tour-docs/issues/586 |
| HU-OPS-001 | Schedule the departures of a tour (`#187`). `operations-cost-api` and `operations-cost-db` were empty: schema, roles and migration runner, the departure table, outbox and idempotency key; the three Maven modules (`core`, `adapters`, `app`), health route, error envelope, correlation id, RS256 filter, pool and body limit; the departure aggregate, schedule and reschedule, JDBC persistence, the catalog and seats adapters, the internal outbox routes; the schedule form of the portal through the container client; coverage floor and format check in `mvn verify`, ESLint and Prettier in the portal CI | doing | `multi-tour-operations-cost-db` #1 to #4, `multi-tour-operations-cost-api` #1 to #19, `multi-tour-operations-cost-portal` #20 to #30, `multi-tour-infra-postgres` #25 to #27, relay #13 (2026-11-07 22:22 to 2026-11-08 14:25): https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/10 · Docs #798 to #814: https://github.com/code-corhuila/multi-tour-docs/pull/814 · In Review: waits on the teammate's services |
| HU-SEC-004 | Recover the password (`#157`): the recovery code table, ask for a code and reset the password with it in one statement, send the code by e-mail, the key read at start, the two screens from the sign-in, the recovery facts routed to the audit, end-to-end run and pillar review | done | `multi-tour-security` #88 to #94, portal #71 to #74, infra #28, relay #14 (2026-11-08 14:19 to 16:34): https://github.com/code-corhuila/multi-tour-security/pull/92 · Docs #815, #816: https://github.com/code-corhuila/multi-tour-docs/pull/816 |
| HU-OPS-002 | Assign and change the vehicles of a departure (`#188`), part A: the vehicle tables, register and list the vehicles of a tenant, assign vehicles under the lock of the departure, the effective capacity, the vehicles section of the portal, the vehicle change routed to commercial-reservation | doing | operations-cost-db #5, #6, api #20 to #25, portal #31 to #34, relay #15, #16 (2026-11-08 17:37 to 20:09): https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/23 · Docs #817 to #819: https://github.com/code-corhuila/multi-tour-docs/pull/819 |
| HU-OPS-003 | Record the start and the end of a departure (`#189`), parts A and B: start, finish and interrupt with two events, the permissions of the token open the execution routes, the start alert, the mark of pending regularization and the regularization; the portal list, page and panels; the container hands the permissions to the portals | doing | operations-cost-db #7, #9, #10, api #26 to #39, portal #35 to #44, `multi-tour-front` #51, relay #17 to #19 (2026-11-08 21:16 to 2026-11-09 03:14): https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/31 · Docs #820 to #825: https://github.com/code-corhuila/multi-tour-docs/pull/825 |
| HU-WRK-003 | Watch the departure deadlines (`#208`): the job 3 worker that asks operations-cost to mark the departures past their end, with a bounded batch and its own service token in the root composition | done | `multi-tour-worker-departure-deadlines` #1 to #4, infra #29 (2026-11-09 02:31 to 08:24): https://github.com/code-corhuila/multi-tour-worker-departure-deadlines/pull/2 · Docs #823 |
| HU-WFL-001 | Onboard a tenant and its first administrator (`#205`): the saga resumes a saga and claims a stale one only once | doing | `multi-tour-workflow-onboard-tenant` #13 (2026-11-02 17:00): https://github.com/code-corhuila/multi-tour-workflow-onboard-tenant/pull/13 |
| HU-WRK-007 / HU-IDA-002 | Relay routes to commercial-reservation, the actor and the reason of an event, and the audit filter that accepts the actions of other services (specs 007 and 008) | doing | relay #20 to #22, identity-audit-api #66, #67 (2026-11-09 07:04 to 07:51) · Docs #826 to #830: https://github.com/code-corhuila/multi-tour-docs/pull/830 |
| HU-SEC-008, HU-OPS-004 | Specified at the end of the window, built next week: the internal role for a final customer account (Docs #831, 08:25) and the delivered and undelivered services (Docs #832, #833, 08:31) | todo | https://github.com/code-corhuila/multi-tour-docs/pull/831 · https://github.com/code-corhuila/multi-tour-docs/pull/833 |

## 2. My individual contribution
Chronological, local time (UTC-5), from Monday 2026-11-02 08:40 to Monday 2026-11-09 08:40. 250 pull requests under my account: 69 in Docs, 41 in `multi-tour-security`, 39 in `multi-tour-operations-cost-api`, 25 in `multi-tour-operations-cost-portal`, 23 in `multi-tour-identity-audit-portal`, 16 in `multi-tour-identity-audit-api`, 12 in `multi-tour-worker-event-relay`, 10 in `multi-tour-operations-cost-db`, 5 in `multi-tour-infra-postgres`, 4 in `multi-tour-worker-departure-deadlines`, 3 in `multi-tour-identity-audit-db`, 2 in `multi-tour-front` and 1 in `multi-tour-workflow-onboard-tenant`. Every story followed the same order: spec signed in Docs, contract, test first (`test(...)` red), code (`feat(...)` green), evidence and pillar review in Docs.
- **2026-11-02, afternoon.** Closed the portal part of HU-SEC-003 (the page behind the e-mail link) and the saga resume of the onboarding.
- **2026-11-03, early morning.** Closed HU-IDA-003 with its refactors, evidence and pillar review, and split the logo and images into HU-IDA-005.
- **2026-11-03 to 2026-11-04.** Built HU-SEC-005 in `security` (18 PRs) and split its AC3 into HU-SEC-008.
- **2026-11-04.** Built HU-SEC-006, special permissions and optional roles (13 PRs).
- **2026-11-06 to 2026-11-07.** Built the audit of HU-IDA-002 in the three identity-audit repositories and the relay route for the events with no tenant.
- **2026-11-07, night.** Added the test of the security story #586 (week 13 backlog).
- **2026-11-07 to 2026-11-08.** Started `operations-cost-api` and `-db` from empty repositories and closed my side of HU-OPS-001, with coverage and format gates in `mvn verify` and ESLint and Prettier in the portal CI.
- **2026-11-08, afternoon.** Built HU-SEC-004, password recovery, end to end.
- **2026-11-08 to 2026-11-09.** Built part A of HU-OPS-002, parts A and B of HU-OPS-003, and the job 3 worker of HU-WRK-003.
- **2026-11-09, morning.** Specified the relay routes and the audited events (specs 007 and 008), HU-SEC-008 and HU-OPS-004.
- **Weekly challenges.** Wrote the two challenges of week 14 as documents that map our Definition of Done to the gates that exist.
- **Not counted:** the commits of the teammate, and anything after 2026-11-09 08:40, which is in week 15.

## 3. Blockers and risks
- **GitHub Actions of the organization is still locked by billing** (since 2026-10-14). Every `ci.yml` job fails in 2 seconds with "The job was not started because your account is locked due to a billing issue". The team keeps merging with the risk accepted, and every PR records the failed job and the local checks that replace it (`mvn -B verify`, `go test ./...` with `gofmt -l .` and `go vet ./...`, `npm ci && npm test && npm run build`).
- **The gates exist but cannot block.** Build, format, lint, tests, coverage floor, contract validation and image build are in the `ci.yml` files, but with Actions locked none of them stops a merge today.
- **No security gate in the CI of the team repositories.** `gitleaks`, `govulncheck` and `npm audit` ran only in my fork in week 13.
- **Many stories wait in `In Review`.** HU-SEC-005, HU-SEC-006, HU-OPS-001 and HU-OPS-003 are finished on my side, and a criterion of each needs a service of my teammate (commercial-reservation, cash-reporting) that is not built or not verified. Per the team rule they are not marked done.
- **The volume of pull requests.** 250 PRs in one week keeps each one under 400 visible lines, but it is a lot for the reviewer.
- **The branch protections are the professor's.** I cannot read the organization rulesets with my token (the branch rules endpoint answers an empty list), so I do not claim what they enforce.

## 4. Plan for next week
- Finish HU-SEC-008, HU-SEC-007 (support and emergency access), HU-OPS-004, HU-OPS-007, HU-OPS-005, HU-OPS-006 and HU-OPS-008, and part B of HU-OPS-002 with its saga.
- Build the notifications worker (HU-WRK-004) and the retention worker (HU-WRK-006).
- Promote the finished stories to `qa` with `chore/promote-<HU>` branches and `git cherry-pick -x`.
- Prepare the release of week 15 for `main` with its five elements, for the professor to approve.
- Complete the test suite as far as the project allows (contract tests, a load test, a chaos experiment), as the week 15 challenge asks.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - the titles of the 250 PRs follow it (`feat(core)`, `feat(http)`, `test(http)`, `docs(07-api)`, `chore(ci)`, `refactor(...)`, `style(portal)`), and the squash keeps only the title in `develop`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - every code PR of the week goes from a `feat/`, `fix/`, `chore/` or `docs/` branch to `develop`; Docs goes from `docs/` branches to `main`. Nothing was promoted to `qa` this week
- [x] Testable acceptance criteria - every spec of the week has its "Tests" section naming the test of each criterion (for example Docs #685 for HU-SEC-005)
- [x] Tests added/updated (unit / integration) - each story has `test(...)` before `feat(...)`; for example `multi-tour-security` #58 (end to end, a registered user signs in only to choose a password) and #83 (no route creates a permission); the CI did not run them (locked), the local checks did
- [x] DDD / hexagonal boundaries respected (domain has no I/O) - `operations-cost-api` has a `core` module that does not declare Spring, so a `@Service` in the domain does not compile; the domain holds a JaCoCo floor of 0.95 (`core/pom.xml`)
- [x] No secrets; config via environment variables - the recovery code key and the service token of job 3 come from the environment (infra #28, #29) and only `.env.example` files are versioned

## 6. Evidence links
- Class summary image for week 14 (CI/CD gates and planning): [WeeklySummary/Week-14.webp](./WeeklySummary/Week-14.webp)
- Weekly Challenge week 14, Session 1 (set up your CI/CD): [WeeklySummary/WeeklyChallenge/CI-CD-Gates-Multitour.md](./WeeklySummary/WeeklyChallenge/CI-CD-Gates-Multitour.md)
- Weekly Challenge week 14, Session 2 (DoD to gates, protections, per-service pipelines, MVP 3 slicing): [WeeklySummary/WeeklyChallenge/DoD-Gates-And-Pipeline-Stories-Multitour.md](./WeeklySummary/WeeklyChallenge/DoD-Gates-And-Pipeline-Stories-Multitour.md)
- Board: https://github.com/orgs/code-corhuila/projects/28
- Password recovery end to end: https://github.com/code-corhuila/multi-tour-docs/pull/816
- Internal users of a tenant: https://github.com/code-corhuila/multi-tour-security/pull/69
- Special permissions in the token: https://github.com/code-corhuila/multi-tour-security/pull/84
- Audit lists of the platform and the tenant: https://github.com/code-corhuila/multi-tour-identity-audit-api/pull/59
- First route of operations-cost: https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/10
- Coverage floor in `mvn verify`: https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/18
- ESLint and Prettier in the portal CI: https://github.com/code-corhuila/multi-tour-operations-cost-portal/pull/30
- Start and end of a departure: https://github.com/code-corhuila/multi-tour-operations-cost-api/pull/31
- Job 3 worker: https://github.com/code-corhuila/multi-tour-worker-departure-deadlines/pull/2
- A failed CI job (billing lock): https://github.com/code-corhuila/multi-tour-security/actions/runs/41948354697/job/126986049310
