# Sesion 1 - Walking Skeleton del Backend

## Servicio escogido

**Backend monolito de Travesia Natural, bounded context Reservations**

Se escoge Reservations porque es el nucleo comercial del dominio (ver DDD de la Sesion 1
de la Semana 03) y porque `HU-RES-001` ya define un criterio de aceptacion completo y
verificable de punta a punta: crear una reserva con datos minimos y que quede persistida.

## Objetivo de la sesion

Probar en codigo, no solo en documentacion, que la arquitectura elegida (monolito
modular en capas hexagonales, `ADR-002`) y la estrategia de aislamiento de tenant
(esquema compartido + columna discriminadora `tenantId`, `ADR-003`) funcionan de punta a
punta: API expuesta -> dominio -> persistencia real contra un motor de base de datos.

## Que se implemento

### Layout hexagonal

Paquete base `com.corhuila.errorcapa8.travesia_natural`, organizado por bounded context
(no por capa tecnica transversal a todo el proyecto). El primer modulo es `reservations`,
con separacion real entre dominio (clases planas sin anotaciones de framework),
aplicacion (casos de uso) e infraestructura (adaptadores de entrada HTTP y de salida
JPA/Postgres):

```
reservations/
  domain/model/        Reservation, ReservedService, ReservationStatus, PaymentStatus
  domain/exception/    InvalidReservationException
  domain/port/in/       CreateReservationUseCase
  domain/port/out/      ReservationRepositoryPort
  application/          CreateReservationService
  infrastructure/in/web/         ReservationController + DTOs
  infrastructure/out/persistence/ ReservationEntity, ReservationRepositoryAdapter
```

Un paquete `common` aparte cubre lo que no pertenece a ningun bounded context todavia
(salud del servicio, seguridad temporal).

### Composition root

Arranque estandar de Spring Boot (`Application` + wiring de beans por Spring), sin
configuracion manual adicional: los adaptadores se inyectan detras de los puertos por
convencion de Spring, no por un contenedor de DI aparte.

### Endpoint `/health`

`GET /health` responde `200 OK` con la aplicacion corriendo contra el contenedor real de
base de datos, confirmando que el proceso completo (app + Postgres) queda arriba.

### Entidad persistida contra base de datos real

`Reservation` es la primera entidad de dominio persistida, con exactamente los atributos
obligatorios del agregado (`02-domain/entities-and-rules.md`):
`reservationId`, `tenantId`, `customerId`, `projectedValue`, `finalValue`,
`pendingBalance`, `creditBalance`, `reservationStatus`, `paymentStatus`,
`paymentMethod`, `createdAt`.

`tenantId` queda como columna `NOT NULL` desde la primera migracion Flyway (`V1`), no se
agrega despues - primera vez que `ADR-003` (aislamiento por tenant) se implementa como
codigo y no solo como documento.

`docker-compose.yml` levanta un contenedor PostgreSQL para desarrollo local; Flyway
aplica las migraciones y deja un historial verificable (en vez de `ddl-auto` de
Hibernate).

## Decisiones tecnicas relevantes

- Dominio sin anotaciones JPA (clases planas) separado de la entidad de persistencia,
  para que la arquitectura hexagonal sea real y no solo nominal.
- `tenantId` de la peticion resuelto por header `X-Tenant-Id` en este corte, documentado
  como valor de confianza temporal hasta la spec de JWT (fuera de alcance aqui).
- `reservationId` generado como UUID v4 en la capa de aplicacion, no autogenerado por
  Postgres.
- Flyway como herramienta de migracion, para tener historial verificable de que
  `tenant_id NOT NULL` existe desde la primera version de la tabla.

## Evidencia (commits, 2026-09-02)

- [`94868e1`](https://github.com/Molina211/Multitour-Monolito-Api/commit/94868e1) - endpoint de creacion de reserva, `/health` y seguridad temporal permisiva
- [`ec0904f`](https://github.com/Molina211/Multitour-Monolito-Api/commit/ec0904f) - puertos, servicio de aplicacion y adaptador de persistencia de Reservation
- [`b380372`](https://github.com/Molina211/Multitour-Monolito-Api/commit/b380372) - migracion Flyway V1 y modelo de dominio del agregado Reservation
- [`12fbdda`](https://github.com/Molina211/Multitour-Monolito-Api/commit/12fbdda) - dependencias Postgres/Flyway, docker-compose y plan de verificacion inicial
- [`c1b2566`](https://github.com/Molina211/Multitour-Monolito-Api/commit/c1b2566) - migracion del build tool de Gradle a Maven y cierre de verificacion del walking skeleton
- `specs/001-walking-skeleton-reservation/` (spec, plan y `PLAN-VERIFICACION.md`) en el repo Backend

## Nota de reconciliacion

El contrato original de esta spec (tenant por header `X-Tenant-Id`, ruta
`POST /api/reservations`) fue reemplazado por la spec 006 (`tenantId` como slug en la
URL, ruta `POST /api/tenants/{tenantId}/reservations`). El walking skeleton como tal -
capas hexagonales, composition root, `/health`, `Reservation` persistida con tenant
obligatorio contra Postgres real en contenedor - sigue vigente y es la base sobre la que
se construyeron las specs 002 a 025.
