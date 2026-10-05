package co.edu.corhuila.abbr.workflow.adapter;

/**
 * The MDC key that carries the X-Correlation-Id of a request. The inbound adapter
 * sets it; the outbound adapters send it. Neither knows the other.
 */
public final class Correlation {

    public static final String MDC_KEY = "correlationId";

    private Correlation() { }
}
