package main.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import javax.swing.Timer;

public class Victim extends FieldObject {

    private String name;
    private Timer timer;
    private boolean collidable = true;

    public Victim(int x, int y, int z, String name, String filepath) {
        super(x, y, z, new File(filepath));
        this.name = name;
        setVelocity(1 + new Random().nextDouble() * 0.5);
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
