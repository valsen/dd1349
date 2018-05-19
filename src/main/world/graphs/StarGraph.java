package main.world.graphs;

import main.world.StationGraph;

public class StarGraph extends StationGraph {

    static final String stationPath = "src/main/graphTextFiles/starGraphStations.txt";
    static final String connectionsPath = "src/main/graphTextFiles/starGraphConnections.txt";
    static final String victimsPath = "src/main/graphTextFiles/enemies.txt";

    public StarGraph() {
        super(stationPath, connectionsPath, victimsPath);
    }
}
