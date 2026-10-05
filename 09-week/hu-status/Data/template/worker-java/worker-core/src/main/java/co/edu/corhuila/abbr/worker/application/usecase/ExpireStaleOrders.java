package co.edu.corhuila.abbr.worker.application.usecase;

import co.edu.corhuila.abbr.worker.application.port.in.Job;
import co.edu.corhuila.abbr.worker.application.port.out.OrdersApi;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

/**
 * Cancels orders that stayed PENDING longer than the TTL. Each run handles at
 * most batchSize orders; the rest waits for the next run.
 */
public class ExpireStaleOrders implements Job {

    private static final Logger LOG = Logger.getLogger(ExpireStaleOrders.class.getName());

    private final OrdersApi orders;
    private final Clock clock;
    private final Duration ttl;
    private final int batchSize;

    public ExpireStaleOrders(OrdersApi orders, Clock clock, Duration ttl, int batchSize) {
        this.orders = orders;
        this.clock = clock;
        this.ttl = ttl;
        this.batchSize = batchSize;
    }

    @Override
    public String name() {
        return "expire-stale-orders";
    }

    @Override
    public Result run(Instant deadline) {
        int processed = 0;
        int failed = 0;
        for (OrdersApi.PendingOrder order : orders.listPendingCreatedBefore(clock.instant().minus(ttl), batchSize)) {
            if (clock.instant().isAfter(deadline)) {
                break; // the run's time is over: what is left waits for the next run
            }
            try {
                orders.cancel(order.id());
                processed++;
            } catch (RuntimeException e) { // left for the next run: cancel is idempotent
                failed++;
                LOG.warning("expire order " + order.id() + ": " + e.getMessage());
            }
        }
        return new Result(processed, failed);
    }
}
