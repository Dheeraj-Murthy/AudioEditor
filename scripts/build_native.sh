#!/bin/bash

# Audio Editor Native Library Build Script
# This script compiles the C++ native library for the current platform

set -e  # Exit on any error

echo "=== Audio Editor Native Library Build Script ==="
echo "Building native library for platform: $(uname -s)"

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
NATIVE_DIR="$PROJECT_DIR/native"

# Check if native directory exists
if [ ! -d "$NATIVE_DIR" ]; then
    echo "Error: Native directory not found at $NATIVE_DIR"
    exit 1
fi

# Change to native directory
cd "$NATIVE_DIR"

# Detect operating system
OS=$(uname -s)
case "$OS" in
    Linux*)
        echo "Detected Linux platform"
        LIB_NAME="libnative.so"
        COMPILER="g++"
        COMPILER_FLAGS="-shared -fPIC -O2"
        ;;
    Darwin*)
        echo "Detected macOS platform"
        LIB_NAME="libnative.dylib"
        COMPILER="g++"
        COMPILER_FLAGS="-shared -fPIC -O2"
        ;;
    CYGWIN*|MINGW*|MSYS*)
        echo "Detected Windows platform"
        LIB_NAME="native.dll"
        COMPILER="g++"
        COMPILER_FLAGS="-shared -O2"
        ;;
    *)
        echo "Error: Unsupported platform: $OS"
        exit 1
        ;;
esac

# Check if compiler is available
if ! command -v "$COMPILER" &> /dev/null; then
    echo "Error: $COMPILER not found. Please install C++ compiler."
    echo "On Ubuntu/Debian: sudo apt install build-essential"
    echo "On macOS: xcode-select --install"
    echo "On Windows: Install MinGW-w64"
    exit 1
fi

echo "Using compiler: $COMPILER"
echo "Compiler flags: $COMPILER_FLAGS"
echo "Output library: $LIB_NAME"

# Find all C++ source files
CPP_FILES=$(find . -name "*.cpp" -type f)
if [ -z "$CPP_FILES" ]; then
    echo "Error: No C++ source files found in $NATIVE_DIR"
    exit 1
fi

echo "Source files found:"
echo "$CPP_FILES"
echo

# Compile the native library
echo "Compiling native library..."
$COMPILER $COMPILER_FLAGS -o "$LIB_NAME" $CPP_FILES

# Check if compilation was successful
if [ $? -eq 0 ] && [ -f "$LIB_NAME" ]; then
    echo "✓ Native library compiled successfully!"
    echo "Library location: $NATIVE_DIR/$LIB_NAME"
    echo "Library size: $(du -h "$LIB_NAME" | cut -f1)"
    
    # Set appropriate permissions
    chmod 755 "$LIB_NAME"
    echo "✓ Library permissions set"
    
    # Display library information
    if command -v file &> /dev/null; then
        echo "Library type: $(file "$LIB_NAME")"
    fi
    
else
    echo "✗ Native library compilation failed!"
    exit 1
fi

echo
echo "=== Build Summary ==="
echo "Platform: $OS"
echo "Library: $LIB_NAME"
echo "Size: $(du -h "$LIB_NAME" | cut -f1)"
echo "Location: $NATIVE_DIR/$LIB_NAME"
echo "Status: ✓ Ready for use"
echo

# Instructions for running the application
echo "=== Next Steps ==="
echo "To run the Audio Editor application:"
echo "1. Make sure you have Java 22+ installed"
echo "2. Compile the Java code: mvn clean compile"
echo "3. Run with: java -Djava.library.path=native -cp target/classes com.meenigam.Main"
echo "   Or use: ./scripts/run_project.sh"
echo

exit 0