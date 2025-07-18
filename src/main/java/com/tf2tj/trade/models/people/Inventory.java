package com.tf2tj.trade.models.people;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tf2tj.trade.enums.Currency;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.models.items.Item;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class that represents a steam inventory.
 *
 * @author Ihor Sytnik
 */
@Getter
@EqualsAndHashCode
@ToString
public class Inventory {
    private List<Item> itemList;
    private Item scraps;
    private Item recs;
    private Item refs;
    private Item keys;

    /**
     *
     * @param inventory inventory json string.
     * @throws JsonProcessingException see {@link #deserializeJsonInventory(String)}.
     */
    public Inventory(String inventory) throws JsonProcessingException {
        this.itemList = deserializeJsonInventory(inventory);
        countCurrencies();
    }

    /**
     * Updates {@link Inventory#itemList} and then calls {@link Inventory#countCurrencies()}.
     *
     * @param inventory inventory json string.
     * @return {@code false} if inventory wasn't updated, {@code true} - otherwise.
     * @throws JsonProcessingException see {@link #deserializeJsonInventory(String)}.
     */
    public boolean updateInventory(String inventory) throws JsonProcessingException {
        List<Item> newItemList = deserializeJsonInventory(inventory);
        if (this.itemList.equals(newItemList)) {
            return false;
        }
        this.itemList = newItemList;
        countCurrencies();
        return true;
    }

    /**
     * Deserializes Json string to a {@link List} of {@link Item}s.
     *
     * @param inventory inventory json string.
     * @return {@link List} of {@link Item}, contents of the inventory.
     * @throws JsonProcessingException see {@link InventoryJsonDeserializer#deserialize(String)}.
     */
    private List<Item> deserializeJsonInventory(String inventory) throws JsonProcessingException {
        return InventoryJsonDeserializer.deserialize(inventory).stream().toList();
    }

    /**
     * Writes currencies, which are Scrap Metal, Reclaimed metal, Refined Metal and Mann Co.
     * Supply Crate Key, into {@link Inventory#scraps}, {@link Inventory#recs}, {@link Inventory#refs} and
     * {@link Inventory#keys}, respectively.
     */
    private void countCurrencies() {
        scraps = itemList.stream()
                .filter(it -> it.getClassId().equals(Currency.SCRAP.getClassId()))
                .findFirst()
                .orElse(new Item());
        recs = itemList.stream()
                .filter(it -> it.getClassId().equals(Currency.RECLAIMED.getClassId()))
                .findFirst()
                .orElse(new Item());
        refs = itemList.stream()
                .filter(it -> it.getClassId().equals(Currency.REFINED.getClassId()))
                .findFirst()
                .orElse(new Item());
        keys = itemList.stream()
                .filter(it -> it.getClassId().equals(Currency.KEY.getClassId()))
                .findFirst()
                .orElse(new Item());
    }

    /**
     * Get all items from inventory (rgInventory in json) without currencies.
     *
     * @return all items from inventory (rgInventory in json) without currencies.
     */
    public List<Item> getItemsWOCurrencies() {
        return getItemList().stream()
                .filter(item -> !item.getClassId().equals(Currency.SCRAP.getClassId()) &&
                        !item.getClassId().equals(Currency.RECLAIMED.getClassId()) &&
                        !item.getClassId().equals(Currency.REFINED.getClassId()) &&
                        !item.getClassId().equals(Currency.KEY.getClassId()))
                .collect(Collectors.toList());
    }

    public Item getItemByAssetId(String assetId) throws Exception {
        return itemList.stream()
                .filter(item -> item.getAssets().stream()
                        .anyMatch(asset -> asset.getId().equals(assetId)))
                .map(item ->
                        item.withAssets(item.getAssets().stream()
                                .filter(
                                        asset -> asset.getId().equals(assetId))
                                .toList())
                )
                .findAny()
                .orElseThrow(() -> new Exception("No such item with this asset id."));
    }

    /**
     * Class for deserialization of json string into a {@link List} of {@link Item}s.
     */
    public static class InventoryJsonDeserializer {

        /**
         * Gets paint somewhere from {@code "{ descriptions: { "value": "Paint Color: ..." }, }"} in rgDescriptions<br>
         * Seems like it is the only way to find the paint.
         *
         * @param descriptions a {@link JsonNode} object of descriptions json string in rgDescriptions.
         * @return {@link Paint} object of an appropriate value, {@link Paint#NOT_PAINTED} if item isn't painted.
         */
        private static Paint getPaint(JsonNode descriptions) {
            Iterator<JsonNode> descriptionsIterator = descriptions.elements();
            while (descriptionsIterator.hasNext()) {
                JsonNode descNode = descriptionsIterator.next();
                if (!descNode.hasNonNull("value"))
                    continue;
                String value = descNode.get("value").asText();
                Pattern pattern = Pattern.compile("\s*Paint\s*Color:\s*(.*?)\s*");
                Matcher matcher = pattern.matcher(value);
                if (matcher.find())
                    return Paint.getByString(matcher.group(1));
            }
            return Paint.NOT_PAINTED;
        }

        /**
         * Gets description for items from rgDescriptions and adds them to itemMap.
         *
         * @param inventory a {@link JsonNode} object of inventory json string.
         * @return {@link HashMap} of key of classId, instanceId and value item.
         */
        private static Map<Tuple2<String, String>, Item> getDescriptions(JsonNode inventory) {
            Map<Tuple2<String, String>, Item> itemMap = new HashMap<>();

            Iterator<JsonNode> rgDescriptionsIterator = inventory.get("rgDescriptions").elements();
            while (rgDescriptionsIterator.hasNext()) {
                JsonNode descriptionJson = rgDescriptionsIterator.next();

                // Checks if an item is tradable
                if (descriptionJson.get("tradable").intValue() != 1)
                    continue;

                Item it = new Item();

                String classId = descriptionJson.get("classid").asText();
                String instanceId = descriptionJson.get("instanceid").asText();
                it.setClassId(classId);
                it.setInstanceId(instanceId);
                it.setDefIndex(descriptionJson.get("app_data").get("def_index").asText());
                it.setQuality(Quality.getByString(descriptionJson.get("app_data").get("quality").asText()));
                it.setName(descriptionJson.get("name").asText());
                /* craftable might only be in
                "{descriptions: { "value": "( Not Tradable, Marketable, or Usable in Crafting )" }}" */
                //boolean craftable;
                it.setPaint(getPaint(descriptionJson.get("descriptions")));

                itemMap.put(Tuples.of(classId, instanceId), it);
            }
            return itemMap;
        }

        /**
         * Gets assets from rgInventory and adds them to appropriate {@link Item}s.
         *
         * @param inventory a {@link JsonNode} object of inventory json string.
         * @param itemMap {@link HashMap} of key of classId, instanceId and value {@link Item}. It should already be
         *                               gone through {@link InventoryJsonDeserializer#getDescriptions(JsonNode)} and
         *                               have {@link Item} instances.
         * @return {@link HashMap} of key of classId, instanceId and value {@link Item}.
         */
        private static Map<Tuple2<String, String>, Item> getAssets(JsonNode inventory,
                                                                   Map<Tuple2<String, String>, Item> itemMap) {
            Iterator<JsonNode> rgInventoryIterator = inventory.get("rgInventory").elements();
            while (rgInventoryIterator.hasNext()) {
                JsonNode assetJson = rgInventoryIterator.next();

                Tuple2<String, String> itemKey = Tuples.of(
                        assetJson.get("classid").asText(),
                        assetJson.get("instanceid").asText());

                if (!itemMap.containsKey(itemKey))
                    continue;

                itemMap.get(itemKey).getAssets().add(
                        new Item.Asset(
                                assetJson.get("id").asText(),
                                assetJson.get("amount").asText(),
                                assetJson.get("pos").asInt())
                );
            }
            return itemMap;
        }

        /**
         * Deserializes a json string into a {@link List} of {@link Item}s.
         *
         * @param inventoryJson inventory json string.
         * @return a {@link List} of {@link Item}s.
         * @throws JsonProcessingException see {@link ObjectMapper#readTree(String)}.
         */
        public static Collection<Item> deserialize(String inventoryJson) throws JsonProcessingException {
            JsonNode inventory = new ObjectMapper().readTree(inventoryJson);
            Map<Tuple2<String, String>, Item> itemMap = getAssets(inventory, getDescriptions(inventory));
            return itemMap.values();
        }
    }
}
