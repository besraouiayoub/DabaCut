package com.example.dabacut.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.models.Booking;

import java.util.*;

public class OwnerBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_owner_bookings);

        RecyclerView recycler = findViewById(R.id.recyclerOwnerBookings);

        List<Booking> list = new ArrayList<>();
        list.add(new Booking("Client", "0600", "Mon Salon", "Coupe", "12/04", "16:00"));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new BookingAdapter(list));
    }
}