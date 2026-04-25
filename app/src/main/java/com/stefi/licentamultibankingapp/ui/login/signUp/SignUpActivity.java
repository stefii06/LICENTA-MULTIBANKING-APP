package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpFragment1;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpFragment2;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpFragment3;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpFragment4;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpFragment5;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpNavigator;

public class SignUpActivity extends AppCompatActivity implements SignUpNavigator {

    private int currentStep = 1;
    private Bundle userData = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        loadFragment(new SignUpFragment1(), false);
    }

    @Override
    public void nextStep(Bundle data) {
        if (data != null) userData.putAll(data);

        currentStep++;
        switch (currentStep) {
            case 2:
                loadFragment(new SignUpFragment2(), true);
                break;
            case 3:
                loadFragment(new SignUpFragment3(), true);
                break;
            case 4:
                loadFragment(new SignUpFragment4(), true);
                break;
            case 5:
                SignUpFragment5 fragment5 = new SignUpFragment5();
                fragment5.setArguments(userData);
                loadFragment(fragment5, true);
                break;
        }
    }

    @Override
    public void previousStep() {
        currentStep--;
        getSupportFragmentManager().popBackStack();
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}