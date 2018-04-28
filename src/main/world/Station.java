package main.world;

import java.awt.*;

public class Station extends FieldObject {

    private String name;
    private int orientationDegrees;

    /**
     * Construct a station at this location.
     * @param location The location of the station.
     * @param name The name of the main.world.Station.
     */
    public Station(Field field, Location location, String name, int orientationDegrees) {
        this.name = name;
        this.orientationDegrees = orientationDegrees;
    }

    /**
     * @return The name of the station.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The number of clockwise degrees the text of this station should be drawn.
     */
    public int getOrientationDegrees() {
        return orientationDegrees;
    }

}
