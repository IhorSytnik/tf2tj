package com.tf2tj.trade.models.people;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.models.items.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
class InventoryTest {
    // TODO: 23.01.2023 testing with inventories, that contain keys 

    /*
    currencies: 1x Scrap Metal
     */
    private static String inventory;

    /*
    currencies: 3x Scrap Metal, 2x Reclaimed Metal, 3x Refined Metal
     */
    private static String inventoryUpdated;
    private static Collection<Item> itemsWithoutCurrenciesExpected = new LinkedList<>();

    static Item makeItem(String defIndex, String classId, String instanceId, String name, Quality quality, Paint paint,
                         List<Item.Asset> assets) {
        Item item = new Item();
        item.setDefIndex(defIndex);
        item.setClassId(classId);
        item.setInstanceId(instanceId);
        item.setName(name);
        item.setQuality(quality);
        item.setPaint(paint);
        item.setAssets(assets);
        return item;
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        inventory = new String(
                Objects.requireNonNull(InventoryTest.class.getResourceAsStream("/inventory.json")).readAllBytes(),
                StandardCharsets.UTF_8);
        inventoryUpdated = new String(
                Objects.requireNonNull(InventoryTest.class.getResourceAsStream("/inventoryUpdated.json")).readAllBytes(),
                StandardCharsets.UTF_8);
        inventoryUpdated = new String(
                Objects.requireNonNull(InventoryJsonDeserializerTest.class.getResourceAsStream("/inventoryUpdated.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        itemsWithoutCurrenciesExpected.add(makeItem("5802", "780650846", "0",
                "Mann Co. Supply Munition Series #91", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498894", "1", 1))));
        itemsWithoutCurrenciesExpected.add(makeItem("5781", "780630690", "0",
                "Mann Co. Supply Munition Series #90", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498921", "1", 3))));
        itemsWithoutCurrenciesExpected.add(makeItem("673", "22989188", "0",
                "Noise Maker - Winter Holiday", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498939", "1", 2))));
    }

    @Test
    void updateInventory_inventoryGetsUpdated_true() throws JsonProcessingException {
        Inventory given = new Inventory(inventory);

        assertTrue(given.updateInventory(inventoryUpdated));
    }

    @Test
    void updateInventory_inventoryDoNotGetUpdated_false() throws JsonProcessingException {
        Inventory given = new Inventory(inventory);

        assertFalse(given.updateInventory(inventory));
    }

    @Test
    void getItemsWOCurrencies_containsExactlyTheseItems_equals() throws JsonProcessingException {
        Inventory given = new Inventory(inventoryUpdated);

        assertEquals(itemsWithoutCurrenciesExpected, given.getItemsWOCurrencies());
    }
}