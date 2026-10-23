package com.example.my_assigned_application.util;

import java.util.Locale;

/** Small helper for tidying text shown on screen. */
public final class DisplayText {

    private DisplayText() { }

    /** "eggs" becomes "Eggs". */
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }
}
