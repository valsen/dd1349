package mdi.world;

import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.util.List;

public class Train extends SimulationObject {

    private final int MAX_RAIL_INDEX = subwayLine.getRails().size() - 1;
    private final double SPECIAL_EVENT_PROBABILITY = 0.5;
    private final int STATION_TIME_LIMIT = 100;
    private final File TRAIN_NAMES_FILE = new File("src/TrainTextFiles/TrainNames.txt");
    private final List<Rail> rails = subwayLine.getRails();
    private final Map<Integer, Station> stationIndices = subwayLine.getStationIndices();
    private static List<String> trainNamesList;
    private boolean atStation = false;
    private int stationCounter = 0;
    private boolean movingForward;
    private int railIndex = -1;
    private Random rng;
    private String name;
    private Station nextStation;
    private String specialInfo;
    private String passengerStatus;

    /**
     * Creates a train at the specified location.
     * @param location the location of the train.
     */
    Train(Field field, SubwayLine line, Color color, Location location) {
        super(field, line, color, location);
        for(SimulationObject other : this.field.getObjectsAt(location)) {
            if(other instanceof Rail && other.getSubwayLine().equals(subwayLine)) {
                railIndex = rails.indexOf(other);
            }
        }

        if(railIndex == -1) {
            System.out.println("Failed to find railIndex");
        }
        rng = new Random();
        if (railIndex == 0) {
            movingForward = true;
        }
        else if (railIndex == MAX_RAIL_INDEX) {
            movingForward = false;
        }
        else {
            movingForward = rng.nextBoolean();
        }
        if (movingForward) {
            setNextStation(1);
        }
        else {
            setNextStation(-1);
        }
        trainNamesList = loadNamesFile();
        name = getRandomName();
        specialInfo = getRandomSpecialInfo();
        passengerStatus = getRandomPassengerStatus();
    }

    /**
     * Move train to next rail, but only if no other train going in
     * the same direction on the same subway line is already occupying
     * the next rail. If this train is at a station, increment its
     * time spent at the station, and only move if time spent exceeds
     * the time limit. Reverse train's direction when it reaches an end
     * station.
     */
    void moveToNextRail() {
        if (atStation && stationCounter <= STATION_TIME_LIMIT) {
            stationCounter += rng.nextInt(10);
            if(stationCounter > STATION_TIME_LIMIT) {
                // all passengers aboard, proceed to move.
                atStation = false;
            }
            else {
                // not finished loading passengers, don't move.
                return;
            }
        }
        stationCounter = 0;
        if (movingForward) {
            if (nextIsOccupied()) {
                // wait for train in front to move.
                return;
            }
            move(rails.get(++railIndex).getLocation());
            if (railIndex == MAX_RAIL_INDEX) {
                // reached last station, change direction.
                movingForward = false;
                setNextStation(-1);
            }
            else {
                setNextStation(1);
            }
        }
        else {
            if (nextIsOccupied()) {
                // wait for train in front to move.
                return;
            }
            move(rails.get(--railIndex).getLocation());
            if (railIndex == 0) {
                // reached first station, change direction.
                movingForward = true;
                setNextStation(1);
            }
            else {
                setNextStation(-1);
            }
        }
        if (stationIndices.keySet().contains(railIndex)) {
            atStation = true;
            passengerStatus = getRandomPassengerStatus();
        }

    }

    /**
     * Checks if any train going in the same direction on same-colored line is occupying
     * the next rail this train is going to. If it is occupied, this train does not move.
     * @return true if next rail occupied, otherwise false.
     */
    private boolean nextIsOccupied() {
        for(SimulationObject o : field.getObjectsAt(nextStation.getLocation())) {
            if(o instanceof Train && subwayLine.getColor().equals(o.getSubwayLine().getColor())
                    && movingForward == ((Train) o).movingForward) {
                return true;
            }
        }
        // ok to move
        return false;
    }

    /**
     * Sets the next station for this train.
     * @param k -1 if this train is moving backwards. 1 if moving forward.
     */
    private void setNextStation(int k) {
        nextStation = subwayLine.getNextStation(railIndex + k, movingForward);
    }

    /**
     * Create string with information about the train's current status.
     * @return string with current train status information.
     */
    public String getInfo() {
        StringBuilder result = new StringBuilder();
        result.append("Train information:\n").append(" \n").append(subwayLine.getColorName() + " ")
                .append(subwayLine.getName());
        result.append("\nDestination: ");
        if (movingForward) {
            result.append(subwayLine.getStations().get(subwayLine.getStations().size()-1).getName());
        }
        else {
            result.append(subwayLine.getStations().get(0).getName());
        }
        result.append("\nNext station: ").append(nextStation.getName());
        result.append("\nCurrent station: ");
        if (atStation) {
            result.append(subwayLine.getNextStation(railIndex, !movingForward).getName());
        }
        else {
            result.append("---");
        }
        //result.append("\nTrain name: ").append(name);
        result.append("\n"); // blank line instead of train name
        result.append("\nSeating: ").append(passengerStatus);
        result.append("\nAdditional info: ").append(specialInfo).append("\n");
        return result.toString();
    }

    // Load the names file as an ArrayList of names.
    private List<String> loadNamesFile() {
        List<String> names = new ArrayList<>();
        try {
            Scanner reader = new Scanner(TRAIN_NAMES_FILE);
            while(reader.hasNextLine()) {
                names.add(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            // No file found, but the trains need to have at least some name
            System.out.println("Could not read train names file. All trains are now named \"Åke\".");
            names.add("Åke");
        }
        return names;
    }

    // Return a random name from the list of names.
    private String getRandomName() {
        return trainNamesList.get(new Random().nextInt(trainNamesList.size()));
    }

    // Cook up some passenger status
    private String getRandomPassengerStatus() {
        int statusNumber = rng.nextInt(4);
        switch (statusNumber) {
            case 0:
                return "Most seats available.";
            case 1:
                return "Most seats taken.";
            case 2:
                return "All seats taken.";
            case 3:
                return "Crowded.";
        }
        return "Uncertain";
    }

    // Return a special information string
    private String getRandomSpecialInfo() {
        String res = "None.";
        double r1 = rng.nextDouble();
        if (r1 < SPECIAL_EVENT_PROBABILITY) {
            int r2 = rng.nextInt(4);
            switch (r2) {
                case 0:
                    res = "Delayed by approximately " + (1 + rng.nextInt(5)) + " minutes.";
                    break;
                case 1:
                    res = "Heating is currently not working.";
                    break;
                case 2:
                    res = "Short train.";
                    break;
                case 3:
                    res = "Old train cars.";
                    break;
            }
        }
        return res;
    }

    /**
     * Set special information about this train.
     * @param info The information to inform about.
     */
    public void setSpecialInfo(String info) {
        specialInfo = info;
    }

    public boolean isAtStation() {
        return atStation;
    }
}
