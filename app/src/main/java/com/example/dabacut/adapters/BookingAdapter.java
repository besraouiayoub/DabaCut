package com.example.dabacut.adapters;

import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.models.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> list;

    public BookingAdapter(List<Booking> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking b = list.get(position);
        holder.name.setText(b.getSalonName());
        holder.date.setText(b.getDate() + " - " + b.getTime());
        holder.service.setText(b.getService());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, service;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSalonName);
            date = itemView.findViewById(R.id.tvDate);
            service = itemView.findViewById(R.id.tvService);
        }
    }
}