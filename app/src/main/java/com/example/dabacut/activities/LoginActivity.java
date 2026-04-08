package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;
import com.example.dabacut.models.User;
import com.example.dabacut.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "email";

    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        Button btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> handleContinue());
    }

    private void handleContinue() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        String language = getIntent().getStringExtra(LanguageSelectionActivity.EXTRA_LANGUAGE);
        String role = getIntent().getStringExtra(RoleSelectionActivity.EXTRA_ROLE);

        // Save session
        User user = new User(name, phone, email, role, language);
        sessionManager.createLoginSession(user);

        Intent intent;
        if ("client".equals(role)) {
            intent = new Intent(this, GenderSelectionActivity.class);
        } else {
            intent = new Intent(this, OwnerDashboardActivity.class);
        }

        // We still pass extras for legacy support in some activities if needed, 
        // but prefer SessionManager
        intent.putExtra(LanguageSelectionActivity.EXTRA_LANGUAGE, language);
        intent.putExtra(RoleSelectionActivity.EXTRA_ROLE, role);
        startActivity(intent);
        finish();
    }
}