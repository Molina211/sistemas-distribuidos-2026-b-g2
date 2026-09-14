# Sesion 1 - Healthcheck real para el arranque orquestado del MVP1

## Objetivo de la sesion

Traer todo el sistema arriba con un solo `docker compose up`, pero esta vez con el
arranque condicionado a que cada servicio este realmente listo, no solo a que su
contenedor haya iniciado: red compartida, healthchecks que condicionan el arranque,
configuracion por variables de entorno, y datos en volumen.

De los cuatro requisitos, tres ya estaban resueltos desde la Semana 05, Sesion 1
(ver `Containerization-MVP1-Travesia-Natural.md`): red compartida (`multitour-net`),
configuracion via entorno, y datos de Postgres en volumen (`multitour-postgres-data`).
Faltaba el cuarto: el `backend` no tenia healthcheck propio, asi que `frontend`
dependia de el con un `depends_on` simple (espera a que el contenedor arranque, no a
que la aplicacion este lista para recibir trafico).

## Que se implemento

Corresponde a `specs/028-actuator-health-endpoint/` del repo Backend.

- **`pom.xml`**: dependencia `spring-boot-starter-actuator`.
- **`application.properties`**: `management.endpoints.web.exposure.include=health,info`
  y `management.endpoint.health.show-details=never` (el endpoint cae bajo
  `anyRequest().permitAll()` de la spec 007, asi que sin esto cualquiera sin
  autenticar veria el detalle de componentes, ej. conectividad a Postgres).
- **`docker-compose.yml`**: healthcheck para el servicio `backend` contra
  `/actuator/health` (`wget --spider`, `start_period: 30s` para darle tiempo a
  Spring Boot de arrancar antes de la primera revision).
- **`docker-compose.yml`**: `frontend.depends_on.backend` cambia de la forma simple
  a `condition: service_healthy`.

## Evidencia de ejecucion

Verificado el 2026-09-13 contra el stack completo (`docker compose up -d --build`),
evidencia detallada en `PLAN-VERIFICACION.md` (Backend), seccion 028:

```
$ docker compose up -d --build
...
Container multitour-postgres   Healthy
Container multitour-backend    Healthy
Container multitour-frontend   Started

$ curl -s http://localhost:8081/actuator/health
{"groups":["liveness","readiness"],"status":"UP"}

$ curl -s http://localhost:8081/actuator/info
{}

$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/
200
```

`multitour-backend` solo queda `healthy` cuando `/actuator/health` responde `UP`
contra Postgres real (no mockeado); `multitour-frontend` arranca despues de que
`backend` pasa a `healthy`, no antes. `/actuator/health` no expone detalle de
componentes (`show-details=never`). Los 6 criterios de aceptacion de la spec 028
quedan cumplidos.

## Nota de reconciliacion

Esta sesion no reemplaza el `docker-compose.yml` de la Semana 05 (Containerizacion):
lo extiende agregando el healthcheck que faltaba sobre la misma red, el mismo
volumen y la misma configuracion por entorno ya existentes.
