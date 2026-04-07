package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class LanguageSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_LANGUAGE = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        Button btnFr = findViewById(R.id.btnFrench);
        Button btnEn = findViewById(R.id.btnEnglish);
        Button btnAr = findViewById(R.id.btnArabic);

        btnFr.setOnClickListener(v -> openRoleSelection("fr"));
        btnEn.setOnClickListener(v -> openRoleSelection("en"));
        btnAr.setOnClickListener(v -> openRoleSelection("ar"));
    }

    private void openRoleSelection(String language) {
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.putExtra(EXTRA_LANGUAGE, language);
        startActivity(intent);
    }
}