-- Roles are NOLOGIN: they carry permissions. Login users are created by the
-- infrastructure from secrets and granted one of these roles. No password is
-- ever versioned in this repository.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'orders_reader') THEN
        CREATE ROLE orders_reader NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'orders_writer') THEN
        CREATE ROLE orders_writer NOLOGIN;
    END IF;
END
$$;
