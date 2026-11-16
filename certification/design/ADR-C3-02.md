# ADR-C3-02: Multi-AZ Disaster Recovery Architecture

## Status

Proposed

## Context

Multitour currently runs on a single host using Docker Compose, with daily backups retained for 30 days. This configuration already meets the PDR requirements of RPO ≤ 24 hours, RTO ≤ 8 hours and monthly availability of at least 95%. However, PostgreSQL contains critical reservation and payment information in the `commercial_reservation` schema, while MongoDB stores identity-audit information. The architecture should improve recovery objectives beyond the minimum requirements without introducing the high costs of a second region.

## Decision

We choose Disaster Recovery Pattern 4, using two availability zones within the same Huawei Cloud region, with both zones actively serving traffic. The target recovery objectives are RPO = 0 minutes and RTO ≤ 15 minutes for an availability zone failure, using synchronous replication for PostgreSQL through RDS Multi-AZ and an appropriate high-availability replica-set configuration for MongoDB. Cost is the dominant decision criterion because this pattern provides stronger recovery capabilities without requiring a second region or continuously idle standby infrastructure.

## Alternatives considered

* Current daily backup with cold standby: Rejected because, although it already satisfies the PDR requirements, its recovery objectives are limited by the daily backup interval and manual restoration process. Pattern 4 provides a more ambitious RPO and RTO.
* Pattern 5 (Two Regions): Rejected because it introduces significantly higher infrastructure and operational costs. DNS cache delays can also increase recovery time, although this pattern offers protection against regional disasters.
* Hot Standby: Rejected because maintaining continuously running standby infrastructure increases costs. In Pattern 4, both availability zones actively serve traffic, avoiding the expense of completely idle application servers.

## Consequences

* Positive: The architecture improves availability and recovery speed through multi-zone deployment and database replication. It targets zero data loss during an availability zone failure and recovery within 15 minutes.
* Negative: Infrastructure and database replication costs increase compared with the current single-host deployment. The solution does not protect against a complete regional disaster, and the application must handle zone failover. PostgreSQL and MongoDB replication configurations must be validated to ensure they actually support the proposed RPO of zero minutes.
