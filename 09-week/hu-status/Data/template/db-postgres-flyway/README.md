# <abbr>-orders-db (PostgreSQL + Flyway)

Database of the **orders** domain: PostgreSQL 16, versioned with Flyway. The same
schema as the Liquibase variant; what changes is how the migrations are named,
ordered and rolled back.

## Layout

```
flyway.toml                 locations (the four families), naming validation, clean disabled
01_ddl/ 02_dml/ 03_dcl/ 04_tcl/   the same families and sub-folders as the Liquibase variant
05_rollbacks/               U<version>__<description>.sql: the rollback of every V<version>
deploy/                     this domain's own instance and its migration runner
.github/workflows/db-ci.yml rebuilds the schema from empty on every pull request
```

## What is different from Liquibase

| | Liquibase | Flyway |
|---|---|---|
| What decides the order | the order of `include` in the changelogs | **the version in the file name**: `V001`, `V002`… one sequence for the whole repository, whatever the folder |
| A new migration | a new changeset in its folder's changelog | the **next** version number: `V012__…`. A lower one (`V005_1`) is refused once `V011` is applied |
| Rollback | declared in the changeset, run with `rollback-count` | Flyway Community does not undo: `05_rollbacks/U<n>__….sql` is the rollback of `V<n>`, applied with `psql` in reverse order; in production a correction is a new forward migration |
| Views, functions | a changeset with `runOnChange` | a repeatable migration `R__<description>.sql`, re-applied when its content changes |
| Editing an applied migration | checksum error | checksum error: `Migration checksum mismatch` |
| History | `DATABASECHANGELOG` | `flyway_schema_history` |

All the rules of the Liquibase variant still apply: singular tables, foreign keys
added in `04_alter` with their index, `CHECK` instead of `ENUM`, idempotent seeds,
expand-then-contract, and `CREATE INDEX CONCURRENTLY` in a migration of its own.

Two Flyway traps, both verified:

- **`CONCURRENTLY` hangs** with Flyway's default transactional advisory lock: the
  index waits for every open transaction, including Flyway's own. `flyway.toml`
  sets `[flyway.postgresql.transactional] lock = false`; Flyway then runs the
  statement non-transactionally and records it.
- **A repeatable view cannot reorder its columns**: `CREATE OR REPLACE VIEW` only
  adds columns at the end. An `R__` view that may change starts with
  `DROP VIEW IF EXISTS …;` and then `CREATE VIEW`.

## Run it

```bash
docker network create platform          # once per machine
cp .env.example .env                    # and set the password
docker compose -f deploy/compose.yml --env-file .env up -d orders-db
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate            # migrate
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate info       # applied and pending
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate validate   # checksums
```

`clean` is disabled: it drops everything, and nothing in this workflow needs it.
