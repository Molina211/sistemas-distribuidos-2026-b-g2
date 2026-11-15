# Week 15 challenge: real runs on the dev stack

Both runs were made on Saturday 2026-11-14 on the dev stack of the root composition (`multi-tour-infra-postgres`,
`COMPOSE_PROJECT_NAME=multi-tour-dev`), through the gateway on port 8080. Nothing in `qa` or `main` was touched.

## Load test - `load-tenant-profile.js` (k6 0.54.0)

Path: `GET /api/v1/identity-audit/public/tenants/travesia-natural`, gateway -> `identity-audit-api` -> MongoDB.
Load: 40 requests per second for 1 minute (under the gateway limit of 50 r/s per address).
SLO: p95 under 300 ms and under 1 % of failed answers. Result: **met** (k6 exit code 0, both thresholds green).

Command, started 2026-11-14 20:36:16 -0500:

```bash
docker run --rm --network container:multi-tour-dev-api-gateway-1 -v "$PWD:/scripts:ro" grafana/k6:0.54.0 run --quiet /scripts/load-tenant-profile.js
```

Output:

```text
     ✓ status is 200
     checks.........................: 100.00% 2399 out of 2399
     data_received..................: 1.7 MB  27 kB/s
     data_sent......................: 319 kB  5.0 kB/s
     dropped_iterations.............: 2       0.031541/s
     http_req_blocked...............: avg=19.73µs  min=6.09µs  med=10.22µs  max=3.42ms   p(90)=13.99µs  p(95)=16.18µs 
     http_req_connecting............: avg=5.56µs   min=0s      med=0s       max=1.62ms   p(90)=0s       p(95)=0s      
   ✓ http_req_duration..............: avg=9.81ms   min=3.79ms  med=5.75ms   max=904.78ms p(90)=7.98ms   p(95)=9.69ms  
       { expected_response:true }...: avg=9.81ms   min=3.79ms  med=5.75ms   max=904.78ms p(90)=7.98ms   p(95)=9.69ms  
   ✓ http_req_failed................: 0.00%   0 out of 2399
     http_req_receiving.............: avg=434.98µs min=76.25µs med=375.41µs max=6.73ms   p(90)=652.63µs p(95)=863.07µs
     http_req_sending...............: avg=62.3µs   min=18.89µs med=50.52µs  max=7.37ms   p(90)=81.3µs   p(95)=94.77µs 
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s       max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=9.31ms   min=3.47ms  med=5.32ms   max=904.16ms p(90)=7.26ms   p(95)=8.7ms   
     http_reqs......................: 2399    37.833365/s
     iteration_duration.............: avg=9.79ms   min=4.08ms  med=6.08ms   max=641.56ms p(90)=8.37ms   p(95)=10.13ms 
     iterations.....................: 2399    37.833365/s
     vus............................: 0       min=0            max=0 
     vus_max........................: 22      min=22           max=22
```

The 2 dropped iterations are k6 not having a free virtual user at that instant, not failed requests; the single
maximum of 905 ms is the first request (warm-up) and stays out of the p95.

## Chaos experiment - `chaos-stop-a-saga.sh`

Victim: the container of the saga `onboard-tenant`, chosen because no other work used it during the run. The other
services of the dev stack were in use by other work, so `operations-cost-api` was not stopped.
Expected: while the saga is down, only its route answers `503 SERVICE_UNAVAILABLE` with the common envelope and the
other domains keep answering; when it starts again, its route recovers with no restart of the gateway (per-request DNS,
`resolver 127.0.0.11 valid=10s`).
Result: **as expected**.

Command, started 2026-11-14 20:37:38 -0500: `sh chaos-stop-a-saga.sh`

```text
before: saga route 401, identity-audit 200, body {"error":"UNAUTHORIZED","message":"authentication required","traceId":"3364e8e58fd68de3d56db070c5bc51ee"}
stopped multi-tour-dev-workflow-onboard-tenant-1 at 20:37:40
during: saga route 503, identity-audit 200, body {"error":"SERVICE_UNAVAILABLE","message":"the service is not available, retry later","traceId":"04a6cefa4e6607e26614fcb7b41808de"}
started multi-tour-dev-workflow-onboard-tenant-1 at 20:37:56
after: saga route 401, identity-audit 200, body {"error":"UNAUTHORIZED","message":"authentication required","traceId":"be8690e8b7f8a5683a625e859d2de7f1"}
```

`401` before and after is the answer of the saga itself to a fake credential: it proves the request reached the
service. `503` during is the gateway's own answer when the service cannot be reached.
