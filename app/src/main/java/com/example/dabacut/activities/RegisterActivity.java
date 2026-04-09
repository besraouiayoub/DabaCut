package com.example.dabacut.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dabacut.R;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.utils.CredentialStore;
import com.example.dabacut.utils.SessionManager;

public class RegisterActivity extends BaseToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        Button btnRegister = findViewById(R.id.btnRegister);

        String language = getIntent().getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE);
        String role = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirm = etPasswordConfirm.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(role)) {
                Toast.makeText(this, R.string.error_bad_credentials, Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager sessionManager = new SessionManager(this);
            SalonRepository.getInstance(this).registerUser(username, password, role, language,
                    result -> {
                        if (result == SalonRepository.RegisterResult.USERNAME_TAKEN) {
                            Toast.makeText(RegisterActivity.this, R.string.error_username_taken,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String lang = language != null ? language : "";
                        sessionManager.createLoginSession(username, role, lang);
                        CredentialStore.save(RegisterActivity.this, username, password);
                        LoginActivity.openHomeAfterAuth(RegisterActivity.this, role, lang, username);
                    });
        });
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.register_title;
    }
}
