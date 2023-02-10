package com.example.othello.viewController;

import com.example.othello.TileColor;
import com.example.othello.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.HashMap;


/**
 * Runs a game of Othello on a GUI.
 */
public class OthelloGUI {
    private static final int DIMENSIONS = 8;
    private static final String SCORE_DISPLAY = "Black: %d \t White: %d";
    private final JFrame mainWindow = new JFrame();
    private final JPanel gameBoard = new JPanel();
    private final JPanel[][] boardPanels = new JPanel[DIMENSIONS][DIMENSIONS];
    private final OthelloModel model;
    private final JLabel currentTurn = new JLabel();
    private final JLabel score = new JLabel();
    private final JButton next = new JButton("next");
    private boolean playersTurn;
    private boolean gameOver;



    public OthelloGUI(){
        setupMainWindow();

        model = new OthelloGreedyAlgorithm();

        setupGameBoard();

        setupStatusBar();


        playersTurn = true;
        gameOver = false;

        gameBoard.setVisible(true);
        mainWindow.setVisible(true);
    }


    /**
     * Does basic setup of the window the app will run in.
     */
    private void setupMainWindow(){
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setTitle("Othello");
        mainWindow.setSize(700, 750);
        mainWindow.setResizable(false);
        mainWindow.setLayout(new BorderLayout());
    }


    /**
     * Does basic setup of the game board.
     */
    private void setupGameBoard(){
        gameBoard.setSize(700, 700);
        gameBoard.addMouseListener(new BoardClickListener());
        gameBoard.setLayout(new GridLayout(DIMENSIONS, DIMENSIONS));

        HashMap<Position, TileColor> coloredTiles = model.startGame();

        //set up each space on the board
        for(int i = 0; i < DIMENSIONS; i++){
            for(int j = 0; j < DIMENSIONS; j++){
                JPanel current = new JPanel();
                current.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                boardPanels[i][j] = current;
                setStartingColor(coloredTiles, i, j);
                gameBoard.add(current);
            }
        }

        mainWindow.add(gameBoard, BorderLayout.CENTER);
    }


    private void setStartingColor(HashMap<Position, TileColor> coloredTiles, int i, int j){
        TileColor color = coloredTiles.get(new Position(i, j));
        if(color == null){
            boardPanels[i][j].setBackground(Color.GREEN);
        }
        else if(color == TileColor.BLACK){
            boardPanels[i][j].setBackground(Color.BLACK);
        }
        else{
            boardPanels[i][j].setBackground(Color.WHITE);
        }
    }



    /**
     * Sets up the status bar.
     */
    private void setupStatusBar(){
        JPanel statusBar = new JPanel(new FlowLayout());
        statusBar.setSize(700, 50);


        /*     Add contents to the status bar     */
        currentTurn.setText("Your Turn");
        statusBar.add(currentTurn);

        //There are some problems with Tread.sleep()
        //and wait() so instead, the computer only goes when you click
        //this button.
        next.addActionListener(new ButtonListener());
        statusBar.add(next);

        score.setText(String.format(SCORE_DISPLAY, 2, 2));
        statusBar.add(score);


        mainWindow.add(statusBar, BorderLayout.NORTH);
    }


    private void updateBoard(List<Position> tilesFlipped, TileColor player){
        if(player == TileColor.GREEN)
            throw new IllegalArgumentException("a non green tile can never become green");

        Color c = (player == TileColor.BLACK ? Color.BLACK : Color.WHITE);
        for(Position p : tilesFlipped){
            boardPanels[p.i][p.j].setBackground(c);
        }
    }


    /**
     * Updates the status bar to display the correct score and player turn.
     */
    private void updateStatusBar(){
        //TODO
    }


    /**
     * Responds to clicks on the game board by informing the model that the
     * player went in that location for his/her turn.
     */
    private class BoardClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(gameOver){ //stop you from taking a turn after the game ends.
                return;
            }

            try{
                if(!playersTurn){
                    throw new IllegalMoveException("its not your turn");
                }


                int x = e.getX();
                int y = e.getY();


                /*   The coordinate clicked divided by the size of each position tells us the
                     index of the clicked panel.   */
                Position chosen = new Position(
                        (y / (gameBoard.getWidth() / DIMENSIONS)),
                        (x / (gameBoard.getHeight() / DIMENSIONS)));



                //now pass this information to the model, and use those results to update the board
                updateBoard(model.getPlayerMove(chosen), TileColor.WHITE);
                playersTurn = false;
                updateStatusBar();

            }
            catch(IllegalMoveException error){
                JOptionPane.showMessageDialog(null, error.getMessage());
            }
        }
    }


    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(gameOver) return; //this button wont do anything after the game has ended
            if(playersTurn){
                JOptionPane.showMessageDialog(null, "Its your turn. Click next after you have gone.");
            }
            else{
                List<Position> move = model.getComputerMove();
                if(move == null){//the computer has no more legal moves.
                    gameOver = true;
                    return;
                }
                updateBoard(move, TileColor.BLACK);
                playersTurn = true;
            }
        }
    }
}
