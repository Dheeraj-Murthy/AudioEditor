package com.meenigam.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelFocusAdapter extends MouseAdapter {
    private final JScrollPane scrollPane;
    private final JFrame frame;

    public PanelFocusAdapter(JScrollPane scrollPane, JFrame frame) {
        this.scrollPane = scrollPane;
        this.frame = frame;
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) comp;
                sp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Reset all scroll pane borders to gray
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) comp;
                sp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        }

        // Set the clicked scroll pane's border to blue
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }
}