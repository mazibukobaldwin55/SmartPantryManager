package com.example.my_assigned_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import com.example.my_assigned_application.RecipeDetailActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.adapter.RecipeAdapter;
import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.util.RecipeMatcher;

import java.util.List;

/** Lists ONLY the recipes the user can cook right now, using the strict-matching rule. */
public class SuggestedFragment extends Fragment {

    private PantryDatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView textSummary;
    private TextView textEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = PantryDatabaseHelper.getInstance(requireContext());
        textSummary = view.findViewById(R.id.textSummary);
        textEmpty = view.findViewById(R.id.textEmpty);

        RecyclerView recycler = view.findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TEMPORARY: Part B replaces this with an Intent to the detail screen
        // Tapping a recipe opens the detail screen, passing the recipe id in an Intent
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        recycler.setAdapter(adapter);
    }

    /** Re-run the matching every time the tab is shown, so it always reflects the pantry. */
    @Override
    public void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipes();

        // The strict-matching rule decides which recipes are shown
        List<Recipe> suggested = RecipeMatcher.getStrictMatches(allRecipes, pantry);
        adapter.setRecipes(suggested);

        if (suggested.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            textSummary.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            textSummary.setText(getResources().getQuantityString(
                    R.plurals.suggested_summary, suggested.size(), suggested.size()));
            textSummary.setVisibility(View.VISIBLE);
        }
    }
}
