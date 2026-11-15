#!/bin/sh
# Week 15 challenge: stop one service of the dev stack and check that only its route degrades (norm 5.6.3).
# The saga onboard-tenant is the victim because no other work uses it while this runs. It is always started again.
set -u
BASE=${BASE:-http://localhost:8080}
VICTIM=${VICTIM:-multi-tour-dev-workflow-onboard-tenant-1}
trap 'docker start "$VICTIM" >/dev/null' EXIT

probe() { # the gateway only checks that a credential exists; a fake one reaches the service, which answers 401
  saga=$(curl -s -o /tmp/saga.json -w '%{http_code}' -H 'Authorization: Bearer chaos' "$BASE/api/v1/sagas/onboard-tenant/x")
  other=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/api/v1/identity-audit/public/tenants/travesia-natural")
  echo "$1: saga route $saga, identity-audit $other, body $(cat /tmp/saga.json)"
}

probe before
docker stop "$VICTIM" >/dev/null && echo "stopped $VICTIM at $(date '+%H:%M:%S')"
sleep 12 # the gateway resolver caches a name for 10 s (valid=10s)
probe during
docker start "$VICTIM" >/dev/null && echo "started $VICTIM at $(date '+%H:%M:%S')"
for i in $(seq 1 30); do
  [ "$(docker inspect -f '{{.State.Health.Status}}' "$VICTIM")" = healthy ] && break
  sleep 2
done
sleep 12
probe after
