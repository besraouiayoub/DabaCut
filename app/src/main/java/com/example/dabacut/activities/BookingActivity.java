package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dabacut.R;
import com.example.dabacut.models.Booking;
import com.example.dabacut.utils.DataManager;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        String salonName = getIntent().getStringExtra("salonName");
        
        TextView tvSalonName = findViewById(R.id.tvSalonName);
        EditText etService = findViewById(R.id.etService);
        EditText etDate = findViewById(R.id.etDate);
        EditText etTime = findViewById(R.id.etTime);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        tvSalonName.setText(salonName);

        // simplified date/time picker logic for now
        etDate.setOnClickListener(v -> etDate.setText("25/05/2024"));
        etTime.setOnClickListener(v -> etTime.setText("14:30"));

        btnConfirm.setOnClickListener(v -> {
            String service = etService.getText().toString();
            String date = etDate.getText().toString();
            String time = etTime.getText().toString();

            if (service.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // In a real app, we'd get the user name/phone from SharedPreferences or Session
            Booking booking = new Booking("Client Test", "0600000000", salonName, service, date, time);
            DataManager.getInstance().addBooking(booking);

            Toast.makeText(this, "Réservation confirmée !", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, MyBookingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}