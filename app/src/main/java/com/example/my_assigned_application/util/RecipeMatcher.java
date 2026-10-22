package com.example.my_assigned_application.util;

import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE CORE BUSINESS LOGIC OF THE APP: the strict-matching rule.
 *
 * A recipe is suggested ONLY if EVERY ingredient it needs is in the pantry, in at
 * least the required quantity. One missing or insufficient ingredient and the recipe
 * is excluded.
 *
 * This class is plain Java (no Android classes) so it can be unit tested on its own.
 */
public final class RecipeMatcher {

    /**
     * What to do when a recipe and the pantry measure the same ingredient in units that
     * cannot be compared (e.g. the recipe needs "1 cup" of rice but the pantry holds
     * "2 kg" of it). true = trust that the user has enough; false = treat as missing.
     * Same-kind units (g/kg, ml/l/tsp/tbsp/cup) are ALWAYS converted and compared strictly.
     */
    static final boolean TRUST_INCOMPARABLE_UNITS = true;

    /** Tolerance for floating point rounding (e.g. 0.2 kg versus 200 g). */
    private static final double EPSILON = 1e-9;

    private RecipeMatcher() { }

    /** The outcome of checking one recipe against the pantry. */
    public static final class MatchResult {
        private final Recipe recipe;
        private final List<RecipeIngredient> missing;

        MatchResult(Recipe recipe, List<RecipeIngredient> missing) {
            this.recipe = recipe;
            this.missing = missing;
        }

        public Recipe getRecipe() { return recipe; }

        /** Ingredients that are absent from the pantry or present in too small a quantity. */
        public List<RecipeIngredient> getMissing() { return missing; }

        /** True only when nothing is missing (and the recipe actually has ingredients). */
        public boolean isStrictMatch() {
            return missing.isEmpty() && !recipe.getIngredients().isEmpty();
        }
    }

    /** Recipes that can be cooked right now: every ingredient present in enough quantity. */
    public static List<Recipe> getStrictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> totals = buildPantryTotals(pantry);
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (evaluate(recipe, totals).isStrictMatch()) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    /** Optional bonus: recipes missing exactly ONE ingredient (kept separate from strict matches). */
    public static List<MatchResult> getAlmostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> totals = buildPantryTotals(pantry);
        List<MatchResult> almost = new ArrayList<>();
        for (Recipe recipe : recipes) {
            MatchResult result = evaluate(recipe, totals);
            if (result.getMissing().size() == 1 && recipe.getIngredients().size() > 1) {
                almost.add(result);
            }
        }
        return almost;
    }

    /** Checks every ingredient of one recipe against the pantry totals. */
    static MatchResult evaluate(Recipe recipe, Map<String, Map<String, Double>> totals) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnough(totals, ingredient)) {
                missing.add(ingredient);
            }
        }
        return new MatchResult(recipe, missing);
    }

    /**
     * Adds up the pantry per ingredient: normalised name -> (kind of unit -> total in base unit).
     * Two separate "Eggs" entries are therefore added together.
     */
    static Map<String, Map<String, Double>> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Map<String, Double>> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            if (key.isEmpty()) {
                continue;
            }
            Map<String, Double> byDimension = totals.get(key);
            if (byDimension == null) {
                byDimension = new HashMap<>();
                totals.put(key, byDimension);
            }
            String dimension = UnitConverter.dimensionOf(item.getUnit());
            double base = UnitConverter.toBase(item.getQuantity(), item.getUnit());
            Double existing = byDimension.get(dimension);
            byDimension.put(dimension, (existing == null ? 0 : existing) + base);
        }
        return totals;
    }

    private static boolean hasEnough(Map<String, Map<String, Double>> totals,
                                     RecipeIngredient ingredient) {
        Map<String, Double> byDimension =
                totals.get(IngredientNormalizer.normalize(ingredient.getName()));
        if (byDimension == null) {
            return false; // ingredient not in the pantry at all
        }

        String dimension = UnitConverter.dimensionOf(ingredient.getUnit());
        Double have = byDimension.get(dimension);
        if (have == null) {
            return TRUST_INCOMPARABLE_UNITS; // in the pantry, but measured differently
        }

        double needed = UnitConverter.toBase(ingredient.getQuantity(), ingredient.getUnit());
        return have + EPSILON >= needed;
    }
}
