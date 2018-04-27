package main.world.lines;

import main.world.Field;

import java.awt.*;
import java.io.File;

public class Line11 extends SubwayLine {

    private static final String LINE_NAME = "Line 11";
    private static final int LINE_NUMBER = 11;
    private static final String COLOR_NAME = "Blue";
    private static final Color LINE_COLOR = new Color(4, 146, 211);
    private static final File STATIONS_FILE = new File("src/LineTextFiles/Line11Stations.txt");
    private static final File CONNECTIONS_FILE = new File("src/LineTextFiles/Line11Connections.txt");

    /**
     * Creates a representation of lines 13.
     */
    public Line11(Field field) {
        super(field, LINE_COLOR, STATIONS_FILE, CONNECTIONS_FILE, LINE_NAME, COLOR_NAME, LINE_NUMBER);
    }

}
