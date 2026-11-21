# Decisions

| ID | Decision | Status | Notes |
|---|---|---|---|
| D-001 | Local dev uses file-based H2 in PostgreSQL mode; tests use in-memory H2; production uses PostgreSQL | PROPOSED (assumed in S-01 to avoid owner needing a DB install) | Deviation risk: SQL must stay portable; first PostgreSQL run happens at deployment. Reversible. |
| D-002 | Hosting: Java cannot run on Vercel's JS-focused platform (as far as known). Candidate: Docker-based free web host + free hosted PostgreSQL. Provider NOT chosen; limits NOT verified. | OPEN — verify in M7 | Free tiers usually sleep when idle and change terms. Must verify current limits before choosing. |
| D-003 | Flyway for migrations (SRS rule 27.17 requires migration scripts) | PROPOSED, owner OK requested in S-01 proposal | Free, open source. |
| D-004 | `spring.jpa.hibernate.ddl-auto=none`; schema owned by Flyway only | ACCEPTED (S-01) | Entity/schema mismatches are caught by tests, not by startup validation. |
| D-005 | AI analytics: compute stats in Java; AI layer generates narrative insights; charts rendered in browser | OPEN — needs approval | Open issues: free AI provider terms, student/teacher privacy (SRS §14 confidentiality — sending feedback text to an external AI needs explicit approval and anonymisation), cost Rs 0. Alternative: rule-based/local analysis with no external service. |
| D-006 | Spring Boot pinned at 3.5.0 | ACCEPTED (S-01) | Verify latest supported version and advisories before M7. |
| D-007 | Username stored lowercase; login is case-insensitive | ACCEPTED (S-01) | Revisit for student ID format in M3. |
| D-008 | Setup token comes only from env `CLASSVOICE_SETUP_TOKEN`; setup page 404s when token missing or admin exists | ACCEPTED (S-01) | |
