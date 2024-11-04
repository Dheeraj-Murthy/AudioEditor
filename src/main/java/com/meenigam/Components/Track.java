package com.meenigam.Components;

import com.meenigam.Panels.TrackEditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Track extends JPanel {
    private List<Track> tracks;
    private final Color backgroundColor = new Color(50, 50, 50);
    private final Border border = BorderFactory.createLineBorder(new Color(70, 70, 70));
    private final Color foreground = Color.white;
    private JPanel clipContainer;  // Holds clips horizontally
    private String title;

    public Track(String title, List<Track> tracks) {
        this.tracks = tracks;
        this.title = title;
        setBackground(backgroundColor);
        setBorder(border);
//        setLayout(new BorderLayout()); // Use BorderLayout for structured positioning
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(foreground);
        add(titleLabel, BorderLayout.NORTH);

        // Container for clips aligned horizontally
        clipContainer = new JPanel();
        clipContainer.setLayout(new FlowLayout(FlowLayout.LEFT)); // Left-align clips
        clipContainer.setOpaque(false); // Matches the background
        add(clipContainer, BorderLayout.CENTER);

        setPreferredSize(new Dimension(300, 50)); // Adjust dimensions as needed
        tracks.add(this);
    }

    public void addTracksToEditor(TrackEditor editor) {
        for (Track track : tracks) {
            editor.add(track);
        }
    }

    public void setClip(fileComponent fileComponent) {
        Clip clip = new Clip(fileComponent, this);
        clipContainer.add(clip); // Add to clip container for horizontal layout
        revalidate();
        repaint();
    }
    public String toString(){
        return this.title;
    }
}