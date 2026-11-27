# Stacklab Technologies API

Spring Boot 3.3 / Java 21 REST API for users, projects, tasks, notifications, audit history, and operational analytics. PostgreSQL is the production database; the default `dev` profile uses in-memory H2 in PostgreSQL compatibility mode and needs no Docker.

Repository: https://github.com/narasimhuluchakali739-gif/stack-lab-assign-ment-

## Prerequisites and run

- Java 21 JDK. If Temurin is installed in its default folder, set it for the current PowerShell session with the commands below; in a new terminal, repeat the setup unless `JAVA_HOME` is configured in Windows Environment Variables.
- No Maven installation is required: `mvnw.cmd` downloads Maven 3.9.9 to the user Maven cache on first use when Maven is not installed.
- Copy `.env.example` to `.env` and set `JWT_SECRET` to a random value with at least 32 bytes. The wrapper does not automatically load `.env`; export its values in your shell or use your preferred env-file loader.

Run locally:

```powershell
$jdk = Get-ChildItem "$env:ProgramFiles\Eclipse Adoptium" -Directory -Filter 'jdk-21*' | Select-Object -First 1
if (-not $jdk) { throw "JDK not found under $env:ProgramFiles\Eclipse Adoptium" }
$env:JAVA_HOME = $jdk.FullName
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
$env:SPRING_PROFILES_ACTIVE='dev'
$env:DEMO_SEED_PASSWORD='set-a-unique-password-at-least-12-chars'
.\mvnw.cmd spring-boot:run
```

The optional demo seed is idempotent and creates `admin@stacklab.local`, `maya@stacklab.local`, `eli@stacklab.local`, and `sam@stacklab.local` using the `DEMO_SEED_PASSWORD` value. It only seeds an empty user database. It is intended for local/demo profiles; never reuse that password in production. Without that variable, no sample accounts are created.

## Database setup, migrations, and demo data

- **Local/dev:** no database installation is needed. The `dev` profile starts H2 in PostgreSQL compatibility mode. Flyway runs `V1__init_schema.sql` and `V2__indexes.sql` automatically on application startup. The H2 database is in memory, so stopping the app clears the data and the next startup reruns migrations and the optional seed.
- **Demo seed:** set `DEMO_SEED_PASSWORD` before starting the app. On an empty H2 database the seeder creates an Admin, Manager, two Employees, a Demo Platform project, tasks covering overdue/in-progress/review/completed/to-do states, an assignment notification, and activity history. The same password is assigned to the four demo accounts. Seed data is skipped when `admin@stacklab.local` already exists.
- **PostgreSQL:** create an empty PostgreSQL database and a database user with schema/table creation rights. Set `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and optionally `DB_POOL_SIZE` / `DB_POOL_MIN_IDLE`, then run the app. Flyway applies pending migrations during startup; inspect the `flyway_schema_history` table and startup logs to confirm the schema version. The demo seeder is disabled in `prod`.

Flyway configuration and migration files live in `src/main/resources/db/migration`. Migrations are intentionally applied by the application at startup; this project does not configure a separate Maven Flyway plugin.

## API docs and tests

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI document: live JSON at `http://localhost:8080/v3/api-docs`; complete generated YAML and JSON snapshots are in `docs/openapi.yaml` and `docs/openapi.json`.
- Postman collection: import `postman/stacklab_collection.json`, set `baseUrl` and `demoPassword` to the `DEMO_SEED_PASSWORD` value, then run the Auth requests before the manager/admin workflows. The collection includes user/project/task lifecycle, activity, notifications, analytics, RBAC, and a stale-version 409 flow.
- Run tests: `.\mvnw.cmd test` (Windows) or `./mvnw test` (Linux/macOS). Tests use the `dev` H2 database and do not require Docker.

## Roles and API surface

- Public: `POST /api/auth/register` (public registration always creates an EMPLOYEE), `POST /api/auth/login`.
- ADMIN: `/api/users` create/list/get/update/deactivate, analytics, and all manager actions.
- MANAGER: project create/update/archive (send `archived: true`), task create/edit/assign/delete/filter, analytics.
- EMPLOYEE: read tasks assigned to their own account and update their progress status; attempts to access another employee's task or change restricted task fields return 403.
- All active authenticated roles can list their own notifications and mark them read. Task and project activity history is available from `/{id}/activity`.
- List endpoints accept standard Spring `page`, `size`, and `sort` parameters. Task filters include `status`, `priority`, `assigneeId`, `projectId`, `overdue`, and title substring `q`.
- Error responses have `timestamp`, `status`, `error`, `message`, and `path` fields. Validation failures return 400, missing resources 404, stale updates 409, and authorization errors 401/403.

## Workflows and concurrency

Tasks use an optimistic `@Version` column. Every task update must include the `version` observed by the client. A stale value is rejected with 409 and a reload/retry message; the database version check also protects against a race between request validation and commit. Task completion sets `completedAt` once, moving back out of `COMPLETED` clears it, and changes are written to `activity_logs`. Reassignment creates an in-app notification. The scheduled overdue service flags newly late, incomplete tasks once, logs the transition, and creates a notification. API reads calculate `isOverdue` directly from deadline/status so the response stays accurate even between scheduler runs.

## Architecture and query performance

The project separates controllers, DTOs, entities, services, repositories, security, configuration, migrations, and scheduling under `com.stacklab`. The database schema is controlled by Flyway (`V1__init_schema.sql`, `V2__indexes.sql`); Hibernate validates it rather than generating production DDL. Task detail reads use a join fetch for assignee/project to avoid N+1 access. List endpoints use pageable JPA Specifications and task response DTOs; analytics use database aggregation queries rather than loading all task rows. Composite indexes cover assignee/status, project/status, deadline/status, priority/deadline, audit history, notifications, and manager/archive filters.

## Scaling plan for roughly 1,000 simultaneous users

Run stateless API replicas behind a load balancer and size HikariCP from measured connection demand and PostgreSQL limits (start near 20 connections per instance, cap aggregate pools below database capacity, and tune timeouts/idle limits using production metrics). Route read-heavy reporting/list traffic to PostgreSQL replicas after defining acceptable replica lag; partition very large task and activity tables by time when retention and query patterns justify it, and validate indexes with query plans. Add Redis for carefully invalidated hot reference/cache data and distributed rate limits. Use ShedLock or a database-backed distributed lease for scheduled jobs before running multiple API replicas, so overdue notifications are emitted once. Export structured logs, traces, request/error/latency metrics, JVM and connection-pool metrics, and database health signals to centralized observability with alerting.

## Demo walkthrough checklist

Use this sequence as a short submission recording walkthrough. Start with the seeded `dev` database and use `DEMO_SEED_PASSWORD` for the demo accounts.

1. Start the `dev` profile with a demo seed password; sign in as admin, manager, and employee.
2. Register a new account and verify public self-registration grants only EMPLOYEE; show login and token-authenticated access.
3. As admin, create/update/deactivate a user. Verify a deactivated account cannot log in and its existing token no longer authenticates.
4. As manager, create a project and task, assign/reassign the task, and show the assignee notification.
5. As employee, view and update an assigned task's status. Try a different employee's task and a restricted field to demonstrate 403.
6. Complete a task and show `completedAt` plus activity history; move it back to work and confirm `completedAt` clears.
7. Inspect seeded overdue work and analytics breakdown/performance endpoints.
8. Submit two updates with the same task version; the later request receives 409 and tells the client to reload.

## Assumptions and current scope

Task versions are mandatory for updates, while other task fields may be omitted to preserve their current values. Registration is self-service employee-only; admins create elevated accounts. Manager operations are role-based (any MANAGER may manage projects/tasks). Task activity endpoints are authenticated; employee task ownership is enforced on task detail/update operations. The API provides bearer JWT auth and a demo-only seed password; production secret rotation, refresh tokens, mail delivery, per-manager project scoping, and distributed scheduler locking are operational extensions and are not implemented here. The checked-in OpenAPI files are generated snapshots; the live generated API docs are authoritative.
