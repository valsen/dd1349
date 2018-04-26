package mdi;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class uses user input to decide how many simulations to launch in parallel.
 * Each simulation can have any combination of subway lines, and each simulation
 * sets its own speed and number of trains per line.
 */
class SimulatorConfigurator {

    private Simulator simulator;

    private ArrayList<ArrayList> simulations = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();
    private ArrayList<Integer> trains = new ArrayList<>();
    private ArrayList<Integer> speeds = new ArrayList<>();
    private ArrayList<String> allLines = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private int speed; //moves per second
    private int trainsPerLine;

    /**
     * Everything is done in this constructor. Each simulation
     * is run in a separate thread. An arraylist containing string
     * names of the selected lines for the simulation is passed
     * as a parameter to Simulator, along with train speed and
     * number of trains per line.
     * @throws InterruptedException
     */
    SimulatorConfigurator(Simulator simulator) throws InterruptedException {
        this.simulator = simulator;

        // Add all lines to a list, represented by a string.
        allLines.add("Blue lines 10");
        allLines.add("Blue lines 11");
        allLines.add("Red lines 13");
        allLines.add("Red lines 14");
        allLines.add("Green lines 17");
        allLines.add("Green lines 18");
        allLines.add("Green lines 19");

        System.out.println();
        boolean gotNumberOfSimulations = false;
        int numberOfSimulations = 0;
        while(!gotNumberOfSimulations) {
            try {
                System.out.print("Number of simulations: ");
                numberOfSimulations = new Scanner(System.in).nextInt();
                gotNumberOfSimulations = true;
                System.out.println("Number of simulations set to " + numberOfSimulations + ".\n");
            } catch (InputMismatchException e) {
                System.out.println("Try again, and enter an integer this time!\n");
            }
        }

        for (int i = 0; i < numberOfSimulations; i++) {
            simulations.add(new ArrayList<ArrayList>());
            ArrayList<String> selectedLines = new ArrayList<>();

            System.out.println("Configuring Simulation " + (i + 1) + "\n");

            for (String line : allLines) {
                System.out.print("Include " + line + "? [yes]/[no]: ");
                String input = scanner.next();
                if (input.matches("[Nn][Oo]")) {
                    System.out.println(line + " was not added to the selected lines.\n");
                }
                else if (input.matches("[Yy][Ee][Ss]")){
                    selectedLines.add(line);
                    System.out.println("Added " + line + " to the selected lines.\n");
                }
                else {
                    System.out.println("Invalid answer. Will not add " + line + ". Moving on...\n");
                }
            }

            if (selectedLines.isEmpty()) {
                System.out.println("No subway lines loaded for simulation. Exiting...");
                Thread.sleep(2000);
                System.exit(1);
            }

            simulations.get(i).add((selectedLines));
            System.out.println("Added " + simulations.get(i).toString() + " to simulation " + (i + 1) + ".\n");

            boolean gotNumberOfTrains = false;
            int numberOfTrains = 0;
            while(!gotNumberOfTrains) {
                try {
                    System.out.print("Number of trains per subway line: ");
                    numberOfTrains = new Scanner(System.in).nextInt();
                    gotNumberOfTrains = true;
                    System.out.println("Simulation " + i + " will contain " + numberOfTrains +
                            " trains per subway line.\n");
                } catch (InputMismatchException e) {
                    System.out.println("Try again, and enter an integer this time!\n");
                }
            }
            trains.add(numberOfTrains);

            boolean gotMovesPerSecond = false;
            int speed = 0;
            while(!gotMovesPerSecond) {
                try {
                    System.out.print("Moves per second: ");
                    speed = new Scanner(System.in).nextInt();
                    gotMovesPerSecond = true;
                    System.out.println();
                } catch (InputMismatchException e) {
                    System.out.println("Try again, and enter an integer this time!");
                    System.out.println();
                }
            }
            speeds.add(speed);

            System.out.println();
        }

        System.out.println("Launching " + numberOfSimulations + " simulations... \n");
        Thread.sleep(1000);

        // Create separate threads for each simulation.
        for (int i = 0; i < numberOfSimulations; i++) {
            int numberOfTrains = trains.get(i);
            int speed = speeds.get(i);
            ArrayList lines = (ArrayList) simulations.get(i).get(0);

            threads.add(new Thread() {
                public void run() {
                    Simulator simulator = new Simulator(500, lines);
                    simulator.trainSample(numberOfTrains);
                    simulator.runSimulation(speed);
                }
            });
        }

        // Run all simulation threads.
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void runSimulation(int trainsPerLine, int speed) {
        simulator.trainSample(trainsPerLine);
        simulator.runSimulation(speed);
    }

    public void setSpeed(int k) {
        boolean gotMovesPerSecond = false;
        speed = 0;
        while(!gotMovesPerSecond) {
            try {
                //System.out.print("Moves per second: ");
                speed = k;
                gotMovesPerSecond = true;
                System.out.println();
            } catch (InputMismatchException e) {
                System.out.println("Try again, and enter an integer this time!");
                System.out.println();
            }
        }
        speed = k;
    }

    public void setTrainsPerLine(int k) {
        boolean gotNumberOfTrains = false;
        trainsPerLine = 0;
        while(!gotNumberOfTrains) {
            try {
                System.out.print("Number of trains per subway line: ");
                trainsPerLine = k;
                gotNumberOfTrains = true;
                System.out.println();
                System.out.println("Configured done. Click the start button to run simulation.\n");
            } catch (InputMismatchException e) {
                System.out.println("Try again, and enter an integer this time!\n");
            }
        }
        trainsPerLine = k;
    }
}
