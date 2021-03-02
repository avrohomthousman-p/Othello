/**
 * This interface is basically a way to allow me to pass a lambda
 * to a function. I did not come up with this idea. I found it here
 * https://www.tutorialspoint.com/how-to-initialize-an-array-using-lambda-expression-in-java#
 * Just to be clear, I googled how to pass a lambda to a function.
 * This Othello algorithm is my own design.
 */
public interface Shifter {
    /**
     * This method defines a shift from one position in a matrix to another.
     * For example, moving right would mean incrementing Position.j
     * @param p the starting position.
     */
    void shift(Position p);
}
