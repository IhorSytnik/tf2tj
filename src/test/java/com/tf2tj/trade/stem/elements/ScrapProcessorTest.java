package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.items.PriceFull;
import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.stem.requests.GetBrowser;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
@ExtendWith(MockitoExtension.class)
class ScrapProcessorTest {

    private static Collection<ScrapOffer> offersExpected = new LinkedList<>();
    private static String scrapHTML;

    @Mock
    private GetBrowser getBrowser = uri -> null;
    @InjectMocks
    private static ScrapProcessor scrapProcessor;

    static Item makeItem(String defIndex, String name, String nameBase, boolean craftable,
                         Quality quality, Paint paint) {
        Item item = new Item();
        item.setDefIndex(defIndex);
        item.setName(name);
        item.setNameBase(nameBase);
        item.setCraftable(craftable);
        item.setQuality(quality);
        item.setPaint(paint);
        return item;
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        scrapHTML = new String(
                Objects.requireNonNull(ScrapProcessorTest.class
                        .getResourceAsStream("/ScrapTF.html")).readAllBytes(),
                StandardCharsets.UTF_8);
//        offers.add(
//                new ScrapOffer(
//                        makeItem("31124", "Smoking Jacket", "Smoking Jacket",
//                                true, Quality.UNIQUE, Paint.NOT_PAINTED),
//                        new PriceFull(3, 37 * 9 + 2),
//                        "12217975829",
//                        1,
//                        Map.of(24, 1))
//        );
        offersExpected.add(
                new ScrapOffer(
                        makeItem("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.A_COLOR_SIMILAR_TO_SLATE),
                        new PriceFull(0, 34 * 9 + 2),
                        "12206095587",
                        1,
                        Map.of(20, 1))
        );
        offersExpected.add(
                new ScrapOffer(
                        makeItem("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.NOT_PAINTED),
                        new PriceFull(0, 34 * 9 + 2),
                        "12206845552",
                        1,
                        Map.of(20, 1))
        );
        offersExpected.add(
                new ScrapOffer(
                        makeItem("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.NOT_PAINTED),
                        new PriceFull(0, 32 * 9 + 5),
                        "12166796907",
                        3,
                        Map.of(24, 3))
        );
//        offers.add(
//                new ScrapOffer(
//                        makeItem("30998", "Lucky Cat Hat", "Lucky Cat Hat",
//                                true, Quality.UNIQUE, Paint.NOT_PAINTED),
//                        new PriceFull(5, 24 * 9 + 6),
//                        "12216276536",
//                        2,
//                        Map.of(22, 1, 23, 1))
//        );
        offersExpected.add(
                new ScrapOffer(
                        makeItem("249", "Bombing Run", "Bombing Run",
                                true, Quality.UNIQUE, Paint.NOT_PAINTED),
                        new PriceFull(0, 9 + 5),
                        "12217987811",
                        1,
                        Map.of(9, 1))
        );
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        Mockito.doReturn(scrapHTML).when(getBrowser).getSource("/buy/hats");
    }

    @Test
    void getItems_containsExactlyTheseItems_equals() throws InterruptedException, CouldNotFindPriceException {
        ReflectionTestUtils.setField(scrapProcessor, "buyingPage", "hats");
        ReflectionTestUtils.setField(scrapProcessor, "buyingPriceLimit", 1630);

        assertEquals(offersExpected, scrapProcessor.getItems());
    }

    @Test
    void getItems_setsScrapKeyPriceBuyCorrectly_equals() {
        ReflectionTestUtils.setField(scrapProcessor, "buyingPage", "hats");
        Document scrapHTML = ReflectionTestUtils.invokeMethod(scrapProcessor, "getHTMLDocument");

        ReflectionTestUtils.invokeMethod(scrapProcessor, "parseAndSetKeyBuyingPrice", scrapHTML);

        assertEquals(432, scrapProcessor.getScrapKeyPriceBuy());
    }
}