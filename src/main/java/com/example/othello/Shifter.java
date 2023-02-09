package com.example.othello;

/**
 * This interface is basically a way to allow me to pass a lambda
 * to a function.
 */
public interface Shifter {
    /**
     * This method defines a shift from one position in a matrix to another.
     * For example, moving right would mean incrementing com.example.othello.Position.j
     * @param p the starting position.
     */
    void shift(Position p);
}
