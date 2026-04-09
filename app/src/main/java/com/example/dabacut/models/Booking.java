package com.example.dabacut.models;

public class Booking {

    private final long id;
    private final String clientName;
    private final String clientPhone;
    private final String clientUsername;
    private final String salonName;
    private final String service;
    private final String date;
    private final String time;
    private final String notes;
    private final double priceTotal;
    private final String status;
    private final String paymentMethod;
    private final int durationMinutes;

    public Booking(long id, String clientName, String clientPhone, String clientUsername,
                   String salonName, String service, String date, String time,
                   String notes, double priceTotal, String status, String paymentMethod,
                   int durationMinutes) {
        this.id = id;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientUsername = clientUsername != null ? clientUsername : "";
        this.salonName = salonName;
        this.service = service;
        this.date = date;
        this.time = time;
        this.notes = notes != null ? notes : "";
        this.priceTotal = priceTotal;
        this.status = status != null ? status : BookingStatus.PENDING;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.AT_SALON;
        this.durationMinutes = durationMinutes;
    }

    public long getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getSalonName() {
        return salonName;
    }

    public String getService() {
        return service;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getNotes() {
        return notes;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
