package com.example.dabacut.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.example.dabacut.R;
import com.example.dabacut.activities.LanguageSelectionActivity;
import com.example.dabacut.models.User;

public final class ProfileHelper {

    private ProfileHelper() {
    }

    public static String roleLabel(Context context, String role) {
        if ("client".equals(role)) {
            return context.getString(R.string.role_label_client);
        }
        if ("salon_homme".equals(role)) {
            return context.getString(R.string.role_label_owner_men);
        }
        if ("salon_filles".equals(role)) {
            return context.getString(R.string.role_label_owner_women);
        }
        return role != null ? role : "—";
    }

    public static void bindAccountSection(Activity activity) {
        SessionManager sm = new SessionManager(activity);
        User u = sm.getUserDetails();

        TextView tvRole = activity.findViewById(R.id.tvProfileRole);
        TextView tvName = activity.findViewById(R.id.tvProfileName);
        Button btnLogout = activity.findViewById(R.id.btnLogout);

        if (u == null) {
            if (tvRole != null) tvRole.setText("—");
            if (tvName != null) tvName.setText("—");
        } else {
            if (tvRole != null) {
                tvRole.setText(roleLabel(activity, u.getRole()));
            }
            if (tvName != null) {
                tvName.setText(u.getUsername() != null ? u.getUsername() : "—");
            }
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sm.logoutUser();
                CredentialStore.clear(activity);
                Intent intent = new Intent(activity, LanguageSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            });
        }
    }
}
