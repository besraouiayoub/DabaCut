package com.example.dabacut.models;

public class Establishment {

    private long id = -1L;
    private String name;
    private String typeSalon;
    private String category;
    private String address;
    private String phone;
    private String description;
    private double latitude;
    private double longitude;
    private String ownerUsername;
    private String weekdayOpenFrom = "09:00";
    private String weekdayOpenTo = "19:00";
    private boolean openSaturday = true;
    private String saturdayOpenFrom = "09:00";
    private String saturdayOpenTo = "18:00";
    private boolean openSunday;
    private String sundayOpenFrom = "10:00";
    private String sundayOpenTo = "16:00";

    public Establishment(String name, String typeSalon, String category,
                         String address, String phone, String description) {
        this.name = name;
        this.typeSalon = typeSalon;
        this.category = category;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeSalon() {
        return typeSalon;
    }

    public void setTypeSalon(String typeSalon) {
        this.typeSalon = typeSalon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername != null ? ownerUsername : "";
    }

    public String getWeekdayOpenFrom() {
        return weekdayOpenFrom;
    }

    public void setWeekdayOpenFrom(String weekdayOpenFrom) {
        this.weekdayOpenFrom = weekdayOpenFrom;
    }

    public String getWeekdayOpenTo() {
        return weekdayOpenTo;
    }

    public void setWeekdayOpenTo(String weekdayOpenTo) {
        this.weekdayOpenTo = weekdayOpenTo;
    }

    public boolean isOpenSaturday() {
        return openSaturday;
    }

    public void setOpenSaturday(boolean openSaturday) {
        this.openSaturday = openSaturday;
    }

    public String getSaturdayOpenFrom() {
        return saturdayOpenFrom;
    }

    public void setSaturdayOpenFrom(String saturdayOpenFrom) {
        this.saturdayOpenFrom = saturdayOpenFrom;
    }

    public String getSaturdayOpenTo() {
        return saturdayOpenTo;
    }

    public void setSaturdayOpenTo(String saturdayOpenTo) {
        this.saturdayOpenTo = saturdayOpenTo;
    }

    public boolean isOpenSunday() {
        return openSunday;
    }

    public void setOpenSunday(boolean openSunday) {
        this.openSunday = openSunday;
    }

    public String getSundayOpenFrom() {
        return sundayOpenFrom;
    }

    public void setSundayOpenFrom(String sundayOpenFrom) {
        this.sundayOpenFrom = sundayOpenFrom;
    }

    public String getSundayOpenTo() {
        return sundayOpenTo;
    }

    public void setSundayOpenTo(String sundayOpenTo) {
        this.sundayOpenTo = sundayOpenTo;
    }
}
