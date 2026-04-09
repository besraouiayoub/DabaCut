package com.example.dabacut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.dabacut.R;

public class EstablishmentDetailActivity extends BaseToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_detail);

        TextView tvDesc = findViewById(R.id.tvDesc);

        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("desc");
        String address = getIntent().getStringExtra("address");
        String phone = getIntent().getStringExtra("phone");

        toolbar().setTitle(name != null ? name : getString(R.string.salon_name_placeholder));

        String descSafe = description != null ? description : "";
        String addrSafe = address != null ? address : "-";
        String phoneSafe = phone != null ? phone : "-";

        String fullDescription = descSafe + "\n\n"
                + getString(R.string.address_label, addrSafe) + "\n"
                + getString(R.string.contact_label, phoneSafe);

        tvDesc.setText(fullDescription);

        Button btnBook = findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("salonName", name);
            intent.putExtra(BookingActivity.EXTRA_SALON_CATEGORY, getIntent().getStringExtra("category"));
            intent.putExtra(BookingActivity.EXTRA_SALON_TYPE, getIntent().getStringExtra("type"));
            startActivity(intent);
        });
    }
}
