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
    private Station nextStation;

    /**
     * Creates a train at the specified location.
     */
    public Train(int x, int y) {
        super(x, y);
    }

    /**
     * Sets the next station for this train.
     * @param k -1 if this train is moving backwards. 1 if moving forward.
     */
    private void setNextStation(int k) {
        // TODO
    }

    public boolean isAtStation() {
        return atStation;
    }
}
