package com.example.my_assigned_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.my_assigned_application.R;
import com.example.my_assigned_application.util.SettingsManager;

/** Lets the user change app preferences. Every change is saved immediately. */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SettingsManager settings = new SettingsManager(requireContext());

        SwitchCompat switchAlerts = view.findViewById(R.id.switchAlerts);
        Spinner spinnerDays = view.findViewById(R.id.spinnerAlertDays);
        Spinner spinnerUnit = view.findViewById(R.id.spinnerDefaultUnit);

        // --- Alert window dropdown (1, 3 or 7 days) ---
        int[] dayValues = getResources().getIntArray(R.array.alert_window_values);
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.alert_window_labels,
                android.R.layout.simple_spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(daysAdapter);
        spinnerDays.setSelection(indexOf(dayValues, settings.getAlertDays()));

        // --- Default unit dropdown (same list as the Add screen) ---
        String[] units = getResources().getStringArray(R.array.units);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setSelection(indexOf(units, settings.getDefaultUnit()));

        // --- Show the saved state ---
        switchAlerts.setChecked(settings.isExpiryAlertsEnabled());
        updateWindowEnabled(spinnerDays, switchAlerts.isChecked());

        // --- Save changes as soon as they happen ---
        switchAlerts.setOnCheckedChangeListener((button, checked) -> {
            settings.setExpiryAlertsEnabled(checked);
            updateWindowEnabled(spinnerDays, checked);
        });

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                settings.setAlertDays(dayValues[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                settings.setDefaultUnit(units[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /** The alert-window dropdown only makes sense while alerts are switched on. */
    private void updateWindowEnabled(Spinner spinner, boolean enabled) {
        spinner.setEnabled(enabled);
        spinner.setAlpha(enabled ? 1f : 0.4f);
    }

    private static int indexOf(int[] values, int target) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == target) {
                return i;
            }
        }
        return 0;
    }

    private static int indexOf(String[] values, String target) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(target)) {
                return i;
            }
        }
        return 0;
    }
}
