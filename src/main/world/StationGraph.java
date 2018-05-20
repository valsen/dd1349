package main.world;

import main.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.*;

public abstract class StationGraph {

    private ArrayList<Station> stations = new ArrayList<>();
    private HashMap<Station, ArrayList<Station>> connections;
    protected Station startingStation, startNext, startNextNext;
    protected File stationsFile;
    protected File connectionsFile;
    protected File victimsFile;

    public StationGraph(String stationPath, String connectionsPath, String victimsPath) {
        stationsFile = new File(stationPath);
        connectionsFile = new File(connectionsPath);
        victimsFile = new File(victimsPath);
        createStations();
        createConnectionsMap();
        printConnections();
    }

    // Create the stations
    private void createStations() {
        try {
            Scanner scanner = new Scanner(stationsFile);
            while (scanner.hasNext()) {
                String[] stationInfo = scanner.nextLine().split("/");
                Location stationLocation = new Location((int) round(Double.valueOf(stationInfo[2]) * Game.HEIGHT),
                        (int) round(Double.valueOf(stationInfo[1]) * Game.HEIGHT)); //instead of width. constrains 1:1 ratio of map regardless window size.
                int z = (int) round(Double.valueOf(stationInfo[3]) * Game.HEIGHT); //instead of depth. constrains 1:1 ratio of map regardless window size.
                stations.add(new Station(stationLocation.getX(), stationLocation.getY(), z, stationInfo[0], Integer.valueOf(stationInfo[4])));
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
    }



    // Helper method to search for station by name
    public Station findStation(String stationName) {
        for(Station station : stations) {
            if(station.getName().equals(stationName)) {
                return station;
            }
        }
        return null;
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

    public File getEnemiesFile() {
        return victimsFile;
    }

    public Station getStartingStation() {
        return startingStation;
    }

    public Station getStartNext() {
        return startNext;
    }

    public Station getStartNextNext() {
        return startNextNext;
    }
}