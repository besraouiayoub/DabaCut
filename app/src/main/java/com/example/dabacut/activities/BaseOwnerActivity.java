package com.example.dabacut.activities;

import android.content.Intent;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseOwnerActivity extends AppCompatActivity {

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
        nav.inflateMenu(R.menu.menu_owner_nav);
        nav.setSelectedItemId(ownerNavSelectedItemId());
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == ownerNavSelectedItemId()) {
                return true;
            }
            Intent intent = null;
            if (id == R.id.nav_owner_dashboard) {
                intent = new Intent(this, OwnerDashboardActivity.class);
            } else if (id == R.id.nav_owner_profile) {
                intent = new Intent(this, CreateSalonProfileActivity.class);
            } else if (id == R.id.nav_owner_bookings) {
                intent = new Intent(this, OwnerBookingsActivity.class);
            } else if (id == R.id.nav_owner_account) {
                intent = new Intent(this, OwnerProfileActivity.class);
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

    protected abstract int ownerNavSelectedItemId();
}
