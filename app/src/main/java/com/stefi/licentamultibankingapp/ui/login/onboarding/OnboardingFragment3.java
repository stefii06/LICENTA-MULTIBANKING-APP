package com.stefi.licentamultibankingapp.ui.login.onboarding;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;

public class OnboardingFragment3 extends Fragment {

    private Handler handler = new Handler(Looper.getMainLooper());
    private ProgressBar progressBar;
    private TextView tvMesaj;

    // Mesajele care se schimba secvential
    private String[] mesaje = {
            "Se stabilește conexiunea securizată...",
            "Se verifică autorizarea PSD2...",
            "Se extrag datele contului...",
            "Se finalizează conectarea..."
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_fragment3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnboardingActivity activity = (OnboardingActivity) getActivity();
        String banca = activity.getBancaSelectata();
        String culoare = activity.getCuloareBanca();

        // Logo banca
        TextView tvLogo = view.findViewById(R.id.tvLogoBancaLoading);
        tvLogo.setText(getAbreviereBanca(banca));
        tvLogo.setBackgroundColor(Color.parseColor(culoare));

        progressBar = view.findViewById(R.id.progressBarOnboarding);
        tvMesaj = view.findViewById(R.id.tvMesajLoading);

        // Pornim animatia de loading
        pornestLoading();
    }

    private void pornestLoading() {
        // Pasul 1 — 0%
        tvMesaj.setText(mesaje[0]);
        progressBar.setProgress(0);

        // Pasul 2 — dupa 1 secunda — 33%
        handler.postDelayed(() -> {
            if (getActivity() == null) return;
            tvMesaj.setText(mesaje[1]);
            progressBar.setProgress(33);
        }, 1000);

        // Pasul 3 — dupa 2 secunde — 66%
        handler.postDelayed(() -> {
            if (getActivity() == null) return;
            tvMesaj.setText(mesaje[2]);
            progressBar.setProgress(66);
        }, 2000);

        // Pasul 4 — dupa 3 secunde — 90%
        handler.postDelayed(() -> {
            if (getActivity() == null) return;
            tvMesaj.setText(mesaje[3]);
            progressBar.setProgress(90);
        }, 3000);

        // Final — dupa 4 secunde — 100% si mergem la Fragment 4
        handler.postDelayed(() -> {
            if (getActivity() == null) return;
            progressBar.setProgress(100);
            ((OnboardingActivity) getActivity()).mergeInainte();
        }, 4000);
    }

    private String getAbreviereBanca(String banca) {
        switch (banca) {
            case "BCR": return "BCR";
            case "ING": return "ING";
            case "BRD": return "BRD";
            case "Raiffeisen": return "RAI";
            case "Banca Transilvania": return "BT";
            case "CEC Bank": return "CEC";
            default: return banca.substring(0, Math.min(3, banca.length())).toUpperCase();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Important — oprim handler-ul ca sa nu avem memory leak
        handler.removeCallbacksAndMessages(null);
    }
}