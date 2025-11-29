#!/bin/bash

# Audio Editor Project Runner Script
# This script compiles and runs the Audio Editor with correct settings

set -e  # Exit on any error

echo "=== Audio Editor Project Runner ==="

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Change to project directory
cd "$PROJECT_DIR"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java not found. Please install Java 22 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 22 ]; then
    echo "Warning: Java version $JAVA_VERSION detected. Java 22+ is recommended."
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven not found. Please install Maven 3.6+."
    exit 1
fi

# Check if native library exists
NATIVE_LIB=""
OS=$(uname -s)
case "$OS" in
    Linux*)
        NATIVE_LIB="native/libnative.so"
        ;;
    Darwin*)
        NATIVE_LIB="native/libnative.dylib"
        ;;
    CYGWIN*|MINGW*|MSYS*)
        NATIVE_LIB="native/native.dll"
        ;;
esac

if [ ! -f "$NATIVE_LIB" ]; then
    echo "Native library not found at $NATIVE_LIB"
    echo "Building native library..."
    ./scripts/build_native.sh
    if [ ! -f "$NATIVE_LIB" ]; then
        echo "Error: Failed to build native library."
        exit 1
    fi
fi

echo "✓ Native library found: $NATIVE_LIB"

# Compile the project
echo "Compiling Java project..."
if ! mvn clean compile -q; then
    echo "Error: Maven compilation failed."
    exit 1
fi

echo "✓ Java project compiled successfully"

# Set up classpath
CLASSPATH="target/classes"

# Set library path
LIBRARY_PATH="$(pwd)/native"

# JVM options
JVM_OPTS="-Djava.library.path=$LIBRARY_PATH"

# Check if we're in debug mode
if [ "$1" = "--debug" ] || [ "$1" = "-d" ]; then
    JVM_OPTS="$JVM_OPTS -Ddebug=true -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
    echo "Debug mode enabled - JVM will listen on port 5005"
fi

# Check if we're in test mode
if [ "$1" = "--test" ] || [ "$1" = "-t" ]; then
    JVM_OPTS="$JVM_OPTS -Dtest.mode=true"
    echo "Test mode enabled"
fi

echo
echo "=== Starting Audio Editor ==="
echo "Java: $(java -version 2>&1 | head -n1)"
echo "Classpath: $CLASSPATH"
echo "Library Path: $LIBRARY_PATH"
echo "JVM Options: $JVM_OPTS"
echo

# Run the application
java $JVM_OPTS -cp "$CLASSPATH" com.meenigam.Main

# Check exit status
EXIT_CODE=$?
if [ $EXIT_CODE -eq 0 ]; then
    echo "Audio Editor exited normally."
else
    echo "Audio Editor exited with code: $EXIT_CODE"
fi

exit $EXIT_CODE