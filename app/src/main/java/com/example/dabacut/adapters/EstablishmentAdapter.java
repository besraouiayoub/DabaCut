package com.example.dabacut.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.activities.EstablishmentDetailActivity;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.utils.DistanceUtils;

import java.util.List;

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.ViewHolder> {

    private final List<Establishment> list;
    private final Context context;
    @Nullable
    private final Double userLat;
    @Nullable
    private final Double userLng;

    public EstablishmentAdapter(Context context, List<Establishment> list) {
        this(context, list, null, null);
    }

    public EstablishmentAdapter(Context context, List<Establishment> list,
                                @Nullable Double userLat, @Nullable Double userLng) {
        this.context = context;
        this.list = list;
        this.userLat = userLat;
        this.userLng = userLng;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_establishment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establishment e = list.get(position);

        holder.name.setText(e.getName());
        holder.type.setText(e.getTypeSalon());
        holder.category.setText(e.getCategory());

        if (userLat != null && userLng != null
                && (e.getLatitude() != 0.0 || e.getLongitude() != 0.0)) {
            double km = DistanceUtils.kmBetween(userLat, userLng, e.getLatitude(), e.getLongitude());
            holder.distance.setText(context.getString(R.string.distance_km_format, km));
            holder.distance.setVisibility(View.VISIBLE);
        } else {
            holder.distance.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EstablishmentDetailActivity.class);
            i.putExtra("name", e.getName());
            i.putExtra("type", e.getTypeSalon());
            i.putExtra("category", e.getCategory());
            i.putExtra("address", e.getAddress());
            i.putExtra("phone", e.getPhone());
            i.putExtra("desc", e.getDescription());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView type;
        TextView category;
        TextView distance;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            type = itemView.findViewById(R.id.tvType);
            category = itemView.findViewById(R.id.tvCategory);
            distance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
