package co.edu.corhuila.abbr.workflow.domain.saga;

import java.util.ArrayList;
import java.util.List;

/** Persisted state of one saga execution. */
public final class SagaInstance {

    private final String id;
    private final String orderId;
    private final List<String> completed = new ArrayList<>();
    private SagaStatus status = SagaStatus.RUNNING;
    private String failedStep = "";
    private String error = ""; // full detail for operators; never sent to clients

    public SagaInstance(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    /** A copy, so a store never shares mutable state with its callers. */
    public SagaInstance copy() {
        SagaInstance c = new SagaInstance(id, orderId);
        c.completed.addAll(completed);
        c.status = status;
        c.failedStep = failedStep;
        c.error = error;
        return c;
    }

    /**
     * The Idempotency-Key sent with a step: the same saga retrying the same step
     * never charges or reserves twice.
     */
    public String stepKey(String step) { return id + ":" + step; }

    public void markDone(String step) { completed.add(step); }
    public void failedAt(String step, String reason) {
        failedStep = step;
        fail(step + ": " + reason);
    }
    public void fail(String reason) { error = error.isEmpty() ? reason : error + "; " + reason; }
    public void status(SagaStatus s) { status = s; }

    public String id() { return id; }
    public String orderId() { return orderId; }
    public List<String> completed() { return List.copyOf(completed); }
    public SagaStatus status() { return status; }
    public String failedStep() { return failedStep; }
    public String error() { return error; }
}
