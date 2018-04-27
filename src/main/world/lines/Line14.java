package main.world.lines;

import main.world.Field;

import java.awt.*;
import java.io.File;

public class Line14 extends SubwayLine {

    private static final String LINE_NAME = "Line 14";
    private static final int LINE_NUMBER = 14;
    private static final String COLOR_NAME = "Red";
    private static final Color LINE_COLOR = new Color(241, 27, 35);
    private static final File STATIONS_FILE = new File("src/LineTextFiles/Line14Stations.txt");
    private static final File CONNECTIONS_FILE = new File("src/LineTextFiles/Line14Connections.txt");

    /**
     * Creates a representation of lines 14.
     */
    public Line14(Field field) {
        super(field, LINE_COLOR, STATIONS_FILE, CONNECTIONS_FILE, LINE_NAME, COLOR_NAME, LINE_NUMBER);
    }

}
