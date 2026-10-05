package co.edu.corhuila.abbr.orders.adapter.out.persistence;

import co.edu.corhuila.abbr.orders.application.port.out.OrderNotFoundException;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/** Reads and writes the schema owned by <abbr>-orders-db. It knows SQL; the domain does not. */
public class JdbcOrderRepository implements OrderRepository {

    private static final String COLUMNS = "id, customer_id, total_cents, status, created_at";

    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;

    public JdbcOrderRepository(JdbcTemplate jdbc, TransactionTemplate tx) {
        this.jdbc = jdbc;
        this.tx = tx;
    }

    @Override
    public SaveResult saveNew(Order o, String idempotencyKey) {
        // The order and its key are written in ONE transaction. If the key already
        // exists the insert of the key does nothing, the transaction is rolled back
        // and the first order is returned: a retry never creates a second order.
        Boolean created = tx.execute(status -> {
            jdbc.update("INSERT INTO orders.customer_order (id, customer_id, status, total_cents, created_at) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    UUID.fromString(o.id()), UUID.fromString(o.customerId()), o.status().name(),
                    o.totalCents(), OffsetDateTime.ofInstant(o.createdAt(), ZoneOffset.UTC));
            int inserted = jdbc.update("INSERT INTO orders.idempotency_key (key, customer_order_id) VALUES (?, ?) "
                    + "ON CONFLICT (key) DO NOTHING", idempotencyKey, UUID.fromString(o.id()));
            if (inserted == 0) {
                status.setRollbackOnly();
            }
            return inserted == 1;
        });
        if (Boolean.TRUE.equals(created)) {
            return new SaveResult(o.id(), true);
        }
        String first = jdbc.queryForObject(
                "SELECT customer_order_id FROM orders.idempotency_key WHERE key = ?", String.class, idempotencyKey);
        return new SaveResult(first, false);
    }

    @Override
    public void update(Order o) {
        int changed = jdbc.update("UPDATE orders.customer_order SET status = ?, updated_at = now() WHERE id = ?",
                o.status().name(), UUID.fromString(o.id()));
        if (changed == 0) {
            throw new OrderNotFoundException(o.id());
        }
    }

    @Override
    public Optional<Order> findById(String id) {
        return jdbc.query("SELECT " + COLUMNS + " FROM orders.customer_order WHERE id = ?",
                (rs, n) -> map(rs), UUID.fromString(id)).stream().findFirst();
    }

    @Override
    public Page findPage(OrderFilter f, int offset, int limit) {
        // Only the clauses that apply are added, and every value stays a parameter.
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        if (f.status() != null) {
            where.append(" AND status = ?");
            args.add(f.status().name());
        }
        if (f.createdBefore() != null) {
            where.append(" AND created_at < ?");
            args.add(OffsetDateTime.ofInstant(f.createdBefore(), ZoneOffset.UTC));
        }
        Long total = jdbc.queryForObject("SELECT count(*) FROM orders.customer_order" + where, Long.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(limit);
        pageArgs.add(offset);
        List<Order> items = jdbc.query("SELECT " + COLUMNS + " FROM orders.customer_order" + where
                + " ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?", (rs, n) -> map(rs), pageArgs.toArray());
        return new Page(items, total == null ? 0 : total);
    }

    private static Order map(ResultSet rs) throws SQLException {
        return Order.restore(rs.getString("id"), rs.getString("customer_id"), rs.getLong("total_cents"),
                Status.valueOf(rs.getString("status")),
                rs.getObject("created_at", OffsetDateTime.class).toInstant());
    }
}
