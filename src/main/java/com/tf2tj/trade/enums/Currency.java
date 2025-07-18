package com.tf2tj.trade.enums;

import com.tf2tj.trade.models.items.ItemDescription;
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
public enum Currency {
    SCRAP(new ItemDescription("5000", "2675", null, "Scrap Metal", "Scrap Metal", "https://community.akamai.steamstatic.com/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-9xgEbZQsUYhTkhzJWhsPZAfOeD-VOn4phtsdQ32ZtxFYoN7PkYmVmIgeaUKNaX_Rjpwy8UHMz6pcxAIfnovUWJ1t9nYFqYw/96fx96f", true, false, false, null, Quality.UNIQUE, Paint.NOT_PAINTED)),
    RECLAIMED(new ItemDescription("5001", "5564", null, "Reclaimed Metal", "Reclaimed Metal", "https://community.akamai.steamstatic.com/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-9xgEbZQsUYhTkhzJWhsO0Mv6NGucF1YJlscMEgDdvxVYsMLPkMmFjI1OSUvMHDPBp9lu0CnVluZQxA9Gwp-hIOVK4sMMNWF4/96fx96f", true, false, false, null, Quality.UNIQUE, Paint.NOT_PAINTED)),
    REFINED(new ItemDescription("5002", "2674", null, "Refined Metal", "Refined Metal", "https://community.akamai.steamstatic.com/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-9xgEbZQsUYhTkhzJWhsO1Mv6NGucF1Ygzt8ZQijJukFMiMrbhYDEwI1yRVKNfD6xorQ3qW3Jr6546DNPuou9IOVK4p4kWJaA/96fx96f", true, false, false, null, Quality.UNIQUE, Paint.NOT_PAINTED)),
    KEY(new ItemDescription("5021", "101785959", null, "Mann Co. Supply Crate Key", "Mann Co. Supply Crate Key", "https://community.akamai.steamstatic.com/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-9xgEAaR4uURrwvz0N252yVaDVWrRTno9m4ccG2GNqxlQoZrC2aG9hcVGUWflbX_drrVu5UGki5sAij6tOtQ/96fx96f", true, false, false, null, Quality.UNIQUE, Paint.NOT_PAINTED));

    private final String name;
    private final String defIndex;
    private final String classId;
    private final ItemDescription itemDescription;
    private static final Map<String, Currency> map;

    Currency(ItemDescription itemDescription) {
        this.itemDescription = itemDescription;
        this.name = itemDescription.getNameBase();
        this.defIndex = itemDescription.getDefIndex();
        this.classId = itemDescription.getClassId();
    }

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
