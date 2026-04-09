package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingAdapter;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Booking;
import com.example.dabacut.utils.ProfileHelper;
import com.example.dabacut.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OwnerDashboardActivity extends BaseOwnerActivity {

    private TextView tvBookingCount;
    private RecyclerView rvOwnerBookings;
    private SessionManager sessionManager;
    private SalonRepository repo;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_owner_dashboard);

        sessionManager = new SessionManager(this);
        repo = SalonRepository.getInstance(this);

        TextView tvRole = findViewById(R.id.tvOwnerRole);
        tvBookingCount = findViewById(R.id.tvBookingCount);
        rvOwnerBookings = findViewById(R.id.rvOwnerBookings);
        rvOwnerBookings.setLayoutManager(new LinearLayoutManager(this));

        tvRole.setText(ProfileHelper.roleLabel(this, sessionManager.getRole()));

        refreshDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {
        String username = sessionManager.getUsername();
        if (username == null) {
            tvBookingCount.setText(getString(R.string.owner_booking_count, 0));
            rvOwnerBookings.setAdapter(new BookingAdapter(new ArrayList<>()));
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
            repo.getBookingsForSalonName(filter, this::applyBookings);
        });
    }

    private void applyBookings(List<Booking> list) {
        tvBookingCount.setText(getString(R.string.owner_booking_count, list.size()));
        rvOwnerBookings.setAdapter(new BookingAdapter(list, booking -> {
            Intent i = new Intent(OwnerDashboardActivity.this, OwnerBookingDetailActivity.class);
            i.putExtra(OwnerBookingDetailActivity.EXTRA_BOOKING_ID, booking.getId());
            startActivity(i);
        }, true));
    }

    @Override
    protected boolean shouldShowToolbarUp() {
        return false;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.owner_dashboard;
    }

    @Override
    protected int ownerNavSelectedItemId() {
        return R.id.nav_owner_dashboard;
    }
}
