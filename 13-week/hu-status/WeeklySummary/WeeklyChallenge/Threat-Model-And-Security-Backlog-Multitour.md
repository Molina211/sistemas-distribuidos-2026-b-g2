# Weekly Challenge - Week 13, Session 2: threat model and security backlog

**The challenge:** build a threat model (data flow, trust boundaries and STRIDE), turn the findings into a
prioritized security backlog with testable criteria, and add security gates (dependencies, secrets, authorization
tests) to CI.

**Scope of this document: it records work that already exists, it adds no code.** The threat model, the backlog and
the gates below were produced in the week 13 workshop (individual, 2026-10-29) for one trust boundary of Multitour,
`multi-tour-api-gateway` to `multi-tour-security`. The rest of the system is not modeled here: other boundaries are
listed as open in the attack surface map. Changing the repositories of the services belongs to the user stories, with
their spec and pull request, so what is missing is stated in section 4 and not built.

## 1. Threat model of the boundary

| Piece | What exists | Where |
|---|---|---|
| Data flow and trust boundaries | A Mermaid diagram with 5 zones (Internet, platform edge, internal network, data, external e-mail service) and 5 trust boundaries, TB1 to TB5. TB2 (gateway to `multi-tour-security`) is highlighted. It shows that there is no message broker: events go through an outbox and a relay | `13-week/workshop/01-attack-surface.md` |
| Assets and entry points | 8 assets with a classification (RS256 private key, password hashes, access and refresh tokens, temporary credential, e-mail verification link, memberships, event, tenant link) and 9 entry points with how each authenticates today. 6 of the 9 take no token | same file |
| STRIDE with a score | 6 threats, one per letter, each with likelihood (1-3) x impact (1-3), the control that exists and the mitigation that is missing | Docs `05-architecture/security/stride-Molina211.md`, [PR #579](https://github.com/code-corhuila/multi-tour-docs/pull/579), corrected in [PR #587](https://github.com/code-corhuila/multi-tour-docs/pull/587) |
| Residual risk | I accept T (risk 3, plain HTTP inside the internal network) and R (risk 4, no audit evidence event from security) for now, each with the reason | same file, section 5 |
| Peer review | A concrete comment on a row of the STRIDE of the partner who models the boundary gateway to `multi-tour-commercial-reservation-api` | [comment on PR #580](https://github.com/code-corhuila/multi-tour-docs/pull/580#discussion_r4414101698) |

| Letter | Threat in the boundary | L x I | Risk |
|---|---|---|---|
| E | A token of one tenant reads or changes the memberships of another tenant | 2 x 3 | 6 |
| S | Credential stuffing against the sign-in route | 2 x 3 | 6 |
| I | E-mail enumeration through the registration answer (row marked [B], from a library book) | 3 x 2 | 6 |
| D | A stranger keeps an administrator account locked with five failed sign-ins | 3 x 2 | 6 |
| R | No audit evidence from the security service | 2 x 2 | 4 |
| T | A process inside the network changes a request, because the hop is plain HTTP | 1 x 3 | 3 |

## 2. Prioritized security backlog

Priority comes from the matrix of the workshop (risk 9 is P0, 6 is P1, 3 to 4 is P2, 1 to 2 is P3), not from opinion.
No threat scored 9, so there is no P0.

| Issue | Threat | Risk | Priority | Criteria |
|---|---|---|---|---|
| [#584](https://github.com/code-corhuila/multi-tour-docs/issues/584) A token of one tenant never reaches the memberships of another tenant | E | 6 | P1 | 3, one is an automated negative test |
| [#585](https://github.com/code-corhuila/multi-tour-docs/issues/585) Password guessing on sign-in is slowed down and seen | S | 6 | P1 | 4, with a proposed alert threshold |
| [#586](https://github.com/code-corhuila/multi-tour-docs/issues/586) Registration does not tell a stranger which e-mails have an account | I | 6 | P1 | 4, cites the book with its pages |

Each issue has the labels `security` and its priority, a story in the form "As a ... I want ... so that ...", the
STRIDE letter and the risk, a link to the PR of the threat model, and it sits in the project board 28 in Backlog.

## 3. Security gates in CI

| Gate | Result in the run | Notes |
|---|---|---|
| Secrets (`gitleaks`) | passed, no leaks | scans the full history |
| Dependencies (`govulncheck`) | **failed with 3 real findings** | `golang.org/x/text@v0.24.0` (`GO-2026-6629`, `GO-2026-5970`) and `github.com/jackc/pgx/v5@v5.7.6` (`GO-2026-5004`); a finding fails the build, as the session asks |
| Authorization (negative test) | passed | a valid token of tenant A, as administrator or customer, gets `403` on the service-only route of tenant B in `multi-tour-security`, and the use case never runs |

The workflow is `13-week/workshop/04-security-ci/security.yml`, with the test and a `HOW-TO-RUN.md` beside it. I saw it
run by hand on 2026-10-29: [run 40943113682](https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/actions/runs/40943113682).
It runs in my fork because the Actions of the organization are locked by billing.

## 4. What is missing, stated as it is

| Gap | Effect |
|---|---|
| **The gates are not in the CI of the team repositories.** I found 20 `ci.yml` files in the distributed repositories and none runs `gitleaks`, `govulncheck`, `npm audit` or a dependency check | The finding of `govulncheck` is known only because of this workshop; no repository fails a build for it today |
| **The 3 vulnerabilities are not fixed.** Raising `pgx` and `x/text` is a change to `multi-tour-security` | The security service keeps the known weakness until its story is worked |
| **One boundary only.** TB1, TB3, TB4, TB5 and the other TB2 boundaries are open in the map; one is claimed by a partner | The system as a whole has no complete threat model |
| **Threat D has risk 6 and no story yet.** I wrote stories for E, S and I | A P1 threat is not in the backlog |
| **All three stories are P1.** The matrix gives the same priority to equal scores | The order among them is not decided |
| **The negative test covers one case.** It uses a route that exists; the memberships routes and `POST /security/users` are in the contract and not in the service | The criteria about memberships cannot be tested until those routes are built |
| **The negative test was never seen failing.** It passed with the current code; I did not remove the protection to see it fail | Its power to catch a regression is assumed, not shown |

## 5. How this maps to the session

| The session asks | Where it is |
|---|---|
| Map and analyze, mark the trust limits | Section 1, attack surface map |
| STRIDE, each threat mapped to a mitigation | Section 1, STRIDE table with the missing mitigation per row |
| Backlog by risk, with owners and criteria | Section 2 (the owner of the three stories is the author of the workshop; no other owner is assigned) |
| Security in CI, a finding fails the build | Section 3 |
| Do not forget to prioritize by impact x likelihood | Risk = L x I on every row |
