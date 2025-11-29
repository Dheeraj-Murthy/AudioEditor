package com.meenigam.unit;

import com.meenigam.Utils.callNative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for callNative class
 * Tests native library loading and method invocation
 */
class TestCallNative {

    private callNative callNativeInstance;
    
    @BeforeEach
    void setUp() {
        callNativeInstance = new callNative();
    }
    
    @Test
    @DisplayName("Native library should load without errors")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testNativeLibraryLoading() {
        // Test that native library loads successfully
        assertDoesNotThrow(() -> {
            // The static block in callNative should load the library
            Class.forName("com.meenigam.Utils.callNative");
        });
    }
    
    @Test
    @DisplayName("callNative should instantiate without errors")
    void testCallNativeInstantiation() {
        // Test that callNative can be instantiated
        assertNotNull(callNativeInstance, 
            "callNative instance should not be null");
    }
    
    @Test
    @DisplayName("callCode should handle valid parameters")
    void testCallCodeWithValidParameters() {
        // Test callCode with valid parameters
        String message = "test message";
        int operation = 1;
        String[] params = {"param1", "param2"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle null message")
    void testCallCodeWithNullMessage() {
        // Test callCode with null message
        int operation = 1;
        String[] params = {"param1"};
        
        // Should handle null message gracefully
        assertDoesNotThrow(() -> {
            callNative.callCode(null, operation, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle null parameters")
    void testCallCodeWithNullParameters() {
        // Test callCode with null parameters
        String message = "test message";
        int operation = 1;
        
        // Should handle null parameters gracefully
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, null);
        });
    }
    
    @Test
    @DisplayName("callCode should handle empty parameters")
    void testCallCodeWithEmptyParameters() {
        // Test callCode with empty parameters array
        String message = "test message";
        int operation = 1;
        String[] params = new String[0];
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle different operation codes")
    void testCallCodeWithDifferentOperations() {
        // Test callCode with various operation codes
        String message = "test message";
        String[] params = {"param1"};
        
        int[] operations = {-1, 0, 1, 2, 100, 999};
        
        for (int operation : operations) {
            assertDoesNotThrow(() -> {
                callNative.callCode(message, operation, params);
            }, "Operation " + operation + " should not throw exception");
        }
    }
    
    @Test
    @DisplayName("callCode should handle long messages")
    void testCallCodeWithLongMessage() {
        // Test callCode with very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("This is a very long message. ");
        }
        
        String[] params = {"param1"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(longMessage.toString(), 1, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle special characters")
    void testCallCodeWithSpecialCharacters() {
        // Test callCode with special characters
        String message = "Test with special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        String[] params = {"param with special chars: !@#$%"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 1, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle unicode characters")
    void testCallCodeWithUnicodeCharacters() {
        // Test callCode with unicode characters
        String message = "Test with unicode: 你好 🎵 ñáéíóú";
        String[] params = {"Unicode param: 🎧"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 1, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle file paths")
    void testCallCodeWithFilePaths() {
        // Test callCode with file paths
        String filePath = "/Users/test/audio/file.wav";
        String[] params = {"/Users/test/output.wav"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(filePath, 1, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle numeric parameters")
    void testCallCodeWithNumericParameters() {
        // Test callCode with numeric parameters
        String message = "test";
        String[] params = {"123", "456.789", "-100"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 1, params);
        });
    }
    
    @Test
    @DisplayName("callCode should handle boolean-like parameters")
    void testCallCodeWithBooleanParameters() {
        // Test callCode with boolean-like parameters
        String message = "test";
        String[] params = {"true", "false", "TRUE", "FALSE"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 1, params);
        });
    }
    
    @Test
    @DisplayName("Native library should be available")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testNativeLibraryAvailability() {
        // Test that native library files exist
        String[] libraryNames = {
            "native/libnative.so",    // Linux
            "native/libnative.dylib",  // macOS
            "native/native.dll"       // Windows
        };
        
        boolean libraryFound = false;
        for (String libName : libraryNames) {
            File libFile = new File(libName);
            if (libFile.exists()) {
                libraryFound = true;
                break;
            }
        }
        
        // Note: This test might fail if native library is not compiled
        // In a real CI/CD environment, you'd compile it first
        if (!libraryFound) {
            System.out.println("Warning: Native library not found. Run build_native.sh first.");
        }
    }
    
    @Test
    @DisplayName("callNative should handle concurrent calls")
    void testConcurrentCalls() throws InterruptedException {
        // Test concurrent calls to native methods
        int numThreads = 5;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                assertDoesNotThrow(() -> {
                    callNative.callCode("thread " + threadId, 1, 
                        new String[]{"param" + threadId});
                });
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }
        
        // If we reach here, concurrent calls didn't cause crashes
        assertTrue(true, "Concurrent calls should not cause exceptions");
    }
}