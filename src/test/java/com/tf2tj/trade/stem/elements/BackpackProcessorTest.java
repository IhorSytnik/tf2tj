package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPartnerException;
import com.tf2tj.trade.models.items.*;
import com.tf2tj.trade.models.people.Partner;
import com.tf2tj.trade.stem.requests.GetBrowser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
@ExtendWith(MockitoExtension.class)
class BackpackProcessorTest {
//    todo test with keys
    private static String bpListingsPage1;
    private static String bpListingsPage2;
    private static String bpListingsPage3;
    private static String bpKeyListingsPage;

    private static Collection<Offer> offersExpected = new LinkedList<>();
    private static ScrapOffer scrapOffer = new ScrapOffer();
    private static String uriPage1;
    private static String uriPage2;
    private static String uriPage3;
    private static String uriKeyPage;

    private int backpackKeyPriceBuy = 729;
    private int backpackKeyPriceSell = 726;

    @Mock
    private GetBrowser getBrowser = new GetBrowser() {
        @Override
        public String getSource(String uri) {
            return null;
        }

        @Override
        public String getSource(String uri, HttpHeaders additionalHeaders) throws InterruptedException {
            return null;
        }

        @Override
        public void updateCookies() {

        }
    };
    @InjectMocks
    private BackpackProcessor backpackProcessor;

    @BeforeAll
    static void beforeAll() throws IOException {
        bpListingsPage1 = new String(
                Objects.requireNonNull(BackpackProcessorTest.class.getResourceAsStream("/BackpackTf_1-1.html"))
                        .readAllBytes(),
                StandardCharsets.UTF_8);
        bpListingsPage2 = new String(
                Objects.requireNonNull(BackpackProcessorTest.class.getResourceAsStream("/BackpackTf_1-2.html"))
                        .readAllBytes(),
                StandardCharsets.UTF_8);
        bpListingsPage3 = new String(
                Objects.requireNonNull(BackpackProcessorTest.class.getResourceAsStream("/BackpackTf_1-3.html"))
                        .readAllBytes(),
                StandardCharsets.UTF_8);
        bpKeyListingsPage = new String(
                Objects.requireNonNull(BackpackProcessorTest.class.getResourceAsStream("/BackpackTf_1_keys.html"))
                        .readAllBytes(),
                StandardCharsets.UTF_8);
        String uriPage = String.format("/classifieds?" +
                        "item=%s&" +
                        "quality=%d&" +
                        "tradable=1&" +
                        "craftable=%d&" +
                        "australium=-1&" +
                        "killstreak_tier=0&" +
                        "offers=1&" +
                        "paint=%s&" +
                        "page=",
                "Conjurer's Cowl",
                6,
                1,
                "");
        uriPage1 = uriPage + "1";
        uriPage2 = uriPage + "2";
        uriPage3 = uriPage + "3";
        uriKeyPage = String.format("/classifieds?" +
                        "item=%s&" +
                        "quality=%d&" +
                        "tradable=1&" +
                        "craftable=%d&" +
                        "australium=-1&" +
                        "killstreak_tier=0&" +
                        "offers=1&" +
                        "paint=%s&" +
                        "page=%d",
                "Mann Co. Supply Crate Key",
                6,
                1,
                "",
                1);
        scrapOffer.setPriceFull(new PriceFull(0, 13));
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setCraftable(true);
        itemDescription.setPaint(Paint.NOT_PAINTED);
        itemDescription.setNameBase("Conjurer's Cowl");
        itemDescription.setQuality(Quality.UNIQUE);
        scrapOffer.setItemDescription(itemDescription);

        offersExpected.add(new Offer(
                new PriceFull(0, 14),
                new Partner("76561199112522018", "1152256290", "yr-jNjuH")
        ));
        offersExpected.add(new Offer(
                new PriceFull(0, 14),
                new Partner("76561199104457222", "1144191494", "dPKn-r4v")
        ));
        offersExpected.add(new Offer(
                new PriceFull(0, 14),
                new Partner("76561199199531944", "1239266216", "DbgXZSMz")
        ));
        offersExpected.add(new Offer(
                new PriceFull(0, 14),
                new Partner("76561199192893105", "1232627377", "u4AvkOA1")
        ));

    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(backpackProcessor, "scrapKeyPriceBuy", new PriceScrap(0));
        ReflectionTestUtils.setField(backpackProcessor, "backpackKeyPriceBuy", new PriceScrap());
        ReflectionTestUtils.setField(backpackProcessor, "backpackKeyPriceSell", new PriceScrap());
    }

    @Test
    void getBuyListings_containsExactlyTheseOffers_equals() throws InterruptedException, CouldNotFindPartnerException {
        Mockito.doReturn(bpListingsPage1).when(getBrowser).getSource(uriPage1);
        Mockito.doReturn(bpListingsPage2).when(getBrowser).getSource(uriPage2);
        Mockito.doReturn(bpListingsPage3).when(getBrowser).getSource(uriPage3);

        assertEquals(offersExpected, backpackProcessor.getBuyOffers(scrapOffer));
    }

    @Test
    void parseAndSetKeyPrices_keyPricesAreSetCorrectly_equals() throws InterruptedException {
        Mockito.doReturn(bpKeyListingsPage).when(getBrowser).getSource(uriKeyPage);
        backpackProcessor.parseAndSetKeyPrices();

        assertEquals(new PriceScrap(backpackKeyPriceBuy),
                ReflectionTestUtils.getField(backpackProcessor, "backpackKeyPriceBuy"));
        assertEquals(new PriceScrap(backpackKeyPriceSell),
                ReflectionTestUtils.getField(backpackProcessor, "backpackKeyPriceSell"));
    }
}