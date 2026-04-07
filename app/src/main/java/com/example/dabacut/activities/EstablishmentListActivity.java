package com.example.dabacut.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.dabacut.R;
import com.example.dabacut.adapters.EstablishmentAdapter;
import com.example.dabacut.models.Establishment;

import java.util.*;

public class EstablishmentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);

        RecyclerView recycler = findViewById(R.id.recyclerSalons);

        List<Establishment> list = new ArrayList<>();
        list.add(new Establishment("Elite Barber", "Salon Homme", "Premium", "Rabat", "060000", "Top salon"));
        list.add(new Establishment("Beauty Queen", "Salon Filles", "Class", "Agdal", "070000", "Beauty salon"));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new EstablishmentAdapter(this, list));
    }
}