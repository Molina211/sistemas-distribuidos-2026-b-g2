// Week 15 challenge: load test of one real path, gateway -> identity-audit-api -> MongoDB, against the SLO below.
// 40 requests per second stays under the gateway limit of 50 r/s per address (zone "general"), so a 429 is a failure.
import http from 'k6/http';
import { check } from 'k6';

const BASE = __ENV.BASE || 'http://localhost:8080';
const TENANT = __ENV.TENANT || 'travesia-natural';

export const options = {
  scenarios: {
    profile: { executor: 'constant-arrival-rate', rate: 40, timeUnit: '1s', duration: '1m', preAllocatedVUs: 20, maxVUs: 40 },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],   // SLO: under 1 % of failed answers
    http_req_duration: ['p(95)<300'], // SLO: p95 under 300 ms
  },
};

export default function () {
  const res = http.get(`${BASE}/api/v1/identity-audit/public/tenants/${TENANT}`);
  check(res, { 'status is 200': (r) => r.status === 200 });
}
