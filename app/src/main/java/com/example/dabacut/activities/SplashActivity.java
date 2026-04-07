package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}