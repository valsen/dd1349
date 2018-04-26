package mdi.world.lines;

import mdi.world.SubwayLine;
import mdi.world.Field;

import java.awt.*;
import java.io.File;

public class Line19 extends SubwayLine {

    private static final String LINE_NAME = "Line 19";
    private static final int LINE_NUMBER = 19;
    private static final String COLOR_NAME = "Green";
    private static final Color LINE_COLOR = new Color(29, 172, 88);
    private static final File STATIONS_FILE = new File("src/LineTextFiles/Line19Stations.txt");
    private static final File CONNECTIONS_FILE = new File("src/LineTextFiles/Line19Connections.txt");

    /**
     * Creates a representation of lines 19.
     */
    public Line19(Field field) {
        super(field, LINE_COLOR, STATIONS_FILE, CONNECTIONS_FILE, LINE_NAME, COLOR_NAME, LINE_NUMBER);
    }

}
