package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.BookingServicePickerAdapter;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Booking;
import com.example.dabacut.models.BookingStatus;
import com.example.dabacut.models.PaymentMethod;
import com.example.dabacut.models.SalonServiceOption;
import com.example.dabacut.models.User;
import com.example.dabacut.reminders.BookingReminderScheduler;
import com.example.dabacut.utils.BookingPriceHelper;
import com.example.dabacut.utils.BookingUi;
import com.example.dabacut.utils.SalonServicesCatalog;
import com.example.dabacut.utils.SessionManager;

import java.util.List;

public class BookingActivity extends BaseToolbarActivity {

    public static final String EXTRA_SALON_CATEGORY = "salon_category";
    public static final String EXTRA_SALON_TYPE = "salon_type";
    /** Si &gt; 0, cette réservation est annulée après succès (reprogrammation). */
    public static final String EXTRA_REPLACE_BOOKING_ID = "replace_booking_id";

    private TextView tvBookingDate;
    private TextView tvBookingTime;
    private TextView tvBookingPrice;
    private TextView tvBookingDuration;
    private RadioGroup rgPayment;
    private double priceTotal;
    private String assignedDate;
    private String assignedTime;
    private String salonName;
    private List<SalonServiceOption> serviceOptions;
    private int[] quantities;
    private EditText etNote;
    private long replaceBookingId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        salonName = getIntent().getStringExtra("salonName");
        replaceBookingId = getIntent().getLongExtra(EXTRA_REPLACE_BOOKING_ID, -1L);
        String category = getIntent().getStringExtra(EXTRA_SALON_CATEGORY);
        if (category == null) {
            category = getIntent().getStringExtra("category");
        }
        String typeSalon = getIntent().getStringExtra(EXTRA_SALON_TYPE);
        if (typeSalon == null) {
            typeSalon = getIntent().getStringExtra("type");
        }

        TextView tvSalonName = findViewById(R.id.tvSalonName);
        etNote = findViewById(R.id.etNote);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvBookingTime = findViewById(R.id.tvBookingTime);
        tvBookingPrice = findViewById(R.id.tvBookingPrice);
        tvBookingDuration = findViewById(R.id.tvBookingDuration);
        rgPayment = findViewById(R.id.rgPayment);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        RecyclerView rvServices = findViewById(R.id.rvServices);

        tvSalonName.setText(salonName);

        serviceOptions = SalonServicesCatalog.buildOptions(typeSalon, category);
        quantities = new int[serviceOptions.size()];
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setAdapter(new BookingServicePickerAdapter(
                this, serviceOptions, quantities, this::refreshTotalsDisplay));

        loadNextAvailableSlot();

        refreshTotalsDisplay();

        btnConfirm.setOnClickListener(v -> {
            String serviceSummary = buildServiceSummary();
            if (serviceSummary.isEmpty()) {
                Toast.makeText(this, R.string.booking_no_service_selected, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(assignedDate) || TextUtils.isEmpty(assignedTime)) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            String notes = etNote.getText().toString().trim();
            String priceStr = BookingPriceHelper.formatMoney(this, priceTotal);
            int duration = computeDurationMinutes();
            String paymentLabel = BookingUi.paymentLabel(this, selectedPaymentMethod());
            String message = getString(R.string.booking_confirm_dialog_body,
                    salonName != null ? salonName : "—",
                    serviceSummary,
                    assignedDate,
                    assignedTime,
                    priceStr,
                    TextUtils.isEmpty(notes) ? "—" : notes,
                    duration,
                    paymentLabel);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.booking_confirm_dialog_title)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.booking_dialog_confirm_yes, (d, w) -> persistBooking(serviceSummary, notes))
                    .show();
        });
    }

    private void loadNextAvailableSlot() {
        SalonRepository.getInstance(this).findNextAvailableSlot(salonName, slot -> {
            if (slot == null) {
                Toast.makeText(this, R.string.booking_no_slot_available, Toast.LENGTH_LONG).show();
                return;
            }
            applySlot(slot.date, slot.time);
            Toast.makeText(this, getString(R.string.booking_slot_suggested_toast, slot.date, slot.time),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void applySlot(String date, String time) {
        assignedDate = date;
        assignedTime = time;
        tvBookingDate.setText(assignedDate);
        tvBookingTime.setText(assignedTime);
    }

    private void refreshTotalsDisplay() {
        priceTotal = 0;
        int duration = 0;
        for (int i = 0; i < serviceOptions.size(); i++) {
            SalonServiceOption opt = serviceOptions.get(i);
            if (quantities[i] > 0) {
                priceTotal += opt.unitPrice * quantities[i];
                duration += opt.durationMinutes * quantities[i];
            }
        }
        if (duration <= 0) {
            duration = 0;
        }
        tvBookingPrice.setText(BookingPriceHelper.formatMoney(this, priceTotal));
        if (duration > 0) {
            tvBookingDuration.setText(getString(R.string.booking_duration_label, duration));
        } else {
            tvBookingDuration.setText(getString(R.string.booking_duration_label, 0));
        }
    }

    private int computeDurationMinutes() {
        int d = 0;
        for (int i = 0; i < serviceOptions.size(); i++) {
            if (quantities[i] > 0) {
                d += serviceOptions.get(i).durationMinutes * quantities[i];
            }
        }
        return Math.max(d, 0);
    }

    private String selectedPaymentMethod() {
        int id = rgPayment.getCheckedRadioButtonId();
        if (id == R.id.rbPayOnline) {
            return PaymentMethod.ONLINE_DEMO;
        }
        return PaymentMethod.AT_SALON;
    }

    private String buildServiceSummary() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serviceOptions.size(); i++) {
            if (quantities[i] <= 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getString(serviceOptions.get(i).nameResId));
        }
        return sb.toString();
    }

    private void persistBooking(String serviceSummary, String notes) {
        SessionManager sessionManager = new SessionManager(this);
        User user = sessionManager.getUserDetails();
        String clientUsername = user != null && user.getUsername() != null
                ? user.getUsername().trim() : "";
        String clientName = user != null && user.getName() != null && !user.getName().isEmpty()
                ? user.getName()
                : getString(R.string.booking_guest_name);
        String clientPhone = user != null && user.getPhone() != null && !user.getPhone().isEmpty()
                ? user.getPhone()
                : getString(R.string.booking_guest_phone);

        int duration = computeDurationMinutes();
        Booking booking = new Booking(0L, clientName, clientPhone, clientUsername, salonName,
                serviceSummary, assignedDate, assignedTime, notes, priceTotal,
                BookingStatus.PENDING, selectedPaymentMethod(), duration);

        SalonRepository repo = SalonRepository.getInstance(this);
        final String userForCancel = clientUsername;
        repo.addBookingIfSlotFree(booking, new SalonRepository.OnAddBookingResult() {
            @Override
            public void onSuccess(long newBookingId) {
                if (replaceBookingId > 0) {
                    repo.cancelBookingForClient(replaceBookingId, userForCancel, cancelled -> {
                        if (cancelled) {
                            BookingReminderScheduler.cancelForBooking(BookingActivity.this, replaceBookingId);
                        }
                    });
                }
                Toast.makeText(BookingActivity.this, R.string.booking_confirmed, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BookingActivity.this, MyBookingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSlotTaken() {
                repo.findNextAvailableSlot(salonName, slot -> {
                    if (slot == null) {
                        Toast.makeText(BookingActivity.this, R.string.booking_no_slot_available,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    applySlot(slot.date, slot.time);
                    Toast.makeText(BookingActivity.this,
                            getString(R.string.booking_slot_conflict, slot.date, slot.time),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.confirm_booking;
    }
}
