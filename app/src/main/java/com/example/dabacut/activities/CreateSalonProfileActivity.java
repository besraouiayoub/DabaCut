package com.example.dabacut.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dabacut.R;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.DataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CreateSalonProfileActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 0;
    private double currentLng = 0;
    private TextView tvLocationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_salon_profile);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        EditText etName = findViewById(R.id.etSalonName);
        EditText etPhone = findViewById(R.id.etSalonPhone);
        EditText etAddress = findViewById(R.id.etSalonAddress);
        EditText etDesc = findViewById(R.id.etSalonDesc);
        Spinner spinnerType = findViewById(R.id.spinnerType);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        Button btnSave = findViewById(R.id.btnSave);

        String[] types = {"Salon Homme", "Salon Filles"};
        String[] categories = {"Standard", "Class", "Premium"};

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        btnGetLocation.setOnClickListener(v -> requestLocation());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Establishment newSalon = new Establishment(name, type, category, address, phone, desc);
            newSalon.setLatitude(currentLat);
            newSalon.setLongitude(currentLng);
            
            DataManager.getInstance().addEstablishment(newSalon);

            Toast.makeText(this, "Profil enregistré avec succès !", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                tvLocationStatus.setText("Position récupérée : " + String.format("%.4f, %.4f", currentLat, currentLng));
                Toast.makeText(this, "Position mise à jour", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Impossible de récupérer la position", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}