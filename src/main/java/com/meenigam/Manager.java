package com.meenigam;

import com.meenigam.Components.WavFileCreator;
import com.meenigam.Utils.callNative;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Manager {
    Frame frame;
    private String HomePath = "Desktop";
    private String SavePath = "Desktop";
    private String ProjectFolder = "ProjectFiles";
    private String finalFile = "finalFile.wav";
    private String userHome = System.getProperty("user.home");
    public final String finalFilePath;

    public Manager() throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        File baseDir = new File(userHome, HomePath);
        // Check if base directory exists
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new IOException("Base path does not exist or is not a directory: " + HomePath);
        }
        // Create the new folder
        File newFolder = new File(baseDir, ProjectFolder);
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                throw new IOException("Failed to create folder: " + newFolder.getAbsolutePath());
            }
        }
        File newFile = new File(newFolder, finalFile);
//        WavFileCreator.createBlankWav(newFile.getAbsolutePath(), 130);
        String[] param = { String.valueOf(900) };
        callNative.callCode(newFile.getAbsolutePath(), -1, param);
        System.out.println(newFile.getAbsolutePath());
        finalFilePath = newFile.getAbsolutePath();

        frame = new Frame(this);
    }

    public void setHomePath(String homePath) {
        this.HomePath = homePath;
    }
    public void setSavePath(String savePath) { this.SavePath = savePath; }
}
