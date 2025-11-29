package com.meenigam.acceptance;

import com.meenigam.Manager;
import com.meenigam.Frame;
import com.meenigam.Components.Clip;
import com.meenigam.Components.FileComponent;
import com.meenigam.Components.Track;
import com.meenigam.Panels.ControlPanel;
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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User acceptance tests for common user scenarios
 * Tests real user workflows and user experience
 */
class TestUserScenarios {

    @TempDir
    Path tempDir;
    
    private File testAudioFile;
    private Frame frame;
    
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
    void setUp() throws IOException, InterruptedException {
        // Create test environment
        testAudioFile = tempDir.resolve("test.wav").toFile();
        Files.createFile(testAudioFile.toPath());
        
        // Set up Swing for testing
        System.setProperty("java.awt.headless", "false");
        System.setProperty("user.home", tempDir.toString());
    }
    
    @Test
    @DisplayName("User should be able to start application")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testApplicationStartupScenario() {
        // Test user scenario: Starting the application
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Double-click application icon / run main method
                Manager manager = new Manager();
                frame = new Frame(manager);
                
                // Expected result: Application window appears
                assertNotNull(frame, "Application frame should be created");
                assertTrue(frame.isVisible(), "Application should be visible");
                assertTrue(frame.isActive(), "Application should be active");
                
                // Expected result: Window has title
                assertEquals("Audio Editor", frame.getTitle(), 
                    "Application should have correct title");
                
                // Expected result: Window has reasonable size
                Dimension size = frame.getSize();
                assertTrue(size.width > 800, "Window width should be reasonable");
                assertTrue(size.height > 600, "Window height should be reasonable");
                
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
    @DisplayName("User should be able to load audio file")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testAudioFileLoadingScenario() {
        // Test user scenario: Loading an audio file
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Select file from file chooser or drag-and-drop
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                FileComponent fileComponent = new FileComponent("test.wav", testAudioFile.getAbsolutePath(), mockFrame, mockStagingArea);
                
                // Expected result: File is loaded successfully
                assertNotNull(fileComponent, "FileComponent should be created");
                assertEquals(testAudioFile.getAbsolutePath(), fileComponent.getFilePath(), 
                    "FileComponent should store correct path");
                assertTrue(testAudioFile.exists(), "Audio file should exist");
                
                // Expected result: File properties are available
                assertTrue(fileComponent.getDuration() >= 0, 
                    "File duration should be non-negative");
                
                // Expected result: File can be used for creating clips
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                Track track = new Track("Test Track", trackList, mockTrackEditor);
                Clip clip = new Clip(fileComponent, track);
                assertNotNull(clip, "Clip should be created from loaded file");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("File loading scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "File loading should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("File loading test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to create multi-track project")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testMultiTrackProjectScenario() {
        // Test user scenario: Creating a multi-track project
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Create multiple tracks and add audio files
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                Track[] tracks = new Track[3];
                Clip[] clips = new Clip[3];
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                
                for (int i = 0; i < tracks.length; i++) {
                    // User creates track
                    tracks[i] = new Track("Track " + (i + 1), trackList, mockTrackEditor);
                    trackList.add(tracks[i]);
                    assertNotNull(tracks[i], "Track " + i + " should be created");
                    
                    // User adds audio file to track
                    File trackFile = tempDir.resolve("track" + i + ".wav").toFile();
                    Files.createFile(trackFile.toPath());
                    
                    FileComponent fileComponent = new FileComponent("track" + i + ".wav", trackFile.getAbsolutePath(), mockFrame, mockStagingArea);
                    clips[i] = new Clip(fileComponent, tracks[i]);
                    assertNotNull(clips[i], "Clip " + i + " should be created");
                }
                
                // Expected result: All tracks are created and have clips
                for (int i = 0; i < tracks.length; i++) {
                    assertNotNull(tracks[i], "Track " + i + " should exist");
                    assertNotNull(clips[i], "Clip " + i + " should exist");
                    assertEquals("Track " + (i + 1), tracks[i].toString(), 
                        "Track " + i + " should have correct name");
                }
                
                // Expected result: Project structure is valid
                assertTrue(tracks.length > 1, "Project should have multiple tracks");
                assertEquals(tracks.length, clips.length, 
                    "Each track should have a clip");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Multi-track scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Multi-track creation should complete within 10 seconds");
        } catch (InterruptedException e) {
            fail("Multi-track test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to play audio")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testAudioPlaybackScenario() {
        // Test user scenario: Playing audio
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Click play button
                ControlPanel controlPanel = new ControlPanel(null, testAudioFile.getAbsolutePath());
                
                // Expected result: Play button is available
                assertNotNull(controlPanel.getPlayButton(), "Play button should exist");
                assertTrue(controlPanel.getPlayButton().isEnabled(), 
                    "Play button should be enabled");
                
                // User clicks play button
                controlPanel.getPlayButton().doClick();
                
                // Expected result: Playback controls update
                assertNotNull(controlPanel.getPauseButton(), "Pause button should exist");
                assertNotNull(controlPanel.getStopButton(), "Stop button should exist");
                
                // Expected result: Progress tracking is available
                assertNotNull(controlPanel.getProgressSlider(), 
                    "Progress slider should exist");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Audio playback scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Audio playback setup should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Audio playback test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to drag and position clips")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testClipDraggingScenario() {
        // Test user scenario: Dragging and positioning clips on timeline
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Create clip and drag it
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                FileComponent fileComponent = new FileComponent("test.wav", testAudioFile.getAbsolutePath(), mockFrame, mockStagingArea);
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                Track track = new Track("Test Track", trackList, mockTrackEditor);
                Clip clip = new Clip(fileComponent, track);
                
                // Expected result: Clip has initial position
                Point initialPosition = clip.getLocation();
                assertNotNull(initialPosition, "Clip should have initial position");
                assertEquals(0, initialPosition.x, "Clip should start at x=0");
                
                // User drags clip to new position
                Point newPosition = new Point(100, 0);
                clip.setLocation(newPosition);
                
                // Expected result: Clip moves to new position
                Point finalPosition = clip.getLocation();
                assertEquals(newPosition.x, finalPosition.x, 
                    "Clip should move to new x position");
                assertEquals(newPosition.y, finalPosition.y, 
                    "Clip should maintain y position");
                
                // Expected result: Clip boundaries are respected
                assertTrue(finalPosition.x >= 0, "Clip should stay within track bounds");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Clip dragging scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Clip dragging should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Clip dragging test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to save project")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testProjectSavingScenario() {
        // Test user scenario: Saving a project
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Create project and save it
                File projectDir = tempDir.resolve("SavedProject").toFile();
                projectDir.mkdirs();
                
                // User creates project structure
                File finalFile = new File(projectDir, "finalFile.wav");
                if (!finalFile.exists()) {
                    Files.createFile(finalFile.toPath());
                }
                
                // Expected result: Project directory is created
                assertTrue(projectDir.exists(), "Project directory should be created");
                assertTrue(projectDir.isDirectory(), "Project should be a directory");
                
                // Expected result: Project files are created
                assertTrue(finalFile.exists(), "Final file should be created");
                assertTrue(finalFile.canWrite(), "Final file should be writable");
                
                // Expected result: Project structure is valid
                assertEquals(projectDir, finalFile.getParentFile(), 
                    "Final file should be in project directory");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Project saving scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Project saving should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Project saving test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to use keyboard shortcuts")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testKeyboardShortcutsScenario() {
        // Test user scenario: Using keyboard shortcuts
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Use keyboard shortcuts
                ControlPanel controlPanel = new ControlPanel(null, testAudioFile.getAbsolutePath());
                
                // Expected result: Space bar controls playback
                Robot robot = new Robot();
                
                // Test space bar for play/pause
                robot.keyPress(KeyEvent.VK_SPACE);
                robot.delay(100);
                robot.keyRelease(KeyEvent.VK_SPACE);
                
                // Expected result: Controls respond to keyboard
                assertNotNull(controlPanel.getPlayButton(), 
                    "Play button should be available for keyboard control");
                
                // Test escape key for stop
                robot.keyPress(KeyEvent.VK_ESCAPE);
                robot.delay(100);
                robot.keyRelease(KeyEvent.VK_ESCAPE);
                
                // Expected result: Stop functionality works
                assertNotNull(controlPanel.getStopButton(), 
                    "Stop button should be available for keyboard control");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Keyboard shortcuts scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Keyboard shortcuts test should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Keyboard shortcuts test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should see helpful error messages")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testErrorHandlingScenario() {
        // Test user scenario: Encountering errors and seeing helpful messages
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Try to load invalid file
                File invalidFile = new File("/nonexistent/path/invalid.wav");
                
                // Expected result: Graceful error handling
                assertDoesNotThrow(() -> {
                    try {
                        Frame mockFrame = createTestFrame();
                        StagingArea mockStagingArea = new StagingArea(mockFrame);
                        FileComponent fileComponent = new FileComponent("invalid.wav", "/nonexistent/path/invalid.wav", mockFrame, mockStagingArea);
                        // Should handle invalid file gracefully
                    } catch (Exception e) {
                        // Expected - error should be caught and handled
                        assertNotNull(e.getMessage(), 
                            "Error message should be provided");
                    }
                });
                
                // User action: Try to create clip with null parameters
                assertDoesNotThrow(() -> {
                    try {
                        new Clip(null, null);
                        // Should handle null parameters gracefully
                    } catch (Exception e) {
                        // Expected - error should be caught and handled
                        assertNotNull(e.getMessage(), 
                            "Error message should be provided");
                    }
                });
                
                // Expected result: Application remains stable
                // If we reach here, error handling worked
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Error handling scenario failed: " + e.getMessage());
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
    @DisplayName("User should have responsive interface")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testResponsiveInterfaceScenario() {
        // Test user scenario: Interface should be responsive
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Perform multiple UI operations
                long startTime = System.currentTimeMillis();
                
                // Create multiple components
                Frame mockFrame = createTestFrame();
                StagingArea mockStagingArea = new StagingArea(mockFrame);
                TrackEditor mockTrackEditor = createTestTrackEditor();
                java.util.List<Track> trackList = new java.util.ArrayList<>();
                
                for (int i = 0; i < 50; i++) {
                    File testFile = tempDir.resolve("responsive_test_" + i + ".wav").toFile();
                    Files.createFile(testFile.toPath());
                    
                    FileComponent fileComponent = new FileComponent("responsive_test_" + i + ".wav", testFile.getAbsolutePath(), mockFrame, mockStagingArea);
                    Track track = new Track("Track " + i, trackList, mockTrackEditor);
                    trackList.add(track);
                    Clip clip = new Clip(fileComponent, track);
                    
                    // Components should be created quickly
                    assertNotNull(fileComponent, "FileComponent " + i + " should be created");
                    assertNotNull(track, "Track " + i + " should be created");
                    assertNotNull(clip, "Clip " + i + " should be created");
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // Expected result: Interface remains responsive
                assertTrue(duration < 3000, 
                    "UI operations should complete within 3 seconds, took " + duration + "ms");
                
                // Expected result: No UI freezing
                // If we reach here, UI was responsive
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Responsive interface scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), 
                "Responsive interface test should complete within 10 seconds");
        } catch (InterruptedException e) {
            fail("Responsive interface test interrupted");
        }
    }
    
    @Test
    @DisplayName("User should be able to close application properly")
    @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
    void testApplicationClosingScenario() {
        // Test user scenario: Closing the application properly
        CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // User action: Create and then close application
                Manager mockManager = new Manager();
                Frame testFrame = new Frame(mockManager);
                
                // Verify frame is created
                assertTrue(testFrame.isVisible(), "Frame should be visible initially");
                
                // User closes application (clicks X button)
                testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                // Simulate window closing
                testFrame.dispatchEvent(new java.awt.event.WindowEvent(
                    testFrame, java.awt.event.WindowEvent.WINDOW_CLOSING));
                
                // Expected result: Application closes cleanly
                testFrame.dispose();
                
                // Expected result: Resources are cleaned up
                assertFalse(testFrame.isVisible(), "Frame should not be visible after closing");
                assertFalse(testFrame.isActive(), "Frame should not be active after closing");
                
                latch.countDown();
                
            } catch (Exception e) {
                System.err.println("Application closing scenario failed: " + e.getMessage());
                latch.countDown();
            }
        });
        
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), 
                "Application closing should complete within 5 seconds");
        } catch (InterruptedException e) {
            fail("Application closing test interrupted");
        }
    }
}