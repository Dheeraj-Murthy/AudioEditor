package com.meenigam.Components;

import javax.swing.*;
        import java.awt.*;
        import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clip extends JPanel {
    private fileComponent fileComponent;

    public Clip(fileComponent file, Track track) {
        this.fileComponent = file;
        setBackground(new Color(100, 100, 100)); // Visual indicator of a clip
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(100, 100)); // Adjust as needed
//        setLayout(new FlowLayout());
        // Make it draggable
        MouseAdapter mouseHandler = new MouseAdapter() {
            private Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                location.translate(e.getX() - offset.x, 0);
                setLocation(location);
                getParent().repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    // Additional methods for interacting with the fileComponent
    public fileComponent getFileComponent() {
        return fileComponent;
    }


}