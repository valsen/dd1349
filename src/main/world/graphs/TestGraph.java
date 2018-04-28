package main.world.graphs;

import main.world.Field;
import main.world.StationGraph;

import java.io.File;

public class TestGraph extends StationGraph {

    static final String stationPath = "src/main/graphTextFiles/testGraphStations.txt";
    static final String connectionsPath = "src/main/graphTextFiles/testGraphConnections.txt";

    public TestGraph(Field field) {
        super(stationPath, connectionsPath, field);
    }
}
