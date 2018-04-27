package main.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class StationLabel extends JLabel {

    private int orientationDegrees;

    StationLabel(String text, int orientationDegrees) {
        this.orientationDegrees = orientationDegrees;
        setFont(getFont().deriveFont(10.0f));
        setText(text);
    }

    /**
     * @return The number of degrees this station label should be rotated to be
     * readable on a map. Not the same as the drawing degrees.
     */
    int getOrientationDegrees() {
        return orientationDegrees;
    }

    /**
     * Rotate and flip label as needed.
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform aT = g2.getTransform();
        Shape oldShape = g2.getClip();
        aT.rotate(Math.toRadians(orientationDegrees));
        if (orientationDegrees > 90 && orientationDegrees < 270) {
            // flip vertically and horizontally
            aT.scale(-1, -1);
        }
        g2.setTransform(aT);
        g2.setClip(oldShape);
        super.paintComponent(g);
    }
}
