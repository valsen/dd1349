package main.world;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class Station extends FieldObject {

    private Random rng = new Random();
    private String name;
    private static final double STATION_SPEED = 0.6;
    private double initialXPos;
    private double initialYPos;
    private double initialZPos;
    private int orientationDegrees;
    private int direction;
    private static final File iconFile = new File("src/Sprites/röd.png");

    /**
     * Construct a station at this location.
     * @param name The name of the main.world.Station.
     */
    public Station(int x, int y, int z, String name, int orientationDegrees) {
        super(x, y, z, iconFile);
        this.name = name;
        this.orientationDegrees = orientationDegrees;
        initialXPos = x;
        initialYPos = y;
        initialZPos = z;
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

    /*
    public void shake() {
        moveTo(getX() - 2 + rng.nextDouble()*4, getY() - 2 + rng.nextDouble()*4);
    }
    */

    public int getDirection() {
        return direction;
    }

    public double getInitialXPos() {
        return  initialXPos;
    }

    public double getInitialYPos() {
        return  initialYPos;
    }

    public double getInitialZPos() {
        return  initialZPos;
    }
}
