# Weekly Challenge - Week 12, Session 2: deployment plan for MVP 3

**The challenge:** the deployment plan for MVP 3: artifact promotion, IaC for the environments, the chosen
zero-downtime strategy, a rehearsed rollback (documented, timed), and the deployment stories sliced with
testable acceptance criteria.

**Scope of this document.** It is a **plan**. The rollback was **not rehearsed and no time was measured**: the
timing table in section 4 is empty on purpose. The strategy is the one proposed in
[Zero-Downtime-Deployment-Multitour.md](./Zero-Downtime-Deployment-Multitour.md), which was not run either.
Facts about the repositories were read on 2026-10-26.

## 1. Artifact promotion

One artifact per service, built once from a release tag and promoted unchanged; only the variables file
changes between environments.

| Step | Rule | State today |
|---|---|---|
| Code reaches `develop` | `feat/` PR, one approval of another member, CI green | CI is red for the billing lock of the organization; PRs document it |
| Promote to `qa` | `qa/<description>` branch, `git cherry-pick -x`, PR, tests in the `qa` stack | Done for the work of the second cut |
| Release to `main` | `release/x.y.z` cut from `main`, one commit per validated story, teacher's approval, then `git tag -a vX.Y.Z` | Done for 2.0.0 (13 repositories) |
| Build the image | From the tag, named with the **same** version as the tag | **Gap:** images are named `0.1.0` and built by hand; no registry |
| Deploy | The same image in `qa` and `main`, with `env/<environment>.env` | Compose only; no host besides the developer's machine |

Rule I would adopt: an image tag equals the Git tag (`multi-tour-security:2.0.0` for `v2.0.0`), and a tag
that exists is never moved.

## 2. Infrastructure as code for the environments

| Environment | What is code today | What is missing |
|---|---|---|
| dev, qa, main | `platform.yml` (the root composition) in `multi-tour-infra-postgres`, `scripts/up.sh` and `down.sh`, `env/*.env.example` per environment, `COMPOSE_PROJECT_NAME` `multi-tour-dev`/`-qa`/`-main` so each has its own containers and volume, the single PostgreSQL and MongoDB instances | Hosts: nothing provisions a machine. No registry. No backup job (the norm asks to back up before migrating in `qa` and `main`) |
| Secrets | Not versioned: `.env`, `keys/`, `*.pem` are ignored; `JWT_PUBLIC_KEY` and the service tokens come from the environment | In `qa` and `main` they would come from the identity service as secrets of the environment; no store exists |
| Cloud | `05-architecture/deployment.md` in Docs is a reference guide for AWS | Nothing written as Terraform or similar; ADR-004 D9 says the course runs on Compose and the company operates the cloud |

Decision: **no Terraform in this cut.** Compose files are the IaC the team can run and test, and a cloud
module nobody can apply would be an untested document. It is a debt, listed in section 6.

## 3. Strategy chosen

Blue-green by container name, switched at the gateway with a reload. Reasons and the discarded alternatives
(rolling by replicas, stop-and-start) are in the first document, section 3. It depends on two things that do
not exist yet: the image tag rule above and a `healthcheck` in the gateway and the front.

Constraint from the data: both colours share one schema, so every migration is expand-then-contract across
two releases, and the volume of `qa` and `main` is never removed (`down.sh` refuses `-v`).

## 4. Rollback

**Procedure (documented, not rehearsed):**

1. The new colour fails its health check or its post-switch checks. Detection: `--wait` fails, or the proof
   loop counts `5xx`.
2. Point the route in `nginx/routes/` back to the old service name; `nginx -s reload`. The old container was
   kept running, so no start is needed.
3. Remove the failed colour with `docker compose rm -sf <service>-green`. Never `down -v`.
4. If a migration of the release already ran: it is expand-only by rule, so the old code works on the new
   schema and nothing is reverted. A destructive migration is not allowed in the same release.
5. Record the incident: which check failed, the log lines by `X-Correlation-Id`, and a `fix(...)` story.

**Timing (to be filled when rehearsed):**

| Step | Seconds |
|---|---|
| Detect the failure | not measured |
| Switch the route back and reload | not measured |
| Remove the failed colour | not measured |
| Total until the old version serves all traffic | not measured |

Target I propose, not measured: under 60 seconds from detection to the old version serving, since step 2 is
a reload and not a start.

## 5. Deployment stories with acceptance criteria

| ID | Story | Acceptance criteria (each is a check) |
|---|---|---|
| DEP-01 | As the team, the image tag equals the release tag | Given tag `v2.0.0`, the built image is named `...:2.0.0`; a script fails if the Compose file names another tag |
| DEP-02 | As the team, an image is pulled from a registry | Given a tag, `docker pull` on a clean host gets it; no `build:` is needed to run `qa` |
| DEP-03 | As an operator, I wait for health before switching traffic | Given a green container whose check never passes, `up --wait` exits non-zero and the route is unchanged |
| DEP-04 | As a user, a release does not drop my requests | Given a loop of requests during the switch, the count of `5xx` and connection errors is 0 |
| DEP-05 | As an operator, I can go back | Given a bad release, the old version serves all traffic in the time measured in section 4, and the volume is untouched |
| DEP-06 | As the team, gateway and front have a health check | `docker compose ps` shows both `healthy` |
| DEP-07 | As the team, `qa` and `main` data is backed up before a migration | A backup file exists and restores into an empty instance |

Dependency order: DEP-01, DEP-06, DEP-03, DEP-02, DEP-04, DEP-05, DEP-07. The stories are written but not
created in the board.

## 6. Declared debts

- No rollback was rehearsed or timed; no zero-downtime run exists.
- No registry, no second host, no cloud, no IaC beyond Compose, no backup job.
- GitHub Actions is blocked, so no pipeline builds or tests an image; CI/CD is the next challenge.
- The 60-second target and the story order are mine and not approved by the team.
