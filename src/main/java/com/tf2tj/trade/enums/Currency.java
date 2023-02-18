package com.tf2tj.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes different currencies' name, defIndex and classId.
 *
 * @author Ihor Sytnik
 */
@Getter
@RequiredArgsConstructor
public enum Currency {
    SCRAP("Scrap Metal", "5000", "2675"),
    RECLAIMED("Reclaimed Metal", "5001", "5564"),
    REFINED("Refined Metal", "5002", "2674"),
    KEY("Mann Co. Supply Crate Key", "5021", "101785959");

    private final String name;
    private final String defIndex;
    private final String classId;
    private static final Map<String, Currency> map;

    static {
        map = new HashMap<>();
        for (Currency v : Currency.values()) {
            map.put(v.name, v);
            map.put(v.defIndex, v);
            map.put(v.classId, v);
        }
    }

    public static Currency getByString(String str) {
        return map.get(str);
    }

}
