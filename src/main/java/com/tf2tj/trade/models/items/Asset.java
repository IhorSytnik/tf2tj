package com.tf2tj.trade.models.items;

import lombok.*;

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
public class Asset {
    private String id;
    private String amount;
    private int pos;
}
