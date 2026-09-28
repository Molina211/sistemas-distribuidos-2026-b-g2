# Anexo G — Repositorio `-infra`

Repositorio: `<abbr>-infra` · Norma: numeral 5.9

Ensambla el sistema completo. **Compone; no contiene.** Cada repositorio ejecutable
trae su propio `deploy/compose.yml`; este repositorio los incluye, crea la red
compartida y agrega lo transversal que no pertenece a ningún dominio: la observabilidad.

## Estructura esperada

```
<abbr>-infra/
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── env/
│   ├── .env.develop.example
│   ├── .env.main.example
│   └── .env.qa.example
├── observability/
│   ├── grafana-datasources.yaml
│   ├── otel-collector.yaml
│   └── prometheus.yml
├── scripts/
│   ├── dev-keys.sh
│   ├── dev-token.sh
│   ├── down.sh
│   └── up.sh
├── .gitattributes
├── .gitignore
├── compose.yml
└── README.md
```

## Qué va en cada parte

| Parte | Contenido |
|---|---|
| `compose.yml` | **solo** `include` de cada repositorio hermano, la red `platform` externa y la observabilidad |
| `env/.env.<ambiente>.example` | uno por ambiente (`develop`, `qa`, `main`): nombres y marcadores, nunca valores |
| `observability/` | configuración del recolector OpenTelemetry, Prometheus y Grafana |
| `scripts/up.sh` · `down.sh` | verifican el `.env`, crean la red si no existe y levantan o bajan todo |
| `scripts/dev-keys.sh` · `dev-token.sh` | identidad de **desarrollo** (ver más abajo) |
| `.gitignore` | ignora `.env`, `keys/` y `*.pem` |
| `.gitattributes` | los `.sh` con fin de línea `LF`: con `CRLF` fallan dentro de un contenedor Linux |

## La regla de composición

| Repositorio | Aporta |
|---|---|
| cada `-db` | su propia instancia de base de datos y su ejecutor de migraciones |
| cada `-api`, `-worker`, `-workflow` | su propio servicio |
| `-api-gateway`, `-front` | su propio servicio |
| **`-infra`** | **solo** los `include`, la red `platform`, la observabilidad y las claves de desarrollo |

Así se cumple que **cada dominio tiene su propia instancia** de base de datos, y cada
equipo puede levantar su dominio de forma aislada sin depender del resto.

## Solo el gateway se publica al host

Los servicios declaran `expose`, no `ports`: son alcanzables en la red `platform`
pero **no desde fuera**. Si cada servicio publicara su puerto, cualquier cliente
podría saltarse el gateway —y con él el límite de tasa, la correlación y la
observabilidad de esa llamada.

## Identidad en desarrollo

Hasta que exista el dominio de identidad, los servicios necesitan una clave pública
para validar tokens y alguien que emita esos tokens. Dos scripts resuelven eso **solo
en `develop`**:

| Script | Qué hace |
|---|---|
| `dev-keys.sh` | genera un par RSA de 2048 bits en `keys/` (ignorado por git) con `openssl genpkey`, extrae la clave pública con `openssl pkey -pubout`, y escribe en `.env` `JWT_PUBLIC_KEY` —el PEM en una línea, con `\n` literales— y un `SERVICE_TOKEN` para el *worker* y el *workflow* |
| `dev-token.sh <sujeto> [minutos]` | arma un JWT: cabecera `{"alg":"RS256","typ":"JWT"}`, carga `{"sub","iat","exp"}`, ambas en base64url; firma `cabecera.carga` con `openssl dgst -sha256 -sign keys/jwt-private.pem` y concatena la firma en base64url |

En `qa` y `main` la clave pública y los tokens de servicio vienen del servicio de
identidad, como secretos del ambiente. Las claves de desarrollo **nunca** salen de
`develop` ni se versionan.

## Migraciones con Liquibase o Flyway en la plataforma

`-infra` **no contiene ninguna migración** y no conoce la estructura de ninguna base.
Cada `-db` trae, en su `deploy/compose.yml`, además de su instancia, un **ejecutor de
migraciones**: un contenedor de **Liquibase o de Flyway**, según la herramienta que el
equipo eligió para ese dominio, que aplica las migraciones de ese repositorio (Anexos
A y B). Una plataforma puede tener dominios con Liquibase y dominios con Flyway: para
`-infra` no hay diferencia, porque cada ejecutor se invoca igual. Como `-infra` incluye ese archivo, el ejecutor queda
disponible en la plataforma con su nombre, por ejemplo `orders-db-migrate`.

### Cómo se define el ejecutor en cada `-db`

| Elemento | Valor | Por qué |
|---|---|---|
| Imagen | `liquibase/liquibase` o `flyway/flyway` con **versión fija**; en MongoDB, una imagen propia de Liquibase que agrega `lpm add liquibase-mongodb mongodb --global` (Anexo B) | La misma versión en todos los ambientes; con `latest`, una migración puede comportarse distinto mañana |
| `profiles: [tooling]` | el ejecutor **no** arranca con `up` | Migrar es un acto deliberado, no algo que ocurra en cada reinicio de la plataforma |
| `depends_on` con `condition: service_healthy` | espera a que la base responda su chequeo de salud | Sin él, la herramienta intenta conectarse a una base que todavía está arrancando y falla |
| Volumen `../:/workspace:ro` | monta el propio repositorio `-db` **solo lectura** | El ejecutor lee las migraciones; nunca las modifica |
| Punto de entrada | Liquibase: `--search-path` y `--changelog-file=changelog/changelog-master.yaml`. Flyway: `-workingDirectory=/workspace -configFiles=flyway.toml` | La herramienta lee la configuración del propio `-db`, no una copia |
| Dirección de la base | el **nombre del servicio** de la base en la red `platform` (`jdbc:postgresql://orders-db:5432/orders` o `mongodb://orders-db:27017/orders?replicaSet=rs0`): en Liquibase como `--url`, en Flyway como variable `FLYWAY_URL` | Dentro de la red no se usa `localhost` ni un puerto publicado |
| Credenciales | variables del `.env` de `-infra` (`ORDERS_DB_USER`, `ORDERS_DB_PASSWORD`), que el ejecutor recibe como `--username`/`--password` (Liquibase) o `FLYWAY_USER`/`FLYWAY_PASSWORD` (Flyway); la contraseña se exige con `${ORDERS_DB_PASSWORD:?…}` | Ninguna credencial queda escrita en un repositorio; si falta, el comando falla con un mensaje claro |
| `command` | la acción por defecto: `update` (Liquibase) o `migrate` (Flyway) | Cualquier otra acción se pasa al ejecutarlo |

### Cómo se opera

Desde `-infra`, con la plataforma levantada:

| Acción | Liquibase | Flyway |
|---|---|---|
| Aplicar lo pendiente | `… run --rm orders-db-migrate` | `… run --rm orders-db-migrate` |
| Ver qué falta y qué se aplicó | `… orders-db-migrate status --verbose` y `history` | `… orders-db-migrate info` |
| Verificar que nada se editó | `… orders-db-migrate validate` | `… orders-db-migrate validate` |
| Revertir el último cambio | `… orders-db-migrate rollback-count 1` | el script `U` de esa versión, con `psql` (Anexo A) |

`…` es `docker compose --env-file .env`, ejecutado desde `-infra`.

- `run --rm` crea un contenedor de un solo uso, que se elimina al terminar: el ejecutor no queda corriendo.
- **Una vez por cada `-db`**: con tres dominios hay tres ejecutores, y cada uno migra solo su base.
- Repetir la migración no aplica nada (`Run: 0` en Liquibase, `No migration necessary` en Flyway): es la prueba de que las migraciones son incrementales.
- Las migraciones se versionan y se prueban en el `-db` (`db-ci.yml`, Anexo A). `-infra` solo las **ejecuta**.

### Orden en `qa` y `main`

La migración de una base se aplica **antes** de desplegar la versión del `-api` que
la necesita. Si el cambio rompe a la versión desplegada —renombrar o eliminar una
columna, cambiar su tipo—, va en dos releases, expandir y contraer (Anexo A, regla 13):
nunca se despliega un servicio que espera una columna que todavía no existe, ni se
retira una columna que un servicio desplegado todavía lee.

## Arranque

```bash
cp env/.env.develop.example .env      # y fijar ORDERS_DB_PASSWORD
./scripts/dev-keys.sh                 # solo develop
./scripts/up.sh                       # verifica .env, crea la red y levanta todo
docker compose --env-file .env run --rm orders-db-migrate      # una vez por cada -db
curl -H "Authorization: Bearer $(./scripts/dev-token.sh alice)" http://localhost:8000/api/v1/orders
```

Mientras un esquema no esté migrado, el API de ese dominio responde
`500 INTERNAL_ERROR` y el *worker* registra la corrida fallida con su `traceId`; la
siguiente corrida funciona. Ese es el comportamiento esperado: nada espera para
siempre y nada falla en silencio.

## Los repositorios se clonan como hermanos

```
espacio-de-trabajo/
  <abbr>-infra/          <- desde aquí se levanta todo
  <abbr>-api-gateway/
  <abbr>-orders-db/
  <abbr>-orders-api/
  <abbr>-worker/
  <abbr>-workflow/
  …
```

Las rutas de `include` asumen esa disposición. Un hermano faltante falla de forma
visible al levantar: nada se omite en silencio.

## Cómo se verifica

- [ ] Con los repositorios clonados como hermanos, `up.sh` levanta la plataforma completa
- [ ] Solo el gateway y la observabilidad publican puertos al host; ningún servicio de dominio
- [ ] Cada `-db` levanta su propia instancia y su migración se aplica con el comando documentado; repetirla aplica cero cambios
- [ ] `-infra` no contiene migraciones ni sentencias de base de datos: solo invoca el ejecutor que trae cada `-db`
- [ ] Los ejecutores de migraciones (Liquibase o Flyway) usan imagen con versión fija, no arrancan con `up` y esperan a que su base esté sana
- [ ] Ninguna credencial de base de datos está escrita en un `compose.yml`: todas vienen del `.env`
- [ ] Por el gateway se crea y se lista un recurso con un token emitido por `dev-token.sh`
- [ ] El *worker* y el *workflow* se autentican con `SERVICE_TOKEN`
- [ ] El mismo `X-Correlation-Id` aparece en los registros de todos los servicios que tocó una petición
- [ ] El repositorio no contiene `.env`, claves ni tokens
