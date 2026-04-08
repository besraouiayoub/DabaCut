package com.example.dabacut.models;

public class User {
    private String name;
    private String phone;
    private String email;
    private String role; // client, salon_homme, salon_filles
    private String language;

    public User(String name, String phone, String email, String role, String language) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.language = language;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}