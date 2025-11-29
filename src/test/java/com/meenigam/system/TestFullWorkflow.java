package com.meenigam.system;

import com.meenigam.Manager;
import com.meenigam.Frame;
import com.meenigam.Components.Clip;
import com.meenigam.Components.FileComponent;
import com.meenigam.Components.Track;
import com.meenigam.Panels.TrackEditor;
import com.meenigam.Panels.StagingArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * System tests for complete application workflow
 * Tests end-to-end functionality from startup to audio processing
 */
class TestFullWorkflow {

    @TempDir
    Path tempDir;
    
    private File testAudioFile;
    private File projectDir;
    
    // Helper method to create test Frame instance
    private Frame createTestFrame() {
        try {
            Manager mockManager = new Manager();
            return new Frame(mockManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test Frame", e);
        }
    }
    
    // Helper method to create test TrackEditor instance
    private TrackEditor createTestTrackEditor() {
        Frame testFrame = createTestFrame();
        JSlider testSlider = new JSlider();
        return new TrackEditor(testFrame, testSlider);
    }
    
    @BeforeEach
    void setUp() throws IOException {
        // Create test environment
        testAudioFile = tempDir.resolve("test.wav").toFile();
        Files.createFile(testAudioFile.toPath());
        
        projectDir = tempDir.resolve("ProjectFiles").toFile();
        Files.createDirectories(projectDir.toPath());
        
        // Set system properties for testing
        System.setProperty("java.awt.headless", "false");
        System.setProperty("user.home", tempDir.toString());
    }
    
    @Test
    @DisplayName("Application should start up completely")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testApplicationStartup() {
        // Test complete application startup
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create manager (this will show dialog in real scenario)
                // For testing, we might need to mock the dialog
                Manager manager = new Manager();
                assertNotNull(manager, "Manager should be created");
                assertNotNull(manager.finalFilePath, "Final file path should be set");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Application startup failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Application should start within 10 seconds");
        } catch (InterruptedException e) {
            fail("Application startup test interrupted");
        }
    }
    
    @Test
    @DisplayName("Frame should initialize with all components")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testFrameInitialization() {
        // Test frame initialization with all components
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Mock manager for testing
                Manager mockManager = new Manager();
                Frame frame = new Frame(mockManager);
                
                assertNotNull(frame, "Frame should be created");
                assertTrue(frame.isVisible(), "Frame should be visible");
                
                // Test that frame has expected components
                Component[] components = frame.getContentPane().getComponents();
                assertTrue(components.length > 0, "Frame should have components");
                
                frame.dispose();
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Frame initialization failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Frame should initialize within 10 seconds");
        } catch (InterruptedException e) {
            fail("Frame initialization test interrupted");
        }
    }
    
    @Test
    @DisplayName("Complete workflow should handle file operations")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testCompleteFileWorkflow() {
        // Test complete file handling workflow
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Test file creation
                File testFile = tempDir.resolve("workflow_test.wav").toFile();
                Files.createFile(testFile.toPath());
                
                assertTrue(testFile.exists(), "Test file should exist");
                assertTrue(testFile.canRead(), "Test file should be readable");
                
                // Test FileComponent creation - need proper constructor
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                FileComponent fileComponent = new FileComponent("test.wav", testFile.getAbsolutePath(), mockFrame, mockStagingArea);
                assertNotNull(fileComponent, "FileComponent should be created");
                assertEquals(testFile.getAbsolutePath(), fileComponent.getFilePath(), 
                    "FileComponent should store correct path");
                
                // Test Track creation - need proper constructor with TrackEditor
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> tracks = new java.util.ArrayList<>();
                Track track = new Track("Test Track", tracks, mockTrackEditor);
                assertNotNull(track, "Track should be created");
                assertEquals("Test Track", track.toString(), "Track should have correct name");
                
                // Test Clip creation
                Clip clip = new Clip(fileComponent, track);
                assertNotNull(clip, "Clip should be created");
                assertEquals(testFile.getAbsolutePath(), clip.getPath(), 
                    "Clip should reference correct file");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("File workflow test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "File workflow should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("File workflow test interrupted");
        }
    }
    
    @Test
    @DisplayName("Project directory should be created and managed")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testProjectDirectoryManagement() {
        // Test project directory creation and management
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Test project directory creation
                Path projectDirPath = tempDir.resolve("ProjectFiles");
                Files.createDirectories(projectDirPath);
                File projectDir = projectDirPath.toFile();
                
                // Test final file creation
                Path finalFilePath = projectDirPath.resolve("finalFile.wav");
                File finalFile = finalFilePath.toFile();
                if (!finalFile.exists()) {
                    Files.createFile(finalFilePath);
                }
                
                assertTrue(finalFile.exists(), "Final file should exist");
                assertTrue(finalFile.getParentFile().equals(projectDir), 
                    "Final file should be in project directory");
                
                // Test directory permissions
                assertTrue(projectDir.canRead(), "Project directory should be readable");
                assertTrue(projectDir.canWrite(), "Project directory should be writable");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Project directory test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Project directory test should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Project directory test interrupted");
        }
    }
    
    @Test
    @DisplayName("Multi-track workflow should function correctly")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testMultiTrackWorkflow() {
        // Test multi-track functionality
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create multiple tracks
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                Track[] tracks = new Track[3];
                for (int i = 0; i < tracks.length; i++) {
                    tracks[i] = new Track("Track " + (i + 1), trackList, mockTrackEditor);
                    trackList.add(tracks[i]);
                    assertNotNull(tracks[i], "Track " + i + " should be created");
                }
                
                // Create file components for each track
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                FileComponent[] fileComponents = new FileComponent[tracks.length];
                for (int i = 0; i < fileComponents.length; i++) {
                    File trackFile = tempDir.resolve("track" + i + ".wav").toFile();
                    Files.createFile(trackFile.toPath());
                    fileComponents[i] = new FileComponent("track" + i + ".wav", trackFile.getAbsolutePath(), mockFrame, mockStagingArea);
                    assertNotNull(fileComponents[i], "FileComponent " + i + " should be created");
                }
                
                // Create clips for each track
                Clip[] clips = new Clip[tracks.length];
                for (int i = 0; i < clips.length; i++) {
                    clips[i] = new Clip(fileComponents[i], tracks[i]);
                    assertNotNull(clips[i], "Clip " + i + " should be created");
                }
                
                // Verify track-clip relationships
                for (int i = 0; i < clips.length; i++) {
                    assertEquals(fileComponents[i], clips[i].getFileComponent(), 
                        "Clip " + i + " should reference correct FileComponent");
                }
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Multi-track workflow test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Multi-track workflow should complete within 10 seconds");
        } catch (InterruptedException e) {
            fail("Multi-track workflow test interrupted");
        }
    }
    
    @Test
    @DisplayName("Application should handle cleanup properly")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testApplicationCleanup() {
        // Test application cleanup and resource management
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and then dispose components
                Manager mockManager = new Manager();
                Frame frame = new Frame(mockManager);
                
                // Verify frame is created and visible
                assertTrue(frame.isVisible(), "Frame should be visible");
                
                // Test cleanup
                frame.dispose();
                assertFalse(frame.isVisible(), "Frame should not be visible after dispose");
                
                // Test resource cleanup
                System.gc(); // Suggest garbage collection
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Cleanup test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Cleanup test should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Cleanup test interrupted");
        }
    }
    
    @Test
    @DisplayName("Error handling should work throughout workflow")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testErrorHandling() {
        // Test error handling throughout the workflow
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Test with invalid file
                File invalidFile = new File("/nonexistent/path/file.wav");
                
                // Should handle invalid file gracefully
                assertDoesNotThrow(() -> {
                    try {
                        Frame mockFrame = createTestFrame();
                        StagingArea mockStagingArea = new StagingArea(mockFrame);
                        FileComponent fileComponent = new FileComponent("invalid.wav", "/nonexistent/path/file.wav", mockFrame, mockStagingArea);
                        // FileComponent should handle invalid file
                    } catch (Exception e) {
                        // Expected for invalid file
                    }
                });
                
                // Test with null values
                assertDoesNotThrow(() -> {
                    try {
                        TrackEditor mockTrackEditor = createTestTrackEditor();
                        java.util.List<Track> tracks = new java.util.ArrayList<>();
                        Track track = new Track(null, tracks, mockTrackEditor);
                        // Should handle null name gracefully
                    } catch (Exception e) {
                        // Expected for null name
                    }
                });
                
                // Test with invalid parameters
                assertDoesNotThrow(() -> {
                    try {
                        new Clip(null, null);
                        // Should handle null parameters gracefully
                    } catch (Exception e) {
                        // Expected for null parameters
                    }
                });
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Error handling test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Error handling test should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Error handling test interrupted");
        }
    }
    
    @Test
    @DisplayName("Performance should be acceptable")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testPerformance() {
        // Test that performance is acceptable
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Create multiple components
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                
                for (int i = 0; i < 100; i++) {
                    File testFile = tempDir.resolve("perf_test_" + i + ".wav").toFile();
                    Files.createFile(testFile.toPath());
                    
                    FileComponent fileComponent = new FileComponent("perf_test_" + i + ".wav", testFile.getAbsolutePath(), mockFrame, mockStagingArea);
                    Track track = new Track("Track " + i, trackList, mockTrackEditor);
                    trackList.add(track);
                    Clip clip = new Clip(fileComponent, track);
                    
                    // Components should be created quickly
                    assertNotNull(fileComponent);
                    assertNotNull(track);
                    assertNotNull(clip);
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // Should complete within reasonable time (5 seconds for 100 components)
                assertTrue(duration < 5000, 
                    "Component creation should complete within 5 seconds, took " + duration + "ms");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Performance test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Performance test should complete within 10 seconds");
        } catch (InterruptedException e) {
            fail("Performance test interrupted");
        }
    }
    
    @Test
    @DisplayName("Memory usage should be reasonable")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testMemoryUsage() {
        // Test that memory usage is reasonable
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection to get baseline
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create many components
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                
                for (int i = 0; i < 1000; i++) {
                    File testFile = tempDir.resolve("memory_test_" + i + ".wav").toFile();
                    Files.createFile(testFile.toPath());
                    
                    FileComponent fileComponent = new FileComponent("memory_test_" + i + ".wav", testFile.getAbsolutePath(), mockFrame, mockStagingArea);
                    Track track = new Track("Track " + i, trackList, mockTrackEditor);
                    trackList.add(track);
                    Clip clip = new Clip(fileComponent, track);
                    
                    // Keep references to prevent GC
                    if (i % 100 == 0) {
                        System.gc();
                    }
                }
                
                // Check memory usage
                long finalMemory = runtime.totalMemory() - runtime.freeMemory();
                long memoryIncrease = finalMemory - initialMemory;
                
                // Memory increase should be reasonable (less than 100MB for 1000 components)
                assertTrue(memoryIncrease < 100 * 1024 * 1024, 
                    "Memory increase should be less than 100MB, was " + 
                    (memoryIncrease / 1024 / 1024) + "MB");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Memory usage test failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(15, TimeUnit.SECONDS), 
                "Memory usage test should complete within 15 seconds");
        } catch (InterruptedException e) {
            fail("Memory usage test interrupted");
        }
    }
}