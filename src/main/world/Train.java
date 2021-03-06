package main.world;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.util.List;

public class Train extends FieldObject {

    private boolean atStation = false;
    private int stationCounter = 0;
    private Random rng;
    private String name;
    private Image trainIcon;
    private static final File icon = new File("src/Sprites/grön.png");


    /**
     * Creates a train at the specified location.
     */
    public Train(int x, int y) {
        super(x, y, icon);
    }

    public void toggleRoute(ArrayList<Station> list) {
        if (list.size() > 1) {
            Station nextNext = list.get(new Random().nextInt(list.size()));
            if (!nextNext.equals(getNextNextStation())) {
                setNextNextStation(nextNext);
            }
            else {
                toggleRoute(list);
            }
        }
    }

    public boolean isAtStation() {
        return atStation;
    }
}
