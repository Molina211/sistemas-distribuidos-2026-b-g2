package co.edu.corhuila.abbr.workflow.application.usecase;

import co.edu.corhuila.abbr.workflow.application.port.in.GetSaga;
import co.edu.corhuila.abbr.workflow.application.port.in.StartPlaceOrderSaga;
import co.edu.corhuila.abbr.workflow.application.port.out.IdGenerator;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Inventory;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Orders;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Payments;
import co.edu.corhuila.abbr.workflow.application.port.out.SagaStore;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaStatus;
import co.edu.corhuila.abbr.workflow.domain.saga.UnknownSagaException;
import java.util.List;

/** Reserve stock, charge, confirm. On failure, undo the completed steps in reverse order. */
public class RunPlaceOrderSaga implements StartPlaceOrderSaga, GetSaga {

    private record Step(String name, Runnable action, Runnable compensation) { }

    private final Inventory inventory;
    private final Payments payments;
    private final Orders orders;
    private final SagaStore store;
    private final IdGenerator ids;

    public RunPlaceOrderSaga(Inventory inventory, Payments payments, Orders orders, SagaStore store, IdGenerator ids) {
        this.inventory = inventory;
        this.payments = payments;
        this.orders = orders;
        this.store = store;
        this.ids = ids;
    }

    @Override
    public SagaInstance get(String id) {
        return store.get(id).orElseThrow(() -> new UnknownSagaException(id));
    }

    @Override
    public StartResult start(PlaceOrderRequest r) {
        SagaStore.CreateResult created = store.create(new SagaInstance(ids.newId(), r.orderId()), r.idempotencyKey());
        if (!created.created()) {
            return new StartResult(created.saga(), false); // a retry of the request: the saga already exists
        }
        SagaInstance saga = created.saga();
        List<Step> steps = List.of(
                new Step("reserve-stock",
                        () -> inventory.reserve(saga.stepKey("reserve-stock"), r.orderId(), r.sku(), r.quantity()),
                        () -> inventory.release(r.orderId())),
                new Step("charge-payment",
                        () -> payments.charge(saga.stepKey("charge-payment"), r.orderId(), r.amountCents()),
                        () -> payments.refund(r.orderId())),
                new Step("confirm-order", () -> orders.confirm(r.orderId()),
                        () -> orders.cancel(r.orderId())));

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            try {
                step.action().run();
            } catch (RuntimeException e) {
                saga.failedAt(step.name(), e.getMessage());
                compensate(saga, steps.subList(0, i));
                store.save(saga);
                return new StartResult(saga, true);
            }
            saga.markDone(step.name());
            store.save(saga); // persist after EVERY step
        }
        saga.status(SagaStatus.COMPLETED);
        store.save(saga);
        return new StartResult(saga, true);
    }

    /** If an undo fails the saga is FAILED: automatic recovery is no longer safe and a person must decide. */
    private void compensate(SagaInstance saga, List<Step> done) {
        for (int i = done.size() - 1; i >= 0; i--) {
            try {
                done.get(i).compensation().run();
            } catch (RuntimeException e) {
                saga.fail("compensation " + done.get(i).name() + ": " + e.getMessage());
                saga.status(SagaStatus.FAILED);
                return;
            }
        }
        saga.status(SagaStatus.COMPENSATED);
    }
}
