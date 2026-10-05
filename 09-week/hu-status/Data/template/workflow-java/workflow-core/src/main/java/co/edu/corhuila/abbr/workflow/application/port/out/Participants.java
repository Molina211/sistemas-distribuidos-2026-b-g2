package co.edu.corhuila.abbr.workflow.application.port.out;

/**
 * Each participant exposes a do and an undo. The do receives an idempotency key,
 * so a retried step is not applied twice; every undo MUST be idempotent.
 */
public final class Participants {

    private Participants() { }

    public interface Inventory {
        void reserve(String key, String orderId, String sku, int quantity);
        void release(String orderId);
    }

    public interface Payments {
        void charge(String key, String orderId, long amountCents);
        void refund(String orderId);
    }

    public interface Orders {
        void confirm(String orderId);
        void cancel(String orderId);
    }
}
