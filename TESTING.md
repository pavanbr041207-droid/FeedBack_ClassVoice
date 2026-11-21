# Testing

Command: `./scripts/check.sh` (tool check, `mvn verify`, secret scan). Log in `logs/` (git-ignored).

| Class | Covers |
|---|---|
| AuthSecurityTest | anonymous redirect, 3-role login routing, case-insensitive username, wrong password, unknown user, disabled user, CSRF, BCrypt storage, role guards (403), DB-backed college name, lockout after 5 failures, audit rows, /healthz |
| SetupFlowTest | setup open, valid setup creates one admin then closes (404), wrong token, mismatch, short password, token-guess lockout |
| SetupDisabledTest | setup 404 without token |

Status S-01: 19 tests run, 19 PASS (2026-11-21, local macOS, Java 21.0.13, Spring Boot 3.5.0). NOT TESTED: real browsers, mobile, PostgreSQL, Docker, HTTPS, hosting.
NOT TESTED at all: real browsers, mobile, PostgreSQL, Docker image, HTTPS cookie behaviour, hosting.
