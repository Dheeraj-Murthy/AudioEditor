package com.meenigam;

import com.meenigam.Panels.StagingArea;
import com.meenigam.Panels.TrackEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Frame extends JFrame {

    private int mouseX, mouseY;  // Store window drag position
    private boolean maximized = false;  // Track maximized state
    private Dimension previousSize;  // Store size before maximizing
    private Point previousLocation;  // Store location before maximizing

    public Frame() {
        super("Audio Editor");

        // Remove default decorations
        setUndecorated(true);
        setSize(1024, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);

        StagingArea stagingArea = new StagingArea();
        TrackEditor trackEditor = new TrackEditor();
        JPanel controlPanel = createControlPanel();

        // Wrap both panels in JScrollPane
        JScrollPane stagingScrollPane = new JScrollPane(stagingArea);
        JScrollPane trackScrollPane = new JScrollPane(trackEditor);
        JScrollPane controlPanelPane = new JScrollPane(controlPanel);

        // Add focus listeners to each scroll pane
        stagingScrollPane.addMouseListener(new PanelFocusAdapter(stagingScrollPane, this));
        trackScrollPane.addMouseListener(new PanelFocusAdapter(trackScrollPane, this));
        controlPanelPane.addMouseListener(new PanelFocusAdapter(controlPanelPane, this));

        reset(this);

        // Add the staging area on the left
        add(stagingScrollPane, BorderLayout.WEST);
        // Add the track editor in the center
        add(trackScrollPane, BorderLayout.CENTER);
        // Control panel at the bottom
        add(controlPanelPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void reset(JFrame frame) {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) comp;
                sp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        }
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(45, 45, 45));

        JLabel titleLabel = new JLabel("Audio Editor", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        styleButton(closeButton);
        closeButton.addActionListener(e -> System.exit(0));
        titleBar.add(closeButton, BorderLayout.EAST);

        // Enable dragging the window
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximize();  // Maximize or restore on double-click
                }
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!maximized) {
                    int x = e.getXOnScreen() - mouseX;
                    int y = e.getYOnScreen() - mouseY;
                    setLocation(x, y);
                }
            }
        });

        return titleBar;
    }

    private void toggleMaximize() {
        if (maximized) {
            // Restore to previous size and location
            setSize(previousSize);
            setLocation(previousLocation);
        } else {
            // Store current size and location
            previousSize = getSize();
            previousLocation = getLocation();

            // Maximize to full screen
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = env.getMaximumWindowBounds();
            setBounds(screenBounds);
        }
        maximized = !maximized;  // Toggle the maximized state
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(new Color(45, 45, 45));

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        styleButton(playButton);
        styleButton(pauseButton);

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);

        return controlPanel;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Frame::new);
    }
}