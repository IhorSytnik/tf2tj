package com.tf2tj.trade.models.items;

import com.tf2tj.trade.enums.Killstreak;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import lombok.*;

import java.util.List;

/**
 * Describes items for parsing and trading.
 *
 * @author Ihor Sytnik
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String defIndex;
    private String classId;
    private String instanceId;
    private String name;
    private String nameBase;
    private boolean craftable;
    private boolean australium;
    private Killstreak killstreak;
    private Quality quality;
    private Paint paint;
    private List<Asset> assets;

    /**
     * Describes an asset, that is a particular item with its id.<br>
     * Should be used in trading.
     *
     * @author Ihor Sytnik
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Asset {
        private String id;
        private String amount;
        private int pos;
    }
}
