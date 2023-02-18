package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.stem.requests.GetBrowser;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class for processing scrap.tf and getting scrap.tf offers.
 *
 * @author Ihor Sytnik
 */
@Component
public class ScrapProcessor {
    @Autowired
    private GetBrowser scrapBrowser;
    @Value("${scrapTf.buyingPage}")
    private String buyingPage;
    @Value("${buyingPriceLimit}")
    private int buyingPriceLimit;
    @Getter
    private int scrapKeyPriceBuy = 0;

    /***
     * Gets scrap.tf selling page html source.
     *
     * @return scrap.tf selling page html source.
     * @throws InterruptedException see {@link GetBrowser#getSource(String)}.
     */
    private Document getHTMLDocument() throws InterruptedException {
        String html = scrapBrowser.getSource("/buy/" + buyingPage);
        return Jsoup.parse(html);
    }

    /**
     * Gets item information from items html tag.
     *
     * @param itemHTML items html tag with all attributes.
     * @return an {@link Item} object which has all the appropriate descriptions, but no <b>item.assets</b>.
     */
    private Item getItemInfo(Element itemHTML) {
        Item item = new Item();

        String name = itemHTML.attr("data-title");
        String[] dataGroupHash = itemHTML.attr("data-item-group-hash").split("-");
        Element span = Jsoup.parse(name)
                .select("span").first();
        Matcher matcher = Pattern.compile("<br/>Painted (.+?)<br/>")
                .matcher(itemHTML.attr("data-content"));

        item.setDefIndex(   itemHTML.attr("data-defindex"));
        item.setName(       span == null ? name : span.text());
        item.setNameBase(   span == null ? name : span.text().split(" ", 2)[1]);
        item.setCraftable(  Arrays.stream(
                                itemHTML.attr("class")
                                    .split(" "))
                                    .noneMatch("uncraft"::equals));
        item.setQuality(    Quality.getByString(dataGroupHash[3]));
        item.setPaint(      matcher.find() ? Paint.getByString(matcher.group(1)) : Paint.NOT_PAINTED);

        return item;
    }

    /**
     * Gets bots' information from attribute <u>{@code data-bot(\\d+)-count}</u>, where <i>(\\d+)</i> is the bots id and the
     * attributes value is the number of these items the bot has.
     *
     * @param itemHTML items html tag with all attributes.
     * @return map of key: bot id, value: item amount.
     */
    private Map<Integer, Integer> getBotInfo(Element itemHTML) {
        Map<Integer, Integer> botIds = new HashMap<>();
        for (Attribute attr : itemHTML.attributes()) {
            if (!attr.getKey().startsWith("data-bot"))
                continue;
            Matcher matcher = Pattern.compile("data-bot(\\d+)-count")
                    .matcher(attr.getKey());
            if (!matcher.find())
                throw new NoSuchElementException("Couldn't find an element with \"data-bot(\\\\d+)-count\" pattern.");
            botIds.put(Integer.parseInt(matcher.group(1)), Integer.parseInt(attr.getValue()));
        }
        return botIds;
    }

    /**
     * Gets key buying price from <b>scrapHTML</b>, scrap.tf page source. The price is grabbed from the dropdown menu.<br>
     * Looks for a price string with <u>{@code Price: \d+\.\d{2}/(.+)}</u> pattern.
     *
     * @param scrapHTML scrap.tf page source.
     * @throws CouldNotFindPriceException if the price wasn't found.
     */
    private void parseAndSetKeyBuyingPrice(Document scrapHTML) throws CouldNotFindPriceException {
        Pattern keyPricePattern = Pattern.compile("Price: \\d+\\.\\d{2}/(.+)");
        Matcher navBarMainMatcher = keyPricePattern.matcher(
                Objects.requireNonNull(scrapHTML.getElementById("navbar-main")).text()
        );
        if (!navBarMainMatcher.find())
            throw new CouldNotFindPriceException("Couldn't find key price string in menu on scrap.tf.");
        scrapKeyPriceBuy = PriceFull.getPriceInRefsFromString(navBarMainMatcher.group(1)).getMetalPrice(0);
    }

    /***
     * Gets all scrap.tf offers filtered, to be priced less than {@link ScrapProcessor#buyingPriceLimit} in metal.
     *
     * @return the list of scrap.tf offers.
     * @throws InterruptedException see {@link #getHTMLDocument()}.
     * @throws CouldNotFindPriceException see {@link #parseAndSetKeyBuyingPrice(Document)}.
     */
    public Collection<ScrapOffer> getItems() throws InterruptedException, CouldNotFindPriceException {
        Document scrapHTML = getHTMLDocument();
        parseAndSetKeyBuyingPrice(scrapHTML);
        return scrapHTML
                .getElementById("buy-container")
                .getElementsByClass("item")
                .stream()
                .filter(itemHTML -> Integer.parseInt(itemHTML.attr("data-item-value")) <= buyingPriceLimit)
                .map(itemHTML -> {
//                    todo add setAustralium, could be fetched by checking if the photo has suffix "-gold" and the name
//                     contains "Australium"

//                    todo add setKillstreak, contained in
//                     class="item hoverable quality11 killstreak2   sheen fx4 expanded rarity app440 ivi"

                    String priceString = itemHTML.getElementsByClass("item-value-indicator")
                        .first()
                        .text();

                    return new ScrapOffer(
                        getItemInfo(itemHTML),
                        PriceFull.getPriceFromString(priceString),
                        itemHTML.attr("data-id"),
                        Integer.parseInt(itemHTML.attr("data-num-available")),
                        getBotInfo(itemHTML)
                    );
                })
                .collect(Collectors.toList());
    }
}
