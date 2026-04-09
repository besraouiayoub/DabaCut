package com.example.dabacut.models;

public class User {

    private final String username;
    private final String role;
    private final String language;

    public User(String username, String role, String language) {
        this.username = username;
        this.role = role;
        this.language = language;
    }

    /** Affichage / réservation : nom affiché au salon. */
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return "";
    }

    public String getEmail() {
        return null;
    }

    public String getRole() {
        return role;
    }

    public String getLanguage() {
        return language;
    }
}
