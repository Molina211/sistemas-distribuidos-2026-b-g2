package co.edu.corhuila.abbr.orders.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.ListOrdersQuery;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrderCommand;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository;
import co.edu.corhuila.abbr.orders.domain.model.DomainException.BusinessRuleViolation;
import co.edu.corhuila.abbr.orders.domain.model.DomainException.InvalidTransition;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderServiceTest {

    static final String CUSTOMER = "11111111-1111-4111-8111-111111111111";

    /** A minimal fake, so the core is tested with no adapter module on the classpath. */
    static class FakeRepository implements OrderRepository {
        final Map<String, Order> orders = new HashMap<>();
        final Map<String, String> keys = new HashMap<>();

        public SaveResult saveNew(Order order, String key) {
            if (keys.containsKey(key)) {
                return new SaveResult(keys.get(key), false);
            }
            orders.put(order.id(), order);
            keys.put(key, order.id());
            return new SaveResult(order.id(), true);
        }

        public void update(Order order) { orders.put(order.id(), order); }

        public Optional<Order> findById(String id) { return Optional.ofNullable(orders.get(id)); }

        public Page findPage(OrderFilter f, int offset, int limit) {
            List<Order> all = new ArrayList<>(orders.values().stream()
                    .filter(o -> f.status() == null || o.status() == f.status())
                    .sorted(Comparator.comparing(Order::createdAt).reversed()).toList());
            return new Page(all.subList(Math.min(offset, all.size()), Math.min(offset + limit, all.size())), all.size());
        }
    }

    private final AtomicInteger seq = new AtomicInteger();
    private final FakeRepository repository = new FakeRepository();
    private final OrderService service = new OrderService(repository,
            () -> "order-" + seq.incrementAndGet(),
            Clock.fixed(Instant.parse("2026-09-01T10:00:00Z"), ZoneOffset.UTC));

    @ParameterizedTest
    @CsvSource({"'', 100", CUSTOMER + ", 0", CUSTOMER + ", -5"})
    void rejectsInvalidData(String customer, long total) {
        assertThrows(BusinessRuleViolation.class,
                () -> service.placeOrder(new PlaceOrderCommand(customer, total, "key-00000001")));
    }

    @Test
    void placeOrderIsIdempotentPerKey() {
        var first = service.placeOrder(new PlaceOrderCommand(CUSTOMER, 1500, "key-00000001"));
        var retry = service.placeOrder(new PlaceOrderCommand(CUSTOMER, 1500, "key-00000001"));
        assertTrue(first.created());
        assertFalse(retry.created());
        assertEquals(first.id(), retry.id());
        assertEquals(1, repository.orders.size());
    }

    @Test
    void transitions() {
        String confirmed = service.placeOrder(new PlaceOrderCommand(CUSTOMER, 100, "key-00000001")).id();
        assertEquals(Status.CONFIRMED, service.confirmOrder(confirmed).status());
        assertThrows(InvalidTransition.class, () -> service.confirmOrder(confirmed));
        assertThrows(InvalidTransition.class, () -> service.cancelOrder(confirmed));

        String cancelled = service.placeOrder(new PlaceOrderCommand(CUSTOMER, 100, "key-00000002")).id();
        service.cancelOrder(cancelled);
        assertEquals(Status.CANCELLED, service.cancelOrder(cancelled).status()); // idempotent
    }

    @Test
    void listIsBoundedAndPaginated() {
        for (int i = 0; i < 5; i++) {
            service.placeOrder(new PlaceOrderCommand(CUSTOMER, 100, "key-0000000" + i));
        }
        var page = service.listOrders(new ListOrdersQuery(null, null, 2, 2));
        assertEquals(2, page.items().size());
        assertEquals(5, page.total());
    }
}
