package com.AntonSibgatulin.exceptions;

/**
 * InvalidConsoleArgException represents an instance in which the user provided
 * invalid arguments at runtime in the command line.
 * 
 * @author Zach Richardson
 */
public class InvalidConsoleArgException extends Exception {

    /**
     * Creates a new instance of InvalidConsoleArgException without
     * detail message.
     */
    public InvalidConsoleArgException() {
    }

    /**
     * Constructs an instance of InvalidConsoleArgException with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidConsoleArgException(String msg) {
        super(msg);
    }
}
