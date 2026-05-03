package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;

public class AdaugaContBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_adauga_cont, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.optionBCR).setOnClickListener(v -> {
            dismiss();
            deschideFormularBanca("BCR", "#E53935");
        });

        view.findViewById(R.id.optionBRD).setOnClickListener(v -> {
            dismiss();
            deschideFormularBanca("BRD", "#1565C0");
        });

        view.findViewById(R.id.optionING).setOnClickListener(v -> {
            dismiss();
            deschideFormularBanca("ING", "#FF6D00");
        });

        view.findViewById(R.id.optionRaiffeisen).setOnClickListener(v -> {
            dismiss();
            deschideFormularBanca("Raiffeisen", "#FFD600");
        });

        view.findViewById(R.id.optionAnuleazaCont).setOnClickListener(v -> dismiss());
    }

    private void deschideFormularBanca(String numeBanca, String culoare) {
        FormularContFragment formular = new FormularContFragment();
        Bundle args = new Bundle();
        args.putString("numeBanca", numeBanca);
        args.putString("culoare", culoare);
        formular.setArguments(args);
        formular.show(getParentFragmentManager(), "FormularCont");
    }
}