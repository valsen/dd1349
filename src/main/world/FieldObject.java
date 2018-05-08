package main.world;

public abstract class FieldObject {

    private double xPos, yPos;
    private double velocity = 2;

    public FieldObject(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * @return The row of this object.
     */
    public double getY() {
        return yPos;
    }

    /**
     * @return The column of this object.
     */
    public double getX() {
        return xPos;
    }

    public double getVelocity() {
        return velocity;
    }

    public void moveTo(double x, double y) {
        xPos = x;
        yPos = y;
    }

    public int getRoundedX() {
        return (int) Math.round(xPos);
    }

    public int getRoundedY() {
        return (int) Math.round(yPos);
    }
}
