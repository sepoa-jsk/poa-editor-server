#!/bin/sh
# Maven Wrapper Script - Linux/Mac/Git Bash
# Licensed to the Apache Software Foundation (ASF)

set -e

# ── Java 확인 ─────────────────────────────────────────────────────────────────
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi
if ! command -v "$JAVACMD" > /dev/null 2>&1; then
    echo "ERROR: Java를 찾을 수 없습니다. JAVA_HOME을 설정하거나 Java 17을 설치하세요." >&2
    exit 1
fi

# ── 프로젝트 루트 탐색 (심볼릭 링크 추적) ────────────────────────────────────
PRG="$0"
while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG="$(dirname "$PRG")/$link"
    fi
done
BASE_DIR=$(cd "$(dirname "$PRG")" && pwd)

# ── maven-wrapper.properties 읽기 ────────────────────────────────────────────
WRAPPER_PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
if [ ! -r "$WRAPPER_PROPS" ]; then
    echo "ERROR: $WRAPPER_PROPS 파일을 찾을 수 없습니다." >&2
    exit 1
fi
DISTRIBUTION_URL=$(grep -E '^distributionUrl=' "$WRAPPER_PROPS" | cut -d= -f2-)

# ── Maven 캐시 경로 ───────────────────────────────────────────────────────────
DIST_FILENAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_HOME="$HOME/.m2/wrapper/dists/$DIST_FILENAME"

# ── Maven 다운로드 (캐시 없을 때만) ──────────────────────────────────────────
if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Maven 다운로드 중: $DISTRIBUTION_URL"
    DISTS_DIR="$HOME/.m2/wrapper/dists"
    mkdir -p "$DISTS_DIR"
    TMP_ZIP="$DISTS_DIR/maven-download.zip"

    if command -v curl > /dev/null 2>&1; then
        curl -fL --progress-bar -o "$TMP_ZIP" "$DISTRIBUTION_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q --show-progress -O "$TMP_ZIP" "$DISTRIBUTION_URL"
    else
        echo "ERROR: curl 또는 wget이 필요합니다." >&2
        exit 1
    fi

    unzip -q "$TMP_ZIP" -d "$DISTS_DIR/"
    rm -f "$TMP_ZIP"
    echo "Maven 설치 완료: $MAVEN_HOME"
fi

# ── Maven 실행 ────────────────────────────────────────────────────────────────
exec "$MAVEN_HOME/bin/mvn" "$@"
