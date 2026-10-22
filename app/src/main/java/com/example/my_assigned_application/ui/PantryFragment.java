package com.example.my_assigned_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.adapter.PantryAdapter;
import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Shows every ingredient currently in the user's pantry. */
public class PantryFragment extends Fragment {

    private PantryDatabaseHelper db;
    private PantryAdapter adapter;
    private TextView emptyView;

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
        emptyView = view.findViewById(R.id.textEmpty);

        RecyclerView recycler = view.findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TEMPORARY: tapping a row just shows a message. Step 4 opens the edit screen.
        adapter = new PantryAdapter(item -> Toast.makeText(requireContext(),
                "Edit screen comes in Step 4: " + item.getName(), Toast.LENGTH_SHORT).show());
        recycler.setAdapter(adapter);

        // TEMPORARY: the + button adds a sample item. Step 4 opens the Add screen.
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            int n = adapter.getItemCount() + 1;
            db.addPantryItem(new PantryItem("Sample item " + n, n, "pcs", null));
            loadItems();
        });
    }

    /** Reload whenever the screen comes back into view so the list is never stale. */
    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
