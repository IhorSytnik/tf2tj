package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPartnerException;
import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.items.Offer;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.models.people.Partner;
import com.tf2tj.trade.stem.requests.GetBrowser;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for processing backpack.tf pages.<br>
 * Use {@link BackpackProcessor#parseAndSetKeyPrices()} to set {@link BackpackProcessor#backpackKeyPriceSell} and
 * {@link BackpackProcessor#backpackKeyPriceBuy}.<br>
 * Use {@link BackpackProcessor#getBuyOffers(ScrapOffer)} to process scrap.tf offers ({@link ScrapOffer}) and get
 * profitable backpack.tf listings ({@link Offer}).
 *
 * @author Ihor Sytnik
 */
@Component
public class BackpackProcessor {
    @Autowired
    private GetBrowser backpackBrowser;
    /**
     * Key prices for the keys you are buying.
     */
    private int backpackKeyPriceBuy = 0;
    /**
     * Key prices for the keys you are selling.
     */
    private int backpackKeyPriceSell = 0;
    @Setter
    private int scrapKeyPriceBuy;

    /**
     * Requests for <b>item</b>'s listing page with page number <b>page</b> on backpack.tf and returns html source code.
     *
     * @param item an item to request listings for on backpack.tf.
     * @param page listings' page number.
     * @return backpack.tf classifieds page html source code.
     * @throws InterruptedException see {@link GetBrowser#getSource(String)}.
     */
    private Document getHTMLDocument(Item item, int page) throws InterruptedException {
        String uri = String.format("/classifieds?" +
                        "item=%s&" +
                        "quality=%d&" +
                        "tradable=1&" +
                        "craftable=%d&" +
                        "australium=-1&" +
                        "killstreak_tier=0&" +
                        "offers=1&" +
                        "paint=%s&" +
                        "page=%d",
                item.getNameBase(),
                item.getQuality().getQualityNumeric(),
                item.isCraftable() ? 1 : -1,
                item.getPaint().getBpCode(),
                page);
        String html = backpackBrowser.getSource(uri);
        return Jsoup.parse(html);
    }

    /**
     * Gets item details from a <b>listing</b>.<br>
     * Grabs an html tag with {@code listing-item} class.
     *
     * @param listing an {@link Element} object to get item details from.
     * @return an html tag, containing item details.
     */
    private Element getListingItemChildFirst(Element listing) {
        return listing.getElementsByClass("listing-item")
                .first()
                .children()
                .first();
    }

    /**
     * Gets buy listings from backpack.tf classifieds page, <b>document</b>.
     *
     * @param document backpack.tf classifieds page html source code.
     * @return collection of tags, containing buy listings.
     */
    private Collection<Element> getBuyListings(Document document) {
        return document
                .getElementsByAttributeValueMatching("data-listing_intent", Pattern.compile("buy"))
                .stream()
                .map(element -> element.parent().parent())
                .toList();
    }

    /**
     * Gets sell listings from backpack.tf classifieds page, <b>document</b>.
     *
     * @param document backpack.tf classifieds page html source code.
     * @return collection of tags, containing sell listings.
     */
    private Collection<Element> getSellListings(Document document) {
        return document
                .getElementsByAttributeValueMatching("data-listing_intent", Pattern.compile("sell"))
                .stream()
                .map(element -> element.parent().parent())
                .toList();
    }

    /**
     * Filters <b>listings</b> so that it contained only automated listings with no additional item attributes.
     *
     * @param listings the collection of item listing tags.
     * @return filtered collection of tags.
     */
    private Collection<Element> getFlashListingsAndFilter(Collection<Element> listings) {
        return listings
                .stream()
                .filter(listing -> !listing
                        .getElementsByClass("fa-flash").isEmpty()
                        &&
                        !getListingItemChildFirst(listing)
                                .hasAttr("data-quality_elevated")
                        &&
                        !getListingItemChildFirst(listing)
                                .hasAttr("data-effect_id")
                )
                .toList();
    }

    /**
     * Filters items by price.
     *
     * @param listings listings to be filtered.
     * @param itemPrice price that the items' prices are compared to.
     * @return items from <b>listings</b> which prices are greater then <p>itemPrice</p>.
     */
    private Collection<Element> getBuyListingsFilterPrice(Collection<Element> listings, int itemPrice) {
        return getFlashListingsAndFilter(listings)
                .stream()
                .filter(listing ->
                        PriceFull.getPriceFromString(getListingItemChildFirst(listing)
                                .attr("data-listing_price")).getMetalPrice(backpackKeyPriceSell) >
                                itemPrice
                )
                .toList();
    }

    /**
     * Gets all listings that are profitable for the item from <b>scrapOffer</b>.
     *
     * @param scrapOffer a scrap.tf offer.
     * @return all listings that are profitable.
     * @throws InterruptedException see {@link #getHTMLDocument(Item, int)}.
     */
    private Collection<Element> getAllListingPages(ScrapOffer scrapOffer) throws InterruptedException {
        Document htmlPage = getHTMLDocument(scrapOffer.getItem(), 1);
        Collection<Element> tempCollection = getBuyListingsFilterPrice(getBuyListings(htmlPage),
                scrapOffer.getPriceFull().getMetalPrice(scrapKeyPriceBuy));
        Collection<Element> listingsCollection = new LinkedList<>();
        for (int page = 2; !tempCollection.isEmpty(); page++) {
            listingsCollection.addAll(tempCollection);
            htmlPage = getHTMLDocument(scrapOffer.getItem(), page);
            tempCollection = getBuyListingsFilterPrice(getBuyListings(htmlPage),
                    scrapOffer.getPriceFull().getMetalPrice(scrapKeyPriceBuy));
        }
        return listingsCollection;
    }

    /***
     * Gets offeror details.
     *
     * @param element an {@link Element} object to get offerors details from.
     * @return a {@link Partner} object, containing offerors details.
     * @throws CouldNotFindPartnerException if partner couldn't be found (couldn't find their trade id or token).
     */
    private Partner getOfferor(Element element) throws CouldNotFindPartnerException {
        element = element.getElementsByClass("user-link")
                .first();
        String dataOffersParams = element.attr("data-offers-params");
        Matcher matcherTradeId = Pattern.compile("partner=(\\d+)").matcher(dataOffersParams);
        Matcher matcherTradeToken = Pattern.compile("token=(.{8})").matcher(dataOffersParams);
        if (!matcherTradeId.find()) {
            throw new CouldNotFindPartnerException("Couldn't find offeror's trade id.");
        }
        if (!matcherTradeToken.find()) {
            throw new CouldNotFindPartnerException("Couldn't find offeror's trade token.");
        }
        return new Partner(
                element
                        .attr("data-id"),
                matcherTradeId.group(1),
                matcherTradeToken.group(1)
        );
    }

    /**
     * Processes backpack.tf <b>listings</b>.
     *
     * @param listings backpack.tf listings to be processed.
     * @return profitable backpack.tf offers.
     * @throws CouldNotFindPartnerException see {@link #getOfferor(Element)}.
     */
    private Collection<Offer> processListings(Collection<Element> listings) throws CouldNotFindPartnerException {

        Collection<Offer> offers = new LinkedList<>();

        for (Element listing : listings) {
            Partner offeror = getOfferor(listing);

            listing = getListingItemChildFirst(listing);

            offers.add(new Offer(
                    PriceFull.getPriceFromString(listing.attr("data-listing_price")),
                    offeror
            ));
        }

        return offers;
    }

    /**
     * Sets backpack key sell and buy prices.<br>
     * Requests backpack Mann Co. Supply Crate Key listing pages, gets a few sell and buy listings (from the tops) and
     * sets {@link BackpackProcessor#backpackKeyPriceBuy} to be the last price of the sell listings and
     * {@link BackpackProcessor#backpackKeyPriceSell} to be the last price of the buy listings. The price is written
     * in metal.
     *
     * @throws InterruptedException see {@link #getHTMLDocument(Item, int)}.
     */
    public void parseAndSetKeyPrices() throws InterruptedException {
        Item key = new Item();
        key.setNameBase("Mann Co. Supply Crate Key");
        key.setQuality(Quality.UNIQUE);
        key.setCraftable(true);
        key.setPaint(Paint.NOT_PAINTED);

        int limit = 3;
        List<Element> buyListings = new ArrayList<>();
        List<Element> sellListings = new ArrayList<>();

        Collection<Element> tempBuyListings = new LinkedList<>();
        Collection<Element> tempSellListings = new LinkedList<>();
        int i = 1;
        do {
            Document html = getHTMLDocument(key, i++);
            if (buyListings.size() < limit) {
                tempBuyListings = getBuyListings(html);
                buyListings.addAll(getFlashListingsAndFilter(tempBuyListings));
            }
            if (sellListings.size() < limit) {
                tempSellListings = getSellListings(html);
                sellListings.addAll(getFlashListingsAndFilter(tempSellListings));
            }
        } while ((buyListings.size() < limit || sellListings.size() < limit) &&
                (!tempBuyListings.isEmpty() || !tempSellListings.isEmpty()));

        backpackKeyPriceSell = PriceFull
                .getPriceFromString(
                        getListingItemChildFirst(buyListings.stream().limit(limit).toList()
                                .get(limit - 1))
                                .attr("data-listing_price"))
                .getMetalPrice(0);

        backpackKeyPriceBuy = PriceFull
                .getPriceFromString(
                        getListingItemChildFirst(sellListings.stream().limit(limit).toList()
                                .get(limit - 1))
                                .attr("data-listing_price"))
                .getMetalPrice(0);
    }

    /**
     * Processes scrap.tf offers ({@link ScrapOffer}) and get profitable backpack.tf listings ({@link Offer}).
     *
     * @param scrapOffer scrap.tf offers to be processed.
     * @return profitable backpack.tf listings ({@link Offer}).
     * @throws InterruptedException see {@link #getAllListingPages(ScrapOffer)}.
     * @throws CouldNotFindPartnerException see {@link #processListings(Collection)}.
     */
    public Collection<Offer> getBuyOffers(ScrapOffer scrapOffer)
            throws InterruptedException, CouldNotFindPartnerException {
        return processListings(getAllListingPages(scrapOffer));
    }
}
