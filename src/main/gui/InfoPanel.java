package main.gui;

import main.world.Train;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class InfoPanel extends JPanel {

    private static final int PANEL_WIDTH = 230;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private Dimension size;
    private Image trainIconBlue;
    private Image trainIconRed;
    private Image trainIconGreen;
    private Train selectedTrain;

    InfoPanel(int height) {
        setSize(PANEL_WIDTH, height);
        size = getSize();
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBackground(Color.WHITE);
        try {
            trainIconBlue = ImageIO.read(new File("src/Sprites/icons8-railcar-40.png"));
            trainIconRed = ImageIO.read(new File("src/Sprites/röd.png"));
            trainIconGreen = ImageIO.read(new File("src/Sprites/grön.png"));
        }
        catch (IOException e) {
            System.out.println("Failed to load all icons.");
        }
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setMargin(new Insets(5, 5, 5, 2));
        textPane.setText("");

        scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(PANEL_WIDTH, getHeight()));
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(scrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);
    }

    /**
     * Stylizes the display of train info.
     * @param info
     * @param train
     */
    void updateTrainInfo(String info, Train train) {
        String[] input = info.split("\n");
        String[] styles = new String[]{ "header",     // "Train information:"
                "icon",     // train icon
                "color",    // subway line
                "regular",  // destination
                "regular",  // next station
                "regular",  // current
                "regular",  // train name (currently just blank line)
                "regular",  // passenger status
                "regular"  // additional info
        };
        textPane.setText(""); // clear all displayed info
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc, train);
        try {
            for (int i=0; i < styles.length; i++) {
                doc.insertString(doc.getLength(), input[i]+"\n", doc.getStyle(styles[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Creates text and icon styles for the train info display.
     * @param doc
     * @param train
     */
    void addStylesToDocument(StyledDocument doc, Train train) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        // header style
        Style header = doc.addStyle("header", regular);
        StyleConstants.setBold(header, true);
        StyleConstants.setFontSize(header, 16);

        // text color matches subway line color
        Style color = doc.addStyle("color", def);
        StyleConstants.setBold(color, true);
        StyleConstants.setForeground(color, Color.WHITE);
        StyleConstants.setBackground(color, train.getColor());

        // train icon
        Style icon = doc.addStyle("icon", StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE));
        StyleConstants.setAlignment(icon, StyleConstants.ALIGN_LEFT);
        ImageIcon trainIcon = new ImageIcon();
        String trainColor = train.getSubwayLine().getColorName();
        if (trainColor.matches("[Gg]reen")) {
            trainIcon = new ImageIcon(trainIconGreen);
        }
        else if (trainColor.matches("[Rr]ed")) {
            trainIcon = new ImageIcon(trainIconRed);
        }
        else if (trainColor.matches("[Bb]lue")){
            trainIcon = new ImageIcon(trainIconBlue);
        }
        else {
            System.err.println("no train icon match.");
        }
        StyleConstants.setIcon(icon, trainIcon);
    }

    /**
     * Update the info in the InfoPanel.
     */
    public void updateInfo() {
        try {
            updateTrainInfo(selectedTrain.getInfo(), selectedTrain);
        }
        catch (NullPointerException e) {
            // no train clicked yet, show default text.
            textPane.setText("Click on a train for info");
        }
    }

    /**
     *
     * @return preferred size as a Dimension.
     */
    public Dimension getPreferredSize()
    {
        return size;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setSelectedTrain(Train train) { selectedTrain = train; }

    public void resetSelectedTrain() { selectedTrain = null; }

}
