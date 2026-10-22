package com.example.my_assigned_application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local SQLite database for the Smart Pantry Manager.
 * Holds the user's pantry (full CRUD) and the seeded recipe collection.
 */
public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Table names
    public static final String T_PANTRY = "pantry_items";
    public static final String T_RECIPES = "recipes";
    public static final String T_RECIPE_ING = "recipe_ingredients";

    // Column names
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_STEPS = "steps";
    public static final String COL_RECIPE_ID = "recipe_id";

    private static PantryDatabaseHelper instance;

    /** One shared helper for the whole app (uses the application context to avoid leaks). */
    public static synchronized PantryDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PantryDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private PantryDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // deleting a recipe removes its ingredients
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_RECIPE_ING + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES "
                + T_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE)");

        // Pre-load the recipe collection on first run
        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy for this assignment: rebuild everything
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // PANTRY CRUD
    // ------------------------------------------------------------------

    /** CREATE: inserts a new pantry item and returns its new id (or -1 on failure). */
    public long addPantryItem(PantryItem item) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(item));
    }

    /** READ: returns every pantry item, sorted alphabetically. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null,
                null, null, COL_NAME + " COLLATE NOCASE")) {
            while (c.moveToNext()) {
                items.add(readPantryItem(c));
            }
        }
        return items;
    }

    /** READ: returns one pantry item by id, or null if it doesn't exist. */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readPantryItem(c) : null;
        }
    }

    /** UPDATE: saves changes to an existing item. Returns the number of rows changed. */
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update(T_PANTRY, toValues(item), COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE: removes an item. Returns the number of rows deleted. */
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(T_PANTRY, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, item.getName());
        v.put(COL_QUANTITY, item.getQuantity());
        v.put(COL_UNIT, item.getUnit());
        v.put(COL_EXPIRY, item.getExpiryDate()); // null is stored as NULL
        return v;
    }

    private PantryItem readPantryItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_EXPIRY)));
    }

    // ------------------------------------------------------------------
    // RECIPES (read-only; seeded on first run)
    // ------------------------------------------------------------------

    /** Returns all recipes, each with its full ingredient list. */
    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Long, Recipe> recipes = new LinkedHashMap<>();

        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE")) {
            while (c.moveToNext()) {
                Recipe r = readRecipe(c);
                recipes.put(r.getId(), r);
            }
        }

        try (Cursor c = db.query(T_RECIPE_ING, null, null, null, null, null, COL_ID)) {
            while (c.moveToNext()) {
                Recipe r = recipes.get(c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)));
                if (r != null) {
                    r.addIngredient(readIngredient(c));
                }
            }
        }
        return new ArrayList<>(recipes.values());
    }

    /** Returns one recipe (with ingredients) by id, or null if it doesn't exist. */
    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe;

        try (Cursor c = db.query(T_RECIPES, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) {
                return null;
            }
            recipe = readRecipe(c);
        }

        try (Cursor c = db.query(T_RECIPE_ING, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, COL_ID)) {
            while (c.moveToNext()) {
                recipe.addIngredient(readIngredient(c));
            }
        }
        return recipe;
    }

    private Recipe readRecipe(Cursor c) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_STEPS)));
    }

    private RecipeIngredient readIngredient(Cursor c) {
        return new RecipeIngredient(
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)));
    }
}
