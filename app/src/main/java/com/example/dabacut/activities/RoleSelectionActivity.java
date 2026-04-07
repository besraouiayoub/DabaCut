package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class RoleSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        String language = getIntent().getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE);

        Button btnClient = findViewById(R.id.btnClient);
        Button btnSalonHomme = findViewById(R.id.btnSalonHomme);
        Button btnSalonFilles = findViewById(R.id.btnSalonFilles);

        btnClient.setOnClickListener(v -> openLogin(language, "client"));
        btnSalonHomme.setOnClickListener(v -> openLogin(language, "salon_homme"));
        btnSalonFilles.setOnClickListener(v -> openLogin(language, "salon_filles"));
    }

    private void openLogin(String language, String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE, language);
        intent.putExtra(EXTRA_ROLE, role);
        startActivity(intent);
    }
}