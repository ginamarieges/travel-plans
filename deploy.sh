#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
WAR_NAME="travel-plans"
TOMCAT_WEBAPPS="/opt/homebrew/Cellar/tomcat@9/9.0.113/libexec/webapps"

echo "1) Compilando y empaquetando WAR..."
cd "$PROJECT_ROOT"
mvn -q -DskipTests package

echo "2) Copiando WAR a Tomcat..."
cp "$PROJECT_ROOT/target/${WAR_NAME}.war" "$TOMCAT_WEBAPPS/"

echo "3) Hecho. Abre: http://localhost:8080/${WAR_NAME}/"
