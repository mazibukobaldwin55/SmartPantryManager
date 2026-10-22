package com.example.my_assigned_application.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities to a common base unit so "0.5 kg" can be compared with "200 g".
 * Base units: grams (mass), millilitres (volume) and pieces (count).
 */
public final class UnitConverter {

    public static final String MASS = "mass";
    public static final String VOLUME = "volume";
    public static final String COUNT = "count";

    private static final Map<String, String> ALIAS_TO_CANONICAL = new HashMap<>();
    private static final Map<String, String> DIMENSION = new HashMap<>();
    private static final Map<String, Double> FACTOR = new HashMap<>();

    static {
        register("g", MASS, 1, "g", "gram", "grams", "gm");
        register("kg", MASS, 1000, "kg", "kgs", "kilo", "kilos", "kilogram", "kilograms");
        register("ml", VOLUME, 1, "ml", "millilitre", "millilitres", "milliliter", "milliliters");
        register("l", VOLUME, 1000, "l", "litre", "litres", "liter", "liters");
        register("tsp", VOLUME, 5, "tsp", "teaspoon", "teaspoons");
        register("tbsp", VOLUME, 15, "tbsp", "tablespoon", "tablespoons");
        register("cup", VOLUME, 250, "cup", "cups");
        register("pcs", COUNT, 1, "pcs", "pc", "piece", "pieces", "whole", "each", "unit", "units");
    }

    private UnitConverter() { }

    private static void register(String canonical, String dimension, double factor,
                                 String... aliases) {
        DIMENSION.put(canonical, dimension);
        FACTOR.put(canonical, factor);
        for (String alias : aliases) {
            ALIAS_TO_CANONICAL.put(alias, canonical);
        }
    }

    /** "Grams" becomes "g". Unknown units are returned lower-cased and trimmed. */
    public static String canonicalUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String u = unit.toLowerCase(Locale.ROOT).trim().replace(".", "");
        String canonical = ALIAS_TO_CANONICAL.get(u);
        return canonical != null ? canonical : u;
    }

    /**
     * Which kind of measurement a unit is: MASS, VOLUME, COUNT, or "other:xyz" for an
     * unknown unit (which can then only be compared with the identical unit).
     */
    public static String dimensionOf(String unit) {
        String canonical = canonicalUnit(unit);
        String dimension = DIMENSION.get(canonical);
        return dimension != null ? dimension : "other:" + canonical;
    }

    /** Converts a quantity to its base unit (g, ml or pcs). */
    public static double toBase(double quantity, String unit) {
        Double factor = FACTOR.get(canonicalUnit(unit));
        return quantity * (factor != null ? factor : 1.0);
    }
}