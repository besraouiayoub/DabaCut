package com.example.dabacut.models;

public class Booking {

    private String clientName;
    private String clientPhone;
    private String salonName;
    private String service;
    private String date;
    private String time;

    public Booking(String clientName, String clientPhone, String salonName,
                   String service, String date, String time) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.salonName = salonName;
        this.service = service;
        this.date = date;
        this.time = time;
    }

    public String getClientName() { return clientName; }
    public String getClientPhone() { return clientPhone; }
    public String getSalonName() { return salonName; }
    public String getService() { return service; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}