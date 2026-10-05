DROP INDEX IF EXISTS orders.idx_customer_order_status_created_at;
DROP INDEX IF EXISTS orders.idx_customer_order_customer_id_status;
DROP INDEX IF EXISTS orders.idx_idempotency_key_customer_order_id;
DROP INDEX IF EXISTS orders.idx_customer_order_channel_code;
DROP INDEX IF EXISTS orders.idx_order_item_customer_order_id;
