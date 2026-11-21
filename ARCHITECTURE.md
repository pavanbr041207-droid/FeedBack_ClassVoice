# Architecture (S-01)

Monolith, server-rendered. Package `com.classvoice`:
- `domain` JPA entities (Role, User, AuditLog, CollegeSetting)
- `repo` Spring Data repositories
- `service` UserService, SetupService, AuditService, CollegeSettingsService
- `security` SecurityConfig, DbUserDetailsService, LoginAttemptService, LoginRateLimitFilter, AuthEventListener, RoleRoutes
- `web` controllers (login, setup, role homes, health), SetupForm, GlobalModelAdvice

Schema: Flyway `db/migration/V1__foundation.sql` (roles, users, college_settings, audit_logs). Never edit an applied migration; add V2+.
Profiles: `dev` (file H2), `test` (memory H2), `prod` (PostgreSQL via env). Browser JS: none yet (CSP allows only same-origin scripts).
Request flow: browser -> Spring Security chain (rate-limit filter, CSRF, form login, role matchers) -> controller -> service -> repo -> DB.
