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

import java.util.List;

public class StergeCardBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_IBAN = "iban";

    public static StergeCardBottomSheet newInstance(String iban) {
        StergeCardBottomSheet sheet = new StergeCardBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_IBAN, iban);
        sheet.setArguments(args);
        return sheet;
    }

    public interface OnCardSters {
        void onCardSters();
    }

    private OnCardSters listener;

    public void setListener(OnCardSters listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_sterge_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String iban = getArguments() != null ? getArguments().getString(ARG_IBAN) : null;
        ContBancar cont = gasesteContDupaIban(iban);
        if (cont == null) { dismiss(); return; }

        String ultimeleCifre = cont.getIban().substring(cont.getIban().length() - 4);

        // Views
        LinearLayout layoutOptiuni   = view.findViewById(R.id.layoutOptiuniSterge);
        LinearLayout layoutConfirmare = view.findViewById(R.id.layoutConfirmareSterge);
        LinearLayout layoutSucces    = view.findViewById(R.id.layoutSuccesSterge);
        TextView tvDesc              = view.findViewById(R.id.tvStergeDesc);
        TextView tvConfirmDesc       = view.findViewById(R.id.tvConfirmDesc);
        LinearLayout btnFinMind      = view.findViewById(R.id.btnStergeFinMind);
        LinearLayout btnBanca        = view.findViewById(R.id.btnStergeBanca);
        TextView tvBancaNume         = view.findViewById(R.id.tvStergeBancaNume);
        Button btnConfirma           = view.findViewById(R.id.btnConfirmaSterge);
        Button btnAnuleaza           = view.findViewById(R.id.btnAnuleazaSterge);
        Button btnAnuleazaConfirm    = view.findViewById(R.id.btnAnuleazaConfirm);
        Button btnInchide            = view.findViewById(R.id.btnInchideSterge);

        tvDesc.setText(cont.getNumeBanca() + " •••• " + ultimeleCifre);
        tvConfirmDesc.setText(cont.getNumeBanca() + " •••• " + ultimeleCifre +
                " va dispărea din FinMind.\nContul bancar real nu este afectat.");
        tvBancaNume.setText("Deschide aplicația " + cont.getNumeBanca());

        // Buton Elimină din FinMind — arata confirmare
        btnFinMind.setOnClickListener(v -> {
            layoutOptiuni.setVisibility(View.GONE);
            layoutConfirmare.setVisibility(View.VISIBLE);
        });

        // Confirmare finala
        btnConfirma.setOnClickListener(v -> {
            // Stergem din Firestore
            FirestoreManager.getInstance().conturi()
                    .document(cont.getId())
                    .delete();

            // Stergem din repository local
            ContBancarRepository.getInstance().getConturi().remove(cont);

            layoutConfirmare.setVisibility(View.GONE);
            layoutSucces.setVisibility(View.VISIBLE);

            if (listener != null) listener.onCardSters();
        });

        // Buton la bancă
        btnBanca.setOnClickListener(v -> {
            redirectLaBanca(cont.getNumeBanca());
            dismiss();
        });

        btnAnuleaza.setOnClickListener(v -> dismiss());
        btnAnuleazaConfirm.setOnClickListener(v -> {
            layoutConfirmare.setVisibility(View.GONE);
            layoutOptiuni.setVisibility(View.VISIBLE);
        });
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
        String packageBanca;
        switch (numeBanca) {
            case "BCR":                packageBanca = "ro.bcr.george"; break;
            case "ING":                packageBanca = "ro.ing.mobile"; break;
            case "BRD":                packageBanca = "ro.brd.gsmobile"; break;
            case "Raiffeisen":         packageBanca = "ro.raiffeisen.mobile"; break;
            case "Banca Transilvania": packageBanca = "ro.btrl.mobile"; break;
            case "CEC Bank":           packageBanca = "ro.cecbank.mobile"; break;
            default:                   packageBanca = null;
        }

        if (packageBanca != null) {
            PackageManager pm = requireContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageBanca);
            if (intent != null) {
                startActivity(intent);
                return;
            }
        }

        Intent playStore = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/search?q=" +
                        Uri.encode(numeBanca + " Romania") + "&c=apps"));
        startActivity(playStore);
    }
}