package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
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
import com.google.android.material.textfield.TextInputEditText;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

public class FormularContFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formular_cont, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String numeBanca = getArguments().getString("numeBanca", "");
        String culoare = getArguments().getString("culoare", "#1A3C6E");

        // Setam culoarea bancii pe header
        TextView tvNumeBanca = view.findViewById(R.id.tvNumeBancaFormular);
        tvNumeBanca.setText("Conectare " + numeBanca);
        tvNumeBanca.setBackgroundColor(Color.parseColor(culoare));

        TextInputEditText etIban = view.findViewById(R.id.etIbanFormular);
        TextInputEditText etTitular = view.findViewById(R.id.etTitularFormular);
        TextInputEditText etSold = view.findViewById(R.id.etSoldFormular);

        Button btnConecteaza = view.findViewById(R.id.btnConecteaza);
        btnConecteaza.setOnClickListener(v -> {
            String iban = etIban.getText().toString().trim();
            String titular = etTitular.getText().toString().trim();
            String soldStr = etSold.getText().toString().trim();

            if (iban.isEmpty() || titular.isEmpty() || soldStr.isEmpty()) {
                Toast.makeText(getContext(), "Completeaza toate campurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            double sold = 0;
            try {
                sold = Double.parseDouble(soldStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Sold invalid!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContBancar contNou = new ContBancar(numeBanca, iban, sold, "RON", culoare, titular, "Visa");
            ContBancarRepository.getInstance().adaugaCont(contNou);

            Toast.makeText(getContext(), "Cont " + numeBanca + " adaugat cu succes!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        view.findViewById(R.id.btnAnuleazaFormular).setOnClickListener(v -> dismiss());
    }
}