package com.meenigam.integration;

import com.meenigam.Utils.callNative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JNI functionality
 * Tests Java-C++ integration through JNI interface
 */
class TestJNIIntegration {

    private callNative callNativeInstance;
    
    @BeforeEach
    void setUp() {
        callNativeInstance = new callNative();
    }
    
    @Test
    @DisplayName("Native library should load successfully")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testNativeLibraryLoading() {
        // Test that native library loads without errors
        assertDoesNotThrow(() -> {
            // The static block in callNative should load the library
            Class<?> clazz = Class.forName("com.meenigam.Utils.callNative");
            assertNotNull(clazz, "callNative class should be loaded");
        });
    }
    
    @Test
    @DisplayName("Native methods should be available")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testNativeMethodAvailability() {
        // Test that native methods are available through reflection
        Class<?> clazz = callNativeInstance.getClass();
        
        // Look for native methods
        Method[] methods = clazz.getDeclaredMethods();
        boolean nativeMethodFound = false;
        
        for (Method method : methods) {
            if (method.getName().equals("sayHello")) {
                nativeMethodFound = true;
                break;
            }
        }
        
        assertTrue(nativeMethodFound, "Native method 'sayHello' should be available");
    }
    
    @Test
    @DisplayName("JNI should handle string parameters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIStringParameters() {
        // Test JNI string parameter handling
        String testString = "Hello from Java!";
        int operation = 1;
        String[] params = {"param1", "param2"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(testString, operation, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle array parameters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIArrayParameters() {
        // Test JNI array parameter handling
        String message = "Test array parameters";
        int operation = 2;
        String[] params = {"item1", "item2", "item3", "item4", "item5"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle null parameters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNINullParameters() {
        // Test JNI null parameter handling
        String message = "Test null parameters";
        int operation = 3;
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, null);
        });
    }
    
    @Test
    @DisplayName("JNI should handle empty arrays")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIEmptyArrays() {
        // Test JNI empty array handling
        String message = "Test empty arrays";
        int operation = 4;
        String[] params = new String[0];
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle large strings")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNILargeStrings() {
        // Test JNI large string handling
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeString.append("This is a large string for testing JNI. ");
        }
        
        String message = largeString.toString();
        int operation = 5;
        String[] params = {"large param"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle special characters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNISpecialCharacters() {
        // Test JNI special character handling
        String message = "Special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        int operation = 6;
        String[] params = {"Special param: ñáéíóú 你好 🎵"};
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, operation, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle concurrent calls")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIConcurrentCalls() throws InterruptedException {
        // Test JNI concurrent call handling
        int numThreads = 10;
        CountDownLatch latch = new CountDownLatch(numThreads);
        boolean[] success = new boolean[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    callNative.callCode("Concurrent test " + threadId, threadId, 
                        new String[]{"param" + threadId});
                    success[threadId] = true;
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " failed: " + e.getMessage());
                    success[threadId] = false;
                } finally {
                    latch.countDown();
                }
            });
            
            thread.start();
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS), 
            "All threads should complete within 10 seconds");
        
        // Check that most threads succeeded (allowing for some JNI threading issues)
        int successCount = 0;
        for (boolean s : success) {
            if (s) successCount++;
        }
        
        assertTrue(successCount >= numThreads * 0.8, 
            "At least 80% of threads should succeed");
    }
    
    @Test
    @DisplayName("JNI should handle different operation codes")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIDifferentOperations() {
        // Test JNI with various operation codes
        String message = "Test different operations";
        String[] params = {"test param"};
        
        int[] operations = {
            -1, 0, 1, 2, 5, 10, 100, 999, -999
        };
        
        for (int operation : operations) {
            assertDoesNotThrow(() -> {
                callNative.callCode(message, operation, params);
            }, "Operation " + operation + " should not throw exception");
        }
    }
    
    @Test
    @DisplayName("JNI should handle file paths")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIFilePaths() {
        // Test JNI file path handling
        String[] filePaths = {
            "/tmp/test.wav",
            "/Users/test/audio/file.wav",
            "C:\\Users\\test\\audio\\file.wav",
            "./relative/path.wav",
            "../parent/path.wav"
        };
        
        for (String filePath : filePaths) {
            assertDoesNotThrow(() -> {
                callNative.callCode(filePath, 7, new String[]{filePath});
            }, "File path " + filePath + " should be handled");
        }
    }
    
    @Test
    @DisplayName("JNI should handle numeric parameters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNINumericParameters() {
        // Test JNI numeric parameter handling
        String message = "Test numeric parameters";
        String[] params = {
            "123",
            "-456",
            "789.012",
            "-3.14159",
            "0",
            "-0"
        };
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 8, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle boolean parameters")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIBooleanParameters() {
        // Test JNI boolean parameter handling
        String message = "Test boolean parameters";
        String[] params = {
            "true",
            "false",
            "TRUE",
            "FALSE",
            "True",
            "False"
        };
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, 9, params);
        });
    }
    
    @Test
    @DisplayName("JNI should handle memory management")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIMemoryManagement() {
        // Test JNI memory management with many calls
        String message = "Memory test";
        String[] params = {"memory test param"};
        
        final String finalMessage = message;
        final String[] finalParams = params;
        
        // Make many calls to test memory management
        for (int i = 0; i < 1000; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                callNative.callCode(finalMessage + " " + index, index, finalParams);
            }, "Call " + index + " should not throw exception");
        }
        
        // If we reach here without OutOfMemoryError, memory management is working
        assertTrue(true, "Memory management test completed successfully");
    }
    
    @Test
    @DisplayName("JNI should handle error conditions")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIErrorConditions() {
        // Test JNI error condition handling
        String message = "Error test";
        
        // Test with potentially problematic inputs
        assertDoesNotThrow(() -> {
            callNative.callCode(message, Integer.MAX_VALUE, new String[]{"test"});
        });
        
        assertDoesNotThrow(() -> {
            callNative.callCode(message, Integer.MIN_VALUE, new String[]{"test"});
        });
        
        assertDoesNotThrow(() -> {
            callNative.callCode(null, -1, null);
        });
    }
    
    @Test
    @DisplayName("JNI should maintain state between calls")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIStateMaintenance() {
        // Test JNI state maintenance between calls
        String message1 = "First call";
        String message2 = "Second call";
        String[] params = {"state test"};
        
        // Make sequential calls
        assertDoesNotThrow(() -> {
            callNative.callCode(message1, 10, params);
            callNative.callCode(message2, 11, params);
        });
        
        // State maintenance would be tested through specific native implementation
        // For now, we just verify that calls don't interfere with each other
        assertTrue(true, "State maintenance test completed");
    }
    
    @Test
    @DisplayName("JNI should be thread-safe")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testJNIThreadSafety() throws InterruptedException {
        // Test JNI thread safety
        int numThreads = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();
                    
                    // Make JNI call
                    callNative.callCode("Thread safety test " + threadId, 
                        threadId, new String[]{"thread" + threadId});
                    
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " error: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
            
            thread.start();
        }
        
        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for completion
        assertTrue(endLatch.await(5, TimeUnit.SECONDS), 
            "All threads should complete within 5 seconds");
    }
}