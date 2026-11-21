# Master plan (SRS §26 milestones)

| M | Scope | Sessions (planned) |
|---|---|---|
| 1 Foundation | Project setup, DB, auth, roles, branding | S-01 setup+DB+auth+roles+onboarding; S-02 branding/settings editing |
| 2 Academic management | Departments, years, semesters, sections, subjects, calendar, teaching assignments | S-03.. |
| 3 Student management | Import, profiles, portal, lifecycle, transfers, promotion | later |
| 4 Teacher management | Registration, approval, dashboard, assignments | later |
| 5 Feedback engine | Question bank, sessions, QR (ZXing), eligibility, submission, lifecycle | later |
| 6 Reports + notifications | Analytics, historical reports, PDF/Excel export, notifications. AI analytics candidate (D-005, pending approval) | later |
| 7 Security + deployment | Security review, audit, backup/restore, free online deployment, acceptance tests | later |

Deployment readiness is built in from S-01 (env-based config, prod profile, Dockerfile, `/healthz`), but actual hosting
provider is chosen and verified in M7 (D-002). No public release before explicit approval.
