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
    private static final int TRAIN_SIZE = 50;
    private static final int STATION_SIZE = 15;
    private static final int VICTIM_SIZE = 50;
    private static final String OBJECTIVE_TEXT = "Hit enemies from behind to collect points. Avoid frontal collisions!";
    private static final String GAME_COMMAND = "Use spacebar to control your spaceship's route";
    private final double GRID_VIEW_SCALING_FACTOR = 1;
    private GUI gui;
    private JLabel scoreCounter;
    private JLabel healthCounter;
    private JLabel objective;
    private JLabel commandLabel;
    private JLabel bigCountDown;
    private JLabel gameOver;
    private Game game;
    private StationGraph currentGraph;
    private BufferedImage bgImage;
    private Image fieldImage;
    private Image trainIcon;
    private Image scaledTrainIcon;
    private Map<Station, StationLabel> stationLabels = new HashMap<>();
    private int gridWidth, gridHeight;
    private double xScale, yScale;
    private Dimension size;
    private Graphics g;
    private Graphics2D g2;

    /**
     * Create a new MapView component.
     */
    MapView(GUI gui, Game game, int height, int width)
    {
        this.gui = gui;
        this.game = game;
        currentGraph = game.getCurrentGraph();
        setLayout(null); //required for custom placement of labels.
        setBackground(BG_COLOR);
        xScale = yScale = GRID_VIEW_SCALING_FACTOR;
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(width, height);
        try {
            trainIcon = ImageIO.read(new File("src/Sprites/mainship.png"));
        }
        catch (IOException e) {
            System.out.println("Failed to load all icons.");
        }
        scaledTrainIcon = trainIcon.getScaledInstance(TRAIN_SIZE, TRAIN_SIZE, 0);
        createScoreCounter();
        createHealthCounter();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false), "toggle route");
        getActionMap().put("toggle route", new ToggleRouteAction());
    }

    private void createScoreCounter() {
        scoreCounter = new JLabel();
        scoreCounter.setFont(getFont().deriveFont(16.0f));
        scoreCounter.setText("Score: 0");
        scoreCounter.setLocation(10, 10);
        scoreCounter.setSize(scoreCounter.getPreferredSize());
        scoreCounter.setForeground(Color.WHITE);
        add(scoreCounter);
    }

    private void createHealthCounter() {
        healthCounter = new JLabel();
        healthCounter.setFont(getFont().deriveFont(16.0f));
        healthCounter.setText("Health: 100");
        healthCounter.setLocation(10, 30);
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
     * Build the map.
     */
    public void buildMap()
    {
        if(!isVisible()) {
            setVisible(true);
        }
        //createLabels();
        preparePaint();
    }

    /**
     * Update the view of the map and trains.
     */
    public void updateView()
    {
        if (!getSize().equals(size)) {
            preparePaint();
        }
        eraseMap();
        drawLinesBetweenAllStations();
        highlightActiveRoute();
        drawStations();
        //drawStationLabels();
        drawVictims();
        drawTrain();
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
     * Prepare for a new round of painting. Since the components
     * may be resized, compute the scaling factor again. Create
     * a buffered image of the map to use as background for next
     * update of view.
     */
    private void preparePaint()
    {
            size = getSize();
            fieldImage = createImage(size.width, size.height);

            g = fieldImage.getGraphics();
            g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(6));

            xScale = (double) size.width / gridWidth;
            yScale = (double) size.height / gridHeight;
            xScale = yScale = min(xScale, yScale);

            drawLinesBetweenAllStations();
            drawNewBackground();
            highlightActiveRoute();
            drawStations();
            //drawStationLabels();
    }

    /**
     * Create buffered image of the whole map except for the trains.
     */
    private void drawNewBackground() {
        bgImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bgImage.getGraphics();
        paintComponent(graphics);
    }

    /**
     * Paint image of map as background.
     */
    private void drawBackground() {
        g.drawImage(bgImage, 0, 0, null);
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
     * Highlight the rail from the train's current position
     * to the next-next station.
     */
    private void highlightActiveRoute() {
        Train train = game.getPlayer();
        Station next = train.getNextStation();
        Station nextNext = train.getNextNextStation();
        int trainXPos = (int) (train.getX() * xScale + (xScale / 2));
        int trainYPos = (int) (train.getY() * yScale + (yScale / 2));
        int nextXPos = (int) (next.getX() * xScale + (xScale / 2));
        int nextYPos = (int) (next.getY() * yScale + (yScale / 2));
        int nextNextXPos = (int) (nextNext.getX() * xScale + (xScale / 2));
        int nextNextYPos = (int) (nextNext.getY() * yScale + (yScale / 2));
        g2.setColor(ACTIVE_RAIL_COLOR);
        g2.drawLine(trainXPos, trainYPos, nextXPos, nextYPos);
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
     * Draw the trains.
     */
    private void drawTrain() {
        drawCenteredTrain(game.getPlayer().getRoundedX(), game.getPlayer().getRoundedY());
    }

    /**
     * Draw a train centered at the specified row and column.
     * @param x The column of the train.
     * @param y The row of the train.
     */
    private void drawCenteredTrain(int x, int y) {
        x = (int) (x * xScale - (TRAIN_SIZE) / 2 + xScale / 2);
        y = (int) (y * yScale - (TRAIN_SIZE / 2) + yScale / 2);
        Graphics2D g2d = (Graphics2D)g;
        g2d.rotate(game.getAngle(game.getPlayer()), x + TRAIN_SIZE/2, y + TRAIN_SIZE/2);
        g2d.drawImage(scaledTrainIcon, x, y, null);
        g2d.rotate(-(game.getAngle(game.getPlayer())), x + TRAIN_SIZE/2, y + TRAIN_SIZE/2);
    }

    /**
     * Draw the victims.
     */
    private void drawVictims() {
        for (Victim victim : game.getVictims()) {
            drawCenteredVictim(victim, victim.getRoundedX(), victim.getRoundedY());
        }
    }

    /**
     * Draw a victim centered at the specified row and column.
     * @param victim the victim to be drawn.
     * @param x the column of the victim.
     * @param y the row of the victim.
     */
    private void drawCenteredVictim(Victim victim, int x, int y) {
        Image victimIcon = victim.getIcon();//.getScaledInstance((int)round(victim.getIcon().getWidth(null)/1.5),
                                                              //(int)round(victim.getIcon().getHeight(null)/1.5), 0);
        x = (int) (x * xScale - victimIcon.getWidth(null) / 2 + xScale/2);
        y = (int) (y * yScale - victimIcon.getHeight(null) / 2 + yScale/2);
        Graphics2D g2d = (Graphics2D)g;
        g2d.rotate(game.getAngle(victim), x + VICTIM_SIZE/2, y + VICTIM_SIZE/2);
        g2d.drawImage(victimIcon, x, y, null);
        g2d.rotate(-(game.getAngle(victim)), x + VICTIM_SIZE/2, y + VICTIM_SIZE/2);
    }

    /**
     * The field view component needs to be redisplayed. Copy the
     * internal image to screen.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(fieldImage != null) {
            Dimension currentSize = getSize();
            if(size.equals(currentSize)) {
                g.drawImage(fieldImage, 0, 0, null);
            }
            else {
                // Rescale the previous image.
                g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }

    /**
     * Erase the whole map and fill with background color.
     */
    public void eraseMap() {
        g.setColor(BG_COLOR);
        g.fillRect(0,0, getWidth(), getHeight());
        eraseLabels(stationLabels);
    }

    /**
     * Erase station labels from map.
     * @param map
     */
    private void eraseLabels(Map<Station, StationLabel> map) {
        for (JLabel label : map.values()) {
            try {
                Container parent = label.getParent();
                parent.remove(label);
                parent.validate();
                parent.repaint();
            } catch (NullPointerException npe){
                // npe is fine, means label was a duplicate and never added.
            }
        }
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
                    60);
            System.out.println(objective.getText());
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
                    90);
            System.out.println(commandLabel.getText());
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
        System.out.println(timeLeft);
    }

    public void removeBigCountDown() {
        remove(bigCountDown);
    }

    public void updateScore(double score) {
        scoreCounter.setText("Score: " + (int)round(score));
        scoreCounter.setSize(scoreCounter.getPreferredSize());
        System.out.println(score);
    }

    public void updateHealth(double health) {
        if (health < 80) healthCounter.setForeground(Color.YELLOW);
        if (health < 50) healthCounter.setForeground(Color.ORANGE);
        if (health < 20) healthCounter.setForeground(Color.RED);
        healthCounter.setText("Health: " + (int)round(health));
        healthCounter.setSize(healthCounter.getPreferredSize());
        System.out.println(health);
    }

    public void removeGameOver() {
        remove(gameOver);
    }
}
