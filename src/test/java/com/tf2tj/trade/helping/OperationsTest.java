package com.tf2tj.trade.helping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
class OperationsTest {

    @Test
    void getMetalFromRefsRecsScrap_8Refs1Recs4Scrap_equals() {
        int refs = 8;
        int recs = 1;
        int scrap = 4;

        int expected = 79;

        assertEquals(expected, Operations.getScrapFromRefsRecsScrap(refs, recs, scrap));
    }

    @Test
    void getMetalFromRefsRecsScrap_0Refs1Recs4Scrap_equals() {
        int refs = 0;
        int recs = 1;
        int scrap = 4;

        int expected = 7;

        assertEquals(expected, Operations.getScrapFromRefsRecsScrap(refs, recs, scrap));
    }

    @Test
    void getMetalFromRefsRecsScrap_8Refs0Recs4Scrap_equals() {
        int refs = 8;
        int recs = 0;
        int scrap = 4;

        int expected = 76;

        assertEquals(expected, Operations.getScrapFromRefsRecsScrap(refs, recs, scrap));
    }

    @Test
    void getMetalFromRefsRecsScrap_8Refs1Recs0Scrap_equals() {
        int refs = 8;
        int recs = 1;
        int scrap = 0;

        int expected = 75;

        assertEquals(expected, Operations.getScrapFromRefsRecsScrap(refs, recs, scrap));
    }

    @Test
    void getMetalToRefs_testingWithWholeNumber_equals() {
        int scrap = 47;

        float expected = 5.22f;

        assertEquals(expected, Operations.getRefsFromScrap(scrap));
    }

    @Test
    void getRefsToMetal_testingWithFloat_equals() {
        float refs = 5.22f;

        int expected = 47;

        assertEquals(expected, Operations.getScrapFromRefs(refs));
    }

    @Test
    void getRefsToMetal_testingWithWholeNumber_equals() {
        float refs = 5f;

        int expected = 45;

        assertEquals(expected, Operations.getScrapFromRefs(refs));
    }
}