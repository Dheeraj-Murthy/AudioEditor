#!/bin/bash

# Audio Editor Audio Playback Test Script
# Tests audio playback functionality

set -e  # Exit on any error

echo "=== Audio Editor Audio Playback Test ==="

# Get directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

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

# Function to check if audio system is available
check_audio_system() {
    if command -v afplay &> /dev/null; then
        echo "macOS audio system detected"
        return 0
    elif command -v aplay &> /dev/null; then
        echo "Linux audio system detected"
        return 0
    elif command -v powershell &> /dev/null; then
        echo "Windows audio system detected"
        return 0
    else
        echo "Warning: Could not detect audio system"
        return 1
    fi
}

echo "Test 1: Check audio system availability..."
if check_audio_system; then
    report_test "Audio system available" "PASS"
else
    report_test "Audio system available" "WARN" "Could not detect audio system"
fi

echo
echo "Test 2: Check for test audio files..."
TEST_AUDIO_DIR="$PROJECT_DIR/tests/audio"
if [ ! -d "$TEST_AUDIO_DIR" ]; then
    mkdir -p "$TEST_AUDIO_DIR"
    echo "Created test audio directory: $TEST_AUDIO_DIR"
fi

# Look for existing test files
TEST_FILES=$(find "$TEST_AUDIO_DIR" -name "*.wav" 2>/dev/null | wc -l)
if [ "$TEST_FILES" -gt 0 ]; then
    report_test "Test audio files found" "PASS" "Found $TEST_FILES WAV files"
else
    report_test "Test audio files found" "WARN" "No test WAV files found in $TEST_AUDIO_DIR"
    echo "  Please add test WAV files to $TEST_AUDIO_DIR"
fi

echo
echo "Test 3: Check Java Sound API availability..."
cd "$PROJECT_DIR"

# Create a simple Java test to check audio system
cat > TestAudioSystem.java << 'EOF'
import javax.sound.sampled.*;
import java.io.File;

public class TestAudioSystem {
    public static void main(String[] args) {
        try {
            // Test mixer info
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            System.out.println("Available mixers: " + mixers.length);
            
            boolean hasClip = false;
            for (Mixer.Info mixer : mixers) {
                Mixer m = AudioSystem.getMixer(mixer);
                if (m.isLineSupported(new Line.Info(Clip.class))) {
                    hasClip = true;
                    break;
                }
            }
            
            if (hasClip) {
                System.out.println("SUCCESS: Audio system supports Clip");
                System.exit(0);
            } else {
                System.out.println("FAILED: No mixer supports Clip");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            System.exit(1);
        }
    }
}
EOF

# Compile and run test
if javac TestAudioSystem.java 2>/dev/null; then
    if java TestAudioSystem 2>/dev/null; then
        report_test "Java Sound API" "PASS" "Audio system supports Clip"
    else
        report_test "Java Sound API" "FAIL" "Java Sound API test failed"
    fi
else
    report_test "Java Sound API" "FAIL" "Failed to compile audio test"
fi

# Cleanup
rm -f TestAudioSystem.java TestAudioSystem.class

echo
echo "Test 4: Check native library for audio functions..."
cd "$PROJECT_DIR"

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

if [ -f "$NATIVE_LIB" ]; then
    report_test "Native library exists" "PASS" "Found $NATIVE_LIB"
    
    # Check library symbols (if nm is available)
    if command -v nm &> /dev/null; then
        AUDIO_SYMBOLS=$(nm "$NATIVE_LIB" 2>/dev/null | grep -i audio | wc -l || echo "0")
        if [ "$AUDIO_SYMBOLS" -gt 0 ]; then
            report_test "Audio symbols in library" "PASS" "Found $AUDIO_SYMBOLS audio-related symbols"
        else
            report_test "Audio symbols in library" "WARN" "No obvious audio symbols found"
        fi
    fi
else
    report_test "Native library exists" "FAIL" "Native library not found"
fi

echo
echo "Test 5: Test application startup with audio..."
cd "$PROJECT_DIR"

# Check if project is compiled
if [ ! -d "target/classes" ]; then
    echo "Compiling project..."
    if ! mvn clean compile -q; then
        report_test "Project compilation" "FAIL" "Maven compilation failed"
        exit 1
    fi
fi

# Test application startup in headless mode
echo "Testing application startup (will exit after 5 seconds)..."
timeout 10s java -Djava.library.path=native -cp target/classes com.meenigam.Main &
APP_PID=$!

# Wait a bit for startup
sleep 3

# Check if process is still running
if kill -0 $APP_PID 2>/dev/null; then
    report_test "Application startup" "PASS" "Application started successfully"
    
    # Kill the application
    kill $APP_PID 2>/dev/null || true
    wait $APP_PID 2>/dev/null || true
else
    report_test "Application startup" "FAIL" "Application failed to start or crashed"
fi

echo
echo "Test 6: Test audio file loading..."
if [ "$TEST_FILES" -gt 0 ]; then
    # Create a test to check audio file loading
    cat > TestAudioFile.java << 'EOF'
import javax.sound.sampled.*;
import java.io.File;

public class TestAudioFile {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: TestAudioFile <wav-file>");
            System.exit(1);
        }
        
        try {
            File audioFile = new File(args[0]);
            if (!audioFile.exists()) {
                System.out.println("FAILED: File does not exist");
                System.exit(1);
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            
            System.out.println("SUCCESS: Audio file loaded");
            System.out.println("Format: " + format);
            System.out.println("Size: " + audioFile.length() + " bytes");
            
            audioStream.close();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            System.exit(1);
        }
    }
}
EOF
    
    # Find a test file
    TEST_FILE=$(find "$TEST_AUDIO_DIR" -name "*.wav" | head -n1)
    
    if javac TestAudioFile.java 2>/dev/null; then
        if java TestAudioFile "$TEST_FILE" 2>/dev/null; then
            report_test "Audio file loading" "PASS" "Successfully loaded $TEST_FILE"
        else
            report_test "Audio file loading" "FAIL" "Failed to load audio file"
        fi
    else
        report_test "Audio file loading" "FAIL" "Failed to compile audio file test"
    fi
    
    # Cleanup
    rm -f TestAudioFile.java TestAudioFile.class
else
    report_test "Audio file loading" "SKIP" "No test audio files available"
fi

echo
echo "Test 7: Check system volume and audio output..."
# This is a basic check - actual audio output testing requires user interaction
if command -v osascript &> /dev/null; then
    # macOS
    VOLUME=$(osascript -e 'output volume of (get volume settings)')
    if [ "$VOLUME" -gt 0 ]; then
        report_test "System volume" "PASS" "System volume is $VOLUME%"
    else
        report_test "System volume" "WARN" "System volume is muted (0%)"
    fi
elif command -v amixer &> /dev/null; then
    # Linux
    if amixer get Master | grep -q "\[on\]"; then
        report_test "System volume" "PASS" "Audio output is enabled"
    else
        report_test "System volume" "WARN" "Audio output may be muted"
    fi
else
    report_test "System volume" "SKIP" "Cannot check system volume on this platform"
fi

echo
echo "=== Test Summary ==="
echo "Tests Passed: $TESTS_PASSED"
echo "Tests Failed: $TESTS_FAILED"
echo "Total Tests:  $((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo "✓ All audio playback tests passed!"
    echo "  Note: Actual audio output requires manual testing"
    exit 0
else
    echo "✗ Some tests failed. Please check the errors above."
    exit 1
fi