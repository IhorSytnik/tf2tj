package com.tf2tj.trade.models.items;

import com.tf2tj.trade.models.people.Partner;
import lombok.*;

/**
 * Describes a trade offer.<br>
 * Contains an offeror and price.
 *
 * @author Ihor Sytnik
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    private PriceFull priceFull;
    private Partner offeror;
}
