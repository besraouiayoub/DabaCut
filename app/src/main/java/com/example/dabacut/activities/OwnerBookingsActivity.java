package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Booking;
import com.example.dabacut.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OwnerBookingsActivity extends BaseOwnerActivity {

    private RecyclerView recyclerOwnerBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_bookings);

        recyclerOwnerBookings = findViewById(R.id.recyclerOwnerBookings);
        recyclerOwnerBookings.setLayoutManager(new LinearLayoutManager(this));

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        SessionManager sessionManager = new SessionManager(this);
        String username = sessionManager.getUsername();
        SalonRepository repo = SalonRepository.getInstance(this);

        if (username == null) {
            recyclerOwnerBookings.setAdapter(new BookingAdapter(new ArrayList<>()));
            return;
        }

        repo.getSalonNameForOwner(username, salonName -> {
            if (salonName != null && !salonName.trim().isEmpty()) {
                sessionManager.setOwnerSalonName(salonName.trim());
            }
            String filter = salonName != null && !salonName.trim().isEmpty()
                    ? salonName.trim()
                    : sessionManager.getOwnerSalonName();
            if (filter == null) {
                filter = "";
            }
            repo.getBookingsForSalonName(filter, this::showBookings);
        });
    }

    private void showBookings(List<Booking> myBookings) {
        recyclerOwnerBookings.setAdapter(new BookingAdapter(myBookings, booking -> {
            Intent i = new Intent(OwnerBookingsActivity.this, OwnerBookingDetailActivity.class);
            i.putExtra(OwnerBookingDetailActivity.EXTRA_BOOKING_ID, booking.getId());
            startActivity(i);
        }, true));
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.owner_bookings;
    }

    @Override
    protected int ownerNavSelectedItemId() {
        return R.id.nav_owner_bookings;
    }
}
