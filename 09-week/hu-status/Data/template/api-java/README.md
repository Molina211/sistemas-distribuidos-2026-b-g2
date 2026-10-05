# <abbr>-orders-api (Java)

Hexagonal service of the **orders** domain, as a three-module Maven build.

```
orders-core/       domain + application — NO Spring dependency
  domain/model/            entities, invariants and domain exceptions
  application/port/in/     what the service offers (OrderUseCases)
  application/port/out/    what it needs (repository, id generator)
  application/usecase/     OrderService + its tests
orders-adapters/   Spring Web and JDBC adapters
  adapter/in/http/         controller, RS256 verifier, auth and correlation filters, error envelope
  adapter/out/persistence/ JDBC (+ integration test) and in-memory repositories
orders-app/        Spring Boot application: the composition root, pool, HTTP tests
deploy/            Dockerfile and this service's compose file
```

**Why three modules.** `orders-core` does not declare Spring as a dependency, so
a `@Service` or `@Entity` in the domain **does not compile**. The dependency rule
stops being a convention you have to remember and becomes a build error.
Use cases are plain classes; the app module turns them into beans.

## The contract this template already honours

| Concern | Where | Rule |
|---|---|---|
| Authentication | `Rs256Verifier`, `AuthFilter` | Validates the JWT itself with the JDK: RS256 only, signature with the public key, `exp` and `sub` required. The gateway only filters requests with no credentials. |
| Errors | `ApiError`, `ErrorHandler` | Every error is `{"error": CODE, "message", "details"?, "traceId"}` — unknown routes and malformed JSON included. |
| Correlation | `CorrelationFilter` | Reuses or generates `X-Correlation-Id`, puts it in the MDC; logs are JSON (ECS). |
| Validation | `OrderController` | UUIDs, integer money (`12.5` is rejected, not truncated), enum filters, `limit` 1..100 → 400 with one entry per field. |
| Idempotency | `POST /api/v1/orders` | `Idempotency-Key` header required. A retry returns 200 and the same order; key and order are written in one transaction. |
| Pagination | `GET /api/v1/orders` | `page`, `limit` (default 20, max 100) → `{data, meta: {page, limit, total, totalPages}}`. |
| Pool and timeouts | `OrdersConfiguration`, `application.yml` | Hikari built explicitly (size, acquire timeout, `statement_timeout`); Tomcat timeouts and graceful shutdown. |

## Run

The schema lives in `<abbr>-orders-db`; this service never runs migrations.

```bash
mvn verify                                             # core, HTTP and (if configured) integration tests
TEST_DATABASE_URL=jdbc:postgresql://localhost:5432/orders TEST_DATABASE_USER=... TEST_DATABASE_PASSWORD=... mvn verify
mvn -DskipTests package
JWT_PUBLIC_KEY_FILE=../<abbr>-infra/keys/jwt-public.pem java -jar orders-app/target/orders-app-0.1.0.jar
```

Without `DATABASE_URL` the service uses the in-memory repository. A development
token: `../<abbr>-infra/scripts/dev-token.sh alice`.

Rename the package `co.edu.corhuila.abbr` to use your team's abbreviation.
