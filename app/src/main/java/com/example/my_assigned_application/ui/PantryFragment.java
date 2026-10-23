package com.example.my_assigned_application.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.AddEditIngredientActivity;
import com.example.my_assigned_application.R;
import com.example.my_assigned_application.adapter.PantryAdapter;
import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.util.ExpiryChecker;
import com.example.my_assigned_application.util.SettingsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Shows every ingredient currently in the user's pantry. */
public class PantryFragment extends Fragment {

    private PantryDatabaseHelper db;
    private SettingsManager settings;
    private PantryAdapter adapter;
    private TextView emptyView;
    private TextView bannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = PantryDatabaseHelper.getInstance(requireContext());
        settings = new SettingsManager(requireContext());
        emptyView = view.findViewById(R.id.textEmpty);
        bannerView = view.findViewById(R.id.textExpiryBanner);

        RecyclerView recycler = view.findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tapping a row opens the form in EDIT mode, passing the item's id in the Intent
        adapter = new PantryAdapter(item -> {
            Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
            intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
            startActivity(intent);
        });
        recycler.setAdapter(adapter);

        // The + button opens the same form in ADD mode (no id extra)
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), AddEditIngredientActivity.class)));
    }

    /** Reload whenever the screen comes back into view so the list is never stale. */
    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        boolean alertsOn = settings.isExpiryAlertsEnabled();
        int alertDays = settings.getAlertDays();

        List<PantryItem> items = db.getAllPantryItems();
        adapter.setExpiryAlerts(alertsOn, alertDays);
        adapter.setItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

        // Banner: how many items are expired or expiring soon
        int flagged = 0;
        if (alertsOn) {
            for (PantryItem item : items) {
                ExpiryChecker.Status status =
                        ExpiryChecker.statusOf(item.getExpiryDate(), alertDays);
                if (status == ExpiryChecker.Status.EXPIRED
                        || status == ExpiryChecker.Status.EXPIRING_SOON) {
                    flagged++;
                }
            }
        }

        if (flagged > 0) {
            bannerView.setText(getResources().getQuantityString(
                    R.plurals.expiring_banner, flagged, flagged));
            bannerView.setVisibility(View.VISIBLE);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }
}
