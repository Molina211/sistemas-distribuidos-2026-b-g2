# Anexo B — Repositorio de base de datos documental (MongoDB + Liquibase)

Repositorio: `<abbr>-<dominio>-db` · Norma: numerales 5.2 y 5.2.4

Con MongoDB la herramienta es **Liquibase** con su extensión (numeral 4.2.2): la opción de
Flyway del Anexo A aplica solo a PostgreSQL. Misma disciplina que PostgreSQL (Anexo A) —un equipo que usa los
dos motores trabaja de una sola forma—, pero **varios conceptos cambian**. Copiar la
estructura relacional sobre Mongo produce un repositorio que aparenta orden y no
protege nada: la norma no lo acepta (numeral 5.2.4).

## Estructura esperada

```
<abbr>-orders-db/
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── 01_ddl/
│   ├── 00_collections/
│   │   └── changelog.yaml
│   ├── 01_validators/
│   │   └── changelog.yaml
│   ├── 02_indexes/
│   │   └── changelog.yaml
│   ├── 03_views/
│   │   └── changelog.yaml
│   └── changelog.yaml
├── 02_dml/
│   ├── 00_inserts/
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
│   │   └── changelog.yaml
│   └── changelog.yaml
├── changelog/
│   └── changelog-master.yaml
├── deploy/
│   ├── compose.yml
│   └── liquibase.Dockerfile
├── .env.example
├── .gitignore
└── README.md
```

## Qué va en cada parte

| Parte | Contenido |
|---|---|
| `changelog/changelog-master.yaml` | Único punto de entrada; incluye cada familia en orden |
| `01_ddl/00_collections/` | La creación de cada colección **con su validador** `$jsonSchema`, su `validationLevel` y su `validationAction` |
| `01_ddl/01_validators/` | Cambios posteriores a un validador (`collMod`), nunca editando el *changeset* original |
| `01_ddl/02_indexes/` | Índices, incluidos los **únicos**: en Mongo son estructura, no optimización |
| `01_ddl/03_views/` | Vistas de agregación, si el dominio las necesita |
| `02_dml/` | Semillas y parches de datos, idempotentes |
| `03_dcl/` | Roles del dominio (`createRole` por `runCommand`), sin contraseña |
| `deploy/compose.yml` | MongoDB con versión fija como **replica set de un nodo**, su chequeo de salud y el ejecutor de migraciones |
| `deploy/liquibase.Dockerfile` | La imagen de Liquibase **con la extensión de MongoDB instalada** |
| `.env.example` | Nombre de la base y variables necesarias, sin valores reales |

## Qué cambia respecto a PostgreSQL

| Concepto | PostgreSQL | MongoDB |
|---|---|---|
| Estructura | Tablas y columnas que el motor exige | Un **validador `$jsonSchema`** asociado a la colección. Sin `additionalProperties: false`, se acepta cualquier campo adicional |
| Llaves foráneas | `04_alter` | **No existen.** La integridad entre colecciones es responsabilidad del servicio. La carpeta `04_alter` desaparece |
| Relaciones | Tabla aparte + llave | **Embeber** (las líneas del pedido viven *dentro* del pedido) o **referenciar** por identificador |
| Unicidad | `PRIMARY KEY`, `UNIQUE` | **Índices únicos** |
| Roles | `CREATE ROLE` / `GRANT` | `createRole` mediante `runCommand` |
| Transacciones | Cada migración corre en una | Las transacciones multi-documento exigen **replica set**, y las migraciones no se envuelven igual: **cada *changeset* debe ser idempotente por sí mismo** |
| Reversión | Archivo SQL espejo | Declarada **en línea** con la operación inversa (`dropCollection`, `dropIndex`) |

Por eso no hay `04_tcl/` ni `05_rollbacks/`.

## El validador del ejemplo

```json
{
  "bsonType": "object",
  "required": ["_id", "customerId", "channel", "status", "totalCents", "items", "createdAt"],
  "additionalProperties": false,
  "properties": {
    "channel":    { "enum": ["WEB", "MOBILE", "STORE"] },
    "status":     { "enum": ["PENDING", "CONFIRMED", "CANCELLED"] },
    "totalCents": { "bsonType": "long", "minimum": 1 },
    "items":      { "bsonType": "array", "minItems": 1, "maxItems": 100, "items": { "…": "…" } }
  }
}
```

Tres decisiones que el validador vuelve obligatorias:

- **`additionalProperties: false`**: un campo con un error de tipeo se rechaza en lugar de guardarse en silencio.
- **Tipos numéricos explícitos**: el dinero es `long` en unidades menores (o `decimal128` si el dominio necesita fracciones); nunca `double`.
- **Todo arreglo embebido tiene `maxItems`.** Un documento de MongoDB no puede pasar de 16 MB: un arreglo sin límite crece hasta que una escritura falla en producción.

## Reglas

1. **La estructura vive en el validador**, con `validationLevel: strict` y `validationAction: error`. `warn` solo registra: nunca en `main`.
2. **Los índices se nombran** (`idx_<colección>_<campos>`) y responden a una consulta real del servicio.
3. **Cada *changeset* es idempotente por sí mismo**: si falla a mitad, volver a ejecutarlo no puede dejar la base peor.
4. **Toda operación declara su inversa** en línea.
5. **Los roles no llevan contraseña**; los usuarios los crea la infraestructura.
6. **MongoDB corre como replica set de un nodo** también en desarrollo: es lo que habilita transacciones y *change streams*, y así desarrollo se comporta como producción.
7. **Esta base pertenece a un dominio.** Ningún otro servicio se conecta a ella.

## Instalar la extensión: dos paquetes, y los nombres engañan

Liquibase necesita **dos** paquetes para hablar con Mongo:

| Paquete | Qué es |
|---|---|
| `liquibase-mongodb` | la extensión: le enseña a Liquibase el protocolo `mongodb://` |
| `mongodb` | el *driver* Java que la extensión necesita para conectarse |

Instalar solo `mongodb` parece funcionar y falla con *"Driver class was not specified
and could not be determined from the url"*. Se instalan ambos:
`lpm add liquibase-mongodb mongodb --global`.

## Decisiones que el equipo registra en un ADR

- **Embeber o referenciar.** Se embebe lo que se lee junto y no tiene vida propia (las líneas del pedido). Se referencia lo que tiene su propio ciclo de vida (el cliente).
- **El límite de cada arreglo embebido** y qué pasa cuando el negocio lo supera.
- **`validationLevel`**: `strict` valida toda escritura; `moderate` permite actualizar documentos que ya eran inválidos. Se empieza en `strict`.

## Cómo se verifica

- [ ] Las migraciones se aplican desde una base vacía; una segunda ejecución aplica cero
- [ ] El validador **rechaza** un documento con un valor fuera de su `enum`, con un campo no declarado, sin arreglo requerido o con un arreglo por encima de `maxItems`
- [ ] El validador **acepta** un documento válido
- [ ] Los índices existen con sus nombres explícitos
- [ ] La reversión total deja la base sin colecciones ni roles, y se vuelve a aplicar
- [ ] MongoDB corre como replica set, y el repositorio no contiene credenciales
