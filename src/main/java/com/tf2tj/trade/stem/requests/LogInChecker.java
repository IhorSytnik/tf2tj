package com.tf2tj.trade.stem.requests;

import com.tf2tj.trade.exceptions.NotLoggedInException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Ihor Sytnik
 */
@Component
public class LogInChecker {
    @Autowired
    private GetBrowser scrapBrowser;
    @Autowired
    private GetBrowser backpackBrowser;
    @Autowired
    private GetBrowser steamGetBrowser;

    @Value("${steam.website}")
    private String steamWebsite;
    @Value("${scrapTf.website}")
    private String scrapTfWebsite;
    @Value("${backpack.website}")
    private String backpackWebsite;

    @Autowired
    private ApplicationContext context;

    private void checkScrapTf() throws InterruptedException, NotLoggedInException {
        HttpHeaders additionalHeaders = new HttpHeaders();
        additionalHeaders.add("Accept-Encoding", "utf-8");
        if (!Jsoup.parse(scrapBrowser.getSource("", additionalHeaders))
                .getElementsByAttributeValue("href", "/login")
                .isEmpty())
            throw new NotLoggedInException(scrapTfWebsite);
    }
    private void checkBackpackTf() throws InterruptedException, NotLoggedInException {
        HttpHeaders additionalHeaders = new HttpHeaders();
//        additionalHeaders.add("Accept-Encoding", "utf-8");
        if (!Jsoup.parse(backpackBrowser.getSource("", additionalHeaders))
                .getElementsByAttributeValue("href", "/login")
                .isEmpty())
            throw new NotLoggedInException(backpackWebsite);
    }
    private void checkSteam() throws InterruptedException, NotLoggedInException {
        HttpHeaders additionalHeaders = new HttpHeaders();
//        additionalHeaders.add("Accept-Encoding", "utf-8");
        if (!Jsoup.parse(steamGetBrowser.getSource("", additionalHeaders))
                .getElementsByAttributeValueMatching("href", "^https://steamcommunity.com/login/home.*")
                .isEmpty())
            throw new NotLoggedInException(steamWebsite);
    }

    public void checkLogIns() throws InterruptedException {
        try {
            checkScrapTf();
            checkBackpackTf();
            checkSteam();
        } catch (NotLoggedInException exception) {
            var starter = context.getBeansOfType(StarterSeleniumBrowser.class).values().stream().findFirst().get();
            starter.quit();
            context.getAutowireCapableBeanFactory().destroyBean(starter);
        }
    }
}
