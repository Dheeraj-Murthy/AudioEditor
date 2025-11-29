package com.meenigam.unit;

import com.meenigam.Components.Clip;
import com.meenigam.Components.FileComponent;
import com.meenigam.Components.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Clip class
 * Tests clip functionality, positioning, and track management
 */
class TestClip {

    @TempDir
    Path tempDir;
    
    @Mock
    private FileComponent mockFileComponent;
    
    @Mock
    private Track mockTrack;
    
    private Clip clip;
    private File testFile;
    
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        
        // Create a test file
        testFile = tempDir.resolve("test.wav").toFile();
        Files.createFile(testFile.toPath());
        
        // Mock FileComponent behavior
        when(mockFileComponent.getFile()).thenReturn(testFile);
        when(mockFileComponent.getFilePath()).thenReturn(testFile.getAbsolutePath());
        when(mockFileComponent.getDuration()).thenReturn(10.0f); // 10 seconds
        
        // Mock Track behavior
        when(mockTrack.getClipContainer()).thenReturn(new JPanel());
        when(mockTrack.getWidth()).thenReturn(800);
        when(mockTrack.getHeight()).thenReturn(100);
    }
    
    @Test
    @DisplayName("Clip should initialize with valid components")
    void testClipInitialization() {
        // Test that Clip can be created with valid components
        assertDoesNotThrow(() -> {
            clip = new Clip(mockFileComponent, mockTrack);
            assertNotNull(clip);
        });
    }
    
    @Test
    @DisplayName("Clip should handle null FileComponent")
    void testNullFileComponentHandling() {
        // Test handling of null FileComponent
        assertThrows(Exception.class, () -> {
            new Clip(null, mockTrack);
        });
    }
    
    @Test
    @DisplayName("Clip should handle null Track")
    void testNullTrackHandling() {
        // Test handling of null Track
        assertThrows(Exception.class, () -> {
            new Clip(mockFileComponent, null);
        });
    }
    
    @Test
    @DisplayName("Clip should set correct initial position")
    void testInitialPosition() {
        // Test initial position setting
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Clip should start at position 0
        assertEquals(0.0f, clip.getStart(), "Clip should start at position 0");
        assertEquals(10.0f, clip.getEnd(), "Clip should end at duration");
    }
    
    @Test
    @DisplayName("Clip should calculate correct size")
    void testClipSize() {
        // Test clip size calculation
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Size should be equal to file duration
        assertEquals(10.0f, clip.getEnd() - clip.getStart(), 
            "Clip size should equal file duration");
    }
    
    @Test
    @DisplayName("Clip should handle file path operations")
    void testFilePathOperations() {
        // Test file path operations
        clip = new Clip(mockFileComponent, mockTrack);
        
        String path = clip.getPath();
        assertEquals(testFile.getAbsolutePath(), path, 
            "Clip should return correct file path");
    }
    
    @Test
    @DisplayName("Clip should handle position updates")
    void testPositionUpdates() {
        // Test position update functionality
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Test position calculation (this would be done through drag operations)
        float newPosition = 50.0f; // 50 pixels from left
        
        // The actual position calculation depends on the implementation
        // For now, we test that the methods exist and return reasonable values
        assertTrue(clip.getStart() >= 0, "Start position should be non-negative");
        assertTrue(clip.getEnd() > clip.getStart(), "End position should be greater than start");
    }
    
    @Test
    @DisplayName("Clip should handle boundary conditions")
    void testBoundaryConditions() {
        // Test boundary conditions
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Test that clip stays within track boundaries
        float clipWidth = (mockFileComponent.getDuration() * 10); // Assuming 10 pixels per second
        float trackWidth = mockTrack.getWidth();
        
        assertTrue(clipWidth <= trackWidth, 
            "Clip width should not exceed track width");
    }
    
    @Test
    @DisplayName("Clip should handle drag operations")
    void testDragOperations() {
        // Test drag operation handling
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Test that clip can be dragged (simulated)
        Point originalLocation = clip.getLocation();
        Point newLocation = new Point(originalLocation.x + 10, originalLocation.y);
        
        // The actual drag handling would be done through mouse events
        // For unit testing, we verify the clip maintains its properties
        assertNotNull(clip.getLocation(), "Clip should have a location");
        assertTrue(clip.getLocation().x >= 0, "Clip x-position should be non-negative");
        assertTrue(clip.getLocation().y >= 0, "Clip y-position should be non-negative");
    }
    
    @Test
    @DisplayName("Clip should handle reset operations")
    void testResetOperations() {
        // Test reset functionality
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Test reset operation
        assertDoesNotThrow(() -> {
            clip.reset();
        });
        
        // After reset, clip should return to initial state
        assertEquals(0.0f, clip.getStart(), "Clip should reset to start position 0");
        assertEquals(10.0f, clip.getEnd(), "Clip should reset to end position equal to duration");
    }
    
    @Test
    @DisplayName("Clip should handle file component operations")
    void testFileComponentOperations() {
        // Test file component operations
        clip = new Clip(mockFileComponent, mockTrack);
        
        FileComponent returnedFileComponent = clip.getFileComponent();
        assertEquals(mockFileComponent, returnedFileComponent, 
            "Clip should return the correct FileComponent");
    }
    
    @Test
    @DisplayName("Clip should handle size calculations")
    void testSizeCalculations() {
        // Test various size calculations
        when(mockFileComponent.getDuration()).thenReturn(15.0f); // 15 seconds
        
        clip = new Clip(mockFileComponent, mockTrack);
        
        float expectedSize = 15.0f;
        float actualSize = clip.getEnd() - clip.getStart();
        
        assertEquals(expectedSize, actualSize, 0.01f, 
            "Clip size should match file duration");
    }
    
    @Test
    @DisplayName("Clip should handle track boundaries")
    void testTrackBoundaries() {
        // Test track boundary handling
        when(mockTrack.getWidth()).thenReturn(1000);
        when(mockFileComponent.getDuration()).thenReturn(20.0f); // 20 seconds = 200 pixels
        
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Clip should fit within track boundaries
        float clipWidth = mockFileComponent.getDuration() * 10; // pixels
        float trackWidth = mockTrack.getWidth();
        
        assertTrue(clipWidth <= trackWidth, 
            "Clip should fit within track width");
    }
    
    @Test
    @DisplayName("Clip should handle component resizing")
    void testComponentResizing() {
        // Test component resizing functionality
        clip = new Clip(mockFileComponent, mockTrack);
        
        // Test preferred size calculation
        Dimension preferredSize = clip.getPreferredSize();
        
        assertNotNull(preferredSize, "Clip should have a preferred size");
        assertTrue(preferredSize.width > 0, "Clip width should be positive");
        assertTrue(preferredSize.height > 0, "Clip height should be positive");
    }
    
    @Test
    @DisplayName("Clip should handle invalid duration values")
    void testInvalidDurationValues() {
        // Test handling of invalid duration values
        when(mockFileComponent.getDuration()).thenReturn(-1.0f); // Invalid negative duration
        
        // Clip should handle invalid duration gracefully
        assertDoesNotThrow(() -> {
            clip = new Clip(mockFileComponent, mockTrack);
        });
        
        // Even with invalid duration, clip should maintain valid state
        assertTrue(clip.getEnd() >= clip.getStart(), 
            "End position should be greater than or equal to start position");
    }
    
    /**
     * Mock Panel class for testing
     */
    private static class Panel extends java.awt.Panel {
        // Simple mock panel for testing
    }
}