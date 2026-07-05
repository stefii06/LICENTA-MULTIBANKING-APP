package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Contact;
import com.stefi.licentamultibankingapp.model.ContactRepository;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailsBottomSheet extends BottomSheetDialogFragment {

    public interface OnContactSters {
        void onContactSters();
    }

    private Contact contact;
    private OnContactSters listener;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setListener(OnContactSters listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_contact_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (contact == null) {
            dismiss();
            return;
        }

        // Header
        TextView tvAvatar = view.findViewById(R.id.tvAvatarContactDetalii);
        TextView tvNume = view.findViewById(R.id.tvNumeContactDetalii);
        tvAvatar.setText(contact.getInitiale());
        tvNume.setText(contact.getNumeComplet());

        // IBAN + telefon
        TextView tvIban = view.findViewById(R.id.tvIbanContactDetalii);
        TextView tvTelefon = view.findViewById(R.id.tvTelefonContactDetalii);
        View rowTelefon = view.findViewById(R.id.rowTelefonContactDetalii);

        tvIban.setText(contact.getIban().isEmpty() ? "—" : contact.getIban());
        if (contact.getTelefon().isEmpty()) {
            rowTelefon.setVisibility(View.GONE);
        } else {
            tvTelefon.setText(contact.getTelefon());
        }

        // Istoric tranzactii — filtram doar cele legate de acest contact
        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();
        List<Tranzactie> tranzactiiContact = new ArrayList<>();
        for (Tranzactie t : toate) {
            if (contact.getId().equals(t.getContactId())) {
                tranzactiiContact.add(t);
            }
        }

        RecyclerView rv = view.findViewById(R.id.rvIstoricContact);
        TextView tvGol = view.findViewById(R.id.tvGolIstoricContact);

        if (tranzactiiContact.isEmpty()) {
            rv.setVisibility(View.GONE);
            tvGol.setVisibility(View.VISIBLE);
        } else {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new TranzactieContactAdapter(tranzactiiContact));
        }

        // Shortcut Trimite bani — deschide TrimiteBottomSheet cu acest contact preselectat
        view.findViewById(R.id.btnTrimiteDinContact).setOnClickListener(v -> {
            TrimiteBottomSheet sheet = new TrimiteBottomSheet();
            sheet.setContactPreselectat(contact);
            sheet.setCallback(this::dismiss);
            sheet.show(getParentFragmentManager(), "TrimiteBottomSheet");
        });

        // Sterge contact — confirmare inainte de stergere
        view.findViewById(R.id.tvStergeContactDetalii).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Sterge contact")
                    .setMessage("Sigur vrei sa stergi contactul " + contact.getNumeComplet() + "?")
                    .setPositiveButton("Sterge", (dialog, which) -> {
                        ContactRepository.getInstance().stergeContact(contact.getId());
                        if (listener != null) listener.onContactSters();
                        dismiss();
                    })
                    .setNegativeButton("Anuleaza", null)
                    .show();
        });
    }
}