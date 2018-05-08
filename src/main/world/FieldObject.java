package main.world;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class FieldObject {

    private double xPos, yPos;
    private double velocity = 2;
    private Station previousStation;
    private Station nextStation;
    private Station nextNextStation;
    private File iconFile;
    private Image icon;

    public FieldObject(int x, int y, File iconFile) {
        this.xPos = x;
        this.yPos = y;
        this.iconFile = iconFile;
        setIcon(iconFile);
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

    public Image getIcon() {
        return icon;
    }

    private void setIcon(File iconFile) {
        try {
            icon = ImageIO.read(iconFile);
        } catch (IOException e) {
            System.out.println("Could not read icon file");
        }
    }
}
