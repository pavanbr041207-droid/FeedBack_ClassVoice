# Known issues / limits
1. S-01 build and tests were NOT executed by the agent (Maven Central unreachable from sandbox). Compile errors are possible. Owner runs `scripts/check.sh`; agent repairs from pasted output.
2. Dev/test use H2, production PostgreSQL (D-001). First PostgreSQL run is untested.
3. Login/setup lockout is in memory: resets on restart, per instance. Lockout by ip+username can be used to annoy one user from one IP only.
4. Client IP behind a hosting proxy is NOT verified (`forward-headers-strategy: native`). Lockout/audit IPs may show proxy IP.
5. Dockerfile untested. No Maven wrapper (needs `brew install maven`).
6. No password-strength list/breach check beyond min length 10. No password change/reset yet.
7. Spring Boot 3.5.0 pinned; check for newer patch/advisories.
8. Placeholder role pages only; no feature beyond auth exists.
9. `ddl-auto=none`: mapping mistakes surface in tests/runtime, not at startup.
10. Setup race: two simultaneous setup submissions could both pass the "no admin yet" check. Needs token holder + exact timing; low risk, not fixed.
1a. RESOLVED 2026-11-21: build and tests now executed locally by owner, 19/19 pass. Issues 2-10 still open.
