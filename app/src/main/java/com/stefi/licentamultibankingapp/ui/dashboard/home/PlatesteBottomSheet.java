package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.util.List;

public class PlatesteBottomSheet extends BottomSheetDialogFragment {

    private ContBancar contSelectat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_plateste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();

        if (!conturi.isEmpty()) {
            contSelectat = conturi.get(0);
            updateCardPreview(view, contSelectat);
        }

        RecyclerView rvCarduri = view.findViewById(R.id.rvCarduriPlateste);
        rvCarduri.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CardSelectAdapter adapter = new CardSelectAdapter(conturi, cont -> {
            contSelectat = cont;
            updateCardPreview(view, cont);
        });
        rvCarduri.setAdapter(adapter);

        Button btnContinua = view.findViewById(R.id.btnContinuaPlateste);
        Button btnAnuleaza = view.findViewById(R.id.btnAnuleazaPlateste);

        btnContinua.setOnClickListener(v -> {
            if (contSelectat == null) return;
            CategorieBottomSheet categorieSheet = new CategorieBottomSheet();
            Bundle args = new Bundle();
            args.putString("numeBanca", contSelectat.getNumeBanca());
            args.putString("ultimeleCifre", contSelectat.getIban().substring(contSelectat.getIban().length() - 4));
            args.putString("titular", contSelectat.getTitular());
            args.putString("tipCard", contSelectat.getTipCard());
            args.putString("culoare", contSelectat.getCuloareBanca());
            categorieSheet.setArguments(args);
            categorieSheet.show(getParentFragmentManager(), "CategorieSheet");
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
    }

    private void updateCardPreview(View view, ContBancar cont) {
        View layoutPreview = view.findViewById(R.id.layoutCardPreview);
        TextView tvBanca = view.findViewById(R.id.tvCardPreviewBanca);
        TextView tvNumar = view.findViewById(R.id.tvCardPreviewNumar);
        TextView tvTitular = view.findViewById(R.id.tvCardPreviewTitular);
        TextView tvTip = view.findViewById(R.id.tvCardPreviewTip);

        try {
            layoutPreview.setBackgroundColor(Color.parseColor(cont.getCuloareBanca()));
        } catch (Exception e) {
            layoutPreview.setBackgroundColor(Color.parseColor("#1A3C6E"));
        }

        tvBanca.setText(cont.getNumeBanca() + " — " + cont.getTipCard());
        tvNumar.setText("•••• •••• •••• " + cont.getIban().substring(cont.getIban().length() - 4));
        tvTitular.setText(cont.getTitular());
        tvTip.setText(cont.getTipCard());
    }
}