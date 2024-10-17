package com.meenigam.Panels;

import javax.swing.*;
import java.awt.*;

public class TrackEditor extends JPanel {

    public TrackEditor() {
        setBackground(new Color(30,30,30)); // Black background for track editor
        setLayout(new GridLayout(3, 1, 5, 5)); // Layout with 3 rows (for tracks)
        // Add three sample track panels
        for (int i = 1; i <= 3; i++) {
            JPanel track = new JPanel();
            track.setBackground(new Color(50,50,50));
            track.setBorder(BorderFactory.createLineBorder(new Color(70,70,70)));
            track.add(new JLabel("Track " + i)).setForeground(Color.WHITE);
            add(track); // Add each track to the editor
        }

    }
}