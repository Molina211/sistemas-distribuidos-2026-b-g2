# Sesion 2 - Servicios, datos y contratos

## Bounded Context base

**Reservas**

Se toma como punto de partida el Bounded Context **Reservas** definido en la Sesion 1 porque es el contexto principal del flujo comercial de Fase 1 y concentra la primera funcionalidad vertical mas implementable del producto: registrar una reserva y dejarla en estado **Pendiente de pago** con trazabilidad suficiente.

## 1. Propiedad de datos

### Contexto: Reservas

**Owner de datos**

- Reserva
- Acompanante
- ServicioReservado
- IntentoPago
- EstadoReserva
- EstadoPago
- ValorProyectado
- ValorFinal
- SaldoPendiente
- SaldoAFavorPendiente
- ReservaVinculada por reagendamiento
- ModalidadPago elegida para la reserva
- Condiciones comerciales aplicadas a la reserva ya resueltas para ese caso

**Justificacion**

Reservas es owner de la informacion comercial y del estado economico funcional de la reserva. Aqui viven la intencion de compra, sus cambios, sus restricciones y la trazabilidad del ciclo de pago a nivel de negocio.

### Contexto relacionado: Catalogo Operativo

**Owner de datos**

- Atractivos
- Hospedajes
- Alimentacion
- Transportes
- Tarifas base
- Costos base
- Capacidad parametrizada
- Politica de cupo activa por recurso

**Justificacion**

Reservas consume esta informacion para cotizar, validar capacidad y construir la reserva, pero no es owner de esos catalogos.

### Contexto relacionado: Descuentos y reglas comerciales

**Owner de datos**

- Descuentos vigentes
- Vigencia de descuentos
- Prioridad de aplicacion
- Acumulacion
- Topes
- Base de calculo
- Motivos habilitados para descuento adicional
- Condiciones comerciales aplicables a promociones

**Justificacion**

Reservas necesita el resultado comercial aplicable al caso de uso, pero no debe ser owner de las reglas maestras de descuento ni mezclar esta responsabilidad con Catalogo Operativo.

### Contexto relacionado: Clientes

**Owner de datos**

- Identidad maestra del cliente
- Datos personales base
- Datos de contacto base
- Consentimientos y datos sensibles persistidos como expediente de cliente, cuando el diseño final los separe

**Justificacion**

Reservas necesita snapshot o datos operativos para vender, pero no deberia asumir la maestria completa de personas a nivel dominio.

### Contexto relacionado: Caja y Consolidacion

**Owner de datos**

- Movimiento de caja
- Apertura y cierre diario
- Base diaria
- Pagos operacionales
- Gastos
- Devoluciones ejecutadas
- Consolidacion mensual

**Justificacion**

Reservas conoce el estado economico funcional de la reserva, pero la salida real de dinero, la caja diaria y la consolidacion pertenecen a Caja.

### Contexto relacionado: Ejecucion Operativa

**Owner de datos**

- Servicios efectivamente prestados
- Servicios no prestados
- Causales operativas
- Novedades de no asistencia
- Estado operativo final de ejecucion

**Justificacion**

Reservas define lo vendido; Ejecucion define lo realmente prestado.

### Contexto relacionado: Tenant Management

**Owner de datos**

- Tenant
- Estado del tenant
- Primer Administrador del tenant
- Alta, activacion, inactivacion y reactivacion del tenant

**Justificacion**

Reservas opera dentro de un tenant ya determinado, pero no administra su ciclo de vida transversal.

### Contexto relacionado: Identidad y acceso

**Owner de datos**

- Usuarios internos
- Membresia por tenant
- Permisos base del tenant

**Justificacion**

Reservas necesita resolver quien ejecuta la operacion y con que permisos, pero no debe ser owner de autenticacion, membresias ni permisos.

## 2. Contratos entre contextos o servicios

### Contrato 1 - Reservas consulta catalogo operativo

**Objetivo**

Obtener la oferta necesaria para construir y valorar una reserva.

**Entrada**

- `tenantId`
- `servicioId` o criterios de consulta
- `fechaObjetivo`

**Salida esperada**

- datos del servicio
- precio base
- capacidad disponible o politica de cupo aplicable
- restricciones operativas relevantes

**Uso**

Se usa antes de crear o modificar una reserva.

### Contrato 1A - Reservas consulta descuentos y reglas comerciales

**Objetivo**

Obtener los descuentos y reglas comerciales aplicables a una reserva o cotizacion.

**Entrada**

- `tenantId`
- `servicioId`
- `fechaObjetivo`
- `canal`
- `perfilCliente` cuando aplique

**Salida esperada**

- descuentos vigentes aplicables
- vigencia
- prioridad
- acumulacion permitida
- topes
- base de calculo
- motivos habilitados para descuento adicional

**Uso**

Se usa al calcular o recalcular el valor final de la reserva.

### Contrato 2 - Reservas consulta/valida cliente

**Objetivo**

Resolver si el titular ya existe y obtener o registrar sus datos operativos obligatorios.

**Entrada**

- `tenantId`
- `documentoIdentidad`
- datos del titular

**Salida esperada**

- `clienteId`
- datos base del cliente
- validacion de existencia

**Uso**

Se usa al crear la reserva.

### Contrato 3 - Reservas publica evento de estado economico

**Objetivo**

Notificar a Caja que ocurrio un hecho de negocio con impacto economico real.

**Entrada**

- `tenantId`
- `reservaId`
- `tipoEvento`
- `monto`
- `fechaHora`
- `actor`
- `motivo`

**Uso**

Se usa cuando un hecho de negocio en Reservas tiene impacto real en caja.

**Semantica**

Este contrato debe entenderse como publicacion de evento asincrono. Reservas publica el hecho y Caja reacciona posteriormente sin bloquear la operacion principal del contexto Reservas.

**Eventos candidatos**

- `PagoValidado`
- `DevolucionEjecutada`
- `SaldoAFavorGenerado` si en una fase posterior Caja necesitara enterarse

### Contrato 4 - Reservas consulta caja para trazabilidad economica visible

**Objetivo**

Mostrar al Administrador o al Colaborador el estado relacionado de movimientos ya ejecutados.

**Entrada**

- `tenantId`
- `reservaId`

**Salida esperada**

- lista de movimientos de caja relacionados

**Uso**

Este contrato si es de lectura sincrona porque la consulta administrativa necesita respuesta inmediata al momento de visualizar la reserva.

### Contrato 5 - Reservas publica reserva confirmada o cancelada

**Objetivo**

Permitir que otros contextos reaccionen a un cambio relevante del ciclo comercial.

**Entrada**

- `tenantId`
- `reservaId`
- `estadoReserva`
- `estadoPago`
- `fechaHora`

**Posibles consumidores**

- Ejecucion Operativa
- Reporting
- Notificaciones futuras

## 3. Tipo de comunicacion

### Comunicacion sincrona

Se recomienda sincrona para:

- Reservas -> Catalogo Operativo
- Reservas -> Descuentos y reglas comerciales
- Reservas -> Clientes
- Reservas -> Identidad y acceso para resolver contexto y permisos
- Reservas -> Tenant Management para validar tenant activo cuando aplique

**Razon**

El caso de uso de crear o modificar reserva necesita respuesta inmediata para validar capacidad, datos obligatorios y restricciones antes de persistir.

### Comunicacion asincrona

Se recomienda asincrona para:

- Reservas -> Caja cuando se genera hecho economico que impacta caja real
- Reservas -> Ejecucion cuando una reserva confirmada queda lista para seguimiento futuro
- Reservas -> Reporting o auditoria avanzada

**Razon**

Son reacciones posteriores a un hecho confirmado del dominio y no deben bloquear el flujo principal de registro de reserva.

### Decision practica para MVP 1

Para el primer corte implementable, el flujo vertical puede mantenerse mayoritariamente sincrono dentro de la funcionalidad principal de **crear reserva interna** y dejar la publicacion de eventos como salida preparada pero simple.

## 4. ACL si aplica

### ACL con sistemas externos

**No aplica de forma implementada en MVP 1**

Segun el PDR, las integraciones con pasarelas de pago, bancos o sistemas contables externos permanecen fuera del alcance de Fase 1.

### ACL futuras previstas

Si en una fase posterior se integran sistemas externos, la comunicacion no deberia entrar directamente al dominio **Reservas** o **Caja**, sino pasar primero por una **ACL - Capa Anticorrupcion**.

### Flujo esperado de una ACL futura

```text
Sistema externo
      ↓
ACL - Capa Anticorrupcion
      ↓
Nuestro dominio
```

### Objetivo de la ACL

- Traducir modelos externos al lenguaje de negocio interno.
- Evitar que nombres, estados o estructuras del sistema externo contaminen el dominio.
- Proteger a `Reservas` y `Caja` de acoplamientos directos con proveedores externos.

### Sistemas externos previstos

- pasarela de pagos
- banco
- sistema contable

### Que deberia traducir la ACL

- estados de pago externos
- identificadores de transaccion
- confirmaciones o rechazos
- movimientos contables
- errores tecnicos del proveedor

### Traduccion esperada al lenguaje del dominio

Ejemplos de traduccion futura:

- `payment_reference` -> `identificadorTransaccion`
- `approved` -> `PagoValidado`
- `rejected` -> `PagoRechazado`
- `refund_processed` -> `DevolucionEjecutada`
- `customer_id` -> `clienteId`

### Aplicacion al contexto Reservas

La ACL no decide reglas de negocio de la reserva. Su responsabilidad es traducir mensajes externos a comandos, respuestas o eventos comprensibles para el dominio, por ejemplo:

- registrar un intento de pago
- marcar un pago como validado
- marcar un pago como rechazado
- informar una devolucion ejecutada en caja

### Decision para MVP 1

En MVP 1 se deja **identificada** la necesidad de ACL, pero **no se implementa** porque no hay integraciones externas activas dentro del alcance definido por el PDR.

## 5. Primera funcionalidad vertical

### Vertical Slice MVP 1

**Crear reserva interna y dejarla en estado Pendiente de pago**

### Alcance de la vertical

Incluye:

- identificar tenant activo
- registrar o resolver cliente titular
- registrar acompanantes
- seleccionar servicios
- calcular valor proyectado y valor final
- validar capacidad de hospedaje
- definir modalidad de pago
- persistir la reserva
- dejarla en estado `Pendiente de pago`
- dejar el estado de pago en `Sin pago` o `En validacion` segun corresponda

### No incluye en este primer slice

- autogestion movil completa
- devolucion monetaria
- reagendamiento
- integracion externa de pagos
- consolidacion administrativa mensual

### Justificacion

Es la vertical mas adecuada para MVP 1 porque toca el nucleo del negocio, es demostrable, conecta con el contexto escogido en Sesion 1 y deja una base real para extender despues confirmacion, caja, ejecucion y reportes.

## 6. Historias de usuario MVP 1

### HU-MVP1-001 - Crear reserva interna

**Como** Colaborador operativo  
**quiero** registrar una reserva interna con cliente, acompanantes y servicios  
**para** dejar registrada la intencion comercial del cliente dentro del tenant correcto.

**Criterios de aceptacion**

- Dado un tenant activo, cuando el colaborador registra una reserva con datos obligatorios validos, entonces la reserva queda creada en ese tenant.
- Dado que el cliente titular no tiene datos obligatorios completos, cuando se intenta crear la reserva, entonces el sistema rechaza la operacion.
- Dado que dos acompanantes tienen el mismo documento dentro de la misma reserva, cuando se intenta guardar, entonces el sistema rechaza la operacion.
- Dado que la reserva se crea correctamente, cuando se consulta despues del guardado, entonces debe mostrarse con su `valor proyectado`, `valor final`, `estadoReserva` y `estadoPago`.

### HU-MVP1-002 - Validar capacidad de hospedaje

**Como** Colaborador operativo  
**quiero** que el sistema valide la capacidad del hospedaje  
**para** no registrar una reserva comercialmente inconsistente.

**Criterios de aceptacion**

- Dado que la reserva incluye hospedaje, cuando la capacidad disponible es suficiente, entonces la reserva puede continuar.
- Dado que la reserva incluye hospedaje, cuando la capacidad disponible no es suficiente, entonces el sistema informa capacidad insuficiente y no permite confirmar esa configuracion.

### HU-MVP1-003 - Calcular valor comercial de la reserva

**Como** Colaborador operativo  
**quiero** que el sistema calcule el valor proyectado y el valor final  
**para** registrar correctamente la base economica de la reserva.

**Criterios de aceptacion**

- Dado un conjunto valido de servicios y cantidad de personas, cuando se calcula la reserva, entonces el sistema debe obtener el `valor proyectado`.
- Dado que existen descuentos vigentes aplicables, cuando se recalcula la reserva, entonces el sistema debe reflejar el `valor final` con trazabilidad del descuento aplicado.
- Dado que la configuracion cambia antes de guardar, cuando se recalcula, entonces el sistema debe actualizar los valores resultantes.

### HU-MVP1-004 - Dejar reserva pendiente de pago

**Como** Colaborador operativo  
**quiero** dejar la reserva en estado `Pendiente de pago` con modalidad definida  
**para** iniciar el seguimiento comercial posterior.

**Criterios de aceptacion**

- Dado que la reserva fue creada y aun no cumple la condicion de pago, cuando se registra la modalidad de pago, entonces la reserva queda en estado `Pendiente de pago`.
- Dado que no existe soporte ni dinero validado, cuando la reserva se crea, entonces el `estadoPago` inicial debe ser `Sin pago`.
- Dado que la modalidad requiere soporte cargado inmediatamente y este fue adjuntado, cuando la reserva se crea, entonces el `estadoPago` puede quedar en `En validacion`.

## 7. Flujo resumido

```text
Bounded Context: Reservas
      ↓
Propiedad de datos:
Reserva, Acompanante, ServicioReservado, IntentoPago, EstadoReserva, EstadoPago
      ↓
Contratos:
Catalogo Operativo, Descuentos y reglas comerciales, Clientes, Caja, Tenant Management, Identidad y acceso
      ↓
ACL:
No aplica en MVP 1
      ↓
Vertical Slice:
Crear reserva interna y dejarla en Pendiente de pago
      ↓
Historias MVP 1:
HU-MVP1-001
HU-MVP1-002
HU-MVP1-003
HU-MVP1-004
```

## 8. Nota de cierre

Esta actividad se mantiene enfocada en **Reservas** como bounded context principal de MVP 1. No intenta cerrar todavia la arquitectura completa del sistema ni asignar cada contexto a un microservicio. Su objetivo es dejar clara la propiedad de datos, las fronteras funcionales y la primera porcion vertical implementable del producto.
