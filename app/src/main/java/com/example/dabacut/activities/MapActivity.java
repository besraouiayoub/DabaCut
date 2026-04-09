package com.example.dabacut.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.dabacut.R;
import com.example.dabacut.data.SalonRepository;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends BaseClientActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private String selectedGender;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SessionManager sm = new SessionManager(this);
        selectedGender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        selectedCategory = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);
        if (selectedGender == null) {
            selectedGender = sm.getSearchGender();
        }
        if (selectedCategory == null) {
            selectedCategory = sm.getSearchCategory();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected int clientNavSelectedItemId() {
        return R.id.nav_client_map;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.view_map;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: Google Maps is ready");

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        enableMyLocation();

        SalonRepository.getInstance(this).getEstablishments(allSalons -> applyMarkers(allSalons));

        mMap.setOnInfoWindowClickListener(marker -> {
            Establishment e = (Establishment) marker.getTag();
            if (e != null) {
                Intent i = new Intent(MapActivity.this, EstablishmentDetailActivity.class);
                i.putExtra("name", e.getName());
                i.putExtra("type", e.getTypeSalon());
                i.putExtra("category", e.getCategory());
                i.putExtra("address", e.getAddress());
                i.putExtra("phone", e.getPhone());
                i.putExtra("desc", e.getDescription());
                startActivity(i);
            }
        });
    }

    private void applyMarkers(List<Establishment> allSalons) {
        LatLng lastLatLng = new LatLng(33.9716, -6.8498);
        int markerCount = 0;

        for (Establishment e : allSalons) {
            if (isMatchingFilter(e)) {
                double lat = e.getLatitude();
                double lng = e.getLongitude();

                if (lat == 0 && lng == 0) {
                    lat = 33.97 + (Math.random() * 0.08 - 0.04);
                    lng = -6.84 + (Math.random() * 0.08 - 0.04);
                }

                LatLng pos = new LatLng(lat, lng);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(e.getName())
                        .snippet(e.getCategory() + " - " + e.getTypeSalon()));

                if (marker != null) {
                    marker.setTag(e);
                }
                lastLatLng = pos;
                markerCount++;
            }
        }

        Log.d(TAG, "Markers added: " + markerCount);

        if (markerCount > 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 13));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12));
            Toast.makeText(this, R.string.no_salon_found, Toast.LENGTH_LONG).show();
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private boolean isMatchingFilter(Establishment e) {
        if (selectedGender == null || selectedCategory == null) return true;

        boolean genderMatch = false;
        if ("homme".equalsIgnoreCase(selectedGender) && "Salon Homme".equalsIgnoreCase(e.getTypeSalon())) {
            genderMatch = true;
        } else if ("femme".equalsIgnoreCase(selectedGender) && "Salon Filles".equalsIgnoreCase(e.getTypeSalon())) {
            genderMatch = true;
        }

        boolean categoryMatch = e.getCategory().equalsIgnoreCase(selectedCategory);

        return genderMatch && categoryMatch;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }
}
