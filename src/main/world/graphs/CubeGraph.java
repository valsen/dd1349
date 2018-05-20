package main.world.graphs;

import main.world.StationGraph;

public class CubeGraph extends StationGraph {

    static final String stationPath = "src/main/graphTextFiles/cubeGraphStations.txt";
    static final String connectionsPath = "src/main/graphTextFiles/cubeGraphConnections.txt";
    static final String victimsPath = "src/main/graphTextFiles/enemies.txt";

    public CubeGraph() {
        super(stationPath, connectionsPath, victimsPath);
    }
}
