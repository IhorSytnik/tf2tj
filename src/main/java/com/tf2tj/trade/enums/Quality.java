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
    NORMAL(     "Normal",       0,  "#B2B2B2"),
    GENUINE(    "Genuine",      1,  "#4D7455"),
    RARITY2(    "rarity2",      2,  "#FFD700"), // Unused
    VINTAGE(    "Vintage",      3,  "#476291"),
    RARITY4(    "rarity4",      4,  "#FFD700"), // Unused
    UNUSUAL(    "Unusual",      5,  "#8650AC"),
    UNIQUE(     "Unique",       6,  "#FFD700"),
    COMMUNITY(  "Community",    7,  "#70B04A"),
    VALVE(      "Valve",        8,  "#A50F79"),
    SELF_MADE(  "Self-Made",    9,  "#70B04A"),
    CUSTOMIZED( "Customized",   10, "#FFD700"),  // Unused
    STRANGE(    "Strange",      11, "#CF6A32"),
    COMPLETED(  "Completed",    12, "#FFD700"),  // Unused
    HAUNTED(    "Haunted",      13, "#38F3AB"),
    COLLECTORS( "Collector's",  14, "#AA0000"),
    DECORATED(  "Decorated",    15, "#FAFAFA");

    private final String name;
    private final int number;
    private final String color;
    private static final Map<String, Quality> map;

    static {
        map = new HashMap<>();
        for (Quality v : Quality.values()) {
            map.put(v.name, v);
            map.put(String.valueOf(v.number), v);
        }
    }

    public static Quality getByString(String str) {
        return map.get(str);
    }
}
