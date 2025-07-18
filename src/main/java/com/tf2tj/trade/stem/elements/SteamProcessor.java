package com.tf2tj.trade.stem.elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tf2tj.trade.enums.Currency;
import com.tf2tj.trade.models.Trade;
import com.tf2tj.trade.models.TradeDetails;
import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.items.ItemDescription;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.PriceScrap;
import com.tf2tj.trade.models.people.Inventory;
import com.tf2tj.trade.models.people.Partner;
import com.tf2tj.trade.models.people.TF2TUser;
import com.tf2tj.trade.stem.requests.GetBrowser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Steam trading class.
 *
 * @author Ihor Sytnik
 */
@Component
public class SteamProcessor {

    @Autowired
    private GetBrowser steamGetBrowser;
    @Autowired
    private TF2TUser tUser;

    @Value("${tf2.primaryContextId}")
    private String tf2PrimaryContextId;
    @Value("${tf2.appId}")
    private String tf2AppId;

    /**
     * Sets {@link TF2TUser}'s {@code id} and {@code customId} if there is one.
     *
     * @throws Exception if unable to retrieve user information.
     */
    private void setUserIds() throws Exception {
        String userPageStr = steamGetBrowser.getSource("/my");
        Matcher matcherProfileData = Pattern
                .compile("g_rgProfileData = \\{(.+?)}")
                .matcher(userPageStr);

        if (matcherProfileData.find()) {
            Matcher matcherSteamId = Pattern
                    .compile("\"steamid\":\"(.+?)\"")
                    .matcher(matcherProfileData.group(1));
            if (matcherSteamId.find()) {
                tUser.setId(matcherSteamId.group(1));

                Matcher matcherUrl = Pattern
                        .compile("\"url\":\"https:\\\\/\\\\/steamcommunity.com\\\\/id\\\\/(.+?)\\\\/\"")
                        .matcher(matcherProfileData.group(1));
                if (matcherUrl.find()) {
                    tUser.setCustomId(matcherUrl.group(1));
                }
            } else {
                throw new Exception("Couldn't find \"\\\"steamid\\\":\\\"(.+?)\\\"\" for tUser.");
            }
        } else {
            throw new Exception("Couldn't find \"g_rgProfileData = \\\\{(.+?)}\" for tUser.");
        }
    }

    /**
     * Sets {@link TF2TUser}'s {@code inventory} and {@code startInventory}.
     *
     * @throws InterruptedException see {@link GetBrowser#getSource(String)}.
     * @throws JsonProcessingException if exception raises when processing inventory json.
     */
    private void setUserInventory() throws InterruptedException, JsonProcessingException {
        String url = tUser.getAppropriateIdUrl() + "/inventory/json/" + tf2AppId + "/" + tf2PrimaryContextId;
        String inventoryStr;
        JsonNode inventory;

        do {
            inventoryStr = steamGetBrowser.getSource(url);
            inventory = new ObjectMapper().readTree(inventoryStr);
        } while (!inventory.get("success").booleanValue());

        tUser.setInventory(inventoryStr);
        tUser.setStartInventory(inventoryStr);
    }

    /**
     * Sets {@link TF2TUser} for steam trading.
     *
     * @throws Exception if unable to retrieve user information. See {@link #setUserIds()}.
     */
    public void setUser() throws Exception {
        setUserIds();
        setUserInventory();
    }

    private String getSessionId() throws InterruptedException {
        String html = steamGetBrowser.getSource("");
        Matcher sessionIdMatcher = Pattern.compile("g_sessionID\\s=\\s\"(.*)\"").matcher(html);
        if (!sessionIdMatcher.find())
            throw new NoSuchElementException("Couldn't find session id");
        return sessionIdMatcher.group(1);
    }

    private String getPartnersInventory(Partner partner) throws InterruptedException {
        String body = "sessionid=%s&partner=%s&appid=%s&contextid=%s"
                .formatted(getSessionId(), partner.getSteamId(), tf2AppId, tf2PrimaryContextId);
        HttpHeaders additionalHeaders = new HttpHeaders();
        additionalHeaders.add("Referer", partner.getTradeOfferLink());
        return steamGetBrowser.getSource("/tradeoffer/new/partnerinventory/?" + body, additionalHeaders);
    }

    private Collection<Item> readTradeItems(JsonNode assets, Inventory inventory) throws Exception {
        Collection<Item> items = new LinkedList<>();
        for (JsonNode anAsset : assets) {
            items.add(inventory.getItemByAssetId(anAsset.get("assetid").asText()));
        }
        return items;
    }

    private void setTradeItems(Trade trade) throws Exception {
        String html = steamGetBrowser.getSource("/tradeoffer/%s/".formatted(trade.getId()));
        Matcher currentTradeStatusMatcher = Pattern.compile("g_rgCurrentTradeStatus = (.*);").matcher(html);
        if (!currentTradeStatusMatcher.find())
            throw new NoSuchElementException("Couldn't find currentTradeStatus by pattern");
        JsonNode currentTradeStatus = new ObjectMapper().readTree(currentTradeStatusMatcher.group(1));

        trade.setTUserItems(
                readTradeItems(currentTradeStatus.get("me").get("assets"), tUser.getInventory()));
        trade.setPartnerItems(
                readTradeItems(currentTradeStatus.get("them").get("assets"), trade.getPartner().getInventory()));
    }

    public Collection<Trade> requestForIncomingTrades() throws InterruptedException, JsonProcessingException {
        String html = steamGetBrowser.getSource(tUser.getAppropriateIdUrl() + "/tradeoffers/");
        Elements tradeOffers = Jsoup.parse(html).getElementsByClass("tradeoffer");
        Collection<Trade> trades = new LinkedList<>();
        for (Element offer : tradeOffers) {
            if (offer.getElementsByTag("div").hasClass("tradeoffer_items_banner")) {
                continue;
            }
            Trade aTrade = new Trade();
            Matcher matcherTradeId = Pattern
                    .compile("tradeofferid_(\\d+)")
                    .matcher(offer.id());
            if (!matcherTradeId.find())
                throw new NoSuchElementException("Couldn't find trade offer id");
            Matcher matcherSteamId = Pattern
                    .compile("ReportTradeScam\\(\\s'(\\d+)'")
                    .matcher(offer.getElementsByTag("a").attr("onclick"));
            if (!matcherSteamId.find())
                throw new NoSuchElementException("Couldn't find partner's steam id");

            aTrade.setId(matcherTradeId.group(1));
            Partner partner = new Partner();
            partner.setSteamId(matcherSteamId.group(1));
            partner.setTradeId(
                    offer.select("div.tradeoffer_partner")
                            .first()
                            .children()
                            .first()
                            .attr("data-miniprofile")
            );
            partner.setInventory(getPartnersInventory(partner));
            aTrade.setPartner(partner);
            trades.add(aTrade);
        }
        return trades;
    }

    private Tuple2<PriceFull, Map<ItemDescription, Integer>> getCurrencyAndOtherItems(Collection<Item> items) {
        Map<ItemDescription, Integer> otherItems = new HashMap<>();
        int refs = 0;
        int recs = 0;
        int scraps = 0;
        int keys = 0;

        for (Item item : items) {
            if (item.getDefIndex().equals(Currency.REFINED.getDefIndex())) {
                refs++;
            } else if (item.getDefIndex().equals(Currency.RECLAIMED.getDefIndex())) {
                recs++;
            } else if (item.getDefIndex().equals(Currency.SCRAP.getDefIndex())) {
                scraps++;
            } else if (item.getDefIndex().equals(Currency.KEY.getDefIndex())) {
                keys++;
            } else {
                otherItems.put(item.getItemDescription(), item.getAssets().size());
            }
        }

        return Tuples.of(new PriceFull(keys, refs, recs, scraps), otherItems);
    }

    public void setTradeDetails(Trade trade, PriceScrap keyBuyingPrice, PriceScrap keySellingPrice) {
        Tuple2<PriceFull, Map<ItemDescription, Integer>> userCurrIt =
                getCurrencyAndOtherItems(trade.getTUserItems());
        Tuple2<PriceFull, Map<ItemDescription, Integer>> partnerCurrIt =
                getCurrencyAndOtherItems(trade.getPartnerItems());
        trade.setTradeDetails(new TradeDetails(
                userCurrIt.getT1(), userCurrIt.getT2(),
                keyBuyingPrice,
                partnerCurrIt.getT1(), partnerCurrIt.getT2(),
                keySellingPrice
        ));
    }

    public Collection<Trade> getIncomingTrades(PriceScrap keyBuyingPrice, PriceScrap keySellingPrice)
            throws Exception {
        Collection<Trade> trades = requestForIncomingTrades();
        for (Trade trade : trades) {
            setTradeItems(trade);
            setTradeDetails(trade, keyBuyingPrice, keySellingPrice);
        }
        return trades;
    }
}
