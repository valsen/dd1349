package main;

import main.gui.GUI;
import main.world.*;
import main.world.graphs.TestGraph;

import javax.swing.Timer;
import java.awt.*;
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
    private Player player;
    private GUI gui;
    private ArrayList<String> enemyNames = new ArrayList<>();
    private HashMap<String, String> enemyStrings = new HashMap<>();
    private Timer timer;
    private Timer countDownTimer;
    private double score;
    private double health = 100;
    private boolean playing = false;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private double enemyVelocity = 1;
    private static final int fps = 60;
    public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - 100;
    // private static final double WIDTH_TO_HEIGHT_FACTOR = 1;//1536.0 / 2048.0;
    private static final double HEIGHT_TO_WIDTH_FACTOR = (double) 2048 / 1536;
    // public static final int HEIGHT = (int) Math.round(WIDTH * WIDTH_TO_HEIGHT_FACTOR);
    public static final int WIDTH = (int) Math.round(HEIGHT*HEIGHT_TO_WIDTH_FACTOR);
    public static final int DEPTH = HEIGHT;
    private static final int MAX_ENEMIES = 5;
    private double difficulty = 0;
    private int level = 1;
    private boolean spinning = false;
    private boolean spinningRandom = false;
    private boolean shaking = false;
    private boolean shrinking = false;
    private boolean expanding = false;
    private double pitch;
    private double yaw;
    private double roll;
    private static final double DIFFICULTY_INCREASE = 0.003;
    private static final double PLAYER_SPEED_INCREASE = 0.0002;
    private static final double ENEMY_SPEED_INCREASE  = 0.00015;

    public Game() {
        graphs.add(new TestGraph());
        //graphs.add(new StarGraph());
        currentGraph = graphs.get(0);
        gui = new GUI(this, WIDTH, HEIGHT, DEPTH);
        createEnemies(currentGraph);
        setStartingStations(currentGraph);
        createMainTrain();
    }
    private void createMainTrain() {
        player = new Player(startingStation.getRoundedX(), startingStation.getRoundedY(), startingStation.getRoundedZ());
        player.setPreviousStation(startingStation);
        player.setNextStation(startNext);
        player.setNextNextStation(startNextNext);
        player.updateDistanceQuotient();
    }

    public void run() {
        difficulty = 0;
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
                    if (rng.nextDouble() < 0.002 && enemies.size() < MAX_ENEMIES) {
                        Station from = currentGraph.getStations().get(rng.nextInt(currentGraph.getStations().size()));
                        ArrayList<Station> nextOptions = currentGraph.getAvailableStations(from, null);
                        Station to = nextOptions.get(rng.nextInt(nextOptions.size()));
                        spawnEnemyBetweenStations(from, to);
                    }
                    for (Station station : currentGraph.getStations()) {
                        if(spinning) {
                            rotate3d(station, gui.getMap().getWidth() / 2, gui.getMap().getHeight() / 2, gui.getMap().getDepth() / 2);
                            //movePerfectlyCircular(station, gui.getMap().getWidth() / 2, gui.getMap().getHeight() / 2, false);
                        }
                        if(spinningRandom) {
                            //movePerfectlyCircular(station, gui.getMap().getWidth() / 2, gui.getMap().getHeight() / 2, true);
                        }
                        if(shaking) {
                            if (rng.nextDouble() < 0.2) {
                                //station.shake();
                            }
                        }
                        if(shrinking) {
                            shrink(station, station.getInitialXPos(), station.getInitialYPos(), station.getInitialZPos());
                        }
                        if (expanding) {
                            expand(station, station.getInitialXPos(), station.getInitialYPos(), station.getInitialZPos());
                        }
                    }
                    adjustLocation(player);
                    moveTowards(player, player.getNextStation(), player.getVelocity());
                    player.updateDistanceQuotient();
                    Iterator it = enemies.iterator();
                    while (it.hasNext()) {
                        // The enemies list obviously contains only enemies
                        Enemy enemy = (Enemy) it.next();
                        adjustLocation(enemy);
                        moveTowards(enemy, enemy.getNextStation(), enemy.getVelocity());
                        enemy.updateDistanceQuotient();
                        if (collisionFromBehind(player, enemy)) {
                            it.remove();
                            // increment score by 10;
                            score += 10 + (1 * difficulty);
                            gui.getMap().updateScore(score);
                            gui.getMap().updateView();
                        }
                        else if (frontalCollision(player, enemy)) {
                            // decrement score and health;
                            if (enemy.getCollidable()) {
                                score -= 10;
                                health -= 5;
                                gui.getMap().updateScore(score);
                                gui.getMap().updateHealth(health);
                                gui.getMap().updateView();
                                if (round(health) <= 0) {
                                    gui.getMap().displayGameOver();
                                    timer.stop();
                                }
                                enemy.startCoolDownTimer();
                            }
                            else {
                                gui.getMap().updateView();
                            }
                        }
                        enemy.increaseVelocity(ENEMY_SPEED_INCREASE);
                        enemyVelocity = enemy.getVelocity();
                    }
                    gui.getMap().updateView();
                    difficulty += DIFFICULTY_INCREASE;
                    player.increaseVelocity(PLAYER_SPEED_INCREASE);
                    if(difficulty > 5) {
                        spinning = true;
                        yaw = -0.003;
                        pitch = 0.003;
                        roll = 0.003;
                    }
                    if(difficulty > 10) {
                        spinning = false;
                        shrinking = true;
                    }
                    if(difficulty > 15) {
                        shrinking = false;
                        spinning = true;
                        yaw = 0.005;
                        pitch = 0.005;
                        roll = 0.005;
                    }
                    if (difficulty > 20) {
                        expanding = true;
                    }
                    if (difficulty > 25) {
                        spinning = false;
                    }
                    if (difficulty > 30) {
                        expanding = false;
                        spinning = true;
                        yaw = 0.007;
                        pitch = 0.007;
                        roll = 0.007;
                    }
                    if (difficulty > 35) {
                        yaw = -0.01;
                        pitch = -0.01;
                        roll = -0.01;
                        //spinningRandom = false;
                    }
                    if (difficulty > 40) {
                        //shaking = true;
                    }
                    if (difficulty > 45) {
                        //spinningRandom = true;
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
        double dz = to.getZ() - fieldObject.getZ();
        double s = sqrt(dx * dx + dy * dy + dz * dz);
        double newXPos, newYPos, newZPos;
        if (s < velocity) {
            newXPos = to.getX();
            newYPos = to.getY();
            newZPos = to.getZ();
            if (!(fieldObject instanceof Station)){
                updateStations(fieldObject);
            }
        } else {
            newXPos = fieldObject.getX() + (dx * velocity) / s;
            newYPos = fieldObject.getY() + (dy * velocity) / s;
            newZPos = fieldObject.getZ() + (dz * velocity) / s;
        }
        fieldObject.moveTo(newXPos, newYPos, newZPos);
    }

    private void moveCircular(Station station, int xMid, int yMid, int zMid, double velocity, boolean randomDirection) {
        double dx = station.getX() - xMid;
        double dy = station.getY() - yMid;
        double dz = station.getZ() - zMid;
        double r = sqrt(dx*dx + dy*dy + dz*dz);
        double newXPos, newYPos, newZPos, newAngle;
        double angle = atan2(dy, dx);
        if (randomDirection) {
            newAngle = angle + (velocity / r) * station.getDirection();
        }
        else {
            newAngle = angle + (velocity / r);
        }
        newXPos = xMid + cos(newAngle) * r;
        newYPos = yMid + sin(newAngle) * r;
        station.moveTo(newXPos, newYPos, station.getZ());
    }

    private void rotate3d(Station station, int xMid, int yMid, int zMid) {
        double dx = station.getX() - xMid;
        double dy = station.getY() - yMid;
        double dz = station.getZ() - zMid;
        double[] v = new double[]{dx, dy, dz};
        double[] w = Transformations.rotate(v, yaw, pitch, roll);
        station.moveTo(w[0] + xMid, w[1] + yMid, w[2] + zMid);
    }


    private void shrink(Station station, double initialXPos, double initialYPos, double initialZPos) {
        double xPos = station.getX();
        double yPos = station.getY();
        double zPos = station.getZ();
        int xMid = gui.getMap().getWidth() / 2;
        int yMid = gui.getMap().getHeight() / 2;
        int zMid = gui.getMap().getDepth() / 2;
        double dx = (initialXPos - xMid) * 0.6 - (xPos - xMid);
        double dy = (initialYPos - yMid) * 0.6 - (yPos - yMid);
        double dz = (initialZPos - zMid) * 0.6 - (zPos - zMid);
        double r = sqrt(dx*dx + dy*dy);
        double r2 = sqrt(dx*dx + dz*dz);
        double angle = atan2(dy, dx);
        double angle2 = atan2(dz, dx);

        if (abs(dx) > 0 || abs(dy) > 0) {
            xPos = xPos + cos(angle) * r * 0.004;
            yPos = yPos + sin(angle) * r * 0.004;
            zPos = zPos + sin(angle2) * r2 * 0.004;
            station.moveTo(xPos, yPos, zPos);
        }
    }


    private void expand(Station station, double initialXPos, double initialYPos, double initialZPos) {
        double xPos = station.getX();
        double yPos = station.getY();
        double zPos = station.getZ();
        double dx = initialXPos - xPos;
        double dy = initialYPos - yPos;
        double dz = initialZPos - zPos;
        double r = sqrt(dx*dx + dy*dy);
        double r2 = sqrt(dx*dx + dz*dz);
        double angle = atan2(dy, dx);
        double angle2 = atan2(dz, dx);

        if (abs(dx) > 0 || abs(dy) > 0) {
            xPos = xPos + cos(angle) * r  * 0.01;
            yPos = yPos + sin(angle) * r * 0.01;
            zPos = zPos + sin(angle2) * r2 * 0.01;
            station.moveTo(xPos, yPos, zPos);
        }

    }

    private void updateStations(FieldObject fieldObject) {
        fieldObject.setPreviousStation(fieldObject.getNextStation());
        fieldObject.setNextStation(fieldObject.getNextNextStation());
        ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(fieldObject.getNextStation(), fieldObject.getPreviousStation());
        fieldObject.setNextNextStation(nextNextOptions.get(new Random().nextInt(nextNextOptions.size())));
    }
    // Create the enemies
    private void createEnemies(StationGraph currentGraph) {
        Random rnd = new Random();
        try {
            Scanner scanner = new Scanner(currentGraph.getEnemiesFile());
            while (scanner.hasNext()) {
                String[] enemyInfo = scanner.nextLine().split("/");
                String enemyName = enemyInfo[0];
                Station fromStation = currentGraph.findStation(enemyInfo[1]);
                Station toStation = currentGraph.findStation(enemyInfo[2]);
                String imageFileName = enemyInfo[3];
                double ratio = rnd.nextDouble() * 0.6 + 0.2;
                if (fromStation != null && toStation != null) {
                    double[] enemyCoords = getPosition(fromStation, toStation, ratio);
                    Enemy enemy = new Enemy((int)round(enemyCoords[0]), (int)round(enemyCoords[1]), (int)round(enemyCoords[2]), enemyName,
                            "src/Sprites/enemyIcons/" + imageFileName, enemyVelocity);
                    enemyNames.add(enemyName);
                    enemyStrings.put(enemyName, "src/Sprites/enemyIcons/" + imageFileName);
                    enemy.setPreviousStation(fromStation);
                    enemy.setNextStation(toStation);
                    ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(toStation, fromStation);
                    Station nextNext = nextNextOptions.get(new Random().nextInt(nextNextOptions.size()));
                    enemy.setNextNextStation(nextNext);
                    enemies.add(enemy);
                    enemy.updateDistanceQuotient();
                } else {
                    System.out.println("Failed to spawn " + enemyName + " between " + enemyInfo[1] + " and " +
                            enemyInfo[2] + " at " + (int)(ratio*100) + "% of the distance. \n" +
                            "Please ensure spelling of stations is correct.");
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
    }

    // Helper method to set location of enemy.
    private double[] getPosition(Station from, Station to, double ratio) {
        double dx = to.getX() - from.getX();
        double dy = (to.getY() - from.getY());
        double dz = to.getZ() - from.getZ();
        double hyp1 = sqrt(dx*dx + dy*dy);
        double hyp2 = sqrt(dx*dx + dz*dz);
        double angle = atan2(dy, dx);
        double angle2 = atan2(dz, dx);
        double xPos = (from.getX() + cos(angle) * hyp1 * ratio);
        double yPos = (from.getY() + sin(angle) * hyp1 * ratio);
        double zPos = (from.getZ() + sin(angle2) * hyp2 * ratio);
        return new double[]{xPos, yPos, zPos};
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

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Player getPlayer() {
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
        movingObject.moveTo(coords[0], coords[1], coords[2]);
    }

    private void spawnEnemyBetweenStations(Station fromStation, Station toStation) {
        String enemyName = enemyNames.get(rng.nextInt(enemyNames.size()));
        double[] enemyCoords = getPosition(fromStation, toStation, rng.nextDouble()*0.6 + 0.2);
        Enemy enemy = new Enemy((int)round(enemyCoords[0]), (int)round(enemyCoords[1]), (int)round(enemyCoords[2]),
                enemyName, enemyStrings.get(enemyName), enemyVelocity);
        enemy.setPreviousStation(fromStation);
        enemy.setNextStation(toStation);
        ArrayList<Station> nextNextOptions = currentGraph.getAvailableStations(toStation, fromStation);
        Station nextNext = nextNextOptions.get(new Random().nextInt(nextNextOptions.size()));
        enemy.setNextNextStation(nextNext);
        enemies.add(enemy);
        enemy.updateDistanceQuotient();
    }
}
