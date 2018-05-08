package main.world;

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


    /**
     * Creates a train at the specified location.
     */
    public Train(int x, int y) {
        super(x, y);
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
