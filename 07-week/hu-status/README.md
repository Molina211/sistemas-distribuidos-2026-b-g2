<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       07-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 07

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Documentation-only week after MVP1: record the decision to split the Backend into four microservices by macrodomain (ADR-004), normalize the "Multitour" name in Docs, open the BPMN section for process diagrams, and define the communication decisions and versioned contracts between the new services (Weekly Challenge).
<!-- CONFIG-END -->

## 1. User stories worked this week
Rows are in chronological order of the work; dates are local time (UTC-5). The last row is still to do.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-05 | Normalize the "Multitour" spelling (was "Multi tour" / "Multi-Tour") in Docs `01-context` and `03-product` | done | commits `80f2ee6`, `05adf4f` (2026-09-14); Docs PR #7, merged 2026-09-17: https://github.com/code-corhuila/multi-tour-docs/pull/7 |
| ARCH-01 | Backend spec 029: ArchUnit architecture conformance tests (Hexagonal boundaries, DDD module isolation, multitenancy rule) over the 7 business modules | done | Backend commits `b3c813e` to `abe4fbb` (2026-09-16); PRs #33 (`chore/archunit-architecture-tests` -> `develop`), #34 (`develop` -> `qa`), #35 (`qa` -> `main`), merged 2026-09-16: https://github.com/Molina211/Multitour-Monolito-Api/pull/35 |
| DOC-06 | ADR-004: supersede ADR-002 and split the Backend into 4 microservices by macrodomain (Platform/Access/Audit, Commercial/Reservations, Operations/Costs, Cash/Reports); rewrite `05-architecture/overview.md` and close AT-001 | done | commit `ddf0168` (2026-09-17); Docs PR #9, merged 2026-09-17: https://github.com/code-corhuila/multi-tour-docs/pull/9 |
| DOC-07 | Add the canonical spelling rule to the Docs glossary: "Multitour" for prose, "multi-tour" only for repository/organization slugs | done | commit `20f7a85` (2026-09-17); Docs PR #9 |
| MS-01 | Start extracting `operations-costs-service`, the first of the 4 ADR-004 microservices: independent Spring Boot scaffold, copied operations domain model, own PostgreSQL persistence layer, HTTP `ReservationClient` to the monolith | doing | 4 local commits `670b157`, `9232ab6`, `c242d75`, `9b7cfaf` (2026-09-17) on Backend branch `chore/extract-operations-costs-service`; not pushed yet |
| DOC-08 | Record the teacher's ratification in the ADR-004 `Reviewers` field | done | commit `1e2aafe` (2026-09-17); Docs PR #10, merged 2026-09-18: https://github.com/code-corhuila/multi-tour-docs/pull/10 |
| DOC-09 | New Docs section `16-bpmn/` for BPMN process diagrams (README, diagram index, Mermaid draft example), cross-referenced from `08-uml`, the root README and CONTRIBUTING | done | commit `88f4a15` (2026-09-18); Docs PR #12, merged 2026-09-19: https://github.com/code-corhuila/multi-tour-docs/pull/12 |
| DOC-10 | Apply the teacher's review of PR #12 (English wording in CONTRIBUTING, "Why this section exists" block in `16-bpmn/README.md`) | done | commit `6aecc91` (2026-09-19); Docs PR #13, merged 2026-09-21: https://github.com/code-corhuila/multi-tour-docs/pull/13 |
| WCH-01 | Weekly Challenge Semana 07, Session 1: sync or async decision with justification for every cross-service interaction, REST vs gRPC, topics vs queues, delivery semantics | done | [WeeklySummary/WeeklyChallenge/Service-Communication-Decisions-Multitour.md](./WeeklySummary/WeeklyChallenge/Service-Communication-Decisions-Multitour.md) (2026-09-21) |
| WCH-02 | Weekly Challenge Semana 07, Session 1: idempotent consumer (Cash handling `PaymentValidated`) | doing | Same file, section 4: designed with schema, algorithm, failure analysis and test scenarios; not implemented, because no broker or consumer exists yet |
| WCH-03 | Weekly Challenge Semana 07, Session 2: publish contracts (1 OpenAPI derived from code, 3 event JSON Schemas) and define versioning and compatibility rules | done | [WeeklySummary/WeeklyChallenge/Versioned-Contracts-MVP2-Multitour.md](./WeeklySummary/WeeklyChallenge/Versioned-Contracts-MVP2-Multitour.md) (sections 1 and 2) and the [contracts/](./WeeklySummary/WeeklyChallenge/contracts/) folder (2026-09-21) |
| WCH-04 | Weekly Challenge Semana 07, Session 2: consumer-driven contract test running in CI | doing | Same document, section 3: designed (3 interactions, Pact, CI job sketch); not implemented, because no CI exists in any repository and adding it needs the team's approval |
| WCH-05 | Weekly Challenge Semana 07, Session 2: slice the MVP2 integration stories with testable acceptance criteria | done | Same document, section 4: INT-01 to INT-07 |
| DOC-11 | Finish the "Multitour" normalization in the remaining Docs sections (`00-governance`, `02-domain`, `06-data`, `12-ux-ui`) and add the macrodomain grouping to `02-domain/domain-map.md` | todo | Docs PR #7 changed only 3 files. Checked against Docs `main` on 2026-09-21: the old spelling remains in 18 files (plus the glossary, where it appears only inside the rule that forbids it), and `02-domain/domain-map.md` has no macrodomain grouping yet. Those files were last authored by a teammate, who will apply the change |

## 2. My individual contribution
Chronological, local time (UTC-5).
- **2026-09-14.** Normalized the product name to "Multitour" in Docs `01-context` and `03-product`, replacing "Multi tour" and "Multi-Tour" (commits `80f2ee6`, `05adf4f`; PR #7, merged 2026-09-17), so the documents stop mixing spellings.
- **2026-09-16.** Added ArchUnit architecture conformance tests to the Backend (spec 029): automated rules that fail the build if a domain package depends on infrastructure, if application code depends on adapters directly, if a bounded-context module reaches into another module's internals, or if a tenant-scoped entity loses its `tenantId`. Promoted through the full `develop -> qa -> main` flow the same day (PRs #33, #34, #35).
- **2026-09-17.** Wrote ADR-004, which supersedes ADR-002: the Backend moves from a modular monolith to four independently deployable microservices grouped by macrodomain over the 11 bounded contexts already defined in `02-domain/domain-map.md`. Rewrote the topology sections of `05-architecture/overview.md` to match, closed AT-001 (open since 2026-09-02) and added the canonical spelling rule to the glossary (PR #9).
- **2026-09-17.** Started the first extraction of ADR-004: four local commits scaffold `operations-costs-service` as an independent Spring Boot project with a copy of the operations domain model, its own PostgreSQL persistence and an HTTP client to the monolith. Nothing is pushed yet.
- **2026-09-17.** After the teacher's review, filled in the ADR-004 `Reviewers` field (commit `1e2aafe`; PR #10, merged 2026-09-18).
- **2026-09-18.** Created the Docs section `16-bpmn/` for BPMN business process diagrams: README, diagram index and a Mermaid draft example. It is cross-referenced from `08-uml/README.md` so the two sections do not overlap, registered in the root README index and diagram, and the procedure for adding a section is documented in `CONTRIBUTING.md` (commit `88f4a15`; PR #12, merged 2026-09-19).
- **2026-09-19.** Applied the teacher's review of that section: English wording in `CONTRIBUTING.md` and a "Why this section exists" block in `16-bpmn/README.md` (commit `6aecc91`; PR #13, merged 2026-09-21).
- **2026-09-21.** Wrote the Weekly Challenge for both sessions. Session 1: an inventory of the cross-service interactions taken from the 34 domain events and the ADR-004 service grouping (26 events cross a service boundary), with a sync or async decision and justification for each, REST for the synchronous ones, topics for the events, at-least-once delivery with an outbox, and a designed idempotent Cash consumer. Session 2: one OpenAPI contract derived from the real `ReservationClient` and the monolith's reservation endpoints, three event JSON Schemas, versioning and compatibility rules, the design of a consumer-driven contract test with its CI job, and seven MVP2 integration stories with testable acceptance criteria.

## 3. Blockers and risks
- The two Weekly Challenge requirements that need running code are only designed: an idempotent consumer (no broker or consumer exists in the code) and a contract test running in CI (no CI exists in any repository; the Pact dependency and the workflow need the team's approval). Both are MVP2 stories (INT-06, INT-02 and INT-03).
- The Weekly Challenge was completed on 2026-09-21, after the Sunday cutoff of week 07.
- The remaining "Multitour" normalization and the macrodomain grouping for `02-domain/domain-map.md` touch files last authored by a teammate, so they wait for her instead of being changed here.
- The four microservice repositories named in ADR-004 are proposed names only; none has been created yet. The `operations-costs-service` code exists only as local commits inside the monolith repository.
- The new `operations-costs-service` hardcodes the development database password in `operations-costs-service/src/main/resources/application.properties` instead of reading it from an environment variable. It is the same development-only value already public in the monolith, but it has to move to configuration before the service is pushed.
- The class instruction for this week was documentation only; the ArchUnit tests (spec 029, 2026-09-16) and the `operations-costs-service` scaffold (2026-09-17) are Backend code work done inside the week.

## 4. Plan for next week
In order of dependency.
- Get the teammate's confirmation and finish the "Multitour" normalization across the remaining Docs sections, and add the macrodomain grouping to `02-domain/domain-map.md` (DOC-11).
- Write the ADR that records the broker choice and the Session 1 and Session 2 communication decisions (INT-04), and get the team's approval to add the Pact dependency and the contract-test CI workflow (INT-02, INT-03).
- Move the `operations-costs-service` database settings to environment variables before pushing it (MS-01).
- Draft the user stories each microservice will need, and document the database configuration for each service repository (documentation only).
- Add 1 or 2 real BPMN process diagrams to `16-bpmn/`.
- Update the repository map with the microservice repository links once those repositories exist.
- Later, in MVP2: the outbox publisher, the idempotent Cash consumer and the identity and tenant checks between services (INT-05, INT-06, INT-07).

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` - all Docs and Backend commits this week follow it (`docs(...)`, `chore(architecture)`, `feat(operations-costs-service)`); Docs PR #9's title lacks the type prefix, which the teacher flagged in review and was left as is
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - spec 029 used a `chore/` branch (tooling work, no product HU) and went `develop` -> `qa` -> `main` through PRs #33, #34, #35; Docs changes went through branch + PR into `main` (Docs has a single permanent branch)
- [x] Testable acceptance criteria - spec 029 was closed with a verification pass (commit `db18383`); the MVP2 stories INT-01 to INT-07 carry checkable acceptance criteria
- [x] Tests added/updated (unit / integration) - `ArchitectureRulesTest` added with its ArchUnit dependency (commits `b3c813e`, `e21f328`, `ec0643d`, `c11f291`, `de11272`, `232307b`, `4460fb1`)
- [x] DDD / hexagonal boundaries respected (domain has no I/O) - now enforced automatically for the 7 monolith modules by spec 029; `operations-costs-service` is not covered by those tests yet
- [ ] No secrets; config via environment variables - `operations-costs-service` hardcodes the development DB password in `application.properties` (see section 3); nothing else new this week

## 6. Evidence links
- Weekly summary image for week 07 (both class sessions): [WeeklySummary/Week-07.png](./WeeklySummary/Week-07.png)
- Weekly Challenge Semana 07, Session 1 writeup: [WeeklySummary/WeeklyChallenge/Service-Communication-Decisions-Multitour.md](./WeeklySummary/WeeklyChallenge/Service-Communication-Decisions-Multitour.md)
- Weekly Challenge Semana 07, Session 2 writeup: [WeeklySummary/WeeklyChallenge/Versioned-Contracts-MVP2-Multitour.md](./WeeklySummary/WeeklyChallenge/Versioned-Contracts-MVP2-Multitour.md)
- Published contracts (OpenAPI and event schemas): [WeeklySummary/WeeklyChallenge/contracts/](./WeeklySummary/WeeklyChallenge/contracts/)
- Backend PR #33 (ArchUnit tests, 2026-09-16): https://github.com/Molina211/Multitour-Monolito-Api/pull/33
- Backend PR #34 (`develop` -> `qa`, 2026-09-16): https://github.com/Molina211/Multitour-Monolito-Api/pull/34
- Backend PR #35 (`qa` -> `main`, 2026-09-16): https://github.com/Molina211/Multitour-Monolito-Api/pull/35
- Backend spec 029: `specs/029-archunit-architecture-tests/` in the Backend repository
- Docs PR #7 (Multitour spelling, merged 2026-09-17): https://github.com/code-corhuila/multi-tour-docs/pull/7
- Docs PR #9 (ADR-004 and glossary, merged 2026-09-17): https://github.com/code-corhuila/multi-tour-docs/pull/9
- Docs PR #10 (ADR-004 Reviewers, merged 2026-09-18): https://github.com/code-corhuila/multi-tour-docs/pull/10
- Docs PR #12 (BPMN section, merged 2026-09-19): https://github.com/code-corhuila/multi-tour-docs/pull/12
- Docs PR #13 (BPMN review follow-up, merged 2026-09-21): https://github.com/code-corhuila/multi-tour-docs/pull/13
