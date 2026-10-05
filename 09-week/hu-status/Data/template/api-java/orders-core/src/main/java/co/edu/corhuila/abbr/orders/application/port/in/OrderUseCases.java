package co.edu.corhuila.abbr.orders.application.port.in;

import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.time.Instant;
import java.util.List;

/** What the service offers. Inbound adapters depend on these interfaces only. */
public final class OrderUseCases {

    private OrderUseCases() { }

    public record PlaceOrderCommand(String customerId, long totalCents, String idempotencyKey) { }

    /** created is false when the key was already used: a retry, nothing new created. */
    public record PlaceOrderResult(String id, boolean created) { }

    /** status and createdBefore are optional filters (null = no filter). */
    public record ListOrdersQuery(Status status, Instant createdBefore, int page, int limit) { }

    public record OrderPage(List<Order> items, long total, int page, int limit) { }

    public interface PlaceOrder {
        PlaceOrderResult placeOrder(PlaceOrderCommand command);
    }

    public interface GetOrder {
        Order getOrder(String id);
    }

    public interface ListOrders {
        OrderPage listOrders(ListOrdersQuery query);
    }

    public interface ConfirmOrder {
        Order confirmOrder(String id);
    }

    public interface CancelOrder {
        Order cancelOrder(String id);
    }
}
