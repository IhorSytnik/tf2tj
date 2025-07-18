package com.tf2tj.trade.models.people;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@NoArgsConstructor
public class Partner {
    private String steamId;
    private String tradeId;
    private String tradeToken;
    private Inventory inventory;

    public Partner(String steamId, String tradeId, String tradeToken) {
        this.steamId = steamId;
        this.tradeId = tradeId;
        this.tradeToken = tradeToken;
    }

    public void setInventory(String inventory) throws JsonProcessingException {
        this.inventory = new Inventory(inventory);
    }

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
