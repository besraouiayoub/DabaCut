package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class OwnerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_owner_dashboard);

        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnBookings = findViewById(R.id.btnBookings);

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, CreateSalonProfileActivity.class)));

        btnBookings.setOnClickListener(v ->
                startActivity(new Intent(this, OwnerBookingsActivity.class)));
    }
}