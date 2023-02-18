package com.tf2tj.trade.stem;

import com.tf2tj.trade.exceptions.CouldNotFindPartnerException;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.Offer;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.stem.elements.BackpackProcessor;
import com.tf2tj.trade.stem.elements.ScrapProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * Called on the start. Starts scrapping scrap.tf for sell offers and looking for profitable listings on
     * backpack.tf to be sold to.
     *
     * @throws InterruptedException see {@link ScrapProcessor#getItems()}.
     * @throws CouldNotFindPartnerException see {@link #gotScrapOffers(Collection)}.
     * @throws CouldNotFindPriceException see {@link ScrapProcessor#getItems()}.
     */
    @Override
    public void start() throws InterruptedException, CouldNotFindPartnerException, CouldNotFindPriceException {
        Collection<ScrapOffer> scrapOffers = scrapProcessor.getItems();
        System.out.println("SCRAP.TF PARSING IS FINISHED");

        gotScrapOffers(scrapOffers);
    }

    /**
     * Called scrap.tf offers are acquired.
     *
     * @param scrapOffers scrap.tf offers.
     * @throws CouldNotFindPartnerException see {@link BackpackProcessor#getBuyOffers(ScrapOffer)}.
     * @throws InterruptedException see {@link BackpackProcessor#getBuyOffers(ScrapOffer)},
     * {@link BackpackProcessor#parseAndSetKeyPrices()}.
     */
    @Override
    public void gotScrapOffers(Collection<ScrapOffer> scrapOffers)
            throws CouldNotFindPartnerException, InterruptedException {
        backpackProcessor.setScrapKeyPriceBuy(scrapProcessor.getScrapKeyPriceBuy());
        backpackProcessor.parseAndSetKeyPrices();
        for (ScrapOffer scrapOffer : scrapOffers) {
            Collection<Offer> list = backpackProcessor.getBuyOffers(scrapOffer);
            if (!list.isEmpty()) {
                gotBackpackOffer(list, scrapOffer);
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
    public void gotBackpackOffer(Collection<Offer> offers, ScrapOffer scrapOffer) {
        System.out.println(scrapOffer);
        System.out.println(offers);
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
