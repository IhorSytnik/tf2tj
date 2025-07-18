package com.tf2tj.trade.configuration;

import com.tf2tj.trade.enums.Killstreak;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.models.items.ItemDescription;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.PriceScrap;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.models.people.TF2TUser;
import com.tf2tj.trade.stem.requests.ChromeDriverManager;
import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//    @Profile({"selenium", "starter"})
    @Lazy
    public WebDriver seleniumDriver(@Value("${default.sleep}") int sleepMilliseconds)
            throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver;
        for (int i = 0; i < 5; i++) {
            try {
                driver = new ChromeDriver(options);
//                ((RemoteWebDriver) driver).getCapabilities().getBrowserVersion();
                return driver;
            } catch (Exception e) {
                Matcher versionMatcher = Pattern.compile("\nCurrent browser version is ([^\\s]+) with").matcher(e.getMessage());
                if (versionMatcher.find())
                    ChromeDriverManager.updateDriver(versionMatcher.group(1), true, sleepMilliseconds);
            }
        }
        return null;
    }

    @Bean
    public TF2TUser tUser() {
        return new TF2TUser();
    }

    /**
     * Key prices for the keys you are buying.
     */
    @Bean
    public PriceScrap backpackKeyPriceBuy() {
        return new PriceScrap();
    }

    /**
     * Key prices for the keys you are selling.
     */
    @Bean
    public PriceScrap backpackKeyPriceSell() {
        return new PriceScrap();
    }

    @Bean
    public PriceScrap scrapKeyPriceBuy() {
        return new PriceScrap();
    }

    @Bean
    public HttpRequestBrowser steamRequestBrowser(
            @Value("${steam.website}") String website,
            @Value("${steam.sleep}") int sleepMilliseconds,
            @Value("${steam.cookieFileName}") String cookieFileName,
            @Value("#{${steam.headers}}") HttpHeaders steamHeaders)
            throws IOException {
        return new HttpRequestBrowser(website, true,
                cookiesDirName + "\\" + cookieFileName, steamHeaders, sleepMilliseconds);
    }

    @Bean
    public Collection<ScrapOffer> sellOffers() {
        var sellOffers = new LinkedList<ScrapOffer>();
        sellOffers.add(
                new ScrapOffer(
                        new ItemDescription("1", "1", "1", "1", "1", "https://steamcdn-a.akamaihd.net/apps/440/icons/short2014_chemists_pride.b6ee6e6d86bd26b65e6df331e58c6d21b4b7ffbd.png", true, true, true, new Killstreak(Killstreak.Tier.NONE, Killstreak.Sheen.AGONIZING_EMERALD, Killstreak.Killstreaker.INCINERATOR), Quality.UNIQUE, Paint.NOT_PAINTED),
                        new PriceFull(),
                        "1",
                        1,
                        Map.of()
                ));
        sellOffers.add(
                new ScrapOffer(
                        new ItemDescription("2", "2", "2", "2", "2", "https://steamcdn-a.akamaihd.net/apps/440/icons/dec2014_hunter_beard.a3c8e7ddc37a88663be5aae9012c1fe5bbdd7d5c.png", false, false, false, new Killstreak(Killstreak.Tier.NONE, Killstreak.Sheen.AGONIZING_EMERALD, Killstreak.Killstreaker.INCINERATOR), Quality.STRANGE, Paint.RADIGAN_CONAGHER_BROWN),
                        new PriceFull(),
                        "2",
                        2,
                        Map.of()
                )
        );

        return sellOffers;
    }
}
