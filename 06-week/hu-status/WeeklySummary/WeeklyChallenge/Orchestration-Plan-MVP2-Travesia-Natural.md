# Sesion 2 - Ambientes, matriz de configuracion y plan de orquestacion para MVP2

## Objetivo de la sesion

Planificar (no implementar todavia) como el Backend se ejecuta en tres ambientes
distintos, dejando explicitas las variables de entorno de cada uno, confirmando que
las ramas de git mapean 1:1 a esos ambientes, y troceando en historias pequenas y
verificables el trabajo de orquestacion que falta para sostener el MVP2.

## 1. Los tres ambientes

Son los mismos tres puntos del flujo de ramas ya vigente (`00-governance/git-conventions.md`
en Docs, `CLAUDE.md` seccion 4):

| Ambiente | Rama | Que es hoy |
|---|---|---|
| Development | `develop` | Unico ambiente que existe realmente: `docker compose up` local, Postgres en contenedor, secretos de desarrollo en `.env` (no comiteado). |
| QA | `qa` | No desplegado en ningun lado todavia - solo existe como rama de codigo. Cuando exista un despliegue real, sera una instancia separada (su propio Postgres, su propio `.env`), sin compartir datos ni secretos con dev. |
| Production | `main` | Tampoco desplegado - misma logica que QA: instancia propia, secretos propios, nunca commiteados, distintos de los de qa. |

## 2. Matriz de configuracion

Variables reales, tomadas de `.env.example` y `docker-compose.yml` del Backend
(Semana 05-06). No se inventan valores de qa/prod porque esos ambientes no existen
desplegados hoy - la fila de dev es la real, las de qa/prod son la forma que deberian
tener cuando se desplieguen:

| Variable | dev (`develop`) | qa | prod (`main`) |
|---|---|---|---|
| `POSTGRES_DB` | `multitour` (default en `.env.example`) | valor propio de qa, no compartido con dev | valor propio de prod, no compartido con dev/qa |
| `POSTGRES_USER` | `multitour` | valor propio de qa | valor propio de prod |
| `POSTGRES_PASSWORD` | `multitour` (dev, sin riesgo real) | secreto real, nunca commiteado | secreto real, nunca commiteado |
| `APP_JWT_SECRET` | `dev-only-secret-multitour-2026-...` (explicitamente marcado como no apto para produccion en `application.properties`) | secreto propio generado para qa | secreto propio generado para produccion, distinto del de qa |
| `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` | armadas en `docker-compose.yml` a partir de las `POSTGRES_*` de arriba | idem, contra la instancia de Postgres de qa | idem, contra la instancia de Postgres de produccion |

Ninguna celda de qa/prod es un secreto real: son placeholders describiendo que la
variable existe y debe diferir por ambiente, no su valor.

## 3. Secretos fuera de git

Patron ya vigente desde la Semana 05, confirmado aqui como estandar para los tres
ambientes: `.env.example` documenta el *nombre* de cada variable con un valor de
ejemplo (nunca uno real de qa/prod); `.env` (valores reales) esta en `.gitignore`.
Cuando existan qa/prod, cada uno vive en su propio `.env` local al servidor que lo
corre - nunca commiteado ni compartido entre ambientes.

## 4. Mapeo rama <-> ambiente

Confirmado, sin cambios frente a `git-conventions.md`:

- `develop` -> Development
- `qa` -> QA
- `main` -> Production

Cada merge entre estos puntos (`develop -> qa`, `qa -> main`) sigue pidiendo
autorizacion explicita (regla 4 de `CLAUDE.md`), igual que el despliegue real a cada
ambiente el dia que exista.

## 5. Historias de orquestacion para MVP2

Backlog, no implementado en esta sesion. Cada historia con criterio de aceptacion
verificable:

### ORCH-01 - `.env.qa.example` y `.env.prod.example`
Como equipo, necesito un ejemplo de configuracion por ambiente ademas de
`.env.example` (dev), para no reusar por error valores de desarrollo en qa o
produccion.
- [ ] Existen `.env.qa.example` y `.env.prod.example` en el repo Backend, con los
      mismos nombres de variable que `.env.example` pero valores de placeholder.
- [ ] `.gitignore` cubre cualquier `.env.qa` / `.env.prod` real, no solo `.env`.

### ORCH-02 - Perfil de Spring por ambiente
Como equipo, necesito que el Backend distinga configuracion por ambiente (ej. nivel
de log, `ddl-auto`) sin depender solo de las variables de datasource.
- [ ] Existen `application-qa.properties` y `application-prod.properties` (o
      equivalente) con al menos el nivel de logging y cualquier flag que deba
      diferir de dev.
- [ ] `docker compose up` en dev sigue funcionando igual que hoy sin depender del
      perfil nuevo.

### ORCH-03 - Evidencia de salud antes de promover a `qa`
Como equipo, necesito que promover `develop -> qa` incluya evidencia de que el
`docker compose up` completo (con el healthcheck de la spec 028) funciona contra el
codigo a promover, no solo que compila.
- [ ] El PR de promocion a `qa` incluye, en su descripcion, la salida de
      `docker compose ps` mostrando los 3 contenedores `healthy`/`up`.
- [ ] Si el healthcheck falla, el PR no se aprueba.

### ORCH-04 - Decidir donde vive cada ambiente real
Como equipo, necesito decidir y documentar (no implementar todavia) donde correra
fisicamente qa y produccion, porque hoy ninguno de los dos existe fuera del codigo
de su rama.
- [ ] Existe una decision registrada (ADR corto en el repo Docs, seccion 3 de
      `CLAUDE.md`) sobre donde se desplegaran qa y produccion para MVP2, o una nota
      explicita de que sigue sin decidirse.

## Nota de reconciliacion

Esta sesion no implementa nada del `docker-compose.yml` ni crea secretos reales - es
planeacion. El unico ambiente que existe hoy es `develop` (dev local), ya documentado
en la Sesion 1 de esta semana (`Healthcheck-Docker-MVP1-Travesia-Natural.md`) y en la
Semana 05 (`Containerization-MVP1-Travesia-Natural.md`). Las historias ORCH-01 a
ORCH-04 quedan como backlog para cuando arranque el trabajo de orquestacion de MVP2 -
no se abre ninguna rama `hu-back-{N}-dev` para ellas en esta sesion.
