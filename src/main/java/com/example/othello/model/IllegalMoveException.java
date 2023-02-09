package com.example.othello.model;

public class IllegalMoveException extends RuntimeException{
    public IllegalMoveException(){
        super();
    }

    public IllegalMoveException(String message){
        super(message);
    }

    public IllegalMoveException(String message, Throwable cause){
        super(message, cause);
    }
}
