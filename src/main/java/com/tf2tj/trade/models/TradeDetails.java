package com.tf2tj.trade.models;

import com.tf2tj.trade.models.items.ItemDescription;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.PriceScrap;

import java.util.Map;

/**
 * @author Ihor Sytnik
 */
public record TradeDetails(PriceFull tUserCurrency, Map<ItemDescription, Integer> tUserItems,
                           PriceScrap keyBuyingPrice,
                           PriceFull partnerCurrency, Map<ItemDescription, Integer> partnerItems,
                           PriceScrap keySellingPrice) {

    public int getUserCurrencyDiffInMetal() {
        return tUserCurrency.getMetalPrice(keyBuyingPrice) -
                partnerCurrency.getMetalPrice(keySellingPrice);
    }
}
