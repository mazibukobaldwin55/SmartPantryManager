package com.example.my_assigned_application.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Turns ingredient names into a comparable "key" so that simple real-world
 * differences ("Tomatoes", "tomato ", "TOMATO") do not break matching.
 *
 * Both the pantry name and the recipe name go through the same method, so the
 * result only has to be CONSISTENT, not linguistically perfect.
 */
public final class IngredientNormalizer {

    private static final Map<String, String> IRREGULAR = new HashMap<>();

    static {
        IRREGULAR.put("loaves", "loaf");
        IRREGULAR.put("leaves", "leaf");
        IRREGULAR.put("halves", "half");
    }

    private IngredientNormalizer() { }

    /** "  Bell Peppers (red) " becomes "bell pepper". */
    public static String normalize(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.toLowerCase(Locale.ROOT)
                .replaceAll("\\(.*?\\)", " ")      // drop notes in brackets
                .replaceAll("[^\\p{L}\\s]", " ")   // drop digits and punctuation
                .trim();
        if (cleaned.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (String word : cleaned.split("\\s+")) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(singularize(word));
        }
        return result.toString();
    }

    /** Very small English singulariser covering common food words. */
    static String singularize(String word) {
        String irregular = IRREGULAR.get(word);
        if (irregular != null) {
            return irregular;
        }

        int n = word.length();
        if (n <= 3) {
            return word;
        }

        // cherries -> cherry, and cookie -> cooky so "cookie" and "cookies" still match
        if (word.endsWith("ies") && n > 4) {
            return word.substring(0, n - 3) + "y";
        }
        if (word.endsWith("ie")) {
            return word.substring(0, n - 2) + "y";
        }
        // tomatoes -> tomato, potatoes -> potato
        if (word.endsWith("oes")) {
            return word.substring(0, n - 2);
        }
        // peaches -> peach, radishes -> radish, boxes -> box, glasses -> glass
        if (word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, n - 2);
        }
        // hummus, asparagus, swiss stay as they are
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }
        // eggs -> egg, onions -> onion
        if (word.endsWith("s")) {
            return word.substring(0, n - 1);
        }
        return word;
    }
}
