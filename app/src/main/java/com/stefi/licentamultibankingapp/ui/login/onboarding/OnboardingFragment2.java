package com.stefi.licentamultibankingapp.ui.login.onboarding;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;

public class OnboardingFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_fragment2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Preluam datele bancii din OnboardingActivity
        OnboardingActivity activity = (OnboardingActivity) getActivity();
        String banca = activity.getBancaSelectata();
        String culoare = activity.getCuloareBanca();

        // Setam logo-ul bancii cu culoarea corporativa
        TextView tvLogo = view.findViewById(R.id.tvLogoBanca);
        tvLogo.setText(getAbreviereBanca(banca));
        tvLogo.setBackgroundColor(Color.parseColor(culoare));

        // Setam titlul dinamic
        TextView tvTitlu = view.findViewById(R.id.tvTitluAutorizare);
        tvTitlu.setText("Autorizare " + banca);

        Button btnAutorizeaza = view.findViewById(R.id.btnAutorizeaza);
        btnAutorizeaza.setOnClickListener(v -> {
            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).mergeInainte();
            }
        });
    }

    // Returneaza abrevierea pentru logo
    private String getAbreviereBanca(String banca) {
        switch (banca) {
            case "BCR": return "BCR";
            case "ING": return "ING";
            case "BRD": return "BRD";
            case "Raiffeisen": return "RAI";
            case "Banca Transilvania": return "BT";
            case "CEC Bank": return "CEC";
            default: return banca.substring(0, Math.min(3, banca.length())).toUpperCase();
        }
    }
}