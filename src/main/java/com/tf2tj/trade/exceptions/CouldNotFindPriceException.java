package com.tf2tj.trade.exceptions;

/**
 * An exception that is thrown when a price couldn't be found.
 *
 * @author Ihor Sytnik
 */
public class CouldNotFindPriceException extends Exception {
    public CouldNotFindPriceException(String message) {
        super(message);
    }
}
