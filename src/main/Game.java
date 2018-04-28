package main;

import main.gui.GUI;
import main.world.Field;
import main.world.StationGraph;
import main.world.Train;
import main.world.graphs.TestGraph;

import java.util.ArrayList;

public class Game {

    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private Field field;
    private StationGraph currentGraph;
    private Train mainTrain;
    private GUI gui;

    public Game(int width) {
        field = new Field(width);
        graphs.add(new TestGraph(field));
        currentGraph = graphs.get(0);
        mainTrain = new Train(currentGraph.getStartingLocation);
    }

    public StationGraph getCurrentGraph() {
        return currentGraph;
    }
}
