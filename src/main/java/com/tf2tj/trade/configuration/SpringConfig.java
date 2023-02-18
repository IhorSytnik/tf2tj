package com.tf2tj.trade.configuration;

import com.tf2tj.trade.models.people.TF2TUser;
import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * @author Ihor Sytnik
 */
@Configuration
@ComponentScan("com.tf2tj.trade")
@PropertySource("classpath:project.properties")
@Import({SeleniumProfileConfig.class, RequestProfileConfig.class})
public class SpringConfig {

    @Value("${cookiesDirName}")
    private String cookiesDirName;

    @Bean(destroyMethod = "quit")
    @Profile({"selenium", "starter"})
    public WebDriver seleniumDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        return new ChromeDriver();
    }

    @Bean
    public TF2TUser tUser() {
        return new TF2TUser();
    }

    @Bean
    public HttpRequestBrowser steamBrowser(
            @Value("${steam.website}") String website,
            @Value("${steam.sleep}") int sleepMilliseconds,
            @Value("${steam.cookieFileName}") String cookieFileName,
            @Value("#{${steam.headers}}") HttpHeaders steamHeaders)
            throws IOException {
        return new HttpRequestBrowser(website, true,
                cookiesDirName + "\\" + cookieFileName, steamHeaders, sleepMilliseconds);
    }
}
