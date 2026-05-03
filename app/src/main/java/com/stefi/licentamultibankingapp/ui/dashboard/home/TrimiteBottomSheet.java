package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;

public class TrimiteBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_trimite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.optionIban).setOnClickListener(v -> {
            dismiss();
            Toast.makeText(getContext(), "Trimite prin IBAN - coming soon!", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.optionContacte).setOnClickListener(v -> {
            dismiss();
            Toast.makeText(getContext(), "Trimite prin contacte - coming soon!", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.optionQR).setOnClickListener(v -> {
            dismiss();
            Toast.makeText(getContext(), "Scanează QR - coming soon!", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.optionAnuleaza).setOnClickListener(v -> dismiss());
    }
}