import bcrypt from "bcryptjs";
import { eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { users } from "../../db/schema/index.js";
import type { RegisterBody, UserResponse } from "./user.types.js";

const SALT_ROUNDS = 10;

function toUserResponse(row: {
  id: string;
  email: string;
  name: string;
  avatarUrl: string | null;
  role: string;
  emailVerifiedAt: Date | null;
  createdAt: Date;
  updatedAt: Date;
}): UserResponse {
  return {
    id: row.id,
    email: row.email,
    name: row.name,
    avatarUrl: row.avatarUrl ? row.avatarUrl : null,
    role: row.role,
    emailVerifiedAt: row.emailVerifiedAt,
    createdAt: row.createdAt,
    updatedAt: row.updatedAt,
  };
}

export async function register(data: RegisterBody): Promise<UserResponse> {
  const passwordHash = await bcrypt.hash(data.password, SALT_ROUNDS);
  const inserted = await db
    .insert(users)
    .values({
      email: data.email.toLowerCase().trim(),
      passwordHash,
      name: data.name.trim(),
      provider: "email",
    })
    .returning({
      id: users.id,
      email: users.email,
      name: users.name,
      avatarUrl: users.avatarUrl,
      role: users.role,
      emailVerifiedAt: users.emailVerifiedAt,
      createdAt: users.createdAt,
      updatedAt: users.updatedAt,
    });
  const row = inserted[0];
  if (!row) throw new Error("Insert failed");
  return toUserResponse(row);
}

export async function getByEmail(email: string): Promise<{
  id: string;
  email: string;
  name: string;
  passwordHash: string | null;
  avatarUrl: string | null;
  role: string;
  emailVerifiedAt: Date | null;
  createdAt: Date;
  updatedAt: Date;
} | null> {
  const rows = await db
    .select()
    .from(users)
    .where(eq(users.email, email.toLowerCase().trim()))
    .limit(1);
  return rows[0] ?? null;
}

export async function login(
  email: string,
  password: string,
): Promise<UserResponse | null> {
  const user = await getByEmail(email);
  if (!user || !user.passwordHash) return null;
  const ok = await bcrypt.compare(password, user.passwordHash);
  if (!ok) return null;
  return toUserResponse(user);
}

export async function getById(id: string): Promise<UserResponse | null> {
  const rows = await db
    .select({
      id: users.id,
      email: users.email,
      name: users.name,
      avatarUrl: users.avatarUrl,
      role: users.role,
      emailVerifiedAt: users.emailVerifiedAt,
      createdAt: users.createdAt,
      updatedAt: users.updatedAt,
    })
    .from(users)
    .where(eq(users.id, id))
    .limit(1);
  const row = rows[0];
  return row ? toUserResponse(row) : null;
}

export async function setEmailVerifiedByEmail(email: string): Promise<boolean> {
  const result = await db
    .update(users)
    .set({
      emailVerifiedAt: new Date(),
      updatedAt: new Date(),
    })
    .where(eq(users.email, email.toLowerCase().trim()))
    .returning({ id: users.id });
  return result.length > 0;
}
