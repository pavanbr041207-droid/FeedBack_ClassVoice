#!/usr/bin/env bash
# Quality gate: tool check -> build + tests -> secret scan. Output is evidence for CURRENT_STATUS.md.
set -u
cd "$(dirname "$0")/.."
mkdir -p logs
LOG="logs/check-$(date +%Y%m%d-%H%M%S).log"
FAILED=0

step() {
  local name="$1"; shift
  echo "--- $name" >>"$LOG"
  if "$@" >>"$LOG" 2>&1; then echo "PASS  $name"; else echo "FAIL  $name   (details: $LOG)"; FAILED=1; fi
}

java_21_or_newer() {
  local v
  v="$(java -version 2>&1 | head -1 | sed -E 's/.*"([0-9]+).*/\1/')"
  [ "${v:-0}" -ge 21 ]
}

secret_scan() {
  if grep -rIEn \
      --exclude-dir=target --exclude-dir=.git --exclude-dir=logs --exclude-dir=data --exclude=check.sh \
      -e 'BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY' \
      -e 'AKIA[0-9A-Z]{16}' \
      -e 'ghp_[A-Za-z0-9]{30,}' \
      -e '(password|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*["'"'"']?[A-Za-z0-9/+]{20,}' \
      . ; then
    return 1
  fi
  return 0
}

step "Java 21 or newer installed" java_21_or_newer
step "Maven installed" mvn -v
step "Build + all tests (mvn verify)" mvn -B -q verify
step "Secret scan" secret_scan

echo
if [ "$FAILED" -eq 0 ]; then echo "RESULT: ALL CHECKS PASSED"; else echo "RESULT: FAILED - paste the lines above and the last 60 lines of $LOG"; fi
exit "$FAILED"
