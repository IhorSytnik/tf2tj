package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Currency;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.exceptions.CouldNotFindPartnerException;
import com.tf2tj.trade.models.items.*;
import com.tf2tj.trade.models.people.Partner;
import com.tf2tj.trade.stem.requests.GetBrowser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
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
    @Autowired
    private PriceScrap backpackKeyPriceBuy;

    /**
     * Key prices for the keys you are selling.
     */
    @Autowired
    private PriceScrap backpackKeyPriceSell;

    @Autowired
    private PriceScrap scrapKeyPriceBuy;

    /**
     * Requests for <b>itemDescription</b>'s listing page with page number <b>page</b> on backpack.tf and returns html source code.
     *
     * @param itemDescription an item to request listings for on backpack.tf.
     * @param page listings' page number.
     * @return backpack.tf classifieds page html source code.
     * @throws InterruptedException see {@link GetBrowser#getSource(String)}.
     */
    private Document getHTMLDocument(ItemDescription itemDescription, int page) throws InterruptedException {
        String uri = String.format("/classifieds?" +
                        "item=%s&" +
                        "quality=%d&" +
                        "tradable=1&" +
                        "craftable=%d&" +
                        "australium=%d&" +
                        "killstreak_tier=0&" +
                        "offers=1&" +
                        "paint=%s&" +
                        "page=%d",
                itemDescription.getNameBase(),
                itemDescription.getQuality().getNumber(),
                itemDescription.isCraftable() ? 1 : -1,
                itemDescription.isAustralium() ? 1 : -1,
                itemDescription.getPaint().getBpCode(),
                page);
        String html = backpackBrowser.getSource(uri);
        return Jsoup.parse(html);
    }

    /**
     * Gets all listings, both, buy and sell.
     *
     * @param itemDescription an item to request listings for on backpack.tf.
     * @param page listings' page number.
     * @return a collection of all listings from the <b>document</b> page.
     */
    private Collection<Element> getListingItems(ItemDescription itemDescription, int page) throws InterruptedException {
        return getHTMLDocument(itemDescription, page).getElementsByClass("listing");
    }

    /**
     * Gets item details from a <b>listing</b>.<br>
     * Grabs an html tag with {@code listing-item} class.
     *
     * @param listing an {@link Element} object to get item details from.
     * @return an html tag, containing item details.
     */
    private Element getListingItemChildFirst(Element listing) {
        return listing.getElementsByClass("item")
                .first();
    }

    private PriceFull getPrice(Element listing) {
        return PriceFull.getPriceFromString(getListingItemChildFirst(listing)
                .attr("data-listing_price"));
    }

    /**
     * Checks if it is a <i>buy</i> listing.
     *
     * @return a {@link Predicate} to filter by.
     */
    private Predicate<Element> isBuyListing() {
        return listing -> !listing
                .getElementsByAttributeValueMatching("data-listing_intent", Pattern.compile("buy"))
                .isEmpty();
    }

    /**
     * Checks if it is a <i>sell</i> listing.
     *
     * @return a {@link Predicate} to filter by.
     */
    private Predicate<Element> isSellListing() {
        return listing -> !listing
                .getElementsByAttributeValueMatching("data-listing_intent", Pattern.compile("sell"))
                .isEmpty();
    }

    private Predicate<Element> filterPriceForBuying
            (PriceFull sellingPrice, PriceScrap KeyPriceSell, PriceScrap KeyPriceBuy) {
        return listing -> {
            PriceFull buyingPrice = getPrice(listing);
            return sellingPrice.getMetalPrice(KeyPriceSell) > buyingPrice.getMetalPrice(KeyPriceBuy)
                    /*Checks to be SURE this will be profitable, whether user has keys or not*/
                    &&
                    sellingPrice.compareTo(buyingPrice) > 0;
        };
    }

    private Predicate<Element> filterPriceForSelling
            (PriceFull buyingPrice, PriceScrap KeyPriceSell, PriceScrap KeyPriceBuy) {
        return listing -> {
            PriceFull sellingPrice = getPrice(listing);
            return sellingPrice.getMetalPrice(KeyPriceSell) > buyingPrice.getMetalPrice(KeyPriceBuy)
                    /*Checks to be SURE this will be profitable, whether user has keys or not*/
                    &&
                    sellingPrice.compareTo(buyingPrice) > 0;
        };
    }

    /**
     * Checks if bp item price is more than the scrap item price.
     *
     * @param buyingPrice price that the items' prices are compared to.
     * @return a {@link Predicate} to filter by.
     */
    private Predicate<Element> filterPriceBPScrap(PriceFull buyingPrice) {
        return filterPriceForSelling(buyingPrice, backpackKeyPriceSell, scrapKeyPriceBuy);
    }

    /**
     * Checks if listing is posted by <i>not a user agent</i>, with <i>an elevated quality</i> and <i>with an
     * effect</i>. Plus, does an additional filtering for paint.
     *
     * @param itemDescription item, which listings being filtered.
     * @return a {@link Predicate} to filter by.
     */
    private Predicate<Element> additionalFilters(ItemDescription itemDescription) {
        return listing -> {
            Element listingItemChildFirst = getListingItemChildFirst(listing);
            return !listing.getElementsByClass("fa-flash").isEmpty()
                    &&
                    !listingItemChildFirst.hasAttr("data-quality_elevated")
                    &&
                    !listingItemChildFirst.hasAttr("data-spell_1")
                    &&
                    !listingItemChildFirst.hasAttr("data-effect_id")
                    &&
                    itemDescription.isFestivized() ==
                            listingItemChildFirst.attr("data-festivized").equals("1")
                    &&
                    itemDescription.getPaint().equals(Paint.NOT_PAINTED) ^
                            listingItemChildFirst.hasAttr("data-paint_name");
        };
    }

    /**
     * A function to aggregate the filtering to one place.
     *
     * @param listings {@link Element}s to be filtered.
     * @param predicate {@link Predicate} to filter by.
     * @return filtered {@link Collection} of {@link Element}s.
     */
    private Collection<Element> filterWithPredicates(Collection<Element> listings, Predicate<Element> predicate) {
        return listings.stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Gets all listings that are profitable for the item from <b>scrapOffer</b>.
     *
     * @param scrapOffer a scrap.tf offer.
     * @return all listings that are profitable.
     * @throws InterruptedException see {@link #getHTMLDocument(ItemDescription, int)}.
     */
    private Collection<Element> getAllBuyListingPages(ScrapOffer scrapOffer) throws InterruptedException {
        Collection<Element> tempCollection = filterWithPredicates(
                getListingItems(scrapOffer.getItemDescription(), 1),
                isBuyListing()
                        .and(
                filterPriceBPScrap(scrapOffer.getPriceFull()))
        );
        Collection<Element> listingsCollection = new LinkedList<>();

        for (int page = 2; !tempCollection.isEmpty(); page++) {
            listingsCollection.addAll(filterWithPredicates(
                    tempCollection,
                    additionalFilters(scrapOffer.getItemDescription()))
            );
            tempCollection = filterWithPredicates(
                    getListingItems(scrapOffer.getItemDescription(), page),
                    isBuyListing()
                            .and(
                    filterPriceBPScrap(scrapOffer.getPriceFull()))
            );
        }
        return listingsCollection;
    }

    /***
     * Gets offeror details.
     *
     * @param element an {@link Element} object to get offeror's details from.
     * @return a {@link Partner} object, containing offeror's details.
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

    private Offer processListing(Element listing) throws CouldNotFindPartnerException {
        Partner offeror = getOfferor(listing);
//                todo remove (temporary blacklist)
//            if (Stream.of(
//                    "134552353"
//            ).anyMatch(id -> id.equals(offeror.getTradeId()))) continue;


        return new Offer(
                getPrice(listing),
                offeror
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
            offers.add(processListing(listing));
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
     * @throws InterruptedException see {@link #getHTMLDocument(ItemDescription, int)}.
     */
    public void parseAndSetKeyPrices() throws InterruptedException {
        ItemDescription key = Currency.KEY.getItemDescription();

        int limit = 3;
        List<Element> buyListings = new ArrayList<>();
        List<Element> sellListings = new ArrayList<>();

        Collection<Element> tempListings;
        Collection<Element> tempBuyListings = new LinkedList<>();
        Collection<Element> tempSellListings = new LinkedList<>();
        int i = 1;
        do {
            tempListings = getListingItems(key, i++);
            if (buyListings.size() < limit) {
                tempBuyListings = filterWithPredicates(
                        tempListings,
                        isBuyListing()
                );
                buyListings.addAll(
                        filterWithPredicates(
                                tempBuyListings,
                                additionalFilters(key)
                        )
                );
            }
            if (sellListings.size() < limit) {
                tempSellListings = filterWithPredicates(
                        tempListings,
                        isSellListing()
                );
                sellListings.addAll(
                        filterWithPredicates(
                                tempSellListings,
                                additionalFilters(key)
                        )
                );
            }
        } while ((buyListings.size() < limit || sellListings.size() < limit) &&
                (!tempBuyListings.isEmpty() || !tempSellListings.isEmpty()));

        backpackKeyPriceSell.setScrap(PriceScrap
                .getPriceInRefsFromString(
                        getListingItemChildFirst(buyListings.stream().limit(limit).toList()
                                .get(limit - 1))
                                .attr("data-listing_price")).getScrap());

        backpackKeyPriceBuy.setScrap(PriceScrap
                .getPriceInRefsFromString(
                        getListingItemChildFirst(sellListings.stream().limit(limit).toList()
                                .get(limit - 1))
                                .attr("data-listing_price")).getScrap());
    }

    /**
     * Processes scrap.tf offers ({@link ScrapOffer}) and get profitable backpack.tf listings ({@link Offer}).
     *
     * @param scrapOffer scrap.tf offers to be processed.
     * @return profitable backpack.tf listings ({@link Offer}).
     * @throws InterruptedException see {@link #getAllBuyListingPages(ScrapOffer)}.
     * @throws CouldNotFindPartnerException see {@link #processListings(Collection)}.
     */
    public Collection<Offer> getBuyOffers(ScrapOffer scrapOffer)
            throws InterruptedException, CouldNotFindPartnerException {
        return processListings(getAllBuyListingPages(scrapOffer));
    }

    public Map<Offer, List<Offer>> getProfitableByItem(ItemDescription itemDescription) throws InterruptedException, CouldNotFindPartnerException {
        Map<Offer, List<Offer>> offerMap = new HashMap<>();

        ListingList buyOfferList = new ListingList(
                isBuyListing(),
                itemDescription,
                true);

        Offer buyOffer = buyOfferList.removeFirstUpdate();
        ListingList sellOfferList = new ListingList(
                isSellListing()
                        .and(
                filterPriceForBuying(buyOffer.getPriceFull(), backpackKeyPriceSell, backpackKeyPriceBuy)),
                itemDescription,
                false);

        while (!sellOfferList.isEmpty()) {
            List<Offer> listForBuyOffer = new LinkedList<>(sellOfferList.listings);
            offerMap.put(buyOffer, listForBuyOffer);

            buyOffer = buyOfferList.removeFirstUpdate();

            while (!sellOfferList.isEmpty() &&
                    sellOfferList.getLast().getPriceFull().compareTo(buyOffer.getPriceFull()) > 0) {
                sellOfferList.removeLast();
            }
        }

        return offerMap;
    }

    private class ListingList {
        private int page;
        private Predicate<Element> filter;
        private Predicate<Element> additionalFilters;
        private LinkedList<Offer> listings;
        private ItemDescription itemDescription;
        private boolean lazy;

        public ListingList(Predicate<Element> filter, ItemDescription itemDescription, boolean lazy)
                throws InterruptedException, CouldNotFindPartnerException {
            this.filter = filter;
            this.itemDescription = itemDescription;
            this.additionalFilters = additionalFilters(itemDescription);
            this.lazy = lazy;
            initialize();
        }

        public void initialize() throws InterruptedException, CouldNotFindPartnerException {
            page = 1;
            listings = new LinkedList<>();
            addMore();
        }

        private boolean addMore() throws InterruptedException, CouldNotFindPartnerException {
            if (page < 1)
                return false;
            do {
                Collection<Element> listingItems = getListingItems(itemDescription, page++);
                listingItems = filterWithPredicates(
                        listingItems,
                        filter
                );
                if (listingItems.isEmpty())
                    break;

                listingItems = filterWithPredicates(
                        listingItems,
                        additionalFilters
                );

                if (!listingItems.isEmpty())
                    listings.addAll(processListings(listingItems));

            } while (listings.isEmpty() || !lazy);

            return listings.isEmpty();
        }

        public Offer removeFirstUpdate() throws InterruptedException, NoSuchElementException, CouldNotFindPartnerException {
            if (listings.isEmpty()) {
                addMore();
            }
            return removeFirst();
        }

        public Offer removeFirst() throws NoSuchElementException {
            return listings.removeFirst();
        }

        public Offer removeLast() throws NoSuchElementException {
            return listings.removeLast();
        }

        public Offer getFirst() {
            return listings.getFirst();
        }

        public Offer getLast() {
            return listings.getLast();
        }

        public boolean isEmpty() {
            return listings.isEmpty();
        }

        public int size() {
            return listings.size();
        }
    }
}
