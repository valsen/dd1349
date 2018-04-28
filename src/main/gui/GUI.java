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
    public GUI(Game game, int width, int height)
    {
        setTitle("SL Simulation");
        setLocation(0, 0);
        setBackground(BG_COLOR);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 400));

        map = new MapView(this, game, height, width);

        Container contents = getContentPane();
        contents.add(map, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    /**
     * @return the view of the map.
     */
    public MapView getMap() { return map; }
}