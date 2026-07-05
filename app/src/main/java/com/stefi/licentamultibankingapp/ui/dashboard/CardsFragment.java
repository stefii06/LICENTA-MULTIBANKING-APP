package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.ui.dashboard.home.DetaliiCardBottomSheet;
import com.stefi.licentamultibankingapp.ui.dashboard.home.IngheataCardBottomSheet;
import com.stefi.licentamultibankingapp.ui.dashboard.home.TrimiteBottomSheet;

import java.util.List;

public class CardsFragment extends Fragment {

    private TextView tabToate, tabCurente, tabEconomii, tabDepozite;
    private LinearLayout layoutTabToate, layoutTabCurente, layoutTabEconomii, layoutTabDepozite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabToate    = view.findViewById(R.id.tabToate);
        tabCurente  = view.findViewById(R.id.tabCurente);
        tabEconomii = view.findViewById(R.id.tabEconomii);
        tabDepozite = view.findViewById(R.id.tabDepozite);

        layoutTabToate    = view.findViewById(R.id.layoutTabToate);
        layoutTabCurente  = view.findViewById(R.id.layoutTabCurente);
        layoutTabEconomii = view.findViewById(R.id.layoutTabEconomii);
        layoutTabDepozite = view.findViewById(R.id.layoutTabDepozite);

        tabToate.setOnClickListener(v    -> selecteazaTab(0));
        tabCurente.setOnClickListener(v  -> selecteazaTab(1));
        tabEconomii.setOnClickListener(v -> selecteazaTab(2));
        tabDepozite.setOnClickListener(v -> selecteazaTab(3));

        setupTabToate(view);
        setupTabCurente(view);
        setupEconomii(view);
        setupDepozite(view);
        setupButoane(view);

        selecteazaTab(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            setupTabToate(getView());
            setupTabCurente(getView());
            setupEconomii(getView());
            setupDepozite(getView());
        }
    }

    // ===== LOGICA TAB-URI =====

    private void selecteazaTab(int index) {
        resetTabStyle(tabToate);
        resetTabStyle(tabCurente);
        resetTabStyle(tabEconomii);
        resetTabStyle(tabDepozite);

        layoutTabToate.setVisibility(View.GONE);
        layoutTabCurente.setVisibility(View.GONE);
        layoutTabEconomii.setVisibility(View.GONE);
        layoutTabDepozite.setVisibility(View.GONE);

        switch (index) {
            case 0: activateTabStyle(tabToate);    layoutTabToate.setVisibility(View.VISIBLE);    break;
            case 1: activateTabStyle(tabCurente);  layoutTabCurente.setVisibility(View.VISIBLE);  break;
            case 2: activateTabStyle(tabEconomii); layoutTabEconomii.setVisibility(View.VISIBLE); break;
            case 3: activateTabStyle(tabDepozite); layoutTabDepozite.setVisibility(View.VISIBLE); break;
        }
    }

    private void activateTabStyle(TextView tab) {
        tab.setBackgroundColor(Color.parseColor("#4A90D9"));
        tab.setTextColor(Color.WHITE);
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackgroundColor(Color.parseColor("#1A2E4A"));
        tab.setTextColor(Color.parseColor("#7A9CC0"));
    }

    // ===== TAB TOATE =====

    private void setupTabToate(View view) {
        List<ContBancar> curente  = ContBancarRepository.getInstance().getConturiCurente();
        List<ContBancar> economii = ContBancarRepository.getInstance().getConturiEconomii();
        List<ContBancar> depozite = ContBancarRepository.getInstance().getDepozite();

        LinearLayout containerCurente  = view.findViewById(R.id.layoutCurenteToate);
        LinearLayout containerEconomii = view.findViewById(R.id.layoutEconomiiToate);
        LinearLayout containerDepozite = view.findViewById(R.id.layoutDepoziteToate);

        containerCurente.removeAllViews();
        containerEconomii.removeAllViews();
        containerDepozite.removeAllViews();

        for (ContBancar cont : curente) {
            containerCurente.addView(creeazaItemSimplut(cont));
        }
        for (ContBancar cont : economii) {
            containerEconomii.addView(creeazaItemEconomiiSimplut(cont));
        }
        for (ContBancar cont : depozite) {
            containerDepozite.addView(creeazaItemDepozitSimplut(cont));
        }
    }

    private View creeazaItemSimplut(ContBancar cont) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(Color.parseColor("#1A2E4A"));
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(4));
        item.setLayoutParams(p);

        item.addView(creeazaLogoBanca(cont.getNumeBanca()));

        LinearLayout col = new LinearLayout(getContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colP.setMarginStart(dp(12));
        col.setLayoutParams(colP);

        TextView tvNume = new TextView(getContext());
        tvNume.setText(cont.getNumeCont());
        tvNume.setTextColor(Color.WHITE);
        tvNume.setTextSize(14);
        tvNume.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvTip = new TextView(getContext());
        tvTip.setText(getTipCardText(cont));
        tvTip.setTextColor(Color.parseColor("#7A9CC0"));
        tvTip.setTextSize(11);

        col.addView(tvNume);
        col.addView(tvTip);
        item.addView(col);

        TextView tvSold = new TextView(getContext());
        tvSold.setText(String.format("%.0f %s", cont.getSold(), cont.getValuta()));
        tvSold.setTextColor(Color.WHITE);
        tvSold.setTextSize(14);
        tvSold.setTypeface(null, android.graphics.Typeface.BOLD);
        item.addView(tvSold);

        return item;
    }

    private View creeazaItemEconomiiSimplut(ContBancar cont) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(Color.parseColor("#1A2E4A"));
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(4));
        item.setLayoutParams(p);

        item.addView(creeazaLogoBanca(cont.getNumeBanca()));

        LinearLayout col = new LinearLayout(getContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colP.setMarginStart(dp(12));
        col.setLayoutParams(colP);

        TextView tvNume = new TextView(getContext());
        tvNume.setText(cont.getIconita() + " " + cont.getNumeCont());
        tvNume.setTextColor(Color.WHITE);
        tvNume.setTextSize(14);
        tvNume.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvInfo = new TextView(getContext());
        tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "%");
        tvInfo.setTextColor(Color.parseColor("#7A9CC0"));
        tvInfo.setTextSize(11);

        TextView tvProgres = new TextView(getContext());
        if (cont.getObiectiv() > 0) {
            int procent = (int) (cont.getSold() / cont.getObiectiv() * 100);
            tvProgres.setText(procent + "% din obiectiv • țintă " + (cont.getDataTinta() != null ? cont.getDataTinta() : "-"));
            tvProgres.setTextColor(Color.parseColor("#4CAF50"));
            tvProgres.setTextSize(10);
        }

        col.addView(tvNume);
        col.addView(tvInfo);
        col.addView(tvProgres);
        item.addView(col);

        TextView tvSold = new TextView(getContext());
        tvSold.setText(String.format("%.0f RON", cont.getSold()));
        tvSold.setTextColor(Color.parseColor("#4CAF50"));
        tvSold.setTextSize(14);
        tvSold.setTypeface(null, android.graphics.Typeface.BOLD);
        item.addView(tvSold);

        return item;
    }

    private View creeazaItemDepozitSimplut(ContBancar cont) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(Color.parseColor("#1A2E4A"));
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(4));
        item.setLayoutParams(p);

        item.addView(creeazaLogoBanca(cont.getNumeBanca()));

        LinearLayout col = new LinearLayout(getContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colP.setMarginStart(dp(12));
        col.setLayoutParams(colP);

        TextView tvNume = new TextView(getContext());
        tvNume.setText(cont.getIconita() + " " + cont.getNumeCont());
        tvNume.setTextColor(Color.WHITE);
        tvNume.setTextSize(14);
        tvNume.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvInfo = new TextView(getContext());
        tvInfo.setText(cont.getDobanda() + "% • scadență " + cont.getDataTinta());
        tvInfo.setTextColor(Color.parseColor("#7A9CC0"));
        tvInfo.setTextSize(11);

        col.addView(tvNume);
        col.addView(tvInfo);
        item.addView(col);

        LinearLayout colDreapta = new LinearLayout(getContext());
        colDreapta.setOrientation(LinearLayout.VERTICAL);
        colDreapta.setGravity(android.view.Gravity.END);

        TextView tvSold = new TextView(getContext());
        tvSold.setText(String.format("%.0f RON", cont.getSold()));
        tvSold.setTextColor(Color.WHITE);
        tvSold.setTextSize(14);
        tvSold.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvCastig = new TextView(getContext());
        tvCastig.setText(String.format("+%.0f RON", cont.getSold() * cont.getDobanda() / 100));
        tvCastig.setTextColor(Color.parseColor("#4CAF50"));
        tvCastig.setTextSize(11);

        colDreapta.addView(tvSold);
        colDreapta.addView(tvCastig);
        item.addView(colDreapta);

        return item;
    }

    // ===== TAB CURENTE =====

    private void setupTabCurente(View view) {
        List<ContBancar> curente = ContBancarRepository.getInstance().getConturiCurente();
        LinearLayout container = view.findViewById(R.id.layoutListaCurente);
        container.removeAllViews();

        double totalRon = 0;
        for (ContBancar cont : curente) {
            if ("RON".equals(cont.getValuta())) {
                totalRon += cont.getSold();
            }
        }

        TextView tvTotal = view.findViewById(R.id.tvTotalCurente);
        TextView tvNr    = view.findViewById(R.id.tvNrConturiCurente);
        tvTotal.setText(String.format("%.0f RON", totalRon));
        tvNr.setText("din " + curente.size() + " conturi curente");

        for (ContBancar cont : curente) {
            container.addView(creeazaItemCurentDetaliat(cont));
        }
    }

    private View creeazaItemCurentDetaliat(ContBancar cont) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.VERTICAL);
        item.setBackgroundColor(Color.parseColor("#1A2E4A"));
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(8));
        item.setLayoutParams(p);

        // Rândul de sus: logo + nume + sold
        LinearLayout randSus = new LinearLayout(getContext());
        randSus.setOrientation(LinearLayout.HORIZONTAL);
        randSus.setGravity(android.view.Gravity.CENTER_VERTICAL);
        randSus.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        randSus.addView(creeazaLogoBanca(cont.getNumeBanca()));

        LinearLayout col = new LinearLayout(getContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colP.setMarginStart(dp(12));
        col.setLayoutParams(colP);

        TextView tvNume = new TextView(getContext());
        tvNume.setText(cont.getNumeCont());
        tvNume.setTextColor(Color.WHITE);
        tvNume.setTextSize(15);
        tvNume.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvIban = new TextView(getContext());
        tvIban.setText(mascheazaIban(cont.getIban()));
        tvIban.setTextColor(Color.parseColor("#7A9CC0"));
        tvIban.setTextSize(11);

        col.addView(tvNume);
        col.addView(tvIban);
        randSus.addView(col);

        TextView tvSold = new TextView(getContext());
        tvSold.setText(String.format("%.0f %s", cont.getSold(), cont.getValuta()));
        tvSold.setTextColor(Color.WHITE);
        tvSold.setTextSize(15);
        tvSold.setTypeface(null, android.graphics.Typeface.BOLD);
        randSus.addView(tvSold);

        item.addView(randSus);

        // Linie separatoare
        View linie = new View(getContext());
        linie.setBackgroundColor(Color.parseColor("#12203A"));
        LinearLayout.LayoutParams linieP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        linieP.setMargins(0, dp(10), 0, dp(10));
        linie.setLayoutParams(linieP);
        item.addView(linie);

        // Rândul de jos: status + Detalii
        LinearLayout randJos = new LinearLayout(getContext());
        randJos.setOrientation(LinearLayout.HORIZONTAL);
        randJos.setGravity(android.view.Gravity.CENTER_VERTICAL);
        randJos.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvStatus = new TextView(getContext());
        tvStatus.setText("● " + getTipCardText(cont) + " • activ");
        tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        tvStatus.setTextSize(11);
        LinearLayout.LayoutParams sP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvStatus.setLayoutParams(sP);
        randJos.addView(tvStatus);

        TextView tvDetalii = new TextView(getContext());
        tvDetalii.setText("Detalii →");
        tvDetalii.setOnClickListener(v -> {
            DetaliiCardBottomSheet sheet = DetaliiCardBottomSheet.newInstance(cont.getIban());
            sheet.show(getParentFragmentManager(), "Detalii");
        });
        tvDetalii.setTextColor(Color.parseColor("#4A90D9"));
        tvDetalii.setTextSize(11);
        randJos.addView(tvDetalii);

        item.addView(randJos);

        return item;
    }

    // ===== ECONOMII =====

    private void setupEconomii(View view) {
        List<ContBancar> economii = ContBancarRepository.getInstance().getConturiEconomii();
        LinearLayout container = view.findViewById(R.id.layoutListaEconomii);
        container.removeAllViews();

        double totalEconomii = 0;
        double totalDobanda  = 0;

        for (ContBancar cont : economii) {
            totalEconomii += cont.getSold();
            totalDobanda  += cont.getSold() * cont.getDobanda() / 100;

            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_cont_economii, container, false);

            TextView tvIconita   = item.findViewById(R.id.tvIconitaEconomii);
            TextView tvNume      = item.findViewById(R.id.tvNumeContEconomii);
            TextView tvInfo      = item.findViewById(R.id.tvInfoContEconomii);
            TextView tvSold      = item.findViewById(R.id.tvSoldEconomii);
            TextView tvProcent   = item.findViewById(R.id.tvProcentEconomii);
            TextView tvObiectiv  = item.findViewById(R.id.tvObiectivEconomii);
            TextView tvDataTinta = item.findViewById(R.id.tvDataTintaEconomii);
            ProgressBar progress = item.findViewById(R.id.progressEconomii);

            // Logo bancă în loc de emoji
            GradientDrawable fundalEconomii = new GradientDrawable();
            fundalEconomii.setShape(GradientDrawable.RECTANGLE);
            fundalEconomii.setCornerRadius(dp(8));
            fundalEconomii.setColor(Color.parseColor(getCuloareBanca(cont.getNumeBanca())));
            tvIconita.setBackground(fundalEconomii);
            tvIconita.setText(getInitialeBanca(cont.getNumeBanca()));
            tvIconita.setTextColor(Color.WHITE);

            tvNume.setText(cont.getNumeCont());
            tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "%");
            tvSold.setText(String.format("%.0f RON", cont.getSold()));

            if (cont.getObiectiv() > 0) {
                int procent = (int) (cont.getSold() / cont.getObiectiv() * 100);
                tvProcent.setText(procent + "%");
                progress.setProgress(procent);
                tvObiectiv.setText("obiectiv: " + String.format("%.0f", cont.getObiectiv()) + " RON");
            }

            tvDataTinta.setText("țintă: " + (cont.getDataTinta() != null ? cont.getDataTinta() : "-"));

            item.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DetaliiContEconomiiActivity.class);
                intent.putExtra("iban", cont.getIban());
                startActivity(intent);
            });
            container.addView(item);
        }

        TextView tvTotal   = view.findViewById(R.id.tvTotalEconomii);
        TextView tvDobanda = view.findViewById(R.id.tvDobandaTotala);
        tvTotal.setText(String.format("%.2f RON", totalEconomii));
        tvDobanda.setText(String.format("+%.2f RON", totalDobanda));
    }

    // ===== DEPOZITE =====

    private void setupDepozite(View view) {
        List<ContBancar> depozite = ContBancarRepository.getInstance().getDepozite();
        LinearLayout container = view.findViewById(R.id.layoutListaDepozite);
        container.removeAllViews();

        for (ContBancar cont : depozite) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_depozit, container, false);

            TextView tvIconita = item.findViewById(R.id.tvIconitaDepozit);
            TextView tvNume    = item.findViewById(R.id.tvNumeDepozit);
            TextView tvInfo    = item.findViewById(R.id.tvInfoDepozit);
            TextView tvSold    = item.findViewById(R.id.tvSoldDepozit);
            TextView tvCastig  = item.findViewById(R.id.tvCastigDepozit);
            ProgressBar progress = item.findViewById(R.id.progressDepozit);
            TextView tvZile    = item.findViewById(R.id.tvZileDepozit);

            // Logo bancă în loc de emoji
            GradientDrawable fundalDepozit = new GradientDrawable();
            fundalDepozit.setShape(GradientDrawable.RECTANGLE);
            fundalDepozit.setCornerRadius(dp(8));
            fundalDepozit.setColor(Color.parseColor(getCuloareBanca(cont.getNumeBanca())));
            tvIconita.setBackground(fundalDepozit);
            tvIconita.setText(getInitialeBanca(cont.getNumeBanca()));
            tvIconita.setTextColor(Color.WHITE);

            tvNume.setText(cont.getNumeCont());
            tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "% • Scadență: " + cont.getDataTinta());
            tvSold.setText(String.format("%.0f RON", cont.getSold()));
            tvCastig.setText(String.format("+%.0f RON", cont.getSold() * cont.getDobanda() / 100));
            progress.setProgress(2);
            tvZile.setText("0 / 365 zile");

            item.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DetaliiDepozitActivity.class);
                intent.putExtra("iban", cont.getIban());
                startActivity(intent);
            });
            container.addView(item);
        }
    }

    // ===== BUTOANE =====

    private void setupButoane(View view) {
        view.findViewById(R.id.btnTrimiteCards).setOnClickListener(v -> {
            TrimiteBottomSheet sheet = new TrimiteBottomSheet();
            sheet.show(getParentFragmentManager(), "Trimite");
        });

        view.findViewById(R.id.btnIngheataCards).setOnClickListener(v -> {
            IngheataCardBottomSheet sheet = new IngheataCardBottomSheet();
            sheet.setListener(inghetat -> {});
            sheet.show(getParentFragmentManager(), "Ingheata");
        });

        view.findViewById(R.id.btnDetaliiCards).setOnClickListener(v -> {
            DetaliiCardBottomSheet sheet = new DetaliiCardBottomSheet();
            sheet.show(getParentFragmentManager(), "Detalii");
        });

        view.findViewById(R.id.btnAdaugaContEconomii).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreareContEconomiiActivity.class)));

        view.findViewById(R.id.btnAdaugaContDepozit).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreareDepozitActivity.class)));

    }

    // ===== HELPER-E =====

    private TextView creeazaLogoBanca(String numeBanca) {
        TextView tvLogo = new TextView(getContext());
        tvLogo.setGravity(android.view.Gravity.CENTER);
        tvLogo.setTextColor(Color.WHITE);
        tvLogo.setTextSize(12);
        tvLogo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvLogo.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        tvLogo.setText(getInitialeBanca(numeBanca));

        GradientDrawable fundal = new GradientDrawable();
        fundal.setShape(GradientDrawable.RECTANGLE);
        fundal.setCornerRadius(dp(8));
        fundal.setColor(Color.parseColor(getCuloareBanca(numeBanca)));
        tvLogo.setBackground(fundal);

        return tvLogo;
    }

    private String getCuloareBanca(String numeBanca) {
        if (numeBanca == null) return "#37474F";
        switch (numeBanca.toUpperCase()) {
            case "BCR":                return "#D32F2F";
            case "ING":                return "#E65100";
            case "BRD":                return "#1565C0";
            case "RAIFFEISEN":         return "#FFD600";
            case "BANCA TRANSILVANIA":
            case "BT":                 return "#2E7D32";
            case "CEC BANK":
            case "CEC":                return "#4527A0";
            default:                   return "#37474F";
        }
    }

    private String getInitialeBanca(String numeBanca) {
        if (numeBanca == null) return "?";
        switch (numeBanca.toUpperCase()) {
            case "BCR":                return "BCR";
            case "ING":                return "ING";
            case "BRD":                return "BRD";
            case "RAIFFEISEN":         return "RZB";
            case "BANCA TRANSILVANIA":
            case "BT":                 return "BT";
            case "CEC BANK":
            case "CEC":                return "CEC";
            default:
                return numeBanca.length() > 3 ? numeBanca.substring(0, 3) : numeBanca;
        }
    }

    private String mascheazaIban(String iban) {
        if (iban == null || iban.length() < 8) return iban;
        return iban.substring(0, 8) + "..." + iban.substring(iban.length() - 4);
    }

    private String getTipCardText(ContBancar cont) {
        if (cont.getTipCard() == null) return "Card";
        if (cont.getTipCard().equalsIgnoreCase("Visa"))
            return "Visa ●●●● " + ultimeleCifre(cont.getIban());
        if (cont.getTipCard().equalsIgnoreCase("Mastercard"))
            return "MC ●●●● " + ultimeleCifre(cont.getIban());
        return cont.getTipCard();
    }

    private String ultimeleCifre(String iban) {
        if (iban == null || iban.length() < 4) return "0000";
        return iban.substring(iban.length() - 4);
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }
}