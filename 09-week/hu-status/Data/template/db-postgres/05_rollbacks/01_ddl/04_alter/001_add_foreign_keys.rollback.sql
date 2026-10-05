ALTER TABLE orders.idempotency_key DROP CONSTRAINT IF EXISTS fk_idempotency_key_customer_order;
ALTER TABLE orders.customer_order  DROP CONSTRAINT IF EXISTS fk_customer_order_sales_channel;
ALTER TABLE orders.order_item      DROP CONSTRAINT IF EXISTS fk_order_item_customer_order;
