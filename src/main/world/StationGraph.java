package main.world;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public abstract class StationGraph {

    private Field field;
    private ArrayList<Rail> rails = new ArrayList<>();
    private ArrayList<Station> stations = new ArrayList<>();
    private HashMap<Station, ArrayList<Station>> connections;
    private ArrayList<Victim> victims = new ArrayList<>();
    private Location startingLocation;
    protected File stationsFile;
    protected File connectionsFile;
    protected File victimsFile;

    public StationGraph(String stationPath, String connectionsPath, String victimsPath, Field field) {
        this.field = field;
        stationsFile = new File(stationPath);
        connectionsFile = new File(connectionsPath);
        victimsFile = new File(victimsPath);
        createStations();
        createRailConnections();
        createConnectionsMap();
        printConnections();
        createVictims();
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

    // Create the victims
    private void createVictims() {
        Random rnd = new Random();
        try {
            Scanner scanner = new Scanner(victimsFile);
            while (scanner.hasNext()) {
                String[] victimInfo = scanner.nextLine().split("/");
                String victimName = victimInfo[0];
                Station fromStation = findStation(victimInfo[1]);
                Station toStation = findStation(victimInfo[2]);
                String imageFileName = victimInfo[3];
                try {
                    Image victimIcon = ImageIO.read(new File("src/Sprites/victimIcons/" + imageFileName));
                    double ratio = rnd.nextDouble();
                    if (fromStation != null && toStation != null) {
                        Location victimLocation = setVictimLocation(fromStation, toStation, ratio);
                        victims.add(new Victim(field, victimLocation, victimName, victimIcon));
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
    private Location setVictimLocation(Station from, Station to, double ratio) {
        int dx = to.getCol() - from.getCol();
        int dy = (to.getRow() - from.getRow());
        double hyp = sqrt(pow(dx,2) + pow(dy, 2));
        double angle = atan2(dy, dx);
        int victimXPos = (int) (from.getCol() + cos(angle) * hyp * ratio);
        int victimYPos = (int) (from.getRow() + sin(angle) * hyp * ratio);
        return new Location(victimYPos, victimXPos);
    }

    // Connect the stations by rails and add the connections to the connections HashMap
    private void createRailConnections() {
        try {
            Scanner scanner = new Scanner(connectionsFile);
            while (scanner.hasNext()) {
                String[] connectedStations = scanner.nextLine().split("/");
                connectStationsByName(connectedStations[0], connectedStations[1]);
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read connections file in createRailConnections method.");
        }
    }

    // Helper method to search for station by name
    private Station findStation(String stationName) {
        for(Station station : stations) {
            if(station.getName().equals(stationName)) {
                return station;
            }
        }
        return null;
    }

    // Helper method to connect stations by name
    private void connectStationsByName(String fromName, String toName) {
        Station from = findStation(fromName);
        Station to = findStation(toName);
        if(from != null && to != null) {
            connectStationsDDA(from, to);
        } else {
            System.out.println("Failed to connect stations " + fromName + " and " + toName + ". Please ensure" +
                    " spelling is correct and that all files are found.");
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

    // Create the connections HashMap using the connectionsFile
    private void createConnectionsMap() {
        connections = new HashMap<Station, ArrayList<Station>>();
        for(Station station : stations) {
            connections.put(station, new ArrayList<>());
        }
        try {
            Scanner scanner = new Scanner(connectionsFile);
            while (scanner.hasNext()) {
                String[] connected = scanner.nextLine().split("/");
                Station from = findStation(connected[0]);
                Station to = findStation(connected[1]);
                if(from != null && to != null) {
                    connections.get(from).add(to);
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read connections file in createConnectionsMap method.");
        }
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

    public ArrayList<Victim> getVictims() {
        return victims;
    }

    // Debug method to print all (one-directional) connections
    private void printConnections() {
        for (Station station : connections.keySet()) {
            System.out.print(station.getName() + " connects to: ");
            for (Station connection : connections.get(station)) {
                System.out.print(connection.getName() + ", ");
            }
            System.out.println();
        }
    }
}