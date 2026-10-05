-- Idempotent: an upsert against the primary key, never a bare INSERT.
INSERT INTO orders.sales_channel (code, description) VALUES
    ('WEB',    'Web portal'),
    ('MOBILE', 'Mobile application'),
    ('STORE',  'Physical store')
ON CONFLICT (code) DO UPDATE SET description = EXCLUDED.description;
