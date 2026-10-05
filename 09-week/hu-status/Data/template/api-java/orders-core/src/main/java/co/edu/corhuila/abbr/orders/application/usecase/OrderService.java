package co.edu.corhuila.abbr.orders.application.usecase;

import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.CancelOrder;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.ConfirmOrder;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.GetOrder;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.ListOrders;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.ListOrdersQuery;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.OrderPage;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrder;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrderCommand;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrderResult;
import co.edu.corhuila.abbr.orders.application.port.out.IdGenerator;
import co.edu.corhuila.abbr.orders.application.port.out.OrderNotFoundException;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository.OrderFilter;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import java.time.Clock;
import java.util.function.Consumer;

/** Implements every use case of the domain. It orchestrates; the rules live in Order. */
public class OrderService implements PlaceOrder, GetOrder, ListOrders, ConfirmOrder, CancelOrder {

    private final OrderRepository repository;
    private final IdGenerator ids;
    private final Clock clock;

    public OrderService(OrderRepository repository, IdGenerator ids, Clock clock) {
        this.repository = repository;
        this.ids = ids;
        this.clock = clock;
    }

    @Override
    public PlaceOrderResult placeOrder(PlaceOrderCommand command) {
        Order order = Order.place(ids.newId(), command.customerId(), command.totalCents(), clock.instant());
        OrderRepository.SaveResult saved = repository.saveNew(order, command.idempotencyKey());
        return new PlaceOrderResult(saved.id(), saved.created());
    }

    @Override
    public Order getOrder(String id) {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public OrderPage listOrders(ListOrdersQuery query) {
        OrderRepository.Page page = repository.findPage(
                new OrderFilter(query.status(), query.createdBefore()),
                (query.page() - 1) * query.limit(), query.limit());
        return new OrderPage(page.items(), page.total(), query.page(), query.limit());
    }

    @Override
    public Order confirmOrder(String id) {
        return transition(id, Order::confirm);
    }

    @Override
    public Order cancelOrder(String id) {
        return transition(id, Order::cancel);
    }

    private Order transition(String id, Consumer<Order> change) {
        Order order = getOrder(id);
        change.accept(order);
        repository.update(order);
        return order;
    }
}
