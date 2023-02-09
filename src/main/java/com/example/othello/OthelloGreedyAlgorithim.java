package com.example.othello;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Implementation of the {@link OthelloModel OthelloModel interface} that has the computer
 * always make the move that captures the largest number of pieces in the short term.
 */
public class OthelloGreedyAlgorithim implements OthelloModel {

    //these are all the directions we need to check for captured pieces
    protected static final Shifter[] shifts = new Shifter[]{
            (position -> position.i++), //move south
            (position)->{position.i++; position.j++;}, //move south east
            (position -> position.j++), //move east
            (position -> {position.i--; position.j++;}), //move north east
            (position -> position.i--), //move north
            (position -> {position.i--; position.j--;}), //move north west
            (position -> position.j--), //move west
            (position -> {position.i++; position.j--;}) //move south west

    };
    protected LinkedList<Position> greenSpots;
    protected HashMap<Position, TileColor> nonGreenSpots;

    public OthelloGreedyAlgorithim(){
        this.greenSpots = new LinkedList<>();
        this.nonGreenSpots = new HashMap<>();
    }

    @Override
    public HashMap<Position, TileColor> startGame() {
        nonGreenSpots.clear();
        greenSpots.clear();

        //fill hashmap with starting values
        nonGreenSpots.put(new Position(3, 3), TileColor.WHITE);
        nonGreenSpots.put(new Position(4, 4), TileColor.WHITE);
        nonGreenSpots.put(new Position(4, 3), TileColor.BLACK);
        nonGreenSpots.put(new Position(3, 4), TileColor.BLACK);

        //fill linkedList with starting values
        for (int i = 0; i < DIMENSIONS; i++){
            for(int j = 0; j < DIMENSIONS; j++){
                greenSpots.add(new Position(i, j));
            }
        }
        greenSpots.removeAll(nonGreenSpots.keySet());

        return new HashMap<Position, TileColor>(nonGreenSpots);//deep copy
    }

    @Override
    public List<Position> getPlayerMove(Position playerMove) throws IllegalMoveException {
        if(nonGreenSpots.containsKey(playerMove)){//if the player tried to go in a non-empty spot
            throw new IllegalMoveException("players can only go in unoccupied spots");
        }
        List<Position> tilesFlipped = new ArrayList<>();
        tilesFlipped.add(playerMove);//the actual place he went needs to become white
        tilesFlipped.addAll(getCapturedSpots(playerMove, true));
        //make sure the chosen move is legal
        if(tilesFlipped.size() == 1){//the only spot that was changed was the spot he chose.
            throw new IllegalMoveException("players can only go in spots that cause at least one peice to be captured.");
        }
        //update greenSpots and nonGreenSpots
        for(Position p : tilesFlipped){
            nonGreenSpots.put(p, TileColor.WHITE);
        }
        greenSpots.remove(playerMove);//thats the only one that started as green
        return tilesFlipped;
    }

    @Override
    public List<Position> getComputerMove() {
        //find the position that captures the most spots
        int maxCaptured = 0;
        Position maxPosition = null;
        for(Position p : greenSpots){
            int count = getCaptureCount(p);
            if(count >= maxCaptured){
                maxCaptured = count;
                maxPosition = p;
            }
        }


        //if there are no moves left (the board is full)
        if(maxCaptured <= 0){
            JOptionPane.showMessageDialog(null, "Game Over.");
            return null;
        }



        List<Position> tilesFlipped = getCapturedSpots(maxPosition, false);
        tilesFlipped.add(maxPosition);


        //update greenSpots and nonGreenSpots
        for(Position p : tilesFlipped){
            nonGreenSpots.put(p, TileColor.BLACK);
        }
        greenSpots.remove(maxPosition);
        return tilesFlipped;
    }



    /**
     * Calculates how many spaces change color as a result of the specified move, not
     * including the move itself. The NUMBER of spots captured is calculated, but the actual
     * spots are not specified.
     *
     * This method looks at the changes that happen in all 8
     * directions away from the chosen spot. South, South-West, West, North-West, North,
     * North-East, East, and South-East. These 8 directions are found in the static array 'shifts'
     *
     * This method is used to help the computer decide which move to take.
     *
     * @param possibleNextMove the move whose outcome will be calculated.
     * @return the number of spaces that will be captured (change color, not including the move itself),
     *          in all directions, if the specified move is taken.
     */
    protected int getCaptureCount(Position possibleNextMove){
        int total = 0;

        for(Shifter s : shifts){
            total += countSpots(possibleNextMove, s);
        }

        return total;
    }


    /**
     * This method calculates how many spots will change color as a result of the specified move, but
     * it only checks in ONE direction. This method is used to help the computer decide which move to take.
     * @param chosenMove the move whose results will be specified.
     * @param shiftToUse a function that defines which direction we are checking. It applies that shift
     *                      to the com.example.othello.Position object.
     * @return the number of spots that will be captured (i.e. not including the move itself).
     */
    protected int countSpots(Position chosenMove, Shifter shiftToUse){
        chosenMove = new Position(chosenMove); //make deep copy
        int peicesCaptured = 0;
        //while index is not out of bounds
        while(chosenMove.i >= 0 && chosenMove.i < DIMENSIONS && chosenMove.j >= 0 && chosenMove.j < DIMENSIONS){
            shiftToUse.shift(chosenMove);
            TileColor color = nonGreenSpots.get(chosenMove);
            if(color == null){//if that spot is not in the hashmap because its green
                return 0;
            }
            else if(color == TileColor.BLACK){//if you hit your own piece
                return peicesCaptured;
            }
            else{
                peicesCaptured++;
            }
        }
        return 0; //this happens when all the pieces are white till the end of the board
    }

    /**
     * Finds all the spots that change color as a result of a players move, including the spot
     * itself that was chosen by the player. This method checks in all directions going away
     * from the chosen spot to see if any of the spots there are captured.
     *
     * This method differs from the {@link this#getCaptureCount getCaptureCount method} in its
     * return type. getCaptureCount only counts the NUMBER of spots captured (to determan if
     * that move is worthwhile), this method collects and returns a List of the spots themselves.
     * This should be used only once the move has already been chosen. This separation avoids the
     * creation of 8 List objects (one for each direction) for every potential move.
     *
     * @param chosenMove the move the player or computer has taken.
     * @param playersTurn a boolean that tells if the move being analyzed was done by the player
     *                    or the computer.
     * @return a list of all the spots that changed color, including the spot of the move itself.
     */
    protected List<Position> getCapturedSpots(Position chosenMove, boolean playersTurn){
        List<Position> spots = new ArrayList<>();
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
    protected List<Position> addSpotsToOutput(Shifter shiftPosition, Position chosenMove, boolean playersTurn){
        chosenMove = new Position(chosenMove);//deep copy
        List<Position> output = new ArrayList<>();
        TileColor self = (playersTurn ? TileColor.WHITE : TileColor.BLACK);

        //index out of bounds check
        while(chosenMove.i >= 0 && chosenMove.i < DIMENSIONS && chosenMove.j >= 0 && chosenMove.j < DIMENSIONS){
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
                output.add(new Position(chosenMove));//deep copy
            }
        }
        output.clear();
        return output;
    }


}
