package co.edu.corhuila.abbr.worker.application.port.out;

import java.time.Instant;
import java.util.List;

/** The orders domain as the worker sees it: its public contract, not its tables. */
public interface OrdersApi {

    record PendingOrder(String id, Instant createdAt) { }

    /** At most limit orders: a job never reads an unbounded result. */
    List<PendingOrder> listPendingCreatedBefore(Instant before, int limit);

    void cancel(String orderId);
}
