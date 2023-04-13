package com.AntonSibgatulin.exceptions;


/**
 * InvalidRankException represents a situation in which a given dealt
 * card's suit is not contained within a standard 52 card deck.
 * 
 * @author Zach Richardson
 */
public class InvalidSuitException extends RuntimeException {

    /**
     * Creates a new instance of <code>InvalidSuitException</code> without
     * detail message.
     */
    public InvalidSuitException() {
    }

    /**
     * Constructs an instance of <code>InvalidSuitException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidSuitException(String msg) {
        super(msg);
    }
}
