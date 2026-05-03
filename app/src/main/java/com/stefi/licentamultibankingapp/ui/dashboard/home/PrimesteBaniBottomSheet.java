package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class PrimesteBaniBottomSheet extends BottomSheetDialogFragment {

    private ContBancar contSelectat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_primeste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();

        // Selectam primul cont implicit
        if (!conturi.isEmpty()) {
            contSelectat = conturi.get(0);
        }

        // Setup RecyclerView slider
        RecyclerView rvCarduri = view.findViewById(R.id.rvCarduriPrimeste);
        rvCarduri.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CardSelectAdapter adapter = new CardSelectAdapter(conturi, cont -> {
            contSelectat = cont;
        });
        rvCarduri.setAdapter(adapter);

        // Optiunea IBAN
        view.findViewById(R.id.optionIbanPrimeste).setOnClickListener(v -> {
            if (contSelectat == null) return;
            dismiss();
            IbanBottomSheet ibanSheet = new IbanBottomSheet();
            Bundle args = new Bundle();
            args.putString("iban", contSelectat.getIban());
            args.putString("titular", contSelectat.getTitular());
            args.putString("numeBanca", contSelectat.getNumeBanca());
            args.putString("sold", String.format("%.2f %s", contSelectat.getSold(), contSelectat.getValuta()));
            args.putString("culoare", contSelectat.getCuloareBanca());
            ibanSheet.setArguments(args);
            ibanSheet.show(getParentFragmentManager(), "IbanSheet");
        });

        // Optiunea QR
        view.findViewById(R.id.optionQRPrimeste).setOnClickListener(v -> {
            android.widget.Toast.makeText(getContext(), "Cod QR - coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });

        // Anuleaza
        view.findViewById(R.id.optionAnuleazaPrimeste).setOnClickListener(v -> dismiss());
    }
}