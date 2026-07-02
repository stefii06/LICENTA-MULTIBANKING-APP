package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.utils.FirestoreManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngheataCardBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_IBAN = "iban";

    public static IngheataCardBottomSheet newInstance(String iban) {
        IngheataCardBottomSheet sheet = new IngheataCardBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_IBAN, iban);
        sheet.setArguments(args);
        return sheet;
    }

    public interface OnCardInghetat {
        void onCardInghetat(boolean inghetat);
    }

    private OnCardInghetat listener;

    public void setListener(OnCardInghetat listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_ingheata_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gasim contul dupa IBAN
        String iban = getArguments() != null ? getArguments().getString(ARG_IBAN) : null;
        ContBancar cont = gasesteContDupaIban(iban);
        if (cont == null) { dismiss(); return; }

        boolean esteInghetat = cont.isInghetat();
        String ultimeleCifre = cont.getIban().substring(cont.getIban().length() - 4);

        // Views
        LinearLayout layoutOptiuni   = view.findViewById(R.id.layoutOptiuniIngheata);
        LinearLayout layoutConfirmat = view.findViewById(R.id.layoutInghetatConfirmat);
        TextView tvTitle             = view.findViewById(R.id.tvIngheataTitle);
        TextView tvDesc              = view.findViewById(R.id.tvIngheataDesc);
        LinearLayout btnFinMind      = view.findViewById(R.id.btnIngheataFinMind);
        LinearLayout btnBanca        = view.findViewById(R.id.btnIngheataBanca);
        TextView tvBancaNume         = view.findViewById(R.id.tvIngheataBancaNume);
        Button btnAnuleaza           = view.findViewById(R.id.btnAnuleazaIngheata);
        Button btnInchide            = view.findViewById(R.id.btnInchideIngheata);
        TextView tvSuccess           = view.findViewById(R.id.tvInghetatSuccess);

        // Titlu dinamic — ingheata sau dezgheata
        tvTitle.setText(esteInghetat ? "Dezgheață cardul" : "Îngheață cardul");
        tvDesc.setText(cont.getNumeBanca() + " •••• " + ultimeleCifre);
        tvBancaNume.setText("Deschide aplicația " + cont.getNumeBanca());

        // Buton Îngheață în FinMind
        btnFinMind.setOnClickListener(v -> {
            boolean nouaStare = !esteInghetat;
            cont.setInghetat(nouaStare);

            // Salvam in Firestore
            Map<String, Object> update = new HashMap<>();
            update.put("inghetat", nouaStare);
            FirestoreManager.getInstance().conturi()
                    .document(cont.getId())
                    .update(update);

            // Afisam confirmare
            layoutOptiuni.setVisibility(View.GONE);
            layoutConfirmat.setVisibility(View.VISIBLE);

            if (nouaStare) {
                tvSuccess.setText("Card îngheațat în FinMind");
                tvSuccess.setTextColor(0xFF4A90D9);
            } else {
                tvSuccess.setText("Card dezghețat în FinMind");
                tvSuccess.setTextColor(0xFF4CAF50);
            }

            if (listener != null) listener.onCardInghetat(nouaStare);
        });

        // Buton Îngheață la bancă — redirect
        btnBanca.setOnClickListener(v -> {
            redirectLaBanca(cont.getNumeBanca());
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
        btnInchide.setOnClickListener(v -> dismiss());
    }

    private ContBancar gasesteContDupaIban(String iban) {
        if (iban == null) return null;
        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();
        for (ContBancar c : conturi) {
            if (c.getIban().equals(iban)) return c;
        }
        return null;
    }

    private void redirectLaBanca(String numeBanca) {
        // Package-urile aplicatiilor bancare romanesti
        String packageBanca;
        switch (numeBanca) {
            case "BCR":               packageBanca = "ro.bcr.george"; break;
            case "ING":               packageBanca = "ro.ing.mobile"; break;
            case "BRD":               packageBanca = "ro.brd.gsmobile"; break;
            case "Raiffeisen":        packageBanca = "ro.raiffeisen.mobile"; break;
            case "Banca Transilvania":packageBanca = "ro.btrl.mobile"; break;
            case "CEC Bank":          packageBanca = "ro.cecbank.mobile"; break;
            default:                  packageBanca = null;
        }

        if (packageBanca != null) {
            PackageManager pm = requireContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageBanca);
            if (intent != null) {
                startActivity(intent);
                return;
            }
        }

        // Daca aplicatia nu e instalata — deschide Play Store
        Intent playStore = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/search?q=" +
                        Uri.encode(numeBanca + " Romania") + "&c=apps"));
        startActivity(playStore);
    }
}