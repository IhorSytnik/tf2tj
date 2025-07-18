package com.tf2tj.trade.exceptions;

/**
 * An exception that is thrown when the user isn't logged in onto a web-site.
 *
 * @author Ihor Sytnik
 */
public class NotLoggedInException extends Exception {
    public NotLoggedInException(String webSite) {
        super("You are not logged in onto " + webSite);
    }
}
