# Weekly Challenge - Week 15, Session 2: ship MVP 3

**The challenge:** promote to `main`, tag `v3.0.0`, verify the operability checklist (load, chaos, rollback,
security), and demo the system under stress and failure.

**Scope:** the state on 2026-11-14 and the plan of the release. `main` is production: it changes only through a
`release/` branch with the approval of the professor (norm §12, golden rule 6), so the release is prepared, not merged.

## 1. Where the code is

| Step | State on 2026-11-14 |
|---|---|
| Finished stories in `develop` | Yes, every story of my repositories (see the hu-status of weeks 14 and 15) |
| Promotion to `qa` | Done on 2026-11-13 and 2026-11-14: 147 PRs, `chore/promote-<HU>` branches with `git cherry-pick -x` |
| `release/3.0.0` to `main` | Not opened yet |
| Tag `v3.0.0` | Not created; the last tag is `v2.0.0` (2026-10-15) |

## 2. How the release is done (norm §11)

1. In each repository, `release/3.0.0` cut from `main`.
2. One `git cherry-pick -x` per story validated in `qa`, citing the SHA that exists in `develop`, checked with
   `git merge-base --is-ancestor <sha> origin/develop`.
3. A PR to `main` with the five elements: the stories with id and title, the `cherry picked from` trace of each, the
   declared scope (what goes in and what stays out), the deployment instructions (the migrations of each `-db`, the new
   variables) and the rollback plan. Merge method: **Create a merge commit**, to keep one commit per story.
4. After the approval of `@ariel5253`: `git tag -a v3.0.0`.
5. Before: merge `multi-tour-infra-postgres` #45, so the development key and token scripts never reach `main`
   (norm §5.5.2).

## 3. Operability checklist

| Item | How it is checked | State |
|---|---|---|
| **Backup before migrating** | `infra-postgres` #47 backs up the platform before migrating; `infra-mongo` #5 backs up and restores MongoDB with the oplog. In `qa` and `main` the volume is never removed (`down -v` is forbidden, Annex J J.5.4) | Built, merged in `develop` |
| **Rollback** | Restore the backup taken before the migration and run the `v2.0.0` images; each Liquibase migration has its rollback in `05_rollbacks` and `db-ci` proves a total rollback | Built; not rehearsed on `qa` |
| **Migrations** | Every runner applies and a second run applies nothing (`infra-postgres` #46) | Built |
| **Load** | k6, 40 r/s for 1 minute through the gateway on dev (session 1 document) | Run on dev, SLO met (p95 9.69 ms, 0 % failed); not run on `qa` |
| **Chaos** | Stop the saga `onboard-tenant`: its route `503`, the others `200`, recovery with no gateway restart | Run on dev, as expected; not run on `qa` |
| **Security** | RS256 only in every service, gateway as the only published port, internal routes closed at the edge, no `.env` versioned; no `gitleaks` or `govulncheck` gate in CI yet | Partial |
| **CI green** | Actions locked by billing; local checks recorded in each PR | Blocked by the organization |

## 4. Demo under stress and failure

The demo is the two runs of 2026-11-14 on the dev stack, with their scripts and real output in
[15-week/challenge/RESULTS.md](../../../challenge/RESULTS.md): the system under 40 requests per second kept a p95 of
9.69 ms with no failure, and with one service down only that service's route answered `503` while the rest kept
working. The same demo on `qa`, and the release itself, wait for the professor's approval of `main`.
