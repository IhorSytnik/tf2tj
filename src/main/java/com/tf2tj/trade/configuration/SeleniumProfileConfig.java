package com.tf2tj.trade.configuration;

import com.tf2tj.trade.stem.requests.SeleniumBrowser;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Ihor Sytnik
 */
@Deprecated
@Configuration
@Profile("selenium")
public class SeleniumProfileConfig {

    @Autowired
    WebDriver seleniumDriver;

    @Value("${cookiesDirName}")
    private String cookiesDirName;

    @Bean
    public SeleniumBrowser steamGetBrowser(
            @Value("${steam.website}") String website,
            @Value("${steam.sleep}") int sleepMilliseconds,
            @Value("${steam.cookieFileName}") String cookieFileName) {
        return new SeleniumBrowser(website, seleniumDriver,
                cookiesDirName + "\\" + cookieFileName, sleepMilliseconds);
    }

    @Bean
    public SeleniumBrowser backpackBrowser(
            @Value("${backpack.website}") String website,
            @Value("${backpack.sleep}") int sleepMilliseconds,
            @Value("${backpack.cookieFileName}") String cookieFileName) {
        return new SeleniumBrowser(website, seleniumDriver,
                cookiesDirName + "\\" + cookieFileName, sleepMilliseconds);
    }

    @Bean
    public SeleniumBrowser scrapBrowser(
            @Value("${scrapTf.website}") String website,
            @Value("${scrapTf.sleep}") int sleepMilliseconds,
            @Value("${scrapTf.cookieFileName}") String cookieFileName) {
        return new SeleniumBrowser(website, seleniumDriver,
                cookiesDirName + "\\" + cookieFileName, sleepMilliseconds);
    }
}
