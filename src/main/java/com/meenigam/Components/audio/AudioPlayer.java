package com.meenigam.Components.audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip audioClip;
    private boolean isPlaying = false;
    private JSlider progressSlider;
    private Timer timer;

    public AudioPlayer(String filePath) {
        try {
            // Load the audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            // Create the GUI
//            createGUI();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Audio Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new BorderLayout());

        // Create buttons
        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");

        // Progress slider
        progressSlider = new JSlider(0, (int) audioClip.getMicrosecondLength() / 1000, 0);
        progressSlider.setValue(0);

        // Panel for buttons
        JPanel controlPanel = new JPanel();
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);

        // Add components to the frame
        frame.add(progressSlider, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        playButton.addActionListener(e -> playAudio());
        pauseButton.addActionListener(e -> pauseAudio());

        // Add a timer to update the progress slider
        timer = new Timer(100, e -> updateSlider());
        timer.start();

        frame.setVisible(true);
    }

    private void playAudio() {
        if (!isPlaying) {
            audioClip.start();
            isPlaying = true;
        }
    }

    private void pauseAudio() {
        if (isPlaying) {
            audioClip.stop();
            isPlaying = false;
        }
    }

    private void updateSlider() {
        if (isPlaying) {
            int currentPos = (int) audioClip.getMicrosecondPosition() / 1000;
            progressSlider.setValue(currentPos);

            // Reset when playback is complete
            if (currentPos >= progressSlider.getMaximum()) {
                isPlaying = false;
                audioClip.stop();
                audioClip.setMicrosecondPosition(0);
                progressSlider.setValue(0);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AudioPlayer("/Users/dheerajmurthy/Downloads/PinkPanther60.wav"));
    }
}