package com.tf2tj.trade.stem;

import com.tf2tj.trade.customs.Mediator;
import com.tf2tj.trade.exceptions.CouldNotFindPartnerException;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.Offer;
import com.tf2tj.trade.models.items.PriceScrap;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.stem.elements.BackpackProcessor;
import com.tf2tj.trade.stem.elements.ScrapProcessor;
import com.tf2tj.trade.stem.elements.SteamProcessor;
import com.tf2tj.trade.stem.elements.trading.ScrapTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * A {@link Mediator} implementation to connect all the elements of the application.
 *
 * @author Ihor Sytnik
 */
@Component
public class MediatorImplementation implements Mediator {
    /*
     * scrap - all at once
     * bp - one after another and concurrently should call steam trading.
     * steam trading should work as a queue.
     * */
    @Autowired
    private ScrapProcessor scrapProcessor;
    @Autowired
    private BackpackProcessor backpackProcessor;
    @Autowired
    private SteamProcessor steamProcessor;
    @Autowired
    private ScrapTrade scrapTrade;
    @Autowired
    private PriceScrap backpackKeyPriceSell;
    @Autowired
    private PriceScrap scrapKeyPriceBuy;
    @Autowired
    private Collection<ScrapOffer> sellOffers;
    @Value("${backpack.website}")
    private String backpackWebsite;
    @Value("${safePillow}")
    private int safePillow;

    /**
     * Called on the start. Starts scrapping scrap.tf for sell offers and looking for profitable listings on
     * backpack.tf to be sold to.
     *
     * @throws InterruptedException see {@link ScrapProcessor#getItems()}.
     * @throws CouldNotFindPartnerException see {@link #gotSellOffers(Collection)}.
     * @throws CouldNotFindPriceException see {@link ScrapProcessor#getItems()}.
     */
    @Override
    public void start() throws Exception {
        steamProcessor.setUser();

        sellOffers.clear();
        sellOffers.addAll(scrapProcessor.getItems());
        System.out.println("SCRAP.TF PARSING IS FINISHED");

        gotSellOffers(sellOffers);
    }

    /**
     * Called when scrap.tf offers are acquired.
     *
     * @param scrapOffers scrap.tf offers.
     * @throws CouldNotFindPartnerException see {@link BackpackProcessor#getBuyOffers(ScrapOffer)}.
     * @throws InterruptedException see {@link BackpackProcessor#getBuyOffers(ScrapOffer)},
     * {@link BackpackProcessor#parseAndSetKeyPrices()}.
     */
    @Override
    public void gotSellOffers(Collection<ScrapOffer> scrapOffers)
            throws Exception {
        backpackProcessor.parseAndSetKeyPrices();
        for (ScrapOffer scrapOffer : scrapOffers) {
            System.out.print("\r" + scrapOffer);
            Collection<Offer> list = backpackProcessor.getBuyOffers(scrapOffer);
            if (!list.isEmpty()) {
                gotBuyOffers(list, scrapOffer);
//                if (list.size() >= safePillow) {
//                    scrapTrade.buy(scrapOffer);
//                }
            }
        }
        backpackFinished();
    }

    /**
     * Called when the profitable backpack.tf offer is acquired.
     *
     * @param offers backpack.tf offers for the <b>scrapOffer</b>.
     * @param scrapOffer a {@link ScrapOffer} object, containing item's information.
     */
    @Override
    public void gotBuyOffers(Collection<Offer> offers, ScrapOffer scrapOffer) {
        System.out.println(scrapOffer.getItemDescription());
        System.out.println("NAME: " + scrapOffer.getItemDescription().getName());
        System.out.println("NAME BASE: " + scrapOffer.getItemDescription().getNameBase());
        System.out.println("PRICE: " + scrapOffer.getPriceFull());
        System.out.println("AMOUNT: " + scrapOffer.getAmount());
        System.out.println("BOTS: " + scrapOffer.getBotIds());
        System.out.println("OFFER LINK:");
        System.out.printf("%s/classifieds?" +
                        "item=%s&" +
                        "quality=%d&" +
                        "tradable=1&" +
                        "craftable=%d&" +
                        "australium=%d&" +
                        "killstreak_tier=0&" +
                        "offers=1&" +
                        "paint=%s&" +
                        "page=%d%n",
                backpackWebsite,
                URLEncoder.encode(scrapOffer.getItemDescription().getNameBase(), StandardCharsets.UTF_8),
                scrapOffer.getItemDescription().getQuality().getNumber(),
                scrapOffer.getItemDescription().isCraftable() ? 1 : -1,
                scrapOffer.getItemDescription().isAustralium() ? 1 : -1,
                scrapOffer.getItemDescription().getPaint().getBpCode(),
                1);
        System.out.println("OFFERS:");
        offers.forEach(offer -> {
            System.out.println("\tPRICE: " + offer.getPriceFull());
            System.out.println("\tPRICE DIFF: " +
                    (new PriceScrap(offer.getPriceFull().getMetalPrice(backpackKeyPriceSell) -
                    scrapOffer.getPriceFull().getMetalPrice(scrapKeyPriceBuy))));
            System.out.println("\t LINK: " + offer.getOfferor().getTradeOfferLink());
        });
        System.out.println();
    }

    /**
     * Called when backpack.tf offers processing is finished.
     */
    @Override
    public void backpackFinished() {
        System.out.println("BACKPACK.TF PARSING IS FINISHED");
    }

    @Override
    public void tradeSuccess(Offer offer) {
    }

    @Override
    public void tradeFail(Offer offer) {

    }
}
