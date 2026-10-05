package co.edu.corhuila.abbr.workflow.application.port.in;

import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;

public interface StartPlaceOrderSaga {

    /** idempotencyKey: the same key never starts a second saga. */
    record PlaceOrderRequest(String orderId, String sku, int quantity, long amountCents, String idempotencyKey) { }

    /** created is false when the key had already started a saga: it is returned and nothing runs again. */
    record StartResult(SagaInstance saga, boolean created) { }

    StartResult start(PlaceOrderRequest request);
}
