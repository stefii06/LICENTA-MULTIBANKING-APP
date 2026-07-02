package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.AbonamentRepository;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.MockDataGenerator;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;
import com.stefi.licentamultibankingapp.ui.login.WelcomeActivity;
import com.stefi.licentamultibankingapp.utils.FirestoreManager;

public class ProfileFragment extends Fragment {

    private TextView tvAvatar, tvNumeComplet;
    private TextView tvNrConturi, tvScorProfil;
    private TextView tvPersonalitatEmoji, tvPersonalitateTitlu;
    private TextView tvPersonalitateScor, tvPersonalitateDescriere;
    private LinearLayout layoutSfaturi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        incarcaDateUtilizator();
        actualizeazaScorSiConturi();
        actualizeazaPersonalitate();
        setupClickuri(view);
    }

    private void initViews(View view) {
        tvAvatar             = view.findViewById(R.id.tvAvatar);
        tvNumeComplet        = view.findViewById(R.id.tvNumeComplet);
        tvNrConturi          = view.findViewById(R.id.tvNrConturi);
        tvScorProfil         = view.findViewById(R.id.tvScorProfil);
        tvPersonalitatEmoji  = view.findViewById(R.id.tvPersonalitatEmoji);
        tvPersonalitateTitlu = view.findViewById(R.id.tvPersonalitateTitlu);
        tvPersonalitateScor  = view.findViewById(R.id.tvPersonalitateScor);
        tvPersonalitateDescriere = view.findViewById(R.id.tvPersonalitateDescriere);
        layoutSfaturi        = view.findViewById(R.id.layoutSfaturi);
    }

    // Citeste numele din user_data_{userId} — fix multi-cont
    private void incarcaDateUtilizator() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_data_" + userId, Context.MODE_PRIVATE);

        String firstName = prefs.getString("firstName", "");
        String lastName  = prefs.getString("lastName", "");

        // Fallback la Firebase displayName daca SharedPreferences e gol
        String numeComplet = (firstName + " " + lastName).trim();
        if (numeComplet.isEmpty() && user.getDisplayName() != null) {
            numeComplet = user.getDisplayName();
        }
        if (numeComplet.isEmpty()) numeComplet = "Utilizator";

        tvNumeComplet.setText(numeComplet);

        // Initiale pentru avatar
        String initiale = "";
        if (!firstName.isEmpty()) initiale += firstName.charAt(0);
        if (!lastName.isEmpty())  initiale += lastName.charAt(0);
        if (initiale.isEmpty())   initiale = "U";
        tvAvatar.setText(initiale.toUpperCase());
    }

    private void actualizeazaScorSiConturi() {
        int nrConturi = ContBancarRepository.getInstance().getConturi().size();
        tvNrConturi.setText(String.valueOf(nrConturi));

        float[] venituri   = MockDataGenerator.getVenituriPerLuna();
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] economii   = MockDataGenerator.getEconomiiPerLuna();

        float rataEco = venituri[5] > 0 ? economii[5] / venituri[5] : 0;
        int scoreEco  = (int) Math.min(40, rataEco * 100);

        float medie = 0;
        for (float c : cheltuieli) medie += c;
        medie /= cheltuieli.length;
        float dev = 0;
        for (float c : cheltuieli) dev += Math.abs(c - medie);
        dev /= cheltuieli.length;
        int scoreStab  = (int) Math.min(25, 25 * (1 - dev / medie));
        int scoreBuget = cheltuieli[5] < venituri[5] ? 20 : 0;
        int scoreTrend = cheltuieli[5] < cheltuieli[4] ? 15 : 0;

        tvScorProfil.setText(String.valueOf(scoreEco + scoreStab + scoreBuget + scoreTrend));
    }

    private void actualizeazaPersonalitate() {
        float[] venituri   = MockDataGenerator.getVenituriPerLuna();
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] economii   = MockDataGenerator.getEconomiiPerLuna();
        float[] categorii  = MockDataGenerator.getCheltuieliPerCategorie(0);

        float rataEco = venituri[5] > 0 ? economii[5] / venituri[5] : 0;

        float maxCat = 0; int maxIdx = 0;
        for (int i = 0; i < categorii.length; i++) {
            if (categorii[i] > maxCat) { maxCat = categorii[i]; maxIdx = i; }
        }

        float medie = 0;
        for (float c : cheltuieli) medie += c;
        medie /= cheltuieli.length;
        float dev = 0;
        for (float c : cheltuieli) dev += Math.abs(c - medie);
        dev /= cheltuieli.length;
        float variabilitate = medie > 0 ? dev / medie : 0;

        float rataEco2 = venituri[5] > 0 ? economii[5] / venituri[5] : 0;
        int scoreEco   = (int) Math.min(40, rataEco2 * 100);
        int scoreBuget = cheltuieli[5] < venituri[5] ? 20 : 0;
        int scoreTrend = cheltuieli[5] < cheltuieli[4] ? 15 : 0;
        float medie2 = 0;
        for (float c : cheltuieli) medie2 += c;
        medie2 /= cheltuieli.length;
        float dev2 = 0;
        for (float c : cheltuieli) dev2 += Math.abs(c - medie2);
        dev2 /= cheltuieli.length;
        int scoreStab = (int) Math.min(25, 25 * (1 - dev2 / medie2));
        int scorTotal = scoreEco + scoreStab + scoreBuget + scoreTrend;

        String emoji, titlu, descriere;
        String[] sfaturi;
        boolean areDepozite = ContBancarRepository.getInstance().getDepozite().size() > 0;

        if (scorTotal >= 75 && rataEco >= 0.35f) {
            emoji = "🧙"; titlu = "The Finance Wizard";
            descriere = "Economisești în somn. Probabil ai și un spreadsheet pentru spreadsheet-urile tale. Felicitări, ești rara avis.";
            sfaturi = new String[]{"Investește surplusul în depozite cu dobândă", "Setează un fond de urgență de 3-6 luni", "Diversifică conturile de economii"};
        } else if (variabilitate > 0.15f && scorTotal < 50) {
            emoji = "🎢"; titlu = "The Rollercoaster";
            descriere = "Luna asta zero, luna viitoare erou. Bugetul tău e mai dramatic decât un serial turcesc. Respiră adânc.";
            sfaturi = new String[]{"Stabilește un buget fix pe categorii", "Evită cheltuielile impulsive", "Încearcă regula 24h înainte de orice cumpărătură mare"};
        } else if (rataEco < 0.10f && (maxIdx == 2 || maxIdx == 3)) {
            emoji = "🔥"; titlu = "The Yolo Spender";
            descriere = "Trăiești o singură dată, iar cardul tău știe asta prea bine. YOLO e un stil de viață, nu o scuză financiară.";
            sfaturi = new String[]{"Încearcă să lași măcar 10% în cont", "Setează o limită lunară pe Shopping", "Viitorul tău te va mulțumi"};
        } else if (rataEco >= 0.20f && rataEco < 0.35f && !areDepozite) {
            emoji = "🐿️"; titlu = "The Hoarder";
            descriere = "Economisești bine dar banii stau în cont fără să lucreze pentru tine. Ei sunt triști și plictisiți.";
            sfaturi = new String[]{"Pune economiile la muncă într-un depozit", "Chiar și 4% dobândă anuală face diferența", "Banii inactivi sunt bani care pierd valoare"};
        } else if (maxIdx == 0 && rataEco < 0.15f) {
            emoji = "☕"; titlu = "The Latte Factor";
            descriere = "Ai calculat că dacă nu mai bei cafea 47 de ani îți permiți o casă. Ai ales cafeaua. Respect total.";
            sfaturi = new String[]{"Micile cheltuieli zilnice se adună surprinzător", "Încearcă un cont de economii automat", "Chiar și 50 RON/lună pus deoparte contează"};
        } else {
            emoji = "⚖️"; titlu = "The Balanced One";
            descriere = "Nici prea mult, nici prea puțin. Ești echilibrul pe care toți îl caută dar puțini îl găsesc. Chapeau.";
            sfaturi = new String[]{"Ești pe drumul bun — un mic efort te duce spre Wizard", "Încearcă să crești economiile cu 5% luna viitoare", "Consistența e mai importantă decât suma"};
        }

        tvPersonalitatEmoji.setText(emoji);
        tvPersonalitateTitlu.setText(titlu);
        tvPersonalitateDescriere.setText(descriere);
        tvPersonalitateScor.setText("Scor " + scorTotal + " • " +
                (scorTotal >= 75 ? "Excelent" : scorTotal >= 50 ? "Bun" :
                        scorTotal >= 25 ? "OK" : "Atenție"));

        int culoare = scorTotal >= 75 ? Color.parseColor("#4CAF50") :
                scorTotal >= 50 ? Color.parseColor("#4A90D9") :
                        scorTotal >= 25 ? Color.parseColor("#FFB74D") :
                                Color.parseColor("#E57373");
        tvPersonalitateScor.setBackgroundColor(culoare);

        layoutSfaturi.removeAllViews();
        for (String sfat : sfaturi) {
            LinearLayout rand = new LinearLayout(requireContext());
            rand.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 8;
            rand.setLayoutParams(lp);

            TextView check = new TextView(requireContext());
            check.setText("✓ ");
            check.setTextColor(Color.parseColor("#4CAF50"));
            check.setTextSize(11f);

            TextView tvSfat = new TextView(requireContext());
            tvSfat.setText(sfat);
            tvSfat.setTextColor(Color.WHITE);
            tvSfat.setTextSize(11f);
            tvSfat.setLineSpacing(0, 1.3f);

            rand.addView(check);
            rand.addView(tvSfat);
            layoutSfaturi.addView(rand);
        }
    }

    private void setupClickuri(View view) {
        view.findViewById(R.id.btnDatePersonale).setOnClickListener(v -> showBottomSheetDatePersonale());
        view.findViewById(R.id.btnSecuritate).setOnClickListener(v -> showBottomSheetSecuritate());
        view.findViewById(R.id.btnDespreAplicatie).setOnClickListener(v -> showBottomSheetDespreAplicatie());
        view.findViewById(R.id.btnTermeni).setOnClickListener(v -> showDialogTermeni());

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Salvam userId INAINTE de signOut
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user != null ? user.getUid() : null;

            FirestoreManager.reset();
            ContBancarRepository.reset();
            TranzactieRepository.reset();
            AbonamentRepository.reset();
            FirebaseAuth.getInstance().signOut();


            Intent intent = new Intent(requireContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // Bottom sheet — Date personale (read-only)
    private void showBottomSheetDatePersonale() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_date_personale, null);
        dialog.setContentView(v);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("user_data_" + userId, Context.MODE_PRIVATE);

            ((TextView) v.findViewById(R.id.tvDpPrenume)).setText(
                    prefs.getString("firstName", "—"));
            ((TextView) v.findViewById(R.id.tvDpNume)).setText(
                    prefs.getString("lastName", "—"));
            ((TextView) v.findViewById(R.id.tvDpEmail)).setText(
                    user.getEmail() != null ? user.getEmail() : "—");
            ((TextView) v.findViewById(R.id.tvDpTelefon)).setText(
                    prefs.getString("phone", "—"));
        }

        dialog.show();
    }

    // Bottom sheet — Securitate (coming soon)
    private void showBottomSheetSecuritate() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_securitate, null);
        dialog.setContentView(v);

        v.findViewById(R.id.btnSchimbaParola).setOnClickListener(btn -> {
            dialog.dismiss();
            showBottomSheetSchimbaParola();
        });

        v.findViewById(R.id.btnSchimbaPin).setOnClickListener(btn -> {
            dialog.dismiss();
            showBottomSheetSchimbaPin();
        });

        dialog.show();
    }

    private void showBottomSheetSchimbaParola() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_schimba_parola, null);
        dialog.setContentView(v);

        com.google.android.material.textfield.TextInputEditText etCurenta =
                v.findViewById(R.id.etParolaCurenta);
        com.google.android.material.textfield.TextInputEditText etNoua =
                v.findViewById(R.id.etParolaNouа);
        com.google.android.material.textfield.TextInputEditText etConfirma =
                v.findViewById(R.id.etConfirmaParola);
        TextView tvEroare = v.findViewById(R.id.tvEroareParola);
        LinearLayout layoutFormular = v.findViewById(R.id.layoutFormularParola);
        LinearLayout layoutSucces = v.findViewById(R.id.layoutSuccesParola);

        v.findViewById(R.id.btnSalveazaParola).setOnClickListener(btn -> {
            String curenta  = etCurenta.getText().toString().trim();
            String noua     = etNoua.getText().toString().trim();
            String confirma = etConfirma.getText().toString().trim();

            // Validari de baza
            if (curenta.isEmpty() || noua.isEmpty() || confirma.isEmpty()) {
                tvEroare.setText("Completează toate câmpurile.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }
            if (noua.length() < 6) {
                tvEroare.setText("Parola nouă trebuie să aibă minim 6 caractere.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }
            if (!noua.equals(confirma)) {
                tvEroare.setText("Parolele noi nu coincid.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }

            tvEroare.setVisibility(View.GONE);

            // Reautentificare cu parola curenta — necesar pentru Firebase
            com.google.firebase.auth.FirebaseUser user =
                    com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.getEmail() == null) return;

            com.google.firebase.auth.AuthCredential credential =
                    com.google.firebase.auth.EmailAuthProvider
                            .getCredential(user.getEmail(), curenta);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Parola curenta corecta — schimbam parola
                        user.updatePassword(noua)
                                .addOnSuccessListener(aVoid2 -> {
                                    // Afisam ecranul de succes
                                    layoutFormular.setVisibility(View.GONE);
                                    layoutSucces.setVisibility(View.VISIBLE);
                                })
                                .addOnFailureListener(e -> {
                                    tvEroare.setText("Eroare la schimbarea parolei. Încearcă din nou.");
                                    tvEroare.setVisibility(View.VISIBLE);
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Parola curenta gresita
                        tvEroare.setText("Parola curentă este incorectă.");
                        tvEroare.setVisibility(View.VISIBLE);
                    });
        });

        dialog.show();
    }

    private void showBottomSheetSchimbaPin() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_schimba_pin, null);
        dialog.setContentView(v);

        // Campurile PIN curent
        android.widget.EditText[] pinCurent = {
                v.findViewById(R.id.pinCurent1),
                v.findViewById(R.id.pinCurent2),
                v.findViewById(R.id.pinCurent3),
                v.findViewById(R.id.pinCurent4),
                v.findViewById(R.id.pinCurent5),
                v.findViewById(R.id.pinCurent6)
        };

        // Campurile PIN nou
        android.widget.EditText[] pinNou = {
                v.findViewById(R.id.pinNou1),
                v.findViewById(R.id.pinNou2),
                v.findViewById(R.id.pinNou3),
                v.findViewById(R.id.pinNou4),
                v.findViewById(R.id.pinNou5),
                v.findViewById(R.id.pinNou6)
        };

        // Campurile confirmare PIN nou
        android.widget.EditText[] pinConfirm = {
                v.findViewById(R.id.pinConfirm1),
                v.findViewById(R.id.pinConfirm2),
                v.findViewById(R.id.pinConfirm3),
                v.findViewById(R.id.pinConfirm4),
                v.findViewById(R.id.pinConfirm5),
                v.findViewById(R.id.pinConfirm6)
        };

        TextView tvEroare = v.findViewById(R.id.tvEroarePin);
        LinearLayout layoutFormular = v.findViewById(R.id.layoutFormularPin);
        LinearLayout layoutSucces = v.findViewById(R.id.layoutSuccesPin);

        // Auto-focus intre campuri — acelasi comportament ca la SignUp
        setupPinAutoFocus(pinCurent);
        setupPinAutoFocus(pinNou);
        setupPinAutoFocus(pinConfirm);

        v.findViewById(R.id.btnSalveazaPin).setOnClickListener(btn -> {
            String curentVal  = getPinValue(pinCurent);
            String nouVal     = getPinValue(pinNou);
            String confirmVal = getPinValue(pinConfirm);

            if (curentVal.length() < 6 || nouVal.length() < 6 || confirmVal.length() < 6) {
                tvEroare.setText("Completează toate câmpurile PIN.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }

            // Verificam PIN-ul curent din SharedPreferences
            String userId = com.google.firebase.auth.FirebaseAuth.getInstance()
                    .getCurrentUser() != null
                    ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "";
            String pinSalvat = requireActivity()
                    .getSharedPreferences("finmind_prefs", android.content.Context.MODE_PRIVATE)
                    .getString("user_pin_" + userId, "");

            // Fallback la cheia veche daca nu exista per userId
            if (pinSalvat.isEmpty()) {
                pinSalvat = requireActivity()
                        .getSharedPreferences("finmind_prefs", android.content.Context.MODE_PRIVATE)
                        .getString("user_pin", "");
            }

            if (!curentVal.equals(pinSalvat)) {
                tvEroare.setText("PIN incorect. Mai încearcă.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }

            if (!nouVal.equals(confirmVal)) {
                tvEroare.setText("PIN-urile noi nu coincid.");
                tvEroare.setVisibility(View.VISIBLE);
                return;
            }

            // Salvam noul PIN per userId
            requireActivity()
                    .getSharedPreferences("finmind_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("user_pin_" + userId, nouVal)
                    .putString("user_pin", nouVal) // pastram si cheia veche pentru compatibilitate
                    .apply();

            layoutFormular.setVisibility(View.GONE);
            layoutSucces.setVisibility(View.VISIBLE);
        });

        dialog.show();
    }

    // Helper — auto-focus intre campuri PIN
    private void setupPinAutoFocus(android.widget.EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            final int index = i;
            fields[i].addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(android.text.Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < fields.length - 1) {
                        fields[index + 1].requestFocus();
                    }
                    if (s.length() == 0 && index > 0) {
                        fields[index - 1].requestFocus();
                    }
                }
            });
        }
    }

    // Helper — citeste valoarea din campurile PIN
    private String getPinValue(android.widget.EditText[] fields) {
        StringBuilder sb = new StringBuilder();
        for (android.widget.EditText f : fields) sb.append(f.getText().toString());
        return sb.toString();
    }

    // Bottom sheet — Despre aplicatie
    private void showBottomSheetDespreAplicatie() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_despre_aplicatie, null);
        dialog.setContentView(v);
        dialog.show();
    }

    // Dialog simplu — Termeni si conditii
    private void showDialogTermeni() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Termeni și condiții")
                .setMessage("FinMind este o aplicație realizată în scop educațional, " +
                        "ca proiect de licență (2025–2026).\n\n" +
                        "Datele financiare afișate sunt simulate și nu reprezintă " +
                        "tranzacții sau conturi bancare reale.\n\n" +
                        "Aplicația nu efectuează plăți reale și nu are acces la " +
                        "conturi bancare autentice.")
                .setPositiveButton("Am înțeles", null)
                .show();
    }
}