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

    // Storage for the simulation objects.
    private Set<FieldObject>[][] field;

    /**
     * Represent a field of the given dimensions.
     */
    @SuppressWarnings("unchecked")
    public Field(int width, int depth)
    {
        this.width = width;
        this.depth = depth;
        field = new HashSet[depth][width];
    }
    
    /**
     * Remove the specified object from the specified location if it is there. If it is not, does nothing.
     * @param location The location to remove.
     * @param object The object to remove.
     */
    void remove(FieldObject object, Location location) {
        if (field[location.getRow()][location.getCol()] == null) {
            // nothing 2 do here
        }
        else {
            field[location.getRow()][location.getCol()].remove(object);
        }
    }
    
    /**
     * Place a FieldObject at the given location.
     * @param object The object to be placed.
     * @param location Where to place the object.
     */
    void place(FieldObject object, Location location)
    {
        if(field[location.getRow()][location.getCol()] == null) {
            field[location.getRow()][location.getCol()] = new HashSet<>();
        }
        field[location.getRow()][location.getCol()].add(object);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    Set<FieldObject> getObjectsAt(Location location)
    {
        return getObjectsAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    Set<FieldObject> getObjectsAt(int row, int col)
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
