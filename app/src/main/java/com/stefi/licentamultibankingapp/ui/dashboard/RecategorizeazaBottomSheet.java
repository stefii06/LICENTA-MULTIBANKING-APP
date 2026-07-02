package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Tranzactie;

public class RecategorizeazaBottomSheet extends BottomSheetDialogFragment {

    private Tranzactie tranzactie;
    private OnRecategorizat listener;

    public interface OnRecategorizat {
        void onRecategorizat(String categorie, String emoji);
    }

    public void setTranzactie(Tranzactie tranzactie) {
        this.tranzactie = tranzactie;
    }

    public void setListener(OnRecategorizat listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_recategorizeaza, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategorie(view, R.id.recatMancare, "Mancare", "🍔");
        setupCategorie(view, R.id.recatShopping, "Shopping", "🛍");
        setupCategorie(view, R.id.recatTransport, "Transport", "🚗");
        setupCategorie(view, R.id.recatDivertisment, "Divertisment", "🎬");
        setupCategorie(view, R.id.recatSanatate, "Sanatate", "🏥");
        setupCategorie(view, R.id.recatUtilitati, "Utilitati", "💡");

        view.findViewById(R.id.btnAnuleazaRecat).setOnClickListener(v -> dismiss());
    }

    private void setupCategorie(View view, int id, String categorie, String emoji) {
        view.findViewById(id).setOnClickListener(v -> {
            if (listener != null) listener.onRecategorizat(categorie, emoji);
            dismiss();
        });
    }
}