package com.example.dabacut.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;
import com.google.android.material.appbar.MaterialToolbar;

public abstract class BaseToolbarActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_shell_toolbar);
        FrameLayout shell = findViewById(R.id.shell_content);
        getLayoutInflater().inflate(layoutResID, shell, true);
        bindToolbar();
    }

    protected void bindToolbar() {
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
    }

    protected boolean shouldShowToolbarUp() {
        return true;
    }

    @StringRes
    protected int toolbarTitleRes() {
        return 0;
    }

    protected MaterialToolbar toolbar() {
        return findViewById(R.id.toolbar);
    }
}
