package com.tf2tj.trade.stem;

import com.tf2tj.trade.models.items.Offer;
import com.tf2tj.trade.models.items.ScrapOffer;

import java.util.Collection;

/**
 * A mediator interface to rule them all.
 *
 * @author Ihor Sytnik
 */
public interface Mediator {
    void start() throws Exception;
    void gotScrapOffers(Collection<ScrapOffer> scrapOffers) throws Exception;
    void gotBackpackOffer(Collection<Offer> offers, ScrapOffer scrapOffer);
    void backpackFinished();
    void tradeSuccess(Offer offer);
    void tradeFail(Offer offer);
}
