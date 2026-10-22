package com.example.my_assigned_application.util;

/** Small helper for displaying quantities nicely. */
public final class QuantityFormat {

    private QuantityFormat() { }

    /** 4.0 becomes "4", 0.5 stays "0.5". */
    public static String format(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
