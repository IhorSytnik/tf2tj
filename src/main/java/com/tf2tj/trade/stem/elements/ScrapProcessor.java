package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.*;
import com.tf2tj.trade.models.people.TF2TUser;
import com.tf2tj.trade.stem.requests.GetBrowser;
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
    @Value("${scrapTf.pages.listingPage}")
    private String listingPage;

    @Autowired
    private PriceScrap scrapKeyPriceBuy;
    @Autowired
    private TF2TUser tUser;

    /***
     * Gets scrap.tf selling page html source.
     *
     * @return scrap.tf selling page html source.
     * @throws InterruptedException see {@link GetBrowser#getSource(String)}.
     */
    private Document getHTMLDocument() throws InterruptedException {
        String html = scrapBrowser.getSource("/buy/" + listingPage);
        return Jsoup.parse(html);
    }

    /**
     * Gets item information from items html tag.
     *
     * @param itemHTML items html tag with all attributes.
     * @return an {@link Item} object which has all the appropriate descriptions, but no <b>item.assets</b>.
     */
    private ItemDescription getItemInfo(Element itemHTML) {
        ItemDescription itemDescription = new ItemDescription();

        String name = itemHTML.attr("data-title");
        String[] dataGroupHash = itemHTML.attr("data-item-group-hash").split("-");
        Element span = Jsoup.parse(name)
                .select("span").first();
        Matcher matcherPainted = Pattern.compile("<br/>Painted (.+?)<br/>")
                .matcher(itemHTML.attr("data-content"));
        Matcher matcherFestivized = Pattern.compile("(?<='>)Festivized</span>")
                .matcher(itemHTML.attr("data-content"));
        Matcher matcherBgImg = Pattern.compile("background-image:url\\((.+)\\);")
                .matcher(itemHTML.attr("style"));

        boolean spanIsNull = span == null;

        itemDescription.setDefIndex(   itemHTML.attr("data-defindex"));
        itemDescription.setName(       spanIsNull ? name : span.text());
        itemDescription.setNameBase(   spanIsNull ? name : span.text().split(" ", 2)[1]);
        itemDescription.setCraftable(  Arrays.stream(
                                itemHTML.attr("class")
                                    .split(" "))
                                    .noneMatch("uncraft"::equals));
        itemDescription.setQuality(    Quality.getByString(dataGroupHash[3]));
        itemDescription.setPaint(      matcherPainted.find() ? Paint.getByString(matcherPainted.group(1)) : Paint.NOT_PAINTED);
        itemDescription.setFestivized( matcherFestivized.matches());

        itemDescription.setAustralium( Pattern.compile("-gold\\..*\\)").matcher(itemHTML.attr("style")).find()
                            &&
                            !spanIsNull
                            &&
                            span.text().contains("Australium"));

        itemDescription.setBackgroundImage(matcherBgImg.find() ? matcherBgImg.group(1) : "");

        return itemDescription;
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
        return botIds.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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
        scrapKeyPriceBuy.setScrap(PriceScrap.getPriceInRefsFromString(navBarMainMatcher.group(1)).getScrap());
    }

    /**
     * Gets all scrap.tf offers, filtered, to be priced less than {@link TF2TUser#getPriceLimit()} in metal.
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
                .filter(itemHTML -> Integer.parseInt(itemHTML.attr("data-item-value")) <=
                        tUser.getPriceLimit().apply(scrapKeyPriceBuy))
                .map(itemHTML -> {

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
