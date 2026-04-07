package com.example.dabacut.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.models.Booking;

import java.util.*;

public class MyBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_my_bookings);

        RecyclerView recycler = findViewById(R.id.recyclerBookings);

        List<Booking> list = new ArrayList<>();
        list.add(new Booking("Ayoub", "0600", "Elite Barber", "Coupe", "10/04", "14:00"));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new BookingAdapter(list));
    }
}