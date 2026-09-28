# Anexo F — Repositorio `-api-gateway`

Repositorio: `<abbr>-api-gateway` · Norma: numeral 5.6

La única puerta de entrada del sistema. **Es configuración, no código**, y por eso no
tiene variante por lenguaje: un gateway escrito a mano es una aplicación más que
alguien debe mantener; un proxy declarativo no. Si el equipo necesita lógica que un
proxy no expresa, eso es una decisión para un ADR.

## Estructura esperada

```
<abbr>-api-gateway/
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   └── Dockerfile
├── nginx/
│   ├── conf.d/
│   │   ├── 00-resolver.conf
│   │   ├── 10-security.conf
│   │   └── 20-server.conf
│   ├── routes/
│   │   ├── _health.conf
│   │   ├── orders.conf
│   │   └── workflow.conf
│   ├── snippets/
│   │   └── headers.conf
│   └── nginx.conf
├── tests/
│   └── smoke.sh
├── .env.example
├── .gitattributes
├── .gitignore
└── README.md
```

## Qué va en cada parte

| Parte | Contenido |
|---|---|
| `nginx/nginx.conf` | ajustes globales: formato del registro en JSON con `correlationId`, tamaño máximo de cuerpo, inclusión de `conf.d/` |
| `nginx/conf.d/00-resolver.conf` | resolución DNS **por petición** (ver más abajo) |
| `nginx/conf.d/10-security.conf` | zonas de límite de tasa, orígenes CORS permitidos, el `X-Correlation-Id` y el filtro de credencial |
| `nginx/conf.d/20-server.conf` | el servidor: sus propios errores con el sobre común e inclusión de las rutas |
| `nginx/snippets/headers.conf` | los encabezados que lleva toda respuesta |
| `nginx/routes/<dominio>.conf` | **un archivo por dominio**: sus `location`, el filtro de credencial y el `proxy_pass` a su servicio |
| `nginx/routes/workflow.conf` | `/api/v1/sagas` hacia el `-workflow` |
| `nginx/routes/_health.conf` | `/health` del propio gateway |
| `tests/smoke.sh` | las comprobaciones que el gateway debe pasar |
| `deploy/` | imagen de NGINX con la configuración y el `compose.yml`: el gateway es el **único** servicio que publica un puerto al host |

## Qué hace el gateway y qué no

| Responsabilidad | Dónde |
|---|---|
| Enrutar `/api/v1/<dominio>/…` al servicio correcto, y `/api/v1/sagas` al *workflow* | aquí |
| Rechazar una petición protegida que llega **sin** encabezado `Authorization` | aquí (filtro barato) |
| Límite de tasa y CORS | aquí |
| Reutilizar o generar `X-Correlation-Id`, pasarlo al servicio, devolverlo y registrarlo | aquí |
| Responder **sus propios** errores con el sobre común | aquí |
| **Validar** el token (firma, expiración, *claims*) | **en cada servicio**, nunca solo aquí |
| Reglas de negocio | nunca aquí |
| Base de datos y migraciones con Liquibase | **nunca aquí**: el gateway no guarda estado. Cada base y sus migraciones viven en su `-db` (Anexos A y B) y se ejecutan desde `-infra` (Anexo G) |

Los servicios alcanzados **internamente** no pasan por el gateway, así que no pueden
dar por hecho que otro validó el token.

## Sus propios errores

| Situación | Respuesta |
|---|---|
| Ruta protegida sin credencial | `401 {"error":"UNAUTHORIZED", …, "traceId"}` |
| Ruta que no existe | `404 {"error":"NOT_FOUND", …}` |
| Límite de tasa superado | `429 {"error":"TOO_MANY_REQUESTS", …}` con `Retry-After` |
| El servicio de destino no responde | `503 {"error":"SERVICE_UNAVAILABLE", …}` |

Los errores que devuelve un servicio pasan intactos: ya usan el sobre. El `traceId` es
el mismo `X-Correlation-Id` que llega al servicio, así que una queja de un usuario con
su referencia lleva directo a la línea del registro.

## CORS

Solo los orígenes del contenedor. Se permiten `Authorization`, `Content-Type`,
`Idempotency-Key` y `X-Correlation-Id`, y se **exponen** `X-Correlation-Id` y
`Location` para que el navegador pueda leerlos. Un origen desconocido no recibe
`Access-Control-Allow-Origin`. Nunca `*` junto con credenciales.

## Por qué resuelve los nombres en cada petición

Con un bloque `upstream` estático, NGINX resuelve los servicios **una sola vez, al
arrancar**. Si un dominio está caído en ese momento, el gateway no arranca —y con él
cae la puerta de entrada de *todo* el sistema. El gateway debe resolver por DNS **en
cada petición** (`resolver 127.0.0.11` y una variable en `proxy_pass`): si un dominio
cae, **esa ruta** responde `503` y todas las demás siguen funcionando.

## Por qué `snippets/headers.conf`

NGINX descarta los `add_header` de un nivel exterior en cuanto una `location` declara
uno propio. Por eso los encabezados que toda respuesta debe llevar viven en un archivo
que se incluye en el servidor y en cada `location` que agrega los suyos. Repetir la
lista a mano termina, tarde o temprano, en una respuesta sin CORS o sin correlación.

## Agregar un dominio

1. Crear `routes/<dominio>.conf` con sus `location`, usando **una variable** en `proxy_pass` (`set $orders_api http://orders-api:8080; proxy_pass $orders_api;`): con una dirección fija, NGINX vuelve a resolver solo al arrancar.
2. Si la `location` agrega encabezados propios, incluir `snippets/headers.conf`.
3. Ejecutar `tests/smoke.sh`; el flujo `ci.yml` lo hace en cada Pull Request (Anexo I).

## Cómo se verifica

- [ ] `nginx -t` acepta la configuración
- [ ] `/health` responde `200`
- [ ] Una ruta inexistente responde `404` con el sobre común
- [ ] Una ruta protegida sin `Authorization` responde `401 UNAUTHORIZED` con el sobre
- [ ] Sin `X-Correlation-Id`, el gateway genera uno y lo devuelve; si el cliente lo envía, lo conserva
- [ ] El *preflight* (`OPTIONS`) desde el origen del contenedor responde `204` y permite `Idempotency-Key`
- [ ] La respuesta expone `X-Correlation-Id`; un origen desconocido no recibe `Access-Control-Allow-Origin`
- [ ] Superado el límite de tasa, responde `429` con el sobre y `Retry-After`
- [ ] Con un servicio detenido, su ruta responde `503` con el sobre y el gateway sigue sano
- [ ] El `X-Correlation-Id` que genera el navegador aparece en el registro del gateway y en el del servicio
