package com.example.dabacut.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.activities.EstablishmentDetailActivity;
import com.example.dabacut.models.Establishment;

import java.util.List;

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.ViewHolder> {

    private List<Establishment> list;
    private Context context;

    public EstablishmentAdapter(Context context, List<Establishment> list) {
        this.context = context;
        this.list = list;
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
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            type = itemView.findViewById(R.id.tvType);
            category = itemView.findViewById(R.id.tvCategory);
        }
    }
}