#!/usr/bin/env bash
set -e

BRANCH=$(git branch --show-current)

echo "==> Deploying branch '$BRANCH' to Heroku main..."

# Step 1: set version.http4k=LOCAL in versions.properties
sed -i '' 's/^version\.http4k=.*/version.http4k=LOCAL/' versions.properties

# Step 2: remove libs/.gitignore so the artifacts can be committed
rm libs/.gitignore

# Step 3: stage everything and commit
git add libs/ versions.properties
git commit -m "chore: add libs and set http4k version to LOCAL for heroku deploy"

# Step 4: push current branch to heroku main
git push heroku "$BRANCH:main" --force

# Step 5: reset back to pre-deploy state
echo "==> Resetting deploy commit..."
git reset HEAD~1

# Restore libs/.gitignore and versions.properties
git checkout -- libs/.gitignore versions.properties

echo "==> Done."
