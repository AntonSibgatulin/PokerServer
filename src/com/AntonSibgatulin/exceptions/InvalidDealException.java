package com.AntonSibgatulin.exceptions;


/**
 * InvalidDealException represents a situation in which the dealer of 
 * a Texas Hold 'Em game errors in his distribution of cards.
 * 
 * @author Zach Richardson
 */
public class InvalidDealException extends RuntimeException {

    /**
     * Creates a new instance of InvalidDealException without
     * detail message.
     */
    public InvalidDealException() {
    }

    /**
     * Constructs an instance of InvalidDealException with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidDealException(String msg) {
        super(msg);
    }
}
