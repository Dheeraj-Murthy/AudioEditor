package com.meenigam.Components;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WaveformPanel extends JPanel {

    private float[] audioSamples;
    private Clip clip;

    public WaveformPanel(File audioFile, Clip clip) {
        this.clip = clip;
        try {
            audioSamples = readAudioData(audioFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSize(clip.getWidth(), clip.getHeight()); // Set the size of the panel

        // Start a timer to repaint the panel every second
        Timer timer = new Timer(1000, e -> repaint());
        timer.start(); // Start the timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (audioSamples == null) return;
        setBackground(new Color(50, 50, 50)); // Set the background color to dark gray

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN); // Set the waveform color to green

        int width = getWidth(); // Get the width of the panel
        int height = getHeight(); // Get the height of the panel
        int middle = height / 2; // Find the middle of the panel to center the waveform

        g2d.fillRect(0, middle - 1, width, 2); // Draw the middle line

        // Find the maximum sample value for scaling
        float max = 0;
        for (float audioSample : audioSamples) {
            if (audioSample > max) {
                max = audioSample;
            }
        }

        // Scale factor to fit the waveform within the panel height
        float scale = height / (middle * max);

        // Draw the waveform based on the audio samples
        for (int i = 0; i < width; i++) {
            // Map the screen width to the audio sample array
            int sampleIndex = (int) ((i / (float) width) * audioSamples.length);
            int amplitude = (int) (audioSamples[sampleIndex] * middle * scale); // Scale the sample

            // Draw a line for each sample at the corresponding position
            g2d.drawLine(i, middle - amplitude, i, middle + amplitude);
        }
    }

    // Method to read audio data from the file and convert to PCM samples
    private float[] readAudioData(File audioFile) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioInputStream.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            throw new UnsupportedAudioFileException("Only PCM signed format supported");
        }

        byte[] audioBytes = audioInputStream.readAllBytes();
        int sampleSizeInBits = format.getSampleSizeInBits();
        int numSamples = audioBytes.length / (sampleSizeInBits / 8);

        float[] audioData = new float[numSamples];

        // Convert bytes to PCM samples
        for (int i = 0; i < numSamples; i++) {
            int sample;
            if (sampleSizeInBits == 16) {
                sample = (audioBytes[i * 2 + 1] << 8) | (audioBytes[i * 2] & 0xFF); // 16-bit PCM
            } else {
                sample = audioBytes[i]; // 8-bit PCM
            }
            audioData[i] = sample / (float) (1 << (sampleSizeInBits - 1)); // Normalize to range [-1, 1]
        }

        return audioData;
    }
}