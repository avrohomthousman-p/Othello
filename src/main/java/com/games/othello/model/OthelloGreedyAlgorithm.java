package com.games.othello.model;

import com.games.othello.TileColor;
import com.games.othello.viewController.Shifter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Implementation of the {@link OthelloModel OthelloModel interface} that has the computer
 * always make the move that captures the largest number of pieces in the short term.
 */
public class OthelloGreedyAlgorithm implements OthelloModel {

    //these are all the directions we need to check for captured pieces
    protected static final Shifter[] shifts = new Shifter[]{
            (position -> position.x++),                     //move south
            (position)->{position.x++; position.y++;},      //move south-east
            (position -> position.y++),                     //move east
            (position -> {position.x--; position.y++;}),    //move north-east
            (position -> position.x--),                     //move north
            (position -> {position.x--; position.y--;}),    //move north-west
            (position -> position.y--),                     //move west
            (position -> {position.x++; position.y--;})     //move south-west

    };
    protected LinkedList<Point> greenSpots;
    protected HashMap<Point, TileColor> nonGreenSpots;
    private int blackPieces, whitePieces;
    private boolean playersTurn;
    private boolean gameIsRunning = false;



    public OthelloGreedyAlgorithm(){
        this.greenSpots = new LinkedList<>();
        this.nonGreenSpots = new HashMap<>();
    }


    @Override
    public HashMap<Point, TileColor> startGame() {
        resetBoard();


        gameIsRunning = true;
        playersTurn = true;
        blackPieces = 2;
        whitePieces = 2;

        return new HashMap<Point, TileColor>(nonGreenSpots);//deep copy
    }


    /**
     * Clears the entire board and refills it with the starting pieces.
     */
    private void resetBoard(){
        nonGreenSpots.clear();
        greenSpots.clear();

        setStartingNonGreenSpots();
        setStartingGreenSpots();
    }


    /**
     * Sets up the board with all the non-green spots that should be present at the start of the game.
     */
    private void setStartingNonGreenSpots(){
        //fill hashmap with starting values
        nonGreenSpots.put(new Point(3, 3), TileColor.WHITE);
        nonGreenSpots.put(new Point(4, 4), TileColor.WHITE);
        nonGreenSpots.put(new Point(4, 3), TileColor.BLACK);
        nonGreenSpots.put(new Point(3, 4), TileColor.BLACK);
    }


    /**
     * Sets up the board with all the green spots that should be present at the start of the game.
     */
    private void setStartingGreenSpots(){
        //fill linkedList with starting values
        for (int i = 0; i < DIMENSIONS; i++){
            for(int j = 0; j < DIMENSIONS; j++){
                greenSpots.add(new Point(i, j));
            }
        }
        greenSpots.removeAll(nonGreenSpots.keySet());
    }


    @Override
    public List<Point> takePlayerTurn(Point playerMove) throws IllegalMoveException {
        if(!gameIsRunning){
            throw new IllegalMoveException("Turns cannot be taken after the game ended.");
        }
        if(!playersTurn){
            throw new IllegalMoveException("Its not your turn");
        }
        if(nonGreenSpots.containsKey(playerMove)){      //if the player tried to go in a non-empty spot
            throw new IllegalMoveException("players can only go in unoccupied spots");
        }

        List<Point> tilesFlipped = new ArrayList<>();
        tilesFlipped.add(playerMove);    //the actual place he went needs to become white
        tilesFlipped.addAll(getCapturedSpots(playerMove, true));

        //make sure the chosen move is legal
        if(tilesFlipped.size() == 1){//the only spot that was changed was the spot he chose.
            throw new IllegalMoveException("players can only go in spots that cause at least one piece to be captured.");
        }

        //update greenSpots and nonGreenSpots
        for(Point p : tilesFlipped){
            nonGreenSpots.put(p, TileColor.WHITE);
        }
        greenSpots.remove(playerMove);//that's the only one that started as green


        whitePieces += tilesFlipped.size();             //some pieces became white
        blackPieces -= (tilesFlipped.size() - 1);       //some pieces became no longer black
        playersTurn = false;



        if(thereAreNoLegalMovesLeft(false)){
            gameIsRunning = false;
        }


        return tilesFlipped;
    }


    @Override
    public List<Point> takeComputerTurn() {
        if(!gameIsRunning){
            throw new IllegalStateException("Computer turn was requested when the game is already over.");
        }
        if(playersTurn){
            throw new IllegalStateException("Computer turn was requested when it was the players turn to go.");
        }

        //find the position that captures the most spots
        int maxCaptured = 0;
        Point maxPosition = null;
        for(Point p : greenSpots){
            int count = getCaptureCount(p, TileColor.BLACK);
            if(count >= maxCaptured){
                maxCaptured = count;
                maxPosition = p;
            }
        }



        List<Point> tilesFlipped = getCapturedSpots(maxPosition, false);
        tilesFlipped.add(maxPosition);


        //update greenSpots and nonGreenSpots
        for(Point p : tilesFlipped){
            nonGreenSpots.put(p, TileColor.BLACK);
        }
        greenSpots.remove(maxPosition);


        playersTurn = true;
        blackPieces += tilesFlipped.size();         //pieces became black
        whitePieces -= (tilesFlipped.size() - 1);   //pieces that are no longer white


        //if the computers move left no more moves for the player
        if(thereAreNoLegalMovesLeft(true)){
            gameIsRunning = false;
        }


        return tilesFlipped;
    }


    /**
     * <p>Calculates how many spaces change color as a result of the specified move, not
     * including the move itself. The number of spots captured is calculated, but the actual
     * spots are not specified.</p>
     *
     * <p>This method looks at the changes that happen in all 8 directions away from the chosen spot.
     * South, South-West, West, North-West, North, North-East, East, and South-East. These 8
     * directions are found in the static array 'shifts.'</p>
     *
     * <p>This method is used to help the computer decide which move to take, and to check if there
     * are any moves left for any given player.</p>
     *
     * @param possibleNextMove the move whose outcome will be calculated.
     * @param playerDoingMove the player who is (considering) making the specified move.
     *
     * @return the number of spaces that will be captured (change color, not including the move itself),
     *          in all directions, if the specified move is taken.
     */
    protected int getCaptureCount(Point possibleNextMove, TileColor playerDoingMove){
        int total = 0;

        for(Shifter s : shifts){
            total += countSpots(possibleNextMove, s, playerDoingMove);
        }

        return total;
    }


    /**
     * Calculates how many spots will change color as a result of the specified move, but
     * only checks in ONE direction. This method is used to help the computer decide which move to take.
     *
     * @param chosenMove the move whose results will be calculated.
     * @param shiftToUse a function that defines which direction we are checking and applies that shift
     *                      to the specified Position object.
     * @param playerDoingMove the player who is (considering) going in the specified location.
     *
     * @return the number of spots that will be captured (not including the move itself).
     */
    protected int countSpots(Point chosenMove, Shifter shiftToUse, TileColor playerDoingMove){
        chosenMove = new Point(chosenMove); //make deep copy
        int peicesCaptured = 0;

        //while index is not out of bounds
        while(chosenMove.x >= 0 && chosenMove.x < DIMENSIONS && chosenMove.y >= 0 && chosenMove.y < DIMENSIONS){
            shiftToUse.shift(chosenMove);
            TileColor color = nonGreenSpots.get(chosenMove);
            if(color == null){//if that spot is not in the hashmap because its green
                return 0;
            }
            else if(color == playerDoingMove){//if you hit your own piece
                return peicesCaptured;
            }
            else{
                peicesCaptured++;
            }
        }
        return 0; //this happens when all the pieces are white till the end of the board
    }


    /**
     * <p>Finds all the spots that change color as a result of a players move, including the spot
     * itself that was chosen by the player. This method checks in all directions going away
     * from the chosen spot to see if any of the spots there are captured.</p>
     *
     * <p>This method differs from the {@link OthelloGreedyAlgorithm#getCaptureCount getCaptureCount method} in its
     * return type. getCaptureCount only counts the NUMBER of spots captured (to determan if
     * that move is worthwhile), this method collects and returns a List of the spots themselves.
     * This should be used only once the move has already been chosen. This separation avoids the
     * creation of 8 List objects (one for each direction) for every potential move.</p>
     *
     * @param chosenMove the move the player or computer has taken.
     * @param playersTurn a boolean that tells if the move being analyzed was done by the player
     *                    or the computer.
     * @return a list of all the spots that changed color, including the spot of the move itself.
     */
    protected List<Point> getCapturedSpots(Point chosenMove, boolean playersTurn){
        List<Point> spots = new ArrayList<>();
        for (Shifter s : shifts){
            spots.addAll(addSpotsToOutput(s, chosenMove, playersTurn));
        }

        return spots;
    }


    /**
     * Finds all the spots that change color as a result of the chosen move.
     * It checks all the spots ONLY in the direction specified by the Shifter object provided.
     *
     * @param shiftPosition a function that moves the position in a given direction.
     * @param chosenMove the move the player or computer has chosen to go.
     * @param playersTurn a boolean that says whose move is being evaluated. the player (WHITE)
     *                    or the computer (BLACK).
     * @return a list of spots that change color as a result of the specified move, only taking
     *                    into account changes in one direction (specified by the Shifter input).
     */
    protected List<Point> addSpotsToOutput(Shifter shiftPosition, Point chosenMove, boolean playersTurn){
        chosenMove = new Point(chosenMove);//deep copy
        List<Point> output = new ArrayList<>();
        TileColor self = (playersTurn ? TileColor.WHITE : TileColor.BLACK);

        //index out of bounds check
        while(chosenMove.x >= 0 && chosenMove.x < DIMENSIONS && chosenMove.y >= 0 && chosenMove.y < DIMENSIONS){
            shiftPosition.shift(chosenMove);
            TileColor color = nonGreenSpots.get(chosenMove);
            if(color == null){//if that spot is not in the hashmap because its green
                output.clear();//if you hit a green, that move is not legal
                return output;
            }
            else if(color == self){
                return output;
            }
            else{
                output.add(new Point(chosenMove));//deep copy
            }
        }
        output.clear();
        return output;
    }


    /**
     * Checks if the specified player still has at least one legal move, and the game is
     * not yet over.
     *
     * @param forPlayer the player we are checking.
     * @return true if there is at least one legal move for the specified player (and thus the
     *      game continues), or false otherwise.
     */
    private boolean thereAreNoLegalMovesLeft(boolean forPlayer){
        final TileColor player = (forPlayer ? TileColor.WHITE : TileColor.BLACK);

        for(Point location : greenSpots){
            if(getCaptureCount(location, player) > 0){
                return false;
            }
        }

        return true;
    }


    @Override
    public boolean isPlayersTurn() {
        return this.playersTurn;
    }


    @Override
    public boolean isGameOver() {
        return !gameIsRunning;
    }


    @Override
    public TileColor getWinner() {
        if(gameIsRunning){
            return null;
        }

        if(whitePieces > blackPieces){
            return TileColor.WHITE;
        }
        else if(blackPieces > whitePieces){
            return TileColor.BLACK;
        }
        else{
            return null;
        }
    }


    @Override
    public int getScoreOfBlackPlayer() {
        return blackPieces;
    }


    @Override
    public int getScoreOfWhitePlayer() {
        return whitePieces;
    }


}
