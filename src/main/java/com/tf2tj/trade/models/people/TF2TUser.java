package com.tf2tj.trade.models.people;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tf2tj.trade.helping.Operations;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.PriceScrap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Describes user of the application.<br>
 * Should be used in trading.
 *
 * @author Ihor Sytnik
 */
@Component
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class TF2TUser {
    private String id;
    private String customId = "";
    private Inventory inventory;
    private Inventory startInventory;
    @Value("${buyingPriceLimit}")
    private int buyingPriceLimit;

    public void setInventory(String inventory) throws JsonProcessingException {
        this.inventory = new Inventory(inventory);
    }

    public void setStartInventory(String startInventory) throws JsonProcessingException {
        this.startInventory = new Inventory(startInventory);
    }

    public PriceFull getCurrencyAmount() {
        return new PriceFull(inventory.getKeys().getAssets().size(),
                Operations.getScrapFromRefsRecsScrap(
                        inventory.getRefs().getAssets().size(),
                        inventory.getRecs().getAssets().size(),
                        inventory.getScraps().getAssets().size()
                )
        );
    }

    public Function<PriceScrap, Integer> getPriceLimit() {
        return (keyPrice) -> {
            PriceFull two = getCurrencyAmount();
            int one = two.getMetalPrice(keyPrice);
            return (int) (one *
                    ((float) buyingPriceLimit / 100f));
        };
    }

    public String getAppropriateIdUrl() {
        return customId.isBlank() ? "/profiles/" + id : "/id/" + customId;
    }
}
