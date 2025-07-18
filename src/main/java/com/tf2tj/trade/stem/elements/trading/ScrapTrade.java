package com.tf2tj.trade.stem.elements.trading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.ItemDescription;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.stem.elements.ScrapProcessor;
import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Component
public class ScrapTrade {

    @Autowired
    private HttpRequestBrowser scrapBrowser;
    @Autowired
    private ScrapProcessor scrapProcessor;

    @Value("${scrapTf.pages.buyingPage}")
    private String buyingPage;
    @Value("#{${scrapTf.headersBuy}}")
    private HttpHeaders scrapBuyHeaders;

    private boolean updateOffer(ScrapOffer scrapOffer) throws CouldNotFindPriceException, InterruptedException {
        Optional<ScrapOffer> scrapOfferOptional = scrapProcessor.getItems().stream()
                .filter(offer -> offer.passes(scrapOffer))
                .findFirst();

        if (scrapOfferOptional.isEmpty())
            return false;

        scrapOffer.copy(scrapOfferOptional.get());

        return true;
    }

    private String getCsrfToken() throws Exception {
        Matcher matcher = Pattern.compile("ScrapTF\\.User\\.Hash\\s*=\\s*\\\"(.*?)\\\"").matcher(
                scrapBrowser.getSource("/")
        );

        if (!matcher.find()) {
            throw new Exception("Couldn't find csrf token");
        }

        return matcher.group(1);
    }

    private String makeForm(String csrfToken, List<String> itemswant, String botId) {
        return
            "itemswant=[" + String.join(",", itemswant) + "]&" +
            "itemsgiving=[]&" +
            "bot=" + botId + "&" +
            "csrf=" + csrfToken;
    }

    public boolean buy(ScrapOffer scrapOffer) throws Exception {
        if (!updateOffer(scrapOffer))
            return false;

        JsonNode buyResponse;
        boolean success = false;
        for (Integer botId : scrapOffer.getBotIds().keySet()) {
            WebClient.ResponseSpec responseSpec = scrapBrowser.post(
                    "/ajax/" + buyingPage,
                    scrapBuyHeaders,
                    makeForm(
                            getCsrfToken(),
                            List.of(scrapOffer.getItemId()),
                            botId.toString()
                    )
            );

            String body = responseSpec.bodyToMono(String.class).block();
            buyResponse = new ObjectMapper().readTree(body);

            if (buyResponse.get("success").booleanValue()) {
                success = true;
                break;
            } else if (buyResponse.get("message").toString()
                    .equals("All of the items you selected were already bought")) {
//                if (!updateOffer(scrapOffer)) return false;
                break;
            } else if (!buyResponse.get("message").toString().equals("This bot is no longer available to trade.")) {
//                if (!updateOffer(scrapOffer)) return false;
                throw new RuntimeException("An unexpected error occurred while trying to buy an item from scrap.tf.");
            }
        }
        return success;
    }

    public boolean testing() throws Exception {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setDefIndex("5035");
        itemDescription.setName(Paint.RADIGAN_CONAGHER_BROWN.getName());
        itemDescription.setNameBase(Paint.RADIGAN_CONAGHER_BROWN.getName());
        itemDescription.setCraftable(true);
        itemDescription.setQuality(Quality.UNIQUE);
        itemDescription.setPaint(Paint.RADIGAN_CONAGHER_BROWN);
        itemDescription.setAustralium(false);

        ScrapOffer scrapOffer = new ScrapOffer(
                itemDescription,
                new PriceFull(0, 1 * 9),
                "14717792298",
                2,
                Map.of(35, 12));

        return buy(scrapOffer);
    }
}
