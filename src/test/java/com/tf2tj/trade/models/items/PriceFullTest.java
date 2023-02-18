package com.tf2tj.trade.models.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ihor Sytnik
 */
class PriceFullTest {

    @Test
    void getMetalPrice_4Keys17Scrap93Price_equals() {
        int keyPrice = 93;
        PriceFull priceFull = new PriceFull(4, 17);

        int expected = 389;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_0Keys17Scrap93Price_equals() {
        int keyPrice = 93;
        PriceFull priceFull = new PriceFull(0, 17);

        int expected = 17;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_4Keys0Scrap93Price_equals() {
        int keyPrice = 93;
        PriceFull priceFull = new PriceFull(4, 0);

        int expected = 372;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_0Keys0Scrap93Price_equals() {
        int keyPrice = 93;
        PriceFull priceFull = new PriceFull(0, 0);

        int expected = 0;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

    @Test
    void getMetalPrice_4Keys17Scrap0Price_equals() {
        int keyPrice = 0;
        PriceFull priceFull = new PriceFull(4, 17);

        int expected = 17;

        assertEquals(expected, priceFull.getMetalPrice(keyPrice));
    }

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
}