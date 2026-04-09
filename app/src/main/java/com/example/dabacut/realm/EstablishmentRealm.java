package com.example.dabacut.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EstablishmentRealm extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private String typeSalon;
    private String category;
    private String address;
    private String phone;
    private String description;
    private double latitude;
    private double longitude;
    /** Username du coiffeur propriétaire ; vide pour les salons démo. */
    private String ownerUsername;
    private String weekdayOpenFrom;
    private String weekdayOpenTo;
    private boolean openSaturday;
    private String saturdayOpenFrom;
    private String saturdayOpenTo;
    private boolean openSunday;
    private String sundayOpenFrom;
    private String sundayOpenTo;

    public EstablishmentRealm() {
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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
