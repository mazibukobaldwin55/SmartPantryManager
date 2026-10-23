package com.example.my_assigned_application.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Reads and writes the user's settings, which persist on the device via SharedPreferences. */
public class SettingsManager {

    public static final int DEFAULT_ALERT_DAYS = 3;
    public static final String DEFAULT_UNIT = "g";

    private static final String PREFS_NAME = "pantry_settings";
    private static final String KEY_ALERTS_ENABLED = "expiry_alerts_enabled";
    private static final String KEY_ALERT_DAYS = "expiry_alert_days";
    private static final String KEY_DEFAULT_UNIT = "default_unit";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return prefs.getBoolean(KEY_ALERTS_ENABLED, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply();
    }

    /** How many days ahead counts as "expiring soon". */
    public int getAlertDays() {
        return prefs.getInt(KEY_ALERT_DAYS, DEFAULT_ALERT_DAYS);
    }

    public void setAlertDays(int days) {
        prefs.edit().putInt(KEY_ALERT_DAYS, days).apply();
    }

    /** The unit pre-selected when adding a new ingredient. */
    public String getDefaultUnit() {
        return prefs.getString(KEY_DEFAULT_UNIT, DEFAULT_UNIT);
    }

    public void setDefaultUnit(String unit) {
        prefs.edit().putString(KEY_DEFAULT_UNIT, unit).apply();
    }
}