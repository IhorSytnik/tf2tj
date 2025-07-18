package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.exceptions.CouldNotFindPriceException;
import com.tf2tj.trade.models.items.*;
import com.tf2tj.trade.models.people.TF2TUser;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ihor Sytnik
 */
@ExtendWith(MockitoExtension.class)
class ScrapProcessorTest {

//    todo test with australium

    private static final Collection<ScrapOffer> offersExpected = new LinkedList<>();
    private static String scrapHTML;

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
    @Mock
    private TF2TUser tUser = new TF2TUser();
    @InjectMocks
    private static ScrapProcessor scrapProcessor;

    static ItemDescription makeItemDescription(String defIndex, String name, String nameBase, boolean craftable,
                                    Quality quality, Paint paint, boolean australium) {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setDefIndex(defIndex);
        itemDescription.setName(name);
        itemDescription.setNameBase(nameBase);
        itemDescription.setCraftable(craftable);
        itemDescription.setQuality(quality);
        itemDescription.setPaint(paint);
        itemDescription.setAustralium(australium);
        return itemDescription;
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        scrapHTML = new String(
                Objects.requireNonNull(ScrapProcessorTest.class
                        .getResourceAsStream("/ScrapTf_hats.html")).readAllBytes(),
                StandardCharsets.UTF_8);
//        offers.add(
//                new ScrapOffer(
//                        makeItemDescription("31124", "Smoking Jacket", "Smoking Jacket",
//                                true, Quality.UNIQUE, Paint.NOT_PAINTED, false),
//                        new PriceFull(3, 37 * 9 + 2),
//                        "12217975829",
//                        1,
//                        Map.of(24, 1))
//        );
        offersExpected.add(
                new ScrapOffer(
                        makeItemDescription("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.A_COLOR_SIMILAR_TO_SLATE, false),
                        new PriceFull(0, 34 * 9 + 2),
                        "12206095587",
                        1,
                        Map.of(20, 1))
        );
        offersExpected.add(
                new ScrapOffer(
                        makeItemDescription("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.NOT_PAINTED, false),
                        new PriceFull(0, 34 * 9 + 2),
                        "12206845552",
                        1,
                        Map.of(20, 1))
        );
        offersExpected.add(
                new ScrapOffer(
                        makeItemDescription("30020", "Strange Scrap Sack", "Scrap Sack",
                                true, Quality.STRANGE, Paint.NOT_PAINTED, false),
                        new PriceFull(0, 32 * 9 + 5),
                        "12166796907",
                        3,
                        Map.of(24, 3))
        );
//        offers.add(
//                new ScrapOffer(
//                        makeItemDescription("30998", "Lucky Cat Hat", "Lucky Cat Hat",
//                                true, Quality.UNIQUE, Paint.NOT_PAINTED, false),
//                        new PriceFull(5, 24 * 9 + 6),
//                        "12216276536",
//                        2,
//                        Map.of(22, 1, 23, 1))
//        );
        offersExpected.add(
                new ScrapOffer(
                        makeItemDescription("249", "Bombing Run", "Bombing Run",
                                true, Quality.UNIQUE, Paint.NOT_PAINTED, false),
                        new PriceFull(0, 9 + 5),
                        "12217987811",
                        1,
                        Map.of(9, 1))
        );
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        Mockito.doReturn(scrapHTML).when(getBrowser).getSource("/buy/hats");
//        Mockito.when(tUser.getPriceLimit()).thenReturn(keyPrice -> 1630);
//        Mockito.doReturn((Function<Object, Object>) keyPrice -> 1630).when(tUser).getPriceLimit();
        ReflectionTestUtils.setField(scrapProcessor, "scrapKeyPriceBuy", new PriceScrap());
    }

    @Test
    void getItems_containsExactlyTheseItems_equals() throws InterruptedException, CouldNotFindPriceException {
        ReflectionTestUtils.setField(scrapProcessor, "listingPage", "hats");
        Mockito.when(tUser.getPriceLimit()).thenReturn(keyPrice -> 1630);

        Collection<ScrapOffer> actual = scrapProcessor.getItems();

        assertEquals(offersExpected, actual);
    }

    @Test
    void getItems_setsScrapKeyPriceBuyCorrectly_equals() {
        ReflectionTestUtils.setField(scrapProcessor, "listingPage", "hats");
        Document scrapHTML = ReflectionTestUtils.invokeMethod(scrapProcessor, "getHTMLDocument");

        ReflectionTestUtils.invokeMethod(scrapProcessor, "parseAndSetKeyBuyingPrice", scrapHTML);

        assertEquals(new PriceScrap(432), ReflectionTestUtils.getField(scrapProcessor, "scrapKeyPriceBuy"));
    }
}