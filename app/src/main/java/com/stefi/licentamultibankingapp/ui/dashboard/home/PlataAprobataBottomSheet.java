package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlataAprobataBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_plata_aprobata, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String numeBanca = getArguments().getString("numeBanca", "");
        String ultimeleCifre = getArguments().getString("ultimeleCifre", "");
        String titular = getArguments().getString("titular", "");
        String tipCard = getArguments().getString("tipCard", "");
        String culoare = getArguments().getString("culoare", "#1A3C6E");
        String categorie = getArguments().getString("categorie", "Altele");
        String emoji = getArguments().getString("emoji", "");

        // Setam datele cardului
        View layoutCard = view.findViewById(R.id.layoutCardAprobat);
        TextView tvBanca = view.findViewById(R.id.tvCardAprobatBanca);
        TextView tvNumar = view.findViewById(R.id.tvCardAprobatNumar);
        TextView tvTitular = view.findViewById(R.id.tvCardAprobatTitular);
        TextView tvTip = view.findViewById(R.id.tvCardAprobatTip);

        try {
            layoutCard.setBackgroundColor(Color.parseColor(culoare));
        } catch (Exception e) {
            layoutCard.setBackgroundColor(Color.parseColor("#1A3C6E"));
        }

        tvBanca.setText(numeBanca + " — " + tipCard);
        tvNumar.setText("•••• •••• •••• " + ultimeleCifre);
        tvTitular.setText(titular);
        tvTip.setText(tipCard);

        View layoutLoading = view.findViewById(R.id.layoutLoading);
        View layoutAprobat = view.findViewById(R.id.layoutAprobat);
        Button btnInchide = view.findViewById(R.id.btnInchidePlata);

        TextView tvDetaliiCard = view.findViewById(R.id.tvDetaliiCard);
        TextView tvDetaliiCategorie = view.findViewById(R.id.tvDetaliiCategorie);
        TextView tvDetaliiData = view.findViewById(R.id.tvDetaliiData);

        tvDetaliiCard.setText(numeBanca + " •••• " + ultimeleCifre);
        tvDetaliiCategorie.setText(categorie + " " + emoji);
        tvDetaliiData.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));

        // Salvam tranzactia
        Tranzactie tranzactie = new Tranzactie(numeBanca, ultimeleCifre, categorie, emoji);
        TranzactieRepository.getInstance().adaugaTranzactie(tranzactie);

        // Dupa 2 secunde aratam aprobarea
        new Handler().postDelayed(() -> {
            layoutLoading.setVisibility(View.GONE);
            layoutAprobat.setVisibility(View.VISIBLE);
            btnInchide.setVisibility(View.VISIBLE);
        }, 2000);

        btnInchide.setOnClickListener(v -> dismiss());
    }
}