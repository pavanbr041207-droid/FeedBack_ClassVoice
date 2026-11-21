# Current status — 2026-11-21

Session: S-01 Foundation. Branch: `feature/s01-foundation`. Overall: VERIFIED (19/19 tests pass, `scripts/check.sh` all PASS, manual admin setup + login + landing page OK on local H2).

Done: governance docs, Maven/Spring Boot project, Flyway V1 schema, Spring Security (3 roles), First-Admin setup, login + role landing pages, tests, scripts, Dockerfile.
Bug found by tests and fixed: LoginRateLimitFilter used getServletPath(); lockout never triggered in tests. Now uses request URI.
NOT TESTED: PostgreSQL, Docker image, HTTPS cookie flag, hosting proxy IP handling, real teacher/student login in a browser, mobile.
Warnings: see KNOWN_ISSUES.md.
Owner directives: free online hosting, Java-majority, AI analytics (D-005 pending).

Recommended next: S-02 (branding + settings editing + change password).
