package main;

import main.gui.GUI;
import main.world.*;
import main.world.graphs.StarGraph;
import main.world.graphs.TestGraph;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class Game {

    private Random rng = new Random(667);
    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private StationGraph currentGraph;
    private Station startingStation, startNext, startNextNext;
    private Train player;
    private GUI gui;
    private ArrayList<String> victimNames = new ArrayList<>();
    private HashMap<String, String> victimStrings = new HashMap<>();
    private Timer timer;
    private Timer countDownTimer;
    private double score;
    private double health = 100;
    private boolean playing = false;
    private ArrayList<Victim> victims = new ArrayList<>();
    private static final int fps = 60;
    public static final int WIDTH = 1024;
    private static final double WIDTH_TO_DEPTH_FACTOR = 1536.0 / 2048.0;
    public static final int DEPTH = (int) Math.round(WIDTH * WIDTH_TO_DEPTH_FACTOR);
    private static final int MAX_VICTIMS = 5;
    private double difficulty = 0;
    private int level = 1;
    private boolean spinning = false;
    private boolean spinningRandom = false;
    private boolean shaking = false;
    private boolean shrinking = false;
    private boolean expanding = false;
    private static final double DIFFICULTY_INCREASE = 0.005;
    private static final double SPEED_INCREASE  = 0.0002;

    public Game() {
        graphs.add(new TestGraph());
        //graphs.add(new StarGraph());
        currentGraph = graphs.get(0);
        gui = new GUI(this, WIDTH, DEPTH);
        createVictims(currentGraph);
        setStartingStations(currentGraph);
        createMainTrain();
    }
    private void createMainTrain() {
        player = new Train(startingStation.getRoundedX(), startingStation.getRoundedY());
        player.setPreviousStation(startingStation);
        player.setNextStation(startNext);
        player.setNextNextStation(startNextNext);
        player.updateDistanceQuotient();
    }

    public void run() {
        difficulty = 0;
        gui.getMap().buildMap();
        gui.getMap().updateView();

        // count-down to start
        countDownTimer = new Timer(1000, new ActionListener() {
            int countDown = 5;
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getMap().displayObjective();
                gui.getMap().displayCommandInstruction();
                gui.getMap().displayBigCountDown(countDown--);
                if (countDown < 0) {
                    countDownTimer.stop();
                    gui.getMap().removeObjective();
                    gui.getMap().removeCommandInstruction();
                    gui.getMap().removeBigCountDown();
                    playing = true;
                }
            }
        });
        countDownTimer.setInitialDelay(0);
        countDownTimer.start();

        timer = new Timer(1000 / fps, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playing) {
                    if (rng.nextDouble() < 0.002 && victims.size() < MAX_VICTIMS) {
                        Station from = currentGraph.getStations().get(rng.nextInt(currentGraph.getStations().size()));
                        ArrayList<Station> nextOptions = currentGraph.getAvailableStations(from, null);
                        Station to = nextOptions.get(rng.nextInt(nextOptions.size()));
                        spawnVictimBetweenStations(from, to);
                    }
                    for (Station station : currentGraph.getStations()) {
                        if(spinning) {
                            movePerfectlyCircular(station, gui.getMap().getWidth() / 2, gui.getMap().getHeight() / 2, false);
                        }
                        if(spinningRandom) {
                            movePerfectlyCircular(station, gui.getMap().getWidth() / 2, gui.getMap().getHeight() / 2, true);
                        }
                        if(shaking) {
                            if (rng.nextDouble() < 0.2) {
                                station.shake();
                            }
                        }
                        if(shrinking) {
                            shrink(station, station.getInitialXPos(), station.getInitialYPos());
                        }
                        if (expanding) {
                            expand(station, station.getInitialXPos(), station.getInitialYPos(), 0.1);
                        }
                    }
                    adjustLocation(player);
                    moveTowards(player, player.getNextStation(), player.getVelocity());
                    player.updateDistanceQuotient();
                    Iterator it = victims.iterator();
                    while (it.hasNext()) {
                        // The victims list obiviously contains only victims
                        Victim victim = (Victim) it.next();
                        adjustLocation(victim);
                        moveTowards(victim, victim.getNextStation(), victim.getVelocity());
                        victim.updateDistanceQuotient();
                        if (collisionFromBehind(player, victim)) {
                            it.remove();
                            // increment score by 10;
                            score += 10;
                            gui.getMap().updateScore(score);
                            gui.getMap().updateView();
                        }
                        else if (frontalCollision(player, victim)) {
                            // decrement score and health;
                            score -= 0.2;
                            health -= 0.3;
                            gui.getMap().updateScore(score);
                            gui.getMap().updateHealth(health);
                            gui.getMap().updateView();
                            if (round(health) <= 0) {
                                gui.displayGameOver();
                                timer.stop();
                            }
                        }
                    }
                    gui.getMap().updateView();
                    difficulty += DIFFICULTY_INCREASE;
                    player.increaseVelocity(SPEED_INCREASE);
                    if(difficulty > 5) {
                        spinning = true;
                    }
                    if(difficulty > 10) {
                        spinning = false;
                        spinningRandom = true;
                    }
                    if(difficulty > 15) {
                        spinningRandom = false;
                        shrinking = true;
                    }
                    if (difficulty > 20) {
                        shrinking = false;
                        spinning = true;
                    }
                    if (difficulty > 25) {
                        spinning = false;
                        expanding = true;
                    }
                    if (difficulty > 30) {
                        expanding = false;
                        spinningRandom = true;
                    }
                    if (difficulty > 35) {
                        expanding = true;
                        spinningRandom = false;
                    }
                    if (difficulty > 40) {
                        expanding = false;
                        shaking = true;
                    }
                    if (difficulty > 45) {
                        spinningRandom = true;
                    }
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    public StationGraph getCurrentGraph() {
        return currentGraph;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean bool) {
        playing = bool;
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

    private void moveCircular(Station station, int xMid, int yMid, double velocity, boolean randomDirection) {
        double dx = station.getX() - xMid;
        double dy = station.getY() - yMid;
        double r = sqrt(dx*dx + dy*dy);
        double newXPos, newYPos, newAngle;
        double angle = atan2(dy, dx);
        if (randomDirection) {
            newAngle = angle + (velocity / r) * station.getDirection();
        }
        else {
            newAngle = angle + (velocity / r);
        }
        newXPos = xMid + cos(newAngle) * r;
        newYPos = yMid + sin(newAngle) * r;
        station.moveTo(newXPos, newYPos);
    }

    private void movePerfectlyCircular(Station station, int xMid, int yMid, boolean randomDirection) {
        double dx = station.getX() - xMid;
        double dy = station.getY() - yMid;
        double r = sqrt(dx*dx + dy*dy);
        double newXPos, newYPos, newAngle;
        double angle = atan2(dy, dx);
        if (randomDirection) {
            newAngle = angle + 0.003 * station.getDirection();
        }
        else {
            newAngle = angle + 0.003;
        }
        newXPos = xMid + cos(newAngle) * r;
        newYPos = yMid + sin(newAngle) * r;
        station.moveTo(newXPos, newYPos);
    }

    private void shrink(Station station, double initialXPos, double initialYPos) {
        double xPos = station.getX();
        double yPos = station.getY();
        int xMid = gui.getMap().getWidth() / 2;
        int yMid = gui.getMap().getHeight() / 2;
        double dx = (initialXPos - xMid) * 0.6 - (xPos - xMid);
        double dy = (initialYPos - yMid) * 0.6 - (yPos - yMid);
        double r = sqrt(dx*dx + dy*dy);
        double angle = atan2(dy, dx);

        if (abs(dx) > 0 || abs(dy) > 0) {
            xPos = xPos + cos(angle) * r * 0.004;
            yPos = yPos + sin(angle) * r * 0.004;
            station.moveTo(xPos, yPos);
        }
    }

    private void expand(Station station, double initialXPos, double initialYPos, double velocity) {
        double xPos = station.getX();
        double yPos = station.getY();
        double dx = initialXPos - xPos;
        double dy = initialYPos - yPos;
        double r = sqrt(dx*dx + dy*dy);
        double angle = atan2(dy, dx);

        if (abs(dx) > 0 || abs(dy) > 0) {
            xPos = xPos + cos(angle) * r  * 0.004;
            yPos = yPos + sin(angle) * r * 0.004;
            station.moveTo(xPos, yPos);
        }

    }

    private void updateStations(FieldObject fieldObject) {
        fieldObject.setPreviousStation(fieldObject.getNextStation());
        fieldObject.setNextStation(fieldObject.getNextNextStation());
        ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(fieldObject.getNextStation(), fieldObject.getPreviousStation());
        fieldObject.setNextNextStation(nextNextOptions.get(new Random().nextInt(nextNextOptions.size())));
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
                    victimNames.add(victimName);
                    victimStrings.put(victimName, "src/Sprites/victimIcons/" + imageFileName);
                    victim.setPreviousStation(fromStation);
                    victim.setNextStation(toStation);
                    ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(toStation, fromStation);
                    Station nextNext = nextNextOptions.get(new Random().nextInt(nextNextOptions.size()));
                    victim.setNextNextStation(nextNext);
                    victims.add(victim);
                    victim.updateDistanceQuotient();
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

    public double getAngle(FieldObject object) {
        double dx = object.getNextStation().getX() - object.getPreviousStation().getX();
        double dy = object.getNextStation().getY() - object.getPreviousStation().getY();
        return atan2(dy, dx);
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
        ArrayList<Station> available = currentGraph.getAvailableStations(player.getNextStation(), player.getPreviousStation());
        player.toggleRoute(available);
    }

    public ArrayList<Victim> getVictims() {
        return victims;
    }

    public Train getPlayer() {
        return player;
    }

    private boolean collisionFromBehind(FieldObject player, FieldObject enemy) {
        int radiusA = player.getCollisionRadius();
        int radiusB = enemy.getCollisionRadius();
        int dx = player.getRoundedX() - enemy.getRoundedX();
        int dy = player.getRoundedY() - enemy.getRoundedY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (onSameRail(player, enemy) && player.getNextStation().equals(enemy.getNextStation())) {
            if (dist < radiusA || dist < radiusB) {
                //System.out.println("dist = " + dist + ", rA = " + radiusA + ", rB = " + radiusB);
                return true;
            }
        }
        return false;
    }

    private boolean frontalCollision(FieldObject player, FieldObject enemy) {
        int radiusA = player.getCollisionRadius();
        int radiusB = enemy.getCollisionRadius();
        int dx = player.getRoundedX() - enemy.getRoundedX();
        int dy = player.getRoundedY() - enemy.getRoundedY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (onSameRail(player, enemy) && player.getNextStation().equals(enemy.getPreviousStation())) {
            if (dist < radiusA || dist < radiusB) {
                //System.out.println("dist = " + dist + ", rA = " + radiusA + ", rB = " + radiusB);
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

    private void spawnVictimBetweenStations(Station fromStation, Station toStation) {
        String victimName = victimNames.get(rng.nextInt(victimNames.size()));
        double[] victimCoords = getPosition(fromStation, toStation, rng.nextDouble()*0.6 + 0.2);
        Victim victim = new Victim((int)round(victimCoords[0]), (int)round(victimCoords[1]),
                victimName, victimStrings.get(victimName));
        victim.setPreviousStation(fromStation);
        victim.setNextStation(toStation);
        ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(toStation, fromStation);
        Station nextNext = nextNextOptions.get(new Random().nextInt(nextNextOptions.size()));
        victim.setNextNextStation(nextNext);
        victims.add(victim);
        victim.updateDistanceQuotient();
    }
}
