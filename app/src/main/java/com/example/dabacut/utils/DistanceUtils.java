package com.example.dabacut.utils;

public final class DistanceUtils {

    private DistanceUtils() {
    }

    /** Distance du grand cercle en km. */
    public static double kmBetween(double lat1, double lng1, double lat2, double lng2) {
        final double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
