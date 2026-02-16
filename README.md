# SortOut Jobs Backend

A job platform backend (JobHai / Naukri style) — Node.js, TypeScript, Express, Drizzle ORM, PostgreSQL.

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Runtime     | Node.js + TypeScript (ESM)          |
| Framework   | Express 5.x                         |
| Database    | PostgreSQL 16 (Docker)               |
| ORM         | Drizzle ORM + postgres.js           |
| Validation  | Zod v4                              |
| Auth        | JWT (jsonwebtoken) + bcryptjs       |
| Security    | Helmet, CORS, cookie-parser         |
| Logging     | Morgan                              |
| Dev Tools   | tsx (watch mode), drizzle-kit        |

---

## Project Structure

```
sortout-backend/
├── TODO.md                               # Task tracker (done / not done)
├── README.md                             # You are here
├── .env / .env.example
├── .gitignore
├── docker-compose.yml                    # PostgreSQL 16 (Docker)
├── Dockerfile
├── .dockerignore
├── drizzle.config.ts                     # Drizzle Kit migration config
├── package.json                          # ESM, scripts, dependencies
├── tsconfig.json                         # ES2022, strict, @/* alias
└── src/
    ├── index.ts                          # Server entry (DB test + listen)
    ├── app.ts                            # Express app + middleware
    ├── config/
    │   └── env.ts                        # Zod-validated environment vars
    ├── db/
    │   ├── index.ts                      # Drizzle client + testConnection()
    │   └── schema/
    │       └── index.ts                  # Tables: users, refresh_tokens, auth_tokens
    ├── utils/
    │   ├── apiError.ts                   # Custom error class (statusCode + message)
    │   ├── apiResponse.ts                # Standardized JSON response
    │   └── asyncHandler.ts               # Async route wrapper (catches errors)
    ├── middlewares/
    │   ├── error.middleware.ts           # Global error handler
    │   └── auth.middleware.ts            # requireAuth (JWT Bearer -> req.user)
    └── modules/
        └── user-management/
            ├── user.types.ts             # Zod schemas (register, login, responses)
            ├── user.service.ts           # register, login, getById, getByEmail
            ├── token.service.ts          # JWT + refresh token (issue, verify, rotate)
            ├── auth.controller.ts        # Route handlers
            └── auth.router.ts            # Express router -> /api/auth
```

---

## Modules

| #   | Module          | Status      | Description                                              |
|-----|-----------------|-------------|----------------------------------------------------------|
| 0   | Project Init    | In Progress | Docker, deps, config, DB client, utils done; error middleware, app + server next |
| 1   | User Management | Not Started | Register, login, JWT, refresh, me, email verify, OTP, Google |
| 2   | Onboarding      | Future      | Post-signup flow, role selection                         |
| 3   | Profile         | Future      | User profile CRUD, resume, skills                        |
| 4   | Job Listings    | Future      | Create, search, filter, apply                             |

---

## How to Run

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Install dependencies

```bash
npm install
```

### 3. Set up environment

Copy `.env.example` to `.env` and fill in your values:

```
PORT=8000
NODE_ENV=development
DATABASE_URL=postgresql://sortout_user:sortout_pass@localhost:5432/sortout_jobs
ACCESS_TOKEN_SECRET=<long-random-string>
ACCESS_TOKEN_EXPIRY=15m
REFRESH_TOKEN_SECRET=<long-random-string>
REFRESH_TOKEN_EXPIRY=7d
CORS_ORIGIN=http://localhost:3000
```

### 4. Run migrations

```bash
npm run db:generate
npm run db:migrate
```

### 5. Start the server

```bash
npm run dev
```

Server runs on `http://localhost:8000`. Health check: `GET /health`.

---

## API Routes — Module 1 (User Management)

### Phase 1 (Core)

| Method | Path                  | Auth | Description           |
|--------|-----------------------|------|-----------------------|
| POST   | /api/auth/register    | No   | Register new user     |
| POST   | /api/auth/login       | No   | Login (email + pass)  |
| POST   | /api/auth/logout      | No   | Revoke refresh token  |
| POST   | /api/auth/refresh     | No   | Rotate refresh token  |
| GET    | /api/auth/me          | Yes  | Get current user      |

### Phase 2-5 (Later)

| Method | Path                         | Auth | Description          |
|--------|------------------------------|------|----------------------|
| POST   | /api/auth/verify-email       | No   | Verify email token   |
| POST   | /api/auth/resend-verify-email| No   | Resend verify link   |
| POST   | /api/auth/forgot-password    | No   | Send reset link      |
| POST   | /api/auth/reset-password     | No   | Reset with token     |
| POST   | /api/auth/login/otp/request  | No   | Request OTP          |
| POST   | /api/auth/login/otp/verify   | No   | Verify OTP + login   |
| POST   | /api/auth/google             | No   | Google OAuth login   |

---

## Auth Design

- **Access token:** Short-lived JWT (15m), sent via `Authorization: Bearer <token>`.
- **Refresh token:** Random 32-byte hex, SHA-256 hash stored in DB, raw value in httpOnly cookie.
- **On refresh:** Old token deleted, new one issued (rotation).
- **Cookie:** httpOnly, secure (prod), sameSite=lax, path=/api/auth, maxAge=7d.
- **Passwords:** bcrypt (cost 10), min 8 characters.

---

## Key Patterns

1. One module at a time — complete and test before moving to next.
2. Code provided in chat — you type it manually (learning by doing).
3. Plan first, implement later — each module gets a detailed plan.
4. Modular file structure — each module under `modules/<name>/`.
5. Incremental complexity — start basic, level up as we go.
