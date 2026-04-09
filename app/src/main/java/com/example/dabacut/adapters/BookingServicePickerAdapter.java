package com.example.dabacut.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dabacut.R;
import com.example.dabacut.models.SalonServiceOption;
import com.example.dabacut.utils.BookingPriceHelper;

import java.util.List;

public class BookingServicePickerAdapter extends RecyclerView.Adapter<BookingServicePickerAdapter.Holder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    private final Context context;
    private final List<SalonServiceOption> options;
    /** 1 si la case est cochée, 0 sinon (une unité par service choisi). */
    private final int[] quantities;
    private final OnSelectionChangedListener listener;

    public BookingServicePickerAdapter(Context context, List<SalonServiceOption> options,
                                       int[] quantities, OnSelectionChangedListener listener) {
        this.context = context.getApplicationContext();
        this.options = options;
        this.quantities = quantities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_service_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SalonServiceOption opt = options.get(position);
        holder.name.setText(context.getString(opt.nameResId));
        String priceLine = BookingPriceHelper.formatMoney(context, opt.unitPrice);
        if (opt.durationMinutes > 0) {
            priceLine = priceLine + " · " + opt.durationMinutes + " min";
        }
        holder.unitPrice.setText(priceLine);

        CheckBox cb = holder.checkBox;
        String serviceLabel = context.getString(opt.nameResId);
        cb.setContentDescription(serviceLabel);
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(quantities[position] > 0);

        final int pos = position;
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            quantities[pos] = isChecked ? 1 : 0;
            if (listener != null) {
                listener.onSelectionChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> cb.toggle());
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView name;
        TextView unitPrice;

        Holder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbService);
            name = itemView.findViewById(R.id.tvServiceName);
            unitPrice = itemView.findViewById(R.id.tvServiceUnitPrice);
        }
    }
}
