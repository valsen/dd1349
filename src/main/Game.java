package main;

import main.world.Field;
import main.world.StationGraph;
import main.world.graphs.TestGraph;

import java.util.ArrayList;

public class Game {

    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private Field field;
    private StationGraph currentGraph;

    public Game(int width) {
        field = new Field(width);
        graphs.add(new TestGraph());
    }
}
