package co.edu.corhuila.abbr.orders.application.port.out;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("order " + id + " not found");
    }
}
