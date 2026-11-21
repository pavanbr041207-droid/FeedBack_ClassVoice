#!/usr/bin/env bash
# Run ClassVoice locally (profile "dev", file-based H2, no database install needed).
set -euo pipefail
cd "$(dirname "$0")/.."
if [ -z "${CLASSVOICE_SETUP_TOKEN:-}" ]; then
  CLASSVOICE_SETUP_TOKEN="$(openssl rand -hex 16)"
  export CLASSVOICE_SETUP_TOKEN
fi
echo "=============================================================="
echo " Open:  http://localhost:8080/setup"
echo " Setup token (this run only): $CLASSVOICE_SETUP_TOKEN"
echo "=============================================================="
exec mvn -B spring-boot:run
