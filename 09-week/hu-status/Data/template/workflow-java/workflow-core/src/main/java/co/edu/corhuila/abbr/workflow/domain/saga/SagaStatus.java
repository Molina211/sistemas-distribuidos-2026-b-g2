package co.edu.corhuila.abbr.workflow.domain.saga;

public enum SagaStatus {
    RUNNING,
    COMPLETED,
    COMPENSATED,
    /** A compensation itself failed: automatic recovery is no longer safe. */
    FAILED
}
