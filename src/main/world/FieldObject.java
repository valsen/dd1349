package main.world;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static java.lang.Math.round;

public abstract class FieldObject {

    private double xPos, yPos, zPos;
    private double velocity = 2;
    private Station previousStation;
    private Station nextStation;
    private Station nextNextStation;
    private double distanceQuotient;
    private File iconFile;
    private Image icon;
    private static final int MAX_SIZE = 60;

    public FieldObject(int x, int y, int z, File iconFile) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.iconFile = iconFile;
        setIcon(iconFile);
        //System.out.println(getCollisionRadius());
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

    public double getZ() { return zPos; }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
    }

    public void moveTo(double x, double y, double z) {
        xPos = x;
        yPos = y;
        zPos = z;
    }

    public int getRoundedX() {
        return (int) Math.round(xPos);
    }

    public int getRoundedY() {
        return (int) Math.round(yPos);
    }

    public int getRoundedZ() {
        return (int) Math.round(zPos);
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

    public Image getIcon() {
        return icon;
    }

    private void setIcon(File iconFile) {
        try {
            icon = ImageIO.read(iconFile);
            // rescale to allowed properties
            if(icon.getWidth(null) > MAX_SIZE || icon.getHeight(null) > MAX_SIZE) {
                int height = icon.getHeight(null);
                int width = icon.getWidth(null);
                double scalingFactor = (double) MAX_SIZE / (double) Math.max(icon.getHeight(null), icon.getWidth(null));
                icon = icon.getScaledInstance((int)round(width*scalingFactor), (int)round(height*scalingFactor), 0);
            }
        } catch (IOException e) {
            System.out.println("Could not read icon file");
        }
    }

    public void updateDistanceQuotient() {
        double distanceTotal = getDistance(previousStation, nextStation);
        double distanceCovered = getDistance(previousStation, this);
        distanceQuotient = distanceCovered / distanceTotal;
    }

    public static double getDistance(FieldObject a, FieldObject b) {
        double dx = a.getX()-b.getX();
        double dy = a.getY()-b.getY();
        double dz = a.getZ()-b.getZ();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    public double getDistanceQuotient() {
        return distanceQuotient;
    }

    public void increaseVelocity(double increase) {
        velocity += increase;
    }

    public int getCollisionRadius() {
        return Math.min(icon.getWidth(null), icon.getHeight(null));
    }

    public double[] getCoordinateVector() {
        return new double[]{xPos, yPos, zPos};
    }
}
