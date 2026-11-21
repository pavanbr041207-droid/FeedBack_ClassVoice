# Requirements register

Source: SRS (Part II of the docx). IDs are stable. Statuses are evidence-based. Nothing is VERIFIED until `scripts/check.sh` output is provided.

| ID | Requirement | SRS § | Class | Milestone | Status |
|---|---|---|---|---|---|
| OBJ-001 | Single-college, multi-department feedback system | 1 | CORE | all | IN PROGRESS |
| STK-001 | Java 21, Spring Boot, Thymeleaf, PostgreSQL, Maven, JPA, Security | 2 | CORE | 1 | VERIFIED (tests + local run, H2) |
| STK-002 | Majority of code in Java (owner directive) | 2, owner | CORE | all | IN PROGRESS |
| EDT-001 | Editable/renameable entities, stable internal IDs, historical protection, images | 3 | CORE | 1-2 | PARTIAL: stable IDs yes; edit UI PLANNED (S-02) |
| ROL-001 | Three roles with server-side permission enforcement | 4 | CORE | 1 | VERIFIED (tests + local run, H2) |
| ADM-001 | Admin capabilities (full list) | 4.1 | CORE | 1-7 | PLANNED (only landing page now) |
| TCH-001 | Teacher capabilities | 4.2 | CORE | 4-6 | PLANNED |
| STU-001 | Student capabilities | 4.3 | CORE | 3-6 | PLANNED |
| ACA-001 | Departments, years, semesters, sections, subjects, teaching assignments | 5 | CORE | 2 | PLANNED |
| CAL-001 | Academic calendar + approved semester promotion | 6 | CORE | 2-3 | PLANNED |
| STM-001 | Student records, operations, graduation, discontinuation, bulk upload validation | 7 | CORE | 3 | PLANNED |
| SAU-001 | Student auth: login, initial setup, remember login, single-device, recovery | 8.1-8.5 | CORE | 3 | PLANNED |
| SAU-002 | Password security: BCrypt, no plaintext | 8.6, 27.9 | CORE | 1 | VERIFIED (tests + local run, H2) |
| TRG-001 | Teacher registration + approval | 9 | CORE | 4 | PLANNED (status enum PENDING_APPROVAL exists) |
| FQB-001 | Feedback question bank | 10 | CORE | 5 | PLANNED |
| FSC-001 | Feedback session creation | 11 | CORE | 5 | PLANNED |
| QRC-001 | QR code + session sharing (ZXing) | 12 | CORE | 5 | PLANNED |
| FWF-001 | Student feedback workflow | 13 | CORE | 5 | PLANNED |
| FCI-001 | Confidentiality + identity protection | 14 | CORE | 5 | PLANNED |
| FVL-001 | Visibility + session lifecycle | 15 | CORE | 5 | PLANNED |
| NTF-001 | Student notifications | 16 | CORE | 6 | PLANNED |
| SPT-001 | Student portal | 17 | CORE | 3-5 | PLANNED |
| TDB-001 | Teacher dashboard | 18 | CORE | 4-6 | PLANNED |
| RPT-001 | Reports + analytics, historical reports, PDF/Excel export | 19 | CORE | 6 | PLANNED |
| ADB-001 | Admin dashboard | 20 | CORE | 1-7 | PLANNED |
| UXD-001 | Responsive, clean design | 21, 27.12 | CORE | all | IN PROGRESS (login/setup/home pages only) |
| SEC-001 | Spring Security, role-based authorization | 22 | CORE | 1 | VERIFIED (tests + local run, H2) |
| SEC-002 | CSRF, secure session cookies, session invalidation on logout | 22 | CORE | 1 | VERIFIED (tests + local run, H2) (secure cookie only effective over HTTPS) |
| SEC-003 | Login rate limiting | 22 | CORE | 1 | VERIFIED (tests + local run, H2) (in-memory, see KNOWN_ISSUES) |
| SEC-004 | Input validation, XSS escaping, SQL-injection safe ORM | 22 | CORE | 1+ | IN PROGRESS (setup/login only) |
| SEC-005 | Audit logging | 22, 24 | CORE | 1, 7 | PARTIAL: table + login/setup events only |
| SEC-006 | Secure file upload validation | 22 | CORE | 2 | PLANNED |
| SEC-007 | First-Admin onboarding, no hardcoded production password | 27.19 | CORE | 1 | VERIFIED (tests + local run, H2) |
| SEC-008 | No hidden developer access | 27.11 | CORE | all | IN PROGRESS (none exists) |
| DB-001 | Real persistence, constraints, migration scripts | 23, 27.7, 27.17 | CORE | 1 | VERIFIED (tests + local run, H2) |
| BKP-001 | Backup, restore, deployment, handover docs | 24 | CORE | 7 | PLANNED |
| TST-001 | JUnit 5 / Spring Boot Test, acceptance criteria | 25 | CORE | all | IN PROGRESS |
| DEP-001 | Free online hosting, minimal footprint (owner directive) | 24, owner | CORE | 7 | PARTIAL: env config, prod profile, Dockerfile (UNTESTED), /healthz. Provider NOT chosen. |
| AI-001 | AI-assisted feedback analytics: insights, charts, futuristic UI | 19, owner | FUTURE BACKLOG (pending approval) | 6 | FUTURE BACKLOG |
