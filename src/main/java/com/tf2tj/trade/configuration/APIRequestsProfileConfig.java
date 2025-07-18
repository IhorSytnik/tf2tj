package com.tf2tj.trade.configuration;

import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * @author Ihor Sytnik
 */
@Configuration
@Profile({"api"})
public class APIRequestsProfileConfig {

    @Value("${cookiesDirName}")
    private String cookiesDirName;

    @Bean
    public HttpRequestBrowser steamGetBrowser(
            @Value("${steam.website}") String website,
            @Value("${steam.sleep}") int sleepMilliseconds,
            @Value("${steam.cookieFileName}") String cookieFileName,
            @Value("#{${steam.headers}}") HttpHeaders steamHeaders)
            throws IOException {
        return new HttpRequestBrowser(website, true,
                cookiesDirName + "\\" + cookieFileName, steamHeaders, sleepMilliseconds);
    }

    @Bean
    public HttpRequestBrowser backpackBrowser(
            @Value("${backpack.website}") String website,
            @Value("${backpack.sleep}") int sleepMilliseconds,
            @Value("${backpack.cookieFileName}") String cookieFileName,
            @Value("#{${backpack.headers}}") HttpHeaders backpackHeaders)
            throws IOException {
        return new HttpRequestBrowser(website, true,
                cookiesDirName + "\\" + cookieFileName, backpackHeaders, sleepMilliseconds);
    }

    @Bean
    public HttpRequestBrowser scrapBrowser(
            @Value("${scrapTf.website}") String website,
            @Value("${scrapTf.sleep}") int sleepMilliseconds,
            @Value("${scrapTf.cookieFileName}") String cookieFileName,
            @Value("#{${scrapTf.headers}}") HttpHeaders scrapHeaders)
            throws IOException {
        return new HttpRequestBrowser(website, true,
                cookiesDirName + "\\" + cookieFileName, scrapHeaders, sleepMilliseconds);
    }
}
