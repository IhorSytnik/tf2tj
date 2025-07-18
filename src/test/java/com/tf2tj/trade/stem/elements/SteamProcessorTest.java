package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import com.tf2tj.trade.models.Trade;
import com.tf2tj.trade.models.items.Item;
import com.tf2tj.trade.models.people.Partner;
import com.tf2tj.trade.models.people.TF2TUser;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ihor Sytnik
 */
@ExtendWith(MockitoExtension.class)
class SteamProcessorTest {

    private static String steamHTMLId;
    private static String steamHTMLProfiles;
    private static String steamHTMLIncomingTrades;
    private static String inventoryUpdated;

    private static final TF2TUser expectedTUserId = new TF2TUser();
    private static final TF2TUser expectedTUserProfiles = new TF2TUser();
    private static final Collection<Trade> tradesExpected = new LinkedList<>();

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
    private static SteamProcessor steamProcessor;

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
        steamHTMLId = new String(
                Objects.requireNonNull(SteamProcessorTest.class
                        .getResourceAsStream("/html/SteamCom_id.html")).readAllBytes(),
                StandardCharsets.UTF_8);
        steamHTMLProfiles = new String(
                Objects.requireNonNull(SteamProcessorTest.class
                        .getResourceAsStream("/html/SteamCom_profiles.html")).readAllBytes(),
                StandardCharsets.UTF_8);
        steamHTMLIncomingTrades = new String(
                Objects.requireNonNull(SteamProcessorTest.class
                        .getResourceAsStream("/html/SteamCom_incomingTrades.html")).readAllBytes(),
                StandardCharsets.UTF_8);
        inventoryUpdated = new String(
                Objects.requireNonNull(SteamProcessorTest.class
                        .getResourceAsStream("/json/inventoryUpdated.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        Trade trade1 = new Trade();
        Partner partner = new Partner();
        partner.setTradeId("55555555");
        partner.setSteamId("121212121212121");
        trade1.setId("5884020218");
        trade1.setPartner(partner);
        tradesExpected.add(trade1);
        Trade trade2 = new Trade();
        trade2.setId("5883976869");
        trade2.setPartner(partner);
        tradesExpected.add(trade2);

        expectedTUserId.setCustomId("Theemann");
        expectedTUserId.setId("24124141412414");
        expectedTUserId.setInventory(inventoryUpdated);
        expectedTUserId.setStartInventory(inventoryUpdated);

        expectedTUserProfiles.setId("24124141412414");
        expectedTUserProfiles.setInventory(inventoryUpdated);
        expectedTUserProfiles.setStartInventory(inventoryUpdated);
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(steamProcessor, "tf2PrimaryContextId", "2");
        ReflectionTestUtils.setField(steamProcessor, "tf2AppId", "440");
    }

    @Test
    void setUser_id_equals() throws Exception {
        ReflectionTestUtils.setField(steamProcessor, "tUser", new TF2TUser());
        Mockito.doReturn(steamHTMLId).when(getBrowser).getSource("/my");
        Mockito.doReturn(inventoryUpdated).when(getBrowser).getSource("/id/Theemann/inventory/json/440/2");

        steamProcessor.setUser();

        assertEquals(expectedTUserId, ReflectionTestUtils.getField(steamProcessor, "tUser"));
    }

    @Test
    void setUser_profiles_equals() throws Exception {
        ReflectionTestUtils.setField(steamProcessor, "tUser", new TF2TUser());
        Mockito.doReturn(steamHTMLProfiles).when(getBrowser).getSource("/my");
        Mockito.doReturn(inventoryUpdated).when(getBrowser)
                .getSource("/profiles/24124141412414/inventory/json/440/2");

        steamProcessor.setUser();

        assertEquals(expectedTUserProfiles, ReflectionTestUtils.getField(steamProcessor, "tUser"));
    }

    @Test
    void getIncomingTrades_id_equals() throws Exception {
        TF2TUser tUser = new TF2TUser();
        tUser.setId("24124141412414");
        tUser.setCustomId("cook");
        ReflectionTestUtils.setField(steamProcessor, "tUser", tUser);
        Mockito.doReturn(steamHTMLIncomingTrades).when(getBrowser)
                .getSource("/id/cook/tradeoffers/");

//        Collection<Trade> actual = steamProcessor.getIncomingTrades();

//        assertEquals(tradesExpected, actual);
    }
}