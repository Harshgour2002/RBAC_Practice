import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    authz: {
      executor: 'constant-arrival-rate',
      rate: 10000,
      timeUnit: '1s',
      duration: '30s',
      preAllocatedVUs: 300,
      maxVUs: 2000
    }
  }
};

export default function () {
  const res = http.post('http://localhost:8080/authorize', JSON.stringify({ userId: 1, permission: 'READ' }), {
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${__ENV.ACCESS_TOKEN}` }
  });
  check(res, { 'is 200': (r) => r.status === 200 });
  sleep(0.1);
}
