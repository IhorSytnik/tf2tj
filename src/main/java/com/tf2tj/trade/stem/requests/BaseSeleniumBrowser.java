package com.tf2tj.trade.stem.requests;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
public abstract class BaseSeleniumBrowser {
    @Autowired
    protected WebDriver driver;

    /**
     * Sets cookies for the website with name <b>baseUrl</b>.<br>
     * Reads cookies from file with the name of <b>cookieFileName</b>.
     *
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is
     * read. see {@link Files#readString(Path, Charset)}.
     */
    protected void setCookies(String baseUrl, String cookieFileName) throws IOException {
        driver.get(baseUrl);

        String[] cookies = Files.readString(Path.of(cookieFileName), StandardCharsets.UTF_8).split(";\s");
        Iterator<String> cIter = List.of(cookies).iterator();

        Pattern pattern = Pattern.compile("^([^=]+)=(.+)$");
        while (cIter.hasNext()) {
            String c = cIter.next();
            Matcher matcher = pattern.matcher(c);
            if (matcher.find()) {
                String name = matcher.group(1);
                String value = matcher.group(2);
                driver.manage().deleteCookieNamed(name);
                driver.manage().addCookie(new Cookie(name, value));
            }
        }
    }

    protected void quit() {
        driver.quit();
    }
}
