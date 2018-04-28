package main;

import main.gui.GUI;
import main.world.Field;
import main.world.Location;
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
    private static final int width = 800;
    private static final double WIDTH_TO_DEPTH_FACTOR = 1536.0 / 2048.0;
    private static final int depth = (int) Math.round(width * WIDTH_TO_DEPTH_FACTOR);

    public Game() {
        field = new Field(width, depth);
        graphs.add(new TestGraph(field));
        currentGraph = graphs.get(0);
        mainTrain = new Train(field, new Location(0, 0));
        gui = new GUI(this, width, depth);
    }

    public void run() {
        gui.getMap().buildMap();
        while (true) {
            gui.getMap().updateView();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    public StationGraph getCurrentGraph() {
        return currentGraph;
    }
}
