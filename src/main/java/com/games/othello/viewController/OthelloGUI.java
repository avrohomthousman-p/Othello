package com.games.othello.viewController;

import com.games.othello.TileColor;
import com.games.othello.model.*;
import com.games.othello.model.IllegalMoveException;
import com.games.othello.model.OthelloGreedyAlgorithm;
import com.games.othello.model.OthelloModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private Timer timer;



    public OthelloGUI(){
        setupMainWindow();

        model = new OthelloGreedyAlgorithm();

        setupGameBoard();

        setupStatusBar();

        setupTimer();


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

        HashMap<Point, TileColor> coloredTiles = model.startGame();

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


    /**
     * Checks the specified HashMap of board positions for the starting color of
     * the specified board position, and sets its background color accordingly.
     *
     * @param coloredTiles a map of all the positions that start as either black or white.
     * @param i the x index of the position we are setting.
     * @param j the y index of the position we are checking.
     */
    private void setStartingColor(HashMap<Point, TileColor> coloredTiles, int i, int j){
        TileColor color = coloredTiles.get(new Point(i, j));
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
        currentTurn.setBorder(new EmptyBorder(0, 20, 0, 20));
        statusBar.add(currentTurn);


        score.setText(String.format(SCORE_DISPLAY, 2, 2));
        score.setBorder(new EmptyBorder(0, 20, 0, 20));
        statusBar.add(score);


        mainWindow.add(statusBar, BorderLayout.NORTH);
    }


    /**
     * Sets up the timer object that is used to ensure there is a brief
     * pause between the players turn and the computers turn.
     */
    private void setupTimer(){
        timer = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Point> move = model.getComputerMove();
                if(move == null){       //the computer has no more legal moves.
                    return;
                }
                updateBoard(move, TileColor.BLACK);
                updateStatusBar();


                if(model.isGameOver()){
                    JOptionPane.showMessageDialog(null, "Game Over");
                }
            }
        });


        timer.setRepeats(false);
    }


    /**
     * Changes the colors of the specified tiles on the board to be the specified color.
     *
     * @param tilesFlipped the tiles that need their colors changed.
     * @param player the player who now controls those tiles (the color to display).
     */
    private void updateBoard(List<Point> tilesFlipped, TileColor player){
        if(player == TileColor.GREEN)
            throw new IllegalArgumentException("a non green tile can never become green");

        Color c = (player == TileColor.BLACK ? Color.BLACK : Color.WHITE);
        for(Point p : tilesFlipped){
            boardPanels[p.x][p.y].setBackground(c);
        }
    }


    /**
     * Updates the status bar to display the correct score and player turn.
     */
    private void updateStatusBar(){
        String turnDisplay;

        if(model.isGameOver()){
            turnDisplay = (model.getWinner() == TileColor.BLACK ? "Computer Wins." : "You Win!!!");
        }
        else {
            turnDisplay = (model.isPlayersTurn() ? "Your Turn" : "Computers Turn");
        }

        currentTurn.setText(turnDisplay);

        score.setText(String.format(
                SCORE_DISPLAY, model.getScoreOfBlackPlayer(), model.getScoreOfWhitePlayer()));
    }


    /**
     * Responds to clicks on the game board by informing the model that the
     * player went in that location for his/her turn.
     */
    private class BoardClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(model.isGameOver()){       //stop you from taking a turn after the game ends.
                return;
            }

            try{
                if(!model.isPlayersTurn()){
                    throw new IllegalMoveException("its not your turn");
                }


                Point chosen = getPositionClicked(e.getX(), e.getY());

                //now pass this information to the model, and use those results to update the board
                updateBoard(model.getPlayerMove(chosen), TileColor.WHITE);
                updateStatusBar();




                if(model.isGameOver()){
                    JOptionPane.showMessageDialog(null, "Game Over");
                }
                else{
                    timer.restart(); //have the computer take its turn
                }
            }
            catch(IllegalMoveException error){
                JOptionPane.showMessageDialog(null, error.getMessage());
            }
        }


        /**
         * Uses the X and Y coordinate of the click event to calculate which location
         * on the game board was clicked.
         *
         * @param x the X coordinate of the click event origin.
         * @param y the Y coordinate of the click event origin.
         * @return a Position object containing the X and Y index of the panel that was clicked.
         */
        private Point getPositionClicked(int x, int y){
            /*   The coordinate clicked divided by the size of each position tells us the
                     index of the clicked panel.   */
            return new Point(
                    (y / (gameBoard.getWidth() / DIMENSIONS)),
                    (x / (gameBoard.getHeight() / DIMENSIONS)));
        }
    }
}
