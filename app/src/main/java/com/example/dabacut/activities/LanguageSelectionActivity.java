package com.example.dabacut.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_LANGUAGE = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        Button btnFr = findViewById(R.id.btnFrench);
        Button btnEn = findViewById(R.id.btnEnglish);
        Button btnAr = findViewById(R.id.btnArabic);

        btnFr.setOnClickListener(v -> setLocale("fr"));
        btnEn.setOnClickListener(v -> setLocale("en"));
        btnAr.setOnClickListener(v -> setLocale("ar"));
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.putExtra(EXTRA_LANGUAGE, lang);
        startActivity(intent);
    }
}