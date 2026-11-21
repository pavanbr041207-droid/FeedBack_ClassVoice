# Setup

## Mac prerequisites (once)
    brew install openjdk@21 maven git
    sudo ln -sfn "$(brew --prefix openjdk@21)/libexec/openjdk.jdk" /Library/Java/JavaVirtualMachines/openjdk-21.jdk
    java -version    # must say 21

## Run locally (no database install)
    ./scripts/run-dev.sh
Open http://localhost:8080/setup, use the token printed in the terminal, create the admin, then sign in at /login.
Local data lives in `data/` (git-ignored). Delete the folder to reset.

## Check everything
    ./scripts/check.sh

## Production (later)
Set env vars from `.env.example`. Never commit real values.

## Push to GitHub (owner action)
    git init -b main
    git add .gitignore README.md
    git commit -m "chore: initial commit"
    git remote add origin https://github.com/pavanbr041207-droid/FeedBack_ClassVoice.git
    git push -u origin main
    git checkout -b feature/s01-foundation
    git add .
    git commit -m "feat(s01): foundation - auth, roles, setup, schema, docs"
    git push -u origin feature/s01-foundation
Then open a Pull Request on GitHub and merge it yourself after review.
