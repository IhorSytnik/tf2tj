package com.tf2tj.trade.models.items;

import com.tf2tj.trade.enums.Killstreak;
import com.tf2tj.trade.enums.Paint;
import com.tf2tj.trade.enums.Quality;
import lombok.*;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemDescription {
//    todo add spells
//    todo add strange parts
    private String defIndex;
    private String classId;
    private String instanceId;
    private String name;
    private String nameBase;
    private String backgroundImage;
    private boolean craftable;
    private boolean australium;
    private boolean festivized;
    private Killstreak killstreak;
    private Quality quality;
    private Paint paint;
}
