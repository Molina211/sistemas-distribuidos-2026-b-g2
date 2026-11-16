# ADR-C3-01: Stateless Compute Scaling with ECS, ELB and Auto Scaling

## Status

Proposed

## Context

Multitour currently runs its services using Docker Compose, with one image per service. The expected workload includes 10 tenants, with a reference load of 34 concurrent users within one tenant. The architecture needs to improve availability and handle workload variations without duplicating stateful components. Only stateless services, such as the API Gateway and APIs that validate JWTs without maintaining sessions, can be horizontally scaled; PostgreSQL and MongoDB must remain independently managed database services.

## Decision

We choose Huawei Cloud ECS with Auto Scaling and ELB, distributing instances across two availability zones. Scaling will be triggered when maximum CPU usage exceeds 70% for three consecutive 5-minute evaluations, adding 2 instances per event, with a maximum of 6 instances and a cooldown period of 300 seconds. Scheduled or periodic scaling will also be configured for predictable traffic peaks, while health checks will allow failed ECS instances to be replaced automatically.

The existing `scripts/up.sh` will remain available for initial deployment but will not be executed by cloned instances. Database services will be managed separately using RDS for PostgreSQL and an appropriate managed MongoDB service.

## Alternatives considered

* CCE (Cloud Container Engine): Rejected because Kubernetes introduces additional operational complexity, and the team currently has no experience managing this platform.
* Fixed ECS instances without Auto Scaling: Rejected because failed instances would require manual replacement and additional capacity would need to be provisioned manually, reducing availability and flexibility during workload changes.

## Consequences

* Positive: ELB distributes traffic across instances in two availability zones, while Auto Scaling increases capacity and replaces unhealthy ECS instances. Scheduled scaling also helps prepare for predictable traffic peaks.
* Negative: Additional ECS instances increase infrastructure costs, although the maximum capacity of 6 instances limits this expense. CPU-based scaling may take approximately 15 minutes to react to sustained demand, so scheduled policies are necessary for predictable peaks. Managed database services also introduce additional operating costs.
