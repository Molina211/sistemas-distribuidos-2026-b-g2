# ADR-C3-02: Disaster Recovery Across Two Availability Zones

## Status

Proposed

## Context

Multitour currently runs on a single host using Docker Compose, with daily backups retained for 30 days. The project requirements establish an RPO of no more than 24 hours, an RTO of no more than 8 hours, and monthly availability of at least 95%. PostgreSQL contains critical reservation and payment information in the `commercial_reservation` schema. The architecture needs to improve recovery capabilities while keeping infrastructure costs under control.

## Decision

We choose Disaster Recovery Pattern 4, using two availability zones within the same region. This approach provides a target RPO of 0 and an RTO ranging from seconds to minutes, while maintaining relatively low infrastructure costs. The deciding factor is the balance between recovery performance and operational cost.

## Alternatives considered

* Pattern 5 (Two Regions): Rejected because it introduces significantly higher infrastructure costs and DNS cache delays can increase recovery time, although it provides protection against regional disasters.
* Hot Standby: Rejected because maintaining continuously running standby servers increases infrastructure expenses, even when those resources are not actively serving traffic.

## Consequences

* Positive: The architecture improves availability and recovery speed, with a target RPO of 0 and an RTO of seconds to minutes in the event of an availability zone failure.
* Negative: The solution does not protect against a complete regional disaster. The application must also handle availability zone changes, which introduces additional implementation and testing complexity.
