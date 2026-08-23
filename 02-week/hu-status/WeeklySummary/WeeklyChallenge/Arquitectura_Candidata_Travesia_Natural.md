# Arquitectura Candidata - Travesia Natural

## 1. Objetivo del documento

Este documento presenta una propuesta inicial de arquitectura candidata para el producto **Travesia Natural**, construida a partir del analisis del PDR vigente. Su objetivo es identificar los principales **contextos delimitados (Bounded Contexts)** del negocio, proponer cuales de ellos podrian evolucionar a servicios independientes y justificar cuales conviene mantener agrupados en una primera aproximacion arquitectonica.

Esta propuesta **no representa una decision arquitectonica definitiva**. Su funcion es servir como insumo para la siguiente etapa de analisis y para la elaboracion del **ADR (Architecture Decision Record)** en la Sesion 2.

## 2. Criterio de analisis

La identificacion de contextos delimitados se realizo con base en:

- capacidades del negocio descritas en el PDR
- reglas funcionales y de negocio asociadas a cada proceso
- estados y ciclos de vida diferenciados
- necesidad de autonomia funcional
- nivel de acoplamiento con otros procesos del sistema
- responsabilidad principal sobre la informacion del dominio

El criterio principal fue separar el sistema por **dominios del negocio**, no por tecnologias, pantallas, canales o componentes tecnicos.

## 3. Contextos delimitados identificados

Con base en el PDR, se proponen los siguientes contextos delimitados:

1. Clientes
2. Identidad y acceso
3. Reservas
4. Catalogo operativo
5. Descuentos y reglas comerciales
6. Ejecucion operativa
7. Costos operacionales
8. Caja y consolidacion

## 3.1 Capacidades derivadas o transversales relacionadas

Adicionalmente, la arquitectura candidata reconoce las siguientes capacidades que no se tratan en esta etapa como bounded contexts plenos:

1. Reportes y dashboard
2. Auditoria y trazabilidad

## 4. Propuesta de arquitectura candidata

| Contexto o capacidad | Servicio independiente potencial | Justificacion |
| --- | --- | --- |
| Clientes | Si, potencialmente | Maneja informacion de personas del dominio, incluyendo cliente titular y acompanantes, datos de contacto, informacion sensible requerida por la operacion y relacion comercial con las reservas. Es una responsabilidad distinta a la seguridad y al acceso. |
| Identidad y acceso | Si, potencialmente | Maneja autenticacion, credenciales, acceso de usuarios internos y clientes finales, asi como reglas base de permisos. Tiene una responsabilidad de seguridad distinta a la logica comercial. |
| Reservas | Si, potencialmente | Es el nucleo comercial del negocio. Administra la proyeccion comercial, estados de reserva, asociacion de acompanantes a una reserva, valores proyectados y valor final, asi como la disponibilidad comercial y la gestion de cupos temporales asociados al proceso de reserva segun las reglas parametrizadas por el negocio. |
| Catalogo operativo | No por ahora | Atractivos, hospedaje, alimentacion y transporte soportan la construccion de la reserva y la operacion. Tambien concentra la informacion base comercial y operativa necesaria para habilitar oferta, capacidad y costos catalogados reutilizables. En Fase 1 presenta mas valor como dominio cercano a Reservas que como servicio independiente. |
| Descuentos y reglas comerciales | No por ahora | Participa directamente en la construccion, valoracion y confirmacion de la reserva. Su alta cohesion con Reservas hace recomendable mantenerlo cercano al dominio comercial. |
| Ejecucion operativa | Si, potencialmente | Representa lo efectivamente prestado, con reglas, causales y ciclo de vida distintos a la reserva comercial. Tiene autonomia funcional suficiente para separarse conceptualmente. |
| Costos operacionales | No por ahora | Derivan de la ejecucion real, de la cantidad efectivamente atendida y de los servicios efectivamente prestados. Inicialmente conviene mantenerlos junto al dominio de Ejecucion. |
| Caja y consolidacion | Si, potencialmente | Posee reglas propias de apertura, cierre, movimientos, historico, correcciones excepcionales y consolidacion mensual. Es una capacidad administrativa claramente diferenciada. No es propietario del valor comercial de la reserva, pero si de los movimientos monetarios efectivos que impactan la caja. |
| Reportes y dashboard | No por ahora | Se considera inicialmente una capacidad derivada de lectura y consolidacion. No debe convertirse en propietario de los datos operacionales. |
| Auditoria y trazabilidad | No por ahora, transversal | Se considera inicialmente una capacidad transversal. Cada contexto debe generar las evidencias necesarias de su propio dominio sin mezclar la logica de auditoria con las reglas de negocio. |

## 5. Responsabilidad sobre la informacion

La siguiente tabla resume la responsabilidad principal de cada contexto o capacidad sobre la informacion del dominio:

| Contexto | Informacion bajo su responsabilidad |
| --- | --- |
| Clientes | Informacion maestra de personas del dominio, incluyendo cliente titular y acompanantes, datos de contacto, datos sensibles o de emergencia requeridos por la actividad, consentimientos o aceptaciones funcionales asociados a la persona y evidencias funcionales relacionadas |
| Identidad y acceso | Credenciales, autenticacion, acceso y permisos base |
| Reservas | Informacion comercial de la reserva, estados, valores proyectados, valor final de cobro, descuentos efectivamente aplicados sobre la reserva, condiciones asociadas, modalidad de pago seleccionada, estado de pago, gestion de cupos o disponibilidad comercial temporal segun parametrizacion y vinculacion de acompanantes a la reserva |
| Catalogo operativo | Informacion base de atractivos, hospedaje, alimentacion, transporte, capacidad parametrizada, vigencia comercial habilitada y costos catalogados reutilizables para uso comercial y operativo |
| Descuentos y reglas comerciales | Configuracion de descuentos, vigencia, prioridad, topes y reglas de aplicacion |
| Ejecucion operativa | Informacion real de la prestacion del servicio, servicios prestados, no prestados y causales |
| Costos operacionales | Costos derivados de la ejecucion real y gastos operacionales asociados |
| Caja y consolidacion | Apertura, cierre, ingresos, pagos, gastos, historico, correcciones excepcionales posteriores al cierre y consolidacion mensual |
| Reportes y dashboard | Informacion derivada para consulta operativa y administrativa |
| Auditoria y trazabilidad | Evidencias de operaciones relevantes registradas por cada contexto, con politica minima comun de actor, fecha, accion, registro afectado y motivo cuando aplique |

## 6. Mapa de relaciones entre contextos

El siguiente mapa representa las dependencias funcionales principales identificadas en el PDR:

```text
Identidad y acceso --------------------------> Clientes
Identidad y acceso --------------------------> acceso a capacidades protegidas del sistema

Clientes ------------------------------------> Reservas
Catalogo operativo --------------------------> Reservas
Catalogo operativo --------------------------> Ejecucion operativa
Descuentos y reglas comerciales -------------> Reservas

Reservas -----------------------------------> Ejecucion operativa
Ejecucion operativa -------------------------> Reservas
Ejecucion operativa -------------------------> Costos operacionales

Reservas -----------------------------------> Caja y consolidacion
Caja y consolidacion ------------------------> Reservas

Reservas -----------------------------------> Reportes y dashboard
Ejecucion operativa -------------------------> Reportes y dashboard
Costos operacionales ------------------------> Reportes y dashboard
Caja y consolidacion ------------------------> Reportes y dashboard

Auditoria y trazabilidad -------------------> transversal a todos los contextos
```

Este mapa **no implica todavia APIs, eventos ni contratos tecnicos definitivos**. Solo establece relaciones funcionales y dependencias entre capacidades del negocio.

### 6.1 Tabla base de relaciones entre contextos

| Origen | Destino | Relacion funcional base |
| --- | --- | --- |
| Clientes | Reservas | La reserva identifica y utiliza informacion del cliente titular y de los acompanantes. |
| Identidad y acceso | Clientes | La autenticacion del cliente final y la habilitacion de acceso sobre sus reservas requieren referenciar la identidad de una persona del dominio sin convertir a Identidad en owner de sus datos comerciales u operativos. |
| Identidad y acceso | Capacidades protegidas del sistema | Proporciona autenticacion y control de acceso segun los permisos definidos. |
| Catalogo operativo | Reservas | La reserva utiliza la oferta disponible de atractivos, hospedaje, alimentacion, transporte, capacidad parametrizada y costos catalogados necesarios para calcular y validar la venta. |
| Catalogo operativo | Ejecucion operativa | La ejecucion utiliza informacion base operativa y costos catalogados como referencia para registrar lo efectivamente prestado sin reemplazar los datos reales de la operacion. |
| Descuentos y reglas comerciales | Reservas | La reserva utiliza reglas de descuento para calcular y confirmar valores comerciales. |
| Reservas | Ejecucion operativa | La ejecucion se origina a partir de una reserva valida y conserva trazabilidad con ella. |
| Ejecucion operativa | Reservas | La ejecucion debe conservar referencia a la reserva de origen para trazabilidad, consulta y control de diferencias entre lo reservado y lo efectivamente prestado. |
| Ejecucion operativa | Costos operacionales | Los costos operacionales derivan de lo efectivamente ejecutado y de la cantidad real atendida. |
| Reservas | Caja y consolidacion | La reserva aporta informacion comercial, estado de pago y modalidad seleccionada que pueden dar origen a movimientos monetarios posteriores sin transferir ownership del movimiento de caja. |
| Caja y consolidacion | Reservas | Los movimientos monetarios efectivos asociados a una reserva pueden ser consumidos por Reservas como evidencia para confirmar o actualizar el estado de pago, sin transferir ownership del movimiento de caja. |
| Caja y consolidacion | Reportes y dashboard | La informacion financiera consolidada es consumida para consulta operativa y administrativa. |
| Reservas | Reportes y dashboard | Las reservas alimentan consultas comerciales, operativas y de seguimiento. |
| Ejecucion operativa | Reportes y dashboard | La ejecucion aporta informacion real de prestacion y novedades para consulta. |
| Costos operacionales | Reportes y dashboard | Los costos aportan informacion derivada para control y analisis interno. |
| Todos los contextos | Auditoria y trazabilidad | Cada contexto genera evidencia de las operaciones relevantes de su propio dominio. |

### 6.2 Eventos y decisiones funcionales con ownership primario

La siguiente tabla no define todavia integraciones tecnicas, pero si aclara que contexto debe considerarse responsable primario de los hechos funcionales mas sensibles del PDR:

| Hecho funcional | Contexto owner primario | Observacion |
| --- | --- | --- |
| Creacion de reserva | Reservas | Registra la proyeccion comercial inicial y sus datos asociados. |
| Cambio de estado de reserva | Reservas | Incluye Pendiente de pago, Confirmada, En ejecucion, Finalizada y Cancelada. |
| Gestion de cupo temporal durante Pendiente de pago | Reservas | Debe obedecer a la parametrizacion funcional definida por el negocio para apartar o no cupo. |
| Liberacion de cupo por cancelacion automatica | Reservas | Debe quedar trazabilidad del cambio y de su causa. |
| Aplicacion de descuento vigente o adicional autorizado | Descuentos y reglas comerciales, consumido por Reservas | Reservas usa la regla; el ownership de configuracion permanece fuera de la reserva. |
| Registro de modalidad y condicion de pago de una reserva | Reservas | Forma parte de la logica comercial y del estado de pago de la reserva. |
| Determinacion del valor final de cobro de la reserva | Reservas | Debe reflejar descuentos aplicados, orden de aplicacion y condicion comercial vigente. |
| Registro de un ingreso, pago, gasto o correccion de caja | Caja y consolidacion | Es un hecho monetario efectivo y no debe ser owned por Reservas. |
| Inicio de ejecucion del servicio | Ejecucion operativa | Se origina desde una reserva valida cuando el tour sale a destino. |
| Registro de prestado, no prestado o causal | Ejecucion operativa | Representa la realidad operacional y no la proyeccion comercial. |
| Registro de costo real derivado de la ejecucion | Costos operacionales | Se apoya en referencias catalogadas, pero el costo real nace de la operacion. |
| Consulta consolidada para dashboard o reporte | Reportes y dashboard | Consume informacion derivada sin convertirse en owner transaccional. |
| Registro de evidencia auditada | Contexto de origen, proyectado transversalmente a Auditoria y trazabilidad | La evidencia nace en el contexto que ejecuta la accion. |

## 7. Limites criticos entre contextos

### 7.1 Clientes y Identidad y acceso

Se recomienda separar conceptualmente ambos contextos aunque puedan implementarse juntos inicialmente.

- **Clientes** debe concentrarse en la informacion comercial y operativa asociada a la reserva.
- **Identidad y acceso** debe concentrarse en credenciales, autenticacion y permisos.

Esto evita mezclar logica comercial con responsabilidades de seguridad.

Adicionalmente, dentro de **Clientes** debe distinguirse conceptualmente la informacion general de contacto de la informacion sensible o de emergencia requerida por actividades de riesgo. Aunque ambas pertenezcan al mismo contexto en esta etapa, no deben tratarse con la misma politica de acceso, retencion ni exposicion.

Tambien se recomienda considerar dentro de este contexto las evidencias funcionales asociadas a consentimientos, aceptaciones o autorizaciones requeridas por la actividad, siempre que su objetivo principal sea respaldar la relacion operativa con la persona y no la seguridad tecnica del acceso.

Finalmente, debe reconocerse una relacion funcional entre ambos contextos para el cliente final: **Identidad y acceso** necesita referenciar a la persona autenticada para habilitar autogestion de reservas, pero sin absorber el ownership de sus datos comerciales, sensibles o documentales.

### 7.2 Reservas y Ejecucion operativa

Una **reserva** representa el compromiso comercial, los servicios solicitados y los valores proyectados o finales.  
La **ejecucion operativa** representa lo efectivamente prestado durante la operacion.

La ejecucion debe originarse a partir de una reserva valida, manteniendo independencia sobre los datos operacionales reales.

En la siguiente etapa arquitectonica debera precisarse:

- cuando se crea la ejecucion
- que informacion pasa desde Reservas
- que informacion puede modificarse durante la operacion
- como se mantiene la trazabilidad sin alterar la reserva comercial original

Como criterio ya alineado con el PDR:

- la reserva conserva la proyeccion comercial original
- la ejecucion registra lo efectivamente prestado y sus causales
- una vez iniciada la ejecucion no deben permitirse ajustes ordinarios sobre la reserva como parte del flujo normal

### 7.3 Reservas y Caja y consolidacion

**Reservas** es responsable del compromiso comercial, los valores esperados y la condicion de la reserva.  
**Caja y consolidacion** es responsable de los movimientos monetarios efectivos, ingresos, egresos, apertura, cierre e historico.

La relacion entre ambos contextos debe evitar que los dos sean propietarios del mismo concepto financiero.

En consecuencia:

- Reservas no debe convertirse en el propietario de los movimientos monetarios reales
- Caja no debe convertirse en el propietario del valor comercial de la reserva

Adicionalmente, se recomienda explicitar desde ahora la siguiente separacion conceptual:

- **Reservas** es owner de la modalidad seleccionada, la condicion comercial de pago y el estado de pago de la reserva
- **Reservas** es owner del valor final de cobro de la reserva y de la trazabilidad de descuentos aplicados
- **Caja y consolidacion** es owner del ingreso, pago, gasto o correccion monetaria efectivamente registrada

Esto permite reflejar el requisito del PDR segun el cual una reserva puede estar pendiente, confirmarse o cancelarse por condiciones de pago sin confundir ese estado con la existencia de un movimiento de caja.

### 7.4 Catalogo operativo con Reservas y Ejecucion operativa

**Catalogo operativo** debe proveer informacion base reutilizable para venta y operacion, pero no debe convertirse en owner de hechos transaccionales de reserva o ejecucion.

En particular:

- la capacidad parametrizada, vigencia comercial y configuracion base de la oferta pertenecen al Catalogo operativo
- la capacidad estructural o parametrizada de hospedaje y servicios pertenece al Catalogo operativo
- la disponibilidad comercial temporal y la gestion de cupos durante el flujo de reserva pertenecen a Reservas
- los costos catalogados base pertenecen al Catalogo operativo
- los costos reales derivados de lo ejecutado pertenecen a Costos operacionales
- la informacion efectivamente prestada pertenece a Ejecucion operativa

Esta separacion ayuda a evitar una ambiguedad frecuente: **Catalogo operativo** define lo que puede ofrecerse y bajo que capacidad base, mientras **Reservas** decide si ese recurso se encuentra disponible o bloqueado temporalmente dentro del proceso comercial segun las reglas del negocio.

### 7.5 Reportes y dashboard

Reportes y dashboard deben tener principalmente responsabilidad de **lectura y consolidacion**.

No deben convertirse en propietarios ni modificadores de los datos operacionales de:

- Reservas
- Ejecucion operativa
- Costos operacionales
- Caja y consolidacion

### 7.6 Auditoria y trazabilidad

Auditoria y trazabilidad deben mantenerse como una capacidad transversal.

Cada contexto debe ser responsable de generar la informacion necesaria para registrar las operaciones relevantes de su dominio, evitando que la logica de auditoria invada las reglas propias de negocio.

Sin adelantar todavia una solucion tecnica concreta, la arquitectura candidata si debe reconocer una politica minima comun alineada con el PDR para eventos auditables relevantes:

- actor o usuario que ejecuto la accion
- fecha y hora
- tipo de accion
- identificador del registro afectado
- motivo o justificacion cuando aplique

Como minimo, esta politica debe poder cubrir autenticaciones, cambios de descuentos, movimientos de caja, cancelaciones y modificaciones relevantes de reserva.

## 8. Contextos que conviene mantener juntos inicialmente

### 8.1 Dominio comercial: Reservas + Catalogo operativo + Descuentos y reglas comerciales

Se recomienda mantener estos elementos agrupados inicialmente porque:

- presentan alta cohesion dentro de la capacidad comercial
- participan conjuntamente en el proceso de construccion, valoracion y confirmacion de una reserva
- comparten reglas criticas de negocio en Fase 1
- separarlos demasiado pronto aumentaria integraciones y complejidad innecesaria

### 8.2 Dominio operacional: Ejecucion operativa + Costos operacionales

Se recomienda mantenerlos juntos inicialmente porque:

- los costos operacionales nacen de la ejecucion real
- la ejecucion define cantidades reales, servicios prestados y servicios no prestados
- ambos dominios tienen alta relacion funcional y de trazabilidad

### 8.3 Reportes y dashboard como capacidad derivada

Se recomienda no separarlos inicialmente porque:

- consumen informacion proveniente de otros contextos
- no representan todavia una capacidad de negocio autonoma
- su responsabilidad principal es consolidar y mostrar informacion derivada

## 9. Agrupacion candidata de alto nivel

Como aproximacion inicial, los contextos pueden entenderse agrupados conceptualmente asi:

```text
Gestion de clientes
- Clientes

Seguridad y acceso
- Identidad y acceso

Dominio comercial
- Reservas
- Catalogo operativo
- Descuentos y reglas comerciales

Dominio operacional
- Ejecucion operativa
- Costos operacionales

Caja y consolidacion

Capacidad derivada de lectura
- Reportes y dashboard

Capacidad transversal
- Auditoria y trazabilidad
```

Esta agrupacion **no define todavia una arquitectura final de microservicios**. Solo organiza el dominio para facilitar la toma de decisiones en la siguiente etapa.

## 10. Canales y capacidades

Existen distintos canales de uso del sistema, principalmente:

- canal web interno
- aplicacion movil para cliente final

Estos canales **consumen capacidades del negocio**, pero no constituyen por si mismos bounded contexts del dominio. Por esta razon no se modelan contextos separados llamados `Web`, `Mobile` o `Frontend`.

## 11. Restricciones arquitectonicas conocidas

La arquitectura candidata debe reconocer las restricciones academicas ya definidas en el proyecto:

- Java
- Go
- PostgreSQL
- MongoDB
- Angular
- React
- exactamente 4 Micro Frontends

Estas restricciones son conocidas, pero su asignacion a contextos, componentes o modulos **no debe decidirse todavia en esta etapa**. Esa asignacion debera justificarse posteriormente con base en:

- responsabilidades funcionales
- requisitos no funcionales
- cohesion del dominio
- decisiones registradas en el ADR

En particular, la arquitectura de presentacion debera contemplar la existencia de **4 Micro Frontends**, pero evitando asumir desde ahora una correspondencia uno a uno entre `Micro Frontend` y `Bounded Context`.

## 12. Observaciones arquitectonicas iniciales

- Esta propuesta no obliga a adoptar microservicios desde el inicio.
- La arquitectura candidata podria implementarse inicialmente como un **monolito modular bien delimitado** solo si la restriccion academica de uso de **Java y Go** se satisface dentro de una estrategia hibrida claramente justificada o si una de esas tecnologias queda acotada a un componente complementario fuera del nucleo monolitico principal.
- Si la restriccion exige que ambas tecnologias participen de manera equivalente en el nucleo transaccional de Fase 1, entonces la opcion de monolito modular puro deja de ser plenamente directa y debe reevaluarse formalmente en el ADR.
- En la Sesion 2 debera evaluarse formalmente si conviene:
  - monolito modular
  - microservicios
  - arquitectura hibrida
- La decision final debera registrarse mediante un **ADR**.

## 13. Decisiones pendientes para el ADR

La arquitectura candidata establece los limites principales del dominio, pero deja para la siguiente etapa las decisiones relacionadas con:

- estilo arquitectonico definitivo
- limites fisicos entre modulos o servicios
- mecanismos de comunicacion
- limites transaccionales
- estrategia de persistencia
- asignacion de PostgreSQL y MongoDB
- asignacion de Java y Go
- distribucion de los 4 Micro Frontends entre Angular y React
- autenticacion y autorizacion tecnica
- politicas de acceso a datos sensibles y su retencion operativa
- observabilidad
- despliegue
- cumplimiento de los requisitos no funcionales

Estas decisiones deberan justificarse y registrarse formalmente mediante uno o varios ADR.

## 14. Conclusion

Con base en el PDR de Travesia Natural, es posible identificar claramente los principales contextos delimitados del sistema y proponer una arquitectura candidata razonable. La mejor aproximacion inicial no consiste en fragmentar todo desde el principio, sino en reconocer que existen dominios con alta autonomia potencial y otros que, por su acoplamiento funcional y alta cohesion, conviene mantener juntos en una primera fase.

Por tanto, esta propuesta sugiere:

- reconocer desde ahora los limites del dominio
- separar conceptualmente las responsabilidades de clientes y seguridad
- mantener agrupados los contextos con alta cohesion comercial u operacional
- tratar auditoria como capacidad transversal y reportes como capacidad de lectura
- posponer la asignacion tecnica de servicios, bases de datos, micro frontends y tecnologias hasta el ADR de la Sesion 2
