package com.example.dabacut.utils;

import android.content.Context;
import android.os.Build;

import java.text.NumberFormat;
import java.util.Locale;

public final class BookingPriceHelper {

    private BookingPriceHelper() {
    }

    /** Multiplicateur de prix menu selon la catégorie du salon. */
    public static double categoryMultiplier(String category) {
        if (category == null || category.trim().isEmpty()) {
            return 1.0;
        }
        String c = category.trim().toLowerCase(Locale.ROOT);
        if (c.contains("premium")) {
            return 1.35;
        }
        if (c.contains("class")) {
            return 1.15;
        }
        if (c.contains("standard")) {
            return 1.0;
        }
        return 1.05;
    }

    /** Tarif indicatif selon la catégorie du salon (Standard / Class / Premium). */
    public static double priceForCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return 35.0;
        }
        String c = category.trim().toLowerCase(Locale.ROOT);
        if (c.contains("premium")) {
            return 70.0;
        }
        if (c.contains("class")) {
            return 45.0;
        }
        if (c.contains("standard")) {
            return 25.0;
        }
        return 35.0;
    }

    @SuppressWarnings("deprecation")
    public static String formatMoney(Context context, double amount) {
        Locale loc;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            loc = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            loc = context.getResources().getConfiguration().locale;
        }
        if (loc == null) {
            loc = Locale.getDefault();
        }
        return NumberFormat.getCurrencyInstance(loc).format(amount);
    }
}
