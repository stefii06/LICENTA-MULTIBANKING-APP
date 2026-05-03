package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;

public class IbanBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_iban_primeste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String iban = getArguments().getString("iban", "");
        String titular = getArguments().getString("titular", "");
        String numeBanca = getArguments().getString("numeBanca", "");
        String sold = getArguments().getString("sold", "");
        String culoare = getArguments().getString("culoare", "#1A3C6E");

        // Setam datele
        TextView tvIban = view.findViewById(R.id.tvIbanComplet);
        TextView tvTitular = view.findViewById(R.id.tvTitularIban);
        TextView tvNumeBanca = view.findViewById(R.id.tvNumeBancaIban);
        TextView tvNumarCard = view.findViewById(R.id.tvNumarCardIban);
        View layoutIndicator = view.findViewById(R.id.layoutCardIndicator);

        tvIban.setText(iban);
        tvTitular.setText("Titular: " + titular);
        tvNumeBanca.setText(numeBanca);
        tvNumarCard.setText(sold);

        // Culoarea bancii pe indicator
        try {
            layoutIndicator.setBackgroundColor(Color.parseColor(culoare));
        } catch (Exception e) {
            layoutIndicator.setBackgroundColor(Color.parseColor("#1A3C6E"));
        }

        // Buton Copiaza
        view.findViewById(R.id.btnCopiazaIban).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("IBAN", iban);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "IBAN copiat!", Toast.LENGTH_SHORT).show();
        });

        // Buton Distribuie
        view.findViewById(R.id.btnDistribuieIban).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Trimite-mi bani la IBAN: " + iban + "\nTitular: " + titular);
            startActivity(Intent.createChooser(shareIntent, "Distribuie IBAN prin..."));
        });

        // Inchide
        view.findViewById(R.id.btnInchideIban).setOnClickListener(v -> dismiss());
    }
}