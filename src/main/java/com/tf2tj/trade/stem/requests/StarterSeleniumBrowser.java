package com.tf2tj.trade.stem.requests;

import com.tf2tj.trade.exceptions.NotLoggedInException;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Starter browser to log into the websites and save their cookies to the {@link StarterSeleniumBrowser#cookiesDirName}
 * directory.
 *
 * @author Ihor Sytnik
 */
@Component
@Lazy
public class StarterSeleniumBrowser extends BaseSeleniumBrowser {
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

    @Autowired
    private Collection<GetBrowser> browsers;

    /**
     * Creates cookies directory if there is none and log ins user to the web-sites.
     *
     * @throws IOException see {@link #login()}.
     * @throws InterruptedException see {@link #login()}.
     */
    @PostConstruct
    public void init() throws IOException, InterruptedException, NotLoggedInException {
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
        driver.get(url);
    }

    /**
     * Saves cookies from the web-site entered before the call of this method to the file in the cookies' directory by
     * the name of <b>cookieFilePath</b>.
     *
     * @param cookieFilePath cookies' file name.
     * @throws IOException if an I/O error occurs while opening or creating the file.
     */
    private void saveCookies(String cookieFilePath)
            throws IOException {
        try (PrintWriter file = new PrintWriter(cookiesDirName + "\\" + cookieFilePath, StandardCharsets.UTF_8)) {
            Iterator<Cookie> cookies = driver.manage().getCookies().iterator();
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

    public void quit() {
        super.quit();
    }

    /**
     * Logs into all required web-sites (steamcommunity.com, backpack.tf, scrap.tf), updates all the cookies in the
     * browsers and checks log-ins.
     *
     * @throws IOException see {@link #saveCookies(String) }.
     * @throws InterruptedException see {@link #saveCookies(String) }.
     */
    public void login() throws IOException, InterruptedException {
        get(steamWebsite + "/login/home");
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(
                ExpectedConditions.elementToBeClickable(By.id("account_pulldown")));
        saveCookies(steamCookieFileName);

        get(backpackWebsite + "/login");
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(
                ExpectedConditions.elementToBeClickable(By.id("imageLogin")));
        driver.findElement(By.id("imageLogin")).click();
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(
                ExpectedConditions.elementToBeClickable(By.className("navbar-user-block")));
        saveCookies(bpCookieFileName);

        get(scrapTfWebsite + "/login");
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(
                ExpectedConditions.elementToBeClickable(By.id("imageLogin")));
        driver.findElement(By.id("imageLogin")).click();
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(
                ExpectedConditions.elementToBeClickable(By.className("nav-userinfo")));
        saveCookies(scrapCookieFileName);

        setCookies(steamWebsite, cookiesDirName + "\\" + steamCookieFileName);
        get(steamWebsite + "/market/eligibilitycheck/");
        saveCookies(steamCookieFileName);

        for (GetBrowser browser : browsers) {
            browser.updateCookies();
        }
    }
}
