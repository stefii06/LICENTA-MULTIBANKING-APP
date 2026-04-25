package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;

public interface SignUpNavigator {
    void nextStep(Bundle data);
    void previousStep();
}