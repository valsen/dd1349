package mdi.world;

import java.awt.*;

public class Station extends SimulationObject {

    private String name;
    private int orientationDegrees;

    /**
     * Construct a station at this location.
     * @param location The location of the station.
     * @param name The name of the mdi.world.Station.
     */
    public Station(Field field, SubwayLine line, Color color, Location location, String name, int orientationDegrees) {
        super(field, line, color, location);
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
