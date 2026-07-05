package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Tranzactie;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TranzactieContactAdapter extends RecyclerView.Adapter<TranzactieContactAdapter.ViewHolder> {

    private List<Tranzactie> tranzactii;
    private final SimpleDateFormat formatData =
            new SimpleDateFormat("d MMMM yyyy", new Locale("ro", "RO"));

    public TranzactieContactAdapter(List<Tranzactie> tranzactii) {
        this.tranzactii = tranzactii;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tranzactie_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tranzactie t = tranzactii.get(position);
        boolean trimis = t.getSuma() < 0;

        holder.tvDescriere.setText(t.getDescriere());
        holder.tvData.setText(formatData.format(t.getData()));
        holder.tvSuma.setText(String.format("%.0f RON", t.getSuma()));

        if (trimis) {
            holder.tvIcon.setText("↑");
            holder.tvIcon.setTextColor(android.graphics.Color.parseColor("#E57373"));
            holder.tvSuma.setTextColor(android.graphics.Color.parseColor("#E57373"));
        } else {
            holder.tvIcon.setText("↓");
            holder.tvIcon.setTextColor(android.graphics.Color.parseColor("#81C784"));
            holder.tvSuma.setTextColor(android.graphics.Color.parseColor("#81C784"));
        }
    }

    @Override
    public int getItemCount() {
        return tranzactii.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvDescriere, tvData, tvSuma;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIconTranzactieContact);
            tvDescriere = itemView.findViewById(R.id.tvDescriereTranzactieContact);
            tvData = itemView.findViewById(R.id.tvDataTranzactieContact);
            tvSuma = itemView.findViewById(R.id.tvSumaTranzactieContact);
        }
    }
}