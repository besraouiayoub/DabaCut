package com.example.dabacut.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dabacut.models.User;

public class SessionManager {
    private static final String PREF_NAME = "DabaCutSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_LANG = "language";
    private static final String KEY_SEARCH_GENDER = "search_gender";
    private static final String KEY_SEARCH_CATEGORY = "search_category";
    private static final String KEY_OWNER_SALON_NAME = "owner_salon_name";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createLoginSession(String username, String role, String language) {
        pref.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .putString(KEY_LANG, language != null ? language : "")
                .commit();
    }

    public void createLoginSession(User user) {
        createLoginSession(user.getUsername(), user.getRole(), user.getLanguage());
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public User getUserDetails() {
        if (!isLoggedIn()) return null;
        return new User(
                pref.getString(KEY_USERNAME, null),
                pref.getString(KEY_ROLE, null),
                pref.getString(KEY_LANG, null)
        );
    }

    public void logoutUser() {
        pref.edit().clear().commit();
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public void saveSearchFilters(String gender, String category) {
        SharedPreferences.Editor e = pref.edit();
        if (gender != null) {
            e.putString(KEY_SEARCH_GENDER, gender);
        }
        if (category != null) {
            e.putString(KEY_SEARCH_CATEGORY, category);
        }
        e.commit();
    }

    public String getSearchGender() {
        return pref.getString(KEY_SEARCH_GENDER, null);
    }

    public String getSearchCategory() {
        return pref.getString(KEY_SEARCH_CATEGORY, null);
    }

    public void setOwnerSalonName(String salonName) {
        if (salonName != null) {
            pref.edit().putString(KEY_OWNER_SALON_NAME, salonName.trim()).commit();
        }
    }

    public String getOwnerSalonName() {
        return pref.getString(KEY_OWNER_SALON_NAME, null);
    }
}
