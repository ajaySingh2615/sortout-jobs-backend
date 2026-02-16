-- Rename users.password-hash to password_hash for schema consistency
DO $$ BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'users' AND column_name = 'password-hash'
  ) THEN
    ALTER TABLE "users" RENAME COLUMN "password-hash" TO "password_hash";
  END IF;
END $$;
