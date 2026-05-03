package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class IngheataCardBottomSheet extends BottomSheetDialogFragment {

    private OnCardInghetat listener;

    public interface OnCardInghetat {
        void onCardInghetat(boolean inghetat);
    }

    public void setListener(OnCardInghetat listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_ingheata_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();
        if (conturi.isEmpty()) {
            dismiss();
            return;
        }

        ContBancar cont = conturi.get(0);
        boolean esteInghetat = cont.isInghetat();

        View layoutConfirmare = view.findViewById(R.id.layoutConfirmareIngheata);
        View layoutConfirmat = view.findViewById(R.id.layoutInghetatConfirmat);
        Button btnConfirma = view.findViewById(R.id.btnConfirmaIngheata);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaIngheata);
        Button btnInchide = view.findViewById(R.id.btnInchideIngheata);

        // Daca e deja inghetat, schimbam textul
        if (esteInghetat) {
            android.widget.TextView tvTitle = view.findViewById(R.id.tvIngheataTitle);
            android.widget.TextView tvDesc = view.findViewById(R.id.tvIngheataDesc);
            tvTitle.setText("Dezgheata cardul?");
            tvDesc.setText("Cardul va putea fi folosit din nou pentru plati");
            btnConfirma.setText("Da, dezgheata");
        }

        btnConfirma.setOnClickListener(v -> {
            cont.setInghetat(!esteInghetat);
            layoutConfirmare.setVisibility(View.GONE);
            layoutConfirmat.setVisibility(View.VISIBLE);

            android.widget.TextView tvSuccess = view.findViewById(R.id.tvInghetatSuccess);
            if (!esteInghetat) {
                tvSuccess.setText("Card inghetat!");
                tvSuccess.setTextColor(0xFF4CAF50);
            } else {
                tvSuccess.setText("Card dezghetat!");
                tvSuccess.setTextColor(0xFF4A90D9);
            }

            if (listener != null) listener.onCardInghetat(!esteInghetat);
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
        btnInchide.setOnClickListener(v -> dismiss());
    }
}