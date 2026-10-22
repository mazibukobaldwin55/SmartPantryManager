package com.example.my_assigned_application.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Pre-loads the recipe collection the first time the database is created. */
final class RecipeSeeder {

    private RecipeSeeder() { }

    static void seed(SQLiteDatabase db) {
        add(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Whisk the eggs and melt the butter in a pan.\n"
                        + "3. Stir the eggs gently until just set.\n4. Serve on the toast.",
                "eggs,2,pcs", "bread,2,pcs", "butter,10,g");

        add(db, "Cheese Omelette",
                "1. Whisk the eggs.\n2. Melt the butter in a pan and pour in the eggs.\n"
                        + "3. Sprinkle cheese over one half.\n4. Fold and cook until the cheese melts.",
                "eggs,3,pcs", "cheese,50,g", "butter,10,g");

        add(db, "Tomato Pasta",
                "1. Boil the pasta until tender.\n2. Fry chopped onion and garlic in olive oil.\n"
                        + "3. Add chopped tomatoes and simmer for 10 minutes.\n4. Toss with the pasta.",
                "pasta,200,g", "tomatoes,4,pcs", "onion,1,pcs", "garlic,2,pcs", "olive oil,2,tbsp");

        add(db, "Garlic Butter Rice",
                "1. Cook the rice.\n2. Melt the butter and fry crushed garlic for 1 minute.\n"
                        + "3. Stir the garlic butter through the rice.",
                "rice,1,cup", "butter,20,g", "garlic,2,pcs");

        add(db, "Classic Pancakes",
                "1. Whisk flour, sugar, milk and eggs into a smooth batter.\n"
                        + "2. Pour a ladle into a hot pan.\n3. Flip when bubbles form and cook until golden.",
                "flour,200,g", "milk,300,ml", "eggs,2,pcs", "sugar,30,g");

        add(db, "French Toast",
                "1. Whisk eggs, milk and sugar.\n2. Soak each slice of bread.\n"
                        + "3. Fry both sides until golden brown.",
                "bread,4,pcs", "eggs,2,pcs", "milk,100,ml", "sugar,20,g");

        add(db, "Chicken Fried Rice",
                "1. Fry diced chicken until cooked through.\n2. Add chopped onion and cook until soft.\n"
                        + "3. Push to the side, scramble the eggs, then add the cooked rice.\n"
                        + "4. Season with soy sauce and stir well.",
                "rice,2,cup", "chicken breast,200,g", "eggs,2,pcs", "soy sauce,2,tbsp", "onion,1,pcs");

        add(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash.\n"
                        + "3. Stir in the butter and warm milk until creamy.",
                "potatoes,500,g", "butter,30,g", "milk,100,ml");

        add(db, "Tuna Pasta",
                "1. Boil the pasta.\n2. Fry chopped onion in olive oil.\n"
                        + "3. Add the tuna and warm through.\n4. Toss with the pasta.",
                "pasta,200,g", "tuna,1,pcs", "onion,1,pcs", "olive oil,1,tbsp");

        add(db, "Veggie Stir-Fry",
                "1. Slice the carrots, bell pepper and onion.\n2. Heat the oil in a wok.\n"
                        + "3. Stir-fry the vegetables for 5 minutes.\n4. Add soy sauce and toss.",
                "carrots,2,pcs", "bell peppers,1,pcs", "onion,1,pcs", "soy sauce,2,tbsp",
                "vegetable oil,1,tbsp");

        add(db, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Place cheese between the slices.\n"
                        + "3. Toast in a pan until golden and melted.",
                "bread,2,pcs", "cheese,60,g", "butter,10,g");

        add(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix with the eggs and flour.\n"
                        + "3. Fry spoonfuls in a pan until golden on both sides.",
                "bananas,2,pcs", "eggs,2,pcs", "flour,60,g");

        add(db, "Tomato Soup",
                "1. Fry onion and garlic in butter.\n2. Add chopped tomatoes and stock.\n"
                        + "3. Simmer for 20 minutes.\n4. Blend until smooth.",
                "tomatoes,6,pcs", "onion,1,pcs", "garlic,2,pcs", "vegetable stock,500,ml", "butter,20,g");

        add(db, "Beef Bolognese",
                "1. Brown the beef mince with onion and garlic.\n2. Add chopped tomatoes and simmer for 25 minutes.\n"
                        + "3. Boil the pasta and serve with the sauce.",
                "beef mince,300,g", "pasta,200,g", "tomatoes,4,pcs", "onion,1,pcs", "garlic,2,pcs");

        add(db, "Egg Fried Rice",
                "1. Heat the oil and fry chopped onion.\n2. Scramble in the eggs.\n"
                        + "3. Add the cooked rice and soy sauce and stir-fry for 3 minutes.",
                "rice,2,cup", "eggs,2,pcs", "soy sauce,1,tbsp", "onion,1,pcs", "vegetable oil,1,tbsp");

        add(db, "Potato Wedges",
                "1. Cut the potatoes into wedges.\n2. Toss with olive oil and paprika.\n"
                        + "3. Roast at 200C for 35 minutes, turning once.",
                "potatoes,600,g", "olive oil,2,tbsp", "paprika,1,tsp");

        add(db, "Chicken Curry",
                "1. Fry the onion until soft.\n2. Add chicken and curry powder and cook for 5 minutes.\n"
                        + "3. Add tomatoes and coconut milk.\n4. Simmer for 20 minutes.",
                "chicken breast,400,g", "onion,1,pcs", "tomatoes,2,pcs", "curry powder,2,tbsp",
                "coconut milk,200,ml");

        add(db, "Banana Oatmeal",
                "1. Simmer the oats in the milk for 5 minutes.\n2. Slice in the banana.\n"
                        + "3. Drizzle with honey and serve.",
                "oats,80,g", "milk,250,ml", "bananas,1,pcs", "honey,1,tbsp");

        add(db, "Mac and Cheese",
                "1. Boil the macaroni.\n2. Melt the butter, add milk and cheese and stir until smooth.\n"
                        + "3. Mix the sauce through the macaroni.",
                "macaroni,200,g", "cheese,150,g", "milk,200,ml", "butter,20,g");

        add(db, "Garlic Bread",
                "1. Mix crushed garlic into the softened butter.\n2. Spread over the bread.\n"
                        + "3. Bake at 180C for 10 minutes.",
                "bread,4,pcs", "butter,40,g", "garlic,3,pcs");
    }

    /** Inserts one recipe and its ingredients. Each ingredient is "name,quantity,unit". */
    private static void add(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues recipe = new ContentValues();
        recipe.put(PantryDatabaseHelper.COL_NAME, name);
        recipe.put(PantryDatabaseHelper.COL_STEPS, steps);
        long recipeId = db.insert(PantryDatabaseHelper.T_RECIPES, null, recipe);

        for (String line : ingredients) {
            String[] parts = line.split(",");
            ContentValues ing = new ContentValues();
            ing.put(PantryDatabaseHelper.COL_RECIPE_ID, recipeId);
            ing.put(PantryDatabaseHelper.COL_NAME, parts[0].trim());
            ing.put(PantryDatabaseHelper.COL_QUANTITY, Double.parseDouble(parts[1].trim()));
            ing.put(PantryDatabaseHelper.COL_UNIT, parts[2].trim());
            db.insert(PantryDatabaseHelper.T_RECIPE_ING, null, ing);
        }
    }
}
