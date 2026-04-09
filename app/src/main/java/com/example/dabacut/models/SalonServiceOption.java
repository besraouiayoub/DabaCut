package com.example.dabacut.models;

public final class SalonServiceOption {

    public final String id;
    public final int nameResId;
    public final double unitPrice;
    /** Durée indicative en minutes (affichage / total RDV). */
    public final int durationMinutes;

    public SalonServiceOption(String id, int nameResId, double unitPrice, int durationMinutes) {
        this.id = id;
        this.nameResId = nameResId;
        this.unitPrice = unitPrice;
        this.durationMinutes = durationMinutes;
    }
}
