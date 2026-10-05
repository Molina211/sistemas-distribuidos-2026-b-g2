package co.edu.corhuila.abbr.orders.domain.model;

/** A rule of the domain was violated. The inbound adapter maps each subtype to a status code. */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }

    /** The data breaks an invariant of the aggregate. */
    public static final class BusinessRuleViolation extends DomainException {
        public BusinessRuleViolation(String message) {
            super(message);
        }
    }

    /** The order cannot move to the requested status. */
    public static final class InvalidTransition extends DomainException {
        public InvalidTransition(String message) {
            super(message);
        }
    }
}
