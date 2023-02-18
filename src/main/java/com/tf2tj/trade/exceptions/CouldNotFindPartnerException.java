package com.tf2tj.trade.exceptions;

/**
 * An exception that is thrown when a partner couldn't be found (couldn't find their steam id, trade id or token).
 *
 * @author Ihor Sytnik
 */
public class CouldNotFindPartnerException extends Exception {
    public CouldNotFindPartnerException(String message) {
        super(message);
    }
}
