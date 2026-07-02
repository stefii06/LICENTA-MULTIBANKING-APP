package com.stefi.licentamultibankingapp.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;

import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajViewHolder> {

    public static class Mesaj {
        public String text;
        public boolean esteAI;

        public Mesaj(String text, boolean esteAI) {
            this.text   = text;
            this.esteAI = esteAI;
        }
    }

    private final List<Mesaj> mesaje;

    public MesajAdapter(List<Mesaj> mesaje) {
        this.mesaje = mesaje;
    }

    @NonNull
    @Override
    public MesajViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mesaj, parent, false);
        return new MesajViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajViewHolder holder, int position) {
        Mesaj mesaj = mesaje.get(position);

        holder.tvMesaj.setText(mesaj.text);

        if (mesaj.esteAI) {
            // Mesaj AI — aliniat stanga
            holder.tvSender.setVisibility(View.VISIBLE);
            holder.tvSender.setText("🤖 Asistent");
            holder.tvSender.setTextColor(Color.parseColor("#4A90D9"));
            holder.tvMesaj.setBackgroundColor(Color.parseColor("#1A2F50"));
            holder.tvMesaj.setTextColor(Color.WHITE);

            holder.layoutMesaj.setGravity(Gravity.START);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 80;
            holder.tvMesaj.setLayoutParams(params);
            holder.tvSender.setLayoutParams(params);
        } else {
            // Mesaj utilizator — aliniat dreapta
            holder.tvSender.setVisibility(View.GONE);
            holder.tvMesaj.setBackgroundColor(Color.parseColor("#1A3C6E"));
            holder.tvMesaj.setTextColor(Color.WHITE);

            holder.layoutMesaj.setGravity(Gravity.END);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 80;
            holder.tvMesaj.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return mesaje.size();
    }

    public void adaugaMesaj(Mesaj mesaj) {
        mesaje.add(mesaj);
        notifyItemInserted(mesaje.size() - 1);
    }

    static class MesajViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMesaj;
        TextView tvSender, tvMesaj;

        MesajViewHolder(View view) {
            super(view);
            layoutMesaj = view.findViewById(R.id.layoutMesaj);
            tvSender    = view.findViewById(R.id.tvSender);
            tvMesaj     = view.findViewById(R.id.tvMesaj);
        }
    }
}