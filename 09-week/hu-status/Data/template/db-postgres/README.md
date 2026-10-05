# <abbr>-orders-db

Database of the **orders** domain: PostgreSQL 16, versioned with Liquibase.
This repository owns the whole structure of the domain's data — engine, schema,
seed data, roles and migrations. `<abbr>-orders-api` consumes it; it never
versions the schema.

## Layout

```
changelog/changelog-master.yaml   single entry point of the pipeline
01_ddl/    structure  00_extensions .. 10_indexes
02_dml/    data       inserts, updates, deletes, upserts, patches
03_dcl/    access     roles, grants, row-level policies
04_tcl/    control    transaction blocks, manual recoveries, release tags
05_rollbacks/          mirror of every changeset that has a rollback file
deploy/                this domain's own instance and its migration runner
.github/workflows/     rebuilds the schema from empty on every pull request
```

## Rules

1. **Table names are singular**: a table is named after one row (`customer_order`,
   `order_item`). `order` itself is a reserved word in SQL, which is why the
   aggregate's table is `customer_order`.
2. **Tables are created without foreign keys** in `03_tables`; keys are added in
   `04_alter`, each one declaring what happens on delete.
3. **Every foreign key column is indexed.** Every other index names the query
   pattern it serves: `idx_<table>_<columns>`.
4. **Closed sets of values are a `CHECK` or a lookup table, not an `ENUM`.**
   Adding a value to an enum type is an `ALTER TYPE` that cannot run in the same
   transaction that uses it; a `CHECK` is changed like any other constraint.
5. **Text is `text`**, with a `CHECK` when the business sets a limit.
6. **Every changeset declares its rollback.**
7. **A changeset that has been applied is never edited.** A correction is a new
   changeset.
8. **Seeds are idempotent**: an upsert against a unique key, never a bare insert.
9. **Incompatible changes go in two releases** (expand, then contract): add the new
   column and write to both; drop the old one only when no deployed version reads it.
10. **An index on a table with traffic is created `CONCURRENTLY`**, in a changeset
    with `runInTransaction: false`. The index in this template is created on empty
    tables, where the plain form is correct.
11. **The schema is named after the domain** (`orders`). Nothing lives in `public`.
12. **This database belongs to one domain.** No other service connects to it.

## Run it

```bash
docker network create platform          # once per machine
cp .env.example .env                    # and set the password
docker compose -f deploy/compose.yml --env-file .env up -d orders-db
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate update
```

```bash
# status, history and rollback of the last changeset
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate status --verbose
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate history
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate rollback-count 1
```
