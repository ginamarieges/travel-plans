#!/bin/bash

set -e

echo "🛑 Stopping Tomcat..."
brew services stop tomcat@9 || true

TOMCAT_BASE="/opt/homebrew/Cellar/tomcat@9/9.0.113/libexec"

echo "🧹 Cleaning Maven target..."
mvn clean

echo "🗑 Removing deployed app (webapps)..."
rm -rf "$TOMCAT_BASE/webapps/travel-plans"
rm -f  "$TOMCAT_BASE/webapps/travel-plans.war"

echo "🧨 Removing compiled JSPs (work directory)..."
rm -rf "$TOMCAT_BASE/work/Catalina/localhost/travel-plans"

echo "✅ Clean finished."
echo "👉 Next steps:"
echo "   mvn -DskipTests package"
echo "   cp target/travel-plans.war $TOMCAT_BASE/webapps/"
echo "   brew services start tomcat@9"
