package com.meenigam.Components;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavFileCreator {

    /**
     * Creates a blank .wav file with the specified path and duration.
     *
     * @param filePath          The path to create the .wav file.
     * @param durationInSeconds The duration of the blank audio in seconds.
     * @throws IOException If an I/O error occurs.
     */
    public static void createBlankWav(String filePath, int durationInSeconds) throws IOException {
        // Define the audio format: 44100 Hz, 16-bit, mono, signed, little-endian
        float sampleRate = 44100.0f;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;

        // Create an audio format
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Calculate the number of bytes for the blank audio data
        int bytesPerFrame = format.getFrameSize();
        long numFrames = (long) (sampleRate * durationInSeconds);
        long bufferSize = numFrames * bytesPerFrame;

        if (bufferSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The duration is too long to create the WAV file.");
        }

        // Create an empty byte array for the blank audio
        byte[] blankAudioData = new byte[(int) bufferSize];

        // Create an AudioInputStream using the blank audio data
        AudioInputStream audioInputStream = new AudioInputStream(
                new java.io.ByteArrayInputStream(blankAudioData),
                format,
                numFrames
        );

        // Write the blank .wav file to disk
        File outputFile = new File(filePath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);

        System.out.println("Blank .wav file created at: " + outputFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        try {
            // Specify the output file path and duration
            String filePath = "blank.wav";
            int durationInSeconds = 10; // Create a 1000-second blank .wav file

            createBlankWav(filePath, durationInSeconds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}