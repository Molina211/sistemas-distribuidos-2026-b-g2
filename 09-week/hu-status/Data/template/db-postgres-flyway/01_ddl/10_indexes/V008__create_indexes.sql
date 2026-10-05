-- Foreign key columns are always indexed.
CREATE INDEX IF NOT EXISTS idx_order_item_customer_order_id      ON orders.order_item (customer_order_id);
CREATE INDEX IF NOT EXISTS idx_customer_order_channel_code        ON orders.customer_order (channel_code);
CREATE INDEX IF NOT EXISTS idx_idempotency_key_customer_order_id  ON orders.idempotency_key (customer_order_id);

-- Query patterns, named for the query they serve.
-- "orders of a customer, by status":             WHERE customer_id = ? AND status = ?
CREATE INDEX IF NOT EXISTS idx_customer_order_customer_id_status  ON orders.customer_order (customer_id, status);
-- "pending orders created before a moment":      WHERE status = ? AND created_at < ?
CREATE INDEX IF NOT EXISTS idx_customer_order_status_created_at   ON orders.customer_order (status, created_at);
