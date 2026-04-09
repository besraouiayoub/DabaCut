package com.example.dabacut.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.dabacut.R;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CreateSalonProfileActivity extends BaseOwnerActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private final String[] salonTypes = {"Salon Homme", "Salon Filles"};
    private final String[] salonCategories = {"Standard", "Class", "Premium"};

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat;
    private double currentLng;
    private TextView tvLocationStatus;
    private EditText etName;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etDesc;
    private Spinner spinnerType;
    private Spinner spinnerCategory;
    private Button btnSave;
    private Button btnGetLocation;
    private SessionManager sessionManager;
    private TextView tvSalonLocked;
    private EditText etWeekdayOpenFrom;
    private EditText etWeekdayOpenTo;
    private CheckBox cbOpenSaturday;
    private LinearLayout layoutSaturdayHours;
    private EditText etSaturdayOpenFrom;
    private EditText etSaturdayOpenTo;
    private CheckBox cbOpenSunday;
    private LinearLayout layoutSundayHours;
    private EditText etSundayOpenFrom;
    private EditText etSundayOpenTo;
    private long existingEstablishmentId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_salon_profile);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etSalonName);
        etPhone = findViewById(R.id.etSalonPhone);
        etAddress = findViewById(R.id.etSalonAddress);
        etDesc = findViewById(R.id.etSalonDesc);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        btnSave = findViewById(R.id.btnSave);
        tvSalonLocked = findViewById(R.id.tvSalonLocked);
        etWeekdayOpenFrom = findViewById(R.id.etWeekdayOpenFrom);
        etWeekdayOpenTo = findViewById(R.id.etWeekdayOpenTo);
        cbOpenSaturday = findViewById(R.id.cbOpenSaturday);
        layoutSaturdayHours = findViewById(R.id.layoutSaturdayHours);
        etSaturdayOpenFrom = findViewById(R.id.etSaturdayOpenFrom);
        etSaturdayOpenTo = findViewById(R.id.etSaturdayOpenTo);
        cbOpenSunday = findViewById(R.id.cbOpenSunday);
        layoutSundayHours = findViewById(R.id.layoutSundayHours);
        etSundayOpenFrom = findViewById(R.id.etSundayOpenFrom);
        etSundayOpenTo = findViewById(R.id.etSundayOpenTo);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, salonTypes);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, salonCategories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        cbOpenSaturday.setOnCheckedChangeListener((buttonView, isChecked) ->
                layoutSaturdayHours.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        cbOpenSunday.setOnCheckedChangeListener((buttonView, isChecked) ->
                layoutSundayHours.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        layoutSaturdayHours.setVisibility(cbOpenSaturday.isChecked() ? View.VISIBLE : View.GONE);
        layoutSundayHours.setVisibility(cbOpenSunday.isChecked() ? View.VISIBLE : View.GONE);

        SalonRepository repo = SalonRepository.getInstance(this);
        String owner = sessionManager.getUsername();
        if (owner != null) {
            repo.getEstablishmentForOwner(owner, est -> {
                if (est != null && est.getId() > 0) {
                    existingEstablishmentId = est.getId();
                    tvSalonLocked.setVisibility(View.VISIBLE);
                    tvSalonLocked.setText(R.string.owner_salon_already_created);
                    applyEstablishmentToForm(est);
                }
            });
        }

        String savedSalon = sessionManager.getOwnerSalonName();
        if (savedSalon != null && !savedSalon.isEmpty() && etName.getText().toString().trim().isEmpty()) {
            etName.setText(savedSalon);
        }

        btnGetLocation.setOnClickListener(v -> requestLocation());

        btnSave.setOnClickListener(v -> {
            String username = sessionManager.getUsername();
            if (username == null || username.trim().isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            Establishment salon = buildEstablishmentFromForm(name, type, category, address, phone, desc);

            if (existingEstablishmentId > 0) {
                salon.setId(existingEstablishmentId);
                repo.updateEstablishment(salon, () -> {
                    sessionManager.setOwnerSalonName(name);
                    Toast.makeText(CreateSalonProfileActivity.this, R.string.profile_saved_success,
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                repo.addEstablishment(salon, username.trim(), () -> {
                    sessionManager.setOwnerSalonName(name);
                    Toast.makeText(CreateSalonProfileActivity.this, R.string.profile_saved_success,
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void applyEstablishmentToForm(Establishment est) {
        etName.setText(est.getName());
        etPhone.setText(est.getPhone());
        etAddress.setText(est.getAddress());
        etDesc.setText(est.getDescription());
        currentLat = est.getLatitude();
        currentLng = est.getLongitude();
        if (currentLat != 0 || currentLng != 0) {
            tvLocationStatus.setText(getString(R.string.location_captured,
                    String.format(java.util.Locale.US, "%.4f, %.4f", currentLat, currentLng)));
        }
        setSpinnerToValue(spinnerType, salonTypes, est.getTypeSalon());
        setSpinnerToValue(spinnerCategory, salonCategories, est.getCategory());
        etWeekdayOpenFrom.setText(nzTime(est.getWeekdayOpenFrom(), "09:00"));
        etWeekdayOpenTo.setText(nzTime(est.getWeekdayOpenTo(), "19:00"));
        cbOpenSaturday.setChecked(est.isOpenSaturday());
        etSaturdayOpenFrom.setText(nzTime(est.getSaturdayOpenFrom(), "09:00"));
        etSaturdayOpenTo.setText(nzTime(est.getSaturdayOpenTo(), "18:00"));
        layoutSaturdayHours.setVisibility(est.isOpenSaturday() ? View.VISIBLE : View.GONE);
        cbOpenSunday.setChecked(est.isOpenSunday());
        etSundayOpenFrom.setText(nzTime(est.getSundayOpenFrom(), "10:00"));
        etSundayOpenTo.setText(nzTime(est.getSundayOpenTo(), "16:00"));
        layoutSundayHours.setVisibility(est.isOpenSunday() ? View.VISIBLE : View.GONE);
        sessionManager.setOwnerSalonName(est.getName());
    }

    private static void setSpinnerToValue(Spinner sp, String[] items, String value) {
        if (value == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(value.trim())) {
                sp.setSelection(i);
                return;
            }
        }
    }

    private static String nzTime(String s, String fallback) {
        if (s == null || s.trim().isEmpty()) {
            return fallback;
        }
        return s.trim();
    }

    private Establishment buildEstablishmentFromForm(String name, String type, String category,
                                                     String address, String phone, String desc) {
        Establishment e = new Establishment(name, type, category, address, phone, desc);
        e.setLatitude(currentLat);
        e.setLongitude(currentLng);
        e.setWeekdayOpenFrom(nzTime(etWeekdayOpenFrom.getText().toString(), "09:00"));
        e.setWeekdayOpenTo(nzTime(etWeekdayOpenTo.getText().toString(), "19:00"));
        e.setOpenSaturday(cbOpenSaturday.isChecked());
        e.setSaturdayOpenFrom(nzTime(etSaturdayOpenFrom.getText().toString(), "09:00"));
        e.setSaturdayOpenTo(nzTime(etSaturdayOpenTo.getText().toString(), "18:00"));
        e.setOpenSunday(cbOpenSunday.isChecked());
        e.setSundayOpenFrom(nzTime(etSundayOpenFrom.getText().toString(), "10:00"));
        e.setSundayOpenTo(nzTime(etSundayOpenTo.getText().toString(), "16:00"));
        return e;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.create_salon_profile;
    }

    @Override
    protected int ownerNavSelectedItemId() {
        return R.id.nav_owner_profile;
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                tvLocationStatus.setText(getString(R.string.location_captured,
                        String.format(java.util.Locale.US, "%.4f, %.4f", currentLat, currentLng)));
                Toast.makeText(this, R.string.location_update_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.location_update_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
