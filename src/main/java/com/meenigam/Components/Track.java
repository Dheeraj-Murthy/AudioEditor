package com.meenigam.Components;

import com.meenigam.Panels.TrackEditor;
import com.meenigam.Utils.callNative;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
                    ArrayList<String> options = new ArrayList<>(Arrays.asList("Details", "Loop", "Trim", "Clip Gain", "Frequency Scaling", "Time Scaling", "Compressing", "Pitch Filter", "Normalize", "Reverb", "Delete Clip"));
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
                        String filePath = clips.get(0).getFileComponent().getFilePath();
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
                            case "Details":
                                try {
                                    String[] param = {};
                                    callNative.callCode(filePath, 0, param);
                                } catch (Exception E) {
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                break;
                            case "Loop":
                                try {
                                    String userInput = JOptionPane.showInputDialog(
                                            null,
                                            "Please input loop count: ",
                                            "Loops: ",
                                            JOptionPane.QUESTION_MESSAGE
                                    );

                                    // Check if the user clicked "Cancel" or closed the dialog
                                    if (userInput == null) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }

                                    double loop = Integer.parseInt(userInput);
                                    // todo: call native with filename, option, and count
                                    String[] param = {String.valueOf(loop)};
                                    callNative.callCode(filePath, 1, param);

                                } catch (NumberFormatException E) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                break;
                            case "Trim":
                                try {
                                    ArrayList<String> params = new ArrayList<String>(Arrays.asList("Time stamp", "choose part(1/2)"));
                                    Map<String, String> input = MultiInputDialog.getUserInputs(params);

                                    if (input != null) {
                                        double threshold = Double.parseDouble(input.get(params.get(0)));
                                        double ratio = Double.parseDouble(input.get(params.get(1)));
                                        String[] param = {String.valueOf(threshold), String.valueOf(ratio)};
                                        callNative.callCode(filePath, 2, param);
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }
                                } catch (Exception ex) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                break;
                            case "Clip Gain":
                                try {
                                    String userInput = JOptionPane.showInputDialog(
                                            null,
                                            "Please input frequency factor: ",
                                            "Factor: ",
                                            JOptionPane.QUESTION_MESSAGE
                                    );

                                    // Check if the user clicked "Cancel" or closed the dialog
                                    if (userInput == null) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }

                                    double factor = Double.parseDouble(userInput);
                                    // todo: call native with filename, option, and count
                                    String[] param = {String.valueOf(factor)};
                                    callNative.callCode(filePath, 3, param);

                                } catch (NumberFormatException E) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;
                            case "Frequency Scaling":
                                try {
                                    String userInput = JOptionPane.showInputDialog(
                                            null,
                                            "Please input amplitude factor: ",
                                            "Factor: ",
                                            JOptionPane.QUESTION_MESSAGE
                                    );

                                    // Check if the user clicked "Cancel" or closed the dialog
                                    if (userInput == null) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }

                                    double factor = Double.parseDouble(userInput);
                                    // todo: call native with filename, option, and count
                                    String[] param = {String.valueOf(factor)};
                                    callNative.callCode(filePath, 4, param);

                                } catch (NumberFormatException E) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;
                            case "Time Scaling":
                                try {
                                    String userInput = JOptionPane.showInputDialog(
                                            null,
                                            "Please input desired duration: ",
                                            "Duration (in ms): ",
                                            JOptionPane.QUESTION_MESSAGE
                                    );

                                    // Check if the user clicked "Cancel" or closed the dialog
                                    if (userInput == null) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }

                                    double duration = Double.parseDouble(userInput);
                                    duration = Math.round(duration * 1000) / 1000.0;
                                    // todo: call native with filename, option, and count
                                    String[] param = {String.valueOf(duration)};
                                    callNative.callCode(filePath, 5, param);

                                } catch (NumberFormatException E) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;

                            case "Compressing":
                                try {
                                    ArrayList<String> params = new ArrayList<String>(Arrays.asList("Threshold Frequency", "Compression Ratio"));
                                    Map<String, String> input = MultiInputDialog.getUserInputs(params);

                                    if (input != null) {
                                        double threshold = Double.parseDouble(input.get(params.get(0)));
                                        double ratio = Double.parseDouble(input.get(params.get(1)));
                                        String[] param = {String.valueOf(threshold), String.valueOf(ratio)};
                                        callNative.callCode(filePath, 6, param);
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }
                                } catch (Exception ex) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;
                            case "Pitch Filter":
                                try {
                                    ArrayList<String> params = new ArrayList<String>(Arrays.asList("Cutoff Frequency", "Filter Type (H/L)"));
                                    Map<String, String> input = MultiInputDialog.getUserInputs(params);

                                    if (input != null) {
                                        double cutoff = Double.parseDouble(input.get(params.get(0)));
                                        String type = input.get(params.get(1));
                                        String[] param = {String.valueOf(cutoff), type};
                                        callNative.callCode(filePath, 7, param);

                                    } else {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Input was canceled.",
                                                "Canceled",
                                                JOptionPane.WARNING_MESSAGE
                                        );
                                        return; // Exit the loop and terminate
                                    }
                                } catch (Exception ex) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;
                            case "Normalize":
                                try {
                                    String[] param = {};
                                    callNative.callCode(filePath, 8, param);
                                } catch (Exception ex) {
//                                    JOptionPane.showMessageDialog;
                                }

                                break;
                            case "Reverb":
                                try {
                                    String[] reverbLevels = {"Low", "Medium", "High"};
                                    String selectedReverbLevel = (String) JOptionPane.showInputDialog(null,
                                            "Select Reverb Level:",
                                            "Reverb Setting",
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            reverbLevels,
                                            reverbLevels[1]);

                                    int reverbLevelInt = 1;
                                    if (selectedReverbLevel.equals("Medium")) {
                                        reverbLevelInt = 2;
                                    } else if (selectedReverbLevel.equals("High")) {
                                        reverbLevelInt = 3;
                                    }
                                    // todo: call native with filename, option, and count
                                    String[] param = {String.valueOf(reverbLevelInt)};
                                    callNative.callCode(filePath, 9, param);

                                } catch (Exception E) {
                                    // Show error message if input is not an integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid input. Please enter a valid integer.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                break;
                            case "Delete Clip":
                                try {
                                    System.out.println("calling delete");
                                    clips.removeFirst();
                                    revalidate();
                                    repaint();
                                } catch (Exception E) {
                                    System.out.println(Arrays.toString(E.getStackTrace()));
                                }
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
                    resetClipContainer();
                    if (clips.isEmpty()) {
                        return;
                    }
                    Clip clip = clips.getFirst();
                    clips.removeFirst();
                    setClip(clip.getFileComponent());
                    resetClipContainer();
                    repaint();
                    clips.getFirst().reset();
                    clips.getFirst().repaint();
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
        Clip clip = new Clip(new FileComponent(fileComponent), this);
        clips.add(clip);
        clipContainer.add(clip); // Add to clip container for horizontal layout
        revalidate();
        repaint();
        clip.repaint();
    }

    public JPanel getClipContainer() {
        return clipContainer;
    }
    public void resetClipContainer() {
        clipContainer.removeAll();
        for(Clip clip: clips) {
            clipContainer.add(clip);
        }
        revalidate();
        repaint();
    }

    public String toString() {
        return this.title;
    }
}