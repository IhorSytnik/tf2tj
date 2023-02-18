package com.tf2tj.trade.models.people;

import lombok.*;

/**
 * Describes steam trade partner.
 *
 * @author Ihor Sytnik
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Partner {
    private String steamId;
    private String tradeId;
    private String tradeToken;

    /**
     * Builds and returns trade offer link.
     *
     * @return trade offer link.
     */
    public String getTradeOfferLink() {
        return "https://steamcommunity.com/tradeoffer/new/?partner=%s&token=%s"
                .formatted(tradeId, tradeToken);
    }
}
