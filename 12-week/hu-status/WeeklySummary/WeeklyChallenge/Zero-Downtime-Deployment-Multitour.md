# Weekly Challenge - Week 12, Session 1: zero-downtime deployment

**The challenge:** deploy the system to a cloud environment (or a production-like host) using the tagged
image, with a zero-downtime strategy (rolling or blue-green) and health checks.

**Scope of this document: it is NOT a deployment.** Nothing was deployed to a cloud or to a second host, and
no zero-downtime run was made. The challenge asks for pieces the project does not have yet (section 2), so
this document records what exists today, checked by reading the repositories on 2026-10-26, and the
smallest design that would meet the challenge with them. Everything in sections 3 and 4 is a **proposal**; no
command in them was run.

## 1. What exists today (read from the repositories)

| Piece | State | Where |
|---|---|---|
| Tagged release | Tag `v2.0.0` exists in the repositories I checked: `security`, `front`, `api-gateway`, `infra-postgres`, `identity-audit-api` | `git tag` in each repository |
| One composition per environment | `scripts/up.sh dev\|qa\|main [--check] [--demo]` brings up the instances, then the migration runners, then the services, and waits until they are healthy. `scripts/down.sh` keeps the volumes and refuses `-v` | `multi-tour-infra-postgres/scripts/` |
| Variables per environment | `env/dev.env.example`, `qa` and `main`; the real `.env` files are ignored by Git | `multi-tour-infra-postgres/env/` |
| Health checks in Compose | PostgreSQL (`pg_isready`), MongoDB, the `security` service (`GET /health` on 8086, every 10 s, 5 retries) and the relay | `compose.yml` of `infra-postgres`, `infra-mongo`, `security`, `worker-event-relay` |
| Health route without a Compose check | The gateway answers `GET /health` (`nginx/routes/_health.conf`), but its `deploy/compose.yml` declares no `healthcheck`; the same holds for the front glue file | `multi-tour-api-gateway`, `glue/front.yml` |
| Gateway that survives a missing service | NGINX resolves names per request (`resolver 127.0.0.11 valid=10s`), so a service that is down answers `503` for its route and the others keep working | `multi-tour-api-gateway/nginx/conf.d/00-resolver.conf` |
| Only the gateway is published | The other services use `expose`, not `ports` | the `compose.yml` files |
| Images pinned | Third-party images carry an exact version (`postgres:18.6-alpine`, `axllent/mailpit:v1.21.8`) | `compose.yml` files |

## 2. What the challenge needs and the project does not have

| Missing | Effect |
|---|---|
| **A registry.** The images are built on the machine (`build:` in Compose) and named `multi-tour-security:0.1.0` or `multi-tour-front:0.1.0`. Git tags say `v2.0.0`, the image tags say `0.1.0`: the image tag is not the release tag | There is no "tagged image" to pull on another host, and the two numbers disagree |
| **A host other than my machine,** and any cloud account. ADR-004 D9 says the course runs on Docker Compose and the team does not operate a cloud | "Deploy to a cloud environment" cannot be done; a second Docker host would be the minimum |
| **Two copies of a service.** Every service is one container. Nothing in the compositions runs `replicas` or two versions side by side | Neither rolling nor blue-green can be shown today |
| **A pipeline.** `ci.yml` runs only on pull requests, and GitHub Actions is locked by the billing of the organization since 2026-10-14. Nothing builds or pushes an image | The artifact is built by hand |
| **A Compose check on the gateway and the front** | A traffic switch could not wait for "healthy" on those two |

## 3. Design that fits what exists (proposal, not run)

**Strategy: blue-green by container name, switched at the gateway.** It needs the least new code because the
gateway already resolves names per request.

1. The new version is started next to the old one under another service name (`security-green` beside
   `security`), from an image tagged with the release (`multi-tour-security:2.0.0`, built from tag
   `v2.0.0`), on the same `platform` network and with its own database user unchanged (same schema).
2. `docker compose up -d --wait` returns only when the new container's `healthcheck` is healthy. A failed
   check stops the switch before any traffic moves.
3. The route in `nginx/routes/` points at the new name and `nginx -s reload` applies it. NGINX finishes the
   requests in flight on the old workers, which is what makes the switch free of dropped requests.
4. The old container stays up, idle, until the new version has served the checks below; then it is removed.

Why not rolling with two replicas of the same name: the project has no orchestrator, and plain Compose gives
round-robin DNS over both names, which would send traffic to a starting container before it is ready.
Why not "stop, update, start": it has downtime by definition.

**Database.** The schema is shared by both colours, so a migration must be compatible with the old and the
new code at once. This is the "expand then contract, in two releases" rule the norm already imposes on
`-db`. A release that breaks it is not blue-green safe.

**Health checks to add** (a small change each, in its own repository): `healthcheck` on the gateway
(`/health`) and on the front, with the same shape as the `security` one.

## 4. How it would be proved (proposal, not run)

- A loop of requests to `/api/v1/*` through the gateway during the switch, counting responses by status; the
  acceptance number is zero `5xx` and zero connection errors.
- The log of the gateway (it writes `status`, `upstream` and `durationMs` per line) shows the upstream
  changing from the old name to the new one.
- A deliberate failure: start a green container whose check never passes and show that `--wait` fails and the
  traffic never moves.

## 5. Limits

- No cloud, no registry, no second host, no run: the zero-downtime claim is **unproven**.
- The proof loop in section 4 would show no dropped requests for the gateway and `security` only; the
  portals and the front were not considered.
- The image-tag mismatch (`0.1.0` against `v2.0.0`) must be fixed before any deployment is called "tagged".
