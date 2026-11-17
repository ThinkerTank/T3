#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [ -d .git ]; then
  echo "This folder is already a Git repository. Review status and publish manually." >&2
  exit 1
fi
git init -b main
git remote add origin https://github.com/ThinkerTank/T3.git
git add .
git commit -m "feat: initial SGMMS application, structures, graph, tests and docs"
git push -u origin main
git switch -c dev
git push -u origin dev
printf '\nInitial main/dev branches published. Create feature branches and PRs for subsequent changes.\n'
