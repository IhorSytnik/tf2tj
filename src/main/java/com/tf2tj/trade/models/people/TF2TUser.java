package com.tf2tj.trade.models.people;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Describes user of the application.<br>
 * Should be used in trading.
 *
 * @author Ihor Sytnik
 */
@Component
@Setter
public class TF2TUser {
    private String id;
    private String customId;
    private Inventory inventory;
    private Inventory startInventory;

    public void setInventory(String inventory) throws JsonProcessingException {
        this.inventory = new Inventory(inventory);
    }

    public void setStartInventory(String startInventory) throws JsonProcessingException {
        this.startInventory = new Inventory(startInventory);
    }
}
