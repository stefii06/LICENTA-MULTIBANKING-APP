package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class DetaliiCardBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_detalii_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Luam primul cont din repository
        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();
        if (conturi.isEmpty()) {
            dismiss();
            return;
        }

        ContBancar cont = conturi.get(0);
        String ultimeleCifre = cont.getIban().substring(cont.getIban().length() - 4);

        // Card preview
        View layoutCard = view.findViewById(R.id.layoutCardDetalii);
        TextView tvBanca = view.findViewById(R.id.tvDetaliiCardBanca);
        TextView tvNumar = view.findViewById(R.id.tvDetaliiCardNumar);
        TextView tvTitular = view.findViewById(R.id.tvDetaliiCardTitular);
        TextView tvTip = view.findViewById(R.id.tvDetaliiCardTip);

        try {
            layoutCard.setBackgroundColor(Color.parseColor(cont.getCuloareBanca()));
        } catch (Exception e) {
            layoutCard.setBackgroundColor(Color.parseColor("#1A3C6E"));
        }

        tvBanca.setText(cont.getNumeBanca() + " — " + cont.getTipCard());
        tvNumar.setText("2345 5678 4332 " + ultimeleCifre);
        tvTitular.setText(cont.getTitular());
        tvTip.setText(cont.getTipCard());

        // Campurile detalii
        TextView tvCardName = view.findViewById(R.id.tvCardName);
        TextView tvCardBank = view.findViewById(R.id.tvCardBank);
        TextView tvCardAccountNumber = view.findViewById(R.id.tvCardAccountNumber);
        TextView tvCardExpiry = view.findViewById(R.id.tvCardExpiry);
        TextView tvCardCvv = view.findViewById(R.id.tvCardCvv);

        tvCardName.setText(cont.getTitular());
        tvCardBank.setText(cont.getNumeBanca());
        tvCardAccountNumber.setText("•••• " + ultimeleCifre);
        tvCardExpiry.setText("08/27");
        tvCardCvv.setText("419");

        // Butoane copiere
        setupCopyButton(view, R.id.btnCopyCardName, cont.getTitular(), "Nume copiat!");
        setupCopyButton(view, R.id.btnCopyBank, cont.getNumeBanca(), "Banca copiata!");
        setupCopyButton(view, R.id.btnCopyAccountNumber, ultimeleCifre, "Numar copiat!");
        setupCopyButton(view, R.id.btnCopyExpiry, "08/27", "Data expirarii copiata!");
        setupCopyButton(view, R.id.btnCopyCvv, "419", "CVV copiat!");

        view.findViewById(R.id.btnInchideDetalii).setOnClickListener(v -> dismiss());
    }

    private void setupCopyButton(View view, int btnId, String text, String mesaj) {
        view.findViewById(btnId).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("copy", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), mesaj, Toast.LENGTH_SHORT).show();
        });
    }
}