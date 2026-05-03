package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Contact;
import com.stefi.licentamultibankingapp.model.ContactRepository;

public class AdaugaContactBottomSheet extends BottomSheetDialogFragment {

    private OnContactAdaugat listener;

    public interface OnContactAdaugat {
        void onContactAdaugat();
    }

    public void setListener(OnContactAdaugat listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_adauga_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etNume = view.findViewById(R.id.etNumeContact);
        TextInputEditText etPrenume = view.findViewById(R.id.etPrenumeContact);
        TextInputEditText etIban = view.findViewById(R.id.etIbanContact);
        TextInputEditText etNota = view.findViewById(R.id.etNotaContact);

        Button btnSalveaza = view.findViewById(R.id.btnSalveazaContact);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaContact);

        btnSalveaza.setOnClickListener(v -> {
            String nume = etNume.getText().toString().trim();
            String prenume = etPrenume.getText().toString().trim();
            String iban = etIban.getText().toString().trim();
            String nota = etNota.getText().toString().trim();

            if (nume.isEmpty() || prenume.isEmpty() || iban.isEmpty()) {
                Toast.makeText(getContext(), "Completeaza numele, prenumele si IBAN-ul!", Toast.LENGTH_SHORT).show();
                return;
            }

            Contact contact = new Contact(nume, prenume, iban, nota);
            ContactRepository.getInstance(getContext()).adaugaContact(contact);

            Toast.makeText(getContext(), "Contact adaugat!", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onContactAdaugat();
            }

            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }
}