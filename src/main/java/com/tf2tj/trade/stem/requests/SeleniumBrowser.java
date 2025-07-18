package com.tf2tj.trade.stem.requests;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * A class for HTTP GET requests.<br>
 * Might be used instead of {@link HttpRequestBrowser}.
 *
 * @author Ihor Sytnik
 */
public class SeleniumBrowser extends BaseSeleniumBrowser implements GetBrowser {
    private String baseUrl;
    private String cookieFileName;
    private long sleep;

    public SeleniumBrowser(String baseUrl, WebDriver driver, String cookieFileName, long sleep) {
        this.baseUrl = baseUrl;
        this.driver = driver;
        this.cookieFileName = cookieFileName;
        this.sleep = sleep;
    }

    @PostConstruct
    public void init() throws IOException {
        updateCookies();
    }

    @Override
    public void updateCookies() throws IOException {
        setCookies(baseUrl, cookieFileName);
    }

//    /**
//     * Sets cookies for the website with name {@link #baseUrl}.<br>
//     * Reads cookies from file with the name of {@link #cookieFileName}.
//     *
//     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is
//     * read. see {@link Files#readString(Path, Charset)}.
//     */
//    private void setCookies() throws IOException {
//        driver.get(baseUrl);
//
//        String[] cookies = Files.readString(Path.of(cookieFileName), StandardCharsets.UTF_8).split(";\s");
//        Iterator<String> cIter = List.of(cookies).iterator();
//
//        Pattern pattern = Pattern.compile("^([^=]+)=(.+)$");
//        while (cIter.hasNext()) {
//            String c = cIter.next();
//            Matcher matcher = pattern.matcher(c);
//            if (matcher.find()) {
//                String name = matcher.group(1);
//                String value = matcher.group(2);
//                driver.manage().deleteCookieNamed(name);
//                driver.manage().addCookie(new Cookie(name, value));
//            }
//        }
//    }

    /**
     * Makes a GET request to <b>url</b>.
     *
     * @param url an address to make a request to.
     * @throws InterruptedException if any thread has interrupted the current thread. See {@link Thread#sleep(long)}.
     * @see WebDriver#get(String)
     */
    private void get(String url) throws InterruptedException {
        synchronized (this) {
            Thread.sleep(sleep);
            driver.get(baseUrl + url);
        }
    }

    /**
     * Gets web-page's source code.
     *
     * @param url URL of the page which source we will be getting.
     * @return web-page's source code by url.
     * @see WebDriver#getPageSource()
     */
    public String getSource(String url) throws InterruptedException {
        get(url);
        return driver.getPageSource();
    }

    @Override
    public String getSource(String url, HttpHeaders additionalHeaders) throws InterruptedException {
        return getSource(url);
    }
}
