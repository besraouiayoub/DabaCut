package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.dabacut.R;

public class CategorySelectionActivity extends BaseToolbarActivity {

    public static final String EXTRA_CATEGORY = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        Button btnStandard = findViewById(R.id.btnStandard);
        Button btnClass = findViewById(R.id.btnClass);
        Button btnPremium = findViewById(R.id.btnPremium);

        btnStandard.setOnClickListener(v -> openSearchMode("standard"));
        btnClass.setOnClickListener(v -> openSearchMode("class"));
        btnPremium.setOnClickListener(v -> openSearchMode("premium"));
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.choose_category;
    }

    private void openSearchMode(String category) {
        Intent current = getIntent();

        Intent intent = new Intent(this, SearchModeActivity.class);
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE,
                current.getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE));
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE,
                current.getStringExtra(RoleSelectionActivity.EXTRA_ROLE));
        intent.putExtra(LoginActivity.EXTRA_USERNAME,
                current.getStringExtra(LoginActivity.EXTRA_USERNAME));
        intent.putExtra(GenderSelectionActivity.EXTRA_GENDER,
                current.getStringExtra(GenderSelectionActivity.EXTRA_GENDER));
        intent.putExtra(EXTRA_CATEGORY, category);

        startActivity(intent);
    }
}
