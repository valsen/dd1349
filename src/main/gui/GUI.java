package main.gui;

import main.*;
import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame {

    static final Color BG_COLOR = new Color(45, 52, 55);
    private MapView map;

    /**
     * Create a view of the given WIDTH and height.
     * @param height The simulation's height.
     * @param width  The simulation's WIDTH.
     */
    public GUI(Game game, int width, int height, int depth)
    {
        setTitle("Dysfunctional train game");
        setBackground(BG_COLOR);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension windowSize = new Dimension(width, height);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);
        setPreferredSize(windowSize);

        map = new MapView(this, game, height, height, height);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(map);
        box.add(Box.createVerticalGlue());
        add(box);
        //Container contents = getContentPane();
        //add(map, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    /**
     * @return the view of the map.
     */
    public MapView getMap() { return map; }
}