package main.world;

public abstract class FieldObject {

    private int xPos, yPos;
    private int velocity = 10;


    public FieldObject(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * @return The row of this object.
     */
    public int getY() {
        return yPos;
    }

    /**
     * @return The column of this object.
     */
    public int getX() {
        return xPos;
    }

    private int getVelocity() {
        return velocity;
    }

    public void moveTo(int x, int y) {
        xPos = x;
        yPos = y;
    }

}
