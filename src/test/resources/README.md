# Test Resources

This directory contains test resources for the Audio Editor test suite.

## **Files**

### **Audio Test Files**
- `test.wav` - Standard test audio file (10 seconds, 44.1kHz, 16-bit, stereo)
- `test_stereo.wav` - Stereo audio file for channel testing
- `test_mono.wav` - Mono audio file for compatibility testing
- `test_long.wav` - Long audio file for performance testing (60+ seconds)

### **Configuration Files**
- `test.properties` - Test configuration and settings

## **Usage**

These resources are automatically loaded by the test suite. The test framework will:

1. **Create temporary files** if test audio files don't exist
2. **Load configuration** from `test.properties`
3. **Clean up** temporary files after tests complete

## **File Formats**

All test audio files should be in WAV format with the following specifications:
- **Sample Rate**: 44.1 kHz
- **Bit Depth**: 16-bit
- **Channels**: Stereo (except test_mono.wav)
- **Duration**: Variable (see file names)

## **Adding New Test Files**

To add new test resources:

1. **Place files** in this directory
2. **Update test.properties** with new file paths
3. **Reference files** in test classes using `@TestResource` annotation or direct path resolution

## **Cleanup**

The test framework automatically cleans up:
- **Temporary files** created during tests
- **Modified test files** (restores originals)
- **Memory allocations** (suggests garbage collection)

## **Notes**

- Test files are **read-only** during test execution
- **Large test files** should be compressed to save space
- **Configuration properties** can be overridden with system properties
- **Audio files** should be **copyright-free** for distribution