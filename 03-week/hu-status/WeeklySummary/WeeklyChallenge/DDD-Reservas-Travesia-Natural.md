# Sesion 1 - DDD y Arquitectura Hexagonal

## Bounded Context escogido

**Reservas**

## Justificacion de la eleccion

Se escoge el Bounded Context **Reservas** porque es el nucleo comercial de **Travesia Natural** y concentra las reglas mas importantes del proceso de venta. En este contexto se registra la proyeccion comercial inicial de la reserva, se asocian el cliente titular y los acompanantes, se seleccionan los servicios, se calculan los valores de cobro, se controla el estado de pago y se aplican restricciones funcionales como capacidad, condiciones de modificacion, cancelacion, saldo a favor y reagendamiento.

Ademas, es el contexto mejor respaldado por los artefactos ya construidos del proyecto:

- `PDR_Travesia_Natural.md`, donde se definen el proceso de gestion de reservas, los requerimientos funcionales y las reglas de negocio asociadas.
- `Arquitectura_Candidata_Travesia_Natural.md`, donde Reservas aparece como el nucleo comercial del dominio.
- `Context-Map-Travesia-Natural.md`, donde se reconoce a Reservas como owner de la informacion comercial de la reserva.

## Raiz agregada

- **Reserva**

La entidad **Reserva** actua como raiz agregada porque es el punto de entrada para proteger la consistencia del contexto. Desde ella se controlan los acompanantes asociados, los servicios reservados, los valores calculados, el estado de la reserva, el estado de pago, el saldo a favor pendiente y las reglas que determinan si la reserva puede confirmarse, modificarse, cancelarse o reagendarse.

## Entidades

- **Reserva**
  - Representa la proyeccion comercial de los servicios solicitados por un cliente.
  - Contiene la informacion principal del proceso: tenant, titular, acompanantes, servicios, valores, estado, estado de pago y condiciones asociadas.

- **Acompanante**
  - Representa a cada persona adicional vinculada a la reserva.
  - Tiene identidad propia dentro del agregado por sus datos individualizados y su documento.

- **ServicioReservado**
  - Representa cada servicio incluido en la reserva, como atractivo, transporte, hospedaje o alimentacion.
  - Permite mantener el detalle de lo solicitado comercialmente como base para calcular valores y validar reglas.

- **IntentoPago**
  - Representa cada intento economico asociado a la reserva, como soporte de transferencia, abono o pago recibido.
  - Permite conservar trazabilidad de validaciones, rechazos y acumulacion de pagos.

- **ReservaVinculada**
  - Representa la relacion funcional entre una reserva original y una nueva reserva generada por reagendamiento.
  - Se usa cuando el cambio requiere control independiente de fecha, servicio principal o condicion comercial.

## Value Objects

- **TenantId**
  - Representa el tenant propietario de la reserva.
  - Garantiza que la reserva y sus operaciones no se mezclen con otro tenant.

- **Dinero**
  - Representa valores monetarios como valor proyectado, valor final, saldo pendiente, devolucion potencial y saldo a favor.

- **DocumentoIdentidad**
  - Representa el documento de una persona dentro de la reserva.
  - Permite validar formato y evitar duplicidad dentro del agregado.

- **EstadoReserva**
  - Representa el estado funcional de la reserva, por ejemplo: pendiente de pago, confirmada o cancelada.

- **EstadoPago**
  - Representa el ciclo economico de la reserva.
  - Sus estados base son: Sin pago, En validacion, Parcial, Pagado, Rechazado, Saldo a favor pendiente y Devuelto parcial o total.

- **CantidadPersonas**
  - Representa el total de personas vinculadas a la reserva.
  - Se usa para calculos y validaciones de capacidad.

- **ModalidadPago**
  - Representa la condicion o modalidad de pago seleccionada para la reserva.

- **CondicionesReserva**
  - Representa las condiciones parametrizadas que definen si una reserva puede modificarse o cancelarse segun la actividad o servicio.

- **RangoFecha**
  - Representa la fecha principal de la reserva o la nueva fecha acordada en caso de reagendamiento.

- **PoliticaCupo**
  - Representa la regla activa de cupo aplicada al recurso controlado: sin apartamiento previo, apartamiento temporal o confirmacion directa solo con pago valido.

## Invariantes que debe proteger

- Toda reserva debe pertenecer a un unico tenant y no puede mezclar informacion de otro tenant.
- No crear una reserva sin los datos obligatorios del cliente titular.
- No registrar acompanantes con documento duplicado dentro de la misma reserva.
- No superar la capacidad del hospedaje seleccionado cuando la reserva incluya hospedaje.
- Toda reserva debe conservar **valor proyectado** y **valor final** como parte de su estado consistente.
- El valor proyectado debe calcularse con base en los servicios seleccionados y la cantidad total de personas.
- El valor final no puede existir sin trazabilidad respecto al valor proyectado y a las condiciones comerciales aplicadas.
- Una reserva solo puede modificarse o cancelarse si las condiciones parametrizadas del servicio o actividad lo permiten.
- Una reserva solo puede pasar a **Confirmada** cuando cumple la condicion de pago exigida por la modalidad configurada.
- Un pago rechazado no puede sobrescribir el historico del intento anterior; solo puede generar un nuevo intento.
- Una devolucion ejecutada solo puede existir si antes hubo determinacion del valor y autorizacion correspondiente.
- El saldo a favor pendiente no debe registrarse como salida efectiva de caja.
- Una reserva en **En ejecucion** no puede aceptar ajustes ordinarios.
- Si un reagendamiento genera una nueva reserva, debe existir relacion trazable entre reserva origen y reserva vinculada.

## Eventos de dominio que genera

- **ReservaCreada**
  - Se genera cuando la reserva queda registrada como proyeccion comercial inicial.

- **AcompananteAgregado**
  - Se genera cuando se vincula un acompanante valido a la reserva.

- **ServicioReservadoAgregado**
  - Se genera cuando se agrega un servicio a la reserva.

- **ValorReservaCalculado**
  - Se genera cuando el sistema calcula o recalcula el valor proyectado y el valor final de la reserva.

- **PagoRegistrado**
  - Se genera cuando se registra un soporte, un abono o un pago asociado a la reserva.

- **PagoValidado**
  - Se genera cuando un intento de pago es aceptado y actualiza el estado economico de la reserva.

- **PagoRechazado**
  - Se genera cuando un soporte o intento de pago no es aceptado.

- **ReservaConfirmada**
  - Se genera cuando la reserva cumple las condiciones necesarias para quedar confirmada.

- **ReservaCancelada**
  - Se genera cuando la reserva es cancelada conforme a las condiciones parametrizadas.

- **SaldoAFavorGenerado**
  - Se genera cuando una modificacion o cancelacion produce una compensacion futura sin salida efectiva de dinero.

- **DevolucionAutorizada**
  - Se genera cuando se aprueba una devolucion monetaria vinculada a la reserva.

- **DevolucionEjecutada**
  - Se genera cuando la salida efectiva de dinero queda registrada y asociada a la reserva.

- **ReservaReagendada**
  - Se genera cuando la reserva cambia de fecha o se vincula con una nueva reserva por reagendamiento.

## Nota de alineacion con el dominio

Este modelado se mantiene dentro del Bounded Context **Reservas** y no mezcla responsabilidades de otros contextos:

- **Clientes** es owner de la informacion maestra de personas.
- **Catalogo operativo** provee informacion base reutilizable de oferta y capacidad.
- **Descuentos y reglas comerciales** define reglas de descuento, aunque Reservas consume su resultado para calcular el valor final.
- **Caja y consolidacion** es owner de los movimientos monetarios reales y de su consolidacion, aunque Reservas mantiene el estado economico funcional de la reserva.
- **Ejecucion operativa** representa lo efectivamente prestado y no debe confundirse con la proyeccion comercial de la reserva.

Por tanto, el agregado **Reserva** se limita a proteger la consistencia de la venta, del estado economico funcional y de las reglas comerciales dentro del dominio de Reservas, sin asumir responsabilidades de caja administrativa, ejecucion real del servicio ni gestion transversal de tenants.
