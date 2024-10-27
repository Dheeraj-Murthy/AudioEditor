package com.meenigam.Panels;

import com.meenigam.Components.Track;
import com.meenigam.Components.fileComponent;

import javax.swing.*;
import java.awt.*;

public class TrackEditor extends JPanel {

    private StagingArea stagingArea;

    public TrackEditor(Frame frame) {
        setBackground(new Color(30, 30, 30)); // Black background for track editor
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Add three sample track panels
        for (int i = 1; i < 6; i++) {
            Track track = new Track("Track" + i);
            track.setClip(new fileComponent("Name" + i, "\\path\\to\\file", frame));
            this.add(track);
        }

    }

    public void setStagingArea(StagingArea stagingArea) {
        this.stagingArea = stagingArea;
    }

    public void addNewTrack(fileComponent file) {
        //TODO: create a new clip
//        Clip clip = new Clip;

    }
}