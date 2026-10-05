package co.edu.corhuila.abbr.workflow.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.corhuila.abbr.workflow.application.port.in.StartPlaceOrderSaga.PlaceOrderRequest;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Inventory;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Orders;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Payments;
import co.edu.corhuila.abbr.workflow.application.port.out.SagaStore;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RunPlaceOrderSagaTest {

    static final PlaceOrderRequest REQUEST = new PlaceOrderRequest("o1", "A", 1, 100, "req-00000001");

    /** Records every call so the tests can assert the ORDER of operations. */
    static class Journal implements Inventory, Payments, Orders {
        final List<String> calls = new ArrayList<>();
        final List<String> keys = new ArrayList<>();
        final Set<String> fail;
        Journal(String... fail) { this.fail = Set.of(fail); }
        private void hit(String name) {
            calls.add(name);
            if (fail.contains(name)) {
                throw new IllegalStateException("boom");
            }
        }
        public void reserve(String k, String o, String s, int q) { keys.add(k); hit("reserve"); }
        public void release(String o) { hit("release"); }
        public void charge(String k, String o, long a) { keys.add(k); hit("charge"); }
        public void refund(String o) { hit("refund"); }
        public void confirm(String o) { hit("confirm"); }
        public void cancel(String o) { hit("cancel"); }
    }

    static class MapStore implements SagaStore {
        final Map<String, SagaInstance> items = new HashMap<>();
        final Map<String, String> keys = new HashMap<>();
        public CreateResult create(SagaInstance s, String key) {
            if (keys.containsKey(key)) {
                return new CreateResult(items.get(keys.get(key)), false);
            }
            keys.put(key, s.id());
            items.put(s.id(), s);
            return new CreateResult(s, true);
        }
        public void save(SagaInstance s) { items.put(s.id(), s); }
        public Optional<SagaInstance> get(String id) { return Optional.ofNullable(items.get(id)); }
    }

    private static RunPlaceOrderSaga saga(Journal j) {
        return new RunPlaceOrderSaga(j, j, j, new MapStore(), () -> "s1");
    }

    private static SagaInstance run(Journal j) {
        return saga(j).start(REQUEST).saga();
    }

    @Test
    void happyPathCompletes() {
        Journal j = new Journal();
        assertEquals(SagaStatus.COMPLETED, run(j).status());
        assertEquals(List.of("reserve", "charge", "confirm"), j.calls);
        assertEquals(List.of("s1:reserve-stock", "s1:charge-payment"), j.keys);
    }

    @Test
    void paymentFailureReleasesStock() {
        Journal j = new Journal("charge");
        SagaInstance s = run(j);
        assertEquals(SagaStatus.COMPENSATED, s.status());
        assertEquals("charge-payment", s.failedStep());
        assertEquals(List.of("reserve", "charge", "release"), j.calls);
    }

    @Test
    void lateFailureCompensatesInReverseOrder() {
        Journal j = new Journal("confirm");
        run(j);
        assertEquals(List.of("reserve", "charge", "confirm", "refund", "release"), j.calls);
    }

    @Test
    void failedCompensationNeedsAPerson() {
        assertEquals(SagaStatus.FAILED, run(new Journal("charge", "release")).status());
    }

    @Test
    void theSameKeyNeverStartsASecondSaga() {
        Journal j = new Journal();
        RunPlaceOrderSaga uc = saga(j);
        var first = uc.start(REQUEST);
        var again = uc.start(REQUEST);
        assertTrue(first.created());
        assertFalse(again.created());
        assertEquals(first.saga().id(), again.saga().id());
        assertEquals(3, j.calls.size(), "a retried request must not run the steps again");
    }
}
