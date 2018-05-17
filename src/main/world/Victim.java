package main.world;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class Victim extends FieldObject {

    private String name;

    public Victim(int x, int y, String name, String filepath) {
        super(x, y, new File(filepath));
        this.name = name;
        setVelocity(0.5 + new Random().nextDouble() * 0.5);
    }

    public String getName() {
        return name;
    }


}
