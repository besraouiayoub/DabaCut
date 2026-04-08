package com.example.dabacut.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dabacut.R;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.DataManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private String selectedGender;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get filter data from intent
        selectedGender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        selectedCategory = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);

        Log.d(TAG, "Filters - Gender: " + selectedGender + ", Category: " + selectedCategory);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: Google Maps is ready");

        // UI Settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Request permissions and enable "My Location"
        enableMyLocation();

        // Load and filter establishments
        List<Establishment> allSalons = DataManager.getInstance().getEstablishments();
        LatLng lastLatLng = new LatLng(33.9716, -6.8498); // Default: Rabat, Morocco
        int markerCount = 0;

        for (Establishment e : allSalons) {
            if (isMatchingFilter(e)) {
                double lat = e.getLatitude();
                double lng = e.getLongitude();

                // If coordinates are missing (dummy data), generate semi-random ones around Rabat
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

        // Zoom to the markers or center on Rabat
        if (markerCount > 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 13));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12));
            Toast.makeText(this, "Aucun salon trouvé pour ces filtres", Toast.LENGTH_LONG).show();
        }

        // Info Window Click Listener -> Detail Activity
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
        // If no filters provided, show all (for debug)
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