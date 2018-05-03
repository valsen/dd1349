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
    private Station previousStation;
    private Station nextStation;
    private Station nextNextStation;

    /**
     * Creates a train at the specified location.
     */
    public Train(int x, int y) {
        super(x, y);
    }

    public void toggleRoute(ArrayList<Station> list) {
        if (list.size() > 1) {
            Station nextNext = list.get(new Random().nextInt(list.size()));
            if (!nextNext.equals(nextNextStation)) {
                nextNextStation = nextNext;
            }
            else {
                toggleRoute(list);
            }
        }
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Station getNextNextStation() {
        return nextNextStation;
    }

    public void setPreviousStation(Station previousStation) {
        this.previousStation = previousStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public void setNextNextStation(Station nextNextStation) {
        this.nextNextStation = nextNextStation;
    }

    public boolean isAtStation() {
        return atStation;
    }
}
