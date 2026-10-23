package com.example.my_assigned_application.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Calendar;

/** Unit tests for the expiring-soon logic used by the pantry list and the settings screen. */
public class ExpiryCheckerTest {

    /** A fixed "today" so the tests give the same result on any day. */
    private static Calendar day(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month - 1, dayOfMonth);
        return c;
    }

    private static final Calendar TODAY = day(2026, 10, 23);

    @Test
    public void countsDaysUntilExpiry() {
        assertEquals(Integer.valueOf(0), ExpiryChecker.daysUntilExpiry("2026-10-23", TODAY));
        assertEquals(Integer.valueOf(1), ExpiryChecker.daysUntilExpiry("2026-10-24", TODAY));
        assertEquals(Integer.valueOf(7), ExpiryChecker.daysUntilExpiry("2026-10-30", TODAY));
        assertEquals(Integer.valueOf(-1), ExpiryChecker.daysUntilExpiry("2026-10-22", TODAY));
    }

    @Test
    public void worksAcrossMonthAndYearBoundaries() {
        assertEquals(Integer.valueOf(2), ExpiryChecker.daysUntilExpiry("2026-11-02", day(2026, 10, 31)));
        assertEquals(Integer.valueOf(1), ExpiryChecker.daysUntilExpiry("2027-01-01", day(2026, 12, 31)));
        assertEquals(Integer.valueOf(2), ExpiryChecker.daysUntilExpiry("2028-03-01", day(2028, 2, 28))); // leap year
    }

    @Test
    public void expiredItems_areFlaggedExpired() {
        assertEquals(ExpiryChecker.Status.EXPIRED, ExpiryChecker.statusOf("2026-10-22", 3, TODAY));
        assertEquals(ExpiryChecker.Status.EXPIRED, ExpiryChecker.statusOf("2025-01-01", 3, TODAY));
    }

    @Test
    public void itemsInsideTheAlertWindow_areExpiringSoon() {
        assertEquals(ExpiryChecker.Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-23", 3, TODAY)); // today
        assertEquals(ExpiryChecker.Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-26", 3, TODAY)); // exactly 3 days
    }

    @Test
    public void itemsOutsideTheAlertWindow_areOk() {
        assertEquals(ExpiryChecker.Status.OK, ExpiryChecker.statusOf("2026-10-27", 3, TODAY));
    }

    @Test
    public void changingTheAlertWindow_changesTheResult() {
        assertEquals(ExpiryChecker.Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-25", 3, TODAY));
        assertEquals(ExpiryChecker.Status.OK, ExpiryChecker.statusOf("2026-10-25", 1, TODAY));
        assertEquals(ExpiryChecker.Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-30", 7, TODAY));
    }

    @Test
    public void missingOrInvalidDates_haveNoStatus() {
        assertEquals(ExpiryChecker.Status.NO_DATE, ExpiryChecker.statusOf(null, 3, TODAY));
        assertEquals(ExpiryChecker.Status.NO_DATE, ExpiryChecker.statusOf("", 3, TODAY));
        assertEquals(ExpiryChecker.Status.NO_DATE, ExpiryChecker.statusOf("soon", 3, TODAY));
        assertEquals(ExpiryChecker.Status.NO_DATE, ExpiryChecker.statusOf("2026-13-45", 3, TODAY));
        assertEquals(ExpiryChecker.Status.NO_DATE, ExpiryChecker.statusOf("2026-02-30", 3, TODAY));
        assertTrue(ExpiryChecker.daysUntilExpiry("2026/10/23", TODAY) == null);
    }
}
