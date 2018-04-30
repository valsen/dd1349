package main.gui;

import main.Game;
import main.world.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    private static final int TRAIN_SIZE = 30;
    private static final int STATION_SIZE = 15;
    private static final int VICTIM_SIZE = 50;
    private final double GRID_VIEW_SCALING_FACTOR = 1;
    private GUI gui;
    private Game game;
    private StationGraph currentGraph;
    private BufferedImage bgImage;
    private Image fieldImage;
    private Image trainIcon;
    private Image scaledTrainIcon;
    private Set<Train> trainsToDraw = new HashSet<>();
    private Set<Station> stationsToDraw = new HashSet<>();
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
            trainIcon = ImageIO.read(new File("src/Sprites/grön.png"));
        }
        catch (IOException e) {
            System.out.println("Failed to load all icons.");
        }
        scaledTrainIcon = trainIcon.getScaledInstance(TRAIN_SIZE, TRAIN_SIZE, 0);
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
        addStations();
        createLabels();
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
        drawBackground();
        drawTrains();
        repaint();
    }

    /**
     * Create list of all stations to draw.
     */
    private void addStations() {
        stationsToDraw.addAll(game.getCurrentGraph().getStations());
    }

    /**
     * Create and add labels for each station. Labels are
     * either straight or rotated.
     */
    private void createLabels() {
        for (Station station : stationsToDraw) {
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
            drawStations();
            drawVictims();
            drawNewBackground();
            drawStationLabels();
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
        for (Station station : stationsToDraw) {
            drawCenteredStation(station.getCol(), station.getRow());
        }
    }

    /**
     * Paint a station on the field with the middle of it at the supplied position.
     */
    private void drawCenteredStation(int x, int y) {
        int radius = 4;
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
            x1 = (int) (from.getCol() * xScale + (xScale / 2));
            y1 = (int) (from.getRow() * yScale + (yScale / 2));

            for(Station to : game.getCurrentGraph().getAvailableStations(from, null)) {
                x2 = (int) (to.getCol() * xScale + (xScale / 2));
                y2 = (int) (to.getRow() * yScale + (yScale / 2));
                g2.setColor(INACTIVE_RAIL_COLOR);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Paint station labels on map.
     */
    private void drawStationLabels() {
        for (Station station : stationsToDraw) {
            int xPixel = (int) Math.round(station.getCol() * xScale);
            int yPixel = (int) Math.round(station.getRow() * yScale);
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
    private void drawTrains() {
        for(Train train : trainsToDraw) {
            drawCenteredTrain(train.getCol(), train.getRow());
        }
    }

    /**
     * Draw a train centered at the specified row and column.
     * @param x The column of the train.
     * @param y The row of the train.
     */
    private void drawCenteredTrain(int x, int y) {
        x = (int) (x * xScale - (TRAIN_SIZE) / 2 + xScale / 2);
        y = (int) (y * yScale - (TRAIN_SIZE / 2) + yScale / 2);

        g.drawImage(scaledTrainIcon, x, y, null);
    }

    /**
     * Draw the victims.
     */
    private void drawVictims() {
        for (Victim victim : game.getCurrentGraph().getVictims()) {
            drawCenteredVictim(victim, victim.getCol(), victim.getRow());
        }
    }

    /**
     * Draw a victim centered at the specified row and column.
     * @param victim the victim to be drawn.
     * @param x the column of the victim.
     * @param y the row of the victim.
     */
    private void drawCenteredVictim(Victim victim, int x, int y) {
        x = (int) (x * xScale - VICTIM_SIZE / 2 + xScale / 2);
        y = (int) (y * xScale - VICTIM_SIZE / 2 + yScale / 2);
        Image scaledVictimIcon = victim.getIcon().getScaledInstance(VICTIM_SIZE, VICTIM_SIZE, 0);
        g.drawImage(scaledVictimIcon, x, y, null);
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
}
