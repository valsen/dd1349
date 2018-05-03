package main.world;

import java.awt.*;

public class Victim extends FieldObject {

    private String name;
    private Image icon;

    public Victim(Field field, Location location, String name, Image icon) {
        super(field, location);
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }
}
