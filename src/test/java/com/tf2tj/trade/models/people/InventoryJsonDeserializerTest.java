package com.tf2tj.trade.models.people;

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
 *
 * @author Ihor Sytnik
 */
class InventoryJsonDeserializerTest {
    //TODO testing with painted items

    /*
    currencies: 3x Scrap Metal, 2x Reclaimed Metal, 3x Refined Metal
     */
    private static String inventoryUpdated;
    private static Collection<Item> itemsExpected = new LinkedList<>();

    static Item makeItem(String defIndex, String classId, String instanceId, String name, Quality quality, Paint paint,
                         List<Item.Asset> assets) {
        Item item = new Item();
        item.setDefIndex(defIndex);
        item.setClassId(classId);
        item.setInstanceId(instanceId);
        item.setName(name);
        item.setQuality(quality);
        item.setPaint(paint);
        item.getAssets().addAll(assets);
        return item;
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        inventoryUpdated = new String(
                Objects.requireNonNull(
                        InventoryJsonDeserializerTest.class
                                .getResourceAsStream("/json/inventoryUpdated.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        itemsExpected.add(makeItem("5802", "780650846", "0",
                "Mann Co. Supply Munition Series #91", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498894", "1", 1))));
        itemsExpected.add(makeItem("5781", "780630690", "0",
                "Mann Co. Supply Munition Series #90", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498921", "1", 3))));
        itemsExpected.add(makeItem("5001", "5564", "11040547",
                "Reclaimed Metal", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600699087", "1", 6),
                        new Item.Asset("12600699308", "1", 10))));
        itemsExpected.add(makeItem("5000", "2675", "11040547",
                "Scrap Metal", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600699153", "1", 5),
                        new Item.Asset("12600498962", "1", 7),
                        new Item.Asset("12600699273", "1", 8))));
        itemsExpected.add(makeItem("5002", "2674", "11040547",
                "Refined Metal", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600699205", "1", 4),
                        new Item.Asset("12600699324", "1", 9),
                        new Item.Asset("12600699249", "1", 11))));
        itemsExpected.add(makeItem("673", "22989188", "0",
                "Noise Maker - Winter Holiday", Quality.UNIQUE, Paint.NOT_PAINTED,
                List.of(new Item.Asset("12600498939", "1", 2))));
    }

    @Test
    void deserialize_containsExactlyTheseItems_equals() throws IOException {

        assertEquals(itemsExpected, Inventory.InventoryJsonDeserializer.deserialize(inventoryUpdated).stream().toList());
    }
}