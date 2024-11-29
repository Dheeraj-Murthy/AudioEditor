package com.meenigam.Panels;

import com.meenigam.Components.FileComponent;
import com.meenigam.Components.Track;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrackEditor extends JPanel {

    private static final List<Track> tracks = new ArrayList<>();
    private StagingArea stagingArea;
    private JSlider slider;
    private double sliderPos;

    public TrackEditor(Frame frame, JSlider slider) {
        setBackground(new Color(30, 30, 30)); // Black background for track editor
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.slider = slider;
        slider.setUI(new CustomSliderUI(slider));
        this.slider.addChangeListener(e -> {
            setSliderPos();
            repaint();
        });

        // Add the slider to the panel
        this.add(slider, BorderLayout.NORTH);

        // Add some sample tracks
        for (int i = 1; i < 8; i++) {
            Track track = new Track("Track" + i, tracks, this);
            this.add(track);
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Cast Graphics to Graphics2D for better control
        Graphics2D g2d = (Graphics2D) g;

        // Enable antialiasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set color and stroke for the red line
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2)); // Adjust thickness if needed

        // Calculate the line's x-position (center of the slider thumb)
        setSliderPos();
        int sliderCenterX = (int) sliderPos;

        // Draw the red line across the entire panel
        g2d.drawLine(sliderCenterX, 13, sliderCenterX, getHeight());
    }

    static class CustomSliderUI extends BasicSliderUI {
        public CustomSliderUI(JSlider b) {
            super(b);
        }

        @Override
        public void paintThumb(Graphics g) {
            // Call the super method to perform default painting
            g.setColor(new Color(0, 0, 0, 0));  // Fully transparent color
            g.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height); // Clear the thumb's area


            // Now, you can safely access thumbRect after the superclass has painted the thumb
            Rectangle thumbRect = this.thumbRect; // The thumb's position and size

            // Define the width and height of the thumb (inverted triangle)
            int width = thumbRect.width;
            int height = thumbRect.height;

            // Calculate the triangle's top (base) and bottom (apex)
            int x1 = thumbRect.x + width / 2;  // Center the top of the triangle
            int y1 = thumbRect.y + height / 2;              // Top of the thumb (base of the triangle)

            int x2 = thumbRect.x + width / 4;  // Left side of the triangle
            int y2 = thumbRect.y;    // Bottom of the thumb (apex of the triangle)

            int x3 = thumbRect.x + 3 * width / 4; // Right side of the triangle
            int y3 = y2; // Bottom of the thumb (apex of the triangle)

            // Create a polygon representing the inverted triangle
            Polygon invertedTriangle = new Polygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);

            // Set the color for the triangle
            g.setColor(Color.RED);

            // Fill the triangle
            g.fillPolygon(invertedTriangle);
        }

        @Override
        public Dimension getThumbSize() {
            // You can customize the thumb's size
            return new Dimension(20, 20); // Example thumb size (width, height)
        }
    }

    private void setSliderPos() {
        double width = ((double) slider.getValue() / (slider.getMaximum())) * (slider.getWidth() - 24);
        sliderPos = width + 12;
    }
    public double getSliderPos() {
        return sliderPos;
    }

    public static List<Track> getTracks() {
        return tracks;
    }

    public void setStagingArea(StagingArea stagingArea) {
        this.stagingArea = stagingArea;
    }

    public void addNewTrack(FileComponent file) {
        // Create a new clip and add it to the track
        Track track = new Track("Track" + tracks.size(), tracks, this);
        track.setClip(file);
        this.add(track);
    }

    public void setSlider(JSlider slider) {
        this.slider = slider;
    }
}