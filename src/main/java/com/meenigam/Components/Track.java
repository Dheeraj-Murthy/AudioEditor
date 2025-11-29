package com.meenigam.Components;

import com.meenigam.Panels.TrackEditor;
import com.meenigam.Utils.callNative;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Track extends JPanel {
    private final Color backgroundColor = new Color(50, 50, 50);
    private final Color selectedColor = new Color(70, 70, 90);
    private final Border border = BorderFactory.createLineBorder(new Color(70, 70, 70));
    private final Border selectedBorder = BorderFactory.createLineBorder(new Color(120, 120, 160));
    private final Color foreground = Color.white;
    private boolean isSelected = false;
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
        Border padding = BorderFactory.createEmptyBorder(0, 10, 10, 10); // top, left, bottom, right
        Border lineBorder = BorderFactory.createLineBorder(new Color(70, 70, 70));
        setBorder(new CompoundBorder(lineBorder, padding));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setMaximumSize(new Dimension(10000, 35));
        topPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(foreground);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 10f));
        topPanel.add(titleLabel, BorderLayout.WEST);


        // Add track selection listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Notify the frame that this track is selected
                if (trackEditor != null) {
                    // Find the frame instance
                    java.awt.Container parent = trackEditor.getParent();
                    while (parent != null && !(parent instanceof com.meenigam.Frame)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof com.meenigam.Frame) {
                        ((com.meenigam.Frame) parent).setSelectedTrack(Track.this);
                    }
                }
            }
        });
        
        // Add right-click context menu
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Select this track first
                    if (trackEditor != null) {
                        java.awt.Container parent = trackEditor.getParent();
                        while (parent != null && !(parent instanceof com.meenigam.Frame)) {
                            parent = parent.getParent();
                        }
                        if (parent instanceof com.meenigam.Frame) {
                            ((com.meenigam.Frame) parent).setSelectedTrack(Track.this);
                        }
                    }
                    
                    // Show context menu
                    showContextMenu(e);
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

    public ArrayList<Clip> getClips() { return this.clips; }

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

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            setBackground(selectedColor);
            setBorder(new CompoundBorder(selectedBorder, 
                BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        } else {
            setBackground(backgroundColor);
            setBorder(new CompoundBorder(border, 
                BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        }
        repaint();
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    private void showContextMenu(MouseEvent e) {
        if (clips.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please add audio clips to the track first.",
                    "No Audio Clips",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        JPopupMenu contextMenu = new JPopupMenu();
        contextMenu.add("Edit Audio...").addActionListener(evt -> {
            // Trigger the frame's audio editing menu
            if (trackEditor != null) {
                java.awt.Container parent = trackEditor.getParent();
                while (parent != null && !(parent instanceof com.meenigam.Frame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof com.meenigam.Frame) {
                    ((com.meenigam.Frame) parent).showAudioEditingMenu();
                }
            }
        });
        
        contextMenu.show(this, e.getX(), e.getY());
    }
    
    public String toString() {
        return this.title;
    }
}