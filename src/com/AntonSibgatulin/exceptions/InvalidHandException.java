package com.AntonSibgatulin.exceptions;


/**
 * InvalidHandException represents a situation in which a player's hand contains
 * in invalid number of cards or a hand that could not be made by a normal
 * deck of cards.
 * 
 * @author Zach Richardson
 */
public class InvalidHandException extends RuntimeException {

    /**
     * Creates a new instance of InvalidHandException without
     * detail message.
     */
    public InvalidHandException() {
    }

    /**
     * Constructs an instance of InvalidHandException with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidHandException(String msg) {
        super(msg);
    }
}
