package com.meenigam.Components;


import com.meenigam.Panels.StagingArea;
import com.meenigam.Panels.TrackEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class fileComponent extends Button {
    private final String Name;
    private final String filePath;
    private final boolean isSelected = false;

    public fileComponent(String name, String filePath, Frame frame) {
        this.Name = name;
        this.filePath = filePath;


        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Clicked");

//                Dialog dialog = new Dialog(frame, "You clicked this", true);
//                dialog.setLocationRelativeTo(frame);
//                dialog.setVisible(true);
                // Use JOptionPane for dialog instead of AWT Dialog
                JOptionPane.showMessageDialog(frame,
                        "You clicked: " + name,
                        "File Component Clicked",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void clicked(StagingArea s) {
        System.out.println("Button Clicked");

        Object[] options = {"delete", "Add to track"};

        // Use JOptionPane for dialog instead of AWT Dialog
        int result = JOptionPane.showOptionDialog(
                s,
                "File: " + Name,
                "Select option",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        // Perform actions based on the user's choice
        switch (result) {
            case JOptionPane.YES_OPTION:
                System.out.println("Option 1 selected");
                break;
            case JOptionPane.NO_OPTION:
                System.out.println("Option 2 selected");
                openTrackSelectionDialogBox(s);
//                s.addToTrack(this, );
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                System.out.println("Canceled");
                break;
            default:
                System.out.println("No option selected");
                break;
        }
    }

    private void openTrackSelectionDialogBox(StagingArea s) {
        // Dialog to select a track from the available list
        List<Track> availableTracks = TrackEditor.getTracks();
        JComboBox<Track> trackComboBox = new JComboBox<>(availableTracks.toArray(new Track[0]));
        int choice = JOptionPane.showConfirmDialog(
                s,
                trackComboBox,
                "Choose Track to Add File",
                JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION) {
            Track selectedTrack = (Track) trackComboBox.getSelectedItem();
            if (selectedTrack != null) {
                selectedTrack.setClip(this);
                System.out.println("File added to track: " + selectedTrack);
            }
        } else {
            System.out.println("Track selection canceled.");
        }
    }

    public String getName() {
        return Name;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return Name;
    }
}
