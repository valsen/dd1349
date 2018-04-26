package mdi.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.awt.Color;

import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 * Representation of a subway line.
 */
public abstract class SubwayLine {

    private Field field;
    private String name;
    private int number;
    private Color color;
    private String colorName;
    private File connectionsFile;
    private File stationsFile;
    private ArrayList<Train> trains;
    private List<Station> stations;
    private Map<Integer, Station> stationIndices;
    private List<Rail> rails;

    /**
     * Create a subway line "map" of the specified DEPTH and WIDTH
     */
    public SubwayLine(Field field, Color color, File stationsFile,
                      File connectionsFile, String name, String colorName, int number) {
        this.field = field;
        this.name = name;
        this.number = number;
        this.color = color;
        this.colorName = colorName;
        this.connectionsFile = connectionsFile;
        this.stationsFile = stationsFile;
        trains = new ArrayList<>();
        stations = new ArrayList<>();
        stationIndices = new HashMap<>();
        rails = new ArrayList<>();
        try {
            addAllStations();
            connectAllStations();
        } catch (FileNotFoundException e) {
            System.out.println("The files for " + name + " could not be found. The line was not properly " +
                    "instantiated.");
        }
        findStationIndices();
    }


    /**
     * Adds a new train to a random position on the subway line.
     */
    public void addRandomTrain() {
        Location randomRailLocation = rails.get(new Random().nextInt(rails.size())).getLocation();
        addTrain(new Train(field,this, color, randomRailLocation));
    }

    // Create the stationIndices list
    private void findStationIndices() {
        for(Rail rail : rails) {
            for(SimulationObject o : field.getObjectsAt(rail.getLocation())) {
                if(o instanceof Station && o.getSubwayLine().equals(this)) {
                    stationIndices.put(rails.indexOf(rail), (Station) o);
                }
            }
        }
    }

    public Station getNextStation(int railIndex, boolean movingForward) {
        if(movingForward) {
            for (int i = railIndex; i < rails.size(); i++) {
                if(stationIndices.keySet().contains(i)) {
                    return stationIndices.get(i);
                }
            }
        }
        else {
            for(int i = railIndex; i >= 0; i--) {
                if (stationIndices.keySet().contains(i)) {
                    return stationIndices.get(i);
                }
            }
        }
        return null;
    }

    /**
     * @return The name of this line.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The Rails of this subway line.
     */
    public List<Rail> getRails() {
        return rails;
    }

    public Map<Integer, Station> getStationIndices() {
        return stationIndices;
    }

    /**
     * @return The color of this line.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return The name of this line's color, as a string.
     */
    public String getColorName() {
        return colorName;
    }

    public int getNumber() {
        return number;
    }

    // Helper method for adding a station with a specified name and coordinates
    private void addStation(String name, int row, int col, int orientationDegrees) {
        stations.add(new Station(field,this, Color.BLACK, new Location(row, col), name, orientationDegrees));
    }

    // Helper method for adding a rail. If there is a rail at the location, do nothing. If there is not, add
    // the rail.
    private void addRail(int row, int col) {
        if(field.getObjectsAt(row, col) != null) {
            for (SimulationObject o : field.getObjectsAt(row, col)) {
                if (o instanceof Rail && o.getSubwayLine().equals(this)) {
                    return;
                }
            }
        }
        rails.add(new Rail(field,this, color, new Location(row, col)));
    }

    // Helper method for adding a train.
    private void addTrain(Train train) {
        trains.add(train);
    }

    public ArrayList<Train> getTrains() {
        return trains;
    }

    // Remove all trainsToDraw from this line.
    public void reset() {
        trains.clear();
    }

    public void moveAllTrains() {
        for(Train train : trains) {
            train.moveToNextRail();
        }
    }

    /**
     * Remove all trains from list of trains to draw.
     */
    public void removeAllTrains() {
        trains.removeAll(getTrains());
    }

    private void connectStations(String fromName, String toName) {
        Station fromStation = null;
        Station toStation = null;
        for(Station station : stations) {
            if (station.getName().equals(fromName)) {
                fromStation = station;
            }
            if (station.getName().equals(toName)) {
                toStation = station;
            }
        }
        if(fromStation == null || toStation == null) {
            System.out.println("Could not connect stationsToDraw " + fromName + " and " + toName + ". Please check that " +
                "the spelling is correct in all files.");
            return;
        }
        connectStationsDDA(fromStation, toStation);
    }

    // Add the stationsToDraw of this line
    private void addAllStations() throws FileNotFoundException {
        for(String stationString : loadStations()) {
            // 0 is name, 1 is relative xPos, 2 is relative yPos (unfortunately, since parameters are row, col)
            // 3 is orientation of station in degrees
            String[] parts = stationString.split("/");
            addStation(parts[0], (int) Math.round(field.getDepth() * Double.valueOf(parts[2])),
                    (int) Math.round(field.getWidth() * Double.valueOf(parts[1])), Integer.valueOf(parts[3]));
        }
    }

    // Return a list with Strings of the format "name/relativeXPos/relativeYPos"
    private List<String> loadStations() throws FileNotFoundException {
        Scanner stationReader = new Scanner(stationsFile);
        ArrayList<String> stationStrings = new ArrayList<>();
        while(stationReader.hasNext()) {
            stationStrings.add(stationReader.nextLine());
        }
        return stationStrings;
    }

    // Connect the stationsToDraw we want to connect
    private void connectAllStations() throws FileNotFoundException {
        List<String> connectionStrings = loadConnections();
        for(String connectionString : connectionStrings) {
            String[] parts = connectionString.split("/");
            connectStations(parts[0], parts[1]);
        }
        // Add a rail and station index at the last station
        Location lastStationLocation = stations.get(stations.size()-1).getLocation();
        addRail(lastStationLocation.getRow(), lastStationLocation.getCol());
    }

    // Return a list with Strings of the format: "fromStation/toStation"
    private List<String> loadConnections() throws FileNotFoundException {
        Scanner connectionReader = new Scanner(connectionsFile);
        ArrayList<String> connectionStrings = new ArrayList<>();
        while(connectionReader.hasNext()) {
            connectionStrings.add(connectionReader.nextLine());
        }
        return connectionStrings;
    }

    // Connect two stationsToDraw using Digital Differential Analyzer algorithm.
    private void connectStationsDDA(Station fromStation, Station toStation) {

        int startX = fromStation.getCol();
        int startY = fromStation.getRow();
        int endX = toStation.getCol();
        int endY = toStation.getRow();

        // Ensure the first location has a rail
        addRail(startY, startX);

        int dX = endX - startX;
        int dY = endY - startY;
        int steps;
        if(abs(dX) > abs(dY)) {
            steps = abs(dX);
        }
        else {
            steps = abs(dY);
        }
        double xIncrement = dX / (double) steps;
        double yIncrement = dY / (double) steps;

        // Start "drawing"
        double x = startX;
        double y = startY;
        for(int i = 0; i < steps; i++) {
            x += xIncrement;
            y += yIncrement;
            addRail((int) round(y), (int) round(x));
        }

        // Ensure the last location has a rail
        addRail(endY, endX);
    }

    public List<Station> getStations() {
        return stations;
    }
}