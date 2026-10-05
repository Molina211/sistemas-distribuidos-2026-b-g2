ALTER TABLE orders.order_item
    ADD CONSTRAINT fk_order_item_customer_order
    FOREIGN KEY (customer_order_id) REFERENCES orders.customer_order (id) ON DELETE CASCADE;

ALTER TABLE orders.customer_order
    ADD CONSTRAINT fk_customer_order_sales_channel
    FOREIGN KEY (channel_code) REFERENCES orders.sales_channel (code) ON DELETE RESTRICT;

ALTER TABLE orders.idempotency_key
    ADD CONSTRAINT fk_idempotency_key_customer_order
    FOREIGN KEY (customer_order_id) REFERENCES orders.customer_order (id) ON DELETE CASCADE;
