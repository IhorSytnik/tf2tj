package com.tf2tj.trade.models.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
class PriceFullTest {

    /*equals*/

    @Test
    void equals_sameAmountOfKeysDifferentAmountOfScrap_notEquals() {
        PriceFull priceFull1 = new PriceFull(4, 17);
        PriceFull priceFull2 = new PriceFull(4, 44);

        assertNotEquals(priceFull1, priceFull2);
    }

    @Test
    void equals_differentAmountOfKeysDifferentAmountOfScrap_notEquals() {
        PriceFull priceFull1 = new PriceFull(4, 17);
        PriceFull priceFull2 = new PriceFull(6, 44);

        assertNotEquals(priceFull1, priceFull2);
    }

    @Test
    void equals_differentAmountOfKeysSameAmountOfScrap_notEquals() {
        PriceFull priceFull1 = new PriceFull(4, 17);
        PriceFull priceFull2 = new PriceFull(6, 17);

        assertNotEquals(priceFull1, priceFull2);
    }

    @Test
    void equals_sameAmountOfEverything_equals() {
        PriceFull priceFull1 = new PriceFull(4, 17);
        PriceFull priceFull2 = new PriceFull(4, 17);

        assertEquals(priceFull1, priceFull2);
    }

    /*getMetalPrice*/

    @Test
    void getMetalPrice_4Keys17Scrap93Price_equals() {
        PriceScrap keyPrice = new PriceScrap(93);
        PriceFull priceFull = new PriceFull(4, 17);

        int expected = 389;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_0Keys17Scrap93Price_equals() {
        PriceScrap keyPrice = new PriceScrap(93);
        PriceFull priceFull = new PriceFull(0, 17);

        int expected = 17;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_4Keys0Scrap93Price_equals() {
        PriceScrap keyPrice = new PriceScrap(93);
        PriceFull priceFull = new PriceFull(4, 0);

        int expected = 372;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_0Keys0Scrap93Price_equals() {
        PriceScrap keyPrice = new PriceScrap(93);
        PriceFull priceFull = new PriceFull(0, 0);

        int expected = 0;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_4Keys17Scrap0Price_equals() {
        PriceScrap keyPrice = new PriceScrap(0);
        PriceFull priceFull = new PriceFull(4, 17);

        int expected = 17;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    /*getPriceFromString*/

    @Test
    void getPriceFromString_intKeysCommaIntRefs_equals() {
        String given = "3 keys, 2 refs";

        PriceFull expected = new PriceFull(3, 18);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKeysCommaFloatRefs_equals() {
        String given = "3 keys, 2.5 refs";

        PriceFull expected = new PriceFull(3, 23);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_floatKeysCommaFloatRefs_equals() {
        String given = "3.22 keys, 2.5 refs";

        PriceFull expected = new PriceFull(22, 23);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKeysFloatRefs_equals() {
        String given = "3 keys2.5 refs";

        PriceFull expected = new PriceFull(3, 23);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKeyFloatRef_equals() {
        String given = "3 key2.5 ref";

        PriceFull expected = new PriceFull(3, 23);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_floatRef_equals() {
        String given = "1.88 ref";

        PriceFull expected = new PriceFull(0, 17);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intRef_equals() {
        String given = "8 ref";

        PriceFull expected = new PriceFull(0, 72);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKey_equals() {
        String given = "42 key";

        PriceFull expected = new PriceFull(42, 0);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKeysCommaFloatRefsPlusWeaponFirst_equals() {
        String given = "3 keys, 2.72 refs";

        PriceFull expected = new PriceFull(3, 24, true);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    @Test
    void getPriceFromString_intKeysCommaFloatRefsPlusWeaponSecond_equals() {
        String given = "3 keys, 4.05 refs";

        PriceFull expected = new PriceFull(3, 36, true);

        assertEquals(expected, PriceFull.getPriceFromString(given));
    }

    /*getPriceInRefsFromString*/

    @Test
    void getPriceInRefsFromString_float_equals() {
        String given = "1.22";

        PriceFull expected = new PriceFull(0, 11);

        assertEquals(expected, PriceFull.getPriceInRefsFromString(given));
    }

    @Test
    void getPriceInRefsFromString_int_equals() {
        String given = "5";

        PriceFull expected = new PriceFull(0, 45);

        assertEquals(expected, PriceFull.getPriceInRefsFromString(given));
    }

    @Test
    void getPriceInRefsFromString_floatRef_equals() {
        String given = "5.55 ref";

        PriceFull expected = new PriceFull(0, 50);

        assertEquals(expected, PriceFull.getPriceInRefsFromString(given));
    }

    @Test
    void getPriceInRefsFromString_intRef_equals() {
        String given = "5 ref";

        PriceFull expected = new PriceFull(0, 45);

        assertEquals(expected, PriceFull.getPriceInRefsFromString(given));
    }

    @Test
    void getPriceInRefsFromString_noneRef_equals() {
        String given = " ref";

        PriceFull expected = new PriceFull(0, 0);

        assertEquals(expected, PriceFull.getPriceInRefsFromString(given));
    }

    /*compareTo*/

    @Test
    void compareTo_1Key21RefTo2Key11Ref_negative() {
        PriceFull price1 = new PriceFull(1, 21);
        PriceFull price2 = new PriceFull(2, 11);

        assertTrue(price1.compareTo(price2) < 0);
    }

    @Test
    void compareTo_2Key11RefTo1Key21Ref_positive() {
        PriceFull price1 = new PriceFull(1, 21);
        PriceFull price2 = new PriceFull(2, 11);

        assertTrue(price2.compareTo(price1) > 0);
    }

    @Test
    void compareTo_1Key21RefTo1Key21Ref_zero() {
        PriceFull price1 = new PriceFull(1, 21);
        PriceFull price2 = new PriceFull(1, 21);

        assertEquals(0, price1.compareTo(price2));
    }
}