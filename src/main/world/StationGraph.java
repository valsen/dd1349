package main.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import static java.lang.Math.round;
import static java.lang.Math.abs;

public abstract class StationGraph {

    private Field field;
    private ArrayList<Rail> rails = new ArrayList<>();
    private ArrayList<Station> stations = new ArrayList<>();
    private HashMap<Station, ArrayList<Station>> connections = new HashMap<>();
    private Location startingLocation;
    protected File stationsFile;
    protected File connectionsFile;

    public StationGraph(String stationPath, String connectionsPath, Field field) {
        this.field = field;
        stationsFile = new File(stationPath);
        connectionsFile = new File(connectionsPath);
        createStations();
    }

    // Create the stations
    private void createStations() {
        try {
            Scanner scanner = new Scanner(stationsFile);
            while (scanner.hasNext()) {
                String[] stationInfo = scanner.nextLine().split("/");
                Location stationLocation = new Location((int) round(Double.valueOf(stationInfo[2]) * field.getDepth()),
                        (int) round(Double.valueOf(stationInfo[1]) * field.getWidth()));
                stations.add(new Station(field, stationLocation, stationInfo[0], Integer.valueOf(stationInfo[3])));
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
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
        if (abs(dX) > abs(dY)) {
            steps = abs(dX);
        } else {
            steps = abs(dY);
        }
        double xIncrement = dX / (double) steps;
        double yIncrement = dY / (double) steps;

        // Start "drawing"
        double x = startX;
        double y = startY;
        for (int i = 0; i < steps; i++) {
            x += xIncrement;
            y += yIncrement;
            addRail((int) round(y), (int) round(x));
        }
    }

    // Helper method for adding a rail. If there is a rail at the location, do nothing. If there is not, add
    // the rail.
    private void addRail(int row, int col) {
        if(field.getObjectsAt(row, col) != null) {
            for (FieldObject o : field.getObjectsAt(row, col)) {
                if (o instanceof Rail) {
                    return;
                }
            }
        }
        rails.add(new Rail(field, new Location(row, col)));
    }

    /**
     * @return The hashmap with station connections of this graph.
     */
    public HashMap<Station, ArrayList<Station>> getConnections() {
        return connections;
    }

    /**
     * Return all stations connected to this one, excluding the previous station. If previousStation
     * is null or not in the list, return all connected stations.
     * @param switchStation The station from which to look for connections.
     * @param previousStation The station that will be excluded from the list of stations returned.
     * @return A list of all stations connected to switchStation, excluding previousStation.
     */
    public ArrayList<Station> getAvailableStations(Station switchStation, Station previousStation) {
        if(previousStation == null) {
            return connections.get(switchStation);
        }
        ArrayList<Station> available = new ArrayList<>();
        for(Station station : connections.get(switchStation)) {
            if(!station.equals(previousStation)) {
                available.add(station);
            }
        }
        return available;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }
}