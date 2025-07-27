package com.games.othello.viewController;
import java.awt.*;

/**
 * Represents the action of moving from one square on the game board to the adjacent square.
 */
@FunctionalInterface
public interface Shifter {
    /**
     * Defines a shift from one position in a matrix to another.
     * For example, moving right would mean incrementing Point.y
     * 
     * @param p the starting position.
     */
    void shift(Point p);
}
