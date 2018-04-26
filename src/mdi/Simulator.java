package mdi;

import mdi.gui.GUI;
import mdi.world.lines.*;
import mdi.world.Field;
import mdi.world.SubwayLine;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator {

    private ArrayList<SubwayLine> subwayLines;
    private Field field;
    private GUI gui;
    // Swing timers used for simulation loop
    private Timer timer1;
    private Timer timer2;

    Simulator(int width)
    {
        field = new Field(width);
        gui = new GUI(this, Field.getWidth(), Field.getDepth());
        addSubwayLines();
    }

    /**
     * Create and add all subway lines.
     */
    private void addSubwayLines() {
        subwayLines = new ArrayList<>();
        subwayLines.add(new Line10(field));
        subwayLines.add(new Line11(field));
        subwayLines.add(new Line13(field));
        subwayLines.add(new Line14(field));
        subwayLines.add(new Line17(field));
        subwayLines.add(new Line18(field));
        subwayLines.add(new Line19(field));
    }

    /**
     * Construct a simulation field with default size.
     */
    Simulator(int width, ArrayList<String> lines) {
        field = new Field(width);
        gui = new GUI(this, Field.getWidth(), Field.getDepth());
        subwayLines = new ArrayList<>();
        for (String line : lines) {
                if (line.matches(".*10$"))
                    subwayLines.add(new Line10(field));
                else if (line.matches(".*11$"))
                    subwayLines.add(new Line11(field));
                else if (line.matches(".*13$"))
                    subwayLines.add(new Line13(field));
                else if (line.matches(".*14$"))
                    subwayLines.add(new Line14(field));
                else if (line.matches(".*17$"))
                    subwayLines.add(new Line17(field));
                else if (line.matches(".*18$"))
                    subwayLines.add(new Line18(field));
                else if (line.matches(".*19$"))
                    subwayLines.add(new Line19(field));
        }
    }

    /**
     * Create new trains and add to random locations
     * on each subway line.
     * @param trainsPerLine number of trains per subway line.
     */
    public void trainSample(int trainsPerLine) {
        for (SubwayLine line : subwayLines) {
            for (int n = 0; n < trainsPerLine; n++) {
                line.addRandomTrain();
            }
        }
    }

    /**
     * Run simulation.
     * @param blocksPerSecond moves per second.
     */
    public void runSimulation(int blocksPerSecond) {
        gui.getMap().buildMap();
        timer1 = new Timer(1000 / blocksPerSecond, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (isStarted()) {
                    for (SubwayLine line : subwayLines) {
                        line.moveAllTrains();
                    }
                    gui.getMap().updateView();
                }
            }
        });
        timer1.setInitialDelay(0);
        timer1.start();
        timer2 = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStarted()) {
                    gui.getInfoPanel().updateInfo();
                }
            }
        });
        timer2.setInitialDelay(0);
        timer2.start();
    }

    /**
     * Reset the simulation to a starting position by
     * clearing all objects and states of current simulation.
     * Prepare for new simulation by adding all subway lines again.
     */
    public void reset()
    {
        for (SubwayLine line : subwayLines) {
            line.reset();
        }
        field.clearField();
        gui.getMap().eraseMap();
        gui.getMap().removeAllStations();
        gui.getMap().removeStationLabels();
        gui.getMap().removeAllTrains();
        gui.getInfoPanel().getTextPane().setText("");
        gui.getInfoPanel().resetSelectedTrain();
        // Redo setup of this Simulator object.
        addSubwayLines();
    }

    /**
     *
     * @return True if simulation is running, otherwaise false.
     */
    boolean isStarted() {
        return gui.getControlPanel().isRunning();
    }

    /**
     * @return The field of the simulator.
     */
    public Field getField() {
        return field;
    }

    /**
     *
     * @return list of all subway lines.
     */
    public ArrayList<SubwayLine> getSubwayLines() {
        return subwayLines;
    }

    /**
     *
     * @return the timer1 that handles the simulation loop.
     */
    public Timer getTimer1() {
        return timer1;
    }
}
