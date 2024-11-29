package com.meenigam;


import com.meenigam.Components.Clip;
import com.meenigam.Components.Track;
import com.meenigam.Panels.ControlPanel;
import com.meenigam.Panels.StagingArea;
import com.meenigam.Panels.TrackEditor;
import com.meenigam.Utils.PanelFocusAdapter;
import com.meenigam.Utils.callNative;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Frame extends JFrame {

    private Manager manager;

    private int mouseX, mouseY;
    private boolean maximized = false;
    private Dimension previousSize;
    private Point previousLocation;
    private ControlPanel controlPanel;

    private final StagingArea stagingArea;
    private final TrackEditor trackEditor;
    private JSlider slider;

    public Frame(Manager manager) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super("Audio Editor");
        this.manager = manager;

        // Add control panel at the bottom
        ControlPanel controlPanel = new ControlPanel(this, manager.finalFilePath);
        this.controlPanel = controlPanel;

        this.slider = controlPanel.getProgressSlider();

        // Remove default decorations
        setUndecorated(true);
        setSize(1024, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);

        stagingArea = new StagingArea(this);
        trackEditor = new TrackEditor(this, this.slider);
        stagingArea.setTrackEditor(trackEditor);
        trackEditor.setStagingArea(stagingArea);

        // Wrap both panels in JScrollPane
        JScrollPane stagingScrollPane = new JScrollPane(stagingArea);
        JScrollPane trackScrollPane = new JScrollPane(trackEditor);

        stagingScrollPane.setPreferredSize(new Dimension(200, this.getHeight()));
        stagingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add focus listeners to each scroll pane
        stagingScrollPane.addMouseListener(new PanelFocusAdapter(stagingScrollPane, this));
        trackScrollPane.addMouseListener(new PanelFocusAdapter(trackScrollPane, this));
        trackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        trackScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        reset(this);

        // Add the staging area on the left
        add(stagingScrollPane, BorderLayout.WEST);
        // Add the track editor in the center
        add(trackScrollPane, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
        // Start the timer to trigger repaint every second
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start(); // Start the timer
    }

    public void setSlider(JSlider slider) {
        trackEditor.setSlider(slider);

    }

    public Path getHomeDir() { return Path.of(manager.getTempLocation()).getParent(); }

    private void reset(JFrame frame) {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JScrollPane sp) {
                sp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        }
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(45, 45, 45));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        container.setBackground(new Color(0, 0, 0, 0));

        JButton setHome = new JButton("Project");
        setHome.setForeground(Color.white);
        setHome.setBackground(new Color(0, 0, 0));
        setHome.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(10, 20, 10, 20)));
        setHome.setFocusPainted(false);
        setHome.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(setHome);
        setHome.addActionListener(e -> homePathDialog());

//        JPanel spacer = new JPanel(); // Adjust the number of spaces for desired spacing
//        container.add(spacer);
        container.add(Box.createRigidArea(new Dimension(10, 3)));

        JButton export = new JButton("Export");
        export.setForeground(Color.white);
        export.setBackground(new Color(0, 0, 0));
        export.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(10, 20, 10, 20)));
        export.setFocusPainted(false);
        export.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(export);
        export.addActionListener(e -> {
            try {
                exportPathDialog();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        container.add(Box.createRigidArea(new Dimension(10, 3)));

        JButton saveButton = new JButton("Save");
        saveButton.setForeground(Color.white);
        saveButton.setBackground(new Color(0, 0, 0));
        saveButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(10, 20, 10, 20)));
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(saveButton);
        saveButton.addActionListener(e -> updateMaster());

        titleBar.add(container, BorderLayout.WEST);


        JLabel titleLabel = new JLabel("Audio Editor", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        styleButton(closeButton);
        closeButton.addActionListener(e -> System.exit(0));
        titleBar.add(closeButton, BorderLayout.EAST);

        // Enable dragging the window
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!maximized) {
                    int x = e.getXOnScreen() - mouseX;
                    int y = e.getYOnScreen() - mouseY;
                    setLocation(x, y);
                }
            }
        });

        return titleBar;
    }

    public TrackEditor getTrackEditor() {
        return trackEditor;
    }
    private void homePathDialog() {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setDialogTitle("Choose Parent Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            manager.setHomePath(path);
            JOptionPane.showMessageDialog(this, "Project directory set to:\n" + path,
                    "Directory Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportPathDialog() throws IOException {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setDialogTitle("Choose export location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            manager.setSavePath(path);
        }

        String userInput = JOptionPane.showInputDialog(
                null,
                "Please enter name of audio file: ",
                "Name: ",
                JOptionPane.QUESTION_MESSAGE
        );
        if (userInput != null) {
            manager.setSavePath(manager.getSavePath() + "/" + userInput + ".wav");
        } else {
            manager.setSavePath(manager.getSavePath() + "/finalAudio.wav");
        }
        export(Path.of(manager.getTempLocation()), Path.of(manager.getSavePath()));
    }

    private void updateMaster() {
        for (Track track : TrackEditor.getTracks()) {
            ArrayList<Clip> clips = track.getClips();
            for (Clip clip : clips) {
                String[] param = {clip.getPath(), String.valueOf(clip.getStart())};
                System.out.println( "clip.getStanrt: " + clip.getStart() + " after multiplication: " + param[1]);
                callNative.callCode(manager.finalFilePath, 10, param);
            }
        }
        controlPanel.loadAudio(manager.finalFilePath);
    }

    private void export(Path oldLoc, Path newLoc) throws IOException {
        updateMaster();
        Files.copy(oldLoc, newLoc, StandardCopyOption.REPLACE_EXISTING); //todo: Trimming
        float maxEnd = 0.0f;
        for (Track track : TrackEditor.getTracks()) {
            ArrayList<Clip> clips = track.getClips();
            for (Clip clip : clips) {
                if (maxEnd < clip.getEnd())
                    maxEnd = clip.getEnd();
            }
        }
        String[] param = {String.valueOf(maxEnd * 1000), String.valueOf(1)};
        callNative.callCode(newLoc.toString(), 2, param);
        JOptionPane.showMessageDialog(this, "File saved as:\n" + newLoc,
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleMaximize() {
        if (maximized) {
            setSize(previousSize);
            setLocation(previousLocation);
            repaint();
            revalidate();
        } else {
            previousSize = getSize();
            previousLocation = getLocation();

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = env.getMaximumWindowBounds();
            setBounds(screenBounds);
            repaint();
            revalidate();
        }
        maximized = !maximized;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }
}