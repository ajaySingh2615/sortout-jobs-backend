-- Master Data tables
CREATE TABLE IF NOT EXISTS "cities" (
  "id" serial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "state" varchar(100) NOT NULL,
  "created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "localities" (
  "id" serial PRIMARY KEY NOT NULL,
  "city_id" integer NOT NULL REFERENCES "cities"("id") ON DELETE cascade,
  "name" varchar(100) NOT NULL,
  "created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "localities_city_id_idx" ON "localities" USING btree ("city_id");
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "job_roles" (
  "id" serial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "category" varchar(100),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "skills" (
  "id" serial PRIMARY KEY NOT NULL,
  "role_id" integer NOT NULL REFERENCES "job_roles"("id") ON DELETE cascade,
  "name" varchar(100) NOT NULL,
  "created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "skills_role_id_idx" ON "skills" USING btree ("role_id");
--> statement-breakpoint

-- Profiles table
CREATE TABLE IF NOT EXISTS "profiles" (
  "id" serial PRIMARY KEY NOT NULL,
  "user_id" uuid NOT NULL UNIQUE REFERENCES "users"("id") ON DELETE cascade,
  "full_name" varchar(255),
  "gender" varchar(10),
  "education_level" varchar(30),
  "has_experience" boolean,
  "experience_level" varchar(30),
  "current_salary" integer,
  "preferred_city_id" integer REFERENCES "cities"("id"),
  "preferred_locality_id" integer REFERENCES "localities"("id"),
  "whatsapp_updates" boolean DEFAULT false,
  "preferred_role_id" integer REFERENCES "job_roles"("id"),
  "headline" varchar(250),
  "summary" text,
  "notice_period" varchar(30),
  "profile_completed" boolean DEFAULT false,
  "profile_picture" varchar(512),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "profiles_user_id_idx" ON "profiles" USING btree ("user_id");
--> statement-breakpoint

-- Profile skills pivot
CREATE TABLE IF NOT EXISTS "profile_skills" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL REFERENCES "profiles"("id") ON DELETE cascade,
  "skill_id" integer NOT NULL REFERENCES "skills"("id") ON DELETE cascade
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "profile_skills_profile_id_idx" ON "profile_skills" USING btree ("profile_id");
--> statement-breakpoint

-- Personal details
CREATE TABLE IF NOT EXISTS "personal_details" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL UNIQUE REFERENCES "profiles"("id") ON DELETE cascade,
  "date_of_birth" date,
  "marital_status" varchar(20),
  "address" text,
  "pincode" varchar(10),
  "nationality" varchar(50)
);
--> statement-breakpoint

-- Employments
CREATE TABLE IF NOT EXISTS "employments" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL REFERENCES "profiles"("id") ON DELETE cascade,
  "designation" varchar(255) NOT NULL,
  "company" varchar(255) NOT NULL,
  "employment_type" varchar(20),
  "is_current" boolean DEFAULT false,
  "start_date" date,
  "end_date" date,
  "description" text,
  "notice_period" varchar(30),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "employments_profile_id_idx" ON "employments" USING btree ("profile_id");
--> statement-breakpoint

-- Educations
CREATE TABLE IF NOT EXISTS "educations" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL REFERENCES "profiles"("id") ON DELETE cascade,
  "degree" varchar(100) NOT NULL,
  "specialization" varchar(100),
  "institution" varchar(255) NOT NULL,
  "pass_out_year" integer,
  "grade_type" varchar(20),
  "grade" varchar(20),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "educations_profile_id_idx" ON "educations" USING btree ("profile_id");
--> statement-breakpoint

-- Projects
CREATE TABLE IF NOT EXISTS "projects" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL REFERENCES "profiles"("id") ON DELETE cascade,
  "title" varchar(255) NOT NULL,
  "description" text,
  "start_date" date,
  "end_date" date,
  "is_ongoing" boolean DEFAULT false,
  "project_url" varchar(512),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "projects_profile_id_idx" ON "projects" USING btree ("profile_id");
--> statement-breakpoint

-- IT Skills
CREATE TABLE IF NOT EXISTS "it_skills" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL REFERENCES "profiles"("id") ON DELETE cascade,
  "name" varchar(100) NOT NULL,
  "proficiency" varchar(20) DEFAULT 'BEGINNER',
  "experience_months" integer DEFAULT 0,
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "it_skills_profile_id_idx" ON "it_skills" USING btree ("profile_id");
--> statement-breakpoint

-- Resumes
CREATE TABLE IF NOT EXISTS "resumes" (
  "id" serial PRIMARY KEY NOT NULL,
  "profile_id" integer NOT NULL UNIQUE REFERENCES "profiles"("id") ON DELETE cascade,
  "file_name" varchar(255) NOT NULL,
  "file_path" varchar(512) NOT NULL,
  "uploaded_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint

-- Jobs
CREATE TABLE IF NOT EXISTS "jobs" (
  "id" serial PRIMARY KEY NOT NULL,
  "title" varchar(255) NOT NULL,
  "company" varchar(255) NOT NULL,
  "company_logo" varchar(512),
  "description" text,
  "requirements" text,
  "city_id" integer REFERENCES "cities"("id"),
  "location_type" varchar(20) DEFAULT 'ONSITE',
  "employment_type" varchar(20) DEFAULT 'FULL_TIME',
  "salary_min" integer,
  "salary_max" integer,
  "is_salary_disclosed" boolean DEFAULT true,
  "experience_min_years" integer DEFAULT 0,
  "experience_max_years" integer,
  "min_education" varchar(30),
  "vacancies" integer DEFAULT 1,
  "application_deadline" date,
  "is_featured" boolean DEFAULT false,
  "is_active" boolean DEFAULT true,
  "posted_by" uuid REFERENCES "users"("id"),
  "created_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "jobs_city_id_idx" ON "jobs" USING btree ("city_id");
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "jobs_is_active_idx" ON "jobs" USING btree ("is_active");
--> statement-breakpoint

-- Job skills pivot
CREATE TABLE IF NOT EXISTS "job_skills" (
  "id" serial PRIMARY KEY NOT NULL,
  "job_id" integer NOT NULL REFERENCES "jobs"("id") ON DELETE cascade,
  "skill_id" integer NOT NULL REFERENCES "skills"("id") ON DELETE cascade
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "job_skills_job_id_idx" ON "job_skills" USING btree ("job_id");
--> statement-breakpoint

-- Saved jobs
CREATE TABLE IF NOT EXISTS "saved_jobs" (
  "id" serial PRIMARY KEY NOT NULL,
  "user_id" uuid NOT NULL REFERENCES "users"("id") ON DELETE cascade,
  "job_id" integer NOT NULL REFERENCES "jobs"("id") ON DELETE cascade,
  "created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE UNIQUE INDEX IF NOT EXISTS "saved_jobs_user_job_idx" ON "saved_jobs" USING btree ("user_id", "job_id");
--> statement-breakpoint

-- Applications
CREATE TABLE IF NOT EXISTS "applications" (
  "id" serial PRIMARY KEY NOT NULL,
  "user_id" uuid NOT NULL REFERENCES "users"("id") ON DELETE cascade,
  "job_id" integer NOT NULL REFERENCES "jobs"("id") ON DELETE cascade,
  "status" varchar(30) NOT NULL DEFAULT 'PENDING',
  "cover_letter" text,
  "applied_at" timestamp with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE UNIQUE INDEX IF NOT EXISTS "applications_user_job_idx" ON "applications" USING btree ("user_id", "job_id");
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "applications_user_id_idx" ON "applications" USING btree ("user_id");
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "applications_job_id_idx" ON "applications" USING btree ("job_id");
