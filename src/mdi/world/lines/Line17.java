package mdi.world.lines;

import mdi.world.SubwayLine;
import mdi.world.Field;

import java.awt.*;
import java.io.File;

public class Line17 extends SubwayLine {

    private static final String LINE_NAME = "Line 17";
    private static final int LINE_NUMBER = 17;
    private static final String COLOR_NAME = "Green";
    private static final Color LINE_COLOR = new Color(29, 172, 88);
    private static final File STATIONS_FILE = new File("src/LineTextFiles/Line17Stations.txt");
    private static final File CONNECTIONS_FILE = new File("src/LineTextFiles/Line17Connections.txt");

    /**
     * Creates a representation of lines 17.
     */
    public Line17(Field field) {
        super(field, LINE_COLOR, STATIONS_FILE, CONNECTIONS_FILE, LINE_NAME, COLOR_NAME, LINE_NUMBER);
    }

}