package co.edu.corhuila.abbr.orders.domain.model;

import co.edu.corhuila.abbr.orders.domain.model.DomainException.BusinessRuleViolation;
import co.edu.corhuila.abbr.orders.domain.model.DomainException.InvalidTransition;
import java.time.Instant;
import java.util.Objects;

/** Aggregate root. Plain Java: no framework annotation may appear in this module. */
public final class Order {

    private final String id;
    private final String customerId;
    private final long totalCents; // minor units: never double for money
    private final Instant createdAt;
    private Status status;

    private Order(String id, String customerId, long totalCents, Status status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = customerId;
        this.totalCents = totalCents;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Order place(String id, String customerId, long totalCents, Instant now) {
        if (customerId == null || customerId.isBlank()) {
            throw new BusinessRuleViolation("customer is required");
        }
        if (totalCents <= 0) {
            throw new BusinessRuleViolation("total must be positive");
        }
        return new Order(id, customerId, totalCents, Status.PENDING, now);
    }

    /** Rebuilds an order that already exists. Used by persistence adapters only. */
    public static Order restore(String id, String customerId, long totalCents, Status status, Instant createdAt) {
        return new Order(id, customerId, totalCents, status, createdAt);
    }

    public void confirm() {
        if (status != Status.PENDING) {
            throw new InvalidTransition("cannot confirm an order in status " + status);
        }
        status = Status.CONFIRMED;
    }

    /** Idempotent: callers that retry — a worker, a saga compensation — rely on it. */
    public void cancel() {
        if (status == Status.CANCELLED) {
            return;
        }
        if (status != Status.PENDING) {
            throw new InvalidTransition("cannot cancel an order in status " + status);
        }
        status = Status.CANCELLED;
    }

    public String id() { return id; }
    public String customerId() { return customerId; }
    public long totalCents() { return totalCents; }
    public Status status() { return status; }
    public Instant createdAt() { return createdAt; }
}
