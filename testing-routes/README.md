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

**Requirements:** `.env` with `DATABASE_URL` (and other env vars). PostgreSQL must be running (e.g. `docker compose up -d`).

## Test coverage (auth)

| Route | Conditions covered |
|-------|--------------------|
| POST /api/auth/register | 201 valid body, 400 invalid email/password/name, 409 duplicate email |
| POST /api/auth/login | 200 valid, 400 invalid body, 401 wrong password, 401 unregistered email |
| GET /api/auth/me | 200 with Bearer token, 401 no token, 401 invalid token |
| POST /api/auth/refresh | 200 with valid cookie, 401 no token, 401 invalid token |
| POST /api/auth/logout | 200 with cookie, 200 without (idempotent) |

## cURL and Postman

See **module/CURL_AND_POSTMAN.md** for copy-paste cURL commands and Postman setup.
