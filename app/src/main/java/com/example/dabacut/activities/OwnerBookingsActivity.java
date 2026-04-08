package com.example.dabacut.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.models.Booking;
import com.example.dabacut.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

public class OwnerBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_bookings);

        // In a real app, this would be the ID of the logged-in owner's salon
        // For now, we take the first salon if available, or a default one
        String mySalonName = "Elite Barber"; 
        if (!DataManager.getInstance().getEstablishments().isEmpty()) {
            mySalonName = DataManager.getInstance().getEstablishments().get(0).getName();
        }

        RecyclerView recycler = findViewById(R.id.recyclerOwnerBookings);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<Booking> allBookings = DataManager.getInstance().getBookings();
        List<Booking> myBookings = new ArrayList<>();

        for (Booking b : allBookings) {
            if (b.getSalonName().equalsIgnoreCase(mySalonName)) {
                myBookings.add(b);
            }
        }

        recycler.setAdapter(new BookingAdapter(myBookings));
    }
}