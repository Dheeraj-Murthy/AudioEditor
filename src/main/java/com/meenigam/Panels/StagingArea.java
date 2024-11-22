package com.meenigam.Panels;

import com.meenigam.Components.fileComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StagingArea extends JPanel {

    private final DefaultListModel<fileComponent> listModel; // List model to manage data
    private final StagingArea stagingArea;
    private TrackEditor trackEditor;

    public StagingArea(Frame frame) {
        setBackground(new Color(45, 45, 45)); // Background color
        setLayout(new BorderLayout());

        stagingArea = this;
        // Title label
        JLabel title = new JLabel("Staging Area", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH); // Add title at the top

        // Create DefaultListModel for JList
        listModel = new DefaultListModel<fileComponent>();
        listModel.addElement(new fileComponent("hello", "./hello.txt", frame));
        listModel.addElement(new fileComponent("world", "./hello1.txt", frame));
        listModel.addElement(new fileComponent("how are you", "./hello2.txt", frame));

        // Create JList with the model
        JList<fileComponent> fileList = new JList<fileComponent>(listModel);
        fileList.setBackground(new Color(60, 60, 60));
        fileList.setForeground(Color.WHITE);
        fileList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
                int index = fileList.getSelectedIndex();
                fileComponent fileComponent = listModel.get(index);
                fileComponent.clicked(stagingArea);
            }
        });

        // Add the JList to a scroll pane
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBackground(new Color(60, 60, 60));
        add(scrollPane, BorderLayout.CENTER); // Add scroll pane in the center

        // Create "Add File" button
        Button addFile = new Button("Add File");
        add(addFile, BorderLayout.SOUTH);

        // Button action to open a file dialog and add the selected file to the list
        addFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Button pressed");
                FileDialog fileDialog = new FileDialog((Frame) null, "Add File", FileDialog.LOAD);
                fileDialog.setVisible(true);

                // Get selected file name and directory
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();

                if (fileName != null) { // If the user selected a file
                    String fullPath = directory + fileName;
                    System.out.println("Selected File: " + fullPath);

                    // Add the file name to the list
                    listModel.addElement(new fileComponent(fileName, fullPath, frame));
                }
            }
        });
    }

    public void addToTrack(fileComponent file) {
        trackEditor.addNewTrack(file);
    }

    public void setTrackEditor(TrackEditor trackEditor) {
        this.trackEditor = trackEditor;
    }
}