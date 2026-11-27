#!/bin/sh
set -eu
if command -v mvn >/dev/null 2>&1; then exec mvn "$@"; fi
MVN_DIR="${HOME}/.m2/wrapper/apache-maven-3.9.9"
if [ ! -x "$MVN_DIR/bin/mvn" ]; then
  mkdir -p "${HOME}/.m2/wrapper"
  ARCHIVE="${HOME}/.m2/wrapper/maven.zip"
  if command -v curl >/dev/null 2>&1; then curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip -o "$ARCHIVE"; else wget -q https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip -O "$ARCHIVE"; fi
  unzip -oq "$ARCHIVE" -d "${HOME}/.m2/wrapper"; rm -f "$ARCHIVE"
fi
exec "$MVN_DIR/bin/mvn" "$@"
