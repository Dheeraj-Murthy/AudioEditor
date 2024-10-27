package com.meenigam.Components;

import com.meenigam.Panels.TrackEditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Track extends JPanel {
    private List<Track> tracks = new ArrayList<>();
    private final Color backgroundColor = new Color(50, 50, 50);
    private final Border border = BorderFactory.createLineBorder(new Color(70, 70, 70));
    private final Color foreground = Color.white;
    private Clip clip;

    public Track(String title) {
        setBackground(backgroundColor);
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(10, 30));
        add(new JLabel(title)).setForeground(foreground);
        tracks.add(this);
    }

    public void addTracksToEditor(TrackEditor editor) {
        for (Track track : tracks) {
            editor.add(track);
        }
    }
    public void setClip(fileComponent fileComponent) {
        this.clip = new Clip(fileComponent, this);
        add(clip);
        revalidate();
        repaint();
    }
}
