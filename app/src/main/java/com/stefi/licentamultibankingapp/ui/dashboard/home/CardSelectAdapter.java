package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;

import java.util.List;

public class CardSelectAdapter extends RecyclerView.Adapter<CardSelectAdapter.CardViewHolder> {

    private List<ContBancar> conturi;
    private int selectedPosition = 0;
    private OnCardSelected listener;

    public interface OnCardSelected {
        void onCardSelected(ContBancar cont);
    }

    public CardSelectAdapter(List<ContBancar> conturi, OnCardSelected listener) {
        this.conturi = conturi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_select_primeste, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ContBancar cont = conturi.get(position);

        holder.tvNumeBanca.setText(cont.getNumeBanca());
        holder.tvNumar.setText("•••• " + cont.getIban().substring(cont.getIban().length() - 4));
        holder.tvSold.setText(String.format("%.2f %s", cont.getSold(), cont.getValuta()));

        // Culoarea bancii
        try {
            holder.itemView.setBackgroundColor(Color.parseColor(cont.getCuloareBanca()));
        } catch (Exception e) {
            holder.itemView.setBackgroundColor(Color.parseColor("#1A3C6E"));
        }

        // Card selectat — border alb
        if (position == selectedPosition) {
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setAlpha(0.5f);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCardSelected(cont);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conturi.size();
    }

    public ContBancar getSelectedCont() {
        if (conturi.isEmpty()) return null;
        return conturi.get(selectedPosition);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeBanca, tvNumar, tvSold;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeBanca = itemView.findViewById(R.id.tvNumeBancaCard);
            tvNumar = itemView.findViewById(R.id.tvNumarCard);
            tvSold = itemView.findViewById(R.id.tvSoldCard);
        }
    }
}