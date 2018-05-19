package main.gui;

import main.*;
import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame {

    static final Color BG_COLOR = new Color(45, 52, 55);
    private static final String GAME_OVER_TEXT = "Game Over";
    private MapView map;
    private JLabel gameOver;

    /**
     * Create a view of the given WIDTH and height.
     * @param height The simulation's height.
     * @param width  The simulation's WIDTH.
     */
    public GUI(Game game, int width, int height, int depth)
    {
        setTitle("Dysfunctional train game");
        //setLocation(50, 50);
        setBackground(BG_COLOR);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 600));

        map = new MapView(this, game, height, width, depth);

        Container contents = getContentPane();
        contents.add(map, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void displayGameOver() {
        gameOver = new JLabel(GAME_OVER_TEXT, SwingConstants.CENTER);
        gameOver.setFont(new Font("Dialog", Font.ITALIC + Font.BOLD, 100));
        gameOver.setForeground(new Color(255, 69, 70));
        gameOver.setBackground(new Color(208, 208, 208));
        gameOver.setOpaque(false);
        ((JPanel)getGlassPane()).setLayout(new BorderLayout());
        ((JPanel)getGlassPane()).add(gameOver, BorderLayout.CENTER);
        getGlassPane().setVisible(true);
        // gameOver.setSize(gameOver.getPreferredSize());
        System.out.println(map.getSize().height + "   " + map.getSize().width);
    }



    /**
     * @return the view of the map.
     */
    public MapView getMap() { return map; }
}