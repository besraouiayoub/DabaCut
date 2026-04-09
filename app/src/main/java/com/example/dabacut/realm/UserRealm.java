package com.example.dabacut.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserRealm extends RealmObject {

    @PrimaryKey
    private String username;
    private String password;
    private String role;
    private String language;

    public UserRealm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
