package com.example.dabacut.activities;

import android.content.Intent;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;
import com.example.dabacut.utils.NavIntents;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseClientActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_shell_bottom);
        FrameLayout shell = findViewById(R.id.shell_content);
        getLayoutInflater().inflate(layoutResID, shell, true);
        bindToolbarAndNav();
    }

    protected void bindToolbarAndNav() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean up = shouldShowToolbarUp();
        if (up) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        } else {
            toolbar.setNavigationIcon(null);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(up);
            getSupportActionBar().setDisplayShowHomeEnabled(up);
        }
        int title = toolbarTitleRes();
        if (title != 0) {
            toolbar.setTitle(title);
        }

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.getMenu().clear();
        nav.inflateMenu(R.menu.menu_client_nav);
        nav.setSelectedItemId(clientNavSelectedItemId());
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == clientNavSelectedItemId()) {
                return true;
            }
            Intent intent = null;
            if (id == R.id.nav_client_home) {
                intent = NavIntents.searchMode(this);
            } else if (id == R.id.nav_client_salons) {
                intent = NavIntents.establishmentList(this);
            } else if (id == R.id.nav_client_map) {
                intent = NavIntents.map(this);
            } else if (id == R.id.nav_client_bookings) {
                intent = new Intent(this, MyBookingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            } else if (id == R.id.nav_client_profile) {
                intent = new Intent(this, ClientProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        });
    }

    protected boolean shouldShowToolbarUp() {
        return true;
    }

    @StringRes
    protected int toolbarTitleRes() {
        return 0;
    }

    protected abstract int clientNavSelectedItemId();
}
