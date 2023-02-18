package com.tf2tj.trade.helping;

/**
 * Helping operations.
 *
 * @author Ihor Sytnik
 */
public class Operations {

    /**
     * Gets the scrap metal sum of refs, recs and scrap.
     *
     * @param refs refined metal, to be converted into scrap metal.
     * @param recs reclaimed metal, to be converted into scrap metal.
     * @param scrap scrap metal.
     * @return sum of <b>refs</b>, <b>recs</b> and <b>scrap</b> in scrap metal.
     */
    public static int getScrapFromRefsRecsScrap(int refs, int recs, int scrap) {
        return refs * 9 + recs * 3 + scrap;
    }

    /**
     * Converts scrap metal to refined metal. 1 ref = 9 scrap. 1 scrap = 0.11 ref. 1.88 refs = 9 + 8 scrap.
     *
     * @param scrap scrap metal, to be converted into refined metal.
     * @return refs converted from scrap metal.
     * @see Operations#getScrapFromRefs(float) reverse operation
     */
    public static float getRefsFromScrap(int scrap) {
        return (float)(scrap / 9) + scrap % 9 * 0.11f;
    }

    /**
     * Converts refined metal to scrap metal. 1 ref = 9 scrap. 1 scrap = 0.11 ref. 1.88 refs = 9 + 8 scrap.
     *
     * @param refs refined metal, to be converted into scrap metal.
     * @return scrap metal converted from refs.
     * @see Operations#getRefsFromScrap(int) reverse operation
     */
    public static int getScrapFromRefs(float refs) {
        return (int)refs * 9 + (int)(refs * 10) % 10;
    }

}
