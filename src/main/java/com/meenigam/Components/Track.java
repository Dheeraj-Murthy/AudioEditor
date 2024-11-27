package com.meenigam.Components;

import com.meenigam.Panels.TrackEditor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Track extends JPanel {
    private final Color backgroundColor = new Color(50, 50, 50);
    private final Border border = BorderFactory.createLineBorder(new Color(70, 70, 70));
    private final Color foreground = Color.white;
    private final List<Track> tracks;
    private final JPanel clipContainer;  // Holds clips horizontally
    private final String title;
    private final TrackEditor trackEditor;
    private final ArrayList<Clip> clips;

    public Track(String title, List<Track> tracks, TrackEditor trackEditor) {
        this.tracks = tracks;
        this.title = title;
        this.trackEditor = trackEditor;
        this.clips = new ArrayList<>();
        setBackground(backgroundColor);

        // Combine an EmptyBorder (for padding) with the existing border
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10); // top, left, bottom, right
        Border lineBorder = BorderFactory.createLineBorder(new Color(70, 70, 70));
        setBorder(new CompoundBorder(lineBorder, padding));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setMaximumSize(new Dimension(10000, 10));
        topPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(foreground);
        topPanel.add(titleLabel, BorderLayout.WEST);

//        JButton buildButton = new JButton(":");
//        buildButton.setAlignmentX(Component.LEFT_ALIGNMENT);
//        buildButton.setForeground(foreground);
//        buildButton.setBackground(backgroundColor);
//        buildButton.setBorder(null);
//        topPanel.add(buildButton, BorderLayout.EAST);
        JButton buildButton = new JButton(":");
        buildButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        buildButton.setForeground(new Color(200, 200, 200)); // Light gray text color
        buildButton.setBackground(new Color(50, 50, 50));    // Darker gray background
        buildButton.setBorder(BorderFactory.createEmptyBorder());
        buildButton.setFocusPainted(false);
        buildButton.setContentAreaFilled(false);
        buildButton.setOpaque(true);
        buildButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), // Subtle border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)          // Padding inside the button
        ));
        buildButton.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(buildButton, BorderLayout.EAST);


        buildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buildButton.setBackground(new Color(80, 80, 80)); // Highlight on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buildButton.setBackground(new Color(50, 50, 50)); // Revert when not hovered
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ArrayList<String> options = new ArrayList<>(Arrays.asList("Loop", "Trim", "Clip Gain", "Frequency Scaling", "Time Scaling", "Compressing", "Pitch Filter", "Normalize", "Reverb"));
                    String[] opts = options.toArray(new String[0]);

                    JComboBox<String> comboBox = new JComboBox<>(opts);
                    int result = JOptionPane.showConfirmDialog(
                            trackEditor,
                            comboBox,
                            "Edit Clip",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (result == JOptionPane.OK_OPTION) {
                        String selectedOption = (String) comboBox.getSelectedItem();
                        int choice = 0;
                        for (int i = 0; i < options.size(); i++) {
                            String option = options.get(i);
                            if (option.equals(selectedOption)) {
                                choice = i;
                                break;
                            }
                        }
                        if (clips.size() == 0) {
                            JOptionPane.showMessageDialog(
                                    null, // Parent component, use `null` if you don't have one
                                    "Please add clips first!", // Message to display
                                    "Error", // Title of the dialog box
                                    JOptionPane.ERROR_MESSAGE // Error icon
                            );
                            return;
                        }
                        // todo: call the jni function with the option and the file name/path
                        System.out.println(clips.get(0).getFileComponent().getName() + choice);
                        if (selectedOption == null) {
                            JOptionPane.showMessageDialog(
                                    null, // Parent component, use `null` if you don't have one
                                    "Please select a valid option", // Message to display
                                    "Error", // Title of the dialog box
                                    JOptionPane.ERROR_MESSAGE // Error icon
                            );
                            return;
                        }
                        switch (selectedOption) {
                            case "Loop":

                                break;
                            case "Trim":

                                break;
                            case "Clip Gain":

                                break;
                            case "Frequency Scaling":

                                break;
                            case "Time Scaling":

                                break;
                            case "Compressing":

                                break;
                            case "Pitch Filter":

                                break;
                            case "Normalize":

                                break;
                            case "Reverb":

                                break;

                            default:
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Something went wrong",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );

                        }

                    } else {
                        System.out.println("Selected Option was cancelled");
                    }
                }
            }

        });

        add(topPanel, BorderLayout.NORTH);

        // Container for clips aligned horizontally
        clipContainer = new JPanel();
        clipContainer.setLayout(new FlowLayout(FlowLayout.LEFT)); // Left-align clips
        clipContainer.setOpaque(false); // Matches the background
        add(clipContainer, BorderLayout.CENTER);

        setPreferredSize(new Dimension(getWidth(), this.getHeight())); // Adjust dimensions as needed
        tracks.add(this);
    }

    public void addTracksToEditor(TrackEditor editor) {
        for (Track track : tracks) {
            editor.add(track);
        }
    }

    public void setClip(FileComponent fileComponent) {
        Clip clip = new Clip(fileComponent, this);
        clips.add(clip);
        clipContainer.add(clip); // Add to clip container for horizontal layout
        revalidate();
        repaint();
        clip.repaint();
    }

    public JPanel getClipContainer() {
        return clipContainer;
    }

    public String toString() {
        return this.title;
    }
}