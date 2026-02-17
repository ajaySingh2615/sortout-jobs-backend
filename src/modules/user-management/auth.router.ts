import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { requireAuth } from "../../middlewares/auth.middleware.js";
import * as authController from "./auth.controller.js";
import { authLoginLimiter, authRegisterLimiter } from "./rate-limit.js";

const router = Router();

router.post(
  "/register",
  authRegisterLimiter,
  asyncHandler(authController.register),
);
router.post("/login", authLoginLimiter, asyncHandler(authController.login));
router.post("/logout", asyncHandler(authController.logout));
router.post("/refresh", asyncHandler(authController.refresh));
router.get("/me", requireAuth, asyncHandler(authController.me));

export const authRouter = router;
