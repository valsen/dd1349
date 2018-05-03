package main.world;

public abstract class FieldObject {

    private double xPos, yPos;
    private double velocity = 2;
    private Station previousStation;
    private Station nextStation;
    private Station nextNextStation;

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

    public void setVelocity(double velocity) {
        this.velocity = velocity;
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

    public Station getPreviousStation() {
        return previousStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Station getNextNextStation() {
        return nextNextStation;
    }

    public void setPreviousStation(Station previousStation) {
        this.previousStation = previousStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public void setNextNextStation(Station nextNextStation) {
        this.nextNextStation = nextNextStation;
    }
}
