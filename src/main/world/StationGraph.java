package main.world;
import main.world.Station;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public abstract class StationGraph {

    private Field field;
    private ArrayList<Station> stations;
    private HashMap<Station, ArrayList<Station>> connections;
    protected File stationsFile;
    protected File connectionsFile;

    public StationGraph(String stationPath, String connectionsPath, Field field) {
        stationsFile = new File(stationPath);
        connectionsFile = new File(connectionsPath);
        createStations();
        createConnections();
    }

    // Create the stations
    private void createStations() {
        try {
            Scanner scanner = new Scanner(stationsFile);
            while (scanner.hasNext()) {
                String[] stationInfo = scanner.nextLine().split("/");
            }
        } catch(FileNotFoundException e) {
            System.out.println("Failed to read stations file.");
        }
    }

    // Create the hashMap
    private void createConnections() {

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