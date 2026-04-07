package com.example.dabacut.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_booking);

        Button btn = findViewById(R.id.btnConfirm);

        btn.setOnClickListener(v ->
                Toast.makeText(this, "Réservation confirmée", Toast.LENGTH_SHORT).show());
    }
}