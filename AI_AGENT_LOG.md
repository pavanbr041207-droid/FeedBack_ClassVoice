# AI agent log
## 2026-11-21 — S-01 Foundation — Claude
- Read docx (Parts I-III). Proposal approved by owner with directives (free online hosting, Java-majority, AI analytics later, ZIP workflow).
- Created project from empty baseline. Wrote code, tests, docs, scripts.
- Could not build: no Maven Central access in sandbox, no Maven installed. Nothing executed. Status = IMPLEMENTED — NOT VERIFIED.
- Added beyond approved list: Dockerfile, `/healthz`, `scripts/run-dev.sh`, H2 dev/test profiles (D-001). Flagged for review.
- Not done: any deployment, AI analytics, branding editing.
- Self-review found 1 bug before delivery: SetupService audit row (separate transaction) would reference an uncommitted admin -> FK failure. Fixed by removing the outer transaction. Not proven by test run.
- 2026-11-21: Owner ran check.sh. First run: 18/19 pass; loginIsLockedAfterFiveFailures failed (filter path check). Fixed LoginRateLimitFilter. Second run: all PASS. S-01 marked VERIFIED.
