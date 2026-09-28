# Anexo D — Repositorio `-worker`

Repositorio: `<abbr>-worker` · Norma: numeral 5.7

Todo lo que ocurre **sin que nadie lo pida**: vencimientos, multas, reindexaciones,
correos, reportes programados. Si el sistema hace algo "cada noche" o "cuando pasen
tres días", vive aquí. Tiene la misma forma hexagonal que un `-api` (Anexo C); lo que
cambia es el **adaptador de entrada**: un planificador en lugar de HTTP.

El ejemplo cancela los pedidos que llevan en `PENDING` más tiempo del permitido, usando
**el API** de pedidos, nunca su base:

```
GET  /api/v1/orders?status=PENDING&createdBefore=<ahora - TTL>&limit=<tamaño de lote>
POST /api/v1/orders/{id}/cancel
```

## Estructura esperada

Una estructura por lenguaje; el equipo usa la del lenguaje que eligió.

### Go
```
<abbr>-worker/   (Go)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── cmd/
│   └── worker/
│       └── main.go
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── internal/
│   ├── adapter/
│   │   ├── in/
│   │   │   └── scheduler/
│   │   │       └── scheduler.go
│   │   └── out/
│   │       └── orders/
│   │           ├── client.go
│   │           └── client_test.go
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   └── job.go
│   │   │   └── out/
│   │   │       └── orders.go
│   │   └── usecase/
│   │       ├── expire_stale_orders.go
│   │       └── expire_stale_orders_test.go
│   ├── config/
│   │   └── config.go
│   └── correlation/
│       └── correlation.go
├── .env.example
├── .gitignore
├── go.mod
└── README.md
```

### Java
```
<abbr>-worker/   (Java)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── worker-adapters/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── worker/
│   │   │                           └── adapter/
│   │   │                               ├── in/
│   │   │                               │   └── scheduler/
│   │   │                               │       └── JobRunner.java
│   │   │                               ├── out/
│   │   │                               │   └── orders/
│   │   │                               │       └── OrdersHttpClient.java
│   │   │                               └── Correlation.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── worker/
│   │                               └── adapter/
│   │                                   └── out/
│   │                                       └── orders/
│   │                                           └── OrdersHttpClientTest.java
│   └── pom.xml
├── worker-app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── co/
│   │       │       └── edu/
│   │       │           └── corhuila/
│   │       │               └── abbr/
│   │       │                   └── worker/
│   │       │                       └── app/
│   │       │                           └── WorkerApplication.java
│   │       └── resources/
│   │           └── application.yml
│   └── pom.xml
├── worker-core/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── worker/
│   │   │                           └── application/
│   │   │                               ├── port/
│   │   │                               │   ├── in/
│   │   │                               │   │   └── Job.java
│   │   │                               │   └── out/
│   │   │                               │       └── OrdersApi.java
│   │   │                               └── usecase/
│   │   │                                   └── ExpireStaleOrders.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── worker/
│   │                               └── application/
│   │                                   └── usecase/
│   │                                       └── ExpireStaleOrdersTest.java
│   └── pom.xml
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

### Python
```
<abbr>-worker/   (Python)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── apps/
│   ├── worker/
│   │   ├── __init__.py
│   │   └── __main__.py
│   └── __init__.py
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   └── worker/
│       ├── adapter/
│       │   ├── inbound/
│       │   │   ├── scheduler/
│       │   │   │   ├── __init__.py
│       │   │   │   └── scheduler.py
│       │   │   └── __init__.py
│       │   ├── outbound/
│       │   │   ├── orders/
│       │   │   │   ├── __init__.py
│       │   │   │   └── client.py
│       │   │   └── __init__.py
│       │   └── __init__.py
│       ├── application/
│       │   ├── port/
│       │   │   ├── inbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── job.py
│       │   │   ├── outbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── orders.py
│       │   │   └── __init__.py
│       │   ├── usecase/
│       │   │   ├── __init__.py
│       │   │   └── expire_stale_orders.py
│       │   └── __init__.py
│       ├── __init__.py
│       └── correlation.py
├── tests/
│   ├── test_expire_stale_orders.py
│   └── test_orders_client.py
├── .env.example
├── .gitignore
├── pyproject.toml
└── README.md
```

En Python basta la biblioteca estándar, también para el cliente HTTP (`urllib`).

### C#
```
<abbr>-worker/   (C#)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   ├── Worker.Adapters/
│   │   ├── In/
│   │   │   └── Scheduling/
│   │   │       └── JobHostedService.cs
│   │   ├── Out/
│   │   │   └── Orders/
│   │   │       └── OrdersHttpClient.cs
│   │   ├── Correlation.cs
│   │   └── Worker.Adapters.csproj
│   ├── Worker.App/
│   │   ├── Program.cs
│   │   └── Worker.App.csproj
│   └── Worker.Core/
│       ├── Jobs.cs
│       └── Worker.Core.csproj
├── tests/
│   ├── Worker.Adapters.Tests/
│   │   ├── OrdersHttpClientTests.cs
│   │   └── Worker.Adapters.Tests.csproj
│   └── Worker.Core.Tests/
│       ├── ExpireStaleOrdersTests.cs
│       └── Worker.Core.Tests.csproj
├── .env.example
├── .gitignore
├── README.md
└── Worker.sln
```

## Qué va en cada parte

| Parte | Responsabilidad |
|---|---|
| Puerto de entrada `Job` | una unidad de trabajo programado: nombre y `run`, que devuelve cuántos procesó y cuántos fallaron |
| Puerto de salida del dominio ajeno (`OrdersApi`) | el otro dominio **tal como lo ve el worker**: solo las operaciones de su contrato público |
| Caso de uso (`ExpireStaleOrders`) | la regla: qué es "vencido", cuántos por corrida, qué hacer con un fallo |
| Adaptador de entrada (planificador) | cada cuánto corre; un identificador de correlación y un tiempo máximo por corrida |
| Adaptador de salida (cliente HTTP) | token, correlación, tiempo máximo por petición, reintentos, lectura del sobre de error |
| Correlación | un componente neutral que el planificador llena y el cliente lee: ningún adaptador conoce al otro |
| Raíz de composición | lee la configuración y declara todos los límites |

## Reglas

1. **Todo trabajo debe ser seguro de ejecutar dos veces.** Dos corridas solapadas, un reintento tras un *timeout* o un reinicio a mitad de lote son operación normal. En el ejemplo, lo que lo hace seguro es que `cancel` es idempotente en el dominio de pedidos.
2. **Un fallo en un elemento no detiene el lote.** Se cuenta, se registra y queda para la siguiente corrida.
3. **Cada corrida es acotada**: lee como máximo `BATCH_SIZE` elementos —el `limit` del API— y se detiene al vencer su tiempo máximo. Un lote sin límite es una corrida que un día no termina.
4. **Credencial propia.** Cada llamada lleva `Authorization: Bearer $SERVICE_TOKEN`: el API valida al worker como a cualquier otro llamador. El token lo emite el servicio de identidad y nunca se versiona.
5. **Correlación por corrida.** Un `X-Correlation-Id` por corrida viaja en cada llamada y aparece en cada línea del registro: una corrida se sigue a través de todo el sistema.
6. **Cada petición tiene su tiempo máximo.** Una llamada sin él puede colgar una corrida para siempre.
7. **Reintentos acotados, con espera exponencial y variación aleatoria (*jitter* completo), y solo ante errores que un reintento puede corregir**: red, `429` y `5xx`. Un `4xx` es un error del que llama: reintentarlo no cambia nada. Sin variación, muchos procesos que fallan juntos reintentan juntos y prolongan la caída.
8. **El worker alcanza a otros dominios solo por su API**, nunca por su base.
9. **No expone interfaz de negocio.** A lo sumo, un punto de salud.

## La espera entre reintentos

Con *jitter* completo, la espera antes del intento `n` es un valor **aleatorio entre 0 y
`base × 2ⁿ`**. Con `base = 200 ms`: hasta 400 ms antes del segundo intento, hasta 800 ms
antes del tercero. Una espera fija, o exponencial sin azar, hace que todos los procesos
que fallaron al mismo tiempo vuelvan a golpear al mismo tiempo.

## Configuración

| Variable | Significado |
|---|---|
| `ORDERS_API_URL` | dirección del API, por el nombre del servicio en la red `platform` |
| `SERVICE_TOKEN` | credencial del worker |
| `EXPIRE_EVERY` / `PENDING_TTL` | cada cuánto corre y cuánto tiempo es "vencido" |
| `BATCH_SIZE` · `RUN_TIMEOUT` | límites de cada corrida |
| `HTTP_TIMEOUT` · `HTTP_ATTEMPTS` | límites de cada petición |

## Cómo se verifica

- [ ] Con tres elementos y un fallo en el segundo, el lote procesa dos, cuenta uno fallido y **continúa hasta el tercero**
- [ ] Una corrida pide exactamente `BATCH_SIZE` elementos
- [ ] Una corrida con su tiempo vencido no procesa nada más
- [ ] Cada llamada lleva el token de servicio y el `X-Correlation-Id` de su corrida
- [ ] El cliente lee la lista como `{data, meta}`
- [ ] `503` y `429` se reintentan hasta el límite; `422` y `401` no se reintentan
- [ ] Contra el API real, una corrida procesa su lote y el mismo `correlationId` aparece en todas las peticiones que recibió el API
- [ ] Si el API falla, la corrida se registra con su `traceId` y la siguiente corrida funciona
