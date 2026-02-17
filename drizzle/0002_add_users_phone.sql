-- Add phone column to users for OTP login
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "phone" varchar(20);
--> statement-breakpoint
CREATE UNIQUE INDEX IF NOT EXISTS "users_phone_unique" ON "users" USING btree ("phone");
