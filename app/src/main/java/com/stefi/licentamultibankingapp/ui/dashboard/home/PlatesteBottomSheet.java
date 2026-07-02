package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;

import java.util.ArrayList;
import java.util.List;

public class PlatesteBottomSheet extends BottomSheetDialogFragment {

    public interface OnPlataFacuta {
        void onPlataFacuta();
    }

    private OnPlataFacuta callback;
    private ViewFlipper viewFlipper;
    private List<ContBancar> conturiCurente = new ArrayList<>();
    private CardSelectAdapter cardAdapter;

    public void setCallback(OnPlataFacuta callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_plateste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFlipper = view.findViewById(R.id.viewFlipperPlateste);

        // Filtram doar conturile curente SI neinghetate
        List<ContBancar> toate = ContBancarRepository.getInstance().getConturi();
        for (ContBancar c : toate) {
            if (c.getTipCont() == ContBancar.TipCont.CURENT && !c.isInghetat()) {
                conturiCurente.add(c);
            }
        }


        setupPas1(view);
        setupPas2(view);
        setupPas3(view);
    }

    // PAS 1 — slider orizontal cu carduri, identic cu TrimiteBottomSheet
    private void setupPas1(View view) {
        if (conturiCurente.isEmpty()) {
            Toast.makeText(getContext(), "Nu ai conturi curente!", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        RecyclerView rv = view.findViewById(R.id.rvCarduriPlateste);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        cardAdapter = new CardSelectAdapter(conturiCurente, cont -> {
            // contul selectat se tine in adapter
        });
        rv.setAdapter(cardAdapter);

        view.findViewById(R.id.btnPlatesteCuCard).setOnClickListener(v -> {
            ContBancar cont = cardAdapter.getSelectedCont();
            if (cont == null) return;

            // Actualizeaza datele pe ecranul NFC
            TextView tvNume = view.findViewById(R.id.tvNfcNumeBanca);
            TextView tvNr = view.findViewById(R.id.tvNfcNrCard);
            TextView tvTitular = view.findViewById(R.id.tvNfcTitular);
            View layoutCard = view.findViewById(R.id.layoutCardNfc);

            tvNume.setText(cont.getNumeBanca() + " " + cont.getTipCard());
            tvNr.setText("•••• •••• •••• " +
                    cont.getIban().substring(cont.getIban().length() - 4));
            tvTitular.setText(cont.getTitular().toUpperCase());

            try {
                layoutCard.setBackgroundColor(Color.parseColor(cont.getCuloareBanca()));
            } catch (Exception e) {
                layoutCard.setBackgroundColor(Color.parseColor("#1A3C6E"));
            }

            viewFlipper.showNext();
        });

        view.findViewById(R.id.tvAnuleazaPlateste).setOnClickListener(v -> dismiss());
    }

    // PAS 2 — NFC simulat
    private void setupPas2(View view) {
        view.findViewById(R.id.btnSimuleazaPlata).setOnClickListener(v -> {
            view.findViewById(R.id.btnSimuleazaPlata).setEnabled(false);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ContBancar cont = cardAdapter.getSelectedCont();
                if (cont == null) return;

                // Salvam tranzactia in Firestore
                Tranzactie t = new Tranzactie(
                        cont.getNumeBanca(),
                        cont.getIban().substring(cont.getIban().length() - 4),
                        "Plata POS",
                        "💳",
                        new java.util.Date(),
                        0f
                );
                TranzactieRepository.getInstance().adaugaTranzactie(t);

                // Actualizam ecranul success
                TextView tvSuccessCard = view.findViewById(R.id.tvSuccessCard);
                tvSuccessCard.setText(cont.getNumeBanca() + " •••• " +
                        cont.getIban().substring(cont.getIban().length() - 4));

                viewFlipper.showNext();
            }, 2000);
        });

        view.findViewById(R.id.tvInapoiNfc).setOnClickListener(v -> {
            view.findViewById(R.id.btnSimuleazaPlata).setEnabled(true);
            viewFlipper.showPrevious();
        });
    }

    // PAS 3 — Success
    private void setupPas3(View view) {
        view.findViewById(R.id.btnInchidePlateste).setOnClickListener(v -> {
            if (callback != null) callback.onPlataFacuta();
            dismiss();
        });
    }
}