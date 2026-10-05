CREATE TABLE orders.order_item (
    id                uuid    PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_order_id uuid    NOT NULL,
    sku               text    NOT NULL CONSTRAINT chk_order_item_sku_length CHECK (char_length(sku) BETWEEN 1 AND 40),
    quantity          integer NOT NULL CONSTRAINT chk_order_item_quantity_positive CHECK (quantity > 0),
    unit_price_cents  bigint  NOT NULL CONSTRAINT chk_order_item_price_not_negative CHECK (unit_price_cents >= 0)
);
