package co.edu.corhuila.abbr.worker.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.corhuila.abbr.worker.application.port.in.Job.Result;
import co.edu.corhuila.abbr.worker.application.port.out.OrdersApi;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExpireStaleOrdersTest {

    static final Instant NOW = Instant.parse("2026-09-01T12:00:00Z");
    static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    static class FakeOrders implements OrdersApi {
        final List<String> pending;
        final String failOn;
        final List<String> cancelled = new ArrayList<>();
        int limit;

        FakeOrders(List<String> pending, String failOn) {
            this.pending = pending;
            this.failOn = failOn;
        }

        public List<PendingOrder> listPendingCreatedBefore(Instant before, int limit) {
            this.limit = limit;
            return pending.stream().limit(limit).map(id -> new PendingOrder(id, before)).toList();
        }

        public void cancel(String id) {
            if (id.equals(failOn)) {
                throw new IllegalStateException("timeout");
            }
            cancelled.add(id);
        }
    }

    @Test
    void oneFailureDoesNotStopTheBatch() {
        FakeOrders orders = new FakeOrders(List.of("a", "b", "c"), "b");
        Result result = new ExpireStaleOrders(orders, CLOCK, Duration.ofMinutes(30), 100).run(NOW.plusSeconds(60));
        assertEquals(new Result(2, 1), result);
        assertEquals(List.of("a", "c"), orders.cancelled);
    }

    @Test
    void aRunIsBounded() {
        FakeOrders orders = new FakeOrders(List.of("a", "b", "c"), "");
        Result result = new ExpireStaleOrders(orders, CLOCK, Duration.ofMinutes(30), 2).run(NOW.plusSeconds(60));
        assertEquals(2, orders.limit);
        assertEquals(2, result.processed());
    }

    @Test
    void aRunPastItsDeadlineStops() {
        FakeOrders orders = new FakeOrders(List.of("a", "b"), "");
        Result result = new ExpireStaleOrders(orders, CLOCK, Duration.ofMinutes(30), 10).run(NOW.minusSeconds(1));
        assertEquals(0, result.processed());
    }
}
