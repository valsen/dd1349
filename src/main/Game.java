package main;

import main.gui.GUI;
import main.world.*;
import main.world.graphs.TestGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class Game {

    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private StationGraph currentGraph;
    private Station startingStation, startNext, startNextNext;
    private Train mainTrain;
    private GUI gui;
    private Timer timer;
    private Timer countDownTimer;
    private int score;
    private boolean running = false;
    private ArrayList<Victim> victims = new ArrayList<>();
    private static final int fps = 60;
    public static final int WIDTH = 800;
    private static final double WIDTH_TO_DEPTH_FACTOR = 1536.0 / 2048.0;
    public static final int DEPTH = (int) Math.round(WIDTH * WIDTH_TO_DEPTH_FACTOR);

    public Game() {
        graphs.add(new TestGraph());
        currentGraph = graphs.get(0);
        gui = new GUI(this, WIDTH, DEPTH);
        createVictims(currentGraph);
        setStartingStations(currentGraph);
        createMainTrain();
    }
    private void createMainTrain() {
        mainTrain = new Train(startingStation.getRoundedX(), startingStation.getRoundedY());
        mainTrain.setPreviousStation(startingStation);
        mainTrain.setNextStation(startNext);
        mainTrain.setNextNextStation(startNextNext);
        mainTrain.updateDistanceQuotient();
    }

    public void run() {
        gui.getMap().buildMap();
        gui.getMap().updateView();

        // count-down to start
        countDownTimer = new Timer(1000, new ActionListener() {
            int countDown = 5;
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getMap().displayBigCountDown(countDown--);
                if (countDown < 0) {
                    countDownTimer.stop();
                    gui.getMap().removeBigCountDown();
                    running = true;
                }
            }
        });
        countDownTimer.setInitialDelay(0);
        countDownTimer.start();

        timer = new Timer(1000 / fps, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    for (Station station : currentGraph.getStations()) {
                        moveCircular(station, gui.getMap().getWidth()/2, gui.getMap().getHeight()/2, station.getVelocity());
                    }
                    adjustLocation(mainTrain);
                    moveTowards(mainTrain, mainTrain.getNextStation(), mainTrain.getVelocity());
                    mainTrain.updateDistanceQuotient();
                    Iterator it = victims.iterator();
                    while (it.hasNext()) {
                        // The victims list obiviously contains only victims
                        Victim victim = (Victim) it.next();
                        adjustLocation(victim);
                        moveTowards(victim, victim.getNextStation(), victim.getVelocity());
                        victim.updateDistanceQuotient();
                        if(doesCollide(victim, mainTrain)) {
                            it.remove();
                            if(victims.isEmpty()) {
                                gui.getMap().updateView();
                                gui.displayGameOver();
                                timer.stop();
                            }
                        }
                    }
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
        double dx = to.getX() - fieldObject.getX();
        double dy = to.getY() - fieldObject.getY();
        double s = sqrt(dx * dx + dy * dy);
        double newXPos, newYPos;
        if (s < velocity) {
            newXPos = to.getX();
            newYPos = to.getY();
            if (!(fieldObject instanceof Station)){
                updateStations(fieldObject);
            }
        } else {
            newXPos = fieldObject.getX() + (dx * velocity) / s;
            newYPos = fieldObject.getY() + (dy * velocity) / s;
        }
        fieldObject.moveTo(newXPos, newYPos);
    }

    private void moveCircular(Station station, int xMid, int yMid, double velocity) {

        double dx = xMid - station.getX();
        double dy = yMid - station.getY();
        double r = sqrt(dx*dx + dy*dy);
        double newXPos, newYPos;
        if (dx == 0) {
            newXPos = station.getX();
            newYPos = yMid > station.getY() ? (int) round(station.getY() + velocity) : (int) round(station.getY() - velocity);
        }
        else {
            double angle = atan2(dy, dx) + Math.PI/2;
            newXPos =  (station.getX() + cos(angle) * velocity);
            newYPos =  (station.getY() + sin(angle) * velocity);
        }
        station.moveTo(newXPos, newYPos);
    }

    private void updateStations(FieldObject fieldObject) {
        fieldObject.setPreviousStation(fieldObject.getNextStation());
        fieldObject.setNextStation(fieldObject.getNextNextStation());
        ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(fieldObject.getNextStation(), fieldObject.getPreviousStation());
        fieldObject.setNextNextStation(nextNextOptions.get(new Random().nextInt(nextNextOptions.size())));

        // increment score counter
        score++;
        gui.getMap().updateScore(score);
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
                double ratio = rnd.nextDouble() * 0.6 + 0.2;
                if (fromStation != null && toStation != null) {
                    double[] victimCoords = getPosition(fromStation, toStation, ratio);
                    Victim victim = new Victim((int)round(victimCoords[0]), (int)round(victimCoords[1]), victimName,
                            "src/Sprites/victimIcons/" + imageFileName);
                    victim.setPreviousStation(fromStation);
                    victim.setNextStation(toStation);
                    ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(toStation, fromStation);
                    Station nextNext = nextNextOptions.get(new Random().nextInt(nextNextOptions.size()));
                    victim.setNextNextStation(nextNext);
                    victims.add(victim);
                    victim.updateDistanceQuotient();
                    System.out.println("Spawned " + victimName + " between " + victimInfo[1] + " and " +
                            victimInfo[2] + " at " + (int)(ratio*100) + "% of the distance.");
                } else {
                    System.out.println("Failed to spawn " + victimName + " between " + victimInfo[1] + " and " +
                            victimInfo[2] + " at " + (int)(ratio*100) + "% of the distance. \n" +
                            "Please ensure spelling of stations is correct.");
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
    }

    // Helper method to set location of victim.
    private double[] getPosition(Station from, Station to, double ratio) {
        double dx = to.getX() - from.getX();
        double dy = (to.getY() - from.getY());
        double hyp = sqrt(pow(dx,2) + pow(dy, 2));
        double angle = atan2(dy, dx);
        double xPos = (from.getX() + cos(angle) * hyp * ratio);
        double yPos = (from.getY() + sin(angle) * hyp * ratio);
        return new double[]{xPos, yPos};
    }

    private void setStartingStations(StationGraph graph) {
        Random rng = new Random();
        startingStation = graph.getStations().get(rng.nextInt(graph.getStations().size()));
        ArrayList<Station> startNextOptions = graph.getAvailableStations(startingStation, null);
        startNext = startNextOptions.get(rng.nextInt(startNextOptions.size()));
        ArrayList<Station> startNextNextOptions = graph.getAvailableStations(startNext, startingStation);
        startNextNext = startNextNextOptions.get(rng.nextInt(startNextNextOptions.size()));
    }

    public void toggleMainRoute() {
        ArrayList<Station> available = currentGraph.getAvailableStations(mainTrain.getNextStation(), mainTrain.getPreviousStation());
        mainTrain.toggleRoute(available);
    }

    public ArrayList<Victim> getVictims() {
        return victims;
    }

    public Train getMainTrain() {
        return mainTrain;
    }

    private boolean doesCollide(FieldObject a, FieldObject b) {
        int radiusA = a.getCollisionRadius();
        int radiusB = b.getCollisionRadius();
        int dx = a.getRoundedX() - b.getRoundedX();
        int dy = a.getRoundedY() - b.getRoundedY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (onSameRail(a, b)) {
            if (dist < radiusA || dist < radiusB) {
                System.out.println("dist = " + dist + ", rA = " + radiusA + ", rB = " + radiusB);
                // decrement score by 5;
                score -= 5;
                gui.getMap().updateScore(score);
                return true;
            }
        }
        return false;
    }

    // Return true if two FieldObjects are on the same rail (between the same stations)
    private boolean onSameRail(FieldObject a, FieldObject b) {
        return (a.getPreviousStation().equals(b.getPreviousStation()) && a.getNextStation().equals(b.getNextStation()))
                || (a.getPreviousStation().equals(b.getNextStation())) && a.getNextStation().equals(b.getPreviousStation());
    }

    private void adjustLocation(FieldObject movingObject) {
        double[] coords = getPosition(movingObject.getPreviousStation(), movingObject.getNextStation(),
                movingObject.getDistanceQuotient());
        movingObject.moveTo(coords[0], coords[1]);
    }

    //private double
}
