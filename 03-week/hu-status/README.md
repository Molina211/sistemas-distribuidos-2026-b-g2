<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       03-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 03

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Jhon Sebastian Molina Fierro
- GITHUB_USER: Molina211
- TEAM: ErrorCapa8
- SPRINT_GOAL: Consolidate the multitenant PDR baseline, complete the project context, and prepare the product for the next iteration.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-01 | Formalize the Travesia Natural product definition baseline | done | [Data/PDR_Travesia_Natural.md](./Data/PDR_Travesia_Natural.md) |
| DOC-02 | Model the reservation domain with DDD artifacts | done | [Data/DDD-Reservas-Travesia-Natural.md](./Data/DDD-Reservas-Travesia-Natural.md) |
| DOC-03 | Complete the project context page in the docs repository | done | N/A |
| DOC-04 | Compile the Week 03 summary artifact | done | [WeeklySummary/Week-03.jpeg](./WeeklySummary/Week-03.jpeg) |
| DOC-05 | Complete the weekly challenges for Week 03 | done | [WeeklySummary/WeeklyChallenge/DDD-Reservas-Travesia-Natural.md](./WeeklySummary/WeeklyChallenge/DDD-Reservas-Travesia-Natural.md) |

## 2. My individual contribution
- I linked the project work to the new documentation repository scaffold.
- I revised the repository guidance and project prompt to make the multitenant focus and Git workflow checkpoint explicit before continuing with documentation work.
- I consolidated the PDR baseline for Travesia Natural, including scope, stakeholders, risks, acceptance criteria, and traceability.
- I completed `01-context` in the docs repository, including `overview.md`, `scope.md`, and `glossary.md`, with a multitenant focus and tenant isolation language.
- I completed `03-product` in the docs repository, including `problem-framing.md`, `discovery-brief.md`, `vision.md`, `roadmap.md`, and `product-backlog.md`.
- I modeled the reservation bounded context in DDD with the aggregate root, entities, value objects, invariants, and domain events.
- I aligned the weekly evidence with the docs repository work so the multitenant baseline, context, and product narrative stayed consistent.
- Weekly summary of the evaluation period.
- The weekly challenges were completed at the end of each session in Moodle.

## 3. Blockers and risks
- The final technical stack and some non-functional baselines are still pending architectural confirmation.
- The multitenant baseline can still affect architecture, data, and requirements as the project moves into the next iteration.
- Some product evidence still depends on the remaining forum of feedback and the final validation of the next academic cut.
- The week is complete at the documentation level, but the next formal step still depends on moving the stabilized product baseline into requirements and architecture.

## 4. Plan for next week
- Start the requirements baseline the stabilized multitenant product definition.
- Revisit `04-requirements`, `05-architecture`, and `06-data` in the next iteration so the future service schemas stay aligned.
- Keep the weekly record focused on the next concrete deliverables while preserving the multitenant wording already validated this week.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [x] Testable acceptance criteria
- [x] DDD / hexagonal boundaries respected (domain has no I/O)

## 6. Evidence links
- [PDR Travesia Natural](./Data/PDR_Travesia_Natural.md)
- [DDD - Reservations - Travesia Natural](./Data/DDD-Reservas-Travesia-Natural.md)
- [Week 03 Summary](./WeeklySummary/Week-03.jpeg)
- [Week 03 Challenge - DDD Reservations](./WeeklySummary/WeeklyChallenge/DDD-Reservas-Travesia-Natural.md)
- [Week 03 Challenge - Services, Data and Contracts](./WeeklySummary/WeeklyChallenge/Servicios-Datos-Contratos-Reservas-MVP1.md)
