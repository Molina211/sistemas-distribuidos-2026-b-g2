ALTER DEFAULT PRIVILEGES IN SCHEMA orders REVOKE ALL ON TABLES FROM orders_reader, orders_writer;
REVOKE ALL ON ALL TABLES IN SCHEMA orders FROM orders_reader, orders_writer;
REVOKE USAGE ON SCHEMA orders FROM orders_reader, orders_writer;
