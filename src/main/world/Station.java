package main.world;

import java.awt.*;

public class Station extends FieldObject {

    private String name;
    private int orientationDegrees;

    /**
     * Construct a station at this location.
     * @param name The name of the main.world.Station.
     */
    public Station(int x, int y, String name, int orientationDegrees) {
        super(x, y);
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
