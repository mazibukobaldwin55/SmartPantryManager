package com.example.my_assigned_application.util;

import java.util.Calendar;

/**
 * Works out whether a pantry item is expired or about to expire.
 * Dates are stored as "yyyy-MM-dd" text. Plain Java (no Android classes), so it is unit tested.
 */
public final class ExpiryChecker {

    /** How urgent an item's expiry date is. */
    public enum Status { NO_DATE, OK, EXPIRING_SOON, EXPIRED }

    private static final double MILLIS_PER_DAY = 24.0 * 60 * 60 * 1000;

    private ExpiryChecker() { }

    /**
     * Whole days from today until the expiry date: 0 = today, 1 = tomorrow, -1 = yesterday.
     * Returns null when there is no date or it cannot be read (e.g. "2026-02-30").
     */
    public static Integer daysUntilExpiry(String expiryDate, Calendar today) {
        if (expiryDate == null) {
            return null;
        }
        String[] parts = expiryDate.trim().split("-");
        if (parts.length != 3) {
            return null;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar expiry = Calendar.getInstance();
            expiry.clear();
            expiry.setLenient(false); // rejects impossible dates such as month 13
            expiry.set(year, month - 1, day);

            Calendar start = Calendar.getInstance();
            start.clear();
            start.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));

            long difference = expiry.getTimeInMillis() - start.getTimeInMillis();
            return (int) Math.round(difference / MILLIS_PER_DAY);
        } catch (IllegalArgumentException e) { // bad number or impossible date
            return null;
        }
    }

    public static Integer daysUntilExpiry(String expiryDate) {
        return daysUntilExpiry(expiryDate, Calendar.getInstance());
    }

    /** EXPIRED if the date has passed, EXPIRING_SOON if it falls within alertDays days from today. */
    public static Status statusOf(String expiryDate, int alertDays, Calendar today) {
        Integer days = daysUntilExpiry(expiryDate, today);
        if (days == null) {
            return Status.NO_DATE;
        }
        if (days < 0) {
            return Status.EXPIRED;
        }
        return days <= alertDays ? Status.EXPIRING_SOON : Status.OK;
    }

    public static Status statusOf(String expiryDate, int alertDays) {
        return statusOf(expiryDate, alertDays, Calendar.getInstance());
    }
}
