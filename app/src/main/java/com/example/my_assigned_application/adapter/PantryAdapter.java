package com.example.my_assigned_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.model.PantryItem;
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

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
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

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setText("Expires: " + item.getExpiryDate());
            holder.textExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.textExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
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
