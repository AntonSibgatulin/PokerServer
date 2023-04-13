package com.AntonSibgatulin.exceptions;


/**
 * InvalidRankException represents a situation in which a given dealt
 * card's rank is not contained within a standard 52 card deck.
 * 
 * @author Zach Richardson
 */
public class InvalidRankException extends RuntimeException {

    /**
     * Creates a new instance of <code>InvalidRankException</code> without
     * detail message.
     */
    public InvalidRankException() {
    }

    /**
     * Constructs an instance of <code>InvalidRankException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidRankException(String msg) {
        super(msg);
    }
}
