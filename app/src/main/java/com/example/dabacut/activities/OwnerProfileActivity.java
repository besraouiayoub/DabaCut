package com.example.dabacut.activities;

import android.os.Bundle;

import com.example.dabacut.R;
import com.example.dabacut.utils.ProfileHelper;

public class OwnerProfileActivity extends BaseOwnerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ProfileHelper.bindAccountSection(this);
    }

    @Override
    protected int ownerNavSelectedItemId() {
        return R.id.nav_owner_account;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.nav_my_account;
    }
}
