package com.example.dabacut.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.models.Booking;
import com.example.dabacut.utils.BookingPriceHelper;
import com.example.dabacut.utils.BookingUi;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    private final List<Booking> list;
    @Nullable
    private final OnBookingClickListener clickListener;
    private final boolean ownerListMode;

    public BookingAdapter(List<Booking> list) {
        this(list, null, false);
    }

    /** Listener without changing row layout (e.g. client “my bookings” actions). */
    public BookingAdapter(List<Booking> list, @Nullable OnBookingClickListener clickListener) {
        this(list, clickListener, false);
    }

    public BookingAdapter(List<Booking> list, @Nullable OnBookingClickListener clickListener,
                          boolean ownerListMode) {
        this.list = list;
        this.clickListener = clickListener;
        this.ownerListMode = ownerListMode;
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
        if (ownerListMode) {
            holder.name.setText(clientPrimaryLabel(b));
            holder.service.setText(b.getSalonName() + " · " + b.getService());
        } else {
            holder.name.setText(b.getSalonName());
            holder.service.setText(b.getService());
        }
        holder.date.setText(b.getDate() + " - " + b.getTime());
        holder.status.setText(BookingUi.statusLabel(holder.itemView.getContext(), b.getStatus()));
        double p = b.getPriceTotal();
        if (p > 0) {
            holder.price.setText(BookingPriceHelper.formatMoney(holder.itemView.getContext(), p));
            holder.price.setVisibility(View.VISIBLE);
        } else {
            holder.price.setVisibility(View.GONE);
        }
        if (clickListener != null && b.getId() > 0) {
            holder.itemView.setOnClickListener(v -> clickListener.onBookingClick(b));
            holder.itemView.setClickable(true);
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static String clientPrimaryLabel(Booking b) {
        if (b.getClientUsername() != null && !b.getClientUsername().trim().isEmpty()) {
            return b.getClientUsername().trim();
        }
        if (b.getClientName() != null && !b.getClientName().trim().isEmpty()) {
            return b.getClientName().trim();
        }
        return "—";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, service, price, status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSalonName);
            date = itemView.findViewById(R.id.tvDate);
            service = itemView.findViewById(R.id.tvService);
            price = itemView.findViewById(R.id.tvBookingPrice);
            status = itemView.findViewById(R.id.tvBookingStatus);
        }
    }
}
