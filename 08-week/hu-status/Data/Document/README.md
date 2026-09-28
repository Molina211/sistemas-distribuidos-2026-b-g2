# Sistemas Distribuidos 2026-B — Norma de repositorios

Esta carpeta contiene la norma con la que se construye y se evalúa el proyecto, y un
anexo por cada tipo de repositorio. **Lea primero la norma completa**: cada regla tiene
un identificador y una forma de verificación, y la evaluación de cada corte se hace
ejecutando esas verificaciones sobre sus repositorios.

```
README.md                                              este archivo
1-Norma-Repositorios-Sistemas-Distribuidos-2026B.pdf   la norma: topología, contenido de cada repositorio, ramas, rúbrica
2-anexos/                                              un anexo por tipo de repositorio (A a I)
```

## Cómo usar esta carpeta

1. **Lea la norma.** Como mínimo, antes de crear código: la topología (numeral 4), el contenido mínimo de cada repositorio (numeral 5), el régimen de ramas (numeral 6), las reglas de oro (numeral 12), la rúbrica (numeral 16) y la autoevaluación (numeral 17).
2. **Calcule sus repositorios** con la fórmula del numeral 4.1 y decida, en ADR, el lenguaje de cada servicio, el motor de cada dominio y el *framework* de interfaz (numerales 4.2.2 y 4.2.3).
3. **Antes de construir cada repositorio, lea su anexo.**

| Anexo | Repositorio | Qué encontrará |
|---|---|---|
| [A](2-anexos/A-db-postgres.md) | `<abbr>-<dominio>-db` con PostgreSQL | familias DDL/DML/DCL/TCL, Liquibase o Flyway (qué cambia entre ellas), reglas del esquema, verificación de reconstrucción |
| [B](2-anexos/B-db-mongo.md) | `<abbr>-<dominio>-db` con MongoDB | qué cambia respecto al relacional, el validador, embeber o referenciar |
| [C](2-anexos/C-api-hexagonal.md) | `<abbr>-<dominio>-api` | arquitectura hexagonal en Go, Java, Python y C#; el contrato público completo; token, idempotencia, correlación, límites; las comprobaciones de contrato |
| [D](2-anexos/D-worker.md) | `<abbr>-worker` | trabajos programados seguros de repetir, lotes acotados, reintentos |
| [E](2-anexos/E-workflow-saga.md) | `<abbr>-workflow` | sagas: pasos, compensaciones, idempotencia, estado |
| [F](2-anexos/F-api-gateway.md) | `<abbr>-api-gateway` | enrutamiento, filtro de credencial, CORS, correlación, errores propios |
| [G](2-anexos/G-infra.md) | `<abbr>-infra` | composición del sistema, migraciones con Liquibase o Flyway, identidad de desarrollo, arranque |
| [H](2-anexos/H-front.md) | `<abbr>-front` y `<abbr>-<dominio>-portal` | contenedor y portales en React o Angular, cliente único, estados de cada vista |
| [I](2-anexos/I-github-common.md) | todos los de código | `CODEOWNERS`, formato de Pull Request, integración continua, seguimiento en el tablero |

## Cómo leer un anexo

Cada anexo tiene las mismas partes:

| Parte | Para qué sirve |
|---|---|
| **Estructura esperada** | el árbol de carpetas y archivos del repositorio, con el dominio de ejemplo `orders` |
| **Qué va en cada parte** | la responsabilidad de cada carpeta y archivo: sin esto, el árbol es solo una lista de nombres |
| **Reglas** | lo que no se negocia, y por qué |
| **Decisiones que el equipo registra en un ADR** | lo que la norma deja a su criterio |
| **Cómo se verifica** | la lista con la que se revisará el repositorio: úsela antes de cada entrega |

Todos los anexos usan el mismo dominio de ejemplo, **pedidos** (`orders`). Al
construir su proyecto, `orders` se reemplaza por sus dominios y `abbr` por la
abreviatura de su equipo. Lo que no se reemplaza es la forma: las capas, la regla de
dependencia, el contrato común y los límites explícitos. Si dos servicios de su
sistema están escritos en lenguajes distintos, un consumidor no debe poder notarlo.

## Antes de cada corte

Recorra la autoevaluación del numeral 17 de la norma y la sección **Cómo se verifica**
del anexo de cada repositorio. Lo que no esté publicado en el repositorio remoto no se
evalúa (numeral 1.4).

---

Jesús Ariel González Bonilla — Docente Ingeniería de Sistemas, CORHUILA
