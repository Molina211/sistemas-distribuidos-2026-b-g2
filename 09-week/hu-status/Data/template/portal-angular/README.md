# <abbr>-orders-portal (Angular remote)

The screens of the **orders** domain, exposed to `<abbr>-front` as `./routes`.

```
src/app/orders/orders.routes.ts                    exposed: the domain's routes
src/app/orders/pages/orders-page.component.ts      list with its four states, filter, pages
src/app/orders/components/order-form.component.ts  reactive form: labels, validators, server errors, Idempotency-Key
src/app/orders/data/orders-api.service.ts          typed calls — through the SHELL's HttpClient
src/app/orders/model/order.ts                      the contract types, mirroring <abbr>-orders-api (camelCase, Page<T>)
src/app/orders/model/money.ts                      text <-> minor units, without floating point
src/app/shell-contract.ts                          the error shape the shell guarantees
src/app/app.config.ts                              standalone runs only — NO provideHttpClient()
federation.config.js
```

**This portal never calls `provideHttpClient()`.** Mounted in the shell, it runs
inside the shell's injector and receives the shell's client with its interceptor.
`OrdersApiService` requests `/api/v1/orders` — a relative path. It does not know
the gateway URL, never touches the token and never builds an error message: it
shows the `userMessage` the shell decided.

## What every screen of a portal does

| Rule | Where |
|---|---|
| A view that loads data has **four designed states**: loading, error (with retry), empty, data | `orders-page.component.ts` |
| A newer request replaces an older one | `orders-page.component.ts` |
| Reactive forms; every field has a `<label>`; errors tied with `aria-describedby` | `order-form.component.ts` |
| The server's field errors are shown next to each field | `order-form.component.ts` |
| The submit button is disabled while the request is pending | `order-form.component.ts` |
| A creation carries an `Idempotency-Key`, reused while the same data is retried | `order-form.component.ts` |
| Money is converted from text, never multiplied as a float | `money.ts` |

```bash
npm ci
npm start                # http://localhost:4201 — run it through the shell
```
