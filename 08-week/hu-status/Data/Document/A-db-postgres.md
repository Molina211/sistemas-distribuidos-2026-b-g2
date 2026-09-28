# Anexo A — Repositorio de base de datos relacional (PostgreSQL + Liquibase o Flyway)

Repositorio: `<abbr>-<dominio>-db` · Norma: numeral 5.2

Un repositorio `-db` es **dueño de toda la estructura de datos de un dominio**: el
motor, el esquema, los datos semilla, los roles y las migraciones. El `-api` del
dominio la consume; nunca la versiona. Si mañana otro equipo necesitara levantar su
dominio en una máquina limpia, este repositorio —y solo este— debe bastarle.

Todos los anexos usan el mismo dominio de ejemplo, **pedidos** (`orders`). Al construir
el suyo, `orders` se reemplaza por su dominio y `abbr` por la abreviatura del equipo.

La herramienta de migraciones la elige el equipo **por dominio**: Liquibase o Flyway
(numeral 4.2.2), y la registra en un ADR. Las dos producen el mismo esquema, con las
mismas familias de carpetas y las mismas reglas; cambia cómo se nombran, se ordenan y
se revierten las migraciones. Este anexo describe primero Liquibase y después, en la
sección **Con Flyway**, todo lo que cambia.

## Estructura esperada — con Liquibase

```
<abbr>-orders-db/
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── 01_ddl/
│   ├── 00_extensions/
│   │   ├── 001_enable_pgcrypto.sql
│   │   └── changelog.yaml
│   ├── 01_schemas/
│   │   ├── 001_create_schema_orders.sql
│   │   └── changelog.yaml
│   ├── 02_types/
│   │   └── changelog.yaml
│   ├── 03_tables/
│   │   ├── 001_create_customer_order.sql
│   │   ├── 002_create_order_item.sql
│   │   ├── 003_create_sales_channel.sql
│   │   ├── 004_create_idempotency_key.sql
│   │   └── changelog.yaml
│   ├── 04_alter/
│   │   ├── 001_add_foreign_keys.sql
│   │   └── changelog.yaml
│   ├── 05_views/
│   │   └── changelog.yaml
│   ├── 06_materialized_views/
│   │   └── changelog.yaml
│   ├── 07_functions/
│   │   └── changelog.yaml
│   ├── 08_procedures/
│   │   └── changelog.yaml
│   ├── 09_triggers/
│   │   └── changelog.yaml
│   ├── 10_indexes/
│   │   ├── 001_create_indexes.sql
│   │   └── changelog.yaml
│   └── changelog.yaml
├── 02_dml/
│   ├── 00_inserts/
│   │   ├── 001_seed_sales_channel.sql
│   │   └── changelog.yaml
│   ├── 01_updates/
│   │   └── changelog.yaml
│   ├── 02_deletes/
│   │   └── changelog.yaml
│   ├── 03_upserts/
│   │   └── changelog.yaml
│   ├── 04_patches/
│   │   └── changelog.yaml
│   └── changelog.yaml
├── 03_dcl/
│   ├── 00_roles/
│   │   ├── 001_create_roles.sql
│   │   └── changelog.yaml
│   ├── 01_grants/
│   │   ├── 001_grants.sql
│   │   └── changelog.yaml
│   ├── 02_policies/
│   │   └── changelog.yaml
│   └── changelog.yaml
├── 04_tcl/
│   ├── 00_transaction_blocks/
│   │   └── changelog.yaml
│   ├── 01_manual_recoveries/
│   │   └── changelog.yaml
│   ├── 02_release_tags/
│   │   └── changelog.yaml
│   └── changelog.yaml
├── 05_rollbacks/
│   ├── 01_ddl/
│   │   ├── 00_extensions/
│   │   │   └── 001_enable_pgcrypto.rollback.sql
│   │   ├── 01_schemas/
│   │   │   └── 001_create_schema_orders.rollback.sql
│   │   ├── 03_tables/
│   │   │   ├── 001_create_customer_order.rollback.sql
│   │   │   ├── 002_create_order_item.rollback.sql
│   │   │   ├── 003_create_sales_channel.rollback.sql
│   │   │   └── 004_create_idempotency_key.rollback.sql
│   │   ├── 04_alter/
│   │   │   └── 001_add_foreign_keys.rollback.sql
│   │   └── 10_indexes/
│   │       └── 001_create_indexes.rollback.sql
│   ├── 02_dml/
│   │   └── 00_inserts/
│   │       └── 001_seed_sales_channel.rollback.sql
│   └── 03_dcl/
│       ├── 00_roles/
│       │   └── 001_create_roles.rollback.sql
│       └── 01_grants/
│           └── 001_grants.rollback.sql
├── changelog/
│   └── changelog-master.yaml
├── deploy/
│   └── compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Qué va en cada parte

| Parte | Contenido | Por qué está separado |
|---|---|---|
| `changelog/changelog-master.yaml` | El **único** punto de entrada: incluye, en orden, el `changelog.yaml` de cada familia | Liquibase ejecuta lo que el maestro incluye y en ese orden; un archivo fuera del maestro no existe |
| `01_ddl/` | **DDL** — definición: extensiones, esquema, tipos, tablas, llaves, vistas, funciones, procedimientos, disparadores, índices | Es la forma de los datos; cambia poco y con cuidado |
| `02_dml/` | **DML** — manipulación: semillas, actualizaciones, borrados, *upserts*, parches de datos | Mover datos no es lo mismo que cambiar su forma, y se revierte distinto |
| `03_dcl/` | **DCL** — control de acceso: roles, permisos, políticas por fila | Quién puede qué es una decisión de seguridad; debe poder auditarse aparte |
| `04_tcl/` | **TCL** — control de transacciones: bloques transaccionales, recuperaciones manuales, etiquetas de versión | Lo que se ejecuta excepcionalmente queda registrado, no en un chat |
| `05_rollbacks/` | Espejo de las carpetas anteriores: un archivo `.rollback.sql` por cada `.sql` que revierte con archivo | La reversión se lee junto a lo que revierte |
| `deploy/compose.yml` | La instancia **propia** del dominio (imagen con versión fija, volumen, chequeo de salud) y el ejecutor de migraciones | Cada dominio tiene su instancia; `-infra` la compone, no la define (Anexo G) |
| `.env.example` | Nombres de las variables (base, usuario, contraseña), sin valores reales | Documenta qué se necesita sin versionar un secreto |
| `.github/workflows/db-ci.yml` | La verificación de reconstrucción (ver más abajo) | Sin ella, el esquema solo "funciona" donde se parchó a mano |

La numeración de las subcarpetas de `01_ddl/` **es el orden de ejecución**: las
extensiones van antes que lo que las usa, y las tablas antes que sus llaves. Cada
subcarpeta tiene su `changelog.yaml`, aunque esté vacía: así el orden queda fijo desde
el primer día y agregar una vista o un disparador no obliga a reordenar nada.

## Cómo se escribe un *changeset* (Liquibase)

Cada cambio es un archivo SQL y una entrada en el `changelog.yaml` de su carpeta:

```yaml
databaseChangeLog:
  - changeSet:
      id: ddl-tables-001
      author: <usuario-github>
      labels: "HU-12,ddl,tables"
      comment: "customer_order: the order aggregate. No foreign keys here, they go in 04_alter."
      changes:
        - sqlFile:
            path: 001_create_customer_order.sql
            relativeToChangelogFile: true
            splitStatements: false
      rollback:
        - sqlFile:
            path: ../../05_rollbacks/01_ddl/03_tables/001_create_customer_order.rollback.sql
            relativeToChangelogFile: true
            splitStatements: false
```

- Las rutas son relativas al `changelog.yaml` que las incluye (`relativeToChangelogFile: true`); `splitStatements: false` deja pasar bloques `DO $$ … $$` completos.
- El `id` es único y **nunca se reutiliza**. El `author` es el usuario de GitHub de quien lo escribió. `labels` enlaza la historia de usuario.
- El `comment` va **siempre entre comillas**: un comentario con `: ` adentro rompe el YAML, porque para YAML eso separa clave y valor.
- Toda sentencia DDL declara su reversión. Si revertir no es posible (por ejemplo, un borrado de datos), el *changeset* lo dice en su `comment` y el equipo lo registra en un ADR.

## El esquema del ejemplo

| Tabla | Qué guarda | Restricciones que la protegen |
|---|---|---|
| `customer_order` | el agregado: cliente, canal, estado, total, fechas | `chk_customer_order_status` (tres estados), total `> 0`, `customer_id` **sin** llave foránea (el cliente vive en otro dominio) |
| `order_item` | las líneas del pedido | `sku` de 1 a 40 caracteres, cantidad `> 0`, precio `>= 0` |
| `sales_channel` | tabla de consulta de canales de venta | llave natural `code` |
| `idempotency_key` | la clave con que se creó cada pedido | clave de 8 a 128 caracteres, llave hacia el pedido |

`idempotency_key` es la mitad de base de datos de la **creación idempotente** del
numeral 5.3.8: el servicio escribe el pedido y su clave en **una sola transacción**; si
la clave ya existe, revierte y devuelve el pedido original (Anexo C). Todo dominio que
cree recursos por HTTP necesita su tabla equivalente.

## Reglas

1. **Nombres en singular y `snake_case`**: una tabla se nombra por una de sus filas (`customer_order`, `order_item`). `order` es palabra reservada de SQL; por eso el agregado se llama `customer_order`.
2. **Las tablas se crean sin llaves foráneas** en `03_tables`; las llaves se agregan en `04_alter`, cada una declarando qué ocurre al borrar (`ON DELETE CASCADE` o `RESTRICT`). Así el orden de creación de las tablas deja de importar.
3. **Toda columna de llave foránea tiene su índice.** Sin él, borrar o actualizar el padre recorre la tabla hija completa. Todo otro índice nombra la consulta que atiende: `idx_<tabla>_<columnas>`.
4. **Restricciones e índices con nombre explícito** (`fk_`, `chk_`, `idx_`). Un nombre generado por el motor no se puede referenciar en una migración posterior.
5. **Los conjuntos cerrados de valores son `CHECK` o tabla de consulta, nunca `ENUM`.** Agregar un valor a un `ENUM` es un `ALTER TYPE` difícil de revertir y que no se puede usar en la misma transacción; un `CHECK` se cambia como cualquier restricción.
6. **El texto es `text`**, con `CHECK` de longitud cuando el negocio fija un límite: el límite queda visible como regla.
7. **El dinero se guarda en unidades menores** (`total_cents bigint`), nunca en punto flotante.
8. **Una llave hacia otro dominio no es una llave foránea.** `customer_id` apunta a un dato que vive en otra base: se guarda el identificador y se verifica por contrato, no con una restricción del motor.
9. **El esquema lleva el nombre del dominio** (`orders`). Nada vive en `public`.
10. **Los roles no llevan contraseña.** Son `NOLOGIN` y cargan permisos (`orders_reader`, `orders_writer`); los usuarios con credenciales los crea la infraestructura desde secretos.
11. **Las semillas son idempotentes**: `INSERT … ON CONFLICT … DO UPDATE` contra una llave única, nunca un `INSERT` a secas.
12. **Todo *changeset* declara su reversión**, y **un *changeset* aplicado nunca se edita**: editarlo cambia su *checksum* y rompe todos los ambientes que ya lo aplicaron. Una corrección es un *changeset* nuevo.
13. **Los cambios incompatibles van en dos releases** (expandir y contraer): primero se agrega lo nuevo y el servicio escribe en ambos; lo viejo se retira cuando ninguna versión desplegada lo usa.
14. **Un índice sobre una tabla con datos se crea `CONCURRENTLY`**, en una migración propia: en Liquibase, con `runInTransaction: false`; en Flyway, con el bloqueo transaccional desactivado (sección **Con Flyway**). Dentro de una transacción, crear el índice bloquea las escrituras mientras dura.
15. **Esta base pertenece a un dominio.** Ningún otro servicio se conecta a ella.

## La verificación de reconstrucción (Liquibase)

`.github/workflows/db-ci.yml` corre en cada Pull Request, con un PostgreSQL **vacío**
como servicio del flujo, y ejecuta en orden:

| Paso | Qué demuestra |
|---|---|
| `update` | el esquema completo se construye desde cero |
| `update` otra vez, que debe aplicar **cero** *changesets* | las migraciones son incrementales |
| `rollback-count 999` | todo se revierte, en orden inverso, y deja la base vacía |
| `update` de nuevo | después de revertir, se vuelve a construir |

La imagen oficial `liquibase/liquibase` se puede ejecutar con `docker run` dentro del
flujo, apuntando al servicio PostgreSQL.

## Con Flyway

Si el dominio usa Flyway, se conservan las familias DDL/DML/DCL/TCL, el espejo de
reversión, el esquema y **todas** las reglas anteriores. Cambian cuatro cosas: el
punto de entrada, el nombre de cada archivo, el orden y la reversión.

### Estructura esperada — con Flyway

```
<abbr>-orders-db/   (Flyway)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── 01_ddl/
│   ├── 00_extensions/
│   │   └── V001__enable_pgcrypto.sql
│   ├── 01_schemas/
│   │   └── V002__create_schema_orders.sql
│   ├── 02_types/
│   │   └── .gitkeep
│   ├── 03_tables/
│   │   ├── V003__create_customer_order.sql
│   │   ├── V004__create_order_item.sql
│   │   ├── V005__create_sales_channel.sql
│   │   └── V006__create_idempotency_key.sql
│   ├── 04_alter/
│   │   └── V007__add_foreign_keys.sql
│   ├── 05_views/
│   │   └── .gitkeep
│   ├── 06_materialized_views/
│   │   └── .gitkeep
│   ├── 07_functions/
│   │   └── .gitkeep
│   ├── 08_procedures/
│   │   └── .gitkeep
│   ├── 09_triggers/
│   │   └── .gitkeep
│   └── 10_indexes/
│       └── V008__create_indexes.sql
├── 02_dml/
│   ├── 00_inserts/
│   │   └── V009__seed_sales_channel.sql
│   ├── 01_updates/
│   │   └── .gitkeep
│   ├── 02_deletes/
│   │   └── .gitkeep
│   ├── 03_upserts/
│   │   └── .gitkeep
│   └── 04_patches/
│       └── .gitkeep
├── 03_dcl/
│   ├── 00_roles/
│   │   └── V010__create_roles.sql
│   ├── 01_grants/
│   │   └── V011__grants.sql
│   └── 02_policies/
│       └── .gitkeep
├── 04_tcl/
│   ├── 00_transaction_blocks/
│   │   └── .gitkeep
│   ├── 01_manual_recoveries/
│   │   └── .gitkeep
│   └── 02_release_tags/
│       └── .gitkeep
├── 05_rollbacks/
│   ├── 01_ddl/
│   │   ├── 00_extensions/
│   │   │   └── U001__enable_pgcrypto.sql
│   │   ├── 01_schemas/
│   │   │   └── U002__create_schema_orders.sql
│   │   ├── 03_tables/
│   │   │   ├── U003__create_customer_order.sql
│   │   │   ├── U004__create_order_item.sql
│   │   │   ├── U005__create_sales_channel.sql
│   │   │   └── U006__create_idempotency_key.sql
│   │   ├── 04_alter/
│   │   │   └── U007__add_foreign_keys.sql
│   │   └── 10_indexes/
│   │       └── U008__create_indexes.sql
│   ├── 02_dml/
│   │   └── 00_inserts/
│   │       └── U009__seed_sales_channel.sql
│   └── 03_dcl/
│       ├── 00_roles/
│       │   └── U010__create_roles.sql
│       └── 01_grants/
│           └── U011__grants.sql
├── deploy/
│   └── compose.yml
├── .env.example
├── .gitignore
├── flyway.toml
└── README.md
```

### Qué cambia respecto a Liquibase

| | Liquibase | Flyway |
|---|---|---|
| Punto de entrada | `changelog/changelog-master.yaml` y un `changelog.yaml` por carpeta | `flyway.toml`: la lista de carpetas (`locations`) que Flyway recorre; no hay changelogs |
| Nombre de una migración | libre, declarado en el changelog | `V<versión>__<descripción>.sql`: `V003__create_customer_order.sql` (dos guiones bajos) |
| **Qué decide el orden** | el orden de los `include` | **el número de versión**, una sola secuencia para todo el repositorio, **no la carpeta** |
| Una migración nueva | un *changeset* nuevo en su carpeta | el **siguiente** número, en la carpeta de su familia: si la última es `V011`, la nueva es `V012`, sea DDL, DML o DCL |
| Reversión | declarada en el *changeset*; `rollback-count` la ejecuta | Flyway Community **no revierte**. La reversión de `V<n>` es `05_rollbacks/…/U<n>__<descripción>.sql`, un script que se aplica con `psql` en orden inverso |
| Vistas y funciones | *changeset* con `runOnChange` | migración repetible `R__<descripción>.sql`: se vuelve a aplicar cuando cambia su contenido |
| Historial | tabla `databasechangelog` | tabla `flyway_schema_history` |

### El orden lo da la versión

Flyway junta los archivos de todas las carpetas y los ejecuta por número. Una versión
**menor** que la última aplicada se rechaza:

```
Detected resolved migration not applied to database: 005.1.
```

Por eso las familias organizan los archivos, pero no los ordenan. Numere con ceros a la
izquierda (`V001`, `V002`…) para que el explorador de archivos muestre el mismo orden
que Flyway.

### `flyway.toml`

```toml
[flyway]
locations = ["filesystem:01_ddl", "filesystem:02_dml", "filesystem:03_dcl", "filesystem:04_tcl"]
validateMigrationNaming = true   # un nombre mal escrito falla en lugar de ignorarse
cleanDisabled = true             # clean borra todo: este flujo nunca lo necesita

[flyway.postgresql.transactional]
lock = false                     # ver "CREATE INDEX CONCURRENTLY", más abajo
```

La conexión **no** va en este archivo: Flyway la lee de las variables de entorno
`FLYWAY_URL`, `FLYWAY_USER` y `FLYWAY_PASSWORD`, que el ejecutor recibe del `.env`
(Anexo G).

### La reversión con Flyway

La edición Community de Flyway no ejecuta reversiones. La norma igual exige que cada
migración tenga la suya (numeral 5.2), así que:

1. Cada `V<n>__<descripción>.sql` tiene su `U<n>__<descripción>.sql` en `05_rollbacks/`, en la misma ruta de familia. `U` es la convención de Flyway para una reversión.
2. `05_rollbacks/` **no** está en `locations`: Flyway nunca la ejecuta por su cuenta.
3. La verificación de reconstrucción aplica los `U` con `psql`, del número más alto al más bajo, y comprueba que la base quedó vacía.
4. En `qa` y `main`, corregir una migración ya aplicada es una migración **nueva hacia adelante**; los `U` documentan y prueban cómo deshacer, y se usan solo en una recuperación decidida y registrada (`04_tcl/01_manual_recoveries/`).

### La verificación de reconstrucción con Flyway

| Paso | Qué demuestra |
|---|---|
| `migrate` | el esquema completo se construye desde cero |
| `migrate` otra vez, que debe responder `No migration necessary` | las migraciones son incrementales |
| los `U` con `psql`, del más alto al más bajo, y comprobar que el esquema ya no existe | cada reversión funciona y el conjunto deja la base vacía |
| borrar `flyway_schema_history` y `migrate` de nuevo | después de revertir, se vuelve a construir |

La imagen oficial `flyway/flyway`, con versión fija, se ejecuta con `docker run` dentro
del flujo, igual que la de Liquibase.

### Dos trampas de Flyway con PostgreSQL

| Trampa | Qué pasa | Qué hacer |
|---|---|---|
| `CREATE INDEX CONCURRENTLY` | con el bloqueo transaccional por defecto, Flyway mantiene abierta su propia transacción y el índice la espera: la migración **se queda colgada** y nunca se registra | `[flyway.postgresql.transactional] lock = false` en `flyway.toml`; Flyway ejecuta esa migración fuera de transacción y la registra |
| Una vista `R__` que cambia sus columnas | `CREATE OR REPLACE VIEW` solo permite **agregar** columnas al final; reordenar o renombrar falla | la migración repetible empieza con `DROP VIEW IF EXISTS …;` y luego `CREATE VIEW` |

## Decisiones que el equipo registra en un ADR

- **Liquibase o Flyway** para este dominio (numeral 4.2.2), con el criterio dominante y el costo aceptado (numeral 4.2.3). Por ejemplo: Flyway es más simple de leer —un archivo, un número—; Liquibase revierte por sí mismo y comparte herramienta con un dominio en MongoDB.
- Qué datos del dominio son **propios** y cuáles son **referencias** a otros dominios.
- Qué migraciones no pueden revertirse, y por qué.
- Si el dominio usa PostgreSQL o MongoDB (numeral 4.2.2), con el criterio dominante y el costo aceptado (numeral 4.2.3).

## Errores frecuentes

| Error | Consecuencia |
|---|---|
| Crear el esquema a mano "para probar" y migrar después | El siguiente ambiente no se puede construir |
| Editar una migración ya aplicada | *Checksum* inválido en todos los ambientes que la aplicaron: Liquibase y Flyway la rechazan |
| En Flyway, darle a una migración nueva un número menor que la última aplicada | Flyway se niega a migrar |
| En Flyway, un nombre sin los dos guiones bajos (`V12_create_x.sql`) | Flyway se niega a migrar |
| Llave foránea hacia la base de otro dominio | Dos dominios acoplados por el motor: ya no se despliegan por separado |
| `comment` sin comillas | El changelog no carga |
| Semilla con `INSERT` simple | La segunda ejecución falla o duplica |

## Cómo se verifica

- [ ] Migrar desde una base vacía (`update` o `migrate`) aplica todo el esquema sin intervención manual
- [ ] Una segunda ejecución no aplica nada
- [ ] La herramienta elegida está registrada en un ADR
- [ ] La reversión total —`rollback` en Liquibase, los scripts `U` en Flyway— deja la base vacía y el esquema se vuelve a aplicar
- [ ] `db-ci.yml` está en verde en `develop`
- [ ] Ninguna tabla vive en `public`; ninguna llave apunta a otro dominio
- [ ] Toda llave foránea tiene índice; ningún conjunto cerrado es `ENUM`
- [ ] El repositorio no contiene contraseñas: solo `.env.example` con nombres
