package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.dabacut.R;
import com.example.dabacut.utils.NavIntents;
import com.example.dabacut.utils.SessionManager;

public class SearchModeActivity extends BaseClientActivity {

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mode);

        SessionManager sessionManager = new SessionManager(this);
        String gender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        String category = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);
        if (gender != null && category != null) {
            sessionManager.saveSearchFilters(gender, category);
        }

        tvSummary = findViewById(R.id.tvSummary);
        Button btnList = findViewById(R.id.btnListMode);
        Button btnMap = findViewById(R.id.btnMapMode);

        showSummary();

        btnList.setOnClickListener(v -> {
            Intent intent = NavIntents.establishmentList(this);
            passExtras(intent);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = NavIntents.map(this);
            passExtras(intent);
            startActivity(intent);
        });
    }

    @Override
    protected int clientNavSelectedItemId() {
        return R.id.nav_client_home;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.search_mode;
    }

    private void passExtras(Intent intent) {
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE,
                getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE));
        intent.putExtra(GenderSelectionActivity.EXTRA_GENDER,
                getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER));
        intent.putExtra(CategorySelectionActivity.EXTRA_CATEGORY,
                getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY));
    }

    private void showSummary() {
        SessionManager sm = new SessionManager(this);
        String role = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);
        if (role == null && sm.getUserDetails() != null) {
            role = sm.getUserDetails().getRole();
        }
        String gender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        if (gender == null) {
            gender = sm.getSearchGender();
        }
        String category = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);
        if (category == null) {
            category = sm.getSearchCategory();
        }

        String summary = getString(R.string.role_label, safe(role))
                + "\n" + getString(R.string.gender_label, safe(gender))
                + "\n" + getString(R.string.category_label, safe(category));

        tvSummary.setText(summary);
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
