# <abbr>-front (Angular shell)

The container of the interface, built with Native Federation. It owns everything
that must exist **exactly once**.

Angular 21 (zoneless, signals) · Native Federation 21 · Node 22 LTS or 24.

```
src/app/core/http/api.interceptor.ts         gateway URL, token, X-Correlation-Id, timeout, one error shape
src/app/core/http/api-error.ts               the error every portal receives, with the message decided HERE
src/app/core/auth/session.service.ts         the session
src/app/core/auth/auth.guard.ts              sends to sign-in and back (returnUrl)
src/app/core/auth/sign-in.component.ts       DEVELOPMENT sign-in: paste a token from <abbr>-infra/scripts/dev-token.sh
src/app/core/errors/remote-unavailable.component.ts   what a portal that cannot load is replaced with
src/app/layout/                              navigation, home, 404
src/app/app.routes.ts                        mounts each portal with loadRemoteModule, and a ** route
src/app/app.config.ts                        THE ONLY provideHttpClient() of the application
public/federation.manifest.json              where each portal is served from
federation.config.js
```

## How the single client is enforced

Portals are loaded as routes **inside the shell's injector**. When a portal injects
`HttpClient`, it receives the shell's instance — with the shell's interceptor:
gateway URL, token, correlation id, timeout, and every failure turned into an
`ApiError` whose `userMessage` is already decided. So the rule for a portal is
simple and checkable: **a portal never calls `provideHttpClient()`**. If it does,
it creates a second client without the interceptor, and its requests silently go
out unauthenticated.

## A portal that is down

`loadRemoteModule(...).catch(...)` replaces the portal's routes with
`RemoteUnavailableComponent`: that area says the portal is not available, and the
rest of the application keeps working. `main.ts` also bootstraps when
`initFederation` cannot reach a portal's manifest, so the shell starts — a few
seconds later — even with a portal down.

```bash
npm ci
npm start                # http://localhost:4200
npm run build
```

`package-lock.json` is versioned: `npm ci` installs exactly what was tested.
