#!/bin/bash

# Audio Editor Native Library Test Script
# Tests native library compilation and loading

set -e  # Exit on any error

echo "=== Audio Editor Native Library Test ==="

# Get directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
NATIVE_DIR="$PROJECT_DIR/native"

# Test results
TESTS_PASSED=0
TESTS_FAILED=0

# Function to report test result
report_test() {
    local test_name="$1"
    local result="$2"
    local message="$3"
    
    if [ "$result" = "PASS" ]; then
        echo "✓ PASS: $test_name"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo "✗ FAIL: $test_name"
        echo "  Error: $message"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

echo "Test 1: Check native directory exists..."
if [ -d "$NATIVE_DIR" ]; then
    report_test "Native directory exists" "PASS"
else
    report_test "Native directory exists" "FAIL" "Directory not found at $NATIVE_DIR"
    exit 1
fi

echo
echo "Test 2: Check for C++ source files..."
CPP_FILES=$(find "$NATIVE_DIR" -name "*.cpp" -type f | wc -l)
if [ "$CPP_FILES" -gt 0 ]; then
    report_test "C++ source files found" "PASS" "Found $CPP_FILES .cpp files"
else
    report_test "C++ source files found" "FAIL" "No .cpp files found"
fi

echo
echo "Test 3: Check for header files..."
HEADER_FILES=$(find "$NATIVE_DIR" -name "*.h" -type f | wc -l)
if [ "$HEADER_FILES" -gt 0 ]; then
    report_test "Header files found" "PASS" "Found $HEADER_FILES .h files"
else
    report_test "Header files found" "FAIL" "No .h files found"
fi

echo
echo "Test 4: Check C++ compiler availability..."
if command -v g++ &> /dev/null; then
    GCC_VERSION=$(g++ --version | head -n1)
    report_test "C++ compiler available" "PASS" "$GCC_VERSION"
else
    report_test "C++ compiler available" "FAIL" "g++ not found in PATH"
fi

echo
echo "Test 5: Compile native library..."
cd "$NATIVE_DIR"

# Detect platform and set library name
OS=$(uname -s)
case "$OS" in
    Linux*)
        LIB_NAME="libnative.so"
        ;;
    Darwin*)
        LIB_NAME="libnative.dylib"
        ;;
    CYGWIN*|MINGW*|MSYS*)
        LIB_NAME="native.dll"
        ;;
    *)
        report_test "Platform detection" "FAIL" "Unsupported platform: $OS"
        exit 1
        ;;
esac

# Remove existing library
rm -f "$LIB_NAME"

# Compile
if g++ -shared -fPIC -o "$LIB_NAME" *.cpp 2>/dev/null; then
    report_test "Native library compilation" "PASS" "Created $LIB_NAME"
else
    report_test "Native library compilation" "FAIL" "Compilation failed"
    exit 1
fi

echo
echo "Test 6: Check compiled library properties..."
if [ -f "$LIB_NAME" ]; then
    LIB_SIZE=$(du -h "$LIB_NAME" | cut -f1)
    LIB_PERMS=$(ls -l "$LIB_NAME" | cut -d' ' -f1)
    report_test "Library file exists" "PASS" "Size: $LIB_SIZE, Permissions: $LIB_PERMS"
    
    # Check if file is executable
    if [ -x "$LIB_NAME" ]; then
        report_test "Library executable" "PASS"
    else
        report_test "Library executable" "FAIL" "Library is not executable"
        chmod +x "$LIB_NAME"
    fi
else
    report_test "Library file exists" "FAIL" "Library file not found after compilation"
fi

echo
echo "Test 7: Test library loading..."
cd "$PROJECT_DIR"

# Compile a simple test class
cat > TestNativeLoad.java << 'EOF'
public class TestNativeLoad {
    static {
        try {
            System.loadLibrary("native");
            System.out.println("SUCCESS: Native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("FAILED: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("SUCCESS: Test completed");
    }
}
EOF

# Compile and run test
if javac TestNativeLoad.java 2>/dev/null; then
    if java -Djava.library.path=native TestNativeLoad 2>/dev/null; then
        report_test "Library loading test" "PASS" "Library loads and runs successfully"
    else
        report_test "Library loading test" "FAIL" "Library failed to load or run"
    fi
else
    report_test "Library loading test" "FAIL" "Failed to compile test class"
fi

# Cleanup
rm -f TestNativeLoad.java TestNativeLoad.class

echo
echo "Test 8: Check for common compilation issues..."
# Check for undefined symbols (Linux)
if command -v nm &> /dev/null && [ "$OS" = "Linux" ]; then
    UNDEFINED_SYMBOLS=$(nm -D "$NATIVE_DIR/$LIB_NAME" 2>/dev/null | grep " U " | wc -l || echo "0")
    if [ "$UNDEFINED_SYMBOLS" -eq 0 ]; then
        report_test "Symbol check" "PASS" "No undefined symbols"
    else
        report_test "Symbol check" "WARN" "$UNDEFINED_SYMBOLS undefined symbols found"
    fi
fi

echo
echo "=== Test Summary ==="
echo "Tests Passed: $TESTS_PASSED"
echo "Tests Failed: $TESTS_FAILED"
echo "Total Tests:  $((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo "✓ All tests passed! Native library is ready for use."
    exit 0
else
    echo "✗ Some tests failed. Please check the errors above."
    exit 1
fi