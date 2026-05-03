package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stefi.licentamultibankingapp.R;

import com.stefi.licentamultibankingapp.ui.dashboard.home.HomeFragment;

import com.stefi.licentamultibankingapp.ui.dashboard.CardsFragment;
import com.stefi.licentamultibankingapp.ui.dashboard.TransactionsFragment;
import com.stefi.licentamultibankingapp.ui.dashboard.StatisticsFragment;
import com.stefi.licentamultibankingapp.ui.dashboard.ProfileFragment;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Pornim cu HomeFragment (il cream dupa)
        loadFragment(new HomeFragment());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_cards) {
                 loadFragment(new CardsFragment());
            } else if (id == R.id.nav_transactions) {
                 loadFragment(new TransactionsFragment());
            } else if (id == R.id.nav_statistics) {
                loadFragment(new StatisticsFragment());
            } else if (id == R.id.nav_profile) {
                 loadFragment(new ProfileFragment());
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}