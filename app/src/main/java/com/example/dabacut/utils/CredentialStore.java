package com.example.dabacut.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Mémorise le dernier couple identifiant / mot de passe (chiffré lorsque possible).
 */
public final class CredentialStore {

    private static final String PREF = "DabaCutCredentials";
    private static final String KEY_USER = "saved_username";
    private static final String KEY_PASS = "saved_password";

    private CredentialStore() {
    }

    private static SharedPreferences prefs(Context context) {
        Context app = context.getApplicationContext();
        try {
            MasterKey mk = new MasterKey.Builder(app)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    app,
                    PREF,
                    mk,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            return app.getSharedPreferences(PREF + "_plain", Context.MODE_PRIVATE);
        }
    }

    public static void save(Context context, String username, String password) {
        if (username == null || password == null) {
            return;
        }
        prefs(context).edit()
                .putString(KEY_USER, username.trim())
                .putString(KEY_PASS, password)
                .apply();
    }

    public static boolean hasSavedCredentials(Context context) {
        SharedPreferences p = prefs(context);
        String u = p.getString(KEY_USER, null);
        String pa = p.getString(KEY_PASS, null);
        return u != null && !u.isEmpty() && pa != null && !pa.isEmpty();
    }

    /** [0] = username, [1] = password (ne pas journaliser). */
    public static String[] readCredentials(Context context) {
        SharedPreferences p = prefs(context);
        return new String[]{p.getString(KEY_USER, ""), p.getString(KEY_PASS, "")};
    }

    /**
     * Efface les identifiants enregistrés. Utilise {@code commit()} pour que la persistance
     * soit terminée avant la suite (évite qu’au prochain lancement Splash ré-authentifie encore).
     */
    public static void clear(Context context) {
        Context app = context.getApplicationContext();
        try {
            prefs(context).edit().clear().commit();
        } catch (Exception ignored) {
        }
        try {
            app.getSharedPreferences(PREF + "_plain", Context.MODE_PRIVATE).edit().clear().commit();
        } catch (Exception ignored) {
        }
    }
}
