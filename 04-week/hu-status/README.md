<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 04

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Set up the initial Backend and Frontend scaffolding and organize the product backlog for the upcoming implementation phase
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 | Scaffold the Backend Spring Boot project with basic dependencies | done | [14db12f](https://github.com/Molina211/Travesia-Natural-Monolito/commit/14db12fa7e1b2287e3180589b04d686291f2ec97) |
| HU-XXX-001 | Scaffold the Frontend project structure | done | [dcf6945](https://github.com/Molina211/Multitour-Monolito-Portal/commit/dcf69454595da18374db2646e2910fc0cee762ff) |
| HU-XXX-001 | Set up the GitHub Projects backlog board for Travesia Natural | done | [Data/Image/Realización del Backlog.png](./Data/Image/Realización%20del%20Backlog.png) |
| DOC-01 | Migrate the PDR v1.7 baseline into the docs repository | done | [223e9be](https://github.com/code-corhuila/travesia-natural-docs/commit/223e9be74dd567099814700653016c00d5892fe2) |
| DOC-02 | Formalize 04-requirements: 19 functional requirements, 25 user stories and the traceability matrix | done | [648d1bd](https://github.com/code-corhuila/travesia-natural-docs/commit/648d1bd77514c8f780c9b84017f0895876b0b164) |
| DOC-03 | Define 05-architecture: system overview, deployment, cross-cutting concerns and threat model | done | [b255042](https://github.com/code-corhuila/travesia-natural-docs/commit/b2550427d8a32832a7fef954e74be7cfa3e7e549) |

## 2. My individual contribution
- I created the Backend project skeleton (Spring Boot 4.1.1, Java 21, Gradle) with its basic dependencies and default bootstrap class (`14db12f`, 2026-08-27).
- I created the Frontend repository's main folder structure (`dcf6945`, 2026-08-27); the technology stack is still not decided.
- I set up the GitHub Projects backlog board for Travesia Natural (Backlog / Ready / In progress / In review columns), currently empty of items pending prioritization.
- I migrated the PDR v1.7 baseline into the docs repository as `03-product/prd.md` (`223e9be`, 2026-08-30).
- I formalized `04-requirements` (functional, non-functional, user stories, traceability matrix) from the PDR baseline (`648d1bd`, 2026-08-30).
- I defined `05-architecture` (system overview, deployment, cross-cutting concerns, STRIDE threat model), using the projected service-per-bounded-context model already in `02-domain/domain-map.md`, and flagged the two open architecture decisions (monolith vs. microservices, RC-001-004 stack assignment) as technical debt instead of resolving them unilaterally (`b255042`, 2026-08-30).
- Weekly summary of the evaluation period.

## 3. Blockers and risks
- The Frontend technology stack is still not decided, even though the folder scaffold now exists.
- The Backend has zero entities, controllers, repositories or database configuration yet; it is only the initial project skeleton.
- The multitenant persistence strategy (schema per tenant / discriminator / DB per tenant) is still undecided and blocks starting real Backend implementation.
- Monolith vs. microservices-per-bounded-context is now documented as an open architecture decision (`05-architecture/overview.md`, AT-001), not yet resolved by an ADR.
- The mandatory dual-stack constraints (PostgreSQL/MongoDB, Angular/React, Java/Go, 4 Micro Frontends) still have no per-service/per-Micro-Frontend assignment (AT-002).
- The GitHub Projects backlog board was created but has no items loaded yet.

## 4. Plan for next week
- Populate the GitHub Projects backlog with the first real items derived from the now-formalized `04-requirements` user stories.
- Write the ADR that resolves monolith vs. microservices and the RC-001-004 stack assignment before starting real Backend implementation.
- Decide and set up the Frontend technology stack.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- [Backend scaffold commit](https://github.com/Molina211/Travesia-Natural-Monolito/commit/14db12fa7e1b2287e3180589b04d686291f2ec97)
- [Frontend scaffold commit](https://github.com/Molina211/Multitour-Monolito-Portal/commit/dcf69454595da18374db2646e2910fc0cee762ff)
- [Backlog board screenshot](./Data/Image/Realización%20del%20Backlog.png)
- [Week 04 Summary](./WeeklySummary/Week-04.png)
