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
    private final JPanel[][] boardColor;
    private final JFrame mainWindow;
    private final OthelloModel model;
    private final JButton next;
    private boolean playersTurn;
    private boolean gameOver;

    public OthelloGUI(){
        //basic setup
        mainWindow = new JFrame();
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setTitle("Othello");
        mainWindow.setSize(700, 700);
        mainWindow.setResizable(false);//resize will mess up the mouse listener. see below
        mainWindow.addMouseListener(new BoardClickListener());
        mainWindow.getContentPane().setLayout(new GridLayout(DIMENSIONS, DIMENSIONS));

        model = new OthelloGreedyAlgorithm();
        HashMap<Position, TileColor> coloredTiles = model.startGame();
        //set up each space on the board
        boardColor = new JPanel[DIMENSIONS][DIMENSIONS];
        for(int i = 0; i < DIMENSIONS; i++){
            for(int j = 0; j < DIMENSIONS; j++){
                JPanel current = new JPanel();
                current.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardColor[i][j] = current;
                setStartingColor(coloredTiles, i, j);
                mainWindow.getContentPane().add(current);
            }
        }

        //There are some problems with Tread.sleep()
        //and wait() so instead, the computer only goes when you click
        //this button.
        next = new JButton("next");
        next.addActionListener(new ButtonListener());
        boardColor[7][7].add(next);
        playersTurn = true;
        gameOver = false;

        mainWindow.setVisible(true);
    }

    private void setStartingColor(HashMap<Position, TileColor> coloredTiles, int i, int j){
        TileColor color = coloredTiles.get(new Position(i, j));
        if(color == null){
            boardColor[i][j].setBackground(Color.GREEN);
        }
        else if(color == TileColor.BLACK){
            boardColor[i][j].setBackground(Color.BLACK);
        }
        else{
            boardColor[i][j].setBackground(Color.WHITE);
        }
    }


    private void updateBoard(List<Position> tilesFlipped, TileColor player){
        if(player == TileColor.GREEN)
            throw new IllegalArgumentException("a non green tile can never become green");

        Color c = (player == TileColor.BLACK ? Color.BLACK : Color.WHITE);
        for(Position p : tilesFlipped){
            boardColor[p.i][p.j].setBackground(c);
        }
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
                //the window is 700 x 700 pixels, so every square is 87.5 square pixels
                //I made it impossible to resize the window, so this calculation can't get messed up.
                int x = e.getX();
                int y = e.getY();
                Position chosen = new Position((int)(y / 87.5), (int)(x / 87.5));

                //now pass this information to the model, and use those results to update the board
                updateBoard(model.getPlayerMove(chosen), TileColor.WHITE);
                playersTurn = false;
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
