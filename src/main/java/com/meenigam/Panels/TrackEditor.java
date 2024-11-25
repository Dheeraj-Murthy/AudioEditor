package com.meenigam.Panels;

import com.meenigam.Components.Track;
import com.meenigam.Components.FileComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrackEditor extends JPanel {

    private static final List<Track> tracks = new ArrayList<>();
    private StagingArea stagingArea;
    JSlider slider;

    public TrackEditor(Frame frame, JSlider slider) {
        setBackground(new Color(30, 30, 30)); // Black background for track editor
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.slider = slider;
        this.add(slider, BorderLayout.NORTH);
        // Add three sample track panels
        for (int i = 1; i < 6; i++) {
            Track track = new Track("Track" + i, tracks);
//            track.setClip(new fileComponent("Name" + i, "\\path\\to\\file", frame));
            this.add(track);
        }
    }

    public static List<Track> getTracks() {
        return tracks;
    }

    public void setStagingArea(StagingArea stagingArea) {
        this.stagingArea = stagingArea;
    }

    public void addNewTrack(FileComponent file) {
        //TODO: create a new clip
        Track track = new Track("Track" + tracks.size(), tracks);
        track.setClip(file);
        this.add(track);
    }

    public void setSlider(JSlider slider) {
        this.slider = slider;
//        this.add(slider, BorderLayout.NORTH);
    }
}