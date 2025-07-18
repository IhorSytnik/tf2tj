package com.tf2tj.trade.helping;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ihor Sytnik
 */
class OperationsTest {

    /*getMetalFromRefsRecsScrap*/

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

    /*getMetalToRefs*/

    @Test
    void getMetalToRefs_testingWithWholeNumber_equals() {
        int scrap = 47;

        String expected = "5.22";

        assertEquals(expected, Operations.getRefsFromScrap(scrap));
    }

    /*getRefsToMetal*/

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