package mdi.world;

import java.awt.*;

public abstract class SimulationObject implements Comparable<SimulationObject> {

    // The location of the object
    private Location location;
    private int row;
    private int col;

    // The color of the object
    private Color color;

    private boolean visited = false;

    protected final SubwayLine subwayLine;

    protected Field field;

    /**
     * Create a simulation object at specified location.
     */
    public SimulationObject(Field field, SubwayLine line, Color color, Location location) {
        this.field = field;
        this.subwayLine = line;
        this.location = location;
        this.color = color;
        this.row = this.location.getRow();
        this.col = this.location.getCol();
        field.place(this, location);
    }

    /**
     * Return the location os this simulation object.
     */
    public Location getLocation() {
        return location;
    }

    public void move(Location newLocation) {
        field.remove(this, location);
        this.location = newLocation;
        this.row = newLocation.getRow();
        this.col = newLocation.getCol();
        field.place(this, location);
    }

    /**
     * @return The column of this object's location
     */
    public int getCol() {
        return col;
    }

    /**
     * @return The row of this objects location
     */
    public int getRow() {
        return row;
    }

    /**
     * Compares the drawing priority of two SimulationObjects. Returns a positive value if the parameter object has a
     * lower priority than this object.
     * @param object The object to compare to this one.
     * @return A positive value if the parameter object has a lower priority than this, a negative value if the
     * parameter object has a higher priority, or zero if their priority is equal.
     */
    public int compareTo(SimulationObject object) {
        return getPriority(this) - getPriority(object);
    }

    // Helper method for defining the drawing priority of simulation objects.
    private static int getPriority(SimulationObject object) {
        if (object instanceof Train) {
            return 0;
        }
        else if (object instanceof Station) {
            return 1;
        }
        else if (object instanceof Rail) {
            return 2;
        }
        else {
            throw new IllegalArgumentException("The priority order of the object is undefined.");
        }
    }

    public Color getColor() {
        return color;
    }

    public SubwayLine getSubwayLine() {
        return subwayLine;
    }
}
