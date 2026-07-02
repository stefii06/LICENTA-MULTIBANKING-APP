package com.stefi.licentamultibankingapp.ui.login.onboarding;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;

public class OnboardingFragment1 extends Fragment {

    private String bancaSelectata = "";
    private String culoareSelectata = "";
    private Button btnContinua;

    // Retinem referintele la checks
    private TextView checkBCR, checkING, checkBRD, checkRaiffeisen, checkBT, checkCEC;
    private LinearLayout optionBCR, optionING, optionBRD, optionRaiffeisen, optionBT, optionCEC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_fragment1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnContinua = view.findViewById(R.id.btnContinuaOnboarding1);

        optionBCR = view.findViewById(R.id.optionBCR);
        optionING = view.findViewById(R.id.optionING);
        optionBRD = view.findViewById(R.id.optionBRD);
        optionRaiffeisen = view.findViewById(R.id.optionRaiffeisen);
        optionBT = view.findViewById(R.id.optionBT);
        optionCEC = view.findViewById(R.id.optionCEC);

        checkBCR = view.findViewById(R.id.checkBCR);
        checkING = view.findViewById(R.id.checkING);
        checkBRD = view.findViewById(R.id.checkBRD);
        checkRaiffeisen = view.findViewById(R.id.checkRaiffeisen);
        checkBT = view.findViewById(R.id.checkBT);
        checkCEC = view.findViewById(R.id.checkCEC);

        optionBCR.setOnClickListener(v -> selecteazaBanca("BCR", "#E53935", optionBCR, checkBCR));
        optionING.setOnClickListener(v -> selecteazaBanca("ING", "#FF6D00", optionING, checkING));
        optionBRD.setOnClickListener(v -> selecteazaBanca("BRD", "#1565C0", optionBRD, checkBRD));
        optionRaiffeisen.setOnClickListener(v -> selecteazaBanca("Raiffeisen", "#FFD600", optionRaiffeisen, checkRaiffeisen));
        optionBT.setOnClickListener(v -> selecteazaBanca("Banca Transilvania", "#003087", optionBT, checkBT));
        optionCEC.setOnClickListener(v -> selecteazaBanca("CEC Bank", "#006400", optionCEC, checkCEC));

        btnContinua.setOnClickListener(v -> {
            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).mergeInainte(bancaSelectata, culoareSelectata);
            }
        });
    }

    private void selecteazaBanca(String banca, String culoare,
                                 LinearLayout optionSelectat, TextView checkSelectat) {
        // Resetam toate optiunile
        resetOption(optionBCR, checkBCR);
        resetOption(optionING, checkING);
        resetOption(optionBRD, checkBRD);
        resetOption(optionRaiffeisen, checkRaiffeisen);
        resetOption(optionBT, checkBT);
        resetOption(optionCEC, checkCEC);

        // Evidentiem optiunea selectata
        optionSelectat.setBackgroundColor(Color.parseColor("#4A90D9"));
        checkSelectat.setVisibility(View.VISIBLE);

        bancaSelectata = banca;
        culoareSelectata = culoare;

        // Activam butonul Continua
        btnContinua.setEnabled(true);
        btnContinua.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
    }

    private void resetOption(LinearLayout option, TextView check) {
        option.setBackgroundColor(Color.parseColor("#1A3C6E"));
        check.setVisibility(View.GONE);
    }
}