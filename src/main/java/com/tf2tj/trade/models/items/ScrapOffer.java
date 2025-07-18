package com.tf2tj.trade.models.items;

import lombok.*;

import java.util.Map;

/**
 * Describes a scrap.tf offer.
 *
 * @author Ihor Sytnik
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ScrapOffer {
    private ItemDescription itemDescription;
    private PriceFull priceFull;
    private String itemId;
    private int amount;

    /**
     * Map of <b>key</b>: <i>bot id</i>, <b>value</b>: <i>item amount</i>.
     */
    private Map<Integer, Integer> botIds;

    public void copy(ScrapOffer another) {
        this.itemDescription = another.itemDescription;
        this.priceFull = another.priceFull;
        this.itemId = another.itemId;
        this.amount = another.amount;
        this.botIds = another.botIds;
    }

    public boolean passes(ScrapOffer that) {
        if (this.equals(that)) return true;
        return itemDescription.equals(that.itemDescription) && priceFull.compareTo(that.priceFull) >= 0;
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//
//        return "ScrapOffer{" +
//                "itemDescription=" + itemDescription +
//                ", priceFull=" + priceFull +
//                ", itemId='" + itemId + '\'' +
//                ", amount=" + amount +
//                ", botIds=" + botIds +
//                '}';
//    }
}
