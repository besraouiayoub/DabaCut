package com.example.dabacut.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dabacut.models.User;

public class SessionManager {
    private static final String PREF_NAME = "DabaCutSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_LANG = "language";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_LANG, user.getLanguage());
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getUserDetails() {
        if (!isLoggedIn()) return null;
        return new User(
                pref.getString(KEY_NAME, null),
                pref.getString(KEY_PHONE, null),
                pref.getString(KEY_EMAIL, null),
                pref.getString(KEY_ROLE, null),
                pref.getString(KEY_LANG, null)
        );
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }
}