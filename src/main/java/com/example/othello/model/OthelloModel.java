package com.example.othello.model;

import com.example.othello.TileColor;

import java.util.HashMap;
import java.util.List;



/**
 * A model for running a game of Othello.
 */
public interface OthelloModel {

    static final int DIMENSIONS = 8;

    /**
     * Does initial setup and ensures that the model has the correct board for the beginning of a
     * game.
     *
     * @return a HashMap containing all the positions on the game board that should start as non-green.
     *      All positions on the board that are not contained in the HashMap are assumed to be green.
     */
    public HashMap<Position, TileColor> startGame();



    /**
     * Computes the results of the specified players move.
     *
     * @param playerMove the position the player chose to go.
     *
     * @return a list of positions that changed color as a result of the players move. This
     *              includes the actual spot the player went in.
     *
     * @throws IllegalMoveException if the players chosen move is not a green spot or if that
     *              move does not cause any other spots to change color.
     */
    public List<Position> getPlayerMove(Position playerMove) throws IllegalMoveException;



    /**
     * Looks at all the moves available for the computer and picks the best one.
     *
     * @return a list of positions that changed color as a result of the computers move. This
     * includes the actual spot the computer went in. Returns null if there are no moves left.
     */
    public List<Position> getComputerMove();
}
