package co.edu.corhuila.abbr.workflow.domain.saga;

public class UnknownSagaException extends RuntimeException {
    public UnknownSagaException(String id) {
        super("saga " + id + " not found");
    }
}
