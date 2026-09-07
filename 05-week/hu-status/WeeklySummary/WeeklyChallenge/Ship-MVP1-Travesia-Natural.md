# Sesion 2 - Ship MVP1: promover a main, tag v1.0.0, DoD, demo y retrospectiva

## Objetivo de la sesion

Cerrar el Corte 1 del MVP de **Multitour**: promover `develop -> qa -> main` en
Backend y Frontend, etiquetar `v1.0.0` con su GitHub Release, verificar el checklist
de Definition of Done (`00-governance/definition-of-done.md`, repo Docs) con
evidencia real -no solo marcar casillas-, dejar constancia de que el sistema corre, y
hacer la retrospectiva del corte antes de arrancar Corte 2. La base de runtime que
sostiene el "sistema corriendo" de esta sesion es la containerizacion de la Sesion 1
(`Containerization-MVP1-Travesia-Natural.md`), no se repite aqui.

---

## 1. Backend - promocion y tag

Repo: `Molina211/Multitour-Monolito-Api` (el remote todavia resuelve por el nombre
anterior, `Travesia-Natural-Monolito`, via redirect de GitHub - renombrado detectado
el 2026-09-02, `CLAUDE.md` seccion 2).

- PR #27 (`qa -> main`) mergeado: commit `a867eb4`, 2026-09-06. Estrategia `-s ours`
  porque `main` habia quedado vacia por dos commits previos de preparacion
  ("Preparacion de la rama para merge futuro solido") - el merge normal habria
  borrado 191 archivos reales de `qa` por resolucion automatica de Git
  (modify/delete en archivos no tocados desde el merge-base); se detecto antes de
  confirmar y se rehizo con `-s ours` para preservar el arbol completo de `qa`.
- Tag `v1.0.0` -> `a867eb4`, con GitHub Release publicado (confirmado, no solo tag
  suelto).
- Estado abierto, dejado a proposito: un commit adicional (`9ce8a21`, ajuste del
  `README.md` para describir la rama `main` en vez de `qa`) quedo en `qa` esperando
  el merge final a `main` - reservado para que el responsable humano lo ejecute el
  mismo desde GitHub ("Merge pull request"), decision explicita de mantener esa
  ultima accion humana, no automatizada.
- 27 specs cerradas o en curso (`specs/001` a `specs/027`), 375 tests pasando (68
  clases: dominio puro sin mocks para agregados/value objects con logica, Mockito
  para los servicios de aplicacion), `mvn test` -> `BUILD SUCCESS`.

## 2. Frontend - promocion y tag

Repo: `Molina211/Multitour-Monolito-Portal`.

- `develop -> qa -> main`: `648047f` ("integrate HU-03 MVP Corte 1 frontend") ->
  `e486c41` ("promote MVP Corte 1 frontend to qa") -> `06fdefd` ("promote MVP Corte 1
  frontend to main"), 2026-09-06.
- Tag `v1.0.0` -> `06fdefd`, con GitHub Release publicado.
- Angular 18, flujos de Cliente y Operador completos; sigue en `localStorage`, sin
  integracion HTTP real con el Backend a proposito (los dos modulos cierran juntos
  antes de conectar).

## 3. Verificacion de la Definition of Done

Checklist completo en `00-governance/definition-of-done.md` (repo Docs). Verificacion
honesta contra el estado real del Backend a esta fecha - no se marcan items sin
evidencia:

| Item | Evidencia | Estado |
|---|---|---|
| Codigo implementa los criterios de aceptacion de la HU | 20/25 HU en Done, trazadas HU por HU con su spec y su clase de test en `04-requirements/traceability-matrix.md` | Cumple (parcial: 20/25) |
| Tests unitarios para logica de negocio nueva | 375 tests, 68 clases (dominio + aplicacion) | Cumple |
| La cobertura de tests no baja respecto a la base del proyecto | 44/44 servicios de aplicacion con test 1:1 (ver sesion anterior de este mismo repo) | Cumple |
| Todos los tests aplicables pasan en validacion local reproducible | `mvn test` -> `BUILD SUCCESS`, 0 failures/errors | Cumple |
| Revision de codigo por al menos 1 companero de equipo (PR review) | Proyecto de un solo desarrollador por modulo (Backend); no hay revision de pares registrada | No cumple - riesgo aceptado, documentado aqui en vez de ocultarlo |
| Sin deuda tecnica introducida sin registrar | Deuda registrada explicitamente en el propio codigo/specs (ej. `permitAll()` deliberado fuera de los 2 endpoints con JWT, capacidad de alojamiento sin validar) | Cumple |
| Si la historia afecta reservas, pagos, caja, descuentos o ejecucion: evidencia funcional | Specs 009/010/012/013/017/019/022/023, cada una con `PLAN-VERIFICACION.md` propio | Cumple |
| Si afecta multitenencia: validacion explicita de no mezcla de datos | `TenantGuardTest`, aislamiento de reservas por cliente (spec 016), `ADR-003` (Accepted) | Cumple |
| Concurrencia: intentos simultaneos sobre el ultimo cupo no producen sobreventa | Sin test de concurrencia todavia | No cumple - gap conocido, NFR-011 sigue en Pending en `traceability-matrix.md` |
| Accesibilidad WCAG 2.1 AA en el canal de cliente final | No verificado desde el Backend (evidencia, si existe, viviria en el repo Frontend) | No verificado en este alcance |
| Rendimiento <=3s en operaciones principales | No medido formalmente | No cumple - gap conocido, NFR-006 en Pending |
| Contrato OpenAPI actualizado si hay cambios de API | No existe un `openapi.yaml` que cubra las 25 specs reales (el unico existente es evidencia de un reto semanal anterior, subconjunto Must-have) | No cumple - gap conocido |
| Mergeable a `main` via Pull Request sin conflictos | PR #27 (Backend) y la cadena `develop->qa->main` (Frontend) mergeados sin conflictos sin resolver | Cumple |
| Decision tecnica significativa con ADR | `ADR-002` (topologia de despliegue) y `ADR-003` (aislamiento multitenant), ambos `Accepted` | Cumple |

**Excepciones permitidas usadas** (seccion "Allowed exceptions" del DoD, requieren
acuerdo explicito del Project Lead): revision de pares por par y tests de concurrencia
quedan documentados como riesgo aceptado para este corte, no resueltos en secreto.

## 4. Demo del sistema corriendo

El "sistema corriendo" de este corte es el mismo levantado en la Sesion 1
(`docker compose up`, los 3 contenedores -`postgres`, `backend`, `frontend`- arriba
juntos, `GET /health` del Backend respondiendo `{"status":"UP"}` contra Postgres real,
Frontend sirviendo su build estatico via Nginx). Ver
`Containerization-MVP1-Travesia-Natural.md` para la evidencia completa (capturas y
salida de consola). La presentacion en vivo ante el curso queda fuera del alcance de
esta evidencia escrita, por decision del responsable humano.

## 5. Retrospectiva del Corte 1

**Que salio bien**
- El walking skeleton (spec 001) sostuvo 26 specs mas sin romperse ni requerir un
  redisenio de la arquitectura hexagonal.
- Los 375 tests atraparon en seco varios errores de regresion durante el desarrollo
  (no hay incidentes de produccion que reportar porque no hay produccion, pero la
  suite se mantuvo verde spec tras spec).
- Ningun archivo se perdio en el proceso de promocion a `main`, pese a que estuvo cerca:
  ver el riesgo detectado abajo.

**Que no salio bien / riesgos que se materializaron**
- `main` quedo vacia (0 archivos) por dos commits de preparacion mal ejecutados
  ("Preparacion de la rama para merge futuro solido"); el merge de reconciliacion
  por defecto habria borrado 191 archivos reales de `qa` sin marcarlo como conflicto
  (Git resuelve un archivo no tocado en un lado y borrado en el otro como borrado
  limpio). Se detecto antes de confirmar el merge, no despues.
- La documentacion (`04-requirements/traceability-matrix.md`, `05-architecture/
  overview.md`, `03-product/*`) se quedo describiendo el estado del proyecto en el
  bootstrap de Spring Initializr durante 27 specs completas - una brecha de varios
  dias entre "lo que dice Docs" y "lo que existe en Backend" que solo se cerro en
  esta misma sesion.
- `CLAUDE.md` mismo mostro deriva sobre si mismo: afirmo en un `ESTADO-ACTUAL.md`
  anterior (2026-09-02) que se habia editado para documentar el catalogo completo de
  skills/agentes, y en la siguiente corrida (2026-09-05) volvia a tener 200 lineas y
  el catalogo viejo - sin poder determinarse si fue un revert intencional o un
  reporte erroneo, porque `CLAUDE.md` no esta versionado en este repositorio.
- Historias marcadas como pendientes en `CLAUDE.md` seccion 5 (`HU-IAM-001/002/003`,
  "aparcadas... sin fecha de retoma") resultaron estar, dos de las tres, ya
  implementadas y probadas desde antes de esa nota (specs 003/004, 2026-09-02 y
  2026-09-03) - la nota de la constitucion del proyecto no se habia actualizado tras
  el propio trabajo que describia.

**Leccion principal**
Promover a `main` y etiquetar un release no cierra el trabajo si la documentacion de
producto/requisitos no se sincroniza en el mismo corte: la brecha entre codigo y Docs
crece silenciosamente porque nada la fuerza a cerrarse sola. Se necesita un paso
explicito de "reflejar en Docs" dentro de la misma sesion que se promueve a `main`, no
como tarea aparte que se posponga.

## 6. Evidencia de compañeros de equipo (fuera de alcance de este fork)

Este documento solo puede verificar `05-week/hu-status/` dentro de este fork
(`Molina211/sistemas-distribuidos-2026-b-g2`). Confirmar que cada integrante del
equipo (Ariel, Fernanda) tiene su propio `05-week/hu-status/` completo en su propio
fork requiere revisarlo directamente en cada repositorio individual - no verificable
desde este entorno.

## 7. Hacia el Corte 2

Candidatos ya identificados en `04-requirements/traceability-matrix.md` (seccion
"Identified gaps") para arrancar Corte 2, sin comprometerlos todavia como plan
formal:
- FR-006 (validacion de capacidad de alojamiento, HU-RES-004).
- FR-008 (bloqueo de ajustes ordinarios durante ejecucion, HU-EXEC-002).
- FR-015C (reprogramacion de una reserva o servicio afectado, HU-RES-009).
- FR-018 (recuperacion de contraseña del cliente final, HU-IAM-003).
- Reconectar el flujo de autoservicio del Frontend (registro -> login -> reservar)
  revertido en la spec 027, coordinando con la autora del Frontend.
