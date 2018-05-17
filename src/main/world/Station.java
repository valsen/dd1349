package main.world;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class Station extends FieldObject {

    private String name;
    private static final double STATION_SPEED = 1;
    private int orientationDegrees;
    private int direction;
    private static final File iconFile = new File("src/Sprites/röd.png");

    /**
     * Construct a station at this location.
     * @param name The name of the main.world.Station.
     */
    public Station(int x, int y, String name, int orientationDegrees) {
        super(x, y, iconFile);
        this.name = name;
        this.orientationDegrees = orientationDegrees;
        direction = new Random().nextBoolean() ? 1 : -1;
        setVelocity(STATION_SPEED);
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

    public int getDirection() {
        return direction;
    }
}
