package com.stefi.licentamultibankingapp.ui.login.onboarding;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stefi.licentamultibankingapp.R;

public class OnboardingActivity extends AppCompatActivity {

    private int pasulCurent = 1;
    private String bancaSelectata = "";
    private String culoareBanca = "";

    private View segment1, segment2, segment3, segment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        segment1 = findViewById(R.id.segment1);
        segment2 = findViewById(R.id.segment2);
        segment3 = findViewById(R.id.segment3);
        segment4 = findViewById(R.id.segment4);

        incarcaFragment(new OnboardingFragment1(), false);
    }

    public void mergeInainte(String banca, String culoare) {
        bancaSelectata = banca;
        culoareBanca = culoare;
        pasulCurent++;
        actualizeazaProgress();
        incarcaFragment(getFragmentPentruPas(), true);
    }

    public void mergeInainte() {
        pasulCurent++;
        actualizeazaProgress();
        incarcaFragment(getFragmentPentruPas(), true);
    }

    public String getBancaSelectata() { return bancaSelectata; }
    public String getCuloareBanca() { return culoareBanca; }

    public void onboardingFinalizat() {
        // Salvam flag-ul ca onboarding-ul a fost completat — per user
        String userId = com.google.firebase.auth.FirebaseAuth
                .getInstance().getCurrentUser().getUid();
        SharedPreferences prefs = getSharedPreferences("finmind_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_completat_" + userId, true).apply();

        android.content.Intent intent = new android.content.Intent(
                this,
                com.stefi.licentamultibankingapp.ui.dashboard.DashboardActivity.class
        );
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Fragment getFragmentPentruPas() {
        switch (pasulCurent) {
            case 2: return new OnboardingFragment2();
            case 3: return new OnboardingFragment3();
            case 4: return new OnboardingFragment4();
            default: return new OnboardingFragment1();
        }
    }

    private void actualizeazaProgress() {
        int activ = Color.parseColor("#4A90D9");
        int inactiv = Color.parseColor("#1A3C6E");

        segment1.setBackgroundColor(pasulCurent >= 1 ? activ : inactiv);
        segment2.setBackgroundColor(pasulCurent >= 2 ? activ : inactiv);
        segment3.setBackgroundColor(pasulCurent >= 3 ? activ : inactiv);
        segment4.setBackgroundColor(pasulCurent >= 4 ? activ : inactiv);
    }

    private void incarcaFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.onboardingContainer, fragment);

        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}