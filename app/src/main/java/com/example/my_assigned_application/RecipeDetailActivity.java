package com.example.my_assigned_application;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.model.RecipeIngredient;
import com.example.my_assigned_application.util.DisplayText;
import com.example.my_assigned_application.util.QuantityFormat;

/** Shows the full ingredient list and method of the recipe whose id arrives in the Intent. */
public class RecipeDetailActivity extends AppCompatActivity {

    /** Key for the Intent extra holding the id of the recipe to show. */
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Read the recipe id that the previous screen put into the Intent
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = PantryDatabaseHelper.getInstance(this).getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(recipe.getName());
        ((TextView) findViewById(R.id.textRecipeName)).setText(recipe.getName());
        ((TextView) findViewById(R.id.textIngredients)).setText(buildIngredientList(recipe));
        ((TextView) findViewById(R.id.textSteps)).setText(recipe.getSteps());
    }

    /** One line per ingredient, e.g. "• Eggs: 2 pcs". */
    private String buildIngredientList(Recipe recipe) {
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append("\u2022 ")
                    .append(DisplayText.capitalize(ingredient.getName()))
                    .append(": ")
                    .append(QuantityFormat.format(ingredient.getQuantity()))
                    .append(' ')
                    .append(ingredient.getUnit());
        }
        return sb.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}