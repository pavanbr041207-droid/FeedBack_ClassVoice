# AGENTS.md — rules for any AI agent working on ClassVoice

Source of truth: `ClassVoice_Master_Development_Prompts.docx` (Part I governance, Part II SRS, Part III libraries/instructions).
This file is the short operating version. If they conflict, the docx wins; record the conflict in `DECISIONS.md`.

## Before work
1. Read `CURRENT_STATUS.md`, `TASKS.md`, `REQUIREMENTS.md`, `DECISIONS.md`, `KNOWN_ISSUES.md`.
2. Inspect the actual repo. Do not trust old notes over code.
3. Present a session proposal (session ID, baseline, tasks + requirement IDs, approach/files, assumptions, risks,
   migrations/credentials/paid services/notifications/deployment/public release, tests, deliverables).
4. WAIT for explicit approval. No code changes before approval.

## During work
- One approved session = one feature branch `feature/sNN-<name>`. Never commit to `main`. Never force-push. Never rewrite history.
- Never silently drop or weaken a requirement. Propose changes in `DECISIONS.md`; user approves.
- Never hardcode secrets, passwords, college-specific names. Server-side validation always. BCrypt only.
- No paid services, no hidden developer access, no fake features/data.
- Status words are evidence-based: PLANNED, IN PROGRESS, IMPLEMENTED — NOT VERIFIED, VERIFIED, BLOCKED, DEFERRED, REJECTED, FUTURE BACKLOG.
  Only VERIFIED with test/run evidence counts as done.
- Loop: Inspect -> Implement -> Lint -> Test -> Build -> Run -> Diagnose -> Repair -> Retest -> Regression -> Document.

## After work
Update `CURRENT_STATUS.md`, `TASKS.md`, `CHANGELOG.md`, `DECISIONS.md`, `TESTING.md`, `KNOWN_ISSUES.md`, `AI_AGENT_LOG.md`.
Run `scripts/check.sh`. Report honestly: what passed, what failed, what was NOT TESTED.

## Project directives from the owner (recorded)
- Final product must be deployable online for free, minimal and light to host.
- Majority of code in Java. JavaScript only for browser interactions.
- Delivery style: ZIP + copy-paste terminal commands for VS Code. Owner pastes outputs back for repair.
- AI-powered analytics on feedback (graphs, charts, futuristic look) is wanted. See REQ-AI-001 / DECISIONS D-005.
