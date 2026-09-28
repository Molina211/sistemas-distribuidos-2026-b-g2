# Anexo H — Interfaz: contenedor (`-front`) y portales de dominio

Repositorios: `<abbr>-front` (contenedor) y `<abbr>-<dominio>-portal` (uno por dominio con canal web) · Norma: numerales 5.4 y 5.5

El *framework* lo elige el equipo entre **React 19** y **Angular 21**, y lo registra
en un ADR. Ambos corren sobre Node 22 LTS o 24: son las versiones con soporte vigente
durante todo el periodo.

## La regla que implementan los dos

**El cliente HTTP y la sesión viven en el contenedor, y solo ahí.** Un portal los
consume; nunca crea los suyos. Un portal con cliente propio duplica la parte más
difícil de la interfaz —y es el error más común al partir una aplicación única en
micro-frontends: el sistema termina con tantos manejos de token como pantallas.

Cada *framework* lo hace cumplir a su manera:

| | React | Angular |
|---|---|---|
| Mecanismo | El contenedor **expone** su cliente por Module Federation (`@module-federation/vite`); el portal lo importa como `shell/apiClient` | El portal se monta **dentro del inyector del contenedor** (Native Federation) y recibe su `HttpClient` con su interceptor |
| Regla verificable | El portal nunca llama a `fetch` contra el API | El portal nunca llama a `provideHttpClient()` |
| Si se incumple | Un segundo cliente que no conoce el gateway ni el token | Un segundo cliente **sin interceptor**: sus peticiones salen sin autenticar, en silencio |

## Lo que el cliente del contenedor hace por todos

| Aspecto | Comportamiento |
|---|---|
| Destino | Solo el contenedor conoce la URL del gateway; los portales piden `/api/v1/...` |
| Credencial | Adjunta el token; un `401` cierra la sesión |
| Correlación | Un `X-Correlation-Id` nuevo por petición |
| Tiempo máximo | 10 s por petición; al vencer, un error `TIMEOUT` (estado 0) |
| Errores | Todo fallo llega al portal con la forma del sobre común más el mensaje para la persona, decidido **en un solo lugar** según el estado, con la referencia `traceId` |

## Lo que cada pantalla de un portal hace

| Regla | Por qué |
|---|---|
| Cuatro estados diseñados: cargando, error con reintento, vacío y datos | Una vista que solo diseña el caso feliz queda en blanco ante el primero de los otros tres |
| Una petición más nueva reemplaza a la anterior | Sin eso, una respuesta lenta pisa a una rápida y la tabla muestra el filtro equivocado |
| Etiqueta por campo y error junto al campo (`aria-describedby`) | Un lector de pantalla necesita saber qué campo falló, no solo que algo falló |
| Botón deshabilitado mientras hay un envío pendiente | El doble clic es el origen más común de registros duplicados |
| `Idempotency-Key` por intención, reutilizada al reintentar | Si la red corta la respuesta, reintentar no crea un segundo pedido |
| Dinero desde el texto, nunca multiplicando un flotante | `0.07 * 100` es `7.000000000000001` |

## Un portal caído no tumba la aplicación

| | React | Angular |
|---|---|---|
| Carga de remotos | `shareStrategy: 'loaded-first'`: un portal se descarga al abrir su ruta | `loadRemoteModule` por ruta; `main.ts` arranca aunque un manifiesto no responda |
| Contención | `RemoteBoundary`: un *error boundary* por portal | `.catch()` que reemplaza las rutas del portal por un aviso |
| Resultado | Solo el área del portal avisa que no está disponible; el resto sigue funcionando | igual |

Con la estrategia por defecto de Module Federation (`version-first`), el contenedor
descarga **todos** los remotos al arrancar para comparar versiones: un portal caído
deja la aplicación entera en blanco.

## Estructura esperada

### React — contenedor
```
<abbr>-front/   (React)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   ├── Dockerfile
│   └── nginx.conf
├── src/
│   ├── app/
│   │   └── App.tsx
│   ├── core/
│   │   ├── auth/
│   │   │   ├── RequireAuth.tsx
│   │   │   └── session.ts
│   │   ├── errors/
│   │   │   └── RemoteBoundary.tsx
│   │   └── http/
│   │       └── apiClient.ts
│   ├── layout/
│   │   └── Shell.tsx
│   ├── remotes/
│   │   ├── registry.ts
│   │   └── remotes.d.ts
│   ├── shared/
│   │   └── ui/
│   │       └── Button.tsx
│   ├── main.tsx
│   └── vite-env.d.ts
├── .env.example
├── .gitignore
├── index.html
├── package-lock.json
├── package.json
├── README.md
├── tsconfig.json
├── tsconfig.tsbuildinfo
└── vite.config.ts
```

### React — portal de dominio
```
<abbr>-orders-portal/   (React)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   ├── Dockerfile
│   └── nginx.conf
├── src/
│   ├── orders/
│   │   ├── api/
│   │   │   └── ordersApi.ts
│   │   ├── components/
│   │   │   └── OrderForm.tsx
│   │   ├── model/
│   │   │   ├── money.ts
│   │   │   └── order.ts
│   │   └── pages/
│   │       └── OrdersPage.tsx
│   ├── shell.d.ts
│   └── vite-env.d.ts
├── .env.example
├── .gitignore
├── index.html
├── package-lock.json
├── package.json
├── README.md
├── tsconfig.json
├── tsconfig.tsbuildinfo
└── vite.config.ts
```

### Angular — contenedor (Native Federation, sin zone.js)
```
<abbr>-front/   (Angular)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   ├── Dockerfile
│   └── nginx.conf
├── public/
│   └── federation.manifest.json
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── auth/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   ├── session.service.ts
│   │   │   │   └── sign-in.component.ts
│   │   │   ├── errors/
│   │   │   │   └── remote-unavailable.component.ts
│   │   │   └── http/
│   │   │       ├── api-error.ts
│   │   │       └── api.interceptor.ts
│   │   ├── layout/
│   │   │   ├── home.component.ts
│   │   │   ├── not-found.component.ts
│   │   │   └── shell-layout.component.ts
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── bootstrap.ts
│   ├── index.html
│   └── main.ts
├── .env.example
├── .gitignore
├── angular.json
├── federation.config.js
├── package-lock.json
├── package.json
├── README.md
├── tsconfig.app.json
├── tsconfig.federation.json
└── tsconfig.json
```

### Angular — portal de dominio
```
<abbr>-orders-portal/   (Angular)
├── .github/  ·  común a todo repositorio de código (Anexo I)
├── deploy/
│   ├── compose.yml
│   ├── Dockerfile
│   └── nginx.conf
├── public/
│   └── .gitkeep
├── src/
│   ├── app/
│   │   ├── orders/
│   │   │   ├── components/
│   │   │   │   └── order-form.component.ts
│   │   │   ├── data/
│   │   │   │   └── orders-api.service.ts
│   │   │   ├── model/
│   │   │   │   ├── money.ts
│   │   │   │   └── order.ts
│   │   │   ├── pages/
│   │   │   │   └── orders-page.component.ts
│   │   │   └── orders.routes.ts
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── shell-contract.ts
│   ├── bootstrap.ts
│   ├── index.html
│   └── main.ts
├── .env.example
├── .gitignore
├── angular.json
├── federation.config.js
├── package-lock.json
├── package.json
├── README.md
├── tsconfig.app.json
├── tsconfig.federation.json
└── tsconfig.json
```

## Qué va en cada parte

| Parte | React | Angular |
|---|---|---|
| El cliente único (gateway, token, correlación, tiempo máximo, errores) | `src/core/http/apiClient.ts` | `src/app/core/http/api.interceptor.ts` + `api-error.ts` |
| La sesión | `src/core/auth/session.ts` | `src/app/core/auth/session.service.ts` |
| El guardián de rutas y el inicio de sesión de desarrollo | `src/core/auth/RequireAuth.tsx` | `auth.guard.ts` + `sign-in.component.ts` |
| El aislamiento de cada portal | `src/core/errors/RemoteBoundary.tsx` | `remote-unavailable.component.ts` |
| Qué portales existen y dónde se montan | `src/remotes/registry.ts` + `vite.config.ts` | `app.routes.ts` + `public/federation.manifest.json` |
| Navegación, 404 | `src/layout/`, `src/app/App.tsx` | `src/app/layout/` |
| En el portal: tipos del contrato | `src/<dominio>/model/` | `src/app/<dominio>/model/` |
| En el portal: llamadas tipadas | `src/<dominio>/api/` (usa `shell/apiClient`) | `src/app/<dominio>/data/` (usa el `HttpClient` del contenedor) |
| En el portal: páginas y componentes | `src/<dominio>/pages/`, `components/` | `src/app/<dominio>/pages/`, `components/` |
| Despliegue | `deploy/Dockerfile` (Node 22 + `npm ci`) y `deploy/nginx.conf` | igual |

## Reglas

1. **Un portal por dominio**, nombrado por canal: `-portal` para web.
2. **Los tipos del portal reflejan el contrato del API** con los mismos nombres de campo,
   incluido `Page<T>` con `data` y `meta`.
3. **El portal pide rutas relativas** (`/api/v1/orders`); solo el contenedor sabe dónde
   está el gateway.
4. **`remoteEntry` y el manifiesto de federación nunca se cachean**: deciden qué versión
   de cada portal carga el navegador.
5. **El inicio de sesión de desarrollo nunca llega a `main`.** Se reemplaza por el portal
   del dominio de identidad.
6. **`package-lock.json` se versiona** y los Dockerfile usan `npm ci`: se instala
   exactamente lo que se probó.

## Cómo se verifica

- [ ] Los proyectos compilan con TypeScript estricto y `npm ci` instala desde el `package-lock.json` versionado
- [ ] Sin sesión, la ruta protegida lleva al inicio de sesión y regresa a la ruta pedida
- [ ] El portal carga dentro del contenedor y **no** contiene la URL del gateway ni maneja el token (se revisa sobre el código compilado)
- [ ] Cada petición lleva `Authorization` y un `X-Correlation-Id`; un `401` cierra la sesión
- [ ] Cada vista muestra sus cuatro estados: cargando, error con reintento, vacío y datos
- [ ] El formulario muestra el error junto a cada campo, del cliente y del servidor
- [ ] El botón se deshabilita mientras hay un envío pendiente
- [ ] La creación envía `Idempotency-Key` y la reutiliza en el reintento
- [ ] Un monto escrito como `0.07` llega al API como `7`
- [ ] Una ruta inexistente muestra la página 404
- [ ] Con un portal apagado, el contenedor arranca y solo el área de ese portal avisa que no está disponible
