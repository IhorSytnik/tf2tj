package com.tf2tj.trade.models.items;

import com.tf2tj.trade.helping.Operations;
import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Item price in Scrap Metal.<br>
 * Used standalone for key prices
 *
 * @author Ihor Sytnik
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PriceScrap {

    protected int scrap;
    protected boolean plusWeapon = false;

    public PriceScrap(int refs, int recs, int scraps) {
        this.scrap = Operations.getScrapFromRefsRecsScrap(refs, recs, scraps);
    }

    public PriceScrap(int scraps) {
        this.scrap = scraps;
    }

//    protected static boolean findPlusWeapon(String priceString) {
//        Matcher matcherWeapon = Pattern.compile("\\d+\\.(\\d)(\\d)").matcher(priceString);
//        boolean plusWeapon = false;
//        if (matcherWeapon.find()) {
//            plusWeapon = matcherWeapon.group(1) != null && matcherWeapon.group(2) != null &&
//                    !matcherWeapon.group(1).equals(matcherWeapon.group(2));
//        }
//        return plusWeapon;
//    }

    protected static int countScrapsAfterDot(String priceString, String pattern) {
        Matcher matcherWeapon = Pattern.compile(pattern).matcher(priceString);
        if (matcherWeapon.find() && matcherWeapon.group(1) != null) {
            String scrStr =
                    matcherWeapon.group(2) != null ?
                            matcherWeapon.group(1) + matcherWeapon.group(2) :
                            matcherWeapon.group(1) + matcherWeapon.group(1);
            return Integer.parseInt(scrStr);
        }
        return 0;
    }

    /**
     * Gets an object of {@link PriceScrap} by converting a string to {@link PriceScrap}. Reads only refined metal amount.
     *
     * @param priceString a string to convert the price from.<br>
     *                    Keys should be written as an integer and refs could be written as an integer or a float value.<br>
     *                    Some possible variants of <b>priceString</b>:<br>
     *                    <ul>
     *                     <li>{@code 1.2 refs}</li>
     *                     <li>{@code 1.22 refs}</li>
     *                     <li>{@code 1.22}</li>
     *                     <li>{@code 2 ref}</li>
     *                    </ul>
     *
     * @return an object of {@link PriceScrap} converted from string.
     * @see #getPriceInRefsFromString(Matcher)
     */
    public static PriceScrap getPriceInRefsFromString(String priceString) {
        int scrapsBeforeDot = getPriceInRefsFromString(Pattern.compile("(\\d+)\\.\\d\\d?|(\\d+)").matcher(priceString));
        int scrapsAfterDot = countScrapsAfterDot(priceString, "\\d+\\.(\\d)(\\d)?");
        boolean plusWeapon = scrapsAfterDot % 11 != 0;
        return new PriceScrap(
                scrapsBeforeDot + scrapsAfterDot / 11,
                plusWeapon
        );
    }

    /**
     * A method for matching ref string.
     *
     * @param matcherPriceRef a matcher to match refs from.
     * @return an object of {@link PriceFull} converted from string.
     */
    protected static int getPriceInRefsFromString(Matcher matcherPriceRef) {
        return matcherPriceRef.find() ?
                        matcherPriceRef.group(1) != null ?
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(1)),
                                        0,
                                        0) :
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(2)),
                                        0,
                                        0) :
                        0;
    }

    @Override
    public String toString() {
        return "\"" + Operations.getRefsFromScrap(scrap) + " refs" + (plusWeapon ? " + weapon" : "") + "\"";
    }
}
