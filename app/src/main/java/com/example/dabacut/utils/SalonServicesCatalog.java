package com.example.dabacut.utils;

import com.example.dabacut.R;
import com.example.dabacut.models.SalonServiceOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SalonServicesCatalog {

    private SalonServicesCatalog() {
    }

    /** Tarifs de base ajustés par la catégorie du salon (Standard / Class / Premium). */
    public static List<SalonServiceOption> buildOptions(String typeSalon, String category) {
        double mult = BookingPriceHelper.categoryMultiplier(category);
        boolean women = isWomenSalon(typeSalon);
        List<SalonServiceOption> list = new ArrayList<>();
        if (women) {
            list.add(new SalonServiceOption("w_cut", R.string.service_women_cut, round(50 * mult), 45));
            list.add(new SalonServiceOption("w_brush", R.string.service_women_brush, round(40 * mult), 30));
            list.add(new SalonServiceOption("w_color", R.string.service_women_color, round(95 * mult), 90));
            list.add(new SalonServiceOption("w_care", R.string.service_women_care, round(55 * mult), 40));
        } else {
            list.add(new SalonServiceOption("m_cut", R.string.service_men_cut, round(45 * mult), 30));
            list.add(new SalonServiceOption("m_beard", R.string.service_men_beard, round(35 * mult), 20));
            list.add(new SalonServiceOption("m_combo", R.string.service_men_combo, round(65 * mult), 45));
            list.add(new SalonServiceOption("m_styling", R.string.service_men_styling, round(30 * mult), 15));
        }
        return list;
    }

    private static boolean isWomenSalon(String typeSalon) {
        if (typeSalon == null) {
            return false;
        }
        String t = typeSalon.toLowerCase(Locale.ROOT);
        return t.contains("fille") || t.contains("femme") || t.contains("women") || t.contains("lady");
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
