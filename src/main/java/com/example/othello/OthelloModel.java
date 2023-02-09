package com.example.othello;

import java.util.HashMap;
import java.util.List;


//For convienece the computer will always be black
//and white will always go first
enum TileColor{GREEN, WHITE, BLACK}


public interface OthelloModel {

    static final int DIMENSIONS = 8;

    /**
     * This method sets up the model's data structures to contain the correct
     * positions for the beginning of the game. It also returns that data to
     * the view/controller so that it can display the same starting set up.
     * @return a HashMap with all the positions of the tiles that start as non-green.
     * The key is the position and the value is the color it should be. White or black.
     * All positions not contained in the HashMap are assumed to be green.
     */
    public HashMap<Position, TileColor> startGame();

    /**
     * This method computes the results of the players move
     * @param playerMove the position the player chose to go.
     * @return a list of positions that changed color as a result
     * of the players move. This includes the actual spot the player went in.
     * @throws IllegalMoveException if the players chosen move is not a green spot
     * or if that move does not cause any other spots to change color.
     */
    public List<Position> getPlayerMove(Position playerMove) throws IllegalMoveException;

    /**
     * This method looks at all the moves availible for the computer and picks the best one.
     * @return a list of positions that changed color as a result of the computers move. This
     * includes the the actual spot the computer went in. Returns null if there are no moves left.
     */
    public List<Position> getComputerMove();
}
