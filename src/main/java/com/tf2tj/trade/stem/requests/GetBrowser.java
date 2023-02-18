package com.tf2tj.trade.stem.requests;

/**
 * An interface that limits browser to have only GET requests.
 *
 * @author Ihor Sytnik
 */
public interface GetBrowser {

    /**
     * Gets web-page's source code.
     *
     * @param uri URI of the page which source we will be getting.
     * @return web-page's source code by url.
     */
    String getSource(String uri) throws InterruptedException;
}
