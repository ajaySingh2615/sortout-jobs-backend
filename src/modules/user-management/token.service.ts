import jwt from "jsonwebtoken";
import crypto from "node:crypto";
import { eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { refreshTokens } from "../../db/schema/index.js";
import { env } from "../../config/env.js";

const REFRESH_TOKEN_BYTES = 32;

function parseExpiryToMs(expiry: string): number {
  const match = expiry.trim().match(/^(\d+)([smhd])$/);
  if (!match) return 7 * 24 * 60 * 60 * 1000;
  const n = parseInt(match[1], 10);
  const unit = match[2];
  const multipliers: Record<string, number> = { s: 1000, m: 60 * 1000, h: 60 * 60 * 1000, d: 24 * 60 * 60 * 1000 };
  return n * (multipliers[unit] ?? 86400000);
}

function hashToken(token: string): string {
  return crypto.createHash("sha256").update(token).digest("hex");
}

export function issueAccessToken(userId: string, email: string): string {
  return jwt.sign(
    { sub: userId, email },
    env.ACCESS_TOKEN_SECRET,
    { expiresIn: env.ACCESS_TOKEN_EXPIRY } as jwt.SignOptions,
  );
}

export async function issueRefreshToken(
  userId: string,
  deviceInfo?: string,
): Promise<string> {
  const raw = crypto.randomBytes(REFRESH_TOKEN_BYTES).toString("hex");
  const tokenHash = hashToken(raw);
  const expiresAt = new Date(Date.now() + parseExpiryToMs(env.REFRESH_TOKEN_EXPIRY));

  await db.insert(refreshTokens).values({
    userId,
    tokenHash,
    deviceInfo: deviceInfo ?? null,
    expiresAt,
  });

  return raw;
}

export function verifyAccessToken(token: string): { userId: string; email: string } {
  const payload = jwt.verify(token, env.ACCESS_TOKEN_SECRET) as { sub: string; email: string };
  return { userId: payload.sub, email: payload.email };
}

export async function verifyAndRotateRefreshToken(
  rawToken: string,
  deviceInfo?: string,
): Promise<{ userId: string; newRefreshToken: string } | null> {
  const tokenHash = hashToken(rawToken);
  const rows = await db
    .select({ userId: refreshTokens.userId, id: refreshTokens.id, expiresAt: refreshTokens.expiresAt })
    .from(refreshTokens)
    .where(eq(refreshTokens.tokenHash, tokenHash))
    .limit(1);

  const row = rows[0];
  if (!row || new Date() > row.expiresAt) return null;

  await db.delete(refreshTokens).where(eq(refreshTokens.id, row.id));
  const newRefreshToken = await issueRefreshToken(row.userId, deviceInfo);
  return { userId: row.userId, newRefreshToken };
}

export async function revokeRefreshToken(rawToken: string): Promise<boolean> {
  const tokenHash = hashToken(rawToken);
  const deleted = await db
    .delete(refreshTokens)
    .where(eq(refreshTokens.tokenHash, tokenHash))
    .returning({ id: refreshTokens.id });
  return deleted.length > 0;
}
