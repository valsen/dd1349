package main.world;

import java.awt.*;

public class Victim extends FieldObject {

    private String name;
    private Image icon;

    public Victim(int x, int y, String name, Image icon) {
        super(x, y);
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
