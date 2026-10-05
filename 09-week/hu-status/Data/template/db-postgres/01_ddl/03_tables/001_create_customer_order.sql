-- Singular name; `order` is a reserved word in SQL, hence customer_order.
CREATE TABLE orders.customer_order (
    id           uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id  uuid        NOT NULL,   -- owned by another domain: referenced by id, no FK
    channel_code text        NOT NULL DEFAULT 'WEB',
    status       text        NOT NULL DEFAULT 'PENDING'
                 CONSTRAINT chk_customer_order_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    total_cents  bigint      NOT NULL
                 CONSTRAINT chk_customer_order_total_positive CHECK (total_cents > 0),
    created_at   timestamptz NOT NULL DEFAULT now(),
    updated_at   timestamptz NOT NULL DEFAULT now()
);
