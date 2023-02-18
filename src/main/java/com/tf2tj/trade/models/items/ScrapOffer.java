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
    private Item item;
    private PriceFull priceFull;
    private String itemId;
    private int amount;

    /**
     * Map of key: bot id, value: item amount.
     */
    private Map<Integer, Integer> botIds;
}
