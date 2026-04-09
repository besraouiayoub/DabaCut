package com.example.dabacut.utils;

import android.content.Context;
import android.content.Intent;

import com.example.dabacut.activities.CategorySelectionActivity;
import com.example.dabacut.activities.EstablishmentListActivity;
import com.example.dabacut.activities.GenderSelectionActivity;
import com.example.dabacut.activities.MapActivity;
import com.example.dabacut.activities.RoleSelectionActivity;
import com.example.dabacut.activities.SearchModeActivity;

public final class NavIntents {

    private NavIntents() {
    }

    private static void attachSearchExtras(Context context, Intent intent) {
        SessionManager sm = new SessionManager(context);
        String gender = sm.getSearchGender();
        String category = sm.getSearchCategory();
        intent.putExtra(GenderSelectionActivity.EXTRA_GENDER, gender);
        intent.putExtra(CategorySelectionActivity.EXTRA_CATEGORY, category);
        String role = sm.getRole();
        if (role != null) {
            intent.putExtra(RoleSelectionActivity.EXTRA_ROLE, role);
        }
    }

    public static Intent searchMode(Context context) {
        Intent intent = new Intent(context, SearchModeActivity.class);
        attachSearchExtras(context, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    public static Intent establishmentList(Context context) {
        Intent intent = new Intent(context, EstablishmentListActivity.class);
        attachSearchExtras(context, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    public static Intent map(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        attachSearchExtras(context, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }
}
