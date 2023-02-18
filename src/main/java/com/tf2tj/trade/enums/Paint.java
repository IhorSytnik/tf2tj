package com.tf2tj.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes different paints' name, hex value and code on backpack.tf.<br>
 *
 * <table border="1">
 *   <tr>
 *     <th>ENUM NAME</th> <th>paint name</th> <th>hex value</th> <th>bp.tf code</th>
 *   </tr>
 *   <tr>
 *     <td>NOT_PAINTED</td> <td></td> <td></td> <td></td>
 *   </tr>
 *   <tr>
 *     <th style="text-align:center;" colspan="4">Single colours</th>
 *   </tr>
 *   <tr>
 *     <td>ZEPHENIAHS_GREED</td> <td>Zepheniah's Greed</td> <td>424F3B</td> <td>4345659</td>
 *   </tr>
 *   <tr>
 *     <td>YE_OLDE_RUSTIC_COLOUR</td> <td>Ye Olde Rustic Colour</td> <td>7C6C57</td> <td>8154199</td>
 *   </tr>
 *   <tr>
 *     <td>THE_COLOR_OF_A_GENTLEMANNS_BUSINESS_PANTS</td> <td>The Color of a Gentlemann's Business Pants</td> <td>F0E68C</td> <td>15787660</td>
 *   </tr>
 *   <tr>
 *     <td>THE_BITTER_TASTE_OF_DEFEAT_AND_LIME</td> <td>The Bitter Taste of Defeat and Lime</td> <td>32CD32</td> <td>3329330</td>
 *   </tr>
 *   <tr>
 *     <td>RADIGAN_CONAGHER_BROWN</td> <td>Radigan Conagher Brown</td> <td>694D3A</td> <td>6901050</td>
 *   </tr>
 *   <tr>
 *     <td>PINK_AS_HELL</td> <td>Pink as Hell</td> <td>FF69B4</td> <td>16738740</td>
 *   </tr>
 *   <tr>
 *     <td>PECULIARLY_DRAB_TINCTURE</td> <td>Peculiarly Drab Tincture</td> <td>C5AF91</td> <td>12955537</td>
 *   </tr>
 *   <tr>
 *     <td>NOBLE_HATTERS_VIOLET</td> <td>Noble Hatter's Violet</td> <td>51384A</td> <td>5322826</td>
 *   </tr>
 *   <tr>
 *     <td>MUSKELMANNBRAUN</td> <td>Muskelmannbraun</td> <td>A57545</td> <td>10843461</td>
 *   </tr>
 *   <tr>
 *     <td>MANN_CO_ORANGE</td> <td>Mann Co. Orange</td> <td>CF7336</td> <td>13595446</td>
 *   </tr>
 *   <tr>
 *     <td>INDUBITABLY_GREEN</td> <td>Indubitably Green</td> <td>729E42</td> <td>7511618</td>
 *   </tr>
 *   <tr>
 *     <td>DRABLY_OLIVE</td> <td>Drably Olive</td> <td>808000</td> <td>8421376</td>
 *   </tr>
 *   <tr>
 *     <td>DARK_SALMON_INJUSTICE</td> <td>Dark Salmon Injustice</td> <td>E9967A</td> <td>15308410</td>
 *   </tr>
 *   <tr>
 *     <td>COLOR_NO_216_190_216</td> <td>Color No. 216-190-216</td> <td>D8BED8</td> <td>14204632</td>
 *   </tr>
 *   <tr>
 *     <td>AUSTRALIUM_GOLD</td> <td>Australium Gold</td> <td>E7B53B</td> <td>15185211</td>
 *   </tr>
 *   <tr>
 *     <td>AN_EXTRAORDINARY_ABUNDANCE_OF_TINGE</td> <td>An Extraordinary Abundance of Tinge</td> <td>E6E6E6</td> <td>15132390</td>
 *   </tr>
 *   <tr>
 *     <td>AGED_MOUSTACHE_GREY</td> <td>Aged Moustache Grey</td> <td>7E7E7E</td> <td>8289918</td>
 *   </tr>
 *   <tr>
 *     <td>AFTER_EIGHT</td> <td>After Eight</td> <td>2D2D24</td> <td>2960676</td>
 *   </tr>
 *   <tr>
 *     <td>A_MANNS_MINT</td> <td>A Mann's Mint</td> <td>BCDDB3</td> <td>12377523</td>
 *   </tr>
 *   <tr>
 *     <td>A_DISTINCTIVE_LACK_OF_HUE</td> <td>A Distinctive Lack of Hue</td> <td>141414</td> <td>1315860</td>
 *   </tr>
 *   <tr>
 *     <td>A_DEEP_COMMITMENT_TO_PURPLE</td> <td>A Deep Commitment to Purple</td> <td>7D4071</td> <td>8208497</td>
 *   </tr>
 *   <tr>
 *     <td>A_COLOR_SIMILAR_TO_SLATE</td> <td>A Color Similar to Slate</td> <td>2F4F4F</td> <td>3100495</td>
 *   </tr>
 *   <tr>
 *     <th style="text-align:center;" colspan="4">Team colours</th>
 *   </tr>
 *   <tr>
 *     <td>WATER_LOGGED_LAB_COAT</td> <td>Waterlogged Lab Coat</td> <td>A89A8C</td> <td>11049612</td>
 *   </tr>
 *   <tr>
 *     <td>THE_VALUE_OF_TEAMWORK</td> <td>The Value of Teamwork</td> <td>803020</td> <td>8400928</td>
 *   </tr>
 *   <tr>
 *     <td>TEAM_SPIRIT</td> <td>Team Spirit</td> <td>B8383B</td> <td>12073019</td>
 *   </tr>
 *   <tr>
 *     <td>OPERATORS_OVERALLS</td> <td>Operator's Overalls</td> <td>483838</td> <td>4732984</td>
 *   </tr>
 *   <tr>
 *     <td>CREAM_SPIRIT</td> <td>Cream Spirit</td> <td>C36C2D</td> <td>12807213</td>
 *   </tr>
 *   <tr>
 *     <td>BALACLAVAS_ARE_FOREVER</td> <td>Balaclavas Are Forever</td> <td>3B1F23</td> <td>3874595</td>
 *   </tr>
 *   <tr>
 *     <td>AN_AIR_OF_DEBONAIR</td> <td>An Air of Debonair</td> <td>654740</td> <td>6637376</td>
 *   </tr>
 * </table>
 *
 * @author Ihor Sytnik
 */
@RequiredArgsConstructor
@Getter
public enum Paint {
    NOT_PAINTED("", "", ""),
    /*Single colours*/
    ZEPHENIAHS_GREED("Zepheniah's Greed", "424F3B", "4345659"),
    YE_OLDE_RUSTIC_COLOUR("Ye Olde Rustic Colour", "7C6C57", "8154199"),
    THE_COLOR_OF_A_GENTLEMANNS_BUSINESS_PANTS("The Color of a Gentlemann's Business Pants", "F0E68C", "15787660"),
    THE_BITTER_TASTE_OF_DEFEAT_AND_LIME("The Bitter Taste of Defeat and Lime", "32CD32", "3329330"),
    RADIGAN_CONAGHER_BROWN("Radigan Conagher Brown", "694D3A", "6901050"),
    PINK_AS_HELL("Pink as Hell", "FF69B4", "16738740"),
    PECULIARLY_DRAB_TINCTURE("Peculiarly Drab Tincture", "C5AF91", "12955537"),
    NOBLE_HATTERS_VIOLET("Noble Hatter's Violet", "51384A", "5322826"),
    MUSKELMANNBRAUN("Muskelmannbraun", "A57545", "10843461"),
    MANN_CO_ORANGE("Mann Co. Orange", "CF7336", "13595446"),
    INDUBITABLY_GREEN("Indubitably Green", "729E42", "7511618"),
    DRABLY_OLIVE("Drably Olive", "808000", "8421376"),
    DARK_SALMON_INJUSTICE("Dark Salmon Injustice", "E9967A", "15308410"),
    COLOR_NO_216_190_216("Color No. 216-190-216", "D8BED8", "14204632"),
    AUSTRALIUM_GOLD("Australium Gold", "E7B53B", "15185211"),
    AN_EXTRAORDINARY_ABUNDANCE_OF_TINGE("An Extraordinary Abundance of Tinge", "E6E6E6", "15132390"),
    AGED_MOUSTACHE_GREY("Aged Moustache Grey", "7E7E7E", "8289918"),
    AFTER_EIGHT("After Eight", "2D2D24", "2960676"),
    A_MANNS_MINT("A Mann's Mint", "BCDDB3", "12377523"),
    A_DISTINCTIVE_LACK_OF_HUE("A Distinctive Lack of Hue", "141414", "1315860"),
    A_DEEP_COMMITMENT_TO_PURPLE("A Deep Commitment to Purple", "7D4071", "8208497"),
    A_COLOR_SIMILAR_TO_SLATE("A Color Similar to Slate", "2F4F4F", "3100495"),
    /*Team colours*/
    WATER_LOGGED_LAB_COAT("Waterlogged Lab Coat", "A89A8C", "11049612"),
    THE_VALUE_OF_TEAMWORK("The Value of Teamwork", "803020", "8400928"),
    TEAM_SPIRIT("Team Spirit", "B8383B", "12073019"),
    OPERATORS_OVERALLS("Operator's Overalls", "483838", "4732984"),
    CREAM_SPIRIT("Cream Spirit", "C36C2D", "12807213"),
    BALACLAVAS_ARE_FOREVER("Balaclavas Are Forever", "3B1F23", "3874595"),
    AN_AIR_OF_DEBONAIR("An Air of Debonair", "654740", "6637376");

    private final String paintName;
    private final String hexValue;
    private final String bpCode;
    private static final Map<String, Paint> map;

    static {
        map = new HashMap<>();
        for (Paint v : Paint.values()) {
            map.put(v.paintName, v);
            map.put(v.hexValue, v);
            map.put(v.bpCode, v);
        }
    }

    public static Paint getByString(String str) {
        return map.get(str);
    }
}
