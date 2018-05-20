package main.gui;

import main.Game;
import main.world.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;
import static java.lang.Math.round;
import static main.gui.GUI.BG_COLOR;

public class MapView extends JPanel {
    
    private static final Color INACTIVE_RAIL_COLOR = new Color(160, 160, 160);
    private static final Color ACTIVE_RAIL_COLOR = new Color(255, 101, 162);
    private static final int PLAYER_SIZE = 50;
    private static final int STATION_SIZE = 15;
    private static final int ENEMY_SIZE = 50;
    private static final String OBJECTIVE_TEXT = "Hit enemies from behind to collect points. Avoid frontal collisions!";
    private static final String GAME_COMMAND = "Use spacebar to control your spaceship's route";
    private final double GRID_VIEW_SCALING_FACTOR = 1;
    private GUI gui;
    private JLabel scoreCounter;
    private JLabel healthCounter;
    private JLabel objective;
    private JLabel commandLabel;
    private JLabel bigCountDown;
    private Game game;
    private StationGraph currentGraph;
    private Image playerIcon;
    private Image scaledPlayerIcon;
    private Map<Station, StationLabel> stationLabels = new HashMap<>();
    private int gridWidth, gridHeight;
    private double xScale, yScale;
    private Dimension size;
    private int depth;
    private Graphics g;
    private Graphics2D g2;

    /**
     * Create a new MapView component.
     */
    MapView(GUI gui, Game game, int height, int width, int depth)
    {
        this.gui = gui;
        this.game = game;
        currentGraph = game.getCurrentGraph();
        setLayout(null); //required for custom placement of labels.
        setBackground(BG_COLOR);
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        xScale = yScale = GRID_VIEW_SCALING_FACTOR;
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(width, height);
        this.depth = depth;
        try {
            playerIcon = ImageIO.read(new File("src/Sprites/mainship.png"));
        }
        catch (IOException e) {
            System.out.println("Failed to load all icons.");
        }
        scaledPlayerIcon = playerIcon.getScaledInstance(PLAYER_SIZE, PLAYER_SIZE, 0);
        createScoreCounter();
        createHealthCounter();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false), "toggle route");
        getActionMap().put("toggle route", new ToggleRouteAction());
    }

    private void createScoreCounter() {
        scoreCounter = new JLabel();
        scoreCounter.setFont(getFont().deriveFont(26.0f));
        scoreCounter.setText("Score: 0");
        scoreCounter.setLocation(20, 15);
        scoreCounter.setSize(scoreCounter.getPreferredSize());
        scoreCounter.setForeground(Color.WHITE);
        add(scoreCounter);
    }

    private void createHealthCounter() {
        healthCounter = new JLabel();
        healthCounter.setFont(getFont().deriveFont(26.0f));
        healthCounter.setText("Health: 100");
        healthCounter.setLocation(20, 45);
        healthCounter.setSize(healthCounter.getPreferredSize());
        healthCounter.setForeground(Color.GREEN);
        add(healthCounter);
    }

    /**
     * Tell the GUI manager how big we would like to be.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension((int) (gridWidth * xScale),
                (int) (gridHeight * yScale));
    }

    /**
     * Update the view of the gamestate.
     */
    public void updateView()
    {
        revalidate();
        repaint();
    }


    /**
     * Create and add labels for each station. Labels are
     * either straight or rotated.
     */
    private void createLabels() {
        for (Station station : game.getCurrentGraph().getStations()) {
            stationLabels.put(station, new StationLabel(station.getName(), station.getOrientationDegrees()));
        }
    }

    /**
     * Paint all stations on the field.
     */
    private void drawStations() {
        for (Station station : game.getCurrentGraph().getStations()) {
            drawCenteredStation(station.getRoundedX(), station.getRoundedY());
        }
    }

    /**
     * Paint a station on the field with the middle of it at the supplied position.
     */
    private void drawCenteredStation(int x, int y) {
        int radius = 10;
        x = (int) (x * xScale - (radius / 2) + xScale / 2);
        y = (int) (y * yScale - (radius / 2) + yScale / 2);
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, radius, radius);
    }

    /**
     * Draw lines between all connected stations.
     */
    private void drawLinesBetweenAllStations() {
        int x1, x2, y1, y2;
        for (int i = 0; i < game.getCurrentGraph().getStations().size() - 1; i++) {
            Station from = currentGraph.getStations().get(i);
            x1 = (int) (from.getX() * xScale + (xScale / 2));
            y1 = (int) (from.getY() * yScale + (yScale / 2));

            for(Station to : game.getCurrentGraph().getAvailableStations(from, null)) {
                x2 = (int) (to.getX() * xScale + (xScale / 2));
                y2 = (int) (to.getY() * yScale + (yScale / 2));
                g2.setColor(INACTIVE_RAIL_COLOR);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Highlight the path from the player's current position
     * to the next-next station.
     */
    private void highlightActiveRoute() {
        Player player = game.getPlayer();
        Station next = player.getNextStation();
        Station nextNext = player.getNextNextStation();
        int playerXPos = (int) (player.getX() * xScale + (xScale / 2));
        int playerYPos = (int) (player.getY() * yScale + (yScale / 2));
        int nextXPos = (int) (next.getX() * xScale + (xScale / 2));
        int nextYPos = (int) (next.getY() * yScale + (yScale / 2));
        int nextNextXPos = (int) (nextNext.getX() * xScale + (xScale / 2));
        int nextNextYPos = (int) (nextNext.getY() * yScale + (yScale / 2));
        g2.setColor(ACTIVE_RAIL_COLOR);
        g2.drawLine(playerXPos, playerYPos, nextXPos, nextYPos);
        g2.drawLine(nextXPos, nextYPos, nextNextXPos, nextNextYPos);
    }

    /**
     * Action performed when pressing the spacebar in gameplay.
     */
    class ToggleRouteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            game.toggleMainRoute();
        }
    }

    /**
     * Paint station labels on map.
     */
    private void drawStationLabels() {
        for (Station station : game.getCurrentGraph().getStations()) {
            int xPixel = (int) Math.round(station.getX() * xScale);
            int yPixel = (int) Math.round(station.getY() * yScale);
            StationLabel label = stationLabels.get(station);
            if (label.getOrientationDegrees() != -1) {
                setLabelLocation(label, xPixel, yPixel);
                label.setSize(label.getPreferredSize());
                label.setForeground(Color.WHITE);
                add(label);
            }
        }
    }

    /**
     * Adjusts the location of a label at a specified location depending on the degree of rotation of the label.
     * Positions the text so that the label starts next to the station and is read from left to right.
     * @param stationLabel The label to adjust.
     * @param x The pixel x-coordinate of the label.
     * @param y The pixel y-coordinate of the label.
     */
    private void setLabelLocation(StationLabel stationLabel, int x, int y) {
        int degrees = stationLabel.getOrientationDegrees();
        double width = stationLabel.getPreferredSize().getWidth();
        // Initialize coordinates.
        int newX = x;
        int newY = y;
        // Set new coordinates.
        if (degrees == 0) {
            newX = x + (int) Math.round(STATION_SIZE / 2.0) + 2;
            newY = (int) Math.round(y - STATION_SIZE / 2.0);
        } else if (degrees <= 45) {
            newX = x + STATION_SIZE + 2;
            newY = (int) Math.round(y - STATION_SIZE / 2.0);
        } else if (degrees <= 90) {
            newX = (int) Math.round(x + STATION_SIZE / 2.0);
            newY = (int) Math.round(y + STATION_SIZE / 2.0) + 2;
        } else if (degrees <= 135) {
            newX = x + (int) round(cos(toRadians(stationLabel.getOrientationDegrees())) * width);
            newY = y + (int) round(sin(toRadians(stationLabel.getOrientationDegrees())) * width);
            newX -= (int) Math.round(STATION_SIZE/2);
            newY += (int) Math.round(STATION_SIZE/10.0);
        } else if(degrees <= 180) {
            newX = x + (int) round(cos(toRadians(stationLabel.getOrientationDegrees())) * width);
            newY = y + (int) round(sin(toRadians(stationLabel.getOrientationDegrees())) * width);
            newX -= (int) Math.round(STATION_SIZE/1.5);
            newY -= (int) Math.round(STATION_SIZE/2.0);
        } else if (degrees <= 225) {
            newX = x + (int) round(cos(toRadians(stationLabel.getOrientationDegrees())) * width);
            newY = y + (int) round(sin(toRadians(stationLabel.getOrientationDegrees())) * width);
            newX -= (int) Math.round(STATION_SIZE / 10.0);
            newY -= (int) Math.round(STATION_SIZE / 1.3);
        } else if (degrees <= 270) {
            newX = (int) Math.round(x - STATION_SIZE / 2.0);
            newY = (int) Math.round(y - STATION_SIZE / 2.0) - 2;
        } else if (degrees <= 315) {
            newX = (int) Math.round(x - STATION_SIZE / 5.0);
            newY = y - STATION_SIZE;
        }
        stationLabel.setLocation(newX, newY);
    }

    /**
     * Draw the player.
     */
    private void drawPlayer() {
        drawCenteredPlayer(game.getPlayer().getRoundedX(), game.getPlayer().getRoundedY());
    }

    /**
     * Draw the player centered at the specified row and column.
     * @param x The column of the player.
     * @param y The row of the player.
     */
    private void drawCenteredPlayer(int x, int y) {
        x = (int) (x * xScale - (PLAYER_SIZE) / 2 + xScale / 2);
        y = (int) (y * yScale - (PLAYER_SIZE / 2) + yScale / 2);
        g2.rotate(game.getAngle(game.getPlayer()), x + PLAYER_SIZE /2, y + PLAYER_SIZE /2);
        g2.drawImage(scaledPlayerIcon, x, y, null);
        g2.rotate(-(game.getAngle(game.getPlayer())), x + PLAYER_SIZE /2, y + PLAYER_SIZE /2);
    }

    /**
     * Draw the enemies.
     */
    private void drawEnemies() {
        for (Enemy enemy : game.getEnemies()) {
            drawCenteredEnemy(enemy, enemy.getRoundedX(), enemy.getRoundedY());
        }
    }

    /**
     * Draw a enemy centered at the specified row and column.
     * @param enemy the enemy to be drawn.
     * @param x the column of the enemy.
     * @param y the row of the enemy.
     */
    private void drawCenteredEnemy(Enemy enemy, int x, int y) {
        Image enemyIcon = enemy.getIcon();//.getScaledInstance((int)round(enemy.getIcon().getWidth(null)/1.5),
                                                              //(int)round(enemy.getIcon().getHeight(null)/1.5), 0);
        x = (int) (x * xScale - enemyIcon.getWidth(null) / 2 + xScale/2);
        y = (int) (y * yScale - enemyIcon.getHeight(null) / 2 + yScale/2);
        g2.rotate(game.getAngle(enemy), x + ENEMY_SIZE /2, y + ENEMY_SIZE /2);
        g2.drawImage(enemyIcon, x, y, null);
        g2.rotate(-(game.getAngle(enemy)), x + ENEMY_SIZE /2, y + ENEMY_SIZE /2);
    }

    /**
     * The field view component needs to be redisplayed. Copy the
     * internal image to screen.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(6));

        xScale = (double) size.width / gridWidth;
        yScale = (double) size.height / gridHeight;
        xScale = yScale = min(xScale, yScale);

        drawLinesBetweenAllStations();
        highlightActiveRoute();
        drawStations();
        drawEnemies();
        drawPlayer();
    }

    public void displayObjective() {
        if (objective == null) {
            objective = new JLabel();
            objective.setFont(getFont().deriveFont(20.0f));
            objective.setForeground(Color.WHITE);
            add(objective);
            objective.setText(OBJECTIVE_TEXT);
            objective.setSize(objective.getPreferredSize());
            objective.setLocation(size.width / 2 - objective.getSize().width / 2,
                    80);
        }
    }

    public void displayCommandInstruction() {
        if (commandLabel == null) {
            commandLabel = new JLabel();
            commandLabel.setFont(getFont().deriveFont(20.0f));
            commandLabel.setForeground(Color.WHITE);
            add(commandLabel);
            commandLabel.setText(GAME_COMMAND);
            commandLabel.setSize(commandLabel.getPreferredSize());
            commandLabel.setLocation(size.width / 2 - commandLabel.getSize().width / 2,
                    110);
        }
    }

    public void removeCommandInstruction() {
        remove(commandLabel);
    }

    public void removeObjective() {
        remove(objective);
    }

    public void displayBigCountDown(int timeLeft) {
        if (bigCountDown == null) {
            bigCountDown = new JLabel();
            bigCountDown.setFont(getFont().deriveFont(100.0f));
            bigCountDown.setForeground(Color.WHITE);
            add(bigCountDown);
            bigCountDown.setText("" + timeLeft);
            bigCountDown.setSize(bigCountDown.getPreferredSize());
            bigCountDown.setLocation(size.width / 2 - bigCountDown.getSize().width / 2,
                    size.height / 2 - bigCountDown.getSize().height / 2);
        }
        bigCountDown.setText("" + timeLeft);
    }

    public void removeBigCountDown() {
        remove(bigCountDown);
    }

    public void updateScore(double score) {
        scoreCounter.setText("Score: " + (int)round(score));
        scoreCounter.setSize(scoreCounter.getPreferredSize());
    }

    public void updateHealth(double health) {
        if (health < 80) healthCounter.setForeground(Color.YELLOW);
        if (health < 50) healthCounter.setForeground(Color.ORANGE);
        if (health < 20) healthCounter.setForeground(Color.RED);
        healthCounter.setText("Health: " + (int)round(health));
        healthCounter.setSize(healthCounter.getPreferredSize());
    }

    public int getDepth() {
        return depth;
    }
}
