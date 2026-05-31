#!/usr/bin/env bash
set -e

SRC=~/.m2/repository/org/http4k
DEST=libs/org/http4k

MODULES=(
  http4k-api-jsonschema
  http4k-bom
  http4k-client-okhttp
  http4k-config
  http4k-core
  http4k-format-core
  http4k-format-jackson
  http4k-format-moshi
  http4k-realtime-core
  http4k-security-core
  http4k-security-oauth
  http4k-server-undertow
  http4k-testing-hamkrest
)

echo "==> Clearing $DEST..."
rm -rf "$DEST"

echo "==> Copying required LOCAL http4k artifacts..."
for module in "${MODULES[@]}"; do
  src="$SRC/$module/LOCAL"
  dst="$DEST/$module/LOCAL"
  if [ ! -d "$src" ]; then
    echo "    MISSING: $module (not found in ~/.m2)"
    continue
  fi
  mkdir -p "$dst"
  for f in "$src"/*; do
    filename=$(basename "$f")
    case "$filename" in
      *-javadoc.jar|*-test-fixtures-sources.jar)
        ;;
      *)
        cp "$f" "$dst/"
        ;;
    esac
  done
  echo "    Copied $module"
done

echo "==> Done."
