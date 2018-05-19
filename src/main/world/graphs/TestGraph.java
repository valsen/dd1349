package main.world.graphs;

import main.world.StationGraph;

public class TestGraph extends StationGraph {

    static final String stationPath = "src/main/graphTextFiles/testGraphStations.txt";
    static final String connectionsPath = "src/main/graphTextFiles/testGraphConnections.txt";
    static final String victimsPath = "src/main/graphTextFiles/enemies.txt";

    public TestGraph() {
        super(stationPath, connectionsPath, victimsPath);
    }
}
