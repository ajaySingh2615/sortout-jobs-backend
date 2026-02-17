import { z } from "zod";

const passwordSchema = z
  .string()
  .min(8, "Password must be at least 8 characters")
  .max(128);

export const registerBodySchema = z.object({
  email: z.string().email("Invalid email").max(255),
  password: passwordSchema,
  name: z.string().min(1, "Name is required").max(255),
});

export const loginBodySchema = z.object({
  email: z.string().email("Invalid email"),
  password: z.string().min(1, "Password is required"),
});

export const verifyEmailBodySchema = z.object({
  token: z.string().min(1, "Token is required"),
});

export const resendVerifyEmailBodySchema = z.object({
  email: z.string().email("Invalid email"),
});

export const userResponseSchema = z.object({
  id: z.string().uuid(),
  email: z.string().email(),
  name: z.string(),
  avatarUrl: z.string().url().nullable(),
  role: z.string(),
  emailVerifiedAt: z.date().nullable(),
  createdAt: z.date(),
  updatedAt: z.date(),
});

export const authResponseSchema = z.object({
  user: userResponseSchema,
  accessToken: z.string(),
  expiresIn: z.string().optional(),
});

export type RegisterBody = z.infer<typeof registerBodySchema>;
export type LoginBody = z.infer<typeof loginBodySchema>;
export type VerifyEmailBody = z.infer<typeof verifyEmailBodySchema>;
export type ResendVerifyEmailBody = z.infer<typeof resendVerifyEmailBodySchema>;
export type UserResponse = z.infer<typeof userResponseSchema>;
export type AuthResponse = z.infer<typeof authResponseSchema>;
