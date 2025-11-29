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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
    private Track selectedTrack;
    private JButton floatingEditButton;

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
        
        // Ensure the frame stays alive and prevents JVM exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Application closing...");
                System.exit(0);
            }
        });

        getContentPane().setBackground(new Color(30, 30, 30));
        
        // Use regular BorderLayout for main content
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

        // Create floating edit button using JLayeredPane overlay
        createFloatingEditButton();

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

//        JButton setHome = new JButton("Project");
//        setHome.setForeground(Color.white);
//        setHome.setBackground(new Color(0, 0, 0));
//        setHome.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(10, 20, 10, 20)));
//        setHome.setFocusPainted(false);
//        setHome.setFont(new Font("Arial", Font.BOLD, 14));
//        container.add(setHome);
//        setHome.addActionListener(e -> homePathDialog());
//
//        container.add(Box.createRigidArea(new Dimension(10, 3)));

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
        closeButton.addActionListener(e -> {
            try {
                terminateProgram();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
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
        Files.copy(oldLoc, newLoc, StandardCopyOption.REPLACE_EXISTING);
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

    private void terminateProgram() throws IOException {
        Path folder = Path.of(manager.finalFilePath).getParent();

        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error deleting folder and its contents.");
        }

        System.exit(0);
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

    private void createFloatingEditButton() {
        floatingEditButton = new JButton("⋮ Edit");
        floatingEditButton.setBackground(new Color(80, 80, 120));
        floatingEditButton.setForeground(Color.WHITE);
        floatingEditButton.setFocusPainted(false);
        floatingEditButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 160)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        floatingEditButton.setFont(new Font("Arial", Font.BOLD, 12));
        floatingEditButton.setToolTipText("Edit audio for selected track");
        
        // Set button size and make it non-opaque for better visual effect
        floatingEditButton.setPreferredSize(new Dimension(80, 30));
        floatingEditButton.setOpaque(true);
        
        // Add hover effect
        floatingEditButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                floatingEditButton.setBackground(new Color(100, 100, 140));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                floatingEditButton.setBackground(new Color(80, 80, 120));
            }
        });
        
        // Add click action
        floatingEditButton.addActionListener(e -> showAudioEditingMenu());
        
        // Use the glass pane approach for floating button
        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Make glass pane transparent except for our button
                setOpaque(false);
            }
        };
        glassPane.setOpaque(false);
        glassPane.setLayout(null); // Use absolute positioning
        
        // Add the button to the glass pane
        glassPane.add(floatingEditButton);
        
        // Set the glass pane
        setGlassPane(glassPane);
        glassPane.setVisible(true);
        
        // Position the button in the top-right corner
        updateFloatingButtonPosition();
        
        // Add component listener to update button position on resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateFloatingButtonPosition();
            }
        });
    }
    
    public void showAudioEditingMenu() {
        if (selectedTrack == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a track first by clicking on it.",
                    "No Track Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if track has clips
        if (selectedTrack.getClips().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add audio clips to the track first.",
                    "No Audio Clips",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show the same menu as the original track button
        ArrayList<String> options = new ArrayList<>(java.util.Arrays.asList(
                "Details", "Loop", "Trim", "Clip Gain", "Frequency Scaling", 
                "Time Scaling", "Compressing", "Pitch Filter", "Normalize", 
                "Reverb", "Delete Clip"
        ));
        String[] opts = options.toArray(new String[0]);
        
        JComboBox<String> comboBox = new JComboBox<>(opts);
        int result = JOptionPane.showConfirmDialog(
                this,
                comboBox,
                "Edit Audio - " + selectedTrack.toString(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String selectedOption = (String) comboBox.getSelectedItem();
            if (selectedOption != null) {
                processAudioEditingOption(selectedOption, selectedTrack);
            }
        }
    }
    
    private void processAudioEditingOption(String selectedOption, Track track) {
        // This method contains the same logic as the original Track.java button handler
        // We'll extract the common logic to avoid duplication
        java.util.ArrayList<com.meenigam.Components.Clip> clips = track.getClips();
        if (clips.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please add clips first!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        String filePath = clips.get(0).getFileComponent().getFilePath();
        
        try {
            switch (selectedOption) {
                case "Details":
                    String[] param = {};
                    com.meenigam.Utils.callNative.callCode(filePath, 0, param);
                    break;
                case "Loop":
                    String userInput = JOptionPane.showInputDialog(
                            null,
                            "Please input loop count: ",
                            "Loops: ",
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (userInput != null) {
                        double loop = Integer.parseInt(userInput);
                        String[] loopParam = {String.valueOf(loop)};
                        com.meenigam.Utils.callNative.callCode(filePath, 1, loopParam);
                    }
                    break;
                case "Trim":
                    java.util.ArrayList<String> params = new java.util.ArrayList<>(java.util.Arrays.asList("Time stamp", "choose part(1/2)"));
                    java.util.Map<String, String> input = com.meenigam.Components.MultiInputDialog.getUserInputs(params);
                    if (input != null) {
                        double threshold = Double.parseDouble(input.get(params.get(0)));
                        double ratio = Double.parseDouble(input.get(params.get(1)));
                        String[] trimParam = {String.valueOf(threshold), String.valueOf(ratio)};
                        com.meenigam.Utils.callNative.callCode(filePath, 2, trimParam);
                    }
                    break;
                case "Clip Gain":
                    String gainInput = JOptionPane.showInputDialog(
                            null,
                            "Please input frequency factor: ",
                            "Factor: ",
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (gainInput != null) {
                        double factor = Double.parseDouble(gainInput);
                        String[] gainParam = {String.valueOf(factor)};
                        com.meenigam.Utils.callNative.callCode(filePath, 3, gainParam);
                    }
                    break;
                case "Frequency Scaling":
                    String freqInput = JOptionPane.showInputDialog(
                            null,
                            "Please input amplitude factor: ",
                            "Factor: ",
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (freqInput != null) {
                        double freqFactor = Double.parseDouble(freqInput);
                        String[] freqParam = {String.valueOf(freqFactor)};
                        com.meenigam.Utils.callNative.callCode(filePath, 4, freqParam);
                    }
                    break;
                case "Time Scaling":
                    String timeInput = JOptionPane.showInputDialog(
                            null,
                            "Please input desired duration: ",
                            "Duration (in ms): ",
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (timeInput != null) {
                        double duration = Double.parseDouble(timeInput);
                        duration = Math.round(duration * 1000) / 1000.0;
                        String[] timeParam = {String.valueOf(duration)};
                        com.meenigam.Utils.callNative.callCode(filePath, 5, timeParam);
                    }
                    break;
                case "Compressing":
                    java.util.ArrayList<String> compressParams = new java.util.ArrayList<>(java.util.Arrays.asList("Threshold Frequency", "Compression Ratio"));
                    java.util.Map<String, String> compressInput = com.meenigam.Components.MultiInputDialog.getUserInputs(compressParams);
                    if (compressInput != null) {
                        double threshold = Double.parseDouble(compressInput.get(compressParams.get(0)));
                        double ratio = Double.parseDouble(compressInput.get(compressParams.get(1)));
                        String[] compressParam = {String.valueOf(threshold), String.valueOf(ratio)};
                        com.meenigam.Utils.callNative.callCode(filePath, 6, compressParam);
                    }
                    break;
                case "Pitch Filter":
                    java.util.ArrayList<String> pitchParams = new java.util.ArrayList<>(java.util.Arrays.asList("Cutoff Frequency", "Filter Type (H/L)"));
                    java.util.Map<String, String> pitchInput = com.meenigam.Components.MultiInputDialog.getUserInputs(pitchParams);
                    if (pitchInput != null) {
                        double cutoff = Double.parseDouble(pitchInput.get(pitchParams.get(0)));
                        String type = pitchInput.get(pitchParams.get(1));
                        String[] pitchParam = {String.valueOf(cutoff), type};
                        com.meenigam.Utils.callNative.callCode(filePath, 7, pitchParam);
                    }
                    break;
                case "Normalize":
                    String[] normParam = {};
                    com.meenigam.Utils.callNative.callCode(filePath, 8, normParam);
                    break;
                case "Reverb":
                    String[] reverbLevels = {"Low", "Medium", "High"};
                    String selectedReverbLevel = (String) JOptionPane.showInputDialog(null,
                            "Select Reverb Level:",
                            "Reverb Setting",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            reverbLevels,
                            reverbLevels[1]);
                    
                    int reverbLevelInt = 1;
                    if (selectedReverbLevel != null) {
                        if (selectedReverbLevel.equals("Medium")) {
                            reverbLevelInt = 2;
                        } else if (selectedReverbLevel.equals("High")) {
                            reverbLevelInt = 3;
                        }
                        String[] reverbParam = {String.valueOf(reverbLevelInt)};
                        com.meenigam.Utils.callNative.callCode(filePath, 9, reverbParam);
                    }
                    break;
                case "Delete Clip":
                    clips.removeFirst();
                    track.revalidate();
                    track.repaint();
                    break;
                default:
                    JOptionPane.showMessageDialog(
                            null,
                            "Something went wrong",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
            }
            
            // Refresh the track after processing
            track.resetClipContainer();
            if (!clips.isEmpty()) {
                com.meenigam.Components.Clip clip = clips.getFirst();
                clips.removeFirst();
                track.setClip(clip.getFileComponent());
                track.resetClipContainer();
                track.repaint();
                if (!clips.isEmpty()) {
                    clips.getFirst().reset();
                    clips.getFirst().repaint();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Invalid input. Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public void setSelectedTrack(Track track) {
        // Deselect previous track
        if (this.selectedTrack != null && this.selectedTrack != track) {
            this.selectedTrack.setSelected(false);
        }
        
        this.selectedTrack = track;
        
        // Select new track
        if (track != null) {
            track.setSelected(true);
        }
        
        // Update button to show which track is selected
        if (floatingEditButton != null) {
            floatingEditButton.setText("⋮ Edit" + (track != null ? " (" + track.toString() + ")" : ""));
        }
    }
    
    public Track getSelectedTrack() {
        return selectedTrack;
    }
    
    private void updateFloatingButtonPosition() {
        if (floatingEditButton != null) {
            int buttonWidth = 80;
            int buttonHeight = 30;
            int xOffset = 100; // Distance from right edge
            int yOffset = 60;  // Distance from top edge
            
            floatingEditButton.setBounds(
                getWidth() - xOffset - buttonWidth, 
                yOffset, 
                buttonWidth, 
                buttonHeight
            );
        }
    }
}