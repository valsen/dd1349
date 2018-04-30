package main;

import main.gui.GUI;
import main.world.*;
import main.world.graphs.TestGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

public class Game {

    private ArrayList<StationGraph> graphs = new ArrayList<>();
    private Field field;
    private StationGraph currentGraph;
    private Train mainTrain;
    private GUI gui;
    private Timer timer;
    private boolean running = true;
    private static final int fps = 60;
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
        timer = new Timer(1000 / fps, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    gui.getMap().updateView();
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    public StationGraph getCurrentGraph() {
        return currentGraph;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean bool) {
        running = bool;
    }
}
