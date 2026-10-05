# <abbr>-orders-db (MongoDB)

Database of the **orders** domain: MongoDB 7, versioned with Liquibase and its
MongoDB extension — the same tool and the same discipline as the PostgreSQL
repositories, so a team that runs both engines keeps one way of working.

## What changes with respect to PostgreSQL

The discipline is the same; several concepts are not. Read this before copying
the PostgreSQL layout.

| Concept | PostgreSQL | MongoDB |
|---|---|---|
| Structure | Tables and columns the engine enforces | A `$jsonSchema` **validator** attached to the collection. Without `additionalProperties: false` any extra field is accepted |
| Foreign keys | `04_alter` | **Do not exist.** Integrity between collections is the service's job. The `04_alter` folder disappears |
| Relations | Separate table + FK | Embedding (order lines live **inside** the order) or reference by id |
| Uniqueness | `PRIMARY KEY`, `UNIQUE` | **Unique indexes.** Indexes stop being tuning and become the structure |
| Roles | `CREATE ROLE` / `GRANT` | `createRole` through `runCommand`, scoped to the database |
| Transactions | Every migration runs in one | Multi-document transactions need a **replica set**; migrations are not wrapped the same way, so each changeset must be **idempotent on its own** |
| Rollback | Mirror SQL file | Declared **inline** with the inverse operation (`dropCollection`, `dropIndex`) |

## Layout

```
changelog/changelog-master.yaml
01_ddl/  00_collections  createCollection + $jsonSchema validator
         01_validators   evolve a validator with collMod
         02_indexes      createIndex — unique indexes enforce identity
         03_views        read-only views defined by an aggregation pipeline
02_dml/  00_inserts .. 04_patches
03_dcl/  00_roles        createRole
```

There is no `04_tcl` and no `05_rollbacks`: see the table above.

## Decisions the team records in an ADR

- **Embed or reference.** Embed what is read together and has no life of its
  own (order lines). Reference what has its own lifecycle (the customer).
- **`validationLevel`.** `strict` validates every write; `moderate` lets
  existing invalid documents be updated. Start strict.
- **`validationAction`.** `error` rejects; `warn` only logs. Never `warn` in `main`.

## Run it

```bash
docker network create platform          # once per machine
cp .env.example .env
docker compose -f deploy/compose.yml --env-file .env up -d orders-db
docker compose -f deploy/compose.yml --env-file .env run --rm orders-db-migrate update
```

## Two traps specific to documents

- **An array that grows with use has no place in a document.** A document has a
  16 MB limit and is rewritten whole on every update. The order lines are bounded
  (`maxItems: 100`); a history or an event log is a collection of its own.
- **The type of a number is part of the contract.** A number that arrives as JSON
  is a double unless the driver is told otherwise, and this validator requires a
  `long` for money: a client that sends a plain JSON number is rejected. That is the
  validator doing its job. Write money as a 64-bit integer of minor units (or as
  `decimal128`), declared explicitly in the driver.
- **Declare the write concern.** The driver's default has changed between versions;
  what must not be lost is written with `w: "majority"`, declared in the client.

MongoDB runs as a **single-node replica set**: it is what enables transactions
and change streams, so development behaves like production.
