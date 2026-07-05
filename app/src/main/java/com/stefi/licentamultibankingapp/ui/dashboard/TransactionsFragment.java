package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionsFragment extends Fragment {

    // Tabs
    private View layoutTabToate, layoutTabIntrari, layoutTabIesiri;
    private TextView tabToate, tabIntrari, tabIesiri;

    // Header luna (comun pentru toate taburile)
    private TextView tvLunaSelectata, tvLunaSubtitlu;
    private TextView btnLunaAnterioara, btnLunaUrmatoare;

    // Luna selectata global
    private int lunaSelectata;
    private int anSelectat;

    // Calendar (tab Toate)
    private LinearLayout layoutCautaBtn, layoutCalendar;
    private TextView tvCautaLabel, tvCautaSub, tvCautaArrow;

    private GridLayout gridZileCalendar;
    private TextView tvLunaCalendar;

    // Filtru zi activ (tab Toate)
    private boolean filtruZiActiv = false;
    private int filtruZi = -1;
    private int filtruZiLuna = -1;
    private int filtruZiAn = -1;

    // Sortare intrari/iesiri: 0=recente, 1=desc, 2=asc
    private int sortIntrari = 0;
    private int sortIesiri = 0;

    private static final String[] NUME_LUNI_LUNG = {
            "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie",
            "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializam luna pe luna curenta
        Calendar azi = Calendar.getInstance();
        lunaSelectata = azi.get(Calendar.MONTH);
        anSelectat = azi.get(Calendar.YEAR);

        // Tabs
        layoutTabToate = view.findViewById(R.id.layoutTabToate);
        layoutTabIntrari = view.findViewById(R.id.layoutTabIntrari);
        layoutTabIesiri = view.findViewById(R.id.layoutTabIesiri);
        tabToate = view.findViewById(R.id.tabToate);
        tabIntrari = view.findViewById(R.id.tabIntrari);
        tabIesiri = view.findViewById(R.id.tabIesiri);

        // Header luna
        tvLunaSelectata = view.findViewById(R.id.tvLunaSelectata);
        tvLunaSubtitlu = view.findViewById(R.id.tvLunaSubtitlu);
        btnLunaAnterioara = view.findViewById(R.id.btnLunaAnterioara);
        btnLunaUrmatoare = view.findViewById(R.id.btnLunaUrmatoare);

        // Calendar
        layoutCautaBtn = view.findViewById(R.id.layoutCautaBtn);
        layoutCalendar = view.findViewById(R.id.layoutCalendar);
        tvCautaLabel = view.findViewById(R.id.tvCautaLabel);
        tvCautaSub = view.findViewById(R.id.tvCautaSub);
        tvCautaArrow = view.findViewById(R.id.tvCautaArrow);
        gridZileCalendar = view.findViewById(R.id.gridZileCalendar);
        tvLunaCalendar = view.findViewById(R.id.tvLunaCalendar);

        setupHeaderLuna(view);
        setupTabs(view);
        setupCalendar(view);
        actualizeazaToate(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) actualizeazaToate(getView());
    }

    // ---------------------------------------------------------------
    // HEADER LUNA — comun pentru toate taburile
    // ---------------------------------------------------------------

    private void setupHeaderLuna(View view) {
        actualizeazaHeaderLuna();

        btnLunaAnterioara.setOnClickListener(v -> {
            lunaSelectata--;
            if (lunaSelectata < 0) { lunaSelectata = 11; anSelectat--; }
            stergeFiltrZi(view);
            if (layoutCalendar.getVisibility() == View.VISIBLE) {
                construiesteCalendar();
            }
            actualizeazaHeaderLuna();
            actualizeazaToate(view);
        });

        btnLunaUrmatoare.setOnClickListener(v -> {
            Calendar azi = Calendar.getInstance();
            if (lunaSelectata == azi.get(Calendar.MONTH) && anSelectat == azi.get(Calendar.YEAR)) return;
            lunaSelectata++;
            if (lunaSelectata > 11) { lunaSelectata = 0; anSelectat++; }
            stergeFiltrZi(view);
            if (layoutCalendar.getVisibility() == View.VISIBLE) {
                construiesteCalendar();
            }
            actualizeazaHeaderLuna();
            actualizeazaToate(view);
        });
    }

    private void actualizeazaHeaderLuna() {
        if (tvLunaSelectata == null) return;
        tvLunaSelectata.setText(NUME_LUNI_LUNG[lunaSelectata] + " " + anSelectat);

        Calendar azi = Calendar.getInstance();
        boolean eLunaCurenta = (lunaSelectata == azi.get(Calendar.MONTH)
                && anSelectat == azi.get(Calendar.YEAR));
        tvLunaSubtitlu.setText(eLunaCurenta ? "luna curenta" : "");

        // Dezactivam sageata dreapta daca suntem pe luna curenta
        btnLunaUrmatoare.setTextColor(eLunaCurenta ? 0xFF3A5A7A : 0xFF4A90D9);
    }

    // Actualizeaza toate cele 3 taburi dupa schimbarea lunii
    private void actualizeazaToate(View view) {
        setupTabToate(view);
        setupTabIntrari(view);
        setupTabIesiri(view);
    }

    // ---------------------------------------------------------------
    // TABS
    // ---------------------------------------------------------------

    private void setupTabs(View view) {
        tabToate.setOnClickListener(v -> selectTab(0));
        tabIntrari.setOnClickListener(v -> selectTab(1));
        tabIesiri.setOnClickListener(v -> selectTab(2));
        selectTab(0);
    }

    private void selectTab(int index) {
        layoutTabToate.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        layoutTabIntrari.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        layoutTabIesiri.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        tabToate.setBackground(index == 0 ? creazaFundalChip(0xFF4A90D9) : null);
        tabIntrari.setBackground(index == 1 ? creazaFundalChip(0xFF4A90D9) : null);
        tabIesiri.setBackground(index == 2 ? creazaFundalChip(0xFF4A90D9) : null);

        tabToate.setTextColor(index == 0 ? 0xFFFFFFFF : 0xFF7A9CC0);
        tabIntrari.setTextColor(index == 1 ? 0xFFFFFFFF : 0xFF7A9CC0);
        tabIesiri.setTextColor(index == 2 ? 0xFFFFFFFF : 0xFF7A9CC0);
    }

    // ---------------------------------------------------------------
    // CALENDAR (tab Toate) — butonul de sus doar deschide/inchide
    // calendarul; selectarea/deselectarea zilei se face din calendar
    // ---------------------------------------------------------------

    private void setupCalendar(View view) {
        // Singurul control care deschide/inchide calendarul
        layoutCautaBtn.setOnClickListener(v -> {
            if (layoutCalendar.getVisibility() == View.VISIBLE) {
                layoutCalendar.setVisibility(View.GONE);
                tvCautaArrow.setText("▼");
            } else {
                layoutCalendar.setVisibility(View.VISIBLE);
                tvCautaArrow.setText("▲");
                construiesteCalendar();
            }
        });
    }

    private void stergeFiltrZi(View view) {
        filtruZiActiv = false;
        filtruZi = -1;
        filtruZiLuna = -1;
        filtruZiAn = -1;

        tvCautaLabel.setText("Cauta dupa zi");
        tvCautaSub.setText("selecteaza o zi din calendar");

        setupTabToate(view);
    }

    private void construiesteCalendar() {
        if (getContext() == null) return;

        tvLunaCalendar.setText(NUME_LUNI_LUNG[lunaSelectata] + " " + anSelectat);
        gridZileCalendar.removeAllViews();

        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();

        // Ce zile au tranzactii
        Map<Integer, Integer> tipZi = new HashMap<>();
        for (Tranzactie t : toate) {
            if (t.getData() == null) continue;
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getData());
            if (cal.get(Calendar.MONTH) == lunaSelectata && cal.get(Calendar.YEAR) == anSelectat) {
                int zi = cal.get(Calendar.DAY_OF_MONTH);
                boolean eIntrare = t.getSuma() >= 0;
                if (!tipZi.containsKey(zi)) {
                    tipZi.put(zi, eIntrare ? 1 : 0);
                } else {
                    int ex = tipZi.get(zi);
                    if ((ex == 0 && eIntrare) || (ex == 1 && !eIntrare)) tipZi.put(zi, 2);
                }
            }
        }

        Calendar primaZi = Calendar.getInstance();
        primaZi.set(anSelectat, lunaSelectata, 1);
        int ziuaSapt = primaZi.get(Calendar.DAY_OF_WEEK);
        int offset = (ziuaSapt == Calendar.SUNDAY) ? 6 : ziuaSapt - 2;
        int nrZile = primaZi.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < offset; i++) {
            TextView tv = new TextView(getContext());
            tv.setText("");
            tv.setLayoutParams(celulaCalendarParams());
            gridZileCalendar.addView(tv);
        }

        Calendar azi = Calendar.getInstance();

        for (int zi = 1; zi <= nrZile; zi++) {
            final int ziF = zi;
            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(zi));
            tv.setTextSize(10);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setLayoutParams(celulaCalendarParams());

            boolean eAzi = (zi == azi.get(Calendar.DAY_OF_MONTH)
                    && lunaSelectata == azi.get(Calendar.MONTH)
                    && anSelectat == azi.get(Calendar.YEAR));
            boolean eSel = (filtruZiActiv && zi == filtruZi
                    && lunaSelectata == filtruZiLuna && anSelectat == filtruZiAn);

            if (eSel) {
                tv.setBackgroundColor(0xFF4A90D9);
                tv.setTextColor(0xFFFFFFFF);
            } else if (eAzi) {
                tv.setTextColor(0xFF4A90D9);
            } else if (tipZi.containsKey(zi)) {
                int tip = tipZi.get(zi);
                if (tip == 0) tv.setTextColor(0xFFFF8A80);
                else if (tip == 1) tv.setTextColor(0xFF80CBC4);
                else tv.setTextColor(0xFFFFD54F);
            } else {
                tv.setTextColor(0xFF5A7A9A);
            }

            if (tipZi.containsKey(zi)) {
                tv.setOnClickListener(v -> {
                    boolean esteDejaSelectata = filtruZiActiv && ziF == filtruZi
                            && lunaSelectata == filtruZiLuna && anSelectat == filtruZiAn;
                    if (esteDejaSelectata) {
                        if (getView() != null) stergeFiltrZi(getView());
                        construiesteCalendar();
                    } else {
                        selecteazaZi(ziF, lunaSelectata, anSelectat);
                    }
                });
            }

            gridZileCalendar.addView(tv);
        }
    }

    private GridLayout.LayoutParams celulaCalendarParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 4, 2, 4);
        return params;
    }

    private void selecteazaZi(int zi, int luna, int an) {
        filtruZiActiv = true;
        filtruZi = zi;
        filtruZiLuna = luna;
        filtruZiAn = an;

        construiesteCalendar();

        tvCautaLabel.setText(zi + " " + NUME_LUNI_LUNG[luna]);
        tvCautaSub.setText("Atinge din nou ziua ca sa anulezi");

        if (getView() != null) setupTabToate(getView());
    }

    // ---------------------------------------------------------------
    // TAB TOATE
    // ---------------------------------------------------------------

    private void setupTabToate(View view) {
        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();

        // Filtram dupa luna selectata
        List<Tranzactie> dinLuna = filtreazaDupaLuna(toate, lunaSelectata, anSelectat);

        // Daca avem filtru zi activ, filtram si dupa zi
        List<Tranzactie> deAfisat;
        if (filtruZiActiv) {
            deAfisat = new ArrayList<>();
            for (Tranzactie t : dinLuna) {
                if (t.getData() == null) continue;
                Calendar cal = Calendar.getInstance();
                cal.setTime(t.getData());
                if (cal.get(Calendar.DAY_OF_MONTH) == filtruZi
                        && cal.get(Calendar.MONTH) == filtruZiLuna
                        && cal.get(Calendar.YEAR) == filtruZiAn) {
                    deAfisat.add(t);
                }
            }
        } else {
            deAfisat = dinLuna;
        }

        TextView tvTotal = view.findViewById(R.id.tvTotalCheltuit);
        TextView tvNr = view.findViewById(R.id.tvNrTranzactii);
        LinearLayout layoutLista = view.findViewById(R.id.layoutListaTranzactii);
        LinearLayout layoutInsight = view.findViewById(R.id.layoutInsight);
        TextView tvInsight = view.findViewById(R.id.tvInsight);

        layoutLista.removeAllViews();

        double total = 0;
        for (Tranzactie t : dinLuna) {
            if (t.getSuma() < 0) total += Math.abs(t.getSuma());
        }
        tvTotal.setText(String.format("-%.0f RON", total));
        tvNr.setText(String.valueOf(dinLuna.size()));

        if (dinLuna.isEmpty()) {
            TextView tvGol = new TextView(getContext());
            tvGol.setText("Nu exista tranzactii in " + NUME_LUNI_LUNG[lunaSelectata]);
            tvGol.setTextColor(0xFF7A9CC0);
            tvGol.setTextSize(13);
            tvGol.setPadding(0, 32, 0, 0);
            tvGol.setGravity(android.view.Gravity.CENTER);
            layoutLista.addView(tvGol);
            return;
        }

        // Insight
        Map<String, Integer> frecventa = new HashMap<>();
        for (Tranzactie t : dinLuna) {
            if (t.getSuma() < 0) {
                frecventa.put(t.getCategorie(), frecventa.getOrDefault(t.getCategorie(), 0) + 1);
            }
        }
        String catMax = "";
        int maxFrecv = 0;
        for (Map.Entry<String, Integer> entry : frecventa.entrySet()) {
            if (entry.getValue() > maxFrecv) { maxFrecv = entry.getValue(); catMax = entry.getKey(); }
        }
        if (!catMax.isEmpty() && maxFrecv > 1) {
            layoutInsight.setVisibility(View.VISIBLE);
            tvInsight.setText("Categoria cu cele mai multe tranzactii: " + catMax + " (" + maxFrecv + " tranzactii)");
        } else {
            layoutInsight.setVisibility(View.GONE);
        }

        setupFiltre(view, dinLuna, deAfisat);
        afiseazaGrupatepeZile(deAfisat, layoutLista, view);
    }

    // ---------------------------------------------------------------
    // TAB INTRARI
    // ---------------------------------------------------------------

    private void setupTabIntrari(View view) {
        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();
        List<Tranzactie> dinLuna = filtreazaDupaLuna(toate, lunaSelectata, anSelectat);

        List<Tranzactie> intrari = new ArrayList<>();
        for (Tranzactie t : dinLuna) {
            if (t.getSuma() >= 0) intrari.add(t);
        }

        TextView tvTotal = view.findViewById(R.id.tvTotalIntrari);
        TextView tvNr = view.findViewById(R.id.tvNrIntrari);
        LinearLayout layoutLista = view.findViewById(R.id.layoutListaIntrari);

        double total = 0;
        for (Tranzactie t : intrari) total += t.getSuma();
        tvTotal.setText(String.format("+%.0f RON", total));
        tvNr.setText(String.valueOf(intrari.size()));

        // Butoane sortare
        TextView btnRecente = view.findViewById(R.id.btnSortIntrariRecente);
        TextView btnDesc = view.findViewById(R.id.btnSortIntrariDesc);
        TextView btnAsc = view.findViewById(R.id.btnSortIntrariAsc);

        actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, sortIntrari, true);

        btnRecente.setOnClickListener(v -> {
            sortIntrari = 0;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 0, true);
            afiseazaListaIntrariIesiri(intrari, sortIntrari, layoutLista);
        });
        btnDesc.setOnClickListener(v -> {
            sortIntrari = 1;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 1, true);
            afiseazaListaIntrariIesiri(intrari, sortIntrari, layoutLista);
        });
        btnAsc.setOnClickListener(v -> {
            sortIntrari = 2;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 2, true);
            afiseazaListaIntrariIesiri(intrari, sortIntrari, layoutLista);
        });

        afiseazaListaIntrariIesiri(intrari, sortIntrari, layoutLista);
    }

    // ---------------------------------------------------------------
    // TAB IESIRI
    // ---------------------------------------------------------------

    private void setupTabIesiri(View view) {
        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();
        List<Tranzactie> dinLuna = filtreazaDupaLuna(toate, lunaSelectata, anSelectat);

        List<Tranzactie> iesiri = new ArrayList<>();
        for (Tranzactie t : dinLuna) {
            if (t.getSuma() < 0) iesiri.add(t);
        }

        TextView tvTotal = view.findViewById(R.id.tvTotalIesiri);
        TextView tvNr = view.findViewById(R.id.tvNrIesiri);
        LinearLayout layoutLista = view.findViewById(R.id.layoutListaIesiri);

        double total = 0;
        for (Tranzactie t : iesiri) total += Math.abs(t.getSuma());
        tvTotal.setText(String.format("-%.0f RON", total));
        tvNr.setText(String.valueOf(iesiri.size()));

        // Butoane sortare
        TextView btnRecente = view.findViewById(R.id.btnSortIesiriRecente);
        TextView btnDesc = view.findViewById(R.id.btnSortIesiriDesc);
        TextView btnAsc = view.findViewById(R.id.btnSortIesiriAsc);

        actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, sortIesiri, false);

        btnRecente.setOnClickListener(v -> {
            sortIesiri = 0;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 0, false);
            afiseazaListaIntrariIesiri(iesiri, sortIesiri, layoutLista);
        });
        btnDesc.setOnClickListener(v -> {
            sortIesiri = 1;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 1, false);
            afiseazaListaIntrariIesiri(iesiri, sortIesiri, layoutLista);
        });
        btnAsc.setOnClickListener(v -> {
            sortIesiri = 2;
            actualizeazaButoaneSort3(btnRecente, btnDesc, btnAsc, 2, false);
            afiseazaListaIntrariIesiri(iesiri, sortIesiri, layoutLista);
        });

        afiseazaListaIntrariIesiri(iesiri, sortIesiri, layoutLista);
    }

    // Actualizeaza stilul celor 3 butoane de sortare
    private void actualizeazaButoaneSort3(TextView btnRecente, TextView btnDesc,
                                          TextView btnAsc, int sortActiv, boolean eIntrari) {
        int culoareActiv = 0xFF1A3C6E;
        int culoareInactiv = 0xFF1A2E4A;

        btnRecente.setBackground(creazaFundalChip(sortActiv == 0 ? culoareActiv : culoareInactiv));
        btnRecente.setTextColor(sortActiv == 0 ? 0xFFFFFFFF : 0xFF7A9CC0);

        btnDesc.setBackground(creazaFundalChip(sortActiv == 1 ? culoareActiv : culoareInactiv));
        btnDesc.setTextColor(sortActiv == 1 ? 0xFFFFFFFF : 0xFF7A9CC0);

        btnAsc.setBackground(creazaFundalChip(sortActiv == 2 ? culoareActiv : culoareInactiv));
        btnAsc.setTextColor(sortActiv == 2 ? 0xFFFFFFFF : 0xFF7A9CC0);
    }

    // Afiseaza lista simpla pentru Intrari/Iesiri cu sortarea specificata
    private void afiseazaListaIntrariIesiri(List<Tranzactie> lista, int sortare,
                                            LinearLayout container) {
        // Copiem lista ca sa nu modificam originalul
        List<Tranzactie> copie = new ArrayList<>(lista);

        if (sortare == 0) {
            // Recente — sortam dupa data descrescator
            Collections.sort(copie, (a, b) -> {
                if (a.getData() == null || b.getData() == null) return 0;
                return b.getData().compareTo(a.getData());
            });
        } else if (sortare == 1) {
            // Suma descrescatoare (valoare absoluta)
            Collections.sort(copie, (a, b) ->
                    Float.compare(Math.abs(b.getSuma()), Math.abs(a.getSuma())));
        } else {
            // Suma crescatoare (valoare absoluta)
            Collections.sort(copie, (a, b) ->
                    Float.compare(Math.abs(a.getSuma()), Math.abs(b.getSuma())));
        }

        afiseazaListaSimple(copie, container);
    }

    private void afiseazaListaSimple(List<Tranzactie> tranzactii, LinearLayout container) {
        container.removeAllViews();

        if (tranzactii.isEmpty()) {
            TextView tvGol = new TextView(getContext());
            tvGol.setText("Nu exista tranzactii in aceasta perioada");
            tvGol.setTextColor(0xFF7A9CC0);
            tvGol.setTextSize(12);
            tvGol.setPadding(0, 16, 0, 0);
            container.addView(tvGol);
            return;
        }

        SimpleDateFormat sdfData = new SimpleDateFormat("d MMM", new Locale("ro", "RO"));

        for (Tranzactie t : tranzactii) {
            View item = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_tranzactie, container, false);

            TextView tvEmoji = item.findViewById(R.id.tvEmojiTranzactie);
            TextView tvCategorie = item.findViewById(R.id.tvCategorieTranzactie);
            TextView tvCard = item.findViewById(R.id.tvCardTranzactie);
            TextView tvSuma = item.findViewById(R.id.tvSumaTranzactie);
            TextView tvBadge = item.findViewById(R.id.tvBadgeTranzactie);
            TextView tvActiune = item.findViewById(R.id.tvActiuneTranzactie);

            tvEmoji.setText(t.getEmoji() != null && !t.getEmoji().isEmpty() ? t.getEmoji() : "•");

            String descriere = t.getDescriere();
            tvCategorie.setText(descriere != null && !descriere.isEmpty() ? descriere : t.getCategorie());

            String dataStr = t.getData() != null ? " • " + sdfData.format(t.getData()) : "";
            tvCard.setText(t.getNumeBanca() + " •••• " + t.getUltimeleCifre() + dataStr);

            float suma = t.getSuma();
            if (suma >= 0) {
                tvSuma.setText(String.format("+%.0f RON", suma));
                tvSuma.setTextColor(0xFF4DCEA8);
            } else {
                tvSuma.setText(String.format("%.0f RON", suma));
                tvSuma.setTextColor(0xFFE05252);
            }

            tvBadge.setVisibility(View.GONE);
            tvActiune.setVisibility(View.GONE);

            container.addView(item);
        }
    }

    // ---------------------------------------------------------------
    // HELPER: filtreaza tranzactiile dupa luna si an
    // ---------------------------------------------------------------

    private List<Tranzactie> filtreazaDupaLuna(List<Tranzactie> toate, int luna, int an) {
        List<Tranzactie> rezultat = new ArrayList<>();
        for (Tranzactie t : toate) {
            if (t.getData() == null) continue;
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getData());
            if (cal.get(Calendar.MONTH) == luna && cal.get(Calendar.YEAR) == an) {
                rezultat.add(t);
            }
        }
        return rezultat;
    }

    // ---------------------------------------------------------------
    // FILTRE CATEGORII (tab Toate) — chip-uri rotunjite tip pastila
    // ---------------------------------------------------------------

    private void setupFiltre(View view, List<Tranzactie> dinLuna,
                             List<Tranzactie> deAfisat) {
        LinearLayout layoutFiltre = view.findViewById(R.id.layoutFiltre);
        layoutFiltre.removeAllViews();

        String[] categorii = {"Toate", "Mâncare", "Shopping", "Transport",
                "Divertisment", "Sănătate", "Utilități", "Venit"};

        LinearLayout layoutLista = view.findViewById(R.id.layoutListaTranzactii);

        for (String cat : categorii) {
            TextView chip = new TextView(getContext());
            chip.setText(cat);
            chip.setTextSize(11);
            chip.setTextColor(cat.equals("Toate") ? 0xFFFFFFFF : 0xFF7A9CC0);
            chip.setBackground(creazaFundalChip(cat.equals("Toate") ? 0xFF4A90D9 : 0xFF1A2E4A));
            chip.setPadding(24, 10, 24, 10);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(8);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                for (int i = 0; i < layoutFiltre.getChildCount(); i++) {
                    TextView c = (TextView) layoutFiltre.getChildAt(i);
                    c.setTextColor(0xFF7A9CC0);
                    c.setBackground(creazaFundalChip(0xFF1A2E4A));
                }
                chip.setTextColor(0xFFFFFFFF);
                chip.setBackground(creazaFundalChip(0xFF4A90D9));

                List<Tranzactie> filtrate = new ArrayList<>();
                if (cat.equals("Toate")) {
                    filtrate = deAfisat;
                } else {
                    for (Tranzactie t : deAfisat) {
                        if (t.getCategorie().equals(cat)) filtrate.add(t);
                    }
                }
                afiseazaGrupatepeZile(filtrate, layoutLista, view);
            });

            layoutFiltre.addView(chip);
        }
    }

    // Creeaza un fundal rotunjit tip pastila pentru chip-urile de filtrare
    private android.graphics.drawable.GradientDrawable creazaFundalChip(int culoare) {
        android.graphics.drawable.GradientDrawable fundal = new android.graphics.drawable.GradientDrawable();
        fundal.setColor(culoare);
        fundal.setCornerRadius(40f);
        return fundal;
    }

    // Creeaza un fundal usor rotunjit pentru header-ul fiecarei zile din lista
    private android.graphics.drawable.GradientDrawable creazaFundalHeaderZi(int culoare) {
        android.graphics.drawable.GradientDrawable fundal = new android.graphics.drawable.GradientDrawable();
        fundal.setColor(culoare);
        fundal.setCornerRadius(20f);
        return fundal;
    }

    // ---------------------------------------------------------------
    // GRUPARE PE ZILE (tab Toate)
    // ---------------------------------------------------------------

    private void afiseazaGrupatepeZile(List<Tranzactie> tranzactii,
                                       LinearLayout container, View rootView) {
        container.removeAllViews();

        if (tranzactii.isEmpty()) {
            TextView tvGol = new TextView(getContext());
            tvGol.setText("Nu exista tranzactii pentru perioada selectata");
            tvGol.setTextColor(0xFF7A9CC0);
            tvGol.setTextSize(12);
            tvGol.setPadding(0, 16, 0, 0);
            container.addView(tvGol);
            return;
        }

        Map<String, List<Tranzactie>> grupate = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        for (Tranzactie t : tranzactii) {
            if (t.getData() == null) continue;
            String cheie = sdf.format(t.getData());
            if (!grupate.containsKey(cheie)) grupate.put(cheie, new ArrayList<>());
            grupate.get(cheie).add(t);
        }

        Calendar azi = Calendar.getInstance();
        Calendar ieri = Calendar.getInstance();
        ieri.add(Calendar.DAY_OF_MONTH, -1);
        String cheieAzi = sdf.format(azi.getTime());
        String cheieIeri = sdf.format(ieri.getTime());
        SimpleDateFormat sdfAfisare = new SimpleDateFormat("d MMM yyyy", new Locale("ro", "RO"));

        for (Map.Entry<String, List<Tranzactie>> entry : grupate.entrySet()) {
            String cheie = entry.getKey();
            List<Tranzactie> listaZi = entry.getValue();

            double totalIesiri = 0, totalIntrari = 0;
            for (Tranzactie t : listaZi) {
                if (t.getSuma() < 0) totalIesiri += Math.abs(t.getSuma());
                else totalIntrari += t.getSuma();
            }

            String etichetaZi;
            try {
                java.util.Date dataZi = sdf.parse(cheie);
                if (cheie.equals(cheieAzi)) etichetaZi = "Azi";
                else if (cheie.equals(cheieIeri)) etichetaZi = "Ieri";
                else etichetaZi = sdfAfisare.format(dataZi);
            } catch (Exception e) { etichetaZi = cheie; }

            LinearLayout grupZi = new LinearLayout(getContext());
            grupZi.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams pg = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pg.setMargins(0, 0, 0, 8);
            grupZi.setLayoutParams(pg);

            LinearLayout header = new LinearLayout(getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setBackground(creazaFundalHeaderZi(0xFF1A2C4A));
            header.setPadding(16, 14, 16, 14);
            header.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams ph = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ph.setMargins(0, 0, 0, 2);
            header.setLayoutParams(ph);

            TextView tvZi = new TextView(getContext());
            tvZi.setText(etichetaZi);
            tvZi.setTextColor(0xFFFFFFFF);
            tvZi.setTextSize(12);
            tvZi.setTypeface(null, android.graphics.Typeface.BOLD);
            tvZi.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            header.addView(tvZi);

            LinearLayout layoutSume = new LinearLayout(getContext());
            layoutSume.setOrientation(LinearLayout.HORIZONTAL);
            layoutSume.setGravity(android.view.Gravity.CENTER_VERTICAL);

            if (totalIesiri > 0) {
                TextView tvOut = new TextView(getContext());
                tvOut.setText(String.format("-%.0f RON", totalIesiri));
                tvOut.setTextColor(0xFFE05252);
                tvOut.setTextSize(11);
                tvOut.setTypeface(null, android.graphics.Typeface.BOLD);
                layoutSume.addView(tvOut);
            }
            if (totalIntrari > 0 && totalIesiri > 0) {
                TextView tvSep = new TextView(getContext());
                tvSep.setText("  |  ");
                tvSep.setTextColor(0xFF5A7A9A);
                tvSep.setTextSize(10);
                layoutSume.addView(tvSep);
            }
            if (totalIntrari > 0) {
                TextView tvIn = new TextView(getContext());
                tvIn.setText(String.format("+%.0f RON", totalIntrari));
                tvIn.setTextColor(0xFF4DCEA8);
                tvIn.setTextSize(11);
                tvIn.setTypeface(null, android.graphics.Typeface.BOLD);
                layoutSume.addView(tvIn);
            }

            header.addView(layoutSume);

            TextView tvSageata = new TextView(getContext());
            tvSageata.setText("  ▲");
            tvSageata.setTextColor(0xFF7A9CC0);
            tvSageata.setTextSize(10);
            header.addView(tvSageata);

            grupZi.addView(header);

            LinearLayout containerZi = new LinearLayout(getContext());
            containerZi.setOrientation(LinearLayout.VERTICAL);
            containerZi.setVisibility(View.VISIBLE);
            grupZi.addView(containerZi);

            for (Tranzactie t : listaZi) {
                View item = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_tranzactie, containerZi, false);
                bindTranzactie(item, t, containerZi, rootView);
                containerZi.addView(item);
            }

            header.setOnClickListener(v -> {
                if (containerZi.getVisibility() == View.VISIBLE) {
                    containerZi.setVisibility(View.GONE);
                    tvSageata.setText("  ▼");
                    header.setBackground(creazaFundalHeaderZi(0xFF12203A));
                } else {
                    containerZi.setVisibility(View.VISIBLE);
                    tvSageata.setText("  ▲");
                    header.setBackground(creazaFundalHeaderZi(0xFF1A2C4A));
                }
            });

            container.addView(grupZi);
        }
    }

    private void bindTranzactie(View item, Tranzactie t,
                                LinearLayout container, View rootView) {
        TextView tvEmoji = item.findViewById(R.id.tvEmojiTranzactie);
        TextView tvCategorie = item.findViewById(R.id.tvCategorieTranzactie);
        TextView tvCard = item.findViewById(R.id.tvCardTranzactie);
        TextView tvSuma = item.findViewById(R.id.tvSumaTranzactie);
        TextView tvBadge = item.findViewById(R.id.tvBadgeTranzactie);
        TextView tvActiune = item.findViewById(R.id.tvActiuneTranzactie);

        tvEmoji.setText(t.getEmoji() != null && !t.getEmoji().isEmpty() ? t.getEmoji() : "•");

        String descriere = t.getDescriere();
        tvCategorie.setText(descriere != null && !descriere.isEmpty() ? descriere : t.getCategorie());
        tvCard.setText(t.getNumeBanca() + " •••• " + t.getUltimeleCifre());

        float suma = t.getSuma();
        if (suma >= 0) {
            tvSuma.setText(String.format("+%.0f RON", suma));
            tvSuma.setTextColor(0xFF4DCEA8);
        } else {
            tvSuma.setText(String.format("%.0f RON", suma));
            tvSuma.setTextColor(0xFFE05252);
        }

        if (t.getCategorie().equals("Venit")) {
            tvBadge.setVisibility(View.GONE);
            tvActiune.setVisibility(View.GONE);
        } else if (t.getCategorie().equals("Altele")) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText("Recategorizeaza");

            // "+ Seteaza abonament" nu are sens pe un transfer catre/de la un contact —
            // il aratam doar daca tranzactia NU e legata de un contact (contactId gol/null)
            boolean esteTransferContact = t.getContactId() != null && !t.getContactId().isEmpty();
            if (esteTransferContact) {
                tvActiune.setVisibility(View.GONE);
            } else {
                tvActiune.setVisibility(View.VISIBLE);
                tvActiune.setText("+ Seteaza abonament");
            }

            item.setOnClickListener(v -> {
                RecategorizeazaBottomSheet sheet = new RecategorizeazaBottomSheet();
                sheet.setTranzactie(t);
                sheet.setListener((categorie, emoji) -> {
                    t.setCategorie(categorie);
                    t.setEmoji(emoji);
                    setupTabToate(rootView);
                });
                sheet.show(getParentFragmentManager(), "Recategorizeaza");
            });

        } else {
            tvBadge.setVisibility(View.GONE);
            tvActiune.setVisibility(View.VISIBLE);
            tvActiune.setText("+ Seteaza abonament");
            item.setOnClickListener(v -> {
                AdaugaAbonamentBottomSheet sheet = new AdaugaAbonamentBottomSheet();
                sheet.setDatePrecompletate(t.getCategorie(), t.getEmoji());
                sheet.setListener(() -> {});
                sheet.show(getParentFragmentManager(), "AdaugaAbonament");
            });
        }
    }
}