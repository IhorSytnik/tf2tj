package com.tf2tj.trade.stem.elements.trading;

import com.tf2tj.trade.enums.Currency;
import com.tf2tj.trade.models.Trade;
import com.tf2tj.trade.models.TradeDetails;
import com.tf2tj.trade.models.items.*;
import com.tf2tj.trade.stem.elements.SteamProcessor;
import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ihor Sytnik
 */
@Component
@Scope("prototype")
public class SteamTrade {

    @Autowired
    private HttpRequestBrowser steamRequestBrowser;
    @Autowired
    private SteamProcessor steamProcessor;
    @Setter
    private Trade trade;
    private PriceScrap keyBuyingPrice;
    private PriceScrap keySellingPrice;

    public boolean sell(ScrapOffer scrapOffer, Collection<Offer> offers) {
        return false;
    }

    private void makeForm(Collection myItems, Collection partnerItems) {
    }

    public void send(Collection myItems, Collection partnerItems) {/*calls #makeForm()*/}

    public void sell(PriceFull priceFull, Collection<Item> items) {/*prepares the offer and calls #send()*/}


    public void accept() {
    }
}
