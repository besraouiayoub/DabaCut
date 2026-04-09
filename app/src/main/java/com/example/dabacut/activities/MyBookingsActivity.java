package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Booking;
import com.example.dabacut.models.BookingStatus;
import com.example.dabacut.reminders.BookingReminderScheduler;
import com.example.dabacut.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends BaseClientActivity {

    private RecyclerView recyclerBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        recyclerBookings = findViewById(R.id.recyclerBookings);
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));

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
        SalonRepository r = SalonRepository.getInstance(this);

        r.getBookings(all -> {
            List<Booking> mine = filterMyBookings(all, username);
            recyclerBookings.setAdapter(new BookingAdapter(mine, this::onBookingClick, false));
        });
    }

    private void onBookingClick(Booking booking) {
        if (booking.getId() <= 0) {
            return;
        }
        SessionManager sm = new SessionManager(this);
        String username = sm.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        boolean canChange = BookingStatus.PENDING.equals(booking.getStatus())
                || BookingStatus.CONFIRMED.equals(booking.getStatus());
        if (!canChange) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.my_booking_cannot_cancel)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.my_booking_actions_title)
                .setItems(new CharSequence[]{
                        getString(R.string.my_booking_cancel),
                        getString(R.string.my_booking_reschedule)
                }, (d, which) -> {
                    if (which == 0) {
                        cancelBooking(booking, username.trim());
                    } else {
                        rescheduleBooking(booking);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void cancelBooking(Booking booking, String username) {
        SalonRepository r = SalonRepository.getInstance(this);
        r.cancelBookingForClient(booking.getId(), username, success -> {
            if (Boolean.TRUE.equals(success)) {
                BookingReminderScheduler.cancelForBooking(MyBookingsActivity.this, booking.getId());
                Toast.makeText(MyBookingsActivity.this,
                        R.string.my_booking_cancelled, Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });
    }

    private void rescheduleBooking(Booking booking) {
        SalonRepository r = SalonRepository.getInstance(this);
        r.getEstablishmentBySalonName(booking.getSalonName(), est -> {
            Intent i = new Intent(MyBookingsActivity.this, BookingActivity.class);
            i.putExtra("salonName", booking.getSalonName());
            if (est != null) {
                i.putExtra(BookingActivity.EXTRA_SALON_CATEGORY, est.getCategory());
                i.putExtra(BookingActivity.EXTRA_SALON_TYPE, est.getTypeSalon());
            }
            i.putExtra(BookingActivity.EXTRA_REPLACE_BOOKING_ID, booking.getId());
            startActivity(i);
        });
    }

    private List<Booking> filterMyBookings(List<Booking> all, String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String u = username.trim();
        List<Booking> mine = new ArrayList<>();
        for (Booking b : all) {
            if (b.getClientUsername() != null && b.getClientUsername().trim().equalsIgnoreCase(u)) {
                mine.add(b);
            }
        }
        return mine;
    }

    @Override
    protected int clientNavSelectedItemId() {
        return R.id.nav_client_bookings;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.my_bookings;
    }
}
