package main.world;

import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class Field
{
    
    // The depth and width of the field.
    private static int width, depth;
    private static final double WIDTH_TO_DEPTH_FACTOR = 868.0 / 1028.0;

    // Storage for the simulation objects.
    private Set<SimulationObject>[][] field;

    /**
     * Represent a field of the given dimensions.
     */
    @SuppressWarnings("unchecked")
    public Field(int width)
    {
        this.width = width;
        this.depth = (int) Math.round(width * WIDTH_TO_DEPTH_FACTOR);
        field = new HashSet[depth][width];
    }
    
    /**
     * Remove the specified object from the specified location if it is there. If it is not, does nothing.
     * @param location The location to remove.
     * @param object The object to remove.
     */
    void remove(SimulationObject object, Location location) {
        if (field[location.getRow()][location.getCol()] == null) {
            // nothing 2 do here
        }
        else {
            field[location.getRow()][location.getCol()].remove(object);
        }
    }
    
    /**
     * Place a simulation object at the given location.
     * @param simulationObject The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(SimulationObject simulationObject, int row, int col)
    {
        place(simulationObject, new Location(row, col));
    }
    
    /**
     * Place a simulation object at the given location.
     * @param simulationObject The object to be placed.
     * @param location Where to place the animal.
     */
    void place(SimulationObject simulationObject, Location location)
    {
        if(field[location.getRow()][location.getCol()] == null) {
            field[location.getRow()][location.getCol()] = new HashSet<>();
        }
        field[location.getRow()][location.getCol()].add(simulationObject);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    Set<SimulationObject> getObjectsAt(Location location)
    {
        return getObjectsAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    Set<SimulationObject> getObjectsAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Return the DEPTH of the field.
     * @return The DEPTH of the field.
     */
    public static int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the WIDTH of the field.
     * @return The WIDTH of the field.
     */
    public static int getWidth()
    {
        return width;
    }

    /**
     * Sets all references in the field to null, effectively removing all objects from it.
     */
    public void clearField() {
        for (int i = 0; i < field.length; i++) {
            Arrays.fill(field[i], null);
        }
    }
}
