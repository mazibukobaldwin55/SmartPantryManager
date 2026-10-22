package com.example.my_assigned_application;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TEMPORARY database test - will be removed in Step 3
        PantryDatabaseHelper helper = PantryDatabaseHelper.getInstance(this);
        List<Recipe> recipes = helper.getAllRecipes();
        Log.d("PantryTest", "Recipes seeded: " + recipes.size());
        Log.d("PantryTest", "First recipe: " + recipes.get(0).getName()
                + " has " + recipes.get(0).getIngredients().size() + " ingredients");
    }
}