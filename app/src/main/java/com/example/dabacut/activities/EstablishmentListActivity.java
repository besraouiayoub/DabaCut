package com.example.dabacut.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.EstablishmentAdapter;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.DistanceUtils;
import com.example.dabacut.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EstablishmentListActivity extends BaseClientActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4102;

    private RecyclerView recyclerSalons;
    private SearchView searchView;
    private android.widget.CheckBox cbSortDistance;
    private String selectedGender;
    private String selectedCategory;
    private String searchQuery = "";
    private List<Establishment> cachedAll = new ArrayList<>();
    private Double userLat;
    private Double userLng;
    private FusedLocationProviderClient fusedLocationClient;
    private Runnable pendingAfterPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);

        SessionManager sm = new SessionManager(this);
        selectedGender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        selectedCategory = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);
        if (selectedGender == null) {
            selectedGender = sm.getSearchGender();
        }
        if (selectedCategory == null) {
            selectedCategory = sm.getSearchCategory();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        recyclerSalons = findViewById(R.id.recyclerSalons);
        recyclerSalons.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchSalons);
        cbSortDistance = findViewById(R.id.cbSortDistance);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText != null ? newText : "";
                if (!cachedAll.isEmpty()) {
                    applyDisplayInternal();
                }
                return true;
            }
        });

        cbSortDistance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fetchUserLocation(this::applyDisplayInternal);
            } else {
                userLat = null;
                userLng = null;
                applyDisplayInternal();
            }
        });

        loadFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFromDb();
    }

    private void loadFromDb() {
        SalonRepository.getInstance(this).getEstablishments(allSalons -> {
            cachedAll = allSalons != null ? new ArrayList<>(allSalons) : new ArrayList<>();
            if (cbSortDistance.isChecked()) {
                fetchUserLocation(this::applyDisplayInternal);
            } else {
                applyDisplayInternal();
            }
        });
    }

    private void fetchUserLocation(Runnable onDone) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            pendingAfterPermission = onDone;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
            } else {
                userLat = null;
                userLng = null;
            }
            onDone.run();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Runnable run = pendingAfterPermission;
            pendingAfterPermission = null;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (run != null) {
                    fetchUserLocation(run);
                }
            } else {
                if (cbSortDistance.isChecked()) {
                    cbSortDistance.setChecked(false);
                }
                userLat = null;
                userLng = null;
                if (run != null) {
                    run.run();
                }
            }
        }
    }

    private void applyDisplayInternal() {
        List<Establishment> work = filter(new ArrayList<>(cachedAll));
        work = filterBySearch(work);
        if (cbSortDistance.isChecked() && userLat != null && userLng != null) {
            Collections.sort(work, (a, b) -> Double.compare(distanceKm(a), distanceKm(b)));
        } else {
            Collections.sort(work, (a, b) -> {
                String na = a.getName() != null ? a.getName() : "";
                String nb = b.getName() != null ? b.getName() : "";
                return na.compareToIgnoreCase(nb);
            });
        }
        boolean showDist = cbSortDistance.isChecked() && userLat != null && userLng != null;
        recyclerSalons.setAdapter(new EstablishmentAdapter(this, work,
                showDist ? userLat : null,
                showDist ? userLng : null));
    }

    private double distanceKm(Establishment e) {
        if (userLat == null || userLng == null) {
            return Double.MAX_VALUE;
        }
        if (e.getLatitude() == 0.0 && e.getLongitude() == 0.0) {
            return Double.MAX_VALUE;
        }
        return DistanceUtils.kmBetween(userLat, userLng, e.getLatitude(), e.getLongitude());
    }

    private List<Establishment> filterBySearch(List<Establishment> in) {
        String q = searchQuery.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            return in;
        }
        List<Establishment> out = new ArrayList<>();
        for (Establishment e : in) {
            if (e.getName() != null && e.getName().toLowerCase(Locale.ROOT).contains(q)) {
                out.add(e);
            }
        }
        return out;
    }

    private List<Establishment> filter(List<Establishment> allSalons) {
        List<Establishment> filteredSalons = new ArrayList<>();
        if (selectedGender == null || selectedCategory == null) {
            filteredSalons.addAll(allSalons);
        } else {
            for (Establishment e : allSalons) {
                boolean matchesGender = false;
                if ("homme".equalsIgnoreCase(selectedGender)
                        && "Salon Homme".equalsIgnoreCase(e.getTypeSalon())) {
                    matchesGender = true;
                } else if ("femme".equalsIgnoreCase(selectedGender)
                        && "Salon Filles".equalsIgnoreCase(e.getTypeSalon())) {
                    matchesGender = true;
                }
                boolean matchesCategory = e.getCategory().equalsIgnoreCase(selectedCategory);
                if (matchesGender && matchesCategory) {
                    filteredSalons.add(e);
                }
            }
        }
        return filteredSalons;
    }

    @Override
    protected int clientNavSelectedItemId() {
        return R.id.nav_client_salons;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.available_salons;
    }
}
