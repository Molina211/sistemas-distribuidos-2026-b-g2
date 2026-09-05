# Semana 1 — Configuración del repositorio, arquitectura y backlog inicial

## Sistemas Distribuidos

**Proyecto:** Travesía Natural  
**Equipo:** ErrorCapa8  
**Estudiante:** Jhon Sebastian Molina Fierro  
**GitHub:** Molina211

---

## 1. Configuración del repositorio de perfil

El repositorio de perfil personal utilizado para la configuración solicitada es:

- Repositorio: `Molina211/Molina211`
- URL: https://github.com/Molina211/Molina211

Este repositorio corresponde al perfil de GitHub del estudiante y permite mantener la configuración personal asociada a la actividad.

### Estado

**Completado.**

---

## 2. Fork / bifurcación del repositorio académico

El trabajo académico del estudiante se encuentra en:

- Repositorio: `Molina211/sistemas-distribuidos-2026-b-g2`
- URL: https://github.com/Molina211/sistemas-distribuidos-2026-b-g2

La bifurcación contiene la estructura correspondiente a las semanas del curso y el registro semanal del estudiante.

### Estado

**Completado.**

---

## 3. Weekly Status de la Semana 1

Se creó y mantuvo el archivo:

```text
01-week/hu-status/README.md
```

El archivo contiene la configuración de identificación del estudiante y del equipo:

- `FULL_NAME: Jhon Sebastian Molina Fierro`
- `GITHUB_USER: Molina211`
- `TEAM: ErrorCapa8`
- `SPRINT_GOAL: Course introduction and creation of the initial PDR`

También registra las contribuciones realizadas durante la primera semana y los bloqueos identificados.

### Evidencia

El archivo se encuentra dentro del repositorio académico:

https://github.com/Molina211/sistemas-distribuidos-2026-b-g2/blob/main/01-week/hu-status/README.md

El historial del repositorio contiene commits correspondientes a la Semana 1 asociados con este archivo.

### Estado

**Completado y evidenciado.**

---

# 4. Repositorio de documentación

Como equipo se creó el repositorio:

- `Travesia-Natural-docs`
- https://github.com/Molina211/Travesia-Natural-docs

Este repositorio se planteó como espacio para almacenar la documentación técnica y arquitectónica del proyecto.

Durante la evolución posterior del proyecto este repositorio no se convirtió en la fuente documental principal utilizada para todo el sistema, pero sí contiene evidencia de la actividad solicitada en Semana 1.

### Estado

**Completado.**

---

# 5. ADR-001

## ADR-001 — Selección del estilo arquitectónico y stack tecnológico principal

### Estado

**Propuesto — 2026-08-15**

El repositorio `Travesia-Natural-docs` contiene el archivo:

```text
ADRs/ADR-001.md
```

Este ADR existe como evidencia del trabajo realizado durante esta etapa.

### Evidencia

https://github.com/Molina211/Travesia-Natural-docs/blob/main/ADRs/ADR-001.md

---

# 6. Decisión arquitectónica

Para el proyecto Travesía Natural se propone utilizar un:

> **Monolito modular orientado al dominio, aplicando principios de Domain-Driven Design (DDD), con límites de dominio preparados para una futura evolución hacia microservicios.**

Esta decisión permite comenzar con una estructura controlable para el MVP 1 sin introducir desde el principio una complejidad operacional innecesaria.

DDD se utiliza aquí como enfoque para modelar y separar el dominio, mientras que el estilo arquitectónico inicial corresponde a un **monolito modular**.

---

## 6.1. Justificación

El problema principal de Travesía Natural consiste en organizar y centralizar información que actualmente se encuentra distribuida en diferentes archivos y procesos operativos.

El sistema necesita diferenciar claramente operaciones como:

- reservas;
- ejecución real de los servicios;
- costos operacionales;
- movimientos de caja;
- consultas y reportes.

Por esta razón resulta conveniente separar el sistema por responsabilidades de dominio desde el inicio.

La estructura conceptual inicial puede representarse como:

```text
                 TRAVESÍA NATURAL
                        |
        +---------------+---------------+
        |               |               |
     RESERVAS        EJECUCIÓN       COSTOS
        |               |               |
        +---------------+---------------+
                        |
                       CAJA
                        |
                    CONSULTAS
```

Estos límites permiten evolucionar posteriormente cada área sin tener que rediseñar todo el sistema.

---

# 7. Relación entre DDD y la arquitectura

DDD no se considera aquí como el estilo arquitectónico por sí mismo.

Se utiliza como una estrategia para identificar y organizar el dominio.

### DDD aporta

- lenguaje ubicuo;
- separación por dominios;
- límites claros de responsabilidad;
- agregados y reglas de negocio;
- bounded contexts.

### La arquitectura inicial aporta

- un único sistema desplegable;
- módulos separados internamente;
- comunicación clara entre módulos;
- menor complejidad operacional;
- posibilidad de extraer módulos como servicios independientes posteriormente.

Por tanto:

```text
DDD
  ↓
Modelado y separación del dominio
  ↓
Monolito modular
  ↓
MVP 1
  ↓
Evolución futura
  ↓
Microservicios cuando exista una necesidad real
```

---

# 8. Arquitectura propuesta para MVP 1

La primera versión funcional se puede estructurar conceptualmente de la siguiente manera:

```text
┌─────────────────────────────────────┐
│            Aplicación               │
├─────────────────────────────────────┤
│                                     │
│  Módulo Reservas                    │
│          │                          │
│          ▼                          │
│  Módulo Ejecución                   │
│          │                          │
│          ▼                          │
│  Módulo Costos                      │
│          │                          │
│          ▼                          │
│  Módulo Caja                        │
│          │                          │
│          ▼                          │
│  Consultas / Reportes               │
│                                     │
└─────────────────────────────────────┘
```

La separación es lógica y de dominio aunque inicialmente pueda ejecutarse como una sola aplicación.

---

# 9. Principios arquitectónicos iniciales

## 9.1. Separación entre reserva y ejecución

Una reserva representa la operación comercial prevista.

La ejecución representa lo que realmente ocurrió.

Por tanto:

```text
Reserva != Ejecución
```

Esto evita mezclar lo vendido con lo efectivamente prestado.

---

## 9.2. Separación de costos

Los costos operacionales deben registrarse de forma independiente de la reserva para poder determinar posteriormente el impacto real de la operación.

---

## 9.3. Separación de caja

Los movimientos de caja constituyen una responsabilidad financiera específica y no deben quedar mezclados directamente con la lógica de reserva o ejecución.

---

## 9.4. Evolución progresiva

La arquitectura inicial no obliga a desplegar múltiples servicios.

Los módulos se diseñan desde el comienzo con límites suficientemente claros para permitir una futura extracción si el crecimiento del sistema lo justifica.

---

# 10. Backlog inicial para MVP 1

## Nota histórica

El backlog formal no se encontraba completamente construido durante la primera semana.

Por lo tanto, las siguientes historias representan el **backlog inicial elaborado para completar la actividad académica**, tomando como base el problema identificado y las operaciones centrales del sistema.

No se afirma que estas historias hayan existido como artefactos formales durante la Semana 1 original.

---

## HU-001 — Registrar una reserva

### Como

personal encargado de reservas,

### Quiero

registrar una nueva reserva,

### Para

centralizar la información comercial de una operación.

### Criterios de aceptación

**CA-001**

Dado que el usuario dispone de los datos requeridos, cuando registra una reserva, entonces el sistema debe crear una reserva identificable.

**CA-002**

La reserva debe permitir registrar como mínimo la información del titular y los datos principales del servicio solicitado.

**CA-003**

El sistema no debe crear la reserva cuando falten datos obligatorios.

**CA-004**

La reserva creada debe quedar disponible para consulta posterior.

---

## HU-002 — Modificar o cancelar una reserva

### Como

personal autorizado,

### Quiero

modificar o cancelar una reserva,

### Para

mantener actualizada la información comercial.

### Criterios de aceptación

**CA-001**

Dado que existe una reserva, cuando un usuario autorizado modifica datos permitidos, entonces el sistema debe conservar la reserva con la información actualizada.

**CA-002**

Las modificaciones deben respetar las reglas de negocio definidas para cada operación.

**CA-003**

Dado que existe una reserva válida, cuando se solicita su cancelación, entonces el sistema debe cambiar su estado de acuerdo con la regla de cancelación establecida.

**CA-004**

Una operación no autorizada debe ser rechazada.

---

## HU-003 — Registrar la ejecución del servicio

### Como

personal operativo,

### Quiero

registrar lo que realmente ocurrió durante la prestación del servicio,

### Para

diferenciar la ejecución real de la reserva original.

### Criterios de aceptación

**CA-001**

Dada una reserva existente, cuando comienza la operación, entonces debe poder registrarse su ejecución.

**CA-002**

La ejecución debe quedar asociada a la reserva correspondiente.

**CA-003**

El sistema debe permitir registrar si los servicios previstos fueron efectivamente proporcionados.

**CA-004**

Cuando una actividad no se realiza, el sistema debe permitir registrar la causa correspondiente.

---

## HU-004 — Registrar costos operacionales

### Como

personal encargado de costos,

### Quiero

registrar los costos generados por la operación,

### Para

conocer el costo real del servicio ejecutado.

### Criterios de aceptación

**CA-001**

El sistema debe permitir registrar un costo asociado a una operación.

**CA-002**

Cada costo debe almacenar como mínimo su concepto y valor.

**CA-003**

El sistema debe permitir consultar los costos registrados posteriormente.

**CA-004**

Los costos deben poder distinguirse de los valores comerciales cobrados al cliente.

---

## HU-005 — Registrar movimientos de caja

### Como

personal autorizado de caja,

### Quiero

registrar movimientos de entrada y salida,

### Para

controlar el dinero asociado a las operaciones del negocio.

### Criterios de aceptación

**CA-001**

El sistema debe permitir registrar una entrada de dinero.

**CA-002**

El sistema debe permitir registrar una salida de dinero.

**CA-003**

Cada movimiento debe conservar su valor y la información necesaria para identificar la operación.

**CA-004**

El movimiento registrado debe modificar el estado de caja de acuerdo con las reglas definidas.

---

## HU-006 — Cerrar y reabrir caja

### Como

usuario autorizado de caja,

### Quiero

cerrar o reabrir la caja,

### Para

controlar formalmente el estado financiero de una jornada.

### Criterios de aceptación

**CA-001**

Cuando una caja está abierta, el usuario autorizado debe poder ejecutar el cierre.

**CA-002**

Al cerrar la caja debe conservarse el estado correspondiente al cierre.

**CA-003**

El sistema debe impedir operaciones no permitidas sobre una caja cerrada.

**CA-004**

La reapertura debe estar restringida a usuarios autorizados.

**CA-005**

Los cierres y reaperturas deben quedar registrados.

---

## HU-007 — Consultar la operación diaria

### Como

usuario interno,

### Quiero

consultar la información operativa del día,

### Para

obtener una visión consolidada de la actividad.

### Criterios de aceptación

**CA-001**

La consulta debe mostrar las operaciones correspondientes al período seleccionado.

**CA-002**

La información mostrada debe diferenciar reservas y ejecución.

**CA-003**

El usuario debe poder identificar los costos asociados a las operaciones.

**CA-004**

La información debe poder ser consultada sin modificar los datos operativos.

---

## HU-008 — Consultar balance de caja

### Como

usuario autorizado,

### Quiero

consultar el balance de caja,

### Para

conocer el estado financiero de las operaciones registradas.

### Criterios de aceptación

**CA-001**

El sistema debe mostrar el estado de caja correspondiente al período consultado.

**CA-002**

El balance debe considerar los movimientos registrados.

**CA-003**

El sistema debe permitir identificar los períodos de caja cerrados.

**CA-004**

La consulta no debe modificar los movimientos registrados.

---

# 11. Priorización inicial

| Prioridad | Historia | Motivo |
|---|---|---|
| Alta | HU-001 | Permite iniciar el flujo principal |
| Alta | HU-002 | Mantiene la operación comercial |
| Alta | HU-003 | Permite distinguir venta y ejecución |
| Alta | HU-004 | Permite determinar costos |
| Alta | HU-005 | Permite controlar caja |
| Alta | HU-006 | Completa el control financiero |
| Media | HU-007 | Facilita la operación diaria |
| Media | HU-008 | Facilita la consulta financiera |

---

# 12. Flujo principal del MVP 1

El flujo funcional inicial puede representarse como:

```text
Reserva
   ↓
Ejecución
   ↓
Costos
   ↓
Caja
   ↓
Consultas
```

Este flujo representa la cadena principal que se busca centralizar dentro del sistema.

---

# 13. Resultado de la actividad

La actividad solicita cuatro elementos principales:

| Actividad | Estado |
|---|---|
| Configuración del repositorio de perfil | ✅ Completada |
| Bifurcación/repositorio académico | ✅ Completada |
| `01-week/hu-status/README.md` | ✅ Completado y evidenciado |
| Repositorio `docs` | ✅ Creado |
| ADR-001 | ✅ Creado; decisión arquitectónica complementada en este documento |
| Backlog inicial MVP 1 | ✅ Elaborado |
| Criterios de aceptación comprobables | ✅ Elaborados |
