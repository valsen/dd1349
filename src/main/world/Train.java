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
    private static final File icon = new File("src/Sprites/spaceship.png");


    /**
     * Creates a train at the specified location.
     */
    public Train(int x, int y, int z) {
        super(x, y, z, icon);
    }

    public void toggleRoute(ArrayList<Station> list) {
        if (list.size() > 1) {
            //Station nextNext = list.get(new Random().nextInt(list.size()));
            int currentIndex = list.indexOf(getNextNextStation());
            if (currentIndex < list.size() - 1) {
                setNextNextStation(list.get(currentIndex + 1));
            }
            else {
                setNextNextStation(list.get(0));
            }
        }
    }

    public boolean isAtStation() {
        return atStation;
    }
}
