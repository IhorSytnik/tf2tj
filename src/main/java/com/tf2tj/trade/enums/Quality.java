package com.tf2tj.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes different qualities' name and numerical value.<br>
 *
 * <table border="1">
 *   <tr>
 *     <th>ENUM NAME</th> <th>quality name</th> <th>numerical value</th> <th>comment</th>
 *   </tr>
 *   <tr>
 *     <td>NORMAL</td> <td>Normal</td> <td>0</td>
 *   </tr>
 *   <tr>
 *     <td>GENUINE</td> <td>Genuine</td> <td>1</td>
 *   </tr>
 *   <tr>
 *     <td>RARITY2</td> <td>rarity2</td> <td>2</td> <td>Unused</td>
 *   </tr>
 *   <tr>
 *     <td>VINTAGE</td> <td>Vintage</td> <td>3</td>
 *   </tr>
 *   <tr>
 *     <td>RARITY4</td> <td>rarity4</td> <td>4</td> <td>Unused</td>
 *   </tr>
 *   <tr>
 *     <td>UNUSUAL</td> <td>Unusual</td> <td>5</td>
 *   </tr>
 *   <tr>
 *     <td>UNIQUE</td> <td>Unique</td> <td>6</td>
 *   </tr>
 *   <tr>
 *     <td>COMMUNITY</td> <td>Community</td> <td>7</td>
 *   </tr>
 *   <tr>
 *     <td>VALVE</td> <td>Valve</td> <td>8</td>
 *   </tr>
 *   <tr>
 *     <td>SELF_MADE</td> <td>Self-Made</td> <td>9</td>
 *   </tr>
 *   <tr>
 *     <td>CUSTOMIZED</td> <td>Customized</td> <td>10</td> <td>Unused</td>
 *   </tr>
 *   <tr>
 *     <td>STRANGE</td> <td>Strange</td> <td>11</td>
 *   </tr>
 *   <tr>
 *     <td>COMPLETED</td> <td>Completed</td> <td>12</td> <td>Unused</td>
 *   </tr>
 *   <tr>
 *     <td>HAUNTED</td> <td>Haunted</td> <td>13</td>
 *   </tr>
 *   <tr>
 *     <td>COLLECTORS</td> <td>Collector's</td> <td>14</td>
 *   </tr>
 *   <tr>
 *     <td>DECORATED</td> <td>Decorated</td> <td>15</td>
 *   </tr>
 * </table>
 *
 * @author Ihor Sytnik
 */
@RequiredArgsConstructor
@Getter
public enum Quality {
    NORMAL("Normal", 0),
    GENUINE("Genuine", 1),
    RARITY2("rarity2", 2), // Unused
    VINTAGE("Vintage", 3),
    RARITY4("rarity4", 4), // Unused
    UNUSUAL("Unusual", 5),
    UNIQUE("Unique", 6),
    COMMUNITY("Community", 7),
    VALVE("Valve", 8),
    SELF_MADE("Self-Made", 9),
    CUSTOMIZED("Customized", 10),  // Unused
    STRANGE("Strange", 11),
    COMPLETED("Completed", 12),  // Unused
    HAUNTED("Haunted", 13),
    COLLECTORS("Collector's", 14),
    DECORATED("Decorated", 15);

    private final String qualityName;
    private final int qualityNumeric;
    private static final Map<String, Quality> map;

    static {
        map = new HashMap<>();
        for (Quality v : Quality.values()) {
            map.put(v.qualityName, v);
            map.put(String.valueOf(v.qualityNumeric), v);
        }
    }

    public static Quality getByString(String str) {
        return map.get(str);
    }
}
