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
     * @param location the location of the train.
     */
    Train(Field field, Location location) {
        super(field, location);
    }

    /**
     * Move train to next rail, but only if no other train going in
     * the same direction on the same subway line is already occupying
     * the next rail. If this train is at a station, increment its
     * time spent at the station, and only move if time spent exceeds
     * the time limit. Reverse train's direction when it reaches an end
     * station.
     */
    // TODO
    /* void moveToNextRail() {
        if (atStation && stationCounter <= STATION_TIME_LIMIT) {
            stationCounter += rng.nextInt(10);
            if(stationCounter > STATION_TIME_LIMIT) {
                // all passengers aboard, proceed to move.
                atStation = false;
            }
            else {
                // not finished loading passengers, don't move.
                return;
            }
        }
        stationCounter = 0;
        if (movingForward) {
            if (nextIsOccupied()) {
                // wait for train in front to move.
                return;
            }
            move(rails.get(++railIndex).getLocation());
            if (railIndex == MAX_RAIL_INDEX) {
                // reached last station, change direction.
                movingForward = false;
                setNextStation(-1);
            }
            else {
                setNextStation(1);
            }
        }
        else {
            if (nextIsOccupied()) {
                // wait for train in front to move.
                return;
            }
            move(rails.get(--railIndex).getLocation());
            if (railIndex == 0) {
                // reached first station, change direction.
                movingForward = true;
                setNextStation(1);
            }
            else {
                setNextStation(-1);
            }
        }
        if (stationIndices.keySet().contains(railIndex)) {
            atStation = true;
            passengerStatus = getRandomPassengerStatus();
        }

    } */

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
