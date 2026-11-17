#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p target/classes target/test-classes
find src/main/java -name '*.java' > target/sources.txt
javac --release 17 -d target/classes @target/sources.txt
javac --release 17 -cp target/classes -d target/test-classes src/test/java/IntegrationSelfTest.java
java -ea -cp target/classes:target/test-classes IntegrationSelfTest
