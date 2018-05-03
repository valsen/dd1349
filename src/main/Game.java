package main;

import main.gui.GUI;
import main.world.*;
import main.world.graphs.TestGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class Game {

    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private StationGraph currentGraph;
    private Train mainTrain;
    private GUI gui;
    private Timer timer;
    private boolean running = true;
    private ArrayList<Victim> victims = new ArrayList<>();
    private static final int fps = 60;
    public static final int WIDTH = 800;
    private static final double WIDTH_TO_DEPTH_FACTOR = 1536.0 / 2048.0;
    public static final int DEPTH = (int) Math.round(WIDTH * WIDTH_TO_DEPTH_FACTOR);

    public Game() {
        graphs.add(new TestGraph());
        currentGraph = graphs.get(0);
        mainTrain = new Train(0, 0);
        gui = new GUI(this, WIDTH, DEPTH);
        createVictims(currentGraph);
    }

    public void run() {
        gui.getMap().buildMap();
        timer = new Timer(1000 / fps, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    gui.getMap().updateView();
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    public StationGraph getCurrentGraph() {
        return currentGraph;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean bool) {
        running = bool;
    }

    private void moveTowards(FieldObject fieldObject, Station to, double velocity) {
        int dx = to.getX() - fieldObject.getX();
        int dy = to.getY() - fieldObject.getY();
        int newXPos, newYPos;
        if (dx == 0) {
            newXPos = fieldObject.getX();
            newYPos = to.getY() > fieldObject.getY() ? (int) round(fieldObject.getY() + velocity) : (int) round(fieldObject.getY() - velocity);
        }
        else {
            double angle = atan2(dy, dx);
            newXPos = (int) (fieldObject.getX() + cos(angle) * velocity);
            newYPos = (int) (fieldObject.getY() + sin(angle) * velocity);
        }
        fieldObject.moveTo(newXPos, newYPos);
    }

    // Create the victims
    private void createVictims(StationGraph currentGraph) {
        Random rnd = new Random();
        try {
            Scanner scanner = new Scanner(currentGraph.getVictimsFile());
            while (scanner.hasNext()) {
                String[] victimInfo = scanner.nextLine().split("/");
                String victimName = victimInfo[0];
                Station fromStation = currentGraph.findStation(victimInfo[1]);
                Station toStation = currentGraph.findStation(victimInfo[2]);
                String imageFileName = victimInfo[3];
                try {
                    Image victimIcon = ImageIO.read(new File("src/Sprites/victimIcons/" + imageFileName));
                    double ratio = rnd.nextDouble() * 0.6 + 0.2;
                    if (fromStation != null && toStation != null) {
                        Location victimLocation = initializePosition(fromStation, toStation, ratio);
                        victims.add(new Victim(victimLocation.getX(), victimLocation.getY(), victimName, victimIcon));
                        System.out.println("Spawned " + victimName + " between " + victimInfo[1] + " and " +
                                victimInfo[2] + " at " + (int)(ratio*100) + "% of the distance.");
                    } else {
                        System.out.println("Failed to spawn " + victimName + " between " + victimInfo[1] + " and " +
                                victimInfo[2] + " at " + (int)(ratio*100) + "% of the distance. \n" +
                                "Please ensure spelling of stations is correct.");
                    }
                } catch (IOException e) {
                    System.out.println("No image found for " + victimName + ". Will not spawn victim.");
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
    }

    // Helper method to set location of victim.
    private Location initializePosition(Station from, Station to, double ratio) {
        int dx = to.getX() - from.getX();
        int dy = (to.getY() - from.getY());
        double hyp = sqrt(pow(dx,2) + pow(dy, 2));
        double angle = atan2(dy, dx);
        int victimXPos = (int) (from.getX() + cos(angle) * hyp * ratio);
        int victimYPos = (int) (from.getY() + sin(angle) * hyp * ratio);
        return new Location(victimYPos, victimXPos);
    }

    public ArrayList<Victim> getVictims() {
        return victims;
    }

}
