package com.example.dabacut.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class CreateSalonProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_create_salon_profile);

        Button btn = findViewById(R.id.btnSave);

        btn.setOnClickListener(v ->
                Toast.makeText(this, "Profil enregistré", Toast.LENGTH_SHORT).show());
    }
}