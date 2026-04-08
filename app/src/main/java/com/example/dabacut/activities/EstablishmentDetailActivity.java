package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;

public class EstablishmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_detail);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvDesc = findViewById(R.id.tvDesc);
        
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("desc");
        String address = getIntent().getStringExtra("address");
        String phone = getIntent().getStringExtra("phone");

        tvName.setText(name);
        
        String fullDescription = description + "\n\n" + 
                getString(R.string.address_label, address) + "\n" + 
                getString(R.string.contact_label, phone);
        
        tvDesc.setText(fullDescription);

        Button btnBook = findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("salonName", name);
            startActivity(intent);
        });
    }
}