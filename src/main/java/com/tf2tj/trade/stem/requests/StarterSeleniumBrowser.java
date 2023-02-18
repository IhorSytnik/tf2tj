package com.tf2tj.trade.stem.requests;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Starter browser to log into the websites and save their cookies to the {@link StarterSeleniumBrowser#cookiesDirName}
 * directory.
 *
 * @author Ihor Sytnik
 */
@Component
@Profile("starter")
public class StarterSeleniumBrowser {
    @Autowired
    private WebDriver seleniumDriver;
    @Value("${cookiesDirName}")
    private String cookiesDirName;
    @Value("${default.sleep}")
    private long sleep;

    @Value("${backpack.website}")
    private String backpackWebsite;
    @Value("${scrapTf.website}")
    private String scrapTfWebsite;
    @Value("${steam.website}")
    private String steamWebsite;

    @Value("${backpack.cookieFileName}")
    private String bpCookieFileName;
    @Value("${scrapTf.cookieFileName}")
    private String scrapCookieFileName;
    @Value("${steam.cookieFileName}")
    private String steamCookieFileName;

    /**
     * Creates cookies directory if there is none and log ins user to the web-sites.
     *
     * @throws IOException see {@link #login()}.
     * @throws InterruptedException see {@link #login()}.
     */
    @PostConstruct
    public void init() throws IOException, InterruptedException {
        new File(cookiesDirName).mkdir();
        login();
    }

    /**
     * Makes a GET request to <b>url</b>.
     *
     * @param url an address to make a request to.
     * @throws InterruptedException if any thread has interrupted the current thread. See {@link Thread#sleep(long)}.
     * @see WebDriver#get(String)
     */
    private void get(String url) throws InterruptedException {
        Thread.sleep(sleep);
        seleniumDriver.get(url);
    }

    /**
     * Saves cookies from the web-site <b>siteURL</b> to the file in the cookies' directory by the name of
     * <b>cookieFilePath</b>.
     *
     * @param siteURL the web-site we are taking cookies from.
     * @param cookieFilePath cookies' file name.
     * @throws IOException if an I/O error occurs while opening or creating the file.
     * @throws InterruptedException see {@link #get(String)}.
     */
    private void saveCookies(String siteURL, String cookieFilePath) throws IOException, InterruptedException {
        get(siteURL);
        System.in.read();
        try (PrintWriter file = new PrintWriter(cookiesDirName + "\\" + cookieFilePath, StandardCharsets.UTF_8)) {
            Iterator<Cookie> cookies = seleniumDriver.manage().getCookies().iterator();
            Map<String, Object> cookie;
            if (cookies.hasNext()) {
                cookie = cookies.next().toJson();
                file.print(cookie.get("name") + "=" + cookie.get("value"));
            }
            while (cookies.hasNext()) {
                cookie = cookies.next().toJson();
                file.print("; ");
                file.print(cookie.get("name") + "=" + cookie.get("value"));
            }
        }
    }

    /**
     * Logs into all required web-sites (steamcommunity.com, backpack.tf, scrap.tf).
     *
     * @throws IOException see {@link #saveCookies(String, String)}.
     * @throws InterruptedException see {@link #saveCookies(String, String)}.
     */
    public void login() throws IOException, InterruptedException {
        saveCookies(steamWebsite + "/login/home", steamCookieFileName);
        saveCookies(backpackWebsite + "/login", bpCookieFileName);
        saveCookies(scrapTfWebsite + "/login", scrapCookieFileName);
    }
}
