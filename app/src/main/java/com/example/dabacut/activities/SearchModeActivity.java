package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class SearchModeActivity extends AppCompatActivity {

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mode);

        tvSummary = findViewById(R.id.tvSummary);
        Button btnList = findViewById(R.id.btnListMode);
        Button btnMap = findViewById(R.id.btnMapMode);

        showSummary();

        // ✅ MODE LISTE → ouvre la liste des salons
        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(this, EstablishmentListActivity.class);

            // passer les données (important pour filtre plus tard)
            intent.putExtra(RoleSelectionActivity.EXTRA_ROLE,
                    getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE));

            intent.putExtra(GenderSelectionActivity.EXTRA_GENDER,
                    getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER));

            intent.putExtra(CategorySelectionActivity.EXTRA_CATEGORY,
                    getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY));

            startActivity(intent);
        });

        // ❌ MAP pas encore
        btnMap.setOnClickListener(v ->
                Toast.makeText(this, "Carte bientôt disponible", Toast.LENGTH_SHORT).show());
    }

    private void showSummary() {
        String role = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);
        String gender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        String category = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);

        String summary = "Rôle : " + safe(role)
                + "\nGenre : " + safe(gender)
                + "\nCatégorie : " + safe(category);

        tvSummary.setText(summary);
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}