package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Contact;

import java.util.List;

public class ContacteAdapter extends RecyclerView.Adapter<ContacteAdapter.ContactViewHolder> {

    private List<Contact> contacte;
    private OnAdaugaClick onAdaugaClick;
    private OnContactClick onContactClick;

    public interface OnAdaugaClick {
        void onAdaugaClick();
    }

    public interface OnContactClick {
        void onContactClick(Contact contact);
    }

    // Constructor pentru HomeFragment — cu buton "+"
    public ContacteAdapter(List<Contact> contacte, OnAdaugaClick onAdaugaClick) {
        this.contacte = contacte;
        this.onAdaugaClick = onAdaugaClick;
        this.onContactClick = null;
    }

    // Constructor pentru TrimiteBottomSheet — fara sau cu buton "+"
    public ContacteAdapter(List<Contact> contacte, OnAdaugaClick onAdaugaClick,
                           OnContactClick onContactClick) {
        this.contacte = contacte;
        this.onAdaugaClick = onAdaugaClick;
        this.onContactClick = onContactClick;
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
        if (onAdaugaClick != null) {
            // Cu buton "+" la pozitia 0
            if (position == 0) {
                holder.tvInitiale.setText("+");
                holder.tvNume.setText("Adauga");
                holder.itemView.setOnClickListener(v -> onAdaugaClick.onAdaugaClick());
                return;
            }
            Contact contact = contacte.get(position - 1);
            holder.tvInitiale.setText(contact.getInitiale());
            holder.tvNume.setText(contact.getNume());
            holder.itemView.setOnClickListener(v -> {
                if (onContactClick != null) onContactClick.onContactClick(contact);
            });
        } else {
            // Fara buton "+" — direct contactele de la pozitia 0
            Contact contact = contacte.get(position);
            holder.tvInitiale.setText(contact.getInitiale());
            holder.tvNume.setText(contact.getNume());
            holder.itemView.setOnClickListener(v -> {
                if (onContactClick != null) onContactClick.onContactClick(contact);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (onAdaugaClick != null) {
            return contacte.size() + 1;
        }
        return contacte.size();
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