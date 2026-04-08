package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1800;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.dabacut.R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getRole();
                if ("client".equals(role)) {
                    intent = new Intent(SplashActivity.this, GenderSelectionActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, OwnerDashboardActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}