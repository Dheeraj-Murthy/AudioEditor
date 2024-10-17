package com.meenigam.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StagingArea extends JPanel {

    private DefaultListModel<String> listModel; // List model to manage data

    public StagingArea() {
        setBackground(new Color(45, 45, 45)); // Background color
        setLayout(new BorderLayout());

        // Title label
        JLabel title = new JLabel("Staging Area", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH); // Add title at the top

        // Create DefaultListModel for JList
        listModel = new DefaultListModel<>();
        listModel.addElement("Track 1");
        listModel.addElement("Track 2");
        listModel.addElement("Track 3");

        // Create JList with the model
        JList<String> fileList = new JList<>(listModel);
        fileList.setBackground(new Color(60, 60, 60));
        fileList.setForeground(Color.WHITE);

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
                System.out.println("Button pressed");
                FileDialog fileDialog = new FileDialog((Frame) null, "Add File", FileDialog.LOAD);
                fileDialog.setVisible(true);

                // Get selected file name and directory
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();

                if (fileName != null) { // If the user selected a file
                    String fullPath = directory + fileName;
                    System.out.println("Selected File: " + fullPath);

                    // Add the file name to the list
                    listModel.addElement(fullPath);
                }
            }
        });
    }
}