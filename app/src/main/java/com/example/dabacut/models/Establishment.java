package com.example.dabacut.models;

public class Establishment {

    private String name;
    private String typeSalon; // salon_homme / salon_filles
    private String category;
    private String address;
    private String phone;
    private String description;

    public Establishment(String name, String typeSalon, String category,
                         String address, String phone, String description) {
        this.name = name;
        this.typeSalon = typeSalon;
        this.category = category;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    public String getName() { return name; }
    public String getTypeSalon() { return typeSalon; }
    public String getCategory() { return category; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
}