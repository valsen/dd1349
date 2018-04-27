package main.world.lines;

import main.world.SubwayLine;
import main.world.Field;

import java.awt.*;
import java.io.File;

public class Line13 extends SubwayLine {

    private static final String LINE_NAME = "Line 13";
    private static final int LINE_NUMBER = 13;
    private static final String COLOR_NAME = "Red";
    private static final Color LINE_COLOR = new Color(241, 27, 35);
    private static final File STATIONS_FILE = new File("src/LineTextFiles/Line13Stations.txt");
    private static final File CONNECTIONS_FILE = new File("src/LineTextFiles/Line13Connections.txt");

    /**
     * Creates a representation of lines 13.
     */
    public Line13(Field field) {
        super(field, LINE_COLOR, STATIONS_FILE, CONNECTIONS_FILE, LINE_NAME, COLOR_NAME, LINE_NUMBER);
    }

}
