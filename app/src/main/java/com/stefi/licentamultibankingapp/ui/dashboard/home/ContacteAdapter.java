package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Contact;

import java.util.List;

public class ContacteAdapter extends RecyclerView.Adapter<ContacteAdapter.ContactViewHolder> {

    private List<Contact> contacte;
    private OnAdaugaClick onAdaugaClick;

    public interface OnAdaugaClick {
        void onAdaugaClick();
    }

    public ContacteAdapter(List<Contact> contacte, OnAdaugaClick onAdaugaClick) {
        this.contacte = contacte;
        this.onAdaugaClick = onAdaugaClick;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (position == 0) {
            // Primul item e butonul de adauga
            holder.tvInitiale.setText("+");
            holder.tvNume.setText("Adauga");
            holder.itemView.setOnClickListener(v -> {
                if (onAdaugaClick != null) onAdaugaClick.onAdaugaClick();
            });
        } else {
            Contact contact = contacte.get(position - 1);
            holder.tvInitiale.setText(contact.getInitiale());
            holder.tvNume.setText(contact.getNume());
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(v.getContext(), contact.getNumeComplet(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return contacte.size() + 1; // +1 pentru butonul de adauga
    }

    public void actualizeazaLista(List<Contact> contacteNoi) {
        this.contacte = contacteNoi;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitiale, tvNume;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitiale = itemView.findViewById(R.id.tvInitiale);
            tvNume = itemView.findViewById(R.id.tvNume);
        }
    }
}