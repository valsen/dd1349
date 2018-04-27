package mdi.world;
import mdi.world.Station;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class StationGraph {

    private HashMap<Station, ArrayList<Station>> connections;
    private File stationsFile;
    private File connectionsFile;

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
}