# Security

Implemented in S-01 (unverified until tests run): BCrypt hashing (strength 12), CSRF on all POSTs, HttpOnly + SameSite=Lax session cookie (Secure in prod),
session fixation migration, logout invalidates session, login lockout (5 failures -> 15 min, per ip+username), CSP header (same-origin only),
server-side role matchers, generic login error (no user enumeration), audit log without usernames or passwords, one-time setup token compared in constant time.
Secrets: environment variables only. `.env` git-ignored. `scripts/check.sh` runs a basic secret scan (not a replacement for a real scanner).
Report vulnerabilities to the project owner. No hidden accounts exist.
