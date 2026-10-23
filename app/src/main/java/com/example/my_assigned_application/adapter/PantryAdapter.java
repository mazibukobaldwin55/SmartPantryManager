package com.example.my_assigned_application.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.util.ExpiryChecker;
import com.example.my_assigned_application.util.QuantityFormat;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter that binds PantryItem objects to the rows of the pantry RecyclerView. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets the screen that owns the list decide what happens when a row is tapped. */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    private boolean alertsEnabled = false;
    private int alertDays = 3;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** Applies the user's expiry-alert settings to the rows (call before setItems). */
    public void setExpiryAlerts(boolean enabled, int days) {
        this.alertsEnabled = enabled;
        this.alertDays = days;
    }

    /** Replaces the displayed data and refreshes the list. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(
                QuantityFormat.format(item.getQuantity()) + " " + item.getUnit());
        bindExpiry(holder, item.getExpiryDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    /**
     * Shows the expiry line. Because RecyclerView re-uses rows, the colour and style are
     * set explicitly in every case so a recycled row never keeps the previous look.
     */
    private void bindExpiry(ViewHolder holder, String expiry) {
        if (expiry == null || expiry.isEmpty()) {
            holder.textExpiry.setVisibility(View.GONE);
            return;
        }

        Context context = holder.itemView.getContext();
        String text = context.getString(R.string.expiry_plain, expiry);
        int color = ContextCompat.getColor(context, R.color.text_secondary);
        boolean emphasise = false;

        if (alertsEnabled) {
            ExpiryChecker.Status status = ExpiryChecker.statusOf(expiry, alertDays);
            Integer days = ExpiryChecker.daysUntilExpiry(expiry);

            if (status == ExpiryChecker.Status.EXPIRED) {
                text = context.getString(R.string.expired_on, expiry);
                color = ContextCompat.getColor(context, R.color.danger);
                emphasise = true;
            } else if (status == ExpiryChecker.Status.EXPIRING_SOON && days != null) {
                if (days == 0) {
                    text = context.getString(R.string.expiry_today);
                } else if (days == 1) {
                    text = context.getString(R.string.expiry_tomorrow);
                } else {
                    text = context.getString(R.string.expiry_in_days, days);
                }
                color = ContextCompat.getColor(context, R.color.orange_700);
                emphasise = true;
            }
        }

        holder.textExpiry.setText(text);
        holder.textExpiry.setTextColor(color);
        holder.textExpiry.setTypeface(null, emphasise ? Typeface.BOLD : Typeface.NORMAL);
        holder.textExpiry.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Holds references to the views of one row so they aren't looked up repeatedly. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
        }
    }
}
