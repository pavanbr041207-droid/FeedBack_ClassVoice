# Tasks

## S-01 Foundation (branch `feature/s01-foundation`) — status: VERIFIED (tests + local run, H2)
- [x] T1 Governance docs
- [x] T2 Maven project, Java 21, Spring Boot, Thymeleaf, JPA, Validation, Security
- [x] T3 Schema v1 via Flyway: roles, users, college_settings, audit_logs
- [x] T4 Spring Security: 3 roles, BCrypt, CSRF, secure cookie config, login rate limit, server-side role guards
- [x] T5 First-Admin onboarding via one-time setup token (no hardcoded password)
- [x] T6 Login + 3 honest placeholder role pages
- [x] T7 Automated tests written (3 test classes)
- [ ] Owner runs `scripts/check.sh` locally and pastes result -> then mark VERIFIED
- [ ] Owner pushes branch, reviews, merges (owner action)

## Next (needs approval)
- S-02: college branding + settings editing (admin), logo upload validation, audit of changes. Then M2.
