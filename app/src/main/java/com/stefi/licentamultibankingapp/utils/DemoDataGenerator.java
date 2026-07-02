package com.stefi.licentamultibankingapp.utils;

public class DemoDataGenerator {

    public interface OnFinalizat {
        void onFinalizat();
    }

    // Nu mai genereaza nimic automat
    // Datele se genereaza in OnboardingFragment4 cand userul adauga primul cont
    public static void verificaSiInitializeaza(String firstName, String lastName, OnFinalizat callback) {
        callback.onFinalizat();
    }
}