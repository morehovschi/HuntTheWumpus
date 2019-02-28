/*
    Marius Orehovschi
    F18
    Project 10: Hunt the Wumpus
    CS 231
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * Main class in Hunt The Wumpus game;
 * contains both display methods and game logic
 */

public class HuntTheWumpus implements KeyListener {

    private JFrame win;
    private JPanel canvas;
    private Graph graph;

    private JLabel text;

    public HuntTheWumpus(){
        //constructor that displays the initial menu

        this.win=new JFrame("Hunt The Wumpus");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.win.setPreferredSize(new Dimension(800,600));
        this.win.addKeyListener(this);

        this.canvas=new JPanel();

        JButton square=new JButton("Play on a Square Map");
        square.addActionListener(new SquareListener());
        this.canvas.add(square);

        JButton random=new JButton("Play on a Random Map");
        random.addActionListener(new RandomListener());
        this.canvas.add(random);

        this.win.add(this.canvas,BorderLayout.PAGE_START);
        this.win.pack();
        this.win.setVisible(true);
    }

    private void startGame(boolean random) {
        /*
        handles main run of the game;
        resets all JFrame elements;
        parameter random determines whether game is played on a
        randomly generated graph
         */

        this.win=new JFrame("Hunt The Wumpus");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.win.addKeyListener(this);

        this.graph=new Graph();

        //make specified graph
        if(random){
            graph.growRandomly(15);
        } else {
            Random randy=new Random();
            graph.growSquarely(randy.nextInt(3)+4);
        }


        graph.addWumpus();
        graph.addHunter();

        this.canvas=new GraphPanel(this.graph);

        this.text=new JLabel("Use the WASD keys to move and the arrow keys to shoot");
        this.canvas.add(text);

        this.win.add(this.canvas,BorderLayout.CENTER);
        this.win.pack();
        this.win.setVisible(true);
    }

    private void displayMessage() {
        //updates JLabel according to end game state

        if(graph.won()){
            text.setText("You win!");
        } else {
            text.setText("You lose");
        }

        win.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //handles all possible key events

        //if Q is pressed, quit game
        if(e.getKeyCode() == KeyEvent.VK_Q){
            System.exit(0);
        }

        //if canvas is not a GraphPanel, game has not started yet
        if(canvas.getClass() != GraphPanel.class){
            return;
        }

        /*
        act according to pressed key
         */

        //move north
        if(e.getKeyCode() == KeyEvent.VK_W){
            graph.moveHunter(Vertex.Direction.NORTH);
        }
        //move east
        if(e.getKeyCode() == KeyEvent.VK_D){
            graph.moveHunter(Vertex.Direction.EAST);
        }
        //move south
        if(e.getKeyCode() == KeyEvent.VK_S){
            graph.moveHunter(Vertex.Direction.SOUTH);
        }
        //move west
        if(e.getKeyCode() == KeyEvent.VK_A){
            graph.moveHunter(Vertex.Direction.WEST);
        }
        //shoot north
        if(e.getKeyCode() == KeyEvent.VK_UP){
            graph.shoot(Vertex.Direction.NORTH);
        }
        //shoot east
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            graph.shoot(Vertex.Direction.EAST);
        }
        //shoot south
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            graph.shoot(Vertex.Direction.SOUTH);
        }
        //shoot west
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            graph.shoot(Vertex.Direction.WEST);
        }

        //determine if game has ended
        if(graph.endGame()){
            displayMessage();
        }

        win.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Helper class fo drawing graph
     */

    private class GraphPanel extends JPanel{
        private Graph graph;

        public GraphPanel(Graph graph){
            //creates a JPanel with set dimensions
            super();
            this.graph=graph;
            this.setPreferredSize(new Dimension(800,600));
            this.setBackground(Color.white);
        }

        private int graphScale(){
            //determine graph scale according to size

            int horizontalScale=600/graph.getHorizontalSize();

            int verticalScale=450/graph.getVerticalSize();

            return Math.min(horizontalScale,verticalScale);
        }

        public int graphX0(){
            //determine x-coordinate of first drawn vertex

            return 100+graph.maxDir()[3]*graphScale();
        }

        public int graphY0(){
            //determine y-coordinate of first drawn vertex
            return 75+graph.maxDir()[0]*graphScale();
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            graph.draw(g,graphX0(),graphY0(),graphScale());
        }
    }//end GraphPanel

    public void repaint(){

        this.win.repaint();
    }

    //action listeners fro the two buttons
    private class RandomListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            startGame(true);
        }
    }
    private class SquareListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            startGame(false);
        }
    }

    public static void main(String[] args) {
        //run game
        HuntTheWumpus game=new HuntTheWumpus();
    }
}
