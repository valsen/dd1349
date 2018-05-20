package main.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import javax.swing.Timer;

public class Enemy extends FieldObject {

    private String name;
    private Timer timer;
    private boolean collidable = true;

    public Enemy(int x, int y, int z, String name, String filepath, double velocity) {
        super(x, y, z, new File(filepath));
        this.name = name;
        setVelocity(velocity);
    }

    public String getName() {
        return name;
    }

    public void startCoolDownTimer() {
        collidable = false;
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                collidable = true;
                timer.stop();
            }
        });
        timer.setInitialDelay(1000);
        timer.start();
    }

    public boolean getCollidable() {
        return collidable;
    }


}
