package com.example.my_assigned_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.my_assigned_application.R;

/** Placeholder: the real settings screen is built in Step 8. */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        ((TextView) view.findViewById(R.id.textPlaceholder))
                .setText("Settings will appear here.");
        return view;
    }
}
