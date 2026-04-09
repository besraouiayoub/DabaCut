package com.example.dabacut.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dabacut.R;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Booking;
import com.example.dabacut.models.BookingStatus;
import com.example.dabacut.reminders.BookingReminderScheduler;
import com.example.dabacut.utils.BookingPriceHelper;
import com.example.dabacut.utils.BookingUi;

public class OwnerBookingDetailActivity extends BaseToolbarActivity {

    public static final String EXTRA_BOOKING_ID = "booking_id";
    private static final int REQ_POST_NOTIFICATIONS = 5101;

    private Booking loadedBooking;
    private LinearLayout layoutActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_booking_detail);

        long id = getIntent().getLongExtra(EXTRA_BOOKING_ID, -1L);
        if (id < 0) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvDetailSalon = findViewById(R.id.tvDetailSalon);
        TextView tvDetailClient = findViewById(R.id.tvDetailClient);
        TextView tvDetailPhone = findViewById(R.id.tvDetailPhone);
        TextView tvDetailService = findViewById(R.id.tvDetailService);
        TextView tvDetailDateTime = findViewById(R.id.tvDetailDateTime);
        TextView tvDetailStatus = findViewById(R.id.tvDetailStatus);
        TextView tvDetailPayment = findViewById(R.id.tvDetailPayment);
        TextView tvDetailDuration = findViewById(R.id.tvDetailDuration);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDetailNotes = findViewById(R.id.tvDetailNotes);
        layoutActions = findViewById(R.id.layoutOwnerActions);
        Button btnAccept = findViewById(R.id.btnAcceptBooking);
        Button btnReject = findViewById(R.id.btnRejectBooking);

        SalonRepository.getInstance(this).getBookingById(id, booking -> {
            if (booking == null) {
                Toast.makeText(OwnerBookingDetailActivity.this, R.string.fill_all_fields,
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            loadedBooking = booking;
            tvDetailSalon.setText(booking.getSalonName());

            String user = booking.getClientUsername();
            String name = booking.getClientName();
            if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(name)) {
                tvDetailClient.setText(getString(R.string.owner_detail_client_line, user, name));
            } else if (!TextUtils.isEmpty(user)) {
                tvDetailClient.setText(getString(R.string.owner_detail_client_user, user));
            } else if (!TextUtils.isEmpty(name)) {
                tvDetailClient.setText(getString(R.string.owner_detail_client_name, name));
            } else {
                tvDetailClient.setText("");
            }

            tvDetailPhone.setText(getString(R.string.owner_detail_phone,
                    !TextUtils.isEmpty(booking.getClientPhone()) ? booking.getClientPhone() : "—"));
            tvDetailService.setText(booking.getService());
            tvDetailDateTime.setText(booking.getDate() + " · " + booking.getTime());
            tvDetailStatus.setText(getString(R.string.owner_detail_status,
                    BookingUi.statusLabel(OwnerBookingDetailActivity.this, booking.getStatus())));
            tvDetailPayment.setText(getString(R.string.owner_detail_payment,
                    BookingUi.paymentLabel(OwnerBookingDetailActivity.this, booking.getPaymentMethod())));
            tvDetailDuration.setText(getString(R.string.owner_detail_duration, booking.getDurationMinutes()));
            tvDetailPrice.setText(getString(R.string.owner_detail_price,
                    BookingPriceHelper.formatMoney(OwnerBookingDetailActivity.this, booking.getPriceTotal())));
            if (!TextUtils.isEmpty(booking.getNotes())) {
                tvDetailNotes.setText(getString(R.string.owner_detail_notes, booking.getNotes()));
                tvDetailNotes.setVisibility(View.VISIBLE);
            } else {
                tvDetailNotes.setVisibility(View.GONE);
            }

            boolean canAct = BookingStatus.PENDING.equals(booking.getStatus());
            layoutActions.setVisibility(canAct ? View.VISIBLE : View.GONE);
            btnAccept.setOnClickListener(v -> acceptWithOptionalNotifPermission(booking));
            btnReject.setOnClickListener(v -> rejectBooking(booking));
        });
    }

    private void acceptWithOptionalNotifPermission(Booking booking) {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQ_POST_NOTIFICATIONS);
            return;
        }
        confirmAccept(booking);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_POST_NOTIFICATIONS && loadedBooking != null) {
            confirmAccept(loadedBooking);
        }
    }

    private void confirmAccept(@NonNull Booking booking) {
        SalonRepository repo = SalonRepository.getInstance(this);
        repo.setBookingStatus(booking.getId(), BookingStatus.CONFIRMED, () -> {
            BookingReminderScheduler.scheduleAfterConfirmation(
                    OwnerBookingDetailActivity.this,
                    booking.getId(),
                    booking.getDate(),
                    booking.getTime(),
                    booking.getSalonName());
            Toast.makeText(OwnerBookingDetailActivity.this, R.string.booking_status_confirmed,
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void rejectBooking(Booking booking) {
        SalonRepository repo = SalonRepository.getInstance(this);
        repo.setBookingStatus(booking.getId(), BookingStatus.REJECTED, () -> {
            BookingReminderScheduler.cancelForBooking(OwnerBookingDetailActivity.this, booking.getId());
            Toast.makeText(OwnerBookingDetailActivity.this, R.string.booking_status_rejected,
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.owner_booking_detail_title;
    }
}
