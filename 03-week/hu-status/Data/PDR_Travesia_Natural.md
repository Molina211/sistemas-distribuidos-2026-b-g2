# PDR - Travesia Natural

## 1. Identificacion del documento

- Producto: Plataforma multitenencia para operadores turisticos de naturaleza y aventura, con Travesia Natural como tenant principal de validacion y demostracion
- Tipo de documento: Product Definition Requirements (PDR)
- Fecha: 2026-08-09
- Version: 1.2

## 2. Autores

- MARIA FERNANDA ROBAYO LAGUNA
- JHON SEBASTIAN MOLINA FIERRO

## 3. Proposito del documento

Este documento define, con enfoque de ingenieria de software, las necesidades de una plataforma multitenencia para operadores turisticos de naturaleza y aventura, usando a **Travesia Natural** como tenant principal de validacion funcional y demostracion. Su objetivo es servir como base para analisis, UX/UI, arquitectura, desarrollo, pruebas, validacion y planificacion, sin adelantar decisiones tecnicas que todavia no han sido definidas.

## 3.1 Control de cambios

- Version 1.2 - 2026-08-23: actualizacion del enfoque del producto hacia multitenencia, manteniendo a Travesia Natural como tenant principal de validacion y demostracion. Se incorporan criterios de aislamiento por tenant en alcance, seguridad, trazabilidad, datos, procesos, pagos, cupos, caja, reportes y riesgos. Sobre esta misma version se consolidan tambien reglas explicitas para reintentos de pago, saldo a favor, reagendamiento, catalogo de parametrizaciones permitidas en Fase 1, alineacion del proceso de caja con devoluciones y cierre del baseline no funcional de Fase 1.
- Version 1.1 - 2026-08-19: refinamiento funcional y no funcional del PDR, incluyendo mayor precision en permisos base, descuentos, pagos pendientes, estados de reserva, criterios de aceptacion, RNF base de Fase 1 y delimitacion de alcance.

## 4. Contexto y antecedentes

Travesia Natural es una operacion de turismo de naturaleza y aventura que sirve como caso base de validacion funcional para este proyecto. Actualmente, parte importante de su gestion se apoya en diferentes archivos de Excel utilizados para registrar y controlar informacion relacionada con las reservas, los pagos operacionales, los gastos operacionales y la consolidacion contable interna.

Las fuentes historicas revisadas muestran que el problema principal no es la falta de registro, sino la dispersion de la informacion y la dificultad para diferenciar:

- lo proyectado al vender una reserva
- lo realmente prestado durante la ejecucion
- los costos operacionales internos
- los gastos generales y el flujo de caja

A partir de la decision de producto tomada por el equipo el 2026-08-23, la solucion deja de entenderse como un sistema exclusivo para una sola empresa y pasa a definirse como una **plataforma multitenencia**. En este nuevo enfoque, cada empresa operadora funciona como un **tenant** aislado dentro de la misma solucion, con sus propios usuarios, clientes, reservas, catalogos, costos, caja y reportes. Travesia Natural se mantiene como el tenant principal de demostracion y referencia funcional.

## 5. Planteamiento del problema

La operacion actual dificulta conocer con claridad:

- cuanto se vendio en cada reserva
- que servicios realmente se prestaron
- cuales fueron los costos reales de operacion
- cual fue el impacto de esa operacion en la caja del negocio

Esto afecta la trazabilidad operativa, la visibilidad de rentabilidad por reserva y el control administrativo de la operacion.

## 6. Justificacion

El proyecto se justifica por la necesidad de centralizar informacion que hoy se consulta y consolida manualmente desde diferentes archivos de Excel. Una solucion integrada permitiria apoyar de mejor manera el registro de reservas, el seguimiento de los servicios efectivamente prestados, el control de costos operacionales y la consolidacion de caja.

Adicionalmente, el enfoque multitenencia permite que la solucion no quede limitada a una unica operacion turistica. La plataforma podra ser utilizada por varias empresas del mismo sector, preservando aislamiento de informacion, configuraciones y operacion, mientras Travesia Natural se conserva como el tenant principal para validar el alcance funcional y demostrar el comportamiento del sistema.

## 7. Objetivo general

Construir una plataforma multitenencia para operadores turisticos de naturaleza y aventura que permita centralizar la gestion de reservas, la ejecucion real de servicios, el control de costos operacionales y la consolidacion de caja, incluyendo canales internos y atencion directa al cliente final, usando a Travesia Natural como tenant principal de validacion y demostracion.

## 8. Objetivos especificos

- Centralizar la informacion de clientes, atractivos, alimentacion, hospedaje, transporte y otros datos operativos necesarios.
- Registrar reservas como proyeccion comercial de los servicios solicitados.
- Permitir que el cliente final consulte la oferta disponible, cree su reserva y gestione en Fase 1 un alcance acotado de postreserva desde una aplicacion movil.
- Diferenciar lo reservado de lo realmente prestado.
- Registrar costos operacionales asociados a la ejecucion.
- Consolidar ingresos, pagos y gastos para control interno de caja.
- Garantizar aislamiento funcional y de datos entre tenants, permitiendo que cada empresa opere con sus propios usuarios, configuraciones y registros sin mezclar informacion con otros tenants.

## 9. Stakeholders y actores identificados

### Stakeholders identificados

- Travesia Natural como cliente base, tenant principal de validacion funcional y referencia de demostracion.
- Operadores turisticos futuros o potenciales con necesidades equivalentes, como beneficiarios del enfoque multitenencia de la plataforma.
- Equipo academico del proyecto como parte interesada en las restricciones obligatorias del proyecto.

### Actores operativos identificados en las fuentes del proyecto

- Administrador de plataforma.
- Administrador.
- Colaborador operativo.
- Cliente final usuario de aplicacion movil para consulta y autogestion de reservas.

Estas denominaciones ya se encuentran alineadas con los roles formales confirmados para esta version del documento.

Observacion de multitenencia:
Los roles descritos en esta seccion se entienden dentro del contexto de un tenant especifico. Un Cliente final, un Administrador o un Colaborador operativo solo pueden operar sobre la informacion del tenant al que pertenecen o del que son responsables.

### Roles del sistema confirmados

- Administrador de plataforma: usuario interno responsable del alta, activacion, inactivacion, reactivacion y trazabilidad administrativa de tenants. No participa en la operacion comercial diaria de un tenant salvo en acciones excepcionales de soporte auditado.
- Cliente final: usuario que consulta tours, hospedaje y descuentos, y gestiona su propia reserva desde la aplicacion movil o canal web habilitado para clientes.
- Administrador: usuario con control general del sistema, incluyendo reservas, descuentos, caja y demas configuraciones habilitadas por el negocio.
- Colaborador operativo: usuario con multiples funciones operativas, incluyendo reservas, seguimiento diario, caja y registro de gastos operacionales, sin permisos para configurar o autorizar descuentos.

### Permisos base de Fase 1

- Administrador de plataforma: puede crear tenants, activar o inactivar tenants, reactivar tenants, asignar el primer Administrador de cada tenant, consultar auditoria transversal de plataforma y ejecutar soporte administrativo excepcional con trazabilidad obligatoria. No puede registrar reservas, pagos, gastos ni movimientos de caja de un tenant salvo accion excepcional auditada y expresamente autorizada por el responsable del tenant.
- Cliente final: puede consultar la oferta disponible, autenticarse, crear su propia reserva, consultar el estado de sus reservas y avanzar al flujo de pago segun las modalidades habilitadas.
- Administrador: puede gestionar reservas, parametros de descuentos, base diaria de caja, configuraciones operativas habilitadas, consultas administrativas y operaciones sensibles que requieran autorizacion.
- Colaborador operativo: puede registrar reservas, consultar reservas del dia y proximas, registrar seguimiento de reservas pendientes de pago, registrar ejecucion, registrar gastos operacionales reales y operar caja dentro de los limites definidos por el negocio.
- Restriccion base: solo el Administrador puede autorizar descuentos adicionales, validar o rechazar soportes de transferencia cuando el flujo requiera validacion administrativa y autorizar devoluciones monetarias.
- Restriccion base: el Colaborador operativo puede validar o rechazar soportes de transferencia unicamente cuando el negocio lo habilite expresamente para ese tenant, quedando siempre registrada la accion y sin facultad para autorizar descuentos adicionales ni devoluciones monetarias.
- Restriccion base: el acceso a datos medicos, de emergencia, consentimientos o documentos sensibles asociados a actividades de riesgo queda limitado al Administrador y al Colaborador operativo cuando la actividad del dia o la gestion previa de la reserva lo requieran. Todo acceso a esta informacion debe quedar auditado.
- Restriccion base: el Colaborador operativo no puede configurar descuentos, autorizar descuentos adicionales ni modificar la base parametrizada de caja.
- Restriccion base: el Cliente final no puede consultar ni modificar reservas de otros clientes ni ejecutar operaciones administrativas de caja, descuentos o configuracion.
- Restriccion base de multitenencia: ningun usuario puede consultar, crear, modificar, ejecutar ni consolidar informacion perteneciente a un tenant distinto del suyo.

### Regla operativa minima de tenant para Fase 1

- El alta de un tenant en Fase 1 se realiza por proceso administrativo interno de la plataforma y no mediante autoservicio publico.
- En Fase 1, dicho aprovisionamiento inicial se entiende como un proceso administrativo manual de plataforma, ejecutado fuera de las funciones operativas del tenant y antes de que exista el primer Administrador de ese tenant.
- Cada tenant debe crearse con un identificador unico, un nombre comercial visible y un estado operativo.
- Los estados operativos minimos del tenant en Fase 1 son: Activo e Inactivo.
- Al crear un tenant debe asignarse al menos un primer usuario con rol Administrador para ese tenant.
- Un tenant inactivo no puede recibir nuevas autenticaciones operativas ni nuevas reservas, pero debe conservar trazabilidad e informacion historica.
- El tenant activo de una operacion debe quedar determinado antes de ejecutar autenticacion, consulta o registro sensible, por ejemplo por seleccion explicita del tenant, canal dedicado, enlace del tenant o configuracion operativa del acceso habilitado.
- Solo el Administrador de plataforma puede crear, activar, inactivar o reactivar tenants.
- La inactivacion de un tenant requiere motivo registrado y no debe eliminar informacion historica ni auditoria asociada.
- La reactivacion de un tenant debe dejar trazabilidad del responsable, fecha, hora y motivo.
- Ninguna operacion de soporte sobre un tenant inactivo puede reactivar implicitamente su operacion; la reactivacion debe ser una accion explicita.

### Regla base de identidad del cliente entre tenants

- En Fase 1, una misma persona puede relacionarse con mas de un tenant.
- Esa relacion debe tratarse como pertenencias separadas por tenant, aunque reutilice el mismo correo electronico.
- El historial, las reservas, los permisos y la informacion operativa del cliente deben permanecer aislados por tenant.
- La forma tecnica de resolver si la autenticacion usa cuenta global con pertenencias por tenant o cuentas separadas por tenant queda sujeta a arquitectura, siempre que el comportamiento funcional anterior se preserve.

### Roles no confirmados formalmente

- Gerente: PENDIENTE DE CONFIRMACION DEL CLIENTE
- Contador: PENDIENTE DE CONFIRMACION DEL CLIENTE
- Analista: PENDIENTE DE CONFIRMACION DEL CLIENTE

## 10. Necesidades del cliente

Con base en las fuentes funcionales del proyecto, el cliente necesita:

- centralizar informacion actualmente dispersa
- registrar reservas con informacion de clientes y servicios
- permitir que el cliente final consulte tours, hospedaje y disponibilidad desde una aplicacion movil
- permitir que el cliente final genere su propia reserva desde donde se encuentre
- diferenciar entre lo reservado y lo realmente prestado
- registrar costos operacionales internos
- consolidar flujo de caja interno
- reflejar descuentos vigentes sobre tours u otros servicios al momento de reservar
- aplicar automaticamente descuentos confirmados al momento de cobrar o pagar, incluyendo descuentos comerciales y descuentos adicionales autorizados por fidelizacion u otro motivo justificado
- gestionar estados de pago, tiempos limite de respuesta del cliente y cancelacion automatica de reservas no pagadas
- consultar informacion operativa y resultados del negocio
- disponer de reportes y dashboard para seguimiento diario y administrativo
- asegurar que cada empresa usuaria de la plataforma vea y gestione unicamente su propia informacion, sin mezcla de datos ni configuraciones con otros tenants

## 11. Alcance

### Dentro del alcance

- Soporte multitenencia con aislamiento logico por tenant para usuarios, clientes, reservas, catalogos operativos, descuentos, costos, caja y reportes.
- Gestion de clientes para reservas.
- Gestion de informacion base de atractivos, alimentacion, hospedaje y transporte necesaria para reservas y control operacional.
- Gestion de informacion obligatoria y documental requerida para clientes y acompanantes segun el tipo de actividad.
- Aplicacion movil para cliente final orientada a consulta de tours, hospedaje y autogestion de reservas.
- Registro de reservas como proyeccion comercial.
- Visualizacion de descuentos vigentes sobre tours u otros servicios en los canales de reserva habilitados para el cliente final.
- Aplicacion automatica de descuentos vigentes y descuentos adicionales autorizados al momento de cobrar o pagar.
- Flujo de pago con modalidades parametrizables como transferencia, efectivo y abono, con tiempos limite y seguimiento operativo sobre reservas pendientes de pago.
- Hospedaje como parte del alcance funcional documentado en el proyecto.
- Control de ejecucion de servicios prestados y no prestados.
- Registro de costos operacionales.
- Gestion operativa diaria de caja con consolidacion mensual.
- Consultas operativas, reporte de reservas, dashboard diario y reporte administrativo de caja mensual.
- Seguridad basica de acceso para usuarios internos y restriccion de operaciones sensibles.

### Fuera del alcance

- Administracion avanzada de roles, modulos y permisos detallados mas alla de los perfiles base confirmados para Fase 1.
- Integraciones con pasarelas de pago, bancos o sistemas contables externos no descritos expresamente en este PDR.
- Implementacion de facturacion electronica o integracion tributaria externa.
- Estrategias avanzadas de personalizacion visual por tenant, como white-label completo, dominios independientes por cliente o motores de tematizacion avanzados, salvo la configuracion minima necesaria para identificar el tenant activo.

### Pendiente de confirmacion

- Reglas comerciales particulares que futuros tenants adicionales puedan requerir por fuera de la base funcional definida para Fase 1.
- Integraciones externas con pasarelas, bancos o sistemas contables que excedan el registro manual y la validacion operativa definida para Fase 1.

### Baseline adoptado para Fase 1

- Para efectos de arquitectura detallada y construccion del backlog, este PDR adopta como baseline vigente de Fase 1 los criterios no funcionales definidos en la seccion 17.
- Cualquier ajuste posterior a ese baseline debe tratarse como cambio controlado del PDR y no como regla implicita abierta a interpretacion.

## 12. Procesos de negocio

Observacion transversal de multitenencia:
Todos los procesos de negocio descritos a continuacion deben ejecutarse dentro del contexto de un tenant identificado. Ningun proceso puede mezclar datos, configuraciones, usuarios, reservas, movimientos de caja o reportes entre tenants distintos.

Regla operativa transversal:
Para Fase 1, todo comando o consulta autenticada debe poder resolverse contra un tenant activo previamente determinado. Ninguna autenticacion exitosa debe habilitar acceso simultaneo a informacion de multiples tenants dentro de una misma sesion operativa sin cambio explicito de contexto.

### 12.1 Gestion de reservas

Proceso orientado a registrar la proyeccion comercial de una reserva, incluyendo cliente titular, acompanantes, servicios solicitados, valor proyectado y valor final.

- Objetivo: dejar registrada la intencion comercial inicial del cliente.
- Inicio: cuando se requiere crear una nueva reserva.
- Actor principal: Colaborador operativo / Administrador.
- Informacion involucrada: cliente titular, acompanantes, atractivos, alimentacion, hospedaje, transporte, descuentos, documentos obligatorios y valores resultantes.
- Comportamiento esperado: registrar la informacion minima obligatoria, calcular personas y valores, validar capacidad de hospedaje y persistir la reserva.
- Reglas asociadas: RN-CLI-001, RN-CLI-002, RN-RES-001, RN-RES-002, RN-RES-003, RN-RES-004, RN-RES-005, RN-HOS-003.
- Resultado esperado: reserva creada como base de seguimiento operativo.
- Casos alternativos relevantes: documentos duplicados, capacidad insuficiente en hospedaje, descuentos invalidos, falta de informacion obligatoria para actividades de riesgo.

Estado: CONFIRMADO

### 12.1.1 Autogestion de reservas desde aplicacion movil

Proceso orientado a permitir que el cliente final consulte la oferta disponible y genere su reserva desde una aplicacion movil, sin depender exclusivamente del registro interno.

- Objetivo: facilitar la autogestion comercial por parte del cliente final desde cualquier ubicacion.
- Inicio: cuando el cliente final ingresa a la aplicacion movil para consultar o reservar servicios.
- Actor principal: Cliente final usuario de aplicacion movil.
- Informacion involucrada: tours, hospedaje, alimentacion, transporte, descuentos vigentes, datos del cliente, acompanantes, condiciones parametrizadas y valores calculados.
- Comportamiento esperado: permitir consultar la oferta disponible, visualizar descuentos vigentes, seleccionar servicios, registrar datos requeridos y generar la reserva con valores actualizados.
- Reglas asociadas: RN-CLI-001, RN-CLI-002, RN-RES-001, RN-RES-002, RN-RES-003, RN-RES-004, RN-HOS-003.
- Resultado esperado: reserva creada desde canal movil con trazabilidad equivalente a la gestion interna.
- Casos alternativos relevantes: descuento vigente aplicado a un tour, descuento adicional autorizado al momento de pago, capacidad insuficiente en hospedaje, datos incompletos del cliente o acompanantes.

Estado: CONFIRMADO

Observacion funcional base de Fase 1:
Para efectos de disponibilidad comercial en canales de consulta y reserva, un tour o servicio se considera disponible cuando se encuentra habilitado para venta por el negocio, dentro de su vigencia de oferta y sin restricciones operativas que lo excluyan de manera expresa para la fecha consultada.

Observacion adicional de cupos y concurrencia:
Como base funcional de Fase 1, los recursos que manejan cupo limitado deben quedar parametrizados por el negocio. Como minimo se reconoce capacidad limitada para hospedaje y puede configurarse tambien para tours, transportes u otros servicios que el Administrador marque como controlados por cupo. Cuando exista el ultimo cupo disponible, la reserva que primero quede registrada con cumplimiento de las condiciones de apartamiento definidas por el negocio es la que obtiene dicho cupo. Toda reserva posterior debe recibir respuesta de capacidad insuficiente o de cupo no disponible.

### 12.2 Ejecucion de servicios

Proceso orientado a registrar que servicios de la reserva realmente se prestaron y cuales no, dejando visible la diferencia frente a la proyeccion inicial.

- Objetivo: controlar la operacion real y diferenciarla de la reserva original.
- Inicio: cuando la actividad esta proxima a iniciar y el guia realiza el control operativo previo a la salida.
- Observacion del actor guia: para Fase 1, el guia se entiende como actor operativo externo al sistema o fuente operativa de informacion previa a la salida, salvo que arquitectura o UX definan posteriormente una interfaz propia para ese rol.
- Actor principal: Colaborador operativo / Administrador.
- Informacion involucrada: reserva existente, servicios reservados, servicios prestados, servicios no prestados, causales de cancelacion o no prestacion, pagos registrados y control operativo previo a la salida.
- Comportamiento esperado: registrar servicios prestados y no prestados, mantener visible la diferencia frente a la proyeccion inicial y reflejar la tolerancia operativa aplicable antes de la salida. Una vez el tour ha salido a su destino no se permiten ajustes ordinarios en ejecucion.
- Reglas asociadas: RN-EJE-001, RN-EJE-002, RN-EJE-003, RN-EJE-004, RN-EJE-005.
- Resultado esperado: trazabilidad clara entre lo reservado y lo efectivamente ejecutado.
- Casos alternativos relevantes: llegada tardia de una persona, no presentacion sin aviso oportuno, servicios no prestados sin causal, cancelacion extraordinaria del tour por emergencia con reagendamiento o devolucion.

Estado: CONFIRMADO

### 12.3 Control de costos operacionales

Proceso orientado a registrar y controlar costos internos asociados a la prestacion de servicios.

- Objetivo: conocer el costo real de operar los servicios prestados.
- Inicio: cuando existen servicios efectivamente prestados que requieren control de costo.
- Actor principal: Administrador / Colaborador operativo.
- Informacion involucrada: atractivos prestados, cantidades reales, costos operacionales catalogados y costos asociados a la ejecucion.
- Comportamiento esperado: registrar o calcular costos operacionales diferenciados del valor comercial.
- Reglas asociadas: RN-ATR-001, RN-OPE-001.
- Resultado esperado: base confiable para analisis operativo, rentabilidad y caja.
- Casos alternativos relevantes: parametrizacion incompleta de costos o necesidad de validacion adicional sobre algunos costos.

Estado: CONFIRMADO

### 12.4 Consolidacion de caja

Proceso orientado a gestionar la caja diaria, sus aperturas y cierres, y consolidar la informacion economica del mes para control interno del negocio.

- Objetivo: dar visibilidad al estado diario de caja y a su consolidacion administrativa mensual.
- Inicio: cuando inicia una jornada operativa o cuando existen movimientos economicos registrados para el periodo correspondiente.
- Actor principal: Colaborador operativo / Administrador.
- Informacion involucrada: base diaria parametrizable por dia, ingresos, pagos operacionales, gastos, devoluciones, cierres e historico de movimientos.
- Comportamiento esperado: abrir la jornada con una base parametrizable segun el dia, registrar movimientos del dia, permitir cierre cuando corresponda, conservar historico y consolidar la informacion mensual bajo la logica funcional definida. Para Fase 1, la base representa el saldo operativo inicial del dia y no debe sumarse repetidamente como ingreso nuevo en la consolidacion mensual.
- Reglas asociadas: RN-CAJ-001.
- Resultado esperado: estado de caja visible para seguimiento administrativo.
- Casos alternativos relevantes: ajuste de parametros de base por parte del Administrador y consolidacion mensual a partir de informacion diaria.

Estado: CONFIRMADO

## 13. Requerimientos funcionales

### RF-001 - Registrar cliente titular

- Descripcion: El sistema debe permitir registrar un cliente titular con la informacion requerida para asociarlo a una reserva.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Ninguna
- Comportamiento esperado: Debe capturar los datos obligatorios definidos para el cliente titular y validar la informacion adicional exigida cuando la actividad sea de riesgo.
- Resultado esperado: Cliente titular disponible para uso en una reserva.
- Reglas relacionadas: RN-CLI-001
- Excepciones conocidas: La regla de descuentos debe ser parametrizable por el administrador y no afecta la obligatoriedad documental.
- Estado: CONFIRMADO

### RF-002 - Registrar acompanantes

- Descripcion: El sistema debe permitir asociar cero o varios acompanantes a una reserva.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Reserva creada o proceso de reserva en curso.
- Comportamiento esperado: Cada acompanante debe registrarse con datos individualizados y con la informacion obligatoria que corresponda segun el tipo de actividad. Como minimo, en Fase 1 debe registrarse nombre completo, documento de identidad, fecha de nacimiento o edad y un dato basico de contacto o referencia cuando el negocio lo requiera. Si la actividad es de riesgo, tambien aplican los requisitos adicionales definidos para ese tipo de actividad.
- Resultado esperado: Reserva con cantidad de personas calculable.
- Reglas relacionadas: RN-CLI-002, RN-RES-005
- Excepciones conocidas: Ninguna conocida.
- Estado: CONFIRMADO

### RF-003 - Crear reserva como proyeccion comercial

- Descripcion: El sistema debe permitir registrar una reserva que represente lo que el cliente desea adquirir.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Datos base disponibles
- Comportamiento esperado: Debe incluir servicios seleccionados, condiciones parametrizadas de modificacion o cancelacion y valores calculados desde datos parametrizados.
- Resultado esperado: Reserva registrada como base de seguimiento.
- Reglas relacionadas: RN-RES-001, RN-RES-002, RN-RES-003, RN-RES-004
- Excepciones conocidas: Las restricciones de modificacion o cancelacion dependen de los terminos y condiciones parametrizados para la actividad o servicio correspondiente.
- Estado: CONFIRMADO

### RF-003A - Modificar reserva antes de la ejecucion

- Descripcion: El sistema debe permitir modificar una reserva antes de que entre en ejecucion, siempre que las condiciones parametrizadas del servicio o actividad lo permitan.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Reserva existente, aun no iniciada y dentro de las condiciones permitidas de modificacion.
- Comportamiento esperado: Debe permitir cambiar servicios, cantidades, acompanantes, hospedaje, transporte u otros elementos autorizados por el negocio. Toda modificacion debe recalcular valores, revalidar capacidad, reevaluar descuentos vigentes segun la politica comercial aplicable y determinar si existe saldo adicional por cobrar, saldo a favor o devolucion. Los pagos ya registrados no se pierden y deben seguir formando parte del saldo resultante. Si el recalculo produce una devolucion potencial, el sistema debe distinguir entre el calculo del saldo a devolver y la autorizacion o ejecucion efectiva de la devolucion monetaria.
- Resultado esperado: Reserva actualizada con trazabilidad clara del cambio y del nuevo estado economico resultante.
- Reglas relacionadas: RN-RES-004, RN-RES-006, RN-TRA-002
- Excepciones conocidas: Si la reserva ya se encuentra en ejecucion, solo aplican cancelaciones extraordinarias por emergencia segun las reglas del negocio.
- Estado: CONFIRMADO

### RF-004 - Gestionar informacion base operativa para reservas

- Descripcion: El sistema debe permitir disponer de informacion base de atractivos, alimentacion, hospedaje y transporte necesaria para registrar reservas y soportar el control operacional.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Ninguna
- Comportamiento esperado: Debe permitir como minimo consultar, registrar y actualizar la informacion parametrizada necesaria para usar esos servicios dentro de la reserva y del control operativo. Cuando un elemento deje de utilizarse, debe poder quedar inactivo para no seguir siendo ofrecido en nuevas reservas sin perder trazabilidad historica. En Fase 1, el Administrador puede crear, actualizar e inactivar catalogos, tarifas base, costos base y parametros de capacidad; el Colaborador operativo puede consultar catalogos y, cuando el negocio lo habilite, registrar o actualizar informacion descriptiva operativa que no altere descuentos, tarifas base, costos base ni parametros estructurales de caja.
- Resultado esperado: Base operativa centralizada y utilizable por los procesos del negocio.
- Reglas relacionadas: RN-ATR-001, RN-ALI-001, RN-HOS-001, RN-HOS-003, RN-TRA-001, RN-TRA-002
- Excepciones conocidas: La eliminacion fisica de informacion base no queda definida en este PDR y no debe asumirse como parte obligatoria de Fase 1.
- Estado: CONFIRMADO

### RF-005 - Calcular valor proyectado y valor final

- Descripcion: El sistema debe calcular el valor proyectado de la reserva y permitir persistir el valor final cuando existan descuentos aplicables.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Servicios seleccionados
- Comportamiento esperado: El calculo debe salir de informacion parametrizada, descuentos configurados por el administrador y reglas de transporte por persona segun el tour correspondiente. Cuando existan descuentos de diferente tipo, el sistema debe dejar visible si cada descuento se calcula sobre el valor original o sobre el subtotal resultante del descuento anterior, de acuerdo con la parametrizacion vigente.
- Resultado esperado: Valores visibles y persistidos para seguimiento.
- Reglas relacionadas: RN-RES-001, RN-RES-002, RN-RES-003, RN-TRA-002
- Excepciones conocidas: La forma exacta de combinar multiples descuentos depende de la parametrizacion definida por el administrador.
- Estado: CONFIRMADO

### RF-005A - Visualizar descuentos vigentes en la reserva

- Descripcion: El sistema debe permitir que el cliente final visualice descuentos vigentes aplicables a tours u otros servicios al momento de consultar y crear una reserva desde la aplicacion movil.
- Actor: Cliente final usuario de aplicacion movil
- Precondiciones: Servicios disponibles y descuentos parametrizados
- Comportamiento esperado: Debe reflejar en la cotizacion y en el valor proyectado cualquier descuento vigente aplicable al servicio consultado.
- Resultado esperado: Cliente informado del valor actualizado antes de confirmar la reserva.
- Reglas relacionadas: RN-RES-002, RN-RES-003
- Excepciones conocidas: La aplicacion de descuentos simultaneos depende de la parametrizacion vigente definida por el administrador.
- Estado: CONFIRMADO

### RF-005B - Aplicar descuentos automaticos al cobro o pago

- Descripcion: El sistema debe aplicar automaticamente los descuentos que correspondan al momento de cobrar o pagar una reserva.
- Actor: Cliente final usuario de aplicacion movil / Colaborador operativo / Administrador
- Precondiciones: Reserva existente y descuentos parametrizados o autorizados
- Comportamiento esperado: Debe aplicar primero los descuentos vigentes del servicio segun la configuracion activa para la fecha, y cuando corresponda permitir registrar y reflejar un descuento adicional autorizado por fidelizacion o por otro motivo definido por el negocio. El sistema debe dejar visible que descuentos fueron aplicados, en que orden y sobre que valor se calcularon.
- Resultado esperado: Valor final de cobro calculado de forma consistente y trazable.
- Reglas relacionadas: RN-RES-002, RN-RES-003
- Excepciones conocidas: El orden de aplicacion, la acumulacion, los topes y los motivos habilitados deben obedecer a la parametrizacion configurada por el administrador.
- Estado: CONFIRMADO

### RF-006 - Validar capacidad de hospedaje

- Descripcion: El sistema debe validar la capacidad del hospedaje frente a la cantidad de personas.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Hospedaje seleccionado
- Comportamiento esperado: Debe advertir si la capacidad es insuficiente y no debe permitir confirmar la reserva con hospedaje mientras la capacidad requerida no quede satisfecha.
- Resultado esperado: Prevencion de una reserva inconsistente.
- Reglas relacionadas: RN-HOS-003
- Excepciones conocidas: Ninguna conocida
- Estado: CONFIRMADO

Observacion funcional adicional:
Si el negocio parametriza otros servicios con cupo limitado, la misma logica de validacion de disponibilidad debe aplicarse sobre dichos recursos antes de confirmar o apartar cupo para la reserva.

### RF-007 - Registrar ejecucion real de servicios

- Descripcion: El sistema debe permitir registrar que servicios realmente se prestaron y cuales no.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Reserva existente, control operativo de salida realizado por el guia y tour listo para salir hacia su destino
- Comportamiento esperado: Debe permitir seguimiento de prestados, no prestados, sus causales y la aplicacion de la tolerancia operativa previa a la salida. El estado de ejecucion inicia cuando el tour ya salio hacia su destino.
- Resultado esperado: Diferenciacion clara entre proyeccion y ejecucion real.
- Reglas relacionadas: RN-EJE-001, RN-EJE-003, RN-EJE-005
- Excepciones conocidas: Si una persona no se presenta y no hubo aviso oportuno, puede aplicarse la tolerancia operativa definida antes de marcar la no participacion.
- Estado: CONFIRMADO

### RF-008 - Permitir ajustes en ejecucion

- Descripcion: El sistema no debe permitir ajustes ordinarios posteriores a la reserva una vez el servicio se encuentre en ejecucion.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Reserva en estado En ejecucion
- Comportamiento esperado: Debe bloquear cambios ordinarios sobre tour, hospedaje, alimentacion, transporte y cantidad de personas una vez el tour haya salido hacia su destino. Solo debe permitirse registrar una cancelacion extraordinaria por emergencia con su respectiva justificacion y la decision posterior de reagendamiento o devolucion.
- Resultado esperado: Integridad operativa y trazabilidad de eventos excepcionales ocurridos durante la ejecucion.
- Reglas relacionadas: RN-EJE-002, RN-EJE-004
- Excepciones conocidas: Solo aplican cancelaciones extraordinarias por emergencia debidamente justificadas.
- Estado: CONFIRMADO

### RF-009 - Registrar costos operacionales

- Descripcion: El sistema debe permitir registrar o calcular costos operacionales internos asociados a la ejecucion.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Ejecucion iniciada
- Comportamiento esperado: Debe diferenciar costo operacional de valor comercial y permitir registrar gastos operacionales reales cuando el Administrador o el Colaborador operativo efectuen pagos asociados a la operacion.
- Resultado esperado: Base para analisis operativo y caja.
- Reglas relacionadas: RN-ATR-001, RN-OPE-001
- Excepciones conocidas: Ninguna conocida.
- Estado: CONFIRMADO

### RF-010 - Consolidar flujo de caja interno

- Descripcion: El sistema debe gestionar la caja diaria, incluyendo base, ingresos, pagos, gastos, cierre e historico, y consolidar esa informacion para control mensual.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Movimientos registrados
- Comportamiento esperado: Debe reflejar la logica BASE + INGRESOS - PAGOS - GASTOS - DEVOLUCIONES = TOTAL para cada jornada y permitir consolidacion mensual.
- Resultado esperado: Visibilidad del estado de caja.
- Reglas relacionadas: RN-CAJ-001
- Excepciones conocidas: La base diaria debe ser parametrizable por el Administrador segun el dia de operacion.
- Estado: CONFIRMADO

### RF-011 - Consultar reservas y ejecucion

- Descripcion: El sistema debe permitir consultar la informacion necesaria para seguimiento de reservas, acompanantes, servicios reservados, servicios prestados, servicios no prestados, causales registradas y metricas operativas del dia.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Informacion registrada
- Comportamiento esperado: Debe facilitar el seguimiento de la diferencia entre lo reservado y lo ejecutado e incluir como minimo el reporte de reservas, reservas pendientes de pago, proximas reservas, cancelaciones, acompanantes registrados, servicios reservados, servicios prestados, servicios no prestados y sus causales. El dashboard diario debe mostrar como minimo las metricas operativas que el cliente ya confirmo para seguimiento del dia y, mientras no se acuerde una version mas detallada, debe contemplar como base minima reservas creadas del dia, reservas pendientes de pago, reservas confirmadas, reservas canceladas y tours o servicios proximos a ejecutar.
- Resultado esperado: Soporte al control operativo y al seguimiento de novedades.
- Reglas relacionadas: RN-EJE-005, RN-CAJ-001
- Excepciones conocidas: Ninguna conocida para los campos de reporte ya confirmados.
- Estado: CONFIRMADO

### RF-012 - Consultar costos operacionales y caja

- Descripcion: El sistema debe permitir consultar la informacion registrada de costos operacionales, ingresos, pagos, gastos y consolidacion mensual de caja para control interno.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Informacion registrada
- Comportamiento esperado: Debe facilitar la consulta de la informacion economica y operativa consolidada necesaria para control interno, incluyendo como minimo ventas por dia, caja, gastos, devoluciones, cancelaciones con su causal y consolidacion mensual bajo la logica funcional definida. Para Fase 1, ventas por dia debe entenderse como reservas cuyo valor comercial quedo confirmado en esa fecha; ingresos del periodo como dinero efectivamente recibido en caja o validado como ingreso del periodo; devoluciones del periodo como devoluciones cuya salida efectiva de dinero quedo registrada en ese periodo; cancelaciones del periodo como reservas cuya cancelacion quedo registrada en ese periodo; y costos del periodo como costos operacionales registrados para servicios efectivamente ejecutados en ese periodo. El reporte administrativo mensual debe mostrar los campos economicos y operativos confirmados por el cliente para control interno y, como base minima mientras se valida su version final, debe contemplar ingresos del periodo, pagos operacionales, gastos, devoluciones efectivamente realizadas en el periodo, total consolidado de caja, cancelaciones y costos operacionales registrados.
- Resultado esperado: Soporte al seguimiento administrativo de costos y caja.
- Reglas relacionadas: RN-OPE-001, RN-CAJ-001
- Excepciones conocidas: Ninguna conocida para los campos administrativos ya confirmados.
- Estado: CONFIRMADO

### RF-013 - Consultar tours y hospedaje desde aplicacion movil

- Descripcion: El sistema debe permitir que el cliente final consulte desde la aplicacion movil la oferta de tours, hospedaje y servicios relacionados disponibles para reserva.
- Actor: Cliente final usuario de aplicacion movil
- Precondiciones: Informacion base disponible
- Comportamiento esperado: Debe mostrar informacion util para la toma de decision comercial, incluyendo tours disponibles, zonas de hospedaje disponibles, descuentos vigentes cuando existan y la demas informacion comercial que el Administrador haya parametrizado para visibilidad del cliente final.
- Resultado esperado: Cliente con informacion suficiente para iniciar una reserva desde cualquier ubicacion.
- Reglas relacionadas: RN-ATR-001, RN-HOS-001, RN-HOS-003, RN-RES-002
- Excepciones conocidas: El nivel exacto de detalle visible para el cliente final podra ajustarse durante diseno UX/UI sin alterar el alcance funcional.
- Estado: CONFIRMADO

### RF-014 - Crear reserva desde aplicacion movil

- Descripcion: El sistema debe permitir que el cliente final cree una reserva directamente desde la aplicacion movil.
- Actor: Cliente final usuario de aplicacion movil
- Precondiciones: Oferta disponible y datos requeridos diligenciados
- Comportamiento esperado: Debe permitir seleccionar servicios, registrar datos del cliente y acompanantes, validar informacion obligatoria, elegir modalidad de pago disponible y persistir la reserva con sus valores correspondientes. La consulta de oferta puede realizarse sin autenticacion previa, pero para confirmar la reserva y posteriormente gestionarla el cliente final debe autenticarse o completar su registro. En Fase 1, gestionar su reserva significa unicamente consultar estado, continuar el proceso de pago, revisar la informacion de la reserva y descargar o consultar los soportes registrados sobre ella. Las capacidades de modificar, cancelar o reagendar desde autogestion quedan fuera de Fase 1 salvo aprobacion documental posterior del negocio.
- Resultado esperado: Reserva creada desde canal movil como parte del flujo comercial del negocio.
- Reglas relacionadas: RN-CLI-001, RN-CLI-002, RN-RES-001, RN-RES-003, RN-RES-004, RN-RES-005, RN-HOS-003
- Excepciones conocidas: El cliente final debe autenticarse con nombre, apellido, correo y contrasena para confirmar o gestionar su reserva, sin perjuicio de los datos adicionales requeridos por cada tour.
- Estado: CONFIRMADO

### RF-015 - Gestionar reservas pendientes de pago

- Descripcion: El sistema debe permitir gestionar el seguimiento, confirmacion o cancelacion automatica de reservas que se encuentren pendientes de pago.
- Actor: Colaborador operativo / Administrador
- Precondiciones: Reserva en estado Pendiente de pago
- Comportamiento esperado: Debe permitir registrar la modalidad de pago esperada, controlar el tiempo limite correspondiente, registrar contacto del colaborador con el cliente y cancelar automaticamente la reserva cuando no exista pago ni respuesta dentro del plazo definido. La parametrizacion minima debe contemplar por modalidad un tiempo inicial de espera y, cuando aplique, un plazo adicional autorizado. Debe permitir manejar pagos parciales o abonos sucesivos cuando la modalidad comercial lo permita, mantener saldo pendiente y registrar validacion, rechazo o correccion de soportes de pago.
- Resultado esperado: Trazabilidad clara de reservas pendientes y liberacion oportuna de reservas no confirmadas.
- Reglas relacionadas: RN-RES-004, RN-RES-006
- Excepciones conocidas: Si el cliente responde que pagara en efectivo o dejara abono, puede otorgarse un plazo adicional de un dia segun la parametrizacion vigente.
- Estado: CONFIRMADO

### RF-015A - Registrar y validar pagos de reserva

- Descripcion: El sistema debe permitir registrar pagos, abonos, soportes de transferencia, validaciones, rechazos y devoluciones asociados a una reserva.
- Actor: Cliente final usuario de aplicacion movil / Colaborador operativo / Administrador
- Precondiciones: Reserva existente y modalidad de pago habilitada.
- Comportamiento esperado: Debe permitir registrar pago esperado, pago recibido, soporte de transferencia cuando aplique, estado de validacion del pago y saldo pendiente. Debe permitir multiples abonos cuando la condicion comercial lo admita. Si un soporte de transferencia es rechazado, el sistema debe conservar trazabilidad del rechazo y mantener la reserva en el estado que corresponda segun tiempo y saldo pendiente. Si existe devolucion, debe quedar valor, motivo y relacion con la reserva y con el movimiento correspondiente de caja cuando implique salida efectiva de dinero.
- Alcance por actor en Fase 1:
  - Cliente final: puede registrar pago o abono segun modalidad habilitada y adjuntar soporte de transferencia cuando aplique.
  - Colaborador operativo: puede consultar pagos, registrar recepcion operativa de dinero cuando corresponda a su funcion y validar o rechazar soportes de transferencia solo si el tenant tiene habilitado ese flujo para dicho rol.
  - Administrador: puede consultar, validar o rechazar pagos y soportes, y autorizar o registrar devoluciones monetarias segun las reglas documentadas de este PDR.
- Resultado esperado: Estado economico de la reserva visible, trazable y consistente con pagos y devoluciones registrados.
- Reglas relacionadas: RN-RES-004, RN-RES-006, RN-CAJ-001
- Excepciones conocidas: La integracion automatica con pasarelas de pago o bancos permanece fuera del alcance de Fase 1.
- Estado: CONFIRMADO

### RF-015B - Gestionar devoluciones de reserva

- Descripcion: El sistema debe permitir calcular, autorizar, registrar y ejecutar devoluciones monetarias derivadas de cancelaciones o modificaciones de reserva.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Reserva existente, causal registrada y valor potencial a devolver determinado por las reglas comerciales parametrizadas.
- Comportamiento esperado: Debe distinguir entre valor potencial a devolver, saldo a favor pendiente, devolucion autorizada y devolucion ejecutada. La autorizacion de devolucion solo puede realizarla el Administrador. El Colaborador operativo solo puede registrar la ejecucion material de la salida de dinero cuando exista autorizacion previa y trazable del Administrador o cuando el propio Administrador la ejecute directamente. Toda devolucion debe registrar causal, responsable, monto, fecha, metodo de salida, relacion con la reserva y relacion con el movimiento de caja correspondiente. Cuando no exista salida efectiva de dinero y el negocio acuerde compensacion futura, el resultado debe quedar registrado como saldo a favor pendiente con su valor y condicion de uso.
- Resultado esperado: Devolucion trazable y consistente con el saldo de la reserva y con la caja.
- Reglas relacionadas: RN-RES-004, RN-RES-008, RN-CAJ-001
- Excepciones conocidas: Si la devolucion no implica salida efectiva de dinero en Fase 1, por ejemplo porque solo existe compensacion futura acordada, debe quedar registrada como saldo a favor pendiente y no como devolucion ejecutada.
- Estado: CONFIRMADO

### RF-015C - Reagendar reserva o servicio afectado

- Descripcion: El sistema debe permitir registrar el reagendamiento derivado de una cancelacion extraordinaria o de una modificacion autorizada que no finaliza en devolucion monetaria inmediata.
- Actor: Administrador / Colaborador operativo
- Precondiciones: Reserva existente, causal registrada y decision explicita de reagendamiento conforme a las reglas del negocio.
- Comportamiento esperado: El reagendamiento debe conservar la relacion con la reserva original, registrar fecha u opcion acordada, recalcular disponibilidad, valores y saldo resultante cuando corresponda, y dejar trazabilidad de quien aprobo la novedad. En Fase 1, el reagendamiento puede resolverse como actualizacion controlada de la misma reserva o como generacion de una nueva reserva vinculada a la original, siempre que el comportamiento funcional preserve trazabilidad bidireccional, estado economico claro y no duplique ingresos ni cupos de forma inconsistente. El criterio operativo por defecto de Fase 1 sera reutilizar la misma reserva cuando no cambie el titular ni el alcance principal del servicio, y generar una nueva reserva vinculada cuando el cambio implique nueva fecha, nuevo servicio principal o nueva condicion comercial que requiera control independiente.
- Resultado esperado: Reserva o servicio reagendado con trazabilidad completa respecto al evento original.
- Reglas relacionadas: RN-EJE-002, RN-EJE-006, RN-RES-004, RN-RES-008
- Excepciones conocidas: Si el reagendamiento no encuentra disponibilidad suficiente, el caso debe volver a decision administrativa entre nueva propuesta, saldo a favor o devolucion.
- Estado: CONFIRMADO

## 14. Reglas de negocio

### RN-CLI-001

- Regla: Antes de crear una reserva deben completarse los campos obligatorios del cliente titular: documento de identidad, datos de contacto, aceptacion de terminos y condiciones, fecha de nacimiento o edad y los demas datos exigidos por la actividad correspondiente. Si la modalidad de reserva o de pago ya requiere soporte de pago en ese momento, tambien debe registrarse el comprobante de pago. Si aplica condicion especial, tambien deben completarse autorizacion para menores, informacion medica basica o de emergencia, seguro o asistencia y los requisitos adicionales definidos para actividades de riesgo.
- Estado: CONFIRMADO

### RN-CLI-002

- Regla: Los acompanantes deben registrarse con datos minimos obligatorios. Como minimo, en Fase 1 cada acompanante debe registrar nombre completo, documento de identidad, fecha de nacimiento o edad y la informacion adicional que corresponda segun el tipo de actividad. Si la actividad es de riesgo, los requisitos adicionales de salud, emergencia o consentimiento aplican tanto al titular como a los acompanantes que participen en dicha actividad, segun corresponda por edad o condicion especial.
- Estado: CONFIRMADO

### RN-CLI-003

- Regla: Para actividades de riesgo son obligatorios, ademas de la informacion general, el consentimiento informado o exoneracion de responsabilidad, el tipo de sangre, el contacto de emergencia y el registro de restricciones fisicas o movilidad reducida.
- Estado: CONFIRMADO

### RN-RES-001

- Regla: El valor proyectado se calcula a partir de los servicios seleccionados y la cantidad total de personas.
- Estado: CONFIRMADO

### RN-RES-002

- Regla: La reserva puede incluir descuentos cuando aplique, tanto por promociones vigentes sobre tours u otros servicios como por descuentos adicionales autorizados al momento de cobro o pago, por ejemplo por fidelizacion u otro motivo definido por el negocio. Los descuentos deben ser parametrizables y administrables por el rol Administrador, quien define su configuracion, vigencia, aplicacion, prioridad, acumulacion, topes y condiciones de uso segun la necesidad del negocio. Como regla base de Fase 1, primero se evalua la existencia de descuentos vigentes del servicio; si existe mas de uno aplicable, el sistema debe resolverlos por prioridad configurada. El descuento adicional autorizado solo puede aplicarse despues del descuento vigente, debe registrar motivo y responsable de autorizacion y debe quedar trazabilidad del valor final calculado. Cuando la configuracion combine descuentos porcentuales y de valor fijo, el sistema debe respetar la base de calculo parametrizada para cada descuento, dejando visible si se aplica sobre el valor original o sobre el subtotal posterior al descuento anterior.
- Estado: CONFIRMADO

### RN-RES-003

- Regla: Deben persistirse valor proyectado y valor final.
- Estado: CONFIRMADO

### RN-RES-004

- Regla: La posibilidad de modificar o cancelar una reserva depende de los terminos y condiciones configurados para la actividad o servicio correspondiente. No existe una unica regla temporal general para todas las actividades. Los tiempos limite dependen del tour y del tipo de cambio solicitado, y deben ser parametrizables. Cuando una reserva se modifica antes de ejecucion, deben recalcularse valores, descuentos, transporte y disponibilidad con base en la politica comercial vigente definida por el negocio. Como regla base de Fase 1, la modificacion debe conservar trazabilidad del valor original, del valor recalculado y del saldo resultante por cobrar, compensar o devolver.
- Estado: CONFIRMADO

Observacion:
Existen ejemplos confirmados de configuracion, como cambio de tour hasta 6 horas antes, cambio de plato antes de servir coordinado con el guia y cambio de hospedaje hasta 2 dias antes. Estos ejemplos no constituyen una regla universal para todas las actividades y deben mantenerse como parametros configurables.

Observacion adicional:
Para hospedaje, si la cancelacion se realiza por fuera del tiempo acordado de al menos 2 dias habiles de anticipacion, el hotel conserva el abono correspondiente segun la condicion comercial aplicable.

Observacion funcional base de Fase 1:
La condicion de pago necesaria para confirmar una reserva depende de la modalidad parametrizada por el Administrador. Como base minima de Fase 1:
- transferencia: la reserva solo puede pasar a Confirmada cuando exista soporte de pago registrado y validacion operativa de recepcion del pago.
- efectivo: la reserva solo puede pasar a Confirmada cuando el negocio haya parametrizado que la modalidad permite confirmacion con compromiso de pago en sitio o cuando el dinero haya sido recibido segun la condicion comercial aplicable.
- abono: la reserva solo puede pasar a Confirmada cuando se haya registrado el abono minimo parametrizado para la actividad o servicio correspondiente.

Observacion financiera base:
Como regla minima de Fase 1, toda cancelacion o modificacion que produzca devolucion debe determinar si la devolucion es total, parcial o inexistente segun la condicion comercial parametrizada para el servicio afectado. El valor definido debe quedar trazable y asociado a la reserva.

### RN-RES-005

- Regla: Si hay acompanantes, cada uno debe registrarse con datos minimos obligatorios y sin duplicidad de documento dentro de la misma reserva.
- Estado: CONFIRMADO

### RN-RES-006

- Regla: Toda reserva que aun no haya cumplido la condicion de pago necesaria para confirmarse puede quedar en estado Pendiente de pago, independientemente del canal en que haya sido creada. Durante el transcurso del mismo dia, el Colaborador operativo puede contactar al cliente para validar si presenta inconvenientes con el pago o si realizara pago en efectivo, transferencia o abono. Si transcurre el tiempo inicial parametrizado sin pago ni respuesta del cliente, la reserva debe cancelarse automaticamente. Si el cliente responde y acuerda pagar en efectivo o dejar abono, puede otorgarse un plazo adicional parametrizado para completar la gestion. La modalidad de pago y los tiempos exactos deben ser parametrizables por el Administrador. Como minimo debe parametrizarse por modalidad un tiempo inicial de espera en horas y un plazo adicional cuando aplique. Mientras la reserva permanezca en estado Pendiente de pago, el manejo de cupo y disponibilidad debe obedecer a la configuracion definida por el negocio para la actividad o servicio correspondiente.
- Estado: CONFIRMADO

Observacion:
La reserva en estado Pendiente de pago no exige comprobante de pago previo para existir, salvo que la modalidad seleccionada o la condicion comercial configurada requieran soporte inmediato.

Observacion adicional:
Como base funcional de Fase 1, el negocio debe poder parametrizar si una reserva en estado Pendiente de pago aparta cupo temporalmente o no lo aparta. Si aparta cupo temporalmente, dicho cupo debe liberarse automaticamente al pasar la reserva a estado Cancelada por vencimiento del tiempo configurado.

Observacion adicional de concurrencia y vencimiento:
Cuando dos intentos de reserva compitan por el ultimo cupo de un mismo recurso controlado, prevalece la primera reserva que cumpla las condiciones funcionales de apartamiento definidas por el negocio y quede registrada antes que la otra. Si el cliente registra un pago o abono exactamente al vencimiento del plazo, prevalece primero la evidencia de pago o abono registrada por el sistema dentro del plazo configurado; si la evidencia se registra despues del vencimiento, prevalece la cancelacion automatica salvo decision operativa excepcional del Administrador con trazabilidad obligatoria. En modalidad transferencia, cargar el comprobante detiene temporalmente la cancelacion automatica por el tiempo de validacion operativa parametrizado; si el comprobante es rechazado y el plazo total ya vencio, la reserva debe pasar a Cancelada.

### RN-RES-006A

- Regla: En Fase 1, toda reserva con implicaciones economicas debe mantener ademas un estado de pago independiente del estado general de la reserva. Los estados minimos de pago son: Sin pago, En validacion, Parcial, Pagado, Rechazado, Saldo a favor pendiente y Devuelto parcial o total. Sin pago es el estado inicial. En validacion aplica cuando existe soporte de transferencia cargado y aun no ha sido aceptado ni rechazado. Parcial aplica cuando existe uno o varios abonos validos pero todavia no se cumple la condicion de confirmacion. Pagado aplica cuando se cumple completamente la condicion economica definida para la modalidad. Rechazado aplica cuando el soporte o registro presentado no fue aceptado. Saldo a favor pendiente aplica cuando existe compensacion futura acordada sin salida efectiva inmediata de dinero. Devuelto parcial o total aplica cuando ya se ejecuto una salida efectiva de dinero vinculada a la reserva. Todo cambio de estado de pago debe dejar trazabilidad de actor, fecha, hora, motivo y relacion con la reserva.
- Estado: CONFIRMADO

### RN-RES-006B

- Regla: Las transiciones minimas del estado de pago en Fase 1 son las siguientes: Sin pago a En validacion cuando se carga soporte de transferencia; Sin pago a Parcial cuando se valida un abono insuficiente para confirmar; Sin pago a Pagado cuando se recibe y valida el valor exigido por la modalidad; En validacion a Pagado cuando el soporte es validado y cubre la condicion de confirmacion; En validacion a Parcial cuando el soporte validado solo cubre una parte; En validacion a Rechazado cuando el soporte no es aceptado; Parcial a Pagado cuando los abonos acumulados cumplen la condicion de confirmacion; Pagado a Devuelto parcial o total cuando se ejecuta una devolucion monetaria; Parcial a Devuelto parcial o total cuando se devuelve total o parcialmente un abono previamente recibido. El estado Rechazado no elimina trazabilidad del intento de pago y debe mantener el saldo pendiente correspondiente.
- Estado: CONFIRMADO

### RN-RES-006C

- Regla: Mientras un pago se encuentre En validacion por soporte de transferencia, la reserva puede permanecer en estado Pendiente de pago, pero la cancelacion automatica debe quedar suspendida solo por el tiempo maximo de validacion parametrizado para la modalidad y el tenant. Al vencer dicho tiempo sin validacion positiva, la reserva retoma su conteo normal y debe cancelarse si el plazo total configurado ya fue excedido.
- Estado: CONFIRMADO

### RN-RES-006D

- Regla: Si un soporte o intento de pago queda en estado Rechazado, el cliente o el usuario interno autorizado puede iniciar un nuevo intento de pago mientras la reserva siga en estado Pendiente de pago y no haya vencido el plazo total configurado. Las transiciones minimas adicionales son: Rechazado a En validacion cuando se carga un nuevo soporte de transferencia; Rechazado a Parcial cuando se valida un nuevo abono insuficiente; y Rechazado a Pagado cuando se valida un nuevo pago suficiente. El rechazo previo debe conservarse en historico y no puede sobrescribirse.
- Estado: CONFIRMADO

### RN-RES-007

- Regla: Todo recurso configurado con cupo limitado debe validar disponibilidad antes de confirmar o apartar cupo para una reserva. El cupo temporal o definitivo solo puede quedar asociado a una reserva del mismo tenant y debe liberarse automaticamente cuando la reserva pierda la condicion que justificaba dicho apartamiento segun la regla parametrizada del negocio. En Fase 1 solo se permiten tres politicas funcionales de cupo por recurso: sin apartamiento previo, apartamiento temporal durante Pendiente de pago y confirmacion directa solo con pago valido. Cada recurso controlado debe usar exactamente una de estas politicas activas por tenant y por periodo de vigencia.
- Estado: CONFIRMADO

Observacion funcional cerrada para Fase 1:
- Sin apartamiento previo: la reserva Pendiente de pago no inmoviliza cupo.
- Apartamiento temporal: la reserva Pendiente de pago inmoviliza cupo hasta el vencimiento parametrizado y luego lo libera automaticamente si no confirma.
- Confirmacion directa solo con pago valido: el cupo se asigna unicamente al validarse el pago o abono que cumpla la condicion de confirmacion definida.

### RN-RES-008

- Regla: Toda devolucion derivada de una cancelacion, modificacion o novedad extraordinaria debe seguir tres decisiones funcionales separadas: determinacion del valor potencial a devolver, autorizacion de la devolucion y ejecucion efectiva de la salida de dinero. La determinacion del valor potencial se calcula segun la condicion comercial parametrizada del servicio y puede dar como resultado devolucion total, parcial o inexistente. La autorizacion solo puede realizarla el Administrador. La ejecucion efectiva de la salida de dinero debe quedar asociada a un movimiento identificable de caja y puede ser registrada por el Administrador o por el Colaborador operativo unicamente cuando exista autorizacion previa trazable. Si solo existe compensacion futura o saldo a favor sin salida efectiva de dinero, el caso no debe marcarse como devolucion ejecutada.
- Estado: CONFIRMADO

### RN-RES-009

- Regla: El saldo a favor pendiente es un resultado economico permitido en Fase 1 cuando una modificacion, cancelacion o novedad extraordinaria produce un valor a compensar pero no existe salida efectiva inmediata de dinero. El saldo a favor no cambia por si solo el estado general de la reserva a Devuelto parcial o total y no debe registrarse como movimiento de caja mientras no exista devolucion ejecutada. Debe registrar valor, origen, fecha, actor responsable, tenant, condicion de uso y reserva relacionada. El saldo a favor puede aplicarse total o parcialmente a una reserva vinculada futura del mismo tenant y del mismo cliente titular, dejando trazabilidad del consumo realizado.
- Estado: CONFIRMADO

### RN-ALI-001

- Regla: La alimentacion se maneja mediante opciones asociadas a restaurante y plato del dia.
- Estado: CONFIRMADO

### RN-HOS-001

- Regla: El hospedaje debe disponer al menos de informacion de precio y capacidad para soportar su uso en reserva.
- Estado: CONFIRMADO

### RN-HOS-003

- Regla: La capacidad del hospedaje debe ser suficiente para la cantidad de personas de la reserva.
- Estado: CONFIRMADO

### RN-TRA-001

- Regla: El transporte forma parte de la reserva y se maneja por trayectos.
- Estado: CONFIRMADO

### RN-TRA-002

- Regla: El transporte debe manejar una tarifa parametrizada por persona. La tarifa puede variar dependiendo del servicio o actividad asociada, y cada tour puede tener una tarifa fija propia por persona para el trayecto correspondiente. Si la reserva se modifica debe recalcularse el valor de transporte que corresponda a la nueva configuracion.
- Estado: CONFIRMADO

### RN-EJE-001

- Regla: La ejecucion se activa cuando el tour sale hacia su destino, despues del control operativo previo a la salida realizado por el guia. Si una persona no se presenta y no hubo aviso oportuno al guia, este puede conceder hasta 10 minutos adicionales antes de iniciar. Si transcurren esos 10 minutos sin presentacion ni aviso oportuno, el servicio correspondiente se mantiene como cobrado aunque la persona no participe en la actividad.
- Estado: CONFIRMADO

### RN-EJE-002

- Regla: En ejecucion no se permiten ajustes ordinarios posteriores a la reserva inicial. Solo puede registrarse una cancelacion extraordinaria del tour por emergencia, la cual debe quedar justificada por el colaborador o responsable operativo y debe conducir a una decision de reagendamiento o devolucion.
- Estado: CONFIRMADO

Observacion funcional base de Fase 1:
Cuando exista cancelacion extraordinaria por emergencia, la reserva o servicio afectado debe quedar con causal registrada y con una definicion posterior obligatoria entre reagendamiento o devolucion. Si la decision es reagendamiento, debe quedar trazabilidad de la nueva fecha o condicion acordada. Si la decision es devolucion, debe quedar trazabilidad del valor definido para devolver segun la regla comercial aplicable. Como base minima de Fase 1, la regla comercial debe permitir definir si la devolucion es total, parcial o no aplica, segun la causa y la condicion comercial del servicio.

### RN-EJE-006

- Regla: El reagendamiento derivado de una emergencia o de una novedad operativa permitida debe preservar la trazabilidad entre el evento original y la solucion acordada. En Fase 1, si el reagendamiento mantiene mismo cliente titular, mismo servicio principal y misma condicion comercial base, puede resolverse sobre la misma reserva conservando el historico del cambio. Si el reagendamiento implica nueva fecha principal, nuevo servicio principal o nueva condicion comercial independiente, debe generarse una nueva reserva vinculada a la original, dejando explicitamente una relacion origen-destino y el tratamiento economico aplicado entre ambas.
- Estado: CONFIRMADO

### RN-EJE-003

- Regla: Solo debe considerarse como base real lo efectivamente prestado.
- Estado: CONFIRMADO

### RN-EJE-004

- Regla: Una vez la reserva entra en ejecucion no deben modificarse cantidad de acompanantes ni servicios asociados como parte del flujo ordinario.
- Estado: CONFIRMADO

### RN-EJE-005

- Regla: Los servicios no prestados deben quedar visibles para seguimiento con su causal.
- Estado: CONFIRMADO

### RN-ATR-001

- Regla: Cada atractivo debe tener definidos valor comercial y costos operacionales para soportar su uso en reserva y control operacional.
- Estado: CONFIRMADO

### RN-OPE-001

- Regla: Para servicios prestados que dependan de costos catalogados, el sistema debe permitir calcular o registrar costos operacionales con base en la parametrizacion disponible y la cantidad real de personas atendidas. El Administrador y el Colaborador operativo pueden registrar gastos operacionales reales cuando estos impliquen salida de dinero de caja durante la operacion. Para Fase 1, se entiende por gasto operacional una salida de dinero originada por consumo interno, compra menor o desembolso operativo no asociado a una obligacion formal previamente causada como pago operacional.
- Estado: CONFIRMADO

### RN-CAJ-001

- Regla: La caja se gestiona operativamente por dia. Cada jornada inicia con una base de caja parametrizable por el Administrador segun la necesidad de cada dia de operacion, por ejemplo entre dias ordinarios y fines de semana. La caja permite registrar ingresos, pagos, gastos y devoluciones, debe soportar cierre formal diario, conservar historico de movimientos y cierres, y permitir obtener el total operativo diario bajo la logica BASE + INGRESOS - PAGOS - GASTOS - DEVOLUCIONES = TOTAL. La modificacion de parametros de base queda restringida al Administrador. Para Fase 1, se entiende por ingreso el dinero efectivamente recibido; por pago operacional la salida de dinero destinada a cubrir obligaciones operativas asociadas a servicios, proveedores o compromisos del negocio; por gasto la salida de dinero menor o administrativa no clasificada como pago operacional; y por devolucion la salida de dinero efectivamente realizada para reintegrar total o parcialmente valores de una reserva. La consolidacion mensual debe construirse a partir de los movimientos diarios y sus cierres, sin sumar repetidamente cada base diaria como si fuera ingreso nuevo del periodo.
- Toda devolucion que implique salida efectiva de dinero debe generar un movimiento identificable en caja, asociado a la reserva correspondiente y sin clasificarse automaticamente como gasto operacional.
- Como regla base de Fase 1, la base de cada jornada es independiente del total de cierre de la jornada anterior, salvo que el Administrador registre expresamente un nuevo valor de base para el dia.
- Estado: CONFIRMADO

Observacion:
Si despues del cierre diario se requiere una correccion operativa excepcional, esta debe quedar restringida al Administrador, con justificacion obligatoria y trazabilidad de la accion realizada. El detalle tecnico del mecanismo de reapertura o ajuste posterior queda sujeto a definicion de arquitectura sin alterar esta regla funcional.

Observacion adicional:
Toda correccion operativa excepcional posterior al cierre debe conservar el historico original del cierre realizado y reflejarse en la consolidacion correspondiente mediante trazabilidad de la novedad aplicada.

## 15. Responsabilidades operativas respaldadas por las fuentes

### Parametrizaciones funcionales permitidas en Fase 1

- Politica de cupo por recurso: sin apartamiento previo, apartamiento temporal o confirmacion directa solo con pago valido.
- Modalidades de pago habilitadas por tenant o servicio: transferencia, efectivo y abono.
- Tiempo inicial de espera por modalidad de pago.
- Tiempo maximo de validacion de soporte de transferencia.
- Plazo adicional permitido para reservas pendientes de pago cuando la condicion comercial lo admita.
- Abono minimo requerido para confirmar una reserva cuando aplique.
- Parametros de descuentos: vigencia, prioridad, acumulacion, topes, base de calculo y motivos autorizables.
- Parametros de modificacion o cancelacion por servicio o actividad.
- Recursos controlados por cupo y su periodo de vigencia.
- Base diaria de caja por dia operativo.

Ninguna otra parametrizacion funcional debe asumirse como obligatoria de Fase 1 si no queda incorporada mediante una nueva version de este PDR.

### Politicas de cupo base definidas para Fase 1

- Hospedaje: apartamiento temporal durante Pendiente de pago.
- Tours o actividades principales: confirmacion directa solo con pago valido, salvo que el Administrador documente explicitamente una politica distinta permitida por este PDR.
- Transportes u otros servicios adicionales con capacidad limitada: sin apartamiento previo por defecto, salvo configuracion explicita del Administrador dentro de las politicas permitidas.

Las siguientes responsabilidades aparecen respaldadas por las fuentes revisadas. En esta version del documento ya existe una alineacion parcial con los roles formales confirmados, aunque algunos detalles funcionales todavia podran refinarse en arquitectura y diseno operativo.

### Funcion operativa de registro de reservas y seguimiento

- Tipo: Funcion operativa identificada
- Responsabilidades respaldadas por las fuentes:
  - registrar clientes y reservas
  - consultar reservas del dia, proximas reservas y seguimiento operativo
  - calcular valores proyectados de la reserva
  - registrar la diferencia entre lo reservado y lo ejecutado
  - verificar documentos obligatorios y requisitos de la actividad
- Estado: FUNCION RESPALDADA / ALINEADA PRINCIPALMENTE CON EL ROL COLABORADOR OPERATIVO

### Funcion operativa de control de costos operacionales

- Tipo: Funcion operativa identificada
- Responsabilidades respaldadas por las fuentes:
  - controlar costos operacionales
- Estado: FUNCION RESPALDADA / ALINEADA CON LOS ROLES ADMINISTRADOR Y COLABORADOR OPERATIVO

### Funcion operativa de control administrativo y de caja

- Tipo: Funcion operativa identificada
- Responsabilidades respaldadas por las fuentes:
  - revisar consolidacion de caja
  - ejecutar cierres de caja
  - registrar gastos y movimientos de caja cuando tenga autorizacion
- Estado: FUNCION RESPALDADA / ALINEADA PRINCIPALMENTE CON EL ROL COLABORADOR OPERATIVO

### Cliente final usuario de aplicacion movil

- Tipo: Funcion operativa propuesta
- Responsabilidades propuestas:
  - consultar tours, hospedaje y otros servicios disponibles
  - visualizar descuentos vigentes aplicables a la reserva
  - crear reservas desde la aplicacion movil
  - autenticarse con sus credenciales para gestionar su reserva
  - avanzar al proceso de cobro o pago con aplicacion automatica de descuentos parametrizados o autorizados
- Estado: FUNCION RESPALDADA / ROL FORMAL CONFIRMADO

### Administrador

- Tipo: Rol del sistema confirmado
- Responsabilidades confirmadas:
  - administrar reservas, descuentos, caja y configuraciones habilitadas del sistema
  - validar o rechazar soportes de transferencia
  - autorizar devoluciones monetarias y registrar su trazabilidad
  - parametrizar descuentos segun las necesidades del negocio
  - mantener control general sobre la operacion del sistema
- Estado: ROL CONFIRMADO

### Administrador de plataforma

- Tipo: Rol del sistema confirmado
- Responsabilidades confirmadas:
  - crear, activar, inactivar y reactivar tenants
  - asignar el primer Administrador de cada tenant
  - consultar la auditoria transversal de plataforma
  - ejecutar soporte administrativo excepcional sobre tenants con trazabilidad obligatoria
- Estado: ROL CONFIRMADO

### Colaborador operativo

- Tipo: Rol del sistema confirmado
- Responsabilidades confirmadas:
  - registrar reservas y consultar reservas del dia o proximas
  - gestionar operaciones de caja de acuerdo con su responsabilidad operativa
  - consultar y registrar informacion de caja cuando corresponda
  - registrar gastos operacionales reales cuando deban pagarse desde caja
  - contactar clientes con reservas pendientes de pago y registrar seguimiento
  - modificar reservas cuando el negocio lo permita antes de la ejecucion
  - operar sin permisos para configurar, autorizar o aplicar descuentos discrecionales fuera de la parametrizacion vigente
- Estado: ROL CONFIRMADO

## 16. Estados y ciclos de vida

### Reserva

- Estados confirmados: Pendiente de pago, Confirmada, En ejecucion, Finalizada y Cancelada.
- Una reserva queda en estado Pendiente de pago cuando el cliente inicia la reserva y aun no completa el pago.
- Si el pago no se realiza, la reserva permanece en Pendiente de pago y la gestion de cupos y disponibilidad debe obedecer a la parametrizacion definida para la actividad o servicio correspondiente.
- Durante el mismo dia, el Colaborador operativo puede contactar al cliente para validar si presenta inconvenientes de pago o si pagara en efectivo, transferencia o abono.
- Si no existe pago ni respuesta del cliente despues del tiempo inicial parametrizado para la modalidad correspondiente, la reserva pasa a Cancelada de forma automatica.
- Si el cliente responde y acuerda pagar en efectivo o dejar abono, puede otorgarse un plazo adicional parametrizado antes de cancelar la reserva.
- Una reserva pasa a Confirmada cuando cumple las condiciones de pago definidas por el negocio segun la modalidad parametrizada.
- Una reserva pasa a En ejecucion cuando el tour sale hacia su destino.
- Una reserva pasa a Finalizada cuando el servicio termina y queda cerrada operativamente.
- Una reserva pasa a Cancelada cuando la reserva completa deja de continuar por falta de pago, decision del cliente o cancelacion extraordinaria del tour conforme a las reglas del negocio.
- La no asistencia de una persona o de parte del grupo no implica automaticamente cancelacion total de la reserva, sino que debe registrarse como novedad operativa segun corresponda.

### Reglas minimas de transicion para Reserva

- Pendiente de pago a Confirmada: cuando se cumple la condicion de pago exigida por la modalidad configurada.
- Pendiente de pago a Cancelada: cuando vence el tiempo parametrizado sin pago, abono, soporte valido en validacion ni respuesta, o cuando el cliente decide no continuar.
- Confirmada a En ejecucion: cuando el tour sale hacia su destino.
- En ejecucion a Finalizada: cuando termina la prestacion del servicio y se cierra operativamente.
- Confirmada o En ejecucion a Cancelada: solo en los casos permitidos por las reglas del negocio y la causal registrada correspondiente.
- Confirmada o En ejecucion a Reserva reagendada vinculada: cuando el caso requiera nueva reserva relacionada conforme a RN-EJE-006.

### Estado de pago

- Estados confirmados: Sin pago, En validacion, Parcial, Pagado, Rechazado, Saldo a favor pendiente y Devuelto parcial o total.
- Sin pago es el estado inicial de una reserva que aun no tiene dinero ni soporte validado.
- En validacion aplica cuando se cargo un soporte de transferencia pendiente de revision.
- Parcial aplica cuando existen abonos validados pero aun no se cumple la condicion de confirmacion.
- Pagado aplica cuando ya se satisface completamente la condicion economica necesaria para confirmar la reserva.
- Rechazado aplica cuando un soporte o intento de pago no fue aceptado, manteniendo trazabilidad y saldo pendiente.
- Saldo a favor pendiente aplica cuando existe compensacion futura acordada sin salida efectiva de dinero.
- Devuelto parcial o total aplica cuando ya se ejecuto una salida efectiva de dinero asociada a la reserva.

### Reglas minimas de transicion para Estado de pago

- Sin pago a En validacion: cuando se carga soporte de transferencia.
- Sin pago a Parcial: cuando se valida un abono insuficiente para confirmar.
- Sin pago a Pagado: cuando se valida el pago suficiente para confirmar.
- En validacion a Pagado: cuando el soporte es aceptado y cubre la condicion de confirmacion.
- En validacion a Parcial: cuando el soporte es aceptado pero no cubre la totalidad exigida.
- En validacion a Rechazado: cuando el soporte no es aceptado.
- Rechazado a En validacion: cuando se carga un nuevo soporte de transferencia dentro del plazo vigente.
- Rechazado a Parcial: cuando se valida un nuevo abono insuficiente dentro del plazo vigente.
- Rechazado a Pagado: cuando se valida un nuevo pago suficiente dentro del plazo vigente.
- Parcial a Pagado: cuando los abonos acumulados cumplen la condicion de confirmacion.
- Pagado a Devuelto parcial o total: cuando se ejecuta una devolucion monetaria.
- Parcial a Devuelto parcial o total: cuando se devuelve total o parcialmente un abono previamente recibido.
- Parcial o Pagado a Saldo a favor pendiente: cuando existe compensacion futura acordada sin salida efectiva de dinero.

### Regla base de disponibilidad comercial

- Para Fase 1, un tour o servicio se considera disponible para consulta y reserva cuando se encuentra habilitado por el negocio para venta, dentro de su vigencia comercial y sin restriccion operativa registrada que lo excluya expresamente para la fecha consultada.
- Si el servicio maneja cupo limitado, la disponibilidad tambien depende de que exista cupo libre o cupo apartable segun la parametrizacion vigente.

### Ejecucion

- La ejecucion comienza cuando el tour ya ha salido hacia su destino.
- Durante la ejecucion se debe registrar lo efectivamente prestado y lo no prestado con su causal correspondiente.
- Durante la ejecucion no se permiten ajustes ordinarios sobre la reserva.
- Si ocurre una emergencia, puede registrarse una cancelacion extraordinaria con su justificacion y con la posterior definicion de reagendamiento o devolucion.

### Caja

- Estados confirmados: Abierta y Cerrada.
- La caja opera diariamente con apertura sobre una base y cierre formal diario.
- La informacion diaria alimenta una consolidacion administrativa mensual.
- La base diaria es parametrizable por el Administrador segun el dia de operacion.

### Tenant

- Estados confirmados: Activo e Inactivo.
- Un tenant se crea por proceso administrativo interno de plataforma.
- Un tenant Activo puede autenticar usuarios y operar reservas.
- Un tenant Inactivo conserva trazabilidad e historico, pero no permite nuevas autenticaciones operativas ni nuevas reservas.

### Reglas minimas de transicion para Tenant

- Creacion administrativa a Activo o Inactivo: segun decision del Administrador de plataforma al momento del alta.
- Activo a Inactivo: por decision administrativa con motivo registrado.
- Inactivo a Activo: por reactivacion explicita del Administrador de plataforma con trazabilidad obligatoria.

## 17. Requerimientos no funcionales

Las diez dimensiones no funcionales fueron revisadas contra las fuentes disponibles del proyecto y consolidadas en esta version 1.3 como baseline vigente para Fase 1. Los criterios aqui definidos deben tratarse como referencia verificable del PDR mientras no exista una actualizacion formal posterior del documento.

- Seguridad: CONFIRMADO PARA FASE 1. En Fase 1 el sistema debe contemplar autenticacion de usuarios, control basico de acceso por perfil operativo, restriccion de operaciones sensibles y aislamiento estricto por tenant. Los perfiles base confirmados son Administrador de plataforma, Cliente final, Administrador y Colaborador operativo. El Cliente final debe autenticarse con nombre, apellido, correo y contrasena para gestionar sus reservas. El Administrador tendra control general del sistema y parametrizacion de descuentos; el Colaborador operativo tendra acceso a caja, reservas, seguimiento y registro operativo sin permisos de descuento; el Administrador de plataforma tendra gestion administrativa de tenants y soporte transversal auditado sin operar comercialmente la informacion de un tenant como flujo ordinario. Todo acceso debe resolverse dentro del tenant correspondiente cuando aplique y debe impedir la consulta o modificacion de informacion de otros tenants. La administracion avanzada de roles, modulos y permisos detallados mas alla de estos perfiles base queda fuera del alcance actual.
- Escalabilidad: CONFIRMADO PARA FASE 1. La solucion debe soportar como escenario inicial al menos 10 tenants activos, con hasta 20 usuarios internos por tenant, hasta 5.000 clientes registrados por tenant y concurrencia operativa baja o moderada propia de una operacion turistica de Fase 1. La arquitectura debe prever crecimiento de tenants y datos sin romper el aislamiento logico por tenant.
- Privacidad y proteccion de informacion: CONFIRMADO PARA FASE 1. El sistema debe preservar la integridad de la informacion gestionada y respetar las politicas y normativa aplicable en Colombia para el tratamiento de la informacion, incluyendo datos personales, medicos o de emergencia asociados a actividades de riesgo. Los datos sensibles del cliente deben conservarse por un ano. El acceso a informacion medica o de emergencia queda restringido a usuarios internos autorizados segun su funcion operativa. El sistema debe conservar evidencia de aceptacion de terminos y consentimientos requeridos por la actividad y, al cumplirse el periodo de retencion definido para datos sensibles, debe aplicar una de dos salidas permitidas en Fase 1: archivo restringido o eliminacion controlada, ambas con trazabilidad obligatoria. En enfoque multitenencia, estas politicas deben aplicarse sin exponer informacion sensible entre tenants.
- Trazabilidad y auditoria: CONFIRMADO PARA FASE 1. El sistema debe mantener auditoria sobre autenticaciones, cambios de descuentos, movimientos de caja, cancelaciones, modificaciones relevantes de reserva, cambios de estado de pago, cambios de tenant, accesos a informacion sensible y autorizaciones o ejecuciones de devolucion. Cada evento auditado debe registrar al menos usuario, tenant cuando aplique, fecha y hora, tipo de accion, identificador del registro afectado y motivo cuando aplique.
- Disponibilidad: CONFIRMADO PARA FASE 1. La solucion debe estar disponible como minimo el 95 por ciento del tiempo mensual, excluyendo ventanas programadas de mantenimiento previamente informadas.
- Rendimiento: CONFIRMADO PARA FASE 1. Las operaciones principales de consulta y registro no deben superar 3 segundos en condiciones normales de operacion interna para el escenario base de Fase 1.
- Usabilidad: CONFIRMADO PARA FASE 1. La solucion debe mantener una navegacion comprensible y consistente para usuarios internos y cliente final, con flujos principales identificables sin ambiguedad operativa. Como criterio minimo verificable, las acciones principales de consulta, creacion de reserva, seguimiento de pago y consulta de caja deben poder completarse sin depender de instrucciones externas al sistema.
- Accesibilidad: CONFIRMADO PARA FASE 1. La solucion debe buscar conformidad con WCAG 2.1 nivel AA en los flujos principales de consulta, autenticacion y reserva.
- Mantenibilidad: CONFIRMADO PARA FASE 1. El sistema debe contar con versionamiento de codigo, documentacion tecnica minima de despliegue y configuracion, separacion clara de responsabilidades entre componentes definidos en arquitectura y un procedimiento basico de operacion para soporte del entorno de Fase 1.
- Respaldo y recuperacion: CONFIRMADO PARA FASE 1. Debe existir al menos un respaldo diario de la informacion operativa, retencion minima de 30 dias, un procedimiento documentado de restauracion, un RPO objetivo maximo de 24 horas y un RTO objetivo maximo de 8 horas.

Bloqueante actual:
No existe bloqueante no funcional abierto que impida continuar con arquitectura detallada para Fase 1, siempre que el equipo respete este baseline como referencia vigente del PDR.

## 18. Restricciones academicas obligatorias

Las siguientes condiciones corresponden a restricciones academicas del proyecto y no a solicitudes directas confirmadas del cliente:

- Bases de datos obligatorias: PostgreSQL y MongoDB.
- Arquitectura frontend obligatoria: exactamente 4 Micro Frontends.
- Frameworks frontend obligatorios: Angular y React.
- Tecnologias backend obligatorias: Java y Go.

La asignacion especifica de responsabilidades entre Java y Go, Angular y React, PostgreSQL y MongoDB REQUIERE DEFINICION DURANTE LA ETAPA DE ARQUITECTURA.

La delimitacion funcional de los cuatro Micro Frontends y su distribucion entre Angular y React REQUIERE DEFINICION DURANTE LA ETAPA DE ARQUITECTURA.

La distribucion de datos y responsabilidades entre PostgreSQL y MongoDB REQUIERE DEFINICION DURANTE LA ETAPA DE ARQUITECTURA.

## 19. Restricciones de despliegue y responsabilidad de entrega

- Restriccion de despliegue prevista: AWS como plataforma prevista para despliegue.
- Restriccion de desarrollo prevista: Docker para la etapa de desarrollo.
- Responsabilidad de entrega: El alcance del equipo del proyecto comprende la entrega del desarrollo del software.
- Responsabilidad de implementacion: La implementacion o puesta en operacion posterior queda bajo responsabilidad de la empresa.

## 20. Supuestos y dependencias

- La documentacion funcional del proyecto se considera valida siempre que no contradiga una necesidad del cliente o una regla de negocio mejor sustentada.
- El alcance de hospedaje se mantiene dentro del documento con base en la documentacion funcional disponible revisada en el proyecto.
- Travesia Natural se mantiene como tenant principal de validacion funcional y demostracion mientras la plataforma evoluciona para soportar multiples operadores turisticos.
- La multitenencia se asume inicialmente con aislamiento logico por tenant, sin que este PDR cierre todavia la decision tecnica definitiva de implementacion.
- Las restricciones academicas obligatorias condicionan la definicion arquitectonica futura del proyecto.
- La definicion definitiva de arquitectura, UX, modelo de datos, base de datos y seguridad debe respetar el baseline confirmado de RNF de la seccion 17 y cualquier cambio posterior requerira actualizacion formal del PDR.

## 21. Riesgos

- Riesgo de inconsistencias comerciales si la parametrizacion de descuentos no se gobierna adecuadamente desde el rol Administrador.
- Riesgo de incumplir restricciones academicas si no se documentan desde esta etapa.
- Riesgo de manejar informacion sensible de salud y emergencia sin implementar de forma consistente los criterios ya definidos de privacidad, respaldo y auditoria.
- Riesgo de dimensionar incorrectamente infraestructura, operacion o seguridad si la arquitectura ignora o interpreta de forma incompleta el baseline no funcional confirmado para Fase 1.
- Riesgo de desalineacion entre arquitectura y producto si el equipo modifica umbrales de seguridad, accesibilidad, disponibilidad o respaldo sin actualizar primero este PDR.
- Riesgo de fuga o cruce indebido de informacion entre tenants si el aislamiento de datos, consultas, reportes y permisos no se implementa de forma consistente.
- Riesgo de ambiguedad documental si parte del proyecto se sigue describiendo como solucion exclusiva para Travesia Natural y otra parte como plataforma multitenencia.
- Riesgo de sobreventa o asignacion incorrecta de cupos si las reglas de apartamiento temporal y concurrencia no se respetan de forma consistente.
- Riesgo de inconsistencias economicas si pagos, abonos, devoluciones, gastos y pagos operacionales no mantienen una semantica funcional unificada.

## 22. Criterios de aceptacion

- CA-001: Debe ser posible registrar un cliente titular y asociarlo a una reserva con la informacion obligatoria general y, cuando aplique, con los requisitos adicionales de actividades de riesgo.
- CA-002: Debe ser posible registrar cero o varios acompanantes con datos individualizados y sin duplicidad de documento dentro de la misma reserva.
- CA-003: Debe ser posible crear una reserva como proyeccion comercial con servicios seleccionados, condiciones parametrizadas aplicables y valores calculados.
- CA-004: Debe existir informacion base utilizable de atractivos, alimentacion, hospedaje y transporte para soportar el registro de reservas y el control operacional, permitiendo como minimo registrar, consultar, actualizar e inactivar registros.
- CA-005: Deben quedar visibles y persistidos el valor proyectado y el valor final de la reserva, incluyendo el calculo del transporte por persona segun el tour correspondiente.
- CA-005A: Desde la aplicacion movil el cliente final debe poder visualizar descuentos vigentes aplicables a tours u otros servicios antes de confirmar la reserva.
- CA-005B: Al momento de cobrar o pagar deben aplicarse automaticamente los descuentos vigentes y, cuando corresponda, los descuentos adicionales autorizados, dejando visible el valor final de cobro, el orden de aplicacion y el motivo del descuento adicional cuando exista.
- CA-005C: Debe ser posible modificar una reserva antes de la ejecucion cuando la regla parametrizada lo permita, recalculando valores, descuentos, disponibilidad y saldo resultante con trazabilidad del cambio.
- CA-006: Si se selecciona hospedaje, el sistema debe advertir cuando la capacidad no sea suficiente para la cantidad total de personas.
- CA-007: Debe quedar diferenciada la reserva original de la ejecucion real de servicios prestados y no prestados.
- CA-008: Cuando existan servicios no prestados, estos deben quedar visibles con su causal de seguimiento.
- CA-008A: Una vez la reserva se encuentre en estado En ejecucion, el sistema no debe permitir ajustes ordinarios sobre tour, hospedaje, alimentacion, transporte ni cantidad de personas. Solo debe permitir registrar cancelacion extraordinaria por emergencia con justificacion y decision posterior de reagendamiento o devolucion.
- CA-009: Debe ser posible registrar o calcular costos operacionales sin confundirlos con el valor comercial vendido.
- CA-010: Debe ser posible gestionar la caja diaria con base parametrizable por el Administrador, ingresos, pagos, gastos, devoluciones, cierre e historico, y obtener la consolidacion mensual bajo la logica de caja definida para control interno.
- CA-011: Debe ser posible consultar la informacion de reservas y ejecucion de forma que se distingan acompanantes, servicios reservados, servicios prestados, servicios no prestados, sus causales registradas y como minimo las metricas operativas diarias confirmadas por el cliente para seguimiento del dia. Mientras no exista una version ampliada validada, el dashboard diario debe contemplar como base minima reservas creadas del dia, reservas pendientes de pago, reservas confirmadas, reservas canceladas y tours o servicios proximos a ejecutar.
- CA-012: Debe ser posible consultar la informacion consolidada de costos operacionales y caja para control interno del negocio, incluyendo ventas por dia, caja, gastos, devoluciones, cancelaciones con su causal y el reporte administrativo mensual con los campos confirmados por el cliente. Mientras no exista una version ampliada validada, el reporte administrativo mensual debe contemplar como base minima ingresos del periodo, pagos operacionales, gastos, devoluciones efectivamente realizadas en el periodo, total consolidado de caja, cancelaciones y costos operacionales registrados.
- CA-013: El cliente final debe poder consultar tours, hospedaje y servicios relacionados desde la aplicacion movil para iniciar una reserva desde cualquier ubicacion.
- CA-014: El cliente final debe poder crear una reserva desde la aplicacion movil con datos obligatorios, valores calculados y descuentos visibles cuando apliquen. La consulta de oferta no requiere autenticacion previa, pero la confirmacion y gestion posterior de la reserva si deben requerir autenticacion del cliente final.
- CA-015: Debe ser posible gestionar reservas pendientes de pago con seguimiento del colaborador, plazos parametrizados por modalidad y cancelacion automatica cuando no exista pago ni respuesta dentro del tiempo definido.
- CA-015A: Debe ser posible registrar pagos, abonos, soportes de transferencia, validaciones, rechazos y devoluciones asociados a una reserva, manteniendo visible el saldo resultante.
- CA-015B: Debe ser posible calcular, autorizar, registrar y ejecutar devoluciones monetarias con causal, responsable, monto, relacion con la reserva y relacion con el movimiento de caja correspondiente.
- CA-015C: Debe ser posible reagendar una reserva o servicio afectado, conservando trazabilidad con la reserva original, disponibilidad recalculada, saldo resultante y criterio claro entre reutilizacion de la misma reserva o creacion de una nueva reserva vinculada.
- CA-016: Toda operacion funcional del sistema debe ejecutarse dentro de un tenant identificado y no debe permitir que usuarios, reservas, clientes, catalogos, costos, caja o reportes de un tenant se mezclen con los de otro.

## 23. Trazabilidad

| ID | Objetivo / Proceso | RF / RN relacionado | Criterio de aceptacion | Estado |
| --- | --- | --- | --- | --- |
| TRA-001 | Centralizar informacion base para reservas | RF-001, RN-CLI-001 | CA-001 | CONFIRMADO |
| TRA-002 | Gestionar acompanantes dentro de la reserva | RF-002, RN-CLI-002, RN-RES-005 | CA-002 | CONFIRMADO |
| TRA-003 | Disponer de informacion base operativa para reservas | RF-004, RN-ATR-001, RN-ALI-001, RN-HOS-001, RN-HOS-003, RN-TRA-001, RN-TRA-002 | CA-004 | CONFIRMADO |
| TRA-004 | Crear reserva como proyeccion comercial | RF-003, RN-RES-001, RN-RES-003, RN-RES-004 | CA-003, CA-005 | CONFIRMADO |
| TRA-004D | Modificar reserva antes de ejecucion | RF-003A, RN-RES-004, RN-RES-006, RN-TRA-002 | CA-005C | CONFIRMADO |
| TRA-004C | Calcular valor proyectado y valor final | RF-005, RN-RES-001, RN-RES-002, RN-RES-003, RN-TRA-002 | CA-005 | CONFIRMADO |
| TRA-004A | Visualizar descuentos vigentes en la reserva | RF-005A, RN-RES-002, RN-RES-003 | CA-005A | CONFIRMADO |
| TRA-004B | Aplicar descuentos automaticos al cobro o pago | RF-005B, RN-RES-002, RN-RES-003 | CA-005B | CONFIRMADO |
| TRA-005 | Validar capacidad cuando exista hospedaje | RF-006, RN-HOS-003 | CA-006 | CONFIRMADO |
| TRA-006 | Diferenciar reserva y ejecucion real | RF-007, RN-EJE-001, RN-EJE-003, RN-EJE-005 | CA-007, CA-008 | CONFIRMADO |
| TRA-007 | Permitir ajustes durante la ejecucion | RF-008, RN-EJE-002, RN-EJE-004 | CA-008A | CONFIRMADO |
| TRA-008 | Controlar costos operacionales | RF-009, RN-ATR-001, RN-OPE-001 | CA-009 | CONFIRMADO |
| TRA-009 | Consolidar caja interna | RF-010, RN-CAJ-001 | CA-010 | CONFIRMADO |
| TRA-010 | Consultar reservas y ejecucion | RF-011, RN-EJE-005, RN-CAJ-001 | CA-011 | CONFIRMADO |
| TRA-011 | Consultar costos operacionales y caja | RF-012, RN-OPE-001, RN-CAJ-001 | CA-012 | CONFIRMADO |
| TRA-012 | Consultar tours y hospedaje desde aplicacion movil | RF-013, RN-ATR-001, RN-HOS-001, RN-HOS-003, RN-RES-002 | CA-013 | CONFIRMADO |
| TRA-013 | Crear reserva desde aplicacion movil | RF-014, RN-CLI-001, RN-CLI-002, RN-RES-001, RN-RES-003, RN-RES-004, RN-RES-005, RN-HOS-003 | CA-014 | CONFIRMADO |
| TRA-014 | Gestionar reservas pendientes de pago | RF-015, RN-RES-004, RN-RES-006 | CA-015 | CONFIRMADO |
| TRA-014A | Registrar y validar pagos de reserva | RF-015A, RN-RES-004, RN-RES-006, RN-CAJ-001 | CA-015A | CONFIRMADO |
| TRA-014B | Gestionar devoluciones de reserva | RF-015B, RN-RES-008, RN-CAJ-001 | CA-015B | CONFIRMADO |
| TRA-014C | Reagendar reserva o servicio afectado | RF-015C, RN-EJE-006, RN-RES-008 | CA-015C | CONFIRMADO |
| TRA-015 | Aislar la informacion y operacion por tenant | Enfoque multitenencia transversal del producto, RNF de seguridad, privacidad y auditoria | CA-016 | CONFIRMADO |

## 24. Pendientes de confirmacion

### Critica

- Validacion final de metricas exactas del dashboard diario y del reporte administrativo mensual antes de construir backlog definitivo.

### Alta

- Confirmacion de las politicas de cupo a habilitar por servicio en Fase 1.

### Media

- Confirmacion de profundidad tecnica deseada para auditoria por evento.
- Confirmacion de criterios ampliados de usabilidad deseados para fases posteriores.

## 24.1 Preguntas puntuales para cierre con cliente o responsable del negocio

- Cuales son exactamente las metricas obligatorias del dashboard diario que deben considerarse definitivas para Fase 1.
- Cuales son exactamente los campos obligatorios del reporte administrativo mensual que deben considerarse definitivos para Fase 1.
- Al terminar el periodo de retencion de datos sensibles, el negocio prefiere archivo restringido, eliminacion controlada o una regla diferente.
- Que nivel de detalle espera el negocio para la auditoria de eventos, ademas del minimo ya definido en este PDR.
- Gerente, Contador y Analista seran actores reales del sistema en Fase 1 o solo stakeholders externos al uso del sistema.

## 25. Glosario

- Reserva: registro de la proyeccion comercial de los servicios solicitados.
- Valor proyectado: valor calculado a partir de lo reservado.
- Valor final: valor proyectado ajustado por descuentos.
- Pendiente de pago: estado de una reserva iniciada que aun no cumple la condicion de pago necesaria para confirmarse. Segun la parametrizacion del negocio, puede apartar cupo temporalmente o no hacerlo.
- Cancelacion total de la reserva: estado en el que la reserva completa deja de continuar por falta de pago, decision del cliente o cancelacion extraordinaria del tour.
- Novedad de no asistencia: registro de una persona o parte del grupo que no participa en el servicio sin que ello implique necesariamente cancelar toda la reserva.
- Descuento vigente: descuento promocional aplicable a un tour o servicio en un periodo determinado.
- Descuento adicional: descuento extraordinario autorizado al momento de cobro o pago, por fidelizacion u otro motivo definido por el negocio.
- Ejecucion: seguimiento de lo realmente prestado.
- Costo operacional: costo interno asociado a la prestacion del servicio.
- Devolucion: salida efectiva de dinero registrada para reintegrar total o parcialmente valores asociados a una reserva.
- Flujo de caja: gestion diaria de base, ingresos, pagos, gastos y devoluciones, con consolidacion administrativa mensual.
- Pago operacional: salida de dinero asociada a obligaciones operativas del negocio, como servicios, proveedores o compromisos formales de operacion.
- Gasto operacional: salida de dinero menor o administrativa de operacion que no se clasifica como pago operacional.
- Estado de pago: ciclo economico de una reserva independiente de su estado operativo general.
- Saldo a favor pendiente: valor economico compensable en una reserva futura vinculada del mismo tenant y cliente, sin salida efectiva inmediata de dinero.
- Reserva reagendada vinculada: nueva reserva relacionada con una reserva original cuando el cambio exige control independiente de fecha, servicio principal o condicion comercial.
- Tenant: empresa operadora que utiliza la plataforma con aislamiento de usuarios, datos, configuraciones y operacion respecto de otros tenants.
- Restriccion academica: condicion tecnologica obligatoria del proyecto, definida por el profesor.
