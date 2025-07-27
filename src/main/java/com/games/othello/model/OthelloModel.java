package com.games.othello.model;

import com.games.othello.TileColor;

import java.awt.Point;
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
    HashMap<Point, TileColor> startGame();



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
    List<Point> takePlayerTurn(Point playerMove) throws IllegalMoveException;



    /**
     * Looks at all the moves available for the computer and picks the best one.
     *
     * @return a list of positions that changed color as a result of the computers move. This
     * includes the actual spot the computer went in. Returns null if there are no moves left.
     */
    List<Point> takeComputerTurn();


    /**
     * Returns true if it is the players turn, and false if it's the computers turn.
     *
     * @return true if it is the players turn, or false otherwise.
     */
    boolean isPlayersTurn();


    /**
     * Returns true if the game is over, and false if it is still going.
     *
     * @return true if the game is over, and false otherwise.
     */
    boolean isGameOver();


    /**
     * Returns TileColor.BLACK if black won and TileColor.WHITE if white won. If the game
     * is not over or it's a tie, null is returned.
     *
     * @return the TileColor value that corresponds to the winning team, or null in a tie or
     *          if the game is still going.
     */
    TileColor getWinner();


    /**
     * Returns the number of black pieces on the board.
     *
     * @return the number of pieces on the board that are currently black.
     */
    int getScoreOfBlackPlayer();


    /**
     * Returns the number of white pieces on the board.
     *
     * @return the number of pieces on the board that are currently white.
     */
    int getScoreOfWhitePlayer();
}
