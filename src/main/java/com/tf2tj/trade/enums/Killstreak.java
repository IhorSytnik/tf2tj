package com.tf2tj.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains killstreak description.
 *
 * @author Ihor Sytnik
 */
public class Killstreak {

    public Tier tier;
    public Sheen sheen;
    public Killstreaker killstreaker;

    /**
     * Describes killstreak tiers.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Tier {
        NONE("", "0"),
        STANDARD("Standard", "1"),
        SPECIALIZED("Specialized", "2"),
        PROFESSIONAL("Proffecional", "3");

        private final String name;
        private final String code;
        private static final Map<String, Tier> map;

        static {
            map = new HashMap<>();
            for (Tier v : Tier.values()) {
                map.put(v.name, v);
                map.put(v.code, v);
            }
        }

        public static Tier getByString(String str) {
            return map.get(str);
        }

    }

    /**
     * Describes killstreak sheens.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Sheen {
        NONE("", ""),
        TEAM_SHINE("Team Shine", "1"),
        DEADLY_DAFFODIL("Deadly Daffodil", "2"),
        MANNDARIN("Manndarin", "3"),
        MEAN_GREEN("Mean Green", "4"),
        AGONIZING_EMERALD("Agonizing Emerald", "5"),
        VILLAINOUS_VIOLET("Villainous Violet", "6"),
        HOT_ROD("Hot Rod", "7");

        private final String name;
        private final String code;
        private static final Map<String, Sheen> map;

        static {
            map = new HashMap<>();
            for (Sheen v : Sheen.values()) {
                map.put(v.name, v);
                map.put(v.code, v);
            }
        }

        public static Sheen getByString(String str) {
            return map.get(str);
        }

    }

    /**
     * Describes killstreaker.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Killstreaker {
        NONE("", ""),
        FIRE_HORNS("Fire Horns", "2002"),
        CEREBRAL_DISCHARGE("Cerebral Discharge", "2003"),
        TORNADO("Tornado", "2004"),
        FLAMES("Flames", "2005"),
        SINGULARITY("Singularity", "2006"),
        INCINERATOR("Incinerator", "2007"),
        HYPNO_BEAM("Hypno-Beam", "2008");

        private final String name;
        private final String code;
        private static final Map<String, Killstreaker> map;

        static {
            map = new HashMap<>();
            for (Killstreaker v : Killstreaker.values()) {
                map.put(v.name, v);
                map.put(v.code, v);
            }
        }

        public static Killstreaker getByString(String str) {
            return map.get(str);
        }
    }
}
