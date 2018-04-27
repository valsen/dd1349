package main.gui;

import main.Simulator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel implements ActionListener {

    private static final int PANEL_WIDTH = 230;
    private Dimension size;
    private JTextField speedInput = new JTextField("15", 8);
    private JTextField trainsPerLineInput = new JTextField("2", 8);
    private JLabel speedLabel = new JLabel("Speed: ");
    private JLabel trainsLabel = new JLabel("Trains per line: ");
    private JButton button = new JButton();
    private Simulator simulator;
    private boolean running = false;

    ControlPanel(Simulator simulator, int height) {
        this.simulator = simulator;
        setSize(PANEL_WIDTH, height);
        size = getSize();
        setBackground(Color.LIGHT_GRAY);
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Pair labels with matching text fields.
        speedLabel.setLabelFor(speedInput);
        trainsLabel.setLabelFor(trainsPerLineInput);

        button.setText("Start");
        button.setPreferredSize(new Dimension(100, 50));
        button.addActionListener(this);

        // Add components to panel
        add(trainsLabel);
        add(trainsPerLineInput);
        add(speedLabel);
        add(speedInput);
        add(button);
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Toggle simulation on/off with button press.
     * BUG ALERT: clicking start after stop not working
     * properly.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        running = !running;
        if (running) {
            // start simulation
            try {
                int speed = Math.abs(Integer.parseInt(speedInput.getText()));
                int trainsPerLine = Math.abs(Integer.parseInt(trainsPerLineInput.getText()));
                runSimulation(trainsPerLine, speed);
                button.setText("Stop");
                speedInput.setText(Integer.toString(speed));
                trainsPerLineInput.setText(Integer.toString(trainsPerLine));
            } catch (NumberFormatException e1){
                System.out.println("Invalid number. Only positive integers allowed.");
                running = !running;
            }
        }
        else {
            // stop simulation
            button.setText("Start");
            simulator.getTimer1().stop();
            simulator.reset();
        }
    }

    /**
     * Start simulation.
     * @param trainsPerLine
     * @param speed
     */
    private void runSimulation(int trainsPerLine, int speed) {
        simulator.trainSample(trainsPerLine);
        simulator.runSimulation(speed);
    }

    /**
     *
     * @return preferred size as a Dimension.
     */
    public Dimension getPreferredSize()
    {
        return size;
    }
}
