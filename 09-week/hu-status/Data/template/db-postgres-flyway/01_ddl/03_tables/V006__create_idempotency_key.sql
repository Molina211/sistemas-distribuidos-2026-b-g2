-- One row per client request that created an order. A retried request with the
-- same key finds its row and returns the order it already created, instead of
-- creating a second one. The primary key is what makes this safe under
-- concurrency: two identical requests cannot both insert it.
CREATE TABLE orders.idempotency_key (
    key               text        PRIMARY KEY CONSTRAINT chk_idempotency_key_length CHECK (char_length(key) BETWEEN 8 AND 128),
    customer_order_id uuid        NOT NULL,
    created_at        timestamptz NOT NULL DEFAULT now()
);
