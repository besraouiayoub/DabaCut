package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class EstablishmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_establishment_detail);

        TextView name = findViewById(R.id.tvName);
        TextView desc = findViewById(R.id.tvDesc);
        Button btn = findViewById(R.id.btnBook);

        name.setText(getIntent().getStringExtra("name"));
        desc.setText(getIntent().getStringExtra("desc"));

        btn.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingActivity.class));
        });
    }
}