#!/bin/bash
set -e

NEW_VERSION=$1

sed -i.bak "s/^http4k = \".*\"/http4k = \"$NEW_VERSION\"/" gradle/libs.versions.toml && rm gradle/libs.versions.toml.bak
