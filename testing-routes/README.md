# Testing routes

Automated tests for API routes, plus cURL/Postman examples.

## Structure

- **module/** — Route test suites by feature
  - `auth.routes.test.ts` — Auth API tests (register, login, me, refresh, logout)
  - `CURL_AND_POSTMAN.md` — cURL and Postman usage

## Run tests

From project root:

```bash
npm run test        # run once
npm run test:watch  # watch mode
```

**Requirements:** `.env` with `DATABASE_URL` (and other env vars). PostgreSQL must be running (e.g. `docker compose up -d`). After adding phone OTP, run **`npm run db:migrate`** so the `users.phone` column exists.

## Test coverage (auth)

| Route | Conditions covered |
|-------|--------------------|
| POST /api/auth/register | 201 valid body, 400 invalid email/password/name, 409 duplicate email |
| POST /api/auth/login | 200 valid, 400 invalid body, 401 wrong password, 401 unregistered email |
| GET /api/auth/me | 200 with Bearer token, 401 no token, 401 invalid token |
| POST /api/auth/refresh | 200 with valid cookie, 401 no token, 401 invalid token |
| POST /api/auth/logout | 200 with cookie, 200 without (idempotent) |
| POST /api/auth/verify-email | 200 valid token (marks verified), 400 invalid/expired token, 400 missing token |
| POST /api/auth/resend-verify-email | 200 user exists unverified, 200 email not registered (no enumeration), 200 already verified, 400 invalid email |
| POST /api/auth/forgot-password | 200 user exists, 200 email not registered (no enumeration), 400 invalid email |
| POST /api/auth/reset-password | 200 valid token + new password (login with new works), 400 invalid/expired token, 400 newPassword too short |
| POST /api/auth/google | 200 valid idToken (user + accessToken + cookie), 401 invalid token, 400 missing idToken |
| POST /api/auth/request-otp | 200 OTP sent (Twilio configured), 503 Twilio not configured, 400 invalid phone, 429 rate limit |
| POST /api/auth/verify-otp | 200 valid code (user + tokens + cookie), 400 invalid/expired code, 400 invalid body |

## cURL and Postman

See **module/CURL_AND_POSTMAN.md** for copy-paste cURL commands and Postman setup.
