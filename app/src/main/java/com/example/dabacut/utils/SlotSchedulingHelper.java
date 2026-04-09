package com.example.dabacut.utils;

import com.example.dabacut.models.Establishment;

import java.util.Calendar;

/**
 * Filtre les créneaux selon les horaires d’ouverture du salon.
 */
public final class SlotSchedulingHelper {

    private SlotSchedulingHelper() {
    }

    public static SalonBusinessHours hoursFromEstablishment(Establishment e) {
        if (e == null) {
            return SalonBusinessHours.defaults();
        }
        return new SalonBusinessHours(
                nz(e.getWeekdayOpenFrom(), "09:00"),
                nz(e.getWeekdayOpenTo(), "19:00"),
                e.isOpenSaturday(),
                nz(e.getSaturdayOpenFrom(), "09:00"),
                nz(e.getSaturdayOpenTo(), "18:00"),
                e.isOpenSunday(),
                nz(e.getSundayOpenFrom(), "10:00"),
                nz(e.getSundayOpenTo(), "16:00")
        );
    }

    private static String nz(String s, String d) {
        return s != null && !s.trim().isEmpty() ? s.trim() : d;
    }

    public static boolean isSlotAllowed(Calendar dayCal, String timeHhMm, SalonBusinessHours h) {
        String[] w = windowForDay(dayCal, h);
        if (w == null) {
            return false;
        }
        return timeWithin(timeHhMm, w[0], w[1]);
    }

    /** [from, to] inclus pour les minutes (ex. 09:00–19:00). */
    private static boolean timeWithin(String timeHhMm, String from, String to) {
        int t = toMinutes(timeHhMm);
        int f = toMinutes(from);
        int x = toMinutes(to);
        return t >= f && t <= x;
    }

    private static int toMinutes(String hhMm) {
        if (hhMm == null || !hhMm.contains(":")) {
            return 0;
        }
        String[] p = hhMm.trim().split(":");
        int h = Integer.parseInt(p[0]);
        int m = Integer.parseInt(p[1]);
        return h * 60 + m;
    }

    /** Retourne { open, close } ou null si fermé ce jour-là. */
    private static String[] windowForDay(Calendar cal, SalonBusinessHours h) {
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        if (dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY) {
            return new String[]{h.weekdayOpenFrom, h.weekdayOpenTo};
        }
        if (dow == Calendar.SATURDAY) {
            if (!h.openSaturday) {
                return null;
            }
            return new String[]{h.saturdayOpenFrom, h.saturdayOpenTo};
        }
        if (dow == Calendar.SUNDAY) {
            if (!h.openSunday) {
                return null;
            }
            return new String[]{h.sundayOpenFrom, h.sundayOpenTo};
        }
        return null;
    }

    public static final class SalonBusinessHours {
        public final String weekdayOpenFrom;
        public final String weekdayOpenTo;
        public final boolean openSaturday;
        public final String saturdayOpenFrom;
        public final String saturdayOpenTo;
        public final boolean openSunday;
        public final String sundayOpenFrom;
        public final String sundayOpenTo;

        public SalonBusinessHours(String weekdayOpenFrom, String weekdayOpenTo,
                                  boolean openSaturday, String saturdayOpenFrom, String saturdayOpenTo,
                                  boolean openSunday, String sundayOpenFrom, String sundayOpenTo) {
            this.weekdayOpenFrom = weekdayOpenFrom;
            this.weekdayOpenTo = weekdayOpenTo;
            this.openSaturday = openSaturday;
            this.saturdayOpenFrom = saturdayOpenFrom;
            this.saturdayOpenTo = saturdayOpenTo;
            this.openSunday = openSunday;
            this.sundayOpenFrom = sundayOpenFrom;
            this.sundayOpenTo = sundayOpenTo;
        }

        public static SalonBusinessHours defaults() {
            return new SalonBusinessHours("09:00", "19:00", true,
                    "09:00", "18:00", false, "10:00", "16:00");
        }
    }
}
