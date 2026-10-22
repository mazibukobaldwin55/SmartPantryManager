package com.example.my_assigned_application.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.model.Recipe;
import com.example.my_assigned_application.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Unit tests for the strict-matching rule (Section 2.3 of the brief). */
public class RecipeMatcherTest {

    // ---------- small helpers to keep the tests readable ----------

    private static RecipeIngredient ing(String name, double qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    private static PantryItem item(String name, double qty, String unit) {
        return new PantryItem(name, qty, unit, null);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe r = new Recipe(1, name, "steps");
        for (RecipeIngredient i : ingredients) {
            r.addIngredient(i);
        }
        return r;
    }

    private static Recipe eggsOnToast() {
        return recipe("Scrambled Eggs on Toast",
                ing("eggs", 2, "pcs"), ing("bread", 2, "pcs"), ing("butter", 10, "g"));
    }

    private static boolean isSuggested(Recipe recipe, PantryItem... pantry) {
        List<Recipe> result = RecipeMatcher.getStrictMatches(
                Collections.singletonList(recipe), Arrays.asList(pantry));
        return result.size() == 1;
    }

    // ---------- THE STRICT RULE ----------

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        assertTrue(isSuggested(eggsOnToast(),
                item("Eggs", 6, "pcs"), item("Bread", 4, "pcs"), item("Butter", 250, "g")));
    }

    @Test
    public void fourOfFiveIngredients_recipeIsNotSuggested() {
        Recipe five = recipe("Tomato Pasta",
                ing("pasta", 200, "g"), ing("tomatoes", 4, "pcs"), ing("onion", 1, "pcs"),
                ing("garlic", 2, "pcs"), ing("olive oil", 2, "tbsp"));
        assertFalse(isSuggested(five,
                item("Pasta", 500, "g"), item("Tomatoes", 6, "pcs"),
                item("Onion", 2, "pcs"), item("Garlic", 5, "pcs")));
    }

    @Test
    public void addingTheMissingIngredient_makesTheRecipeAppear() {
        Recipe r = eggsOnToast();
        assertFalse(isSuggested(r, item("Eggs", 6, "pcs"), item("Bread", 4, "pcs")));
        assertTrue(isSuggested(r, item("Eggs", 6, "pcs"), item("Bread", 4, "pcs"),
                item("Butter", 100, "g")));
    }

    @Test
    public void removingAnIngredient_makesTheRecipeDisappear() {
        Recipe r = eggsOnToast();
        List<PantryItem> pantry = new ArrayList<>(Arrays.asList(
                item("Eggs", 6, "pcs"), item("Bread", 4, "pcs"), item("Butter", 100, "g")));
        assertEquals(1, RecipeMatcher.getStrictMatches(Collections.singletonList(r), pantry).size());

        pantry.remove(2); // user deletes the butter
        assertEquals(0, RecipeMatcher.getStrictMatches(Collections.singletonList(r), pantry).size());
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        assertFalse(isSuggested(eggsOnToast()));
    }

    @Test
    public void recipeWithNoIngredients_isNeverSuggested() {
        assertFalse(isSuggested(recipe("Empty recipe"), item("Eggs", 6, "pcs")));
    }

    // ---------- QUANTITIES ----------

    @Test
    public void notEnoughQuantity_recipeIsNotSuggested() {
        assertFalse(isSuggested(eggsOnToast(),
                item("Eggs", 1, "pcs"), item("Bread", 4, "pcs"), item("Butter", 100, "g")));
    }

    @Test
    public void exactlyTheRequiredQuantity_recipeIsSuggested() {
        assertTrue(isSuggested(eggsOnToast(),
                item("Eggs", 2, "pcs"), item("Bread", 2, "pcs"), item("Butter", 10, "g")));
    }

    @Test
    public void twoEntriesOfTheSameIngredient_areAddedTogether() {
        assertTrue(isSuggested(eggsOnToast(),
                item("Eggs", 1, "pcs"), item("Eggs", 1, "pcs"),
                item("Bread", 2, "pcs"), item("Butter", 10, "g")));
    }

    // ---------- REAL-WORLD MESSINESS ----------

    @Test
    public void singularAndPluralNames_stillMatch() {
        Recipe r = recipe("Tomato Soup", ing("tomatoes", 2, "pcs"));
        assertTrue(isSuggested(r, item("Tomato", 3, "pcs")));
        assertTrue(isSuggested(recipe("Soup", ing("tomato", 2, "pcs")), item("TOMATOES", 3, "pcs")));
    }

    @Test
    public void caseAndExtraSpaces_areIgnored() {
        assertTrue(isSuggested(recipe("R", ing("olive oil", 1, "tbsp")),
                item("  Olive   OIL ", 500, "ml")));
    }

    @Test
    public void kilogramsAreComparedWithGrams() {
        Recipe r = recipe("Pancakes", ing("flour", 200, "g"));
        assertTrue(isSuggested(r, item("Flour", 0.5, "kg")));
        assertFalse(isSuggested(r, item("Flour", 0.1, "kg")));
        assertTrue(isSuggested(r, item("Flour", 0.2, "kg"))); // 0.2 kg is exactly 200 g
    }

    @Test
    public void litresTablespoonsAndCupsAreComparedAsVolume() {
        Recipe r = recipe("Pasta", ing("olive oil", 2, "tbsp"));
        assertTrue(isSuggested(r, item("Olive oil", 1, "l")));
        assertFalse(isSuggested(r, item("Olive oil", 1, "tsp")));
        assertTrue(isSuggested(recipe("Rice", ing("milk", 1, "cup")), item("Milk", 500, "ml")));
    }

    @Test
    public void incomparableUnits_areTrustedWhenTheIngredientIsInThePantry() {
        // recipe measures rice in cups, pantry holds it in kg (different kinds of unit)
        assertTrue(isSuggested(recipe("Rice", ing("rice", 1, "cup")), item("Rice", 2, "kg")));
    }

    @Test
    public void similarButDifferentIngredients_areNotConfused() {
        assertFalse(isSuggested(recipe("R", ing("rice", 100, "g")), item("Rice flour", 500, "g")));
        assertFalse(isSuggested(recipe("R", ing("pepper", 1, "tsp")), item("Bell pepper", 2, "pcs")));
        assertFalse(isSuggested(recipe("R", ing("butter", 10, "g")), item("Peanut butter", 200, "g")));
    }

    // ---------- OPTIONAL "ALMOST THERE" LIST ----------

    @Test
    public void almostThere_listsRecipesMissingExactlyOneIngredient() {
        List<Recipe> recipes = Collections.singletonList(eggsOnToast());

        List<PantryItem> missingOne = Arrays.asList(item("Eggs", 6, "pcs"), item("Bread", 4, "pcs"));
        assertEquals(0, RecipeMatcher.getStrictMatches(recipes, missingOne).size());
        List<RecipeMatcher.MatchResult> almost = RecipeMatcher.getAlmostThere(recipes, missingOne);
        assertEquals(1, almost.size());
        assertEquals("butter", almost.get(0).getMissing().get(0).getName());

        List<PantryItem> missingTwo = Collections.singletonList(item("Eggs", 6, "pcs"));
        assertEquals(0, RecipeMatcher.getAlmostThere(recipes, missingTwo).size());
    }
}
