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
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class StergeCardBottomSheet extends BottomSheetDialogFragment {

    private OnCardSters listener;

    public interface OnCardSters {
        void onCardSters();
    }

    public void setListener(OnCardSters listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_sterge_card, container, false);
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
        String ultimeleCifre = cont.getIban().substring(cont.getIban().length() - 4);

        android.widget.TextView tvDesc = view.findViewById(R.id.tvStergeDesc);
        tvDesc.setText(cont.getNumeBanca() + " •••• " + ultimeleCifre +
                " va fi eliminat din aplicatie. Aceasta actiune nu poate fi anulata.");

        Button btnConfirma = view.findViewById(R.id.btnConfirmaSterge);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaSterge);

        btnConfirma.setOnClickListener(v -> {
            ContBancarRepository.getInstance().getConturi().remove(cont);
            Toast.makeText(getContext(), "Card sters!", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onCardSters();
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }
}