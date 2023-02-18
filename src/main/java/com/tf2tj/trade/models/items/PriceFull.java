package com.tf2tj.trade.models.items;

import com.tf2tj.trade.helping.Operations;
import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Describes item price in keys and metal.
 *
 * @author Ihor Sytnik
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PriceFull {
    private int keys;
    private int scrap;

    /**
     * Gets full metal price by converting {@link PriceFull#keys} to metal and adding that amount to
     * {@link PriceFull#scrap}.
     *
     * @param keyPrice price of one key in scrap metal.
     * @return full price in scrap metal.
     */
    public int getMetalPrice(int keyPrice) {
        return keys * keyPrice + scrap;
    }

    /**
     * Gets an object of {@link PriceFull} by converting a string to {@link PriceFull}.
     *
     * @param priceString a string to convert the price from.<br>
     *                    Keys should be written as an integer and refs could be written as an integer or a float value.<br>
     *                    Some possible variants of <b>priceString</b>:<br>
     *                    <ul>
     *                     <li>{@code 12 keys}</li>
     *                     <li>{@code 1.2 refs}</li>
     *                     <li>{@code 12 keys, 1.22 refs}</li>
     *                     <li>{@code 12 key}</li>
     *                     <li>{@code 2 ref}</li>
     *                    </ul>
     *                    If keys are given as a float number, it drops the dot and the part before it, eg
     *                    "{@code 3.22 keys}" will be read as "{@code 22 keys}".
     *
     * @return an object of {@link PriceFull} converted from string.
     */
    public static PriceFull getPriceFromString(String priceString) {
        Matcher matcherPriceKey = Pattern.compile("(\\d+) key").matcher(priceString);
        Matcher matcherPriceRef = Pattern.compile("(\\d+) ref|(\\d+)\\.(\\d)\\d? ref").matcher(priceString);
        return new PriceFull(
                matcherPriceKey.find() ?
                        Integer.parseInt(matcherPriceKey.group(1)) :
                        0,
                matcherPriceRef.find() ?
                        matcherPriceRef.group(1) != null ?
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(1)),
                                        0,
                                        0) :
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(2)),
                                        0,
                                        Integer.parseInt(matcherPriceRef.group(3))) :
                        0
        );
    }

    /**
     * Gets an object of {@link PriceFull} by converting a string to {@link PriceFull}. Reads only refined metal amount.
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
     * @return an object of {@link PriceFull} converted from string.
     */
    public static PriceFull getPriceInRefsFromString(String priceString) {
        Matcher matcherPriceRef = Pattern.compile("(\\d+)\\.(\\d)\\d?|(\\d+)").matcher(priceString);
        return new PriceFull(
                0,
                matcherPriceRef.find() ?
                        matcherPriceRef.group(1) != null ?
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(1)),
                                        0,
                                        Integer.parseInt(matcherPriceRef.group(2))) :
                                Operations.getScrapFromRefsRecsScrap(
                                        Integer.parseInt(matcherPriceRef.group(3)),
                                        0,
                                        0) :
                        0
        );
    }

    @Override
    public String toString() {
        return "\"" + keys + " keys, " + Operations.getRefsFromScrap(scrap) + " refs\"";
    }
}
