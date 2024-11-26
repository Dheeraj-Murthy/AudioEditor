package com.meenigam.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clip extends JPanel {
    private final FileComponent fileComponent;
    private float start;
    private float end;
    private final float size;
    private WaveformPanel waveformPanel;
    private Track track;

    public Clip(FileComponent file, Track track) {
        this.fileComponent = file;
        this.track = track;
        this.waveformPanel = new WaveformPanel(file.getFile(), this);
        this.waveformPanel.setSize(getWidth(), getHeight());
        setLayout(new BorderLayout());
        add(waveformPanel, BorderLayout.CENTER);

        System.out.println(file.getDuration());
        setBackground(new Color(100, 100, 0)); // Visual indicator of a clip
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension((int) file.getDuration() * 10, track.getClipContainer().getHeight())); // Adjust as needed
        setOpaque(true);
        this.size = file.getDuration();
        this.start = 0;
        this.end = size;
        // Listen for changes in the parent's size
        track.getClipContainer().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateHeight();
            }
        });

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
                Point newLocation = new Point(location);
                newLocation.translate(e.getX() - offset.x, 0);
                if (isOutOfBounds(newLocation)) {
                    if (isLeft(newLocation)) {
                        setLocation(new Point(1, location.y));
                    } else if (isRight(newLocation)) {
                        setLocation(new Point(track.getClipContainer().getWidth() - (int) size * 10, location.y));
                    }
                    return;
                }

                Rectangle oldBounds = getBounds();
                setLocation(newLocation);
                Rectangle newBounds = getBounds();

                getParent().repaint(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height);
                getParent().repaint(newBounds.x, newBounds.y, newBounds.width, newBounds.height);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void updateHeight() {
        int newHeight = track.getClipContainer().getHeight();
        setPreferredSize(new Dimension(getPreferredSize().width, newHeight));
        revalidate(); // Notify Swing to update the layout
    }

    private boolean isOutOfBounds(Point location) {
        int clipLeft = location.x;  // Left edge of the clip
        int clipRight = clipLeft + (int) (size * 10);  // Right edge of the clip, based on size and scale

        // Check if the clip is out of bounds on the left or right
        return clipLeft < 0 || clipRight > track.getWidth();
    }

    private boolean isLeft(Point location) {
        int clipLeft = location.x;
        return clipLeft < 0;
    }

    private boolean isRight(Point location) {
        int clipRight = location.x + (int) (size * 10);  // Right edge of the clip, based on size and scale
        return clipRight > track.getWidth();
    }

    private void setPos(int loco) {
        this.start = loco;
        this.end = start + size;
    }

    // Additional methods for interacting with the fileComponent
    public FileComponent getFileComponent() {
        return fileComponent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}