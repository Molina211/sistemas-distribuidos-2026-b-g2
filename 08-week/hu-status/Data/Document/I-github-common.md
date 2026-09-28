# Anexo I — Archivos comunes a todo repositorio de código

Norma: numerales 5.1, 9 y 16.7

Todo repositorio de código lleva, en sus tres ramas permanentes, una carpeta `.github/`
con estos archivos:

```
.github/
├── CODEOWNERS                    quién aprueba lo que entra a main — ya viene en el repositorio
├── pull_request_template.md      lo que todo Pull Request debe declarar — lo escribe el equipo
└── workflows/
    ├── ci.yml                    compila y prueba en cada Pull Request — lo escribe el equipo
    └── env-tracking.yml          ambiente de cada historia en el tablero — lo instala el docente
```

## `CODEOWNERS` — ya viene en el repositorio

Cada repositorio de código se entregó con su `CODEOWNERS`. Convierte "se requiere una
aprobación" en "se requiere **la aprobación del docente**" para lo que entra a `main`.
Sin él, dos integrantes del mismo equipo se aprobarían entre sí y `main` quedaría sin
control real. **No se modifica ni se elimina**: forma parte de las reglas de protección, y alterarlas es falta grave (numeral 13).

## `pull_request_template.md` — lo escribe el equipo

GitHub precarga este archivo en la descripción de cada Pull Request. Debe pedir, como
mínimo:

| Sección | Contenido |
|---|---|
| Historia de usuario | la referencia a la historia en el repositorio de documentación: `code-corhuila/<abbr>-docs#NN` |
| Qué cambia y por qué | en pocas líneas |
| Cómo se probó | las pruebas que cubren el cambio y el resultado del flujo `ci.yml` |
| Rastro de promoción | solo hacia `qa` o `main`: la lista de *commits* re-aplicados, cada uno con su línea `(cherry picked from commit <sha>)` (numeral 10) |
| Lista de verificación | sin secretos; sin cambios de esquema fuera del `-db`; contrato respetado |

## `workflows/ci.yml` — lo escribe el equipo

Compila y ejecuta las pruebas en cada Pull Request hacia `develop`, `qa` y `main`.
Depende del lenguaje:

| Repositorio | Qué ejecuta |
|---|---|
| Go | `gofmt -l .` (debe estar vacío), `go vet ./...`, `go test ./...` |
| Java | `mvn -B verify` con Java 21 |
| Python | `pip install -e ".[dev]"` y `python -m pytest` con Python 3.12 |
| C# | `dotnet test` con .NET 10 |
| React y Angular | `npm ci` y `npm run build` con Node 22 |
| `-api-gateway` | construir la imagen, `nginx -t` y `tests/smoke.sh` contra el contenedor |
| `-db` PostgreSQL | `db-ci.yml`: reconstrucción desde base vacía, reversión total y reaplicación, con Liquibase o con Flyway (Anexo A) |

Esqueleto de referencia:

```yaml
name: CI
on:
  pull_request:
    branches: [develop, qa, main]
  workflow_dispatch:
permissions:
  contents: read
jobs:
  build-and-test:
    runs-on: ubuntu-latest
    timeout-minutes: 10
    steps:
      - uses: actions/checkout@v4
      # preparar el lenguaje (actions/setup-go, setup-java, setup-python, setup-dotnet o setup-node)
      # y ejecutar los comandos de la tabla
```

- `permissions: contents: read`: el flujo solo necesita leer el código. Un flujo con permisos de escritura es un riesgo innecesario.
- `timeout-minutes`: un flujo sin límite puede quedar colgado y consumir los minutos de la organización.
- Las pruebas de integración contra el motor real (Anexo C) se omiten cuando no hay `TEST_DATABASE_URL`; el flujo no falla por eso.

Un Pull Request con el flujo en rojo no demuestra nada, aunque "funcione en mi máquina".

## `workflows/env-tracking.yml` — lo instala el docente

El tablero del equipo debe mostrar **en qué ambiente está cada historia** (`Dev`, `QA`,
`Main`), y ese estado debe coincidir con el historial real (numeral 16.7). Este flujo
lo actualiza al fusionar un Pull Request en `develop`, `qa` o `main`, a partir de la
historia que el Pull Request referencia. Lo instala el docente; mientras no esté
instalado en un repositorio, el equipo actualiza el campo **Environment** al fusionar.

## `.gitignore` y `.env.example`

Junto a `.github/`, todo repositorio de código tiene:

| Archivo | Exigencia |
|---|---|
| `.gitignore` | adecuado al lenguaje; ignora siempre `.env`, `*.pem` y las carpetas de compilación y dependencias |
| `.env.example` | **todas** las variables que el repositorio lee, con marcadores y nunca con valores reales |

## Cómo se verifica

- [ ] `CODEOWNERS` está presente y sin modificar en las tres ramas
- [ ] Cada Pull Request sigue `pull_request_template.md` y referencia su historia de usuario
- [ ] Los Pull Requests hacia `qa` y `main` traen su rastro de promoción
- [ ] `ci.yml` existe y está en verde en `develop`
- [ ] El estado del tablero coincide con el historial real
- [ ] Ningún `.env`, clave o token está versionado
