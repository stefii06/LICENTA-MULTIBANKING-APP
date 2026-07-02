package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_adauga_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etNume = view.findViewById(R.id.etNumeContact);
        TextInputEditText etPrenume = view.findViewById(R.id.etPrenumeContact);
        TextInputEditText etIban = view.findViewById(R.id.etIbanContact);
        TextInputEditText etNota = view.findViewById(R.id.etNotaContact);
        EditText etTelefon = view.findViewById(R.id.etTelefonContact);
        Spinner spinnerPrefix = view.findViewById(R.id.spinnerPrefix);

        // Setup prefix tara
        String[] prefixuri = {"+40", "+44", "+49", "+33", "+39", "+1", "+7"};
        ArrayAdapter<String> prefixAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                prefixuri);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrefix.setAdapter(prefixAdapter);

        Button btnSalveaza = view.findViewById(R.id.btnSalveazaContact);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaContact);

        btnSalveaza.setOnClickListener(v -> {
            String nume = etNume.getText().toString().trim();
            String prenume = etPrenume.getText().toString().trim();
            String iban = etIban.getText().toString().trim();
            String nota = etNota.getText().toString().trim();
            String telefonRaw = etTelefon.getText().toString().trim();
            String prefix = spinnerPrefix.getSelectedItem().toString();
            String telefon = telefonRaw.isEmpty() ? "" : prefix + " " + telefonRaw;

            if (nume.isEmpty() || prenume.isEmpty()) {
                Toast.makeText(getContext(), "Completeaza cel putin numele si prenumele!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Contact contact = new Contact(nume, prenume, iban, nota, telefon);
            ContactRepository.getInstance(getContext()).adaugaContact(contact);
            Toast.makeText(getContext(), "Contact adaugat!", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onContactAdaugat();
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }
}