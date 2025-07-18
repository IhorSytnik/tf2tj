package com.tf2tj.trade.exceptions;

/**
 * @author Ihor Sytnik
 */
public class OutdatedDataException extends RuntimeException {
    public OutdatedDataException(String message) {
        super(message);
    }
}
