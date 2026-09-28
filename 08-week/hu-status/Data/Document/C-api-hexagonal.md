# Anexo C — Repositorio de servicio (`-api`), arquitectura hexagonal

Repositorio: `<abbr>-<dominio>-api` · Norma: numeral 5.3

El lenguaje de cada servicio **lo elige el equipo** entre Go, Java, Python y C#, y lo
registra en un ADR (numerales 4.2.2 y 4.2.3). Lo que no se elige es la forma ni el
contrato: un consumidor no debe poder saber en qué lenguaje está escrito un servicio.
Este anexo describe esa forma común, la estructura esperada en cada lenguaje, el
contrato público y las comprobaciones con que se verificará.

## La forma, en cualquier lenguaje

| Capa | Qué contiene | De qué puede depender |
|---|---|---|
| **Dominio** | entidades, objetos de valor, invariantes, errores tipados | de nada del proyecto |
| **Puertos de entrada** | lo que el servicio ofrece: interfaces de casos de uso, comandos y consultas | dominio |
| **Puertos de salida** | lo que el servicio necesita: repositorio, reloj, generador de identificadores | dominio |
| **Casos de uso** | la implementación de cada operación de negocio | puertos y dominio |
| **Adaptador de entrada HTTP** | autenticación, validación, sobre de error, correlación, rutas | puertos de entrada |
| **Adaptadores de salida** | base de datos (con *pool*), colas, otros servicios | puertos de salida |
| **Raíz de composición** | el único lugar que conoce todos los tipos concretos, y todos los límites | todo |

**La regla de dependencia:** las flechas apuntan siempre hacia el dominio. Si el
dominio importa un *framework* web o un *driver* de base de datos, la arquitectura
está rota — aunque el código funcione.

## Dónde va cada capa en cada lenguaje

| Capa | Go | Java (Maven, 3 módulos) | Python | C# (.NET) |
|---|---|---|---|---|
| Dominio | `internal/domain/model/` | `orders-core/…/domain/model/` | `src/orders/domain/model/` | `src/Orders.Core/Domain/` |
| Puertos de entrada | `internal/application/port/in/` | `orders-core/…/application/port/in/` | `src/orders/application/port/inbound/` | `src/Orders.Core/Application/Ports/In/` |
| Puertos de salida | `internal/application/port/out/` | `orders-core/…/application/port/out/` | `src/orders/application/port/outbound/` | `src/Orders.Core/Application/Ports/Out/` |
| Casos de uso | `internal/application/usecase/` | `orders-core/…/application/usecase/` | `src/orders/application/usecase/` | `src/Orders.Core/Application/UseCases/` |
| Adaptador HTTP | `internal/adapter/in/httpapi/` | `orders-adapters/…/adapter/in/http/` | `src/orders/adapter/inbound/http/` | `src/Orders.Adapters/In/Http/` |
| Persistencia | `internal/adapter/out/persistence/` | `orders-adapters/…/adapter/out/persistence/` | `src/orders/adapter/outbound/persistence/` | `src/Orders.Adapters/Out/Persistence/` |
| Raíz de composición | `cmd/orders-api/main.go` + `internal/config/` | `orders-app/` | `apps/api/__main__.py` | `src/Orders.App/Program.cs` |

**En Java y C# el núcleo es un módulo o proyecto aparte** que no declara el
*framework* como dependencia (numeral 5.3.3): un `@Service`, un `@Entity`, un
`[ApiController]` o un `HttpContext` en el dominio **no compila**. La regla deja de ser
una convención que hay que recordar y pasa a ser un error de compilación. En Go y
Python se verifica revisando las importaciones.

## Estructura esperada

### Go

```
<abbr>-orders-api/   (Go)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── cmd/
│   └── orders-api/
│       └── main.go
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── internal/
│   ├── adapter/
│   │   ├── in/
│   │   │   └── httpapi/
│   │   │       ├── auth.go
│   │   │       ├── errors.go
│   │   │       ├── handler.go
│   │   │       ├── handler_test.go
│   │   │       └── middleware.go
│   │   └── out/
│   │       └── persistence/
│   │           ├── memory.go
│   │           ├── postgres.go
│   │           └── postgres_integration_test.go
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   └── orders.go
│   │   │   └── out/
│   │   │       └── ports.go
│   │   └── usecase/
│   │       ├── orders.go
│   │       └── orders_test.go
│   ├── config/
│   │   └── config.go
│   └── domain/
│       └── model/
│           └── order.go
├── .env.example
├── .gitignore
├── go.mod
├── go.sum
└── README.md
```

### Java — Spring Boot 3.5, tres módulos

```
<abbr>-orders-api/   (Java)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── orders-adapters/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── orders/
│   │   │                           └── adapter/
│   │   │                               ├── in/
│   │   │                               │   └── http/
│   │   │                               │       ├── ApiError.java
│   │   │                               │       ├── AuthFilter.java
│   │   │                               │       ├── CorrelationFilter.java
│   │   │                               │       ├── ErrorHandler.java
│   │   │                               │       ├── HealthController.java
│   │   │                               │       ├── OrderController.java
│   │   │                               │       └── Rs256Verifier.java
│   │   │                               └── out/
│   │   │                                   └── persistence/
│   │   │                                       ├── InMemoryOrderRepository.java
│   │   │                                       ├── JdbcOrderRepository.java
│   │   │                                       └── UuidGenerator.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── orders/
│   │                               └── adapter/
│   │                                   └── out/
│   │                                       └── persistence/
│   │                                           └── JdbcOrderRepositoryIntegrationTest.java
│   └── pom.xml
├── orders-app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── co/
│   │   │   │       └── edu/
│   │   │   │           └── corhuila/
│   │   │   │               └── abbr/
│   │   │   │                   └── orders/
│   │   │   │                       └── app/
│   │   │   │                           ├── OrdersApplication.java
│   │   │   │                           └── OrdersConfiguration.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── orders/
│   │                               └── app/
│   │                                   └── OrdersHttpTest.java
│   └── pom.xml
├── orders-core/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── orders/
│   │   │                           ├── application/
│   │   │                           │   ├── port/
│   │   │                           │   │   ├── in/
│   │   │                           │   │   │   └── OrderUseCases.java
│   │   │                           │   │   └── out/
│   │   │                           │   │       ├── IdGenerator.java
│   │   │                           │   │       ├── OrderNotFoundException.java
│   │   │                           │   │       └── OrderRepository.java
│   │   │                           │   └── usecase/
│   │   │                           │       └── OrderService.java
│   │   │                           └── domain/
│   │   │                               └── model/
│   │   │                                   ├── DomainException.java
│   │   │                                   ├── Order.java
│   │   │                                   └── Status.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── orders/
│   │                               └── application/
│   │                                   └── usecase/
│   │                                       └── OrderServiceTest.java
│   └── pom.xml
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

### Python — FastAPI

```
<abbr>-orders-api/   (Python)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── apps/
│   ├── api/
│   │   ├── __init__.py
│   │   └── __main__.py
│   └── __init__.py
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   └── orders/
│       ├── adapter/
│       │   ├── inbound/
│       │   │   ├── http/
│       │   │   │   ├── __init__.py
│       │   │   │   ├── app.py
│       │   │   │   ├── auth.py
│       │   │   │   ├── errors.py
│       │   │   │   └── router.py
│       │   │   └── __init__.py
│       │   ├── outbound/
│       │   │   ├── persistence/
│       │   │   │   ├── __init__.py
│       │   │   │   ├── memory.py
│       │   │   │   └── postgres.py
│       │   │   └── __init__.py
│       │   └── __init__.py
│       ├── application/
│       │   ├── port/
│       │   │   ├── inbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── orders.py
│       │   │   ├── outbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── ports.py
│       │   │   └── __init__.py
│       │   ├── usecase/
│       │   │   ├── __init__.py
│       │   │   └── orders.py
│       │   └── __init__.py
│       ├── domain/
│       │   ├── model/
│       │   │   ├── __init__.py
│       │   │   └── order.py
│       │   └── __init__.py
│       └── __init__.py
├── tests/
│   ├── conftest.py
│   ├── test_http.py
│   ├── test_orders.py
│   └── test_postgres_integration.py
├── .env.example
├── .gitignore
├── pyproject.toml
└── README.md
```

### C# — .NET 10

```
<abbr>-orders-api/   (C#)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   ├── Orders.Adapters/
│   │   ├── In/
│   │   │   └── Http/
│   │   │       ├── ApiError.cs
│   │   │       ├── Middleware.cs
│   │   │       ├── OrderEndpoints.cs
│   │   │       └── Rs256Verifier.cs
│   │   ├── Out/
│   │   │   └── Persistence/
│   │   │       └── Repositories.cs
│   │   └── Orders.Adapters.csproj
│   ├── Orders.App/
│   │   ├── appsettings.json
│   │   ├── Orders.App.csproj
│   │   └── Program.cs
│   └── Orders.Core/
│       ├── Application/
│       │   ├── Ports/
│       │   │   ├── In/
│       │   │   │   └── OrderUseCases.cs
│       │   │   └── Out/
│       │   │       └── OrderPorts.cs
│       │   └── UseCases/
│       │       └── OrderService.cs
│       ├── Domain/
│       │   └── Order.cs
│       └── Orders.Core.csproj
├── tests/
│   ├── Orders.Api.Tests/
│   │   ├── NpgsqlOrderRepositoryTests.cs
│   │   ├── Orders.Api.Tests.csproj
│   │   └── OrdersHttpTests.cs
│   └── Orders.Core.Tests/
│       ├── Orders.Core.Tests.csproj
│       └── OrderServiceTests.cs
├── .env.example
├── .gitignore
├── Orders.sln
└── README.md
```

## Qué hace cada archivo del adaptador HTTP

Los nombres cambian por lenguaje; las responsabilidades no.

| Responsabilidad | Go | Java | Python | C# |
|---|---|---|---|---|
| Verificar el JWT | `auth.go` | `Rs256Verifier` + `AuthFilter` | `auth.py` + `app.py` | `Rs256Verifier` + `AuthMiddleware` |
| Correlación y registro de acceso | `middleware.go` | `CorrelationFilter` | `app.py` | `CorrelationMiddleware` |
| Sobre de error y traducción de errores de dominio | `errors.go` | `ApiError` + `ErrorHandler` | `errors.py` | `ApiError` + `ErrorMiddleware` |
| Rutas, validación y objetos de respuesta | `handler.go` | `OrderController` | `router.py` | `OrderEndpoints` |

## El contrato público

### Operaciones

| Método y ruta | Entrada | Respuestas |
|---|---|---|
| `POST /api/v1/orders` | cuerpo `{"customerId", "totalCents"}` + cabecera `Idempotency-Key` | `201` `{"id"}` con `Location`; `200` y el **mismo** id si la clave ya se usó; `400`; `401`; `422` |
| `GET /api/v1/orders` | `page`, `limit`, `status`, `createdBefore` | `200` `{"data": [...], "meta": {...}}`; `400` |
| `GET /api/v1/orders/{id}` | id UUID | `200` el pedido; `400` si el id no es UUID; `404` |
| `POST /api/v1/orders/{id}/confirm` | — | `200` pedido `CONFIRMED`; `422 INVALID_STATUS_TRANSITION` |
| `POST /api/v1/orders/{id}/cancel` | — | `200` pedido `CANCELLED`, también si ya estaba cancelado; `422` si estaba confirmado |
| `GET /health` | — | `200`, **sin** token |

### Representación

```json
{
  "id": "6f178b1a-9c0d-4c23-96ad-1e8da3b2ef6d",
  "customerId": "11111111-1111-4111-8111-111111111111",
  "totalCents": 2500,
  "status": "PENDING",
  "createdAt": "2026-09-24T02:57:30Z"
}
```

- JSON en `camelCase`. Identificadores UUID. Dinero entero en unidades menores. Fechas RFC 3339 en UTC.
- La entidad de dominio **nunca se serializa directamente**: el adaptador la traduce a un objeto de respuesta. Renombrar un campo interno no puede romper a un cliente.

### Errores

Todo error —incluidas las rutas inexistentes y el JSON malformado— responde con el
mismo sobre:

```json
{
  "error": "VALIDATION_ERROR",
  "message": "the request has invalid fields",
  "details": [
    { "field": "customerId", "message": "must be a UUID" },
    { "field": "Idempotency-Key", "message": "header required, 8 to 128 characters" }
  ],
  "traceId": "e2e-saga-1"
}
```

| Código | Estado | Cuándo |
|---|---|---|
| `VALIDATION_ERROR` | 400 | la forma de la entrada es inválida; una entrada en `details` por campo |
| `UNAUTHORIZED` | 401 | sin token, o token inválido o vencido |
| `FORBIDDEN` | 403 | token válido sin permiso para la operación |
| `NOT_FOUND` | 404 | el recurso o la ruta no existen |
| `INVALID_STATUS_TRANSITION` | 422 | la regla de estados del dominio lo impide |
| `BUSINESS_RULE_VIOLATION` | 422 | otra invariante del dominio lo impide |
| `INTERNAL_ERROR` | 500 | cualquier otra cosa: mensaje neutro, detalle completo **solo** en el registro |

La traducción de error de dominio a código de estado ocurre **en un solo lugar** del
adaptador HTTP. El dominio lanza errores tipados y no sabe qué es un 422.

### Listados

`page` desde 1, `limit` de 1 a 100 (20 por defecto), orden estable **del más reciente
al más antiguo**. Un `limit` fuera de rango o un filtro desconocido es `400`.

```json
{ "data": [ { "…": "…" } ], "meta": { "page": 1, "limit": 20, "total": 37, "totalPages": 2 } }
```

## Cómo se valida el token (numeral 5.3.7)

Cada servicio valida el JWT **por sí mismo**: el gateway solo comprueba que exista.

1. Sin cabecera `Authorization: Bearer <token>` → `401`.
2. El token tiene tres partes separadas por punto; se decodifica la cabecera.
3. `alg` debe ser **exactamente** `RS256`. Aceptar el algoritmo que declara el token es el agujero clásico: `none`, o `HS256` firmado usando la clave pública como secreto.
4. Se verifica la firma RSA (PKCS#1 v1.5, SHA-256) de `cabecera.carga` con la **clave pública** del servicio de identidad.
5. `exp` es obligatorio y no puede haber pasado (se tolera un desfase de reloj de 30 s); `sub` es obligatorio.

La clave pública llega por configuración: `JWT_PUBLIC_KEY` (el PEM en una sola línea,
con `\n` escritos literalmente, como lo guarda un `.env`) o `JWT_PUBLIC_KEY_FILE`. Se
puede usar una biblioteca JWT o la biblioteca estándar del lenguaje; en ambos casos,
con una **lista cerrada** de algoritmos. Ningún servicio posee la clave privada.

## Cómo se crea de forma idempotente (numeral 5.3.8)

Una red que corta la respuesta hace que el cliente reintente. Sin idempotencia, cada
reintento crea un pedido más.

1. El cliente envía `Idempotency-Key` (8 a 128 caracteres), la misma en cada reintento de la misma intención.
2. El servicio, **en una sola transacción**: inserta el pedido, e inserta la clave en `idempotency_key` con `ON CONFLICT DO NOTHING` (Anexo A).
3. Si la clave se insertó: confirma y responde `201`.
4. Si la clave ya existía: **revierte** —el pedido nuevo desaparece— y responde `200` con el id del pedido original.

## Correlación y registro (numeral 5.3.9)

Reutilizar el `X-Correlation-Id` recibido o generar uno; devolverlo en la respuesta;
escribirlo en **cada** línea del registro, en JSON; y usarlo como `traceId` del sobre.
Con eso, una queja de un usuario con su referencia lleva directo a la línea del
registro, en todos los servicios por los que pasó la petición.

## Límites explícitos (numeral 5.3.10)

Se declaran en la raíz de composición, con su valor. Estos son los ajustes de cada
lenguaje y un punto de partida razonable:

| Límite | Valor inicial | Go | Java (Spring Boot) | Python | C# |
|---|---|---|---|---|---|
| Lectura de cabeceras | 5 s | `http.Server.ReadHeaderTimeout` | `server.tomcat.connection-timeout` | sin ajuste en uvicorn: lo impone el gateway (`client_header_timeout` de NGINX) | `KestrelServerLimits.RequestHeadersTimeout` |
| Escritura / inactividad | 15 s / 60 s | `WriteTimeout`, `IdleTimeout` | `server.tomcat.keep-alive-timeout` | `timeout_keep_alive` | `KeepAliveTimeout` |
| Tamaño del *pool* | 10 | `db.SetMaxOpenConns` | `HikariConfig.setMaximumPoolSize` | `psycopg_pool.ConnectionPool(max_size=…)` | `MaxPoolSize` |
| Espera por conexión | 5 s | contexto con plazo | `setConnectionTimeout` | `timeout=` del *pool* | `Timeout` |
| Tiempo por sentencia | 5 s | contexto con plazo | `SET statement_timeout` | `statement_timeout` | `CommandTimeout` |
| Apagado ordenado | 20 s | `srv.Shutdown(ctx)` | `server.shutdown: graceful` | `timeout_graceful_shutdown` | `HostOptions.ShutdownTimeout` |

En Go, `http.ListenAndServe` deja los cuatro tiempos del servidor en cero, y cero
significa **sin límite**. En Python, abrir una conexión por operación en lugar de usar
un *pool* agota las conexiones del motor bajo carga.

## Pruebas

| Nivel | Qué prueba | Qué necesita |
|---|---|---|
| Núcleo | invariantes, idempotencia por clave, transiciones, página acotada | nada: ni servidor ni base, con falsos de los puertos |
| HTTP | cada variante de `401`, sobre y correlación, validación por campo, reintento idempotente, límite de página | el servidor en memoria con el repositorio en memoria |
| Integración | ida y vuelta, reversión de la clave de idempotencia, actualización y página | PostgreSQL con el esquema del `-db`, por `TEST_DATABASE_URL` |

Un repositorio simulado prueba que se llamó al simulador; no prueba que el SQL sea
válido, que el mapeo conserve los tipos ni que la migración exista. Por eso la prueba
de integración corre contra el motor real. Si `TEST_DATABASE_URL` no está definida, se
omite en lugar de fallar.

## Errores frecuentes

| Error | Consecuencia |
|---|---|
| Validar el token solo en el gateway | Cualquiera en la red interna entra sin credencial |
| Aceptar el `alg` que declara el token | Tokens forjados con `none` o `HS256` |
| Serializar la entidad de dominio | Nombres internos expuestos; un cambio interno rompe clientes |
| Devolver el mensaje del *driver* en un 500 | Se filtra la estructura de la base |
| Dinero en `float`/`double` | Redondeos en montos |
| Listado sin límite | Una consulta que un día devuelve un millón de filas |
| Migraciones dentro del `-api` | Falta grave (numeral 5.2.1) |

## Cómo se verifica (numeral 5.3.12)

El docente verifica cada `-api` **por HTTP**, sin mirar su lenguaje, con estas
comprobaciones. Cada equipo las automatiza en sus pruebas HTTP, adaptadas a los
recursos de su dominio, y las mantiene en verde.

**Autenticación**
- [ ] `/health` responde sin token
- [ ] Sin token: `401 UNAUTHORIZED`
- [ ] Token con `alg` distinto de `RS256`: `401`
- [ ] Token vencido: `401`
- [ ] Token firmado con otra clave: `401`

**Sobre de error y correlación**
- [ ] Id mal formado: `400 VALIDATION_ERROR`
- [ ] El sobre trae `error`, `message` y `traceId`
- [ ] `traceId` repite el `X-Correlation-Id` recibido
- [ ] La respuesta devuelve el mismo `X-Correlation-Id`
- [ ] Sin `X-Correlation-Id`, el servicio genera uno
- [ ] Recurso inexistente: `404 NOT_FOUND`
- [ ] Ruta inexistente: `404` con el sobre

**Validación en la frontera**
- [ ] Cuerpo inválido: `400 VALIDATION_ERROR`
- [ ] `details` nombra cada campo inválido, incluida la cabecera `Idempotency-Key`
- [ ] Dinero con decimales: `400`
- [ ] JSON malformado: `400 VALIDATION_ERROR`

**Creación idempotente**
- [ ] Primer envío: `201` con un id UUID
- [ ] `201` trae `Location` con la ruta del recurso
- [ ] Reintento con la misma clave: `200` y el mismo id

**Representación**
- [ ] La consulta devuelve la representación en `camelCase` con los tipos correctos y la fecha en RFC 3339

**Transiciones**
- [ ] Confirmar un pedido pendiente: `200 CONFIRMED`
- [ ] Confirmar dos veces: `422 INVALID_STATUS_TRANSITION`
- [ ] Cancelar un pedido confirmado: `422`
- [ ] Cancelar un pedido pendiente: `200 CANCELLED`
- [ ] Cancelar dos veces: `200` otra vez

**Listado acotado**
- [ ] La lista es `{data, meta}` con a lo sumo `limit` elementos
- [ ] `meta` repite `page` y `limit`
- [ ] Sin `limit`, se usan 20
- [ ] Del más reciente al más antiguo
- [ ] `limit` mayor que 100: `400`
- [ ] Filtro de estado desconocido: `400`
- [ ] El filtro `createdBefore` funciona
