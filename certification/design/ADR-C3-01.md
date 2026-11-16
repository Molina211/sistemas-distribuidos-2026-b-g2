# ADR-C3-01: Compute Architecture with ECS and Auto Scaling

## Status

Proposed

## Context

Multitour currently runs its services using Docker Compose, with one image per service. The expected workload includes 10 tenants, with a reference load of 34 concurrent users within one tenant. The architecture needs to support changes in demand without requiring manual deployment of additional servers. The team also needs a solution that can be operated with its current knowledge and preserve the existing deployment scripts.

## Decision

We choose Huawei Cloud ECS with Auto Scaling to manage compute capacity. Scaling will be triggered when maximum CPU usage exceeds a defined threshold for 5 minutes across 3 consecutive evaluations, adding 2 instances with a cooldown period of 300 seconds. The main deciding factor is operational simplicity while maintaining compatibility with the existing `scripts/up.sh` deployment process.

## Alternatives considered

* CCE (Cloud Container Engine): Rejected because Kubernetes introduces additional operational complexity, and the team currently has no experience managing this platform.
* Fixed ECS instances without Auto Scaling: Rejected because capacity would need to be adjusted manually when the number of concurrent users increases, making the solution less flexible.

## Consequences

* Positive: Compute capacity can increase automatically according to workload, reducing manual intervention and preserving the existing deployment approach.
* Negative: Additional ECS instances increase infrastructure costs during periods of high demand. CPU-based scaling may also take time to react to sudden traffic increases.
