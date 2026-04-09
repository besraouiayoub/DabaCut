package com.example.dabacut.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dabacut.R;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.utils.CredentialStore;
import com.example.dabacut.utils.SessionManager;

public class LoginActivity extends BaseToolbarActivity {

    public static final String EXTRA_USERNAME = "username";

    private EditText etUsername;
    private EditText etPassword;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> openRegister());

        if (CredentialStore.hasSavedCredentials(this)) {
            String[] c = CredentialStore.readCredentials(this);
            etUsername.setText(c[0]);
            etPassword.setText(c[1]);
        }
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.login_title;
    }

    private void openRegister() {
        String language = getIntent().getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE);
        String role = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE, language);
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE, role);
        startActivity(intent);
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        final String language = getIntent().getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE);
        final String selectedRole = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);

        SalonRepository.getInstance(this).authenticate(username, password, new SalonRepository.OnAuthResult() {
            @Override
            public void onSuccess(String u, String storedRole, String storedLang) {
                if (!TextUtils.isEmpty(selectedRole) && storedRole != null
                        && !selectedRole.equals(storedRole)) {
                    Toast.makeText(LoginActivity.this, R.string.error_role_mismatch, Toast.LENGTH_SHORT).show();
                    return;
                }
                String useRole = storedRole != null ? storedRole : selectedRole;
                String lang = !TextUtils.isEmpty(language) ? language
                        : (storedLang != null ? storedLang : "");
                sessionManager.createLoginSession(u, useRole, lang);
                CredentialStore.save(LoginActivity.this, u, etPassword.getText().toString());
                openHomeAfterAuth(LoginActivity.this, useRole, lang, u);
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginActivity.this, R.string.error_bad_credentials, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Après login ou inscription : ouvre le parcours client ou le tableau de bord propriétaire. */
    public static void openHomeAfterAuth(Activity activity, String role, String language, String username) {
        Intent intent;
        if ("client".equals(role)) {
            intent = new Intent(activity, GenderSelectionActivity.class);
        } else {
            intent = new Intent(activity, OwnerDashboardActivity.class);
        }
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE, language);
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE, role);
        intent.putExtra(EXTRA_USERNAME, username);
        activity.startActivity(intent);
        activity.finish();
    }
}
