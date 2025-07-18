package com.tf2tj.trade.models.items;

import lombok.*;
import lombok.experimental.Delegate;

import java.util.LinkedList;
import java.util.List;

/**
 * Describes items for parsing and trading.
 *
 * @author Ihor Sytnik
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Delegate
    private ItemDescription itemDescription = new ItemDescription();

    @With
    private List<Asset> assets = new LinkedList<>();

    /**
     * Describes an asset, that is a particular item with its id.<br>
     * Should be used in trading.
     *
     * @author Ihor Sytnik
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Asset {
        private String id;
        private String amount;
        private int pos;
    }
}
