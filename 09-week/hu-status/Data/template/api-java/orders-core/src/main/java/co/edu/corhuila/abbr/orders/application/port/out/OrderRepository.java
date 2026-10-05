package co.edu.corhuila.abbr.orders.application.port.out;

import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** What the service needs to store orders. Outbound adapters implement it. */
public interface OrderRepository {

    record SaveResult(String id, boolean created) { }

    record OrderFilter(Status status, Instant createdBefore) { }

    record Page(List<Order> items, long total) { }

    /**
     * Stores the order and its idempotency key atomically. If the key was already
     * used, stores nothing and returns the id of the first order with created=false.
     */
    SaveResult saveNew(Order order, String idempotencyKey);

    void update(Order order);

    Optional<Order> findById(String id);

    /** Never unbounded: the caller always passes a limit. */
    Page findPage(OrderFilter filter, int offset, int limit);
}
