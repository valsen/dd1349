package main.world;

/**
 * Represent a location in a rectangular grid.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class Location
{
    // Row and column positions.
    private int y;
    private int x;

    /**
     * Represent a y and column.
     * @param y The y.
     * @param x The column.
     */
    public Location(int y, int x)
    {
        this.y = y;
        this.x = x;
    }
    
    /**
     * Implement content equality.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return y == other.getY() && x == other.getX();
        }
        else {
            return false;
        }
    }
    
    /**
     * Return a string of the form y,column
     * @return A string representation of the location.
     */
    public String toString()
    {
        return y + "," + x;
    }
    
    /**
     * Use the top 16 bits for the y value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (y, x) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (y << 16) + x;
    }
    
    /**
     * @return The y.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * @return The column.
     */
    public int getX()
    {
        return x;
    }
}
