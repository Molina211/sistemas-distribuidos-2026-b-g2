package co.edu.corhuila.abbr.orders.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository.OrderFilter;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository.SaveResult;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Runs against the REAL engine with the schema of <abbr>-orders-db applied. A mocked
 * repository proves the mock was called; it does not prove the SQL is valid, that
 * the mapping keeps the types, or that the migration exists.
 *
 * <pre>TEST_DATABASE_URL=jdbc:postgresql://localhost:5432/orders TEST_DATABASE_USER=... TEST_DATABASE_PASSWORD=... mvn verify</pre>
 */
@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class JdbcOrderRepositoryIntegrationTest {

    JdbcOrderRepository repository;

    @BeforeEach
    void connect() {
        var dataSource = new DriverManagerDataSource(System.getenv("TEST_DATABASE_URL"),
                System.getenv("TEST_DATABASE_USER"), System.getenv("TEST_DATABASE_PASSWORD"));
        repository = new JdbcOrderRepository(new JdbcTemplate(dataSource),
                new TransactionTemplate(new DataSourceTransactionManager(dataSource)));
    }

    static Order newOrder() {
        return Order.place(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 1500,
                Instant.now().truncatedTo(ChronoUnit.MICROS));
    }

    @Test
    void roundTrip() {
        Order order = newOrder();
        repository.saveNew(order, "it-" + order.id());
        Order stored = repository.findById(order.id()).orElseThrow();
        assertEquals(order.customerId(), stored.customerId());
        assertEquals(1500, stored.totalCents());
        assertEquals(Status.PENDING, stored.status());
        assertEquals(order.createdAt(), stored.createdAt());
    }

    @Test
    void idempotencyKeyReturnsTheFirstOrder() {
        Order first = newOrder();
        Order second = newOrder();
        String key = "it-key-" + first.id();
        assertEquals(new SaveResult(first.id(), true), repository.saveNew(first, key));
        assertEquals(new SaveResult(first.id(), false), repository.saveNew(second, key));
        assertTrue(repository.findById(second.id()).isEmpty(), "the second order must have been rolled back");
    }

    @Test
    void updateAndBoundedPage() {
        Order order = newOrder();
        repository.saveNew(order, "it-upd-" + order.id());
        order.confirm();
        repository.update(order);
        var page = repository.findPage(new OrderFilter(Status.CONFIRMED, null), 0, 1);
        assertTrue(page.items().size() <= 1 && page.total() >= 1);
    }
}
