package com.example.dabacut.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.adapters.EstablishmentAdapter;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

public class EstablishmentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);

        String selectedGender = getIntent().getStringExtra(GenderSelectionActivity.EXTRA_GENDER);
        String selectedCategory = getIntent().getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY);

        RecyclerView recycler = findViewById(R.id.recyclerSalons);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Use real data from DataManager
        List<Establishment> allSalons = DataManager.getInstance().getEstablishments();
        List<Establishment> filteredSalons = new ArrayList<>();

        // Logic de filtrage
        for (Establishment e : allSalons) {
            boolean matchesGender = false;
            if ("homme".equalsIgnoreCase(selectedGender) && "Salon Homme".equalsIgnoreCase(e.getTypeSalon())) {
                matchesGender = true;
            } else if ("femme".equalsIgnoreCase(selectedGender) && "Salon Filles".equalsIgnoreCase(e.getTypeSalon())) {
                matchesGender = true;
            }

            // Category matching (allow case insensitive)
            boolean matchesCategory = e.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesGender && matchesCategory) {
                filteredSalons.add(e);
            }
        }

        recycler.setAdapter(new EstablishmentAdapter(this, filteredSalons));
    }
}