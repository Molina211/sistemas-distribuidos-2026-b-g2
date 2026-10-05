# <abbr>-infra

Assembles the whole system. **It composes; it does not contain.** Every runnable
repository ships its own `deploy/compose.yml`; this repository includes them,
creates the shared network and adds the cross-cutting services — observability —
that belong to no domain.

```
compose.yml                    root: includes every repository, adds observability
env/.env.<environment>.example one file per environment: develop, qa, main
observability/                 OpenTelemetry collector, Prometheus, Grafana
scripts/up.sh                  checks .env, creates the network and brings everything up
scripts/down.sh
scripts/dev-keys.sh            DEVELOPMENT ONLY: RS256 key pair in keys/ (git-ignored) + JWT_PUBLIC_KEY and SERVICE_TOKEN in .env
scripts/dev-token.sh           DEVELOPMENT ONLY: prints a signed token for a subject
```

## Repositories are cloned as siblings

```
workspace/
  <abbr>-infra/          <- you are here
  <abbr>-api-gateway/
  <abbr>-orders-db/
  <abbr>-orders-api/
  <abbr>-worker/
  <abbr>-workflow/
  <abbr>-front/
```

The `include` paths in `compose.yml` assume that layout. A missing sibling fails
loudly on `up`, which is the point: nothing is silently skipped.

## Rules

- **No database is defined here.** Each lives in its `-db` repository.
- **No secret is versioned.** `env/*.example` carries names and placeholders only;
  `keys/` and `.env` are git-ignored.
- **Development keys are not the identity service.** `dev-keys.sh` exists so the
  system runs before the identity domain exists. In qa and main the public key
  and the service tokens come from the identity service, injected as secrets.
- **One network, `platform`,** created by `scripts/up.sh`. Every repository joins it.

```bash
cp env/.env.develop.example .env      # then set ORDERS_DB_PASSWORD
./scripts/dev-keys.sh                 # develop only
./scripts/up.sh
# every -db repository migrates its own schema; run each one once the platform is up
docker compose --env-file .env run --rm orders-db-migrate
curl -H "Authorization: Bearer $(./scripts/dev-token.sh alice)" http://localhost:8000/api/v1/orders
```

Until a schema is migrated, the API of that domain answers `500 INTERNAL_ERROR`
and the worker logs the failed run with its `traceId`; the next run succeeds.
That is the expected behaviour of the platform, not a bug: nothing waits
forever and nothing fails silently.
