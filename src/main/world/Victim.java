package main.world;

import java.awt.*;
import java.util.Random;

public class Victim extends FieldObject {

    private String name;

    public Victim(int x, int y, String name, Image icon) {
        super(x, y, icon);
        this.name = name;
        setVelocity(new Random().nextDouble() * 0.5);
    }

    public String getName() {
        return name;
    }


}
