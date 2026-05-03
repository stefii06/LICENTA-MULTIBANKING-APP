package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;

public class CategorieBottomSheet extends BottomSheetDialogFragment {

    private String categorieSelectata = "Altele";
    private String emojiSelectat = "•";
    private View categorieActiva = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_categorie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategorie(view, R.id.catMancare, "Mancare", "🍔");
        setupCategorie(view, R.id.catShopping, "Shopping", "🛍");
        setupCategorie(view, R.id.catTransport, "Transport", "🚗");
        setupCategorie(view, R.id.catDivertisment, "Divertisment", "🎬");
        setupCategorie(view, R.id.catSanatate, "Sanatate", "🏥");
        setupCategorie(view, R.id.catAltele, "Altele", "•");

        Button btnConfirma = view.findViewById(R.id.btnConfirmaPlata);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaCategorie);

        btnConfirma.setOnClickListener(v -> {
            PlataAprobataBottomSheet plataSheet = new PlataAprobataBottomSheet();
            Bundle args = new Bundle();
            args.putString("numeBanca", getArguments().getString("numeBanca", ""));
            args.putString("ultimeleCifre", getArguments().getString("ultimeleCifre", ""));
            args.putString("titular", getArguments().getString("titular", ""));
            args.putString("tipCard", getArguments().getString("tipCard", ""));
            args.putString("culoare", getArguments().getString("culoare", "#1A3C6E"));
            args.putString("categorie", categorieSelectata);
            args.putString("emoji", emojiSelectat);
            plataSheet.setArguments(args);
            plataSheet.show(getParentFragmentManager(), "PlataAprobata");
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }

    private void setupCategorie(View view, int id, String nume, String emoji) {
        View cat = view.findViewById(id);
        cat.setOnClickListener(v -> {
            categorieSelectata = nume;
            emojiSelectat = emoji;

            if (categorieActiva != null) {
                categorieActiva.setBackgroundColor(0xFF12203A);
            }
            cat.setBackgroundColor(0xFF1A3C6E);
            categorieActiva = cat;
        });
    }
}