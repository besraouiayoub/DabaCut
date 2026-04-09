package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.User;
import com.example.dabacut.utils.CredentialStore;
import com.example.dabacut.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 900;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.dabacut.R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(this::routeAfterSplash, SPLASH_DELAY_MS);
    }

    private void routeAfterSplash() {
        if (sessionManager.isLoggedIn()) {
            openHomeForRole();
            finish();
            return;
        }

        if (CredentialStore.hasSavedCredentials(this)) {
            String[] c = CredentialStore.readCredentials(this);
            SalonRepository.getInstance(this).authenticate(c[0], c[1], new SalonRepository.OnAuthResult() {
                @Override
                public void onSuccess(String username, String storedRole, String storedLang) {
                    String lang = storedLang != null ? storedLang : "";
                    sessionManager.createLoginSession(username, storedRole, lang);
                    openHomeForRole();
                    finish();
                }

                @Override
                public void onFailure() {
                    CredentialStore.clear(SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
                    finish();
                }
            });
            return;
        }

        startActivity(new Intent(this, LanguageSelectionActivity.class));
        finish();
    }

    private void openHomeForRole() {
        String role = sessionManager.getRole();
        User u = sessionManager.getUserDetails();
        String language = u != null ? u.getLanguage() : null;
        String username = u != null ? u.getUsername() : null;

        Intent intent;
        if ("client".equals(role)) {
            intent = new Intent(SplashActivity.this, GenderSelectionActivity.class);
            if (language != null) {
                intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE, language);
            }
            if (role != null) {
                intent.putExtra(RoleSelectionActivity.EXTRA_ROLE, role);
            }
            if (username != null) {
                intent.putExtra(LoginActivity.EXTRA_USERNAME, username);
            }
        } else {
            intent = new Intent(SplashActivity.this, OwnerDashboardActivity.class);
        }
        startActivity(intent);
    }
}
