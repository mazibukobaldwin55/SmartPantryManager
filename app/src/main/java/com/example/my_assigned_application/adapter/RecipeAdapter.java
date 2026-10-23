package com.example.my_assigned_application.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.model.RecipeIngredient;
import com.example.my_assigned_application.util.DisplayText;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter that shows recipes (name plus a one-line ingredient summary) in a RecyclerView. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    /** Lets the screen that owns the list decide what happens when a recipe is tapped. */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed recipes and refreshes the list. */
    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        List<String> names = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            names.add(DisplayText.capitalize(ingredient.getName()));
        }

        holder.textName.setText(recipe.getName());
        holder.textIngredients.setText(holder.itemView.getContext()
                .getString(R.string.recipe_needs, TextUtils.join(", ", names)));

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /** Holds references to the views of one row. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textIngredients;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textIngredients = itemView.findViewById(R.id.textRecipeIngredients);
        }
    }
}
