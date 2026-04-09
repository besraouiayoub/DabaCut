package com.example.dabacut.models;

public final class BookingStatus {
    public static final String PENDING = "pending";
    public static final String CONFIRMED = "confirmed";
    public static final String REJECTED = "rejected";
    public static final String CANCELLED = "cancelled";

    private BookingStatus() {
    }

    public static boolean occupiesSlot(String status) {
        return PENDING.equals(status) || CONFIRMED.equals(status);
    }
}
