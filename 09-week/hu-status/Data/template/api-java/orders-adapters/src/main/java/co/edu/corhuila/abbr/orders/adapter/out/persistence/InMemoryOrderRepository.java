package co.edu.corhuila.abbr.orders.adapter.out.persistence;

import co.edu.corhuila.abbr.orders.application.port.out.OrderNotFoundException;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A real adapter, not a mock: local runs use it. Synchronized, because the web
 * server serves requests concurrently. Stores copies, as a database would.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, String> keys = new HashMap<>();

    @Override
    public synchronized SaveResult saveNew(Order order, String idempotencyKey) {
        String existing = keys.get(idempotencyKey);
        if (existing != null) {
            return new SaveResult(existing, false);
        }
        orders.put(order.id(), copy(order));
        keys.put(idempotencyKey, order.id());
        return new SaveResult(order.id(), true);
    }

    @Override
    public synchronized void update(Order order) {
        if (!orders.containsKey(order.id())) {
            throw new OrderNotFoundException(order.id());
        }
        orders.put(order.id(), copy(order));
    }

    @Override
    public synchronized Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id)).map(InMemoryOrderRepository::copy);
    }

    @Override
    public synchronized Page findPage(OrderFilter f, int offset, int limit) {
        List<Order> matching = orders.values().stream()
                .filter(o -> f.status() == null || o.status() == f.status())
                .filter(o -> f.createdBefore() == null || o.createdAt().isBefore(f.createdBefore()))
                .sorted(Comparator.comparing(Order::createdAt).thenComparing(Order::id).reversed())
                .toList();
        List<Order> page = matching.stream().skip(offset).limit(limit).map(InMemoryOrderRepository::copy).toList();
        return new Page(page, matching.size());
    }

    private static Order copy(Order o) {
        return Order.restore(o.id(), o.customerId(), o.totalCents(), o.status(), o.createdAt());
    }
}
