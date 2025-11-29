#!/bin/bash

# Audio Editor Master Test Runner
# Runs all test suites and generates a report

set -e  # Exit on any error

echo "=== Audio Editor Master Test Suite ==="
echo "Starting comprehensive test suite..."
echo "Date: $(date)"
echo "Platform: $(uname -s) $(uname -r)"
echo "Java: $(java -version 2>&1 | head -n1)"
echo

# Get directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
REPORT_DIR="$PROJECT_DIR/test-reports"

# Create reports directory
mkdir -p "$REPORT_DIR"

# Test results file
REPORT_FILE="$REPORT_DIR/test_report_$(date +%Y%m%d_%H%M%S).txt"
SUMMARY_FILE="$REPORT_DIR/latest_summary.txt"

# Initialize counters
TOTAL_TESTS=0
TOTAL_PASSED=0
TOTAL_FAILED=0

# Function to run a test suite
run_test_suite() {
    local suite_name="$1"
    local script_path="$2"
    
    echo "Running $suite_name..."
    echo "========================================" >> "$REPORT_FILE"
    echo "$suite_name" >> "$REPORT_FILE"
    echo "========================================" >> "$REPORT_FILE"
    echo "Started: $(date)" >> "$REPORT_FILE"
    echo >> "$REPORT_FILE"
    
    if [ -f "$script_path" ]; then
        # Make script executable
        chmod +x "$script_path"
        
        # Run the test and capture output
        if "$script_path" >> "$REPORT_FILE" 2>&1; then
            echo "✓ $suite_name: PASSED"
            echo "$suite_name: PASSED" >> "$SUMMARY_FILE"
            TOTAL_PASSED=$((TOTAL_PASSED + 1))
        else
            echo "✗ $suite_name: FAILED"
            echo "$suite_name: FAILED" >> "$SUMMARY_FILE"
            TOTAL_FAILED=$((TOTAL_FAILED + 1))
        fi
    else
        echo "✗ $suite_name: SCRIPT NOT FOUND"
        echo "$suite_name: SCRIPT NOT FOUND" >> "$SUMMARY_FILE"
        TOTAL_FAILED=$((TOTAL_FAILED + 1))
    fi
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo >> "$REPORT_FILE"
    echo "Completed: $(date)" >> "$REPORT_FILE"
    echo >> "$REPORT_FILE"
}

# Initialize report files
echo "Audio Editor Test Report" > "$REPORT_FILE"
echo "=========================" >> "$REPORT_FILE"
echo "Generated: $(date)" >> "$REPORT_FILE"
echo "Platform: $(uname -s) $(uname -r)" >> "$REPORT_FILE"
echo "Java: $(java -version 2>&1 | head -n1)" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

echo "Test Suite Summary" > "$SUMMARY_FILE"
echo "=================" >> "$SUMMARY_FILE"
echo "Run Date: $(date)" >> "$SUMMARY_FILE"
echo >> "$SUMMARY_FILE"

# Change to project directory
cd "$PROJECT_DIR"

# Run all test suites
echo "Starting test execution..."
echo

# Test 1: Native Library Tests
run_test_suite "Native Library Tests" "$SCRIPT_DIR/test_native_library.sh"

# Test 2: Audio Playback Tests
run_test_suite "Audio Playback Tests" "$SCRIPT_DIR/test_audio_playback.sh"

# Test 3: Build Tests
echo "Running Build Tests..."
echo "========================================" >> "$REPORT_FILE"
echo "Build Tests" >> "$REPORT_FILE"
echo "========================================" >> "$REPORT_FILE"
echo "Started: $(date)" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

# Test Maven compilation
echo "Testing Maven compilation..." >> "$REPORT_FILE"
if mvn clean compile -q >> "$REPORT_FILE" 2>&1; then
    echo "✓ Build Tests: PASSED"
    echo "Build Tests: PASSED" >> "$SUMMARY_FILE"
    TOTAL_PASSED=$((TOTAL_PASSED + 1))
else
    echo "✗ Build Tests: FAILED"
    echo "Build Tests: FAILED" >> "$SUMMARY_FILE"
    TOTAL_FAILED=$((TOTAL_FAILED + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

echo >> "$REPORT_FILE"
echo "Completed: $(date)" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

# Test 4: Code Quality Checks (if available)
if command -v findbugs &> /dev/null || command -v pmd &> /dev/null; then
    echo "Running Code Quality Tests..."
    echo "========================================" >> "$REPORT_FILE"
    echo "Code Quality Tests" >> "$REPORT_FILE"
    echo "========================================" >> "$REPORT_FILE"
    echo "Started: $(date)" >> "$REPORT_FILE"
    echo >> "$REPORT_FILE"
    
    # Run PMD if available
    if command -v pmd &> /dev/null; then
        echo "Running PMD analysis..." >> "$REPORT_FILE"
        pmd -d src/main/java -f text -R rulesets/java/quickstart.xml >> "$REPORT_FILE" 2>&1 || true
    fi
    
    echo "✓ Code Quality Tests: COMPLETED"
    echo "Code Quality Tests: COMPLETED" >> "$SUMMARY_FILE"
    TOTAL_PASSED=$((TOTAL_PASSED + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    echo >> "$REPORT_FILE"
    echo "Completed: $(date)" >> "$REPORT_FILE"
    echo >> "$REPORT_FILE"
fi

# Test 5: Integration Tests (basic)
echo "Running Integration Tests..."
echo "========================================" >> "$REPORT_FILE"
echo "Integration Tests" >> "$REPORT_FILE"
echo "========================================" >> "$REPORT_FILE"
echo "Started: $(date)" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

# Test application startup
echo "Testing application startup..." >> "$REPORT_FILE"
timeout 10s java -Djava.library.path=native -cp target/classes com.meenigam.Main >> "$REPORT_FILE" 2>&1 || true

if [ $? -eq 124 ]; then
    echo "✓ Integration Tests: PASSED (timeout expected)"
    echo "Integration Tests: PASSED" >> "$SUMMARY_FILE"
    TOTAL_PASSED=$((TOTAL_PASSED + 1))
else
    echo "✗ Integration Tests: FAILED"
    echo "Integration Tests: FAILED" >> "$SUMMARY_FILE"
    TOTAL_FAILED=$((TOTAL_FAILED + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

echo >> "$REPORT_FILE"
echo "Completed: $(date)" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

# Generate final summary
echo "========================================" >> "$REPORT_FILE"
echo "FINAL SUMMARY" >> "$REPORT_FILE"
echo "========================================" >> "$REPORT_FILE"
echo "Total Test Suites: $TOTAL_TESTS" >> "$REPORT_FILE"
echo "Passed: $TOTAL_PASSED" >> "$REPORT_FILE"
echo "Failed: $TOTAL_FAILED" >> "$REPORT_FILE"
echo "Success Rate: $(( TOTAL_PASSED * 100 / TOTAL_TESTS ))%" >> "$REPORT_FILE"
echo >> "$REPORT_FILE"

echo "Test Suite Summary" >> "$SUMMARY_FILE"
echo "=================" >> "$SUMMARY_FILE"
echo "Total Test Suites: $TOTAL_TESTS" >> "$SUMMARY_FILE"
echo "Passed: $TOTAL_PASSED" >> "$SUMMARY_FILE"
echo "Failed: $TOTAL_FAILED" >> "$SUMMARY_FILE"
echo "Success Rate: $(( TOTAL_PASSED * 100 / TOTAL_TESTS ))%" >> "$SUMMARY_FILE"
echo >> "$SUMMARY_FILE"

# Display results
echo
echo "=== Test Suite Results ==="
echo "Total Test Suites: $TOTAL_TESTS"
echo "Passed: $TOTAL_PASSED"
echo "Failed: $TOTAL_FAILED"
echo "Success Rate: $(( TOTAL_PASSED * 100 / TOTAL_TESTS ))%"
echo

if [ $TOTAL_FAILED -eq 0 ]; then
    echo "🎉 All test suites passed!"
    echo "The Audio Editor is ready for deployment."
else
    echo "⚠️  Some test suites failed."
    echo "Please review the detailed report: $REPORT_FILE"
fi

echo
echo "=== Reports Generated ==="
echo "Detailed Report: $REPORT_FILE"
echo "Summary Report: $SUMMARY_FILE"
echo

# Show failed tests if any
if [ $TOTAL_FAILED -gt 0 ]; then
    echo "Failed Test Suites:"
    grep "FAILED" "$SUMMARY_FILE" | grep -v "Test Suite Summary"
    echo
    echo "Please check the detailed report for more information."
fi

# Exit with appropriate code
if [ $TOTAL_FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi