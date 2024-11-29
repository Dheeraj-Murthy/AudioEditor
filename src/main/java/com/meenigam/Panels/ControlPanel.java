package com.meenigam.Panels;

import com.meenigam.Frame;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;


public class ControlPanel extends JPanel {
    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton stopButton;
    private final JSlider progressSlider;
    private Frame frame;
    private final JLabel timerLabel;

    private Clip audioClip;  // Clip for audio playback
    private boolean isPaused = false;  // Track pause state
    private long clipPosition = 0;  // Store current clip position
    private Timer progressTimer;  // Timer for updating slider progress

    public ControlPanel(Frame frame, String finalFilePath) {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 45));
        this.frame = frame;

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(45, 45, 45));

        // Sub-panel for buttons, centered
        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Centered buttons
        buttonSubPanel.setBackground(new Color(45, 45, 45));

        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        styleButton(playButton);
        styleButton(pauseButton);
        styleButton(stopButton);

        playButton.addActionListener(e -> playAudio());
        pauseButton.addActionListener(e -> pauseAudio());
        stopButton.addActionListener(e -> stopAudio());

        buttonSubPanel.add(playButton);
        buttonSubPanel.add(pauseButton);
        buttonSubPanel.add(stopButton);

        timerLabel = new JLabel("00:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Combine an EmptyBorder (for padding) with the existing border
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10); // top, left, bottom, right
        Border lineBorder = BorderFactory.createLineBorder(new Color(70, 70, 70));
        setBorder(new CompoundBorder(lineBorder, padding));

        // Add buttonSubPanel to the center of the buttonPanel
        buttonPanel.add(buttonSubPanel, BorderLayout.CENTER);

        // Add the timer label to the right end of the buttonPanel
        buttonPanel.add(timerLabel, BorderLayout.EAST);

        // Add the progress slider
        progressSlider = new JSlider();
        progressSlider.setBackground(new Color(45, 45, 45));
        progressSlider.setForeground(Color.WHITE);
        progressSlider.setValue(0);
        progressSlider.setEnabled(true);

        // Add listener to handle user seeking
        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (audioClip != null) {
                    int sliderValue = progressSlider.getValue();
                    double newClipPosition = (sliderValue / 100.0) * audioClip.getMicrosecondLength();
                    audioClip.setMicrosecondPosition((long) newClipPosition);

                    // Calculate current time (in seconds)
                    long currentTimeInSeconds = audioClip.getMicrosecondPosition() / 1000000 * 5 / 8; // Convert microseconds to seconds
                    long minutes = currentTimeInSeconds / 60;
                    long seconds = currentTimeInSeconds % 60;

                    // Format the timer label as MM:SS
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    timerLabel.setText(formattedTime);  // Update the timer label
                    System.out.println(currentTimeInSeconds);

                }
            }
        });

//        this.frame.setSlider(progressSlider);
        // Add components to the panel
        add(buttonPanel, BorderLayout.NORTH);
//        add(progressSlider, BorderLayout.SOUTH);

        loadAudio(finalFilePath);  // Specify the audio file path
    }

    public JSlider getProgressSlider() {
        return progressSlider;
    }


    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)  // Padding inside the button
        ));
    }

    private void loadAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            // Enable the slider after loading audio
            progressSlider.setEnabled(true);

            // Timer for updating slider progress
            progressTimer = new Timer(100, e -> updateSlider());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "Error loading audio file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playAudio() {
        if (audioClip != null) {
            if (isPaused) {
                audioClip.setMicrosecondPosition(clipPosition);
                isPaused = false;
            }
            audioClip.start();
            progressTimer.start();
        }
    }

    private void pauseAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            clipPosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            isPaused = true;
            progressTimer.stop();
        }
    }

    private void stopAudio() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);  // Reset to the start
            isPaused = false;
            clipPosition = 0;
            progressSlider.setValue(0);
            progressTimer.stop();
        }
    }

    private float getTime() {
        return progressSlider.getValue();
    }

    private void updateSlider() {
        if (audioClip != null && audioClip.isRunning()) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    long currentPos = audioClip.getMicrosecondPosition();
                    long totalLength = audioClip.getMicrosecondLength();
                    int progress = (int) ((currentPos / (double) totalLength) * 100);
                    progressSlider.setValue(progress);

                    // Calculate current time (in seconds)
                    long currentTimeInSeconds = currentPos / 1000000; // Convert microseconds to seconds
                    long minutes = currentTimeInSeconds / 60;
                    long seconds = currentTimeInSeconds % 60;

                    // Format the timer label as MM:SS
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    timerLabel.setText(formattedTime);  // Update the timer label
                    System.out.println(Float.parseFloat(formattedTime));

                    return null;
                }
            };
            worker.execute();
        }
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}