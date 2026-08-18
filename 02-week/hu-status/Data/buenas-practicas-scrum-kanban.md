# Documentación de buenas prácticas de Scrum y Kanban

## 1. Introducción

El desarrollo de software se realiza en entornos donde los requisitos pueden cambiar, aparecen nuevas necesidades y el equipo necesita obtener retroalimentación durante la construcción del producto. Frente a este escenario, los enfoques ágiles buscan favorecer la adaptación, la colaboración y la entrega frecuente de software que aporte valor.

El **Manifiesto Ágil** establece como ideas centrales valorar las personas y sus interacciones, el software funcionando, la colaboración con el cliente y la capacidad de responder al cambio. Esto no significa eliminar los procesos, la documentación o la planificación, sino utilizarlos de manera que apoyen la entrega de valor y no se conviertan en un obstáculo para el equipo.

Dentro de este contexto se encuentran **Scrum y Kanban**, dos enfoques ampliamente utilizados en proyectos de software, pero que no deben considerarse equivalentes.

Scrum es formalmente un **framework o marco de trabajo ligero** orientado a generar valor mediante soluciones adaptativas para problemas complejos. Kanban, por su parte, se define actualmente como una **estrategia para optimizar el flujo de valor a través de un proceso**. Por esta razón, aunque ambos pueden formar parte de una forma de trabajo ágil, poseen estructuras y mecanismos diferentes.

---

## 2. Scrum

### 2.1. Definición

Scrum es un marco de trabajo ligero utilizado para ayudar a personas, equipos y organizaciones a generar valor mediante soluciones adaptativas para problemas complejos. Su funcionamiento se basa principalmente en el **empirismo** y el pensamiento Lean.

El empirismo plantea que las decisiones deben tomarse con base en la experiencia y en aquello que puede observarse. Scrum desarrolla este principio mediante tres pilares:

* **Transparencia:** el trabajo y su estado deben ser visibles y comprensibles.
* **Inspección:** el progreso y los resultados deben revisarse frecuentemente.
* **Adaptación:** cuando se detectan desviaciones o problemas, deben realizarse ajustes oportunamente.

Scrum también promueve cinco valores: **compromiso, enfoque, apertura, respeto y coraje**, los cuales orientan la forma en que interactúa el Scrum Team.

### 2.2. Propósito

El propósito de Scrum es permitir que un equipo trabaje de forma iterativa e incremental sobre problemas complejos, obteniendo aprendizaje frecuente y generando incrementos de producto que puedan aportar valor.

En lugar de intentar definir desde el principio cada detalle del proyecto, el equipo trabaja mediante períodos cortos denominados **Sprints**, inspecciona los resultados obtenidos y adapta sus siguientes decisiones.

### 2.3. Responsabilidades principales

La guía oficial actual utiliza el concepto de **accountabilities**, que puede entenderse como responsabilidades o rendición de cuentas, en lugar de definir simplemente cargos tradicionales.

El Scrum Team está formado por **Product Owner, Scrum Master y Developers**. Es un equipo **autogestionado y multifuncional**, sin subequipos ni jerarquías internas dentro de Scrum. Sus integrantes deciden internamente quién realiza el trabajo, cuándo y cómo hacerlo.

#### Product Owner

Es responsable de **maximizar el valor del producto** generado por el trabajo del Scrum Team.

Entre sus responsabilidades se encuentran:

* desarrollar y comunicar el Product Goal;
* gestionar correctamente el Product Backlog;
* crear y comunicar claramente sus elementos;
* ordenar los elementos del Product Backlog;
* garantizar que el backlog sea visible y comprensible.

El Product Owner es **una persona y no un comité**, aunque puede representar las necesidades de diferentes interesados.

#### Scrum Master

Es responsable de ayudar a establecer Scrum de acuerdo con su definición y de favorecer la efectividad del Scrum Team.

Entre sus funciones se encuentran:

* ayudar al equipo a comprender Scrum;
* fomentar la autogestión;
* ayudar a eliminar impedimentos;
* facilitar que los eventos Scrum sean productivos;
* apoyar al Product Owner en la gestión del producto;
* colaborar con la organización en la adopción de Scrum.

El Scrum Master no debe actuar como un jefe que distribuye tareas entre los desarrolladores.

#### Developers

Son los miembros del Scrum Team responsables de crear un **Increment** utilizable durante cada Sprint.

Entre sus responsabilidades están:

* crear el Sprint Backlog;
* mantener la calidad mediante la Definition of Done;
* adaptar diariamente el plan para alcanzar el Sprint Goal;
* responsabilizarse profesionalmente del trabajo realizado.

### 2.4. Eventos principales de Scrum

Scrum posee cinco eventos. El **Sprint** contiene los demás eventos.

#### Sprint

Es el período dentro del cual se desarrolla el trabajo necesario para producir valor.

Tiene una duración fija de **un mes o menos**. Al finalizar un Sprint comienza inmediatamente el siguiente. Durante este período no deben realizarse cambios que pongan en riesgo el Sprint Goal y la calidad no debe disminuir.

#### Sprint Planning

Da inicio al Sprint y permite definir el trabajo que se realizará.

La planificación debe responder principalmente:

1. ¿Por qué este Sprint es valioso?
2. ¿Qué puede realizarse durante el Sprint?
3. ¿Cómo se realizará el trabajo seleccionado?

De esta planificación surgen el Sprint Goal y el Sprint Backlog.

#### Daily Scrum

Es un evento con un **timebox máximo de 15 minutos** para los Developers, cuyo propósito es inspeccionar el progreso hacia el Sprint Goal y adaptar el Sprint Backlog cuando sea necesario.

No debe convertirse en una reunión donde cada desarrollador simplemente informa a un jefe qué hizo durante el día. El objetivo principal es facilitar la coordinación y generar un plan útil para continuar avanzando.

#### Sprint Review

Permite inspeccionar los resultados obtenidos durante el Sprint junto con los interesados relevantes y determinar posibles adaptaciones futuras.

No debe limitarse a una presentación o demostración formal. Es una sesión de trabajo donde pueden discutirse cambios del entorno, necesidades del producto y próximos pasos.

#### Sprint Retrospective

Su propósito es identificar formas de aumentar la **calidad y efectividad** del equipo.

Se analizan aspectos como:

* procesos;
* herramientas;
* interacciones;
* problemas encontrados;
* decisiones adoptadas;
* Definition of Done.

Las mejoras más importantes identificadas deberían atenderse lo antes posible.

Los timeboxes máximos para un Sprint de un mes son los siguientes:

| Evento | Timebox máximo |
| --- | --- |
| Sprint Planning | 8 horas |
| Daily Scrum | 15 minutos |
| Sprint Review | 4 horas |
| Sprint Retrospective | 3 horas |

Para Sprints más cortos, estos eventos suelen tener una duración menor.

### 2.5. Artefactos principales

Scrum define tres artefactos principales y cada uno posee un compromiso asociado.

| Artefacto | Descripción | Compromiso |
| --- | --- | --- |
| **Product Backlog** | Lista ordenada y emergente de lo necesario para mejorar el producto. | Product Goal |
| **Sprint Backlog** | Sprint Goal, elementos seleccionados del Product Backlog y plan para desarrollarlos. | Sprint Goal |
| **Increment** | Resultado concreto, integrado y utilizable que acerca el producto a su objetivo. | Definition of Done |

#### Product Goal

Representa un objetivo de largo plazo hacia el cual trabaja el Scrum Team.

#### Sprint Backlog

El Sprint Backlog no es un plan inmutable. Los Developers lo actualizan durante el Sprint a medida que obtienen nueva información. El alcance puede aclararse y renegociarse con el Product Owner siempre que los cambios no pongan en riesgo el Sprint Goal.

#### Sprint Goal

Representa el objetivo único que proporciona dirección y coherencia al trabajo realizado durante un Sprint.

#### Increment

Describe el resultado concreto, integrado y utilizable que acerca el producto a su objetivo.

Durante un Sprint pueden crearse múltiples Increments. Cada Increment debe ser utilizable y cumplir la Definition of Done. Un Increment puede entregarse antes de finalizar el Sprint; no es obligatorio esperar a la Sprint Review para liberar valor.

#### Definition of Done

Describe formalmente las condiciones de calidad que debe cumplir un Increment para considerarse terminado.

Si la organización posee una Definition of Done establecida como estándar, el Scrum Team debe cumplirla como mínimo. Cuando varios Scrum Teams trabajan sobre el mismo producto, deben utilizar una Definition of Done compartida.

### 2.6. Buenas prácticas para aplicar Scrum

Además de respetar los elementos obligatorios del framework, existen prácticas que facilitan una implementación adecuada.

#### Mantener un Product Backlog claro y ordenado

Los elementos prioritarios deben ser suficientemente comprensibles para que puedan discutirse y desarrollarse.

**Importancia:** evita comenzar trabajo sin comprender correctamente qué valor se busca entregar.

#### Definir un Sprint Goal concreto

El Sprint no debe ser simplemente una agrupación de tareas. Debe existir un objetivo que indique qué pretende conseguir el equipo.

**Beneficio:** permite tomar mejores decisiones cuando aparecen cambios o dificultades durante el Sprint.

#### Refinar continuamente el Product Backlog

El refinamiento permite dividir y aclarar elementos del backlog conforme el equipo obtiene nueva información. Scrum reconoce el refinamiento como una actividad continua.

El refinamiento del Product Backlog es una actividad continua y no constituye uno de los cinco eventos formales de Scrum. Scrum tampoco establece una reunión, duración o frecuencia obligatoria para realizarlo.

#### Mantener una Definition of Done clara

El equipo debe compartir el mismo criterio sobre cuándo un trabajo puede considerarse terminado.

**Beneficio:** evita considerar como finalizadas funcionalidades que todavía requieren pruebas, integración u otras condiciones de calidad.

#### Mantener la Daily Scrum enfocada

La conversación debe centrarse en avanzar hacia el Sprint Goal, identificar dificultades y ajustar el trabajo.

#### Entregar incrementos realmente utilizables

Terminar muchas tareas no equivale necesariamente a generar valor. El objetivo debe ser obtener un Increment integrado y que cumpla la Definition of Done.

#### Utilizar la retrospectiva para producir mejoras reales

La retrospectiva pierde utilidad cuando únicamente se mencionan problemas y estos vuelven a aparecer Sprint tras Sprint.

Una buena práctica consiste en seleccionar mejoras concretas y verificar posteriormente si fueron aplicadas.

### 2.7. Malas prácticas comunes en Scrum

Entre los errores más frecuentes se encuentran:

* utilizar al Scrum Master como jefe del equipo;
* convertir la Daily Scrum en un reporte de estado;
* permitir que el Product Owner sea un comité;
* cambiar continuamente el trabajo poniendo en riesgo el Sprint Goal;
* declarar terminadas funcionalidades que no cumplen la Definition of Done;
* eliminar la Sprint Retrospective porque el equipo considera que "no tiene problemas";
* utilizar el Sprint únicamente como una fecha límite;
* realizar la Sprint Review únicamente como una presentación sin obtener retroalimentación;
* asignar directamente a cada Developer qué debe hacer, eliminando la autogestión del equipo.

Estas prácticas reducen las oportunidades de transparencia, inspección y adaptación que constituyen la base de Scrum.

### 2.8. Ejemplo de Scrum en un proyecto de software

Supóngase que un equipo está desarrollando un **sistema web de gestión de reservas**.

El Product Backlog contiene funcionalidades como:

* registro de usuarios;
* autenticación;
* consulta de disponibilidad;
* creación de reservas;
* cancelación de reservas;
* generación de reportes.

Para un Sprint de dos semanas, el equipo establece como Sprint Goal:

> Permitir que un usuario pueda registrarse, autenticarse y acceder de forma segura al sistema.

Los Developers seleccionan los elementos necesarios y crean el Sprint Backlog.

Durante el Sprint:

* se realiza diariamente la Daily Scrum;
* el equipo adapta su planificación cuando encuentra dificultades;
* las funcionalidades se implementan y prueban;
* solo se consideran terminadas cuando cumplen la Definition of Done.

En la Sprint Review se presenta el Increment a los interesados y se obtiene retroalimentación.

Finalmente, durante la Sprint Retrospective, el equipo identifica que varias tareas llegaron demasiado grandes al Sprint y acuerda mejorar su refinamiento para el siguiente ciclo.

---

## 3. Kanban

### 3.1. Definición

Kanban es una estrategia utilizada para **optimizar el flujo de valor a través de un proceso**.

La versión vigente de *The Kanban Guide* establece tres prácticas fundamentales:

1. definir y visualizar el flujo de trabajo;
2. gestionar activamente los elementos dentro del flujo;
3. mejorar el flujo de trabajo.

Kanban puede incorporarse sobre una forma de trabajo existente y no exige reemplazar completamente los procesos que ya utiliza una organización. También puede complementar otros enfoques de trabajo.

### 3.2. Propósito

Kanban busca mejorar la entrega de valor mediante un flujo de trabajo que alcance un equilibrio entre:

* **efectividad:** entregar lo que los interesados necesitan;
* **eficiencia:** utilizar adecuadamente los recursos disponibles;
* **predictibilidad:** poder realizar previsiones razonables sobre la entrega del trabajo.

El objetivo no es mantener a todas las personas ocupadas permanentemente, sino conseguir que el trabajo avance de manera estable y sostenible.

### 3.3. Funcionamiento del tablero Kanban

Un tablero Kanban permite visualizar la **Definition of Workflow (DoW)**, es decir, la definición compartida de cómo fluye el trabajo dentro del sistema.

Un ejemplo sencillo podría ser:

**Pendiente -> Análisis -> Desarrollo -> Revisión -> Pruebas -> Terminado**

Cada tarjeta representa un elemento de trabajo, como:

* una historia de usuario;
* un error;
* una mejora;
* una tarea técnica.

Sin embargo, Kanban **no obliga a utilizar únicamente las columnas "Por hacer, En proceso y Hecho"**. El tablero debe representar el flujo real utilizado por el equipo.

La Definition of Workflow debe establecer, entre otros aspectos:

* qué representa un elemento de trabajo;
* cuándo comienza y termina;
* por cuáles estados puede pasar;
* cómo se controlará el WIP;
* qué políticas permiten mover los elementos;
* cuál es la expectativa de nivel de servicio o SLE.

#### Service Level Expectation - SLE

La **Service Level Expectation (SLE)** expresa una previsión sobre cuánto tiempo debería tardar un elemento de trabajo en recorrer el flujo.

Normalmente combina:

* un período de tiempo;
* una probabilidad.

Por ejemplo:

> El 85 % de los elementos debería finalizar en 8 días o menos.

La SLE permite realizar previsiones utilizando el comportamiento histórico del flujo y ayuda a comunicar expectativas de entrega.

### 3.4. Flujo de trabajo

El flujo representa el movimiento del trabajo desde que comienza hasta que finaliza.

Una gestión adecuada del flujo permite detectar situaciones como:

* tareas que permanecen demasiado tiempo en una etapa;
* bloqueos;
* acumulación de trabajo;
* cuellos de botella;
* capacidad disponible;
* retrasos frecuentes.

Por esta razón, un tablero Kanban no debe utilizarse únicamente como una lista visual de tareas. Debe ayudar al equipo a comprender cómo se está comportando realmente su sistema de trabajo.

### 3.5. Límites de trabajo en progreso - WIP

**WIP (Work in Progress)** corresponde al trabajo que ha comenzado pero todavía no ha finalizado.

Kanban requiere controlar explícitamente el trabajo en progreso entre los puntos de inicio y finalización definidos en el workflow. Los límites numéricos por estado o por grupo de estados son una forma común de controlar el WIP, pero no es obligatorio que cada columna del tablero tenga individualmente un límite numérico.

Cuando existe capacidad disponible, puede seleccionarse nuevo trabajo, generando un sistema basado en **pull**.

Por ejemplo:

| Estado | Control WIP de ejemplo |
| --- | ---: |
| Análisis | 2 |
| Desarrollo | 3 |
| Revisión | 2 |
| Pruebas | 2 |

Si Desarrollo ya contiene tres elementos y ese es el límite acordado, no debería iniciarse un cuarto trabajo simplemente para mantener ocupado a otro miembro.

El equipo debería concentrarse primero en ayudar a completar o desbloquear los elementos existentes.

Esta práctica ayuda a reducir la multitarea y favorece la colaboración y el flujo.

### 3.6. Buenas prácticas para aplicar Kanban

#### Visualizar el flujo real

El tablero debe representar cómo trabaja realmente el equipo y no cómo desearía que funcionara el proceso.

#### Controlar el WIP

No se debe iniciar trabajo indefinidamente.

**Importancia:** comenzar demasiadas tareas puede producir acumulaciones, cambios constantes de contexto y tiempos de entrega mayores.

#### Priorizar terminar antes que comenzar

Cuando una etapa alcanza su capacidad, el equipo debería concentrarse en hacer avanzar el trabajo existente antes de incorporar nuevos elementos.

#### Gestionar activamente los bloqueos

Los elementos bloqueados deben hacerse visibles y atenderse oportunamente.

La guía actual de Kanban considera el desbloqueo del trabajo como parte de la gestión activa del flujo.

#### Definir políticas explícitas

El equipo debe conocer las condiciones necesarias para mover un elemento de una etapa a otra.

Por ejemplo:

> Una funcionalidad solo puede pasar de Desarrollo a Revisión cuando el código compila correctamente y se han ejecutado las pruebas acordadas.

#### Revisar métricas de flujo

La guía actual establece cuatro métricas mínimas:

* **WIP (Work in Progress):** cantidad de elementos que han comenzado pero todavía no han finalizado.
* **Throughput:** cantidad de elementos terminados durante un período determinado.
* **Work Item Age:** tiempo que lleva en progreso un elemento que todavía no ha finalizado.
* **Cycle Time:** tiempo transcurrido desde que un elemento comienza hasta que finaliza.

Estas métricas permiten comprender el comportamiento del flujo, identificar riesgos y tomar decisiones basadas en datos. No deben utilizarse como mecanismo para comparar individualmente el rendimiento de los desarrolladores.

#### Lead Time

El **Lead Time** permite analizar cuánto tiempo transcurre desde un punto definido de solicitud o compromiso hasta que el trabajo es entregado.

Es una métrica ampliamente utilizada dentro del ecosistema Kanban para estudiar la experiencia de entrega desde una perspectiva más amplia.

Debe distinguirse de las cuatro métricas mínimas establecidas por *The Kanban Guide*, que son:

* WIP;
* Throughput;
* Work Item Age;
* Cycle Time.

#### Feedback loops

Kanban promueve mecanismos frecuentes de retroalimentación que permiten revisar el comportamiento del sistema y tomar decisiones de mejora.

Estos ciclos de retroalimentación pueden utilizar información relacionada con:

* métricas de flujo;
* elementos bloqueados;
* envejecimiento del trabajo;
* necesidades de los interesados;
* cambios en la demanda;
* problemas repetitivos del proceso.

La información obtenida puede utilizarse para modificar políticas, ajustar la Definition of Workflow y mejorar progresivamente el sistema de trabajo.

Los feedback loops forman parte especialmente de las prácticas generales descritas por Kanban University, mientras que *The Kanban Guide* resume Kanban mediante tres prácticas fundamentales.

#### Mejorar continuamente el flujo

Cuando aparecen problemas repetitivos, el equipo debe analizar sus causas y modificar su Definition of Workflow cuando sea conveniente.

### 3.7. Malas prácticas comunes en Kanban

Entre los errores frecuentes se encuentran:

* crear un tablero y no mantenerlo actualizado;
* utilizar Kanban únicamente como una lista de tareas;
* permitir una cantidad ilimitada de trabajo en progreso;
* seguir iniciando tareas aunque existan cuellos de botella;
* utilizar siempre "Por hacer-En proceso-Hecho" aunque no represente el proceso real;
* no establecer políticas para mover los elementos;
* ignorar elementos bloqueados o envejecidos;
* cambiar prioridades constantemente sin analizar el impacto sobre el flujo;
* medir únicamente cuántas tareas realiza cada persona;
* establecer controles WIP y posteriormente ignorarlos.

Kanban debe permitir comprender y mejorar el **sistema de trabajo completo**, no simplemente controlar la actividad individual de cada integrante.

### 3.8. Ejemplo de Kanban en un proyecto de software

Supóngase un equipo responsable del mantenimiento de una plataforma web donde diariamente llegan:

* errores reportados por usuarios;
* pequeñas mejoras;
* solicitudes técnicas;
* actualizaciones.

El equipo utiliza este flujo:

**Pendiente -> Análisis -> Desarrollo -> Revisión -> Pruebas -> Terminado**

Se establece un control WIP de tres elementos para Desarrollo y dos para Revisión.

En determinado momento:

* Desarrollo tiene tres tareas;
* Revisión tiene dos;
* una tarea está bloqueada.

Aunque existen nuevas solicitudes pendientes, el equipo no inicia inmediatamente más desarrollo. Primero analiza cómo liberar capacidad ayudando con revisiones o resolviendo el bloqueo.

Una vez termina uno de los elementos, aparece capacidad y puede seleccionarse nuevo trabajo.

De esta forma, el equipo evita acumular demasiadas tareas abiertas y concentra sus esfuerzos en mantener el flujo.

---

## 4. Scrum vs. Kanban

Scrum y Kanban pueden utilizarse para favorecer formas de trabajo ágiles, pero resuelven la gestión del trabajo de manera diferente.

| Aspecto | Scrum | Kanban |
| --- | --- | --- |
| Naturaleza | Framework ligero | Estrategia para optimizar el flujo |
| Organización del trabajo | Sprints | Flujo continuo |
| Iteraciones | Sprints de un mes o menos | No prescribe Sprints |
| Responsabilidades prescritas | Product Owner, Scrum Master y Developers | No establece estas responsabilidades de Scrum |
| Eventos prescritos | Sprint, Planning, Daily, Review y Retrospective | No prescribe los eventos de Scrum |
| Gestión del trabajo | Product Backlog y Sprint Backlog | Definition of Workflow y elementos que fluyen por el sistema |
| Planificación | Utiliza Product Backlog y Sprint Planning para establecer el objetivo y el trabajo inicial del Sprint | El trabajo se selecciona según capacidad disponible, prioridades y políticas definidas en el workflow |
| Control del trabajo en progreso | Gestionado dentro del Sprint según su planificación y objetivo | Control explícito del WIP |
| Cambios durante el trabajo | El Sprint Backlog puede adaptarse durante el Sprint siempre que no se comprometa el Sprint Goal | Los cambios se gestionan continuamente de acuerdo con la capacidad, las prioridades y las políticas del sistema |
| Métricas | Scrum no prescribe Story Points ni una métrica específica obligatoria | Utiliza como métricas mínimas WIP, Throughput, Work Item Age y Cycle Time |
| Mejora | Inspección y adaptación, especialmente mediante los eventos Scrum | Mejora continua del flujo |
| Entrega | Pueden generarse y entregarse múltiples Increments durante el Sprint cuando cumplen la Definition of Done | Los elementos pueden finalizar y entregarse conforme avanzan por el flujo, de acuerdo con las políticas del sistema |

Scrum define explícitamente su estructura, responsabilidades, eventos y artefactos. Kanban es más flexible respecto a la estructura organizacional y se concentra en visualizar, gestionar y mejorar el flujo.

### 4.1. Similitudes

Ambos enfoques buscan:

* hacer visible el trabajo;
* facilitar la adaptación;
* mejorar continuamente;
* fomentar la colaboración;
* reducir problemas en la entrega;
* generar valor para los interesados;
* utilizar información del trabajo realizado para tomar mejores decisiones.

### 4.2. ¿Cuándo utilizar Scrum?

Scrum resulta especialmente conveniente cuando:

* se desarrolla o evoluciona un producto complejo;
* existe un equipo responsable del producto;
* resulta útil trabajar mediante objetivos de corto plazo;
* se requiere inspeccionar periódicamente resultados con los interesados;
* el equipo puede trabajar alrededor de objetivos de corto plazo mediante Sprints y utilizar ciclos frecuentes de inspección y adaptación ante la complejidad y los cambios.

### 4.3. ¿Cuándo utilizar Kanban?

Kanban suele resultar conveniente cuando:

* existe un flujo continuo de solicitudes;
* las prioridades pueden cambiar con frecuencia;
* se realizan tareas de soporte, mantenimiento o evolución continua;
* existen problemas de acumulación o cuellos de botella;
* se necesita mejorar la predictibilidad del flujo;
* la organización desea evolucionar su proceso actual sin reemplazarlo completamente.

Estas condiciones son criterios prácticos de selección y no reglas universales.

Además, **Scrum y Kanban no son necesariamente excluyentes**. Kanban puede complementar otras técnicas o formas de trabajo, siempre que se respeten los elementos esenciales del sistema Kanban.

### 4.4. Mitos frecuentes

#### Scrum

* Scrum es un framework, no una metodología prescrita paso a paso.
* El Scrum Master no es el jefe del Scrum Team.
* El Product Owner no asigna tareas a los Developers.
* La Daily Scrum no es un reporte de estado para un jefe.
* El Sprint Backlog puede cambiar durante el Sprint.
* Story Points no son obligatorios en Scrum.
* Scrum no obliga a utilizar historias de usuario.
* Scrum no obliga a utilizar Planning Poker.
* Scrum no obliga a utilizar burndown charts.
* La Sprint Review no es únicamente una demostración del producto.
* No es necesario esperar a la Sprint Review para entregar un Increment.
* El Product Backlog Refinement no es uno de los cinco eventos formales de Scrum.

#### Kanban

* Kanban no requiere Sprints.
* Kanban no requiere Product Owner.
* Kanban no requiere Scrum Master.
* Kanban no prescribe los eventos de Scrum.
* Kanban no obliga a que cada columna tenga un límite WIP numérico.
* Tener un tablero visual no significa automáticamente estar aplicando Kanban.
* Las métricas de flujo deben analizar el sistema y no utilizarse para controlar individualmente a los desarrolladores.

---

## 5. Buenas prácticas recomendadas

Las siguientes prácticas resumen acciones aplicables a equipos de desarrollo de software.

| Enfoque | Qué se debe hacer | Por qué es una buena práctica | Beneficio | Problema que ayuda a evitar |
| --- | --- | --- | --- | --- |
| Scrum | Definir un Sprint Goal claro | Proporciona dirección al Sprint | Mayor enfoque | Sprint convertido en lista de tareas |
| Scrum | Mantener ordenado el Product Backlog | Permite concentrarse en el trabajo que aporta mayor valor | Mejor planificación | Trabajar sin prioridades |
| Scrum | Refinar el backlog continuamente | Mejora el entendimiento del trabajo futuro | Menor incertidumbre | Historias demasiado grandes o ambiguas |
| Scrum | Respetar la Definition of Done | Establece un criterio compartido de calidad | Incrementos confiables | Trabajo incompleto considerado terminado |
| Scrum | Orientar la Daily hacia el Sprint Goal | Permite adaptar el trabajo diario | Mejor coordinación | Reuniones de reporte al jefe |
| Scrum | Obtener retroalimentación en la Sprint Review | Permite adaptar el producto | Mayor alineación con las necesidades reales | Desarrollar durante semanas sin validar |
| Scrum | Aplicar mejoras de la retrospectiva | Convierte la reflexión en acciones | Mejora continua | Repetición de los mismos problemas |
| Kanban | Visualizar el flujo real | Permite comprender el estado del sistema | Transparencia | Trabajo oculto |
| Kanban | Controlar el WIP | Evita iniciar trabajo por encima de la capacidad | Mayor enfoque y mejor flujo | Exceso de tareas abiertas |
| Kanban | Gestionar los bloqueos | Los bloqueos afectan directamente el flujo | Menores retrasos | Tareas detenidas durante demasiado tiempo |
| Kanban | Establecer políticas explícitas | Todos conocen cómo debe avanzar el trabajo | Consistencia | Decisiones arbitrarias |
| Kanban | Observar el envejecimiento del trabajo | Ayuda a detectar tareas con riesgo de demora | Mayor predictibilidad | Elementos olvidados |
| Kanban | Analizar métricas de flujo | Permite tomar decisiones utilizando datos | Mejora objetiva del proceso | Decisiones basadas únicamente en percepción |
| Kanban | Mejorar continuamente el workflow | El sistema debe evolucionar cuando aparecen problemas | Mayor eficiencia y predictibilidad | Mantener procesos ineficientes |

Una práctica común a ambos enfoques es evitar utilizar las herramientas únicamente para aparentar agilidad. Tener un tablero, realizar reuniones diarias o utilizar historias de usuario no garantiza por sí mismo una forma de trabajo ágil. Lo relevante es que estas prácticas ayuden al equipo a entregar valor, obtener retroalimentación y mejorar su manera de trabajar.

---

## 6. Conclusiones

Scrum y Kanban proporcionan formas diferentes pero compatibles de mejorar la gestión del desarrollo de software.

Scrum establece una estructura claramente definida basada en un Scrum Team, responsabilidades específicas, eventos, artefactos y objetivos. Su enfoque iterativo e incremental permite trabajar sobre problemas complejos, obtener retroalimentación y adaptar tanto el producto como la forma de trabajo.

Kanban concentra su atención principalmente en el flujo de valor. La visualización del trabajo, el control del WIP, la gestión activa de los elementos y el análisis de métricas permiten identificar cuellos de botella, reducir acumulaciones y conseguir una entrega más predecible.

La principal enseñanza es que aplicar Scrum o Kanban correctamente requiere algo más que utilizar determinadas reuniones o herramientas. El equipo debe comprender **por qué utiliza cada práctica y qué problema pretende resolver con ella**.

Una implementación adecuada puede mejorar la organización del trabajo, la colaboración entre integrantes, la detección temprana de dificultades, la capacidad de adaptación y la calidad de las entregas de software.

Por último, Scrum y Kanban no deben entenderse como enfoques idénticos ni como soluciones automáticas para cualquier proyecto. La decisión sobre cuál utilizar debe considerar las características del producto, el tipo de trabajo, la frecuencia de los cambios y las necesidades reales del equipo y de la organización.

---

## Referencias

* Beck, K. et al. (2001). *Manifesto for Agile Software Development*. Agile Manifesto.
* Schwaber, K. y Sutherland, J. (2020). *The Scrum Guide: The Definitive Guide to Scrum*. Scrum Guides.
* *The Kanban Guide*. Versión mayo de 2025. Kanban Guides.
* Kanban University. *The Official Guide to The Kanban Method*.
