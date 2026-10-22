package com.example.my_assigned_application.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Unit tests for ingredient-name and unit normalisation. */
public class IngredientNormalizerTest {

    private static void assertSame(String a, String b) {
        assertEquals(IngredientNormalizer.normalize(a), IngredientNormalizer.normalize(b));
    }

    @Test
    public void pluralsMatchSingulars() {
        assertSame("tomato", "Tomatoes");
        assertSame("potato", "potatoes");
        assertSame("egg", "Eggs");
        assertSame("bell pepper", "Bell Peppers");
        assertSame("cherry", "cherries");
        assertSame("cookie", "cookies");
        assertSame("cheese", "cheeses");
        assertSame("clove", "cloves");
        assertSame("loaf", "loaves");
        assertSame("peach", "peaches");
        assertSame("onion", "onions");
        assertSame("olive", "olives");
    }

    @Test
    public void ignoresCaseSpacesAndBrackets() {
        assertEquals("garlic", IngredientNormalizer.normalize("  GARLIC (chopped) "));
        assertEquals("olive oil", IngredientNormalizer.normalize("Olive    Oil"));
    }

    @Test
    public void tomatoesBecomeTomato() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
    }

    @Test
    public void wordsThatEndInSButAreNotPluralsAreKept() {
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
        assertEquals("asparagus", IngredientNormalizer.normalize("Asparagus"));
    }

    @Test
    public void differentIngredientsStayDifferent() {
        assertFalse(IngredientNormalizer.normalize("rice").equals(IngredientNormalizer.normalize("rice flour")));
    }

    @Test
    public void nullAndBlankNamesGiveAnEmptyKey() {
        assertEquals("", IngredientNormalizer.normalize(null));
        assertEquals("", IngredientNormalizer.normalize("   "));
        assertEquals("", IngredientNormalizer.normalize("123"));
    }

    @Test
    public void unitsConvertToBaseUnits() {
        assertEquals(1000.0, UnitConverter.toBase(1, "kg"), 1e-9);
        assertEquals(1000.0, UnitConverter.toBase(1, "l"), 1e-9);
        assertEquals(15.0, UnitConverter.toBase(1, "tbsp"), 1e-9);
        assertEquals(5.0, UnitConverter.toBase(1, "tsp"), 1e-9);
        assertEquals(250.0, UnitConverter.toBase(1, "cup"), 1e-9);
        assertEquals(3.0, UnitConverter.toBase(3, "pcs"), 1e-9);
    }

    @Test
    public void unitAliasesAreUnderstood() {
        assertEquals("g", UnitConverter.canonicalUnit("Grams"));
        assertEquals("kg", UnitConverter.canonicalUnit("KG."));
        assertEquals("tbsp", UnitConverter.canonicalUnit("tablespoons"));
        assertEquals("pcs", UnitConverter.canonicalUnit("pieces"));
    }

    @Test
    public void unitsAreGroupedByKind() {
        assertEquals(UnitConverter.MASS, UnitConverter.dimensionOf("kg"));
        assertEquals(UnitConverter.VOLUME, UnitConverter.dimensionOf("cup"));
        assertEquals(UnitConverter.COUNT, UnitConverter.dimensionOf("pcs"));
        assertTrue(UnitConverter.dimensionOf("bunch").startsWith("other:"));
    }
}
