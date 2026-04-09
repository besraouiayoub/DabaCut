package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.dabacut.R;

public class GenderSelectionActivity extends BaseToolbarActivity {

    public static final String EXTRA_GENDER = "gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_selection);

        Button btnMale = findViewById(R.id.btnMale);
        Button btnFemale = findViewById(R.id.btnFemale);

        btnMale.setOnClickListener(v -> openCategorySelection("homme"));
        btnFemale.setOnClickListener(v -> openCategorySelection("femme"));
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.choose_type;
    }

    private void openCategorySelection(String gender) {
        Intent current = getIntent();

        Intent intent = new Intent(this, CategorySelectionActivity.class);
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE,
                current.getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE));
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE,
                current.getStringExtra(RoleSelectionActivity.EXTRA_ROLE));
        intent.putExtra(LoginActivity.EXTRA_USERNAME,
                current.getStringExtra(LoginActivity.EXTRA_USERNAME));
        intent.putExtra(EXTRA_GENDER, gender);

        startActivity(intent);
    }
}
