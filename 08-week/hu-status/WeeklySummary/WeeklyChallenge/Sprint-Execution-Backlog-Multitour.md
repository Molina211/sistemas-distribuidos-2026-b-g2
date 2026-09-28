# Session 1 - Run the sprint: prioritized backlog, WIP limit, PRs, daily sync and throughput

## Objective

Run the sprint like a real one: a prioritized backlog with testable stories, a WIP limit,
a pull request for every change, a daily sync, and a throughput measurement.

## 0. What really happened (read this first)

Week 08 was a documentation-only week (no new product code). The team did **not** run a
formal sprint process while the work was being done:

- There was **no board and no backlog** tracking the week's work.
- There was **no daily sync**. The only synchronization points were the two class sessions
  (Monday and Thursday).
- The WIP limit was **not tracked or enforced** during the week.

What did exist during the week is the pull request history in the Docs repository: every
change went through a branch and a PR (section 4). This document therefore does two
things, and keeps them separate:

1. **Reconstructs** the week from real evidence (the PRs and their timestamps), entered
   into a backlog on **2026-09-27** as retroactive entries, marked as such.
2. **Sets up** the process from now on (board, WIP limit, sync proposal) so Sprint 09 can
   actually run this way.

Nothing below claims that the WIP limit or a daily sync were followed in week 08.

## 1. Board

Practice environment, separate from the official course backlog and from
`04-requirements/user-stories.md` (which is not touched):

| Item | Link |
|---|---|
| Repository (issues) | https://github.com/Molina211/Travesia-Natural-docs |
| Project board | https://github.com/users/Molina211/projects/3 |
| Milestone "Sprint 08" | https://github.com/Molina211/Travesia-Natural-docs/milestone/1 |

- **Sprint goal:** Document the MVP 2 microservices architecture (ADR-004/005/006) and the
  BPMN process set, so implementation can start.
- **States:** `status:todo` -> `status:doing` -> `status:done` (labels, mirrored as
  Todo / In Progress / Done columns in the project).
- **Priority:** labels `P1` (highest) to `P5`.
- **Type:** `type:user-story` (product stories) and `type:enabler` (documentation and
  architecture work that supports the sprint goal).

## 2. Prioritized backlog

Product stories are taken from the existing backlog in Docs (`user-stories.md`), the
ones that are still pending or in progress. Priority follows dependencies: HU-RES-006 is
already in progress, HU-EXEC-002 must exist before HU-RES-009, and HU-IAM-003 needs
outbound email (not available yet).

| Priority | Issue | Type | Status | Depends on |
|---|---|---|---|---|
| P1 | [#1 HU-RES-006 - Create a reservation from the digital channel](https://github.com/Molina211/Travesia-Natural-docs/issues/1) | user story | todo | HU-IAM-002, HU-RES-005, HU-RES-003 |
| P2 | [#2 HU-RES-004 - Validate lodging capacity when reserving](https://github.com/Molina211/Travesia-Natural-docs/issues/2) | user story | todo | HU-RES-001, HU-CAT-001 |
| P3 | [#3 HU-EXEC-002 - Block ordinary adjustments during execution](https://github.com/Molina211/Travesia-Natural-docs/issues/3) | user story | todo | HU-EXEC-001 |
| P4 | [#4 HU-IAM-003 - End customer recovers their password](https://github.com/Molina211/Travesia-Natural-docs/issues/4) | user story | todo | HU-IAM-001 |
| P5 | [#5 HU-RES-009 - Reschedule an affected reservation or service](https://github.com/Molina211/Travesia-Natural-docs/issues/5) | user story | todo | HU-EXEC-002, HU-CASH-003 |
| P1 | [#10 Align overview, API docs and event relay with the ADRs](https://github.com/Molina211/Travesia-Natural-docs/issues/10) | enabler | **doing** | #6 |
| P1 | [#6 Record architecture decisions ADR-004/005/006](https://github.com/Molina211/Travesia-Natural-docs/issues/6) | enabler | done (retroactive) | - |
| P2 | [#7 Model business processes as BPMN (BPMN-07 to BPMN-10)](https://github.com/Molina211/Travesia-Natural-docs/issues/7) | enabler | done (retroactive) | - |
| P3 | [#8 Close API contract decisions per service](https://github.com/Molina211/Travesia-Natural-docs/issues/8) | enabler | done (retroactive) | - |
| P4 | [#9 Sync the PDR to v1.8 and v1.9](https://github.com/Molina211/Travesia-Natural-docs/issues/9) | enabler | done (retroactive) | - |

Story points are left as "pending" on purpose: they are estimated with planning poker in
Session 2.

### Testable stories

Every issue is written as "As a / I want / so that" and carries its acceptance criteria in
Gherkin (Given / When / Then), two scenarios each, plus its dependencies and a link to its
definition of done. The five product stories copy the acceptance criteria already
approved in Docs; the five enablers have criteria written for this challenge, each
verifiable by looking at a merged PR. Example, HU-RES-004 (issue #2):

```gherkin
Scenario 2: Insufficient capacity
  Given the reservation's headcount exceeds the selected lodging's available capacity
  When  confirming the reservation is attempted
  Then  the system warns of insufficient capacity and does not allow confirmation
```

## 3. WIP limit

- **Policy from Sprint 09: at most 2 stories in `status:doing` at the same time.** A third
  one may start only after one moves to `done`. WIP counts stories, not PRs: one story can
  have several PRs.
- **Current state (2026-09-27):** 1 story in doing (#10, three open PRs), which is within
  the limit.
- **Week 08:** WIP was not tracked, so no claim is made about it.

## 4. Pull requests for every change

Docs (`code-corhuila/multi-tour-docs`) accepts changes to `main` only through a branch and
a pull request, with the teacher's approval as reviewer (rules in the Docs repository,
`00-governance/branching-policy.md`). All 20 PRs below are from this week (Monday
2026-09-21 to Sunday 2026-09-27, local time UTC-5; three merged in the first minutes of
2026-09-28 UTC but on Sunday evening local time).

| State | PRs |
|---|---|
| Merged (16) | [#13](https://github.com/code-corhuila/multi-tour-docs/pull/13), [#15](https://github.com/code-corhuila/multi-tour-docs/pull/15), [#17](https://github.com/code-corhuila/multi-tour-docs/pull/17), [#24](https://github.com/code-corhuila/multi-tour-docs/pull/24), [#31](https://github.com/code-corhuila/multi-tour-docs/pull/31), [#32](https://github.com/code-corhuila/multi-tour-docs/pull/32), [#35](https://github.com/code-corhuila/multi-tour-docs/pull/35), [#40](https://github.com/code-corhuila/multi-tour-docs/pull/40), [#43](https://github.com/code-corhuila/multi-tour-docs/pull/43), [#44](https://github.com/code-corhuila/multi-tour-docs/pull/44), [#45](https://github.com/code-corhuila/multi-tour-docs/pull/45), [#46](https://github.com/code-corhuila/multi-tour-docs/pull/46), [#50](https://github.com/code-corhuila/multi-tour-docs/pull/50), [#52](https://github.com/code-corhuila/multi-tour-docs/pull/52), [#53](https://github.com/code-corhuila/multi-tour-docs/pull/53), [#54](https://github.com/code-corhuila/multi-tour-docs/pull/54) |
| Closed without merge (1) | [#26](https://github.com/code-corhuila/multi-tour-docs/pull/26) (BPMN-08, replaced by #31) |
| Open (3) | [#55](https://github.com/code-corhuila/multi-tour-docs/pull/55), [#56](https://github.com/code-corhuila/multi-tour-docs/pull/56), [#57](https://github.com/code-corhuila/multi-tour-docs/pull/57) (story #10) |

Mapping to stories: #6 = PRs #52, #53, #54; #7 = #13, #24, #31, #43, #44, #45, #46;
#8 = #32, #35, #40; #9 = #15, #17, #50; #10 = #55, #56, #57. Each issue lists its PRs.

## 5. Daily sync

**There was no daily sync in week 08.** The real cadence was the class sessions on Monday
and Thursday, and nothing else (no weekly or monthly meeting either).

Proposal for Sprint 09, not yet in place: a short written daily note (done yesterday /
doing today / blocked) as a comment on one pinned issue in the board repository, so the
sync leaves evidence. It costs a few minutes a day and works for a team that does not
meet daily.

## 6. Throughput

Measured from the PRs and issues above (Sprint 08, 2026-09-21 to 2026-09-27):

| Metric | Value |
|---|---|
| Stories in the sprint | 10 (5 product stories + 5 enablers) |
| Stories done | 4 (all enablers, entered retroactively) |
| Stories in progress | 1 (#10, three open PRs) |
| Product stories done | 0 of 5 (documentation-only week, no product code) |
| PRs merged | 16 in 7 days (about 2.3 per day) |
| PR cycle time (opened to merged) | median 1.2 h, mean 4.2 h; slowest is #13 at about 35 h (cause not recorded) |

Read this carefully:

- Throughput in **stories** is only 4, and they are enablers. This is not a velocity yet:
  the stories are not estimated, and a velocity needs story points from Session 2 plus at
  least one sprint that ran with the process.
- The mean cycle time is pulled up by one outlier (#13); the median is the better figure.

## 7. Gaps found and next steps

| Gap | Next step (Sprint 09) |
|---|---|
| No board during the week | Keep the board open from day 1 and move stories as they start and finish |
| No WIP tracking | Apply the limit of 2 and record it |
| No daily sync | Try the written daily note on a pinned issue |
| Board holds only the retroactive week | Estimate the backlog in Session 2 (planning poker) and commit the MVP 2 scope from a real velocity |

The official backlog (`04-requirements/user-stories.md`) is intentionally left untouched:
it is being reviewed folder by folder and will be updated separately.
