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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PriceFull extends PriceScrap implements Comparable<PriceFull> {

    private int keys;

    public PriceFull(int keys, int scrap) {
        super(scrap, false);
        this.keys = keys;
    }

    public PriceFull(int keys, int scrap, boolean plusWeapon) {
        super(scrap, plusWeapon);
        this.keys = keys;
    }

    public PriceFull(int keys, int refs, int recs, int scraps) {
        super(refs, recs, scraps);
        this.keys = keys;
    }

    /**
     * Gets full metal price by converting {@link PriceFull#keys} to metal and adding that amount to
     * {@link PriceFull#scrap}.
     *
     * @param keyPrice price of one key.
     * @return full price in scrap metal.
     */
    public int getMetalPrice(PriceScrap keyPrice) {
        return keys * keyPrice.getScrap() + scrap;
    }

    /**
     * Gets an object of {@link PriceFull} by converting a string to {@link PriceFull}.
     *
     * @param priceString a string to convert the price from.<br>
     *                    Keys should be written as an integer and refs could be written as an integer or a float value.<br>
     *                    Some possible variants:<br>
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
        Matcher matcherPriceKey = Pattern.compile("(\\d+)\\s*key").matcher(priceString);
        int scrapsBeforeDot = getPriceInRefsFromString(Pattern.compile("(\\d+)\\.\\d\\d?\\s*ref|(\\d+)\\s*ref").matcher(priceString));
        int scrapsAfterDot = countScrapsAfterDot(priceString, "\\d+\\.(\\d)(\\d)?\\s*ref");
        boolean plusWeapon = scrapsAfterDot % 11 != 0;
        return new PriceFull(
                matcherPriceKey.find() ?
                        Integer.parseInt(matcherPriceKey.group(1)) :
                        0,
                scrapsBeforeDot + scrapsAfterDot / 11,
                plusWeapon
        );
    }

    /**
     * Gets an object of {@link PriceFull} by converting a string to {@link PriceFull}. Reads only refined metal amount.
     *
     * @param priceString a string to convert the price from.<br>
     *                    Some possible variants:<br>
     *                    <ul>
     *                     <li>{@code 1.2 refs}</li>
     *                     <li>{@code 1.22 refs}</li>
     *                     <li>{@code 1.22}</li>
     *                     <li>{@code 2 ref}</li>
     *                    </ul>
     *
     * @return an object of {@link PriceFull} converted from string.
     * @see #getPriceInRefsFromString(Matcher)
     */
    public static PriceFull getPriceInRefsFromString(String priceString) {
        int scrapsBeforeDot = getPriceInRefsFromString(Pattern.compile("(\\d+)\\.\\d\\d?|(\\d+)").matcher(priceString));
        int scrapsAfterDot = countScrapsAfterDot(priceString, "\\d+\\.(\\d)(\\d)?");
        boolean plusWeapon = scrapsAfterDot % 11 != 0;
        return new PriceFull(
                0,
                scrapsBeforeDot + scrapsAfterDot / 11,
                plusWeapon
        );
    }

    @Override
    public String toString() {
        return "\"" + keys + " keys, " + Operations.getRefsFromScrap(scrap) + " refs"
                + (plusWeapon ? " + weapon" : "") + "\"";
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(PriceFull o) {
        int c = Integer.compare(keys, o.keys);
        return c == 0 ?
                Integer.compare(scrap, o.scrap) :
                c;
    }
}
