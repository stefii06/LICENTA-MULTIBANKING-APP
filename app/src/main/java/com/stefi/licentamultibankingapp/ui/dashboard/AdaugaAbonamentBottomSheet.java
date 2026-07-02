package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Abonament;
import com.stefi.licentamultibankingapp.model.AbonamentRepository;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class AdaugaAbonamentBottomSheet extends BottomSheetDialogFragment {

    private String emojiSelectat = "🎬";
    private int ziuaSelectata = 1;
    private int cardIndex = 0;
    private OnAbonamentAdaugat listener;

    private int[] btnZileIds = {R.id.btnZiua1, R.id.btnZiua5, R.id.btnZiua10, R.id.btnZiua15, R.id.btnZiua25};
    private int[] zile = {1, 5, 10, 15, 25};

    public interface OnAbonamentAdaugat {
        void onAdaugat();
    }

    public void setListener(OnAbonamentAdaugat listener) {
        this.listener = listener;
    }

    // Constructor pentru setare din recurente
    private String numePrecompletat = "";
    private String emojiPrecompletat = "";

    public void setDatePrecompletate(String nume, String emoji) {
        this.numePrecompletat = nume;
        this.emojiPrecompletat = emoji;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_adauga_abonament, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etNume = view.findViewById(R.id.etNumeAbonament);
        TextInputEditText etSuma = view.findViewById(R.id.etSumaAbonament);
        TextView tvEmoji = view.findViewById(R.id.tvEmojiSelectat);
        TextView tvCard = view.findViewById(R.id.tvCardAbonament);
        SwitchMaterial switchVariabil = view.findViewById(R.id.switchVariabil);

        // Precompletare daca vine din recurente
        if (!numePrecompletat.isEmpty()) {
            etNume.setText(numePrecompletat);
        }
        if (!emojiPrecompletat.isEmpty()) {
            emojiSelectat = emojiPrecompletat;
            tvEmoji.setText(emojiSelectat);
        }

        // Setup card
        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturiCurente();
        if (!conturi.isEmpty()) {
            ContBancar cont = conturi.get(0);
            tvCard.setText(cont.getNumeBanca() + " •••• " +
                    cont.getIban().substring(cont.getIban().length() - 4));
        }

        view.findViewById(R.id.spinnerCardAbonament).setOnClickListener(v -> {
            if (!conturi.isEmpty()) {
                cardIndex = (cardIndex + 1) % conturi.size();
                ContBancar cont = conturi.get(cardIndex);
                tvCard.setText(cont.getNumeBanca() + " •••• " +
                        cont.getIban().substring(cont.getIban().length() - 4));
            }
        });

        // Setup emoji
        String[] emojii = {"🎬", "🎵", "💡", "🏠", "📱", "🎮"};
        view.findViewById(R.id.btnSelectEmoji).setOnClickListener(v -> {
            int index = 0;
            for (int i = 0; i < emojii.length; i++) {
                if (emojii[i].equals(emojiSelectat)) {
                    index = (i + 1) % emojii.length;
                    break;
                }
            }
            emojiSelectat = emojii[index];
            tvEmoji.setText(emojiSelectat);
        });

        // Setup zile
        setupZile(view);

        Button btnSalveaza = view.findViewById(R.id.btnSalveazaAbonament);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaAbonament);

        btnSalveaza.setOnClickListener(v -> {
            String nume = etNume.getText().toString().trim();
            String sumaStr = etSuma.getText().toString().trim();

            if (nume.isEmpty()) {
                etNume.setError("Introdu numele abonamentului!");
                return;
            }
            if (sumaStr.isEmpty() && !switchVariabil.isChecked()) {
                etSuma.setError("Introdu suma!");
                return;
            }

            double suma = sumaStr.isEmpty() ? 0 : Double.parseDouble(sumaStr);
            String card = tvCard.getText().toString();

            Abonament abonament = new Abonament(
                    nume, emojiSelectat, suma, ziuaSelectata,
                    card, "Altele", switchVariabil.isChecked(),
                    !numePrecompletat.isEmpty()
            );

            AbonamentRepository.getInstance().adaugaAbonament(abonament);
            Toast.makeText(getContext(), "Abonament " + nume + " salvat!", Toast.LENGTH_SHORT).show();

            if (listener != null) listener.onAdaugat();
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }

    private void setupZile(View view) {
        for (int i = 0; i < btnZileIds.length; i++) {
            final int index = i;
            view.findViewById(btnZileIds[i]).setOnClickListener(v -> {
                ziuaSelectata = zile[index];
                updateSelectieZile(view, btnZileIds[index]);
            });
        }
    }

    private void updateSelectieZile(View view, int selectedId) {
        for (int id : btnZileIds) {
            view.findViewById(id).setBackgroundColor(
                    id == selectedId ? 0xFF4A90D9 : 0xFF12203A);
        }
    }
}