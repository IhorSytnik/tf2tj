package com.tf2tj.trade.models;

import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.people.Partner;
import lombok.*;

import java.util.Collection;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Trade {
    private String id;
    private Collection<Item> partnerItems;
    private Collection<Item> tUserItems;
    private Partner partner;
    private TradeDetails tradeDetails;

    public boolean verify(TradeDetails offerTradeDetails) {
        if (tradeDetails.tUserItems().equals(offerTradeDetails.tUserItems()) &&
                tradeDetails.partnerItems().equals(offerTradeDetails.partnerItems())) {
            /*If > 0 then the user pays, if < 0 then partner pays*/
            int tradeDiff = tradeDetails.getUserCurrencyDiffInMetal();
            int offerDiff = offerTradeDetails.getUserCurrencyDiffInMetal();

            return tradeDiff > 0 ? tradeDiff <= offerDiff : tradeDiff >= offerDiff;
        }
        return false;
    }
}
