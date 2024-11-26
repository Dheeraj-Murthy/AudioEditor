package com.meenigam.Panels;

import com.meenigam.Components.FileComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StagingArea extends JPanel {

    private final DefaultListModel<FileComponent> listModel; // List model to manage data
    private final StagingArea stagingArea;
    private TrackEditor trackEditor;
    private int width = 200;


    public StagingArea(Frame frame) {
        setBackground(new Color(45, 45, 45)); // Background color
        setLayout(new BorderLayout());

        stagingArea = this;
        // Title label
        JLabel title = new JLabel("Staging Area", JLabel.CENTER);
        title.setPreferredSize(new Dimension(200, 20));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH); // Add title at the top

        // Create DefaultListModel for JList
        listModel = new DefaultListModel<FileComponent>();
//        listModel.addElement(new fileComponent("hello", "./hello.txt", frame));
//        listModel.addElement(new fileComponent("world", "./hello1.txt", frame));
//        listModel.addElement(new fileComponent("how are you", "./hello2.txt", frame));

        // Create JList with the model
        JScrollPane scrollPane = getjScrollPane(listModel, stagingArea);
        scrollPane.setPreferredSize(new Dimension(width, this.getHeight()));
        add(scrollPane, BorderLayout.CENTER); // Add scroll pane in the center

        // Create "Add File" button
        JButton addFile = new JButton("Add File");
        addFile.setBackground(Color.BLACK);
        addFile.setForeground(Color.WHITE);
        addFile.setOpaque(true);
        addFile.setBorderPainted(false);
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

                if (fileName != null && fileName.toLowerCase().endsWith(".wav")) { // If the user selected a file
                    String fullPath = directory + fileName;
                    System.out.println("Selected File: " + fullPath);
                    // Add the file name to the list
                    listModel.addElement(new FileComponent(fileName, fullPath, frame, stagingArea));
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a valid file");
                }
            }
        });
    }

    private JScrollPane getjScrollPane(DefaultListModel<FileComponent> listModel, StagingArea stagingArea) {
        JList<FileComponent> fileList = new JList<FileComponent>(listModel);
        fileList.setBackground(new Color(60, 60, 60));
        fileList.setForeground(Color.WHITE);
        fileList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    int index = fileList.getSelectedIndex();
                    FileComponent fileComponent = listModel.get(index);
                    fileComponent.clicked(stagingArea);
                }
            }
        });

        // Add the JList to a scroll pane
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setSize(new Dimension(this.width, stagingArea.getHeight()));
        scrollPane.setBackground(new Color(60, 60, 60));
        return scrollPane;
    }

    public void addToTrack(FileComponent file) {
        trackEditor.addNewTrack(file);
    }

    public void setTrackEditor(TrackEditor trackEditor) {
        this.trackEditor = trackEditor;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public void delete(FileComponent fileComponent) {
        listModel.removeElement(fileComponent);
    }
}