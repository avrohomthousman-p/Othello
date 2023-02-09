package com.example.othello;

/**
 * Represents the action of moving from one square on the game board to the adjacent square.
 */
@FunctionalInterface
public interface Shifter {
    /**
     * Defines a shift from one position in a matrix to another.
     * For example, moving right would mean incrementing Position.j
     * 
     * @param p the starting position.
     */
    void shift(Position p);
}
