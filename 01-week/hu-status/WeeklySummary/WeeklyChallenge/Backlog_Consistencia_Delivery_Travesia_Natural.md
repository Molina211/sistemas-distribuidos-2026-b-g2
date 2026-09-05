# Semana 1 — Backlog inicial, consistencia y delivery semantics

**Curso:** Sistemas Distribuidos  
**Proyecto:** Travesía Natural  
**Equipo:** ErrorCapa8  
**Sprint goal:** Course introduction and creation of the initial PDR

> **Nota histórica:** este documento completa ahora la actividad de Semana 1. No afirma que el backlog formal ni estas decisiones técnicas existieran como artefactos en la Semana 1 original. El registro histórico de esa semana no contiene HU formal y el backlog se desarrolló posteriormente. Las decisiones de consistencia y delivery aquí propuestas son el baseline para defender el diseño del MVP 1.

## 1. Problema real

La empresa maneja información del negocio distribuida en varios archivos Excel, lo que dificulta separar lo vendido, lo realmente ejecutado, los costos operacionales y el impacto en caja.

El sistema busca centralizar la operación interna de Travesía Natural, diferenciando la reserva de la ejecución real y permitiendo controlar costos operacionales y caja.

## 2. Objetivo del MVP 1

**Reserva → Ejecución → Costos → Caja → Consulta**

La primera versión debe priorizar la integridad de las operaciones de negocio antes que la optimización de disponibilidad o rendimiento.

## 3. Backlog inicial

| ID | Historia / operación | Prioridad | Objetivo |
|---|---|---|---|
| HU-001 | Registrar una reserva | Alta | Registrar cliente, acompañantes y servicios reservados con valor proyectado. |
| HU-002 | Modificar/cancelar una reserva | Alta | Aplicar reglas de cambio/cancelación y recalcular lo afectado. |
| HU-003 | Registrar ejecución del servicio | Alta | Registrar lo prestado, no prestado y sus causales. |
| HU-004 | Registrar costos operacionales | Alta | Registrar costos reales asociados a la operación. |
| HU-005 | Registrar movimiento de caja | Alta | Registrar ingresos, pagos y gastos del día. |
| HU-006 | Cerrar/reabrir caja | Alta | Mantener cierre diario controlado e histórico. |
| HU-007 | Consultar operación diaria | Media | Consultar reservas, personas programadas y ejecución. |
| HU-008 | Consultar balance de caja | Media | Consultar base, ingresos, pagos, gastos y total. |

## 4. Consistencia y delivery semantics

| Operación | Consistencia requerida | Delivery semantics | Justificación |
|---|---|---|---|
| Registrar reserva | Fuerte | At-least-once + idempotencia | Evita reservas duplicadas ante reintentos. |
| Modificar/cancelar reserva | Fuerte | At-least-once + idempotencia | El cambio debe aplicarse una sola vez sobre el estado correcto. |
| Registrar ejecución | Fuerte | At-least-once + idempotencia | Evita duplicar servicios y estados contradictorios. |
| Registrar costos | Fuerte en escritura; eventual en agregados | At-least-once + idempotencia | Un costo duplicado afecta el resultado financiero. |
| Movimiento de caja | Fuerte | At-least-once + idempotencia | Un duplicado altera el saldo real. |
| Cerrar/reabrir caja | Fuerte | At-least-once + idempotencia + control de concurrencia | Es una operación sensible y debe conservar un único estado válido. |
| Dashboard / operación diaria | Eventual | At-least-once para proyecciones | Puede tolerar pequeño retraso y favorece escalabilidad. |
| Balance administrativo | Fuerte para cierre; eventual informativo | At-least-once para proyecciones | El nivel requerido depende del uso de la lectura. |

## 5. Regla general

1. Operaciones que cambian estado financiero u operacional: **consistencia fuerte**.
2. Proyecciones, dashboards y consultas informativas: **consistencia eventual**.
3. Mensajería de negocio: **at-least-once** como opción base.
4. Operaciones mutantes sensibles: **idempotentes**.
5. La consistencia eventual no debe ocultar errores en operaciones críticas.

## 6. Defensa de las decisiones

Una reserva, una ejecución o un movimiento de caja pueden producir consecuencias operativas o financieras; por eso se prioriza la consistencia fuerte.

Un dashboard es una proyección de información ya registrada y puede tolerar un pequeño retraso. Por ello, la consistencia eventual permite desacoplar las lecturas sin comprometer la transacción de origen.

Se adopta **at-least-once** porque ante fallos de comunicación un reintento puede ser necesario. En lugar de asumir entrega exactamente una vez, el sistema tolera duplicados mediante idempotencia.

## 7. Decisiones pendientes

- Límites exactos de agregados y transacciones.
- Mecanismo concreto de idempotencia.
- Eventos que alimentarán dashboards y reportes.
- Regla definitiva de descuentos.
- Valor exacto de la base de caja.
- Criterios cuantitativos de disponibilidad y rendimiento.
- Topología final y broker, si aplica.

## 8. Evidencia y alcance histórico

El registro de Semana 1 identifica al equipo **ErrorCapa8**, el objetivo de crear el PDR inicial y las contribuciones relacionadas con el análisis del problema, necesidades de negocio, información financiera, ADR-001 y creación del repositorio docs. El mismo registro muestra `HU--001` como `N/A`, por lo que este backlog se presenta como **el entregable elaborado ahora para completar la actividad**, no como evidencia de que el backlog ya existía en Semana 1.
