# Anexo E — Repositorio `-workflow` (sagas)

Repositorio: `<abbr>-workflow` · Norma: numeral 5.8

Orquesta los procesos que **tocan varios dominios**. Un proceso que vive dentro de un
solo dominio no va aquí: va en el `-api` de ese dominio (numeral 5.8.1).

En un sistema distribuido no hay una transacción que abarque varias bases. Una **saga**
es la alternativa: una secuencia de pasos, cada uno con la acción que lo deshace. Si un
paso falla, se deshacen los anteriores en orden inverso.

El ejemplo es la saga *place-order*:

| Paso | Acción | Compensación |
|---|---|---|
| 1 | reservar inventario | liberar la reserva |
| 2 | cobrar | reembolsar |
| 3 | confirmar el pedido | cancelar el pedido |

## Estructura esperada

### Go
```
<abbr>-workflow/   (Go)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── cmd/
│   └── workflow/
│       └── main.go
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── internal/
│   ├── adapter/
│   │   ├── in/
│   │   │   └── httpapi/
│   │   │       ├── auth.go
│   │   │       ├── handler.go
│   │   │       ├── handler_test.go
│   │   │       └── middleware.go
│   │   └── out/
│   │       ├── participants/
│   │       │   ├── http.go
│   │       │   └── http_test.go
│   │       └── state/
│   │           └── memory.go
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   └── start.go
│   │   │   └── out/
│   │   │       └── participants.go
│   │   └── usecase/
│   │       ├── run_place_order_saga.go
│   │       └── run_place_order_saga_test.go
│   ├── correlation/
│   │   └── correlation.go
│   └── domain/
│       └── saga/
│           └── saga.go
├── .env.example
├── .gitignore
├── go.mod
└── README.md
```

### Java
```
<abbr>-workflow/   (Java)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── workflow-adapters/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── workflow/
│   │   │                           └── adapter/
│   │   │                               ├── in/
│   │   │                               │   └── http/
│   │   │                               │       ├── ApiError.java
│   │   │                               │       ├── AuthFilter.java
│   │   │                               │       ├── CorrelationFilter.java
│   │   │                               │       ├── ErrorHandler.java
│   │   │                               │       ├── HealthController.java
│   │   │                               │       ├── Rs256Verifier.java
│   │   │                               │       └── SagaController.java
│   │   │                               ├── out/
│   │   │                               │   ├── participants/
│   │   │                               │   │   └── HttpParticipants.java
│   │   │                               │   └── state/
│   │   │                               │       └── InMemorySagaStore.java
│   │   │                               └── Correlation.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── workflow/
│   │                               └── adapter/
│   │                                   └── out/
│   │                                       └── participants/
│   │                                           └── HttpParticipantsTest.java
│   └── pom.xml
├── workflow-app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── co/
│   │   │   │       └── edu/
│   │   │   │           └── corhuila/
│   │   │   │               └── abbr/
│   │   │   │                   └── workflow/
│   │   │   │                       └── app/
│   │   │   │                           └── WorkflowApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── workflow/
│   │                               └── app/
│   │                                   └── WorkflowHttpTest.java
│   └── pom.xml
├── workflow-core/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── co/
│   │   │           └── edu/
│   │   │               └── corhuila/
│   │   │                   └── abbr/
│   │   │                       └── workflow/
│   │   │                           ├── application/
│   │   │                           │   ├── port/
│   │   │                           │   │   ├── in/
│   │   │                           │   │   │   ├── GetSaga.java
│   │   │                           │   │   │   └── StartPlaceOrderSaga.java
│   │   │                           │   │   └── out/
│   │   │                           │   │       ├── IdGenerator.java
│   │   │                           │   │       ├── Participants.java
│   │   │                           │   │       └── SagaStore.java
│   │   │                           │   └── usecase/
│   │   │                           │       └── RunPlaceOrderSaga.java
│   │   │                           └── domain/
│   │   │                               └── saga/
│   │   │                                   ├── SagaInstance.java
│   │   │                                   ├── SagaStatus.java
│   │   │                                   └── UnknownSagaException.java
│   │   └── test/
│   │       └── java/
│   │           └── co/
│   │               └── edu/
│   │                   └── corhuila/
│   │                       └── abbr/
│   │                           └── workflow/
│   │                               └── application/
│   │                                   └── usecase/
│   │                                       └── RunPlaceOrderSagaTest.java
│   └── pom.xml
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

### Python
```
<abbr>-workflow/   (Python)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── apps/
│   ├── workflow/
│   │   ├── __init__.py
│   │   └── __main__.py
│   └── __init__.py
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   └── workflow/
│       ├── adapter/
│       │   ├── inbound/
│       │   │   ├── http/
│       │   │   │   ├── __init__.py
│       │   │   │   ├── app.py
│       │   │   │   ├── auth.py
│       │   │   │   └── router.py
│       │   │   └── __init__.py
│       │   ├── outbound/
│       │   │   ├── participants/
│       │   │   │   ├── __init__.py
│       │   │   │   └── http.py
│       │   │   ├── state/
│       │   │   │   ├── __init__.py
│       │   │   │   └── memory.py
│       │   │   └── __init__.py
│       │   └── __init__.py
│       ├── application/
│       │   ├── port/
│       │   │   ├── inbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── start.py
│       │   │   ├── outbound/
│       │   │   │   ├── __init__.py
│       │   │   │   └── participants.py
│       │   │   └── __init__.py
│       │   ├── usecase/
│       │   │   ├── __init__.py
│       │   │   └── place_order_saga.py
│       │   └── __init__.py
│       ├── domain/
│       │   ├── saga/
│       │   │   ├── __init__.py
│       │   │   └── saga.py
│       │   └── __init__.py
│       ├── __init__.py
│       └── correlation.py
├── tests/
│   ├── test_http.py
│   ├── test_participants.py
│   └── test_place_order_saga.py
├── .env.example
├── .gitignore
├── pyproject.toml
└── README.md
```

### C#
```
<abbr>-workflow/   (C#)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── src/
│   ├── Workflow.Adapters/
│   │   ├── In/
│   │   │   └── Http/
│   │   │       ├── ApiError.cs
│   │   │       ├── Middleware.cs
│   │   │       ├── Rs256Verifier.cs
│   │   │       └── SagaEndpoints.cs
│   │   ├── Out/
│   │   │   ├── Participants/
│   │   │   │   └── HttpParticipants.cs
│   │   │   └── State/
│   │   │       └── InMemorySagaStore.cs
│   │   ├── Correlation.cs
│   │   └── Workflow.Adapters.csproj
│   ├── Workflow.App/
│   │   ├── Program.cs
│   │   └── Workflow.App.csproj
│   └── Workflow.Core/
│       ├── RunPlaceOrderSaga.cs
│       ├── Saga.cs
│       └── Workflow.Core.csproj
├── tests/
│   ├── Workflow.Api.Tests/
│   │   ├── Workflow.Api.Tests.csproj
│   │   └── WorkflowTests.cs
│   └── Workflow.Core.Tests/
│       ├── RunPlaceOrderSagaTests.cs
│       └── Workflow.Core.Tests.csproj
├── .env.example
├── .gitignore
├── README.md
└── Workflow.sln
```

## Qué va en cada parte

| Parte | Responsabilidad |
|---|---|
| Dominio `saga` | la instancia: id, estado (`RUNNING`, `COMPLETED`, `COMPENSATED`, `FAILED`), pasos completados, paso que falló, detalle del error |
| Puertos de entrada | iniciar una saga y consultar su estado |
| Puertos de salida | un puerto por participante (cada uno con su *do* y su *undo*), el almacén de sagas y el generador de ids |
| Caso de uso (el orquestador) | ejecuta los pasos, persiste después de cada uno y compensa en orden inverso |
| Adaptador HTTP de entrada | `POST /api/v1/sagas/place-order` y `GET /api/v1/sagas/{id}`, con el mismo contrato de un `-api` |
| Adaptador de participantes | los clientes HTTP de los dominios que participan |
| Adaptador de estado | dónde se guarda cada saga |

## Las tres reglas de una saga

1. **Cada paso tiene su compensación**, declarada junto al paso. Un paso sin compensación no se puede deshacer, y por eso va al final.
2. **El estado se persiste después de cada paso.** Un orquestador que guarda el estado solo en memoria no puede reanudar tras un reinicio: una saga cortada a la mitad queda a medio hacer, y nadie sabe qué deshacer.
3. **Las compensaciones corren en orden inverso y son idempotentes.** Pueden ejecutarse más de una vez —por ejemplo, tras una caída durante la propia compensación.

Si una compensación falla, la saga queda en `FAILED`: la recuperación automática ya no
es segura y **una persona debe decidir**. Ocultar ese estado es peor que exponerlo.

## El contrato del workflow

| Aspecto | Regla |
|---|---|
| Entrada | Igual que un `-api` (Anexo C): valida el JWT (RS256), valida el cuerpo campo por campo, responde con el sobre común y en `camelCase` |
| Inicio idempotente | `Idempotency-Key` obligatoria: repetir la petición devuelve **la misma saga** con `200` y no ejecuta ningún paso de nuevo. El primer envío responde `201` con `Location` |
| Pasos idempotentes | Cada *do* envía `Idempotency-Key: <id-de-saga>:<paso>`, así un paso reintentado nunca reserva ni cobra dos veces |
| Credencial | Las llamadas a los participantes usan el `SERVICE_TOKEN` del workflow, no el token de la persona: ese puede vencer en medio de una compensación |
| Correlación | El `X-Correlation-Id` de la petición viaja a cada participante |
| Detalle del fallo | La respuesta dice `failedStep`; el detalle —nombres de servidores, códigos— queda en el estado de la saga y en el registro, **nunca** en la respuesta |
| Límites | Tiempo máximo por petición; reintentos acotados con *jitter* solo ante red, `429` y `5xx` (Anexo D) |

Respuesta de ejemplo cuando falla el cobro:

```json
{
  "id": "35920dc9-bd55-41a0-8e41-5832377383e5",
  "orderId": "9cfcfeec-c4b6-4d76-a0b4-f0db602d7caa",
  "status": "COMPENSATED",
  "completedSteps": ["reserve-stock"],
  "failedStep": "charge-payment"
}
```

## Decisión pendiente para cada equipo

Guardar las sagas en memoria sirve para probar el orquestador, pero no sobrevive a un
reinicio —que es exactamente lo que un almacén de sagas debe hacer—. La topología no
asigna repositorio `-db` a los transversales: **dónde vive el estado en producción es
una decisión que el equipo registra en un ADR** (numerales 4.2.3 y 5.8.4), con su
criterio dominante y su costo aceptado.

## Cómo se verifica

- [ ] Camino feliz: se ejecutan reservar → cobrar → confirmar, en ese orden, y la saga termina `COMPLETED`
- [ ] Si falla el cobro: se libera la reserva, no se confirma nada y la saga queda `COMPENSATED` con `failedStep`
- [ ] Si falla la confirmación: se reembolsa y luego se libera, en orden inverso
- [ ] Si falla una compensación: la saga queda `FAILED`
- [ ] Repetir la petición con la misma clave no ejecuta ningún paso de nuevo
- [ ] Cada paso envía su `Idempotency-Key` derivada de la saga
- [ ] La entrada responde `401` sin token, `400` con un detalle por campo, `201` al iniciar, `200` en el reintento y `404` con el sobre
- [ ] La respuesta nunca contiene el detalle interno del fallo
- [ ] De punta a punta, contra el `-api` real, el pedido queda `CONFIRMED` y cada participante recibe el token de servicio, su clave por paso y el mismo `X-Correlation-Id`
