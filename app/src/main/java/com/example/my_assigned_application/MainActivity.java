package com.example.my_assigned_application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.my_assigned_application.ui.PantryFragment;
import com.example.my_assigned_application.ui.SettingsFragment;
import com.example.my_assigned_application.ui.SuggestedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Host activity: shows one fragment at a time and switches them from the bottom bar. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment;
            int title;

            if (id == R.id.nav_pantry) {
                fragment = new PantryFragment();
                title = R.string.nav_pantry;
            } else if (id == R.id.nav_suggested) {
                fragment = new SuggestedFragment();
                title = R.string.nav_suggested;
            } else {
                fragment = new SettingsFragment();
                title = R.string.nav_settings;
            }

            setTitle(title);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        // Show the Pantry tab on first launch (not again after a rotation)
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_pantry);
        }
    }
}