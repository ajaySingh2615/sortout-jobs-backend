import type { Request, Response } from "express";
import { ApiError } from "../../utils/apiError.js";
import { registerBodySchema, loginBodySchema } from "./user.types.js";
import * as userService from "./user.service.js";
import * as tokenService from "./token.service.js";

const COOKIE_REFRESH = "refreshToken";
const COOKIE_OPTIONS = { httpOnly: true, secure: false, sameSite: "lax" as const, maxAge: 7 * 24 * 60 * 60 * 1000 };

export async function register(req: Request, res: Response): Promise<void> {
  const parsed = registerBodySchema.safeParse(req.body);
  if (!parsed.success) throw new ApiError(400, "Validation failed", parsed.error.issues.map((i) => `${i.path.join(".")}: ${i.message}`));

  const data = parsed.data;
  const existing = await userService.getByEmail(data.email);
  if (existing) throw new ApiError(409, "Email already registered");

  const user = await userService.register(data);
  const accessToken = tokenService.issueAccessToken(user.id, user.email);
  const refreshToken = await tokenService.issueRefreshToken(user.id, req.get("User-Agent") ?? undefined);

  res.cookie(COOKIE_REFRESH, refreshToken, COOKIE_OPTIONS);
  res.status(201).json({
    success: true,
    statusCode: 201,
    message: "Registered",
    data: { user, accessToken, expiresIn: "15m" },
  });
}

export async function login(req: Request, res: Response): Promise<void> {
  const parsed = loginBodySchema.safeParse(req.body);
  if (!parsed.success) throw new ApiError(400, "Validation failed", parsed.error.issues.map((i) => `${i.path.join(".")}: ${i.message}`));

  const { email, password } = parsed.data;
  const user = await userService.login(email, password);
  if (!user) throw new ApiError(401, "Invalid email or password");

  const accessToken = tokenService.issueAccessToken(user.id, user.email);
  const refreshToken = await tokenService.issueRefreshToken(user.id, req.get("User-Agent") ?? undefined);

  res.cookie(COOKIE_REFRESH, refreshToken, COOKIE_OPTIONS);
  res.status(200).json({
    success: true,
    statusCode: 200,
    message: "Logged in",
    data: { user, accessToken, expiresIn: "15m" },
  });
}

export async function logout(req: Request, res: Response): Promise<void> {
  const token = req.cookies?.[COOKIE_REFRESH] ?? req.body?.refreshToken;
  if (token) await tokenService.revokeRefreshToken(token);
  res.clearCookie(COOKIE_REFRESH);
  res.status(200).json({ success: true, statusCode: 200, message: "Logged out", data: null });
}

export async function refresh(req: Request, res: Response): Promise<void> {
  const token = req.cookies?.[COOKIE_REFRESH] ?? req.body?.refreshToken;
  if (!token) throw new ApiError(401, "Refresh token required");

  const result = await tokenService.verifyAndRotateRefreshToken(token, req.get("User-Agent") ?? undefined);
  if (!result) throw new ApiError(401, "Invalid or expired refresh token");

  const user = await userService.getById(result.userId);
  if (!user) throw new ApiError(401, "User not found");

  const accessToken = tokenService.issueAccessToken(user.id, user.email);
  res.cookie(COOKIE_REFRESH, result.newRefreshToken, COOKIE_OPTIONS);
  res.status(200).json({
    success: true,
    statusCode: 200,
    message: "Token refreshed",
    data: { user, accessToken, expiresIn: "15m" },
  });
}

export async function me(req: Request, res: Response): Promise<void> {
  if (!req.user) throw new ApiError(401, "Unauthorized");
  const user = await userService.getById(req.user.userId);
  if (!user) throw new ApiError(404, "User not found");
  res.status(200).json({
    success: true,
    statusCode: 200,
    message: "OK",
    data: { user },
  });
}
