package main.gui;

import main.*;
import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame {

    static final Color BG_COLOR = new Color(45, 52, 55);
    private MapView map;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;

    /**
     * Create a view of the given WIDTH and height.
     * @param height The simulation's height.
     * @param width  The simulation's WIDTH.
     */
    public GUI(Simulator simulator, int width, int height)
    {
        setTitle("SL Simulation");
        setLocation(0, 0);
        setBackground(BG_COLOR);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 400));

        map = new MapView(this, simulator, height, width);
        controlPanel = new ControlPanel(simulator, height);
        infoPanel = new InfoPanel(height);

        Container contents = getContentPane();
        contents.add(controlPanel, BorderLayout.WEST);
        contents.add(map, BorderLayout.CENTER);
        contents.add(infoPanel, BorderLayout.EAST);

        pack();
        setVisible(true);
    }

    /**
     * @return the view of the map.
     */
    public MapView getMap() { return map; }

    /**
     * @return the control panel.
     */
    public ControlPanel getControlPanel() { return controlPanel; }

    /**
     * @return train information display panel.
     */
    public InfoPanel getInfoPanel() { return infoPanel; }
}