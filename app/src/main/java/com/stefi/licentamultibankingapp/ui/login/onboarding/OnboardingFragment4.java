package com.stefi.licentamultibankingapp.ui.login.onboarding;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;
import com.stefi.licentamultibankingapp.utils.FirestoreManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OnboardingFragment4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_fragment4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnboardingActivity activity = (OnboardingActivity) getActivity();
        String banca = activity.getBancaSelectata();
        String culoare = activity.getCuloareBanca();

        String titular = genereazaTitular();
        double sold = genereazaSold(banca);
        String iban = genereazaIban(banca);
        String nrCard = genereazaNrCard(banca);

        // Afisam datele in UI
        TextView tvBanca = view.findViewById(R.id.tvBancaConfirmare);
        tvBanca.setText(banca + " • cont curent");

        TextView tvTitular = view.findViewById(R.id.tvTitularConfirmare);
        tvTitular.setText(titular);

        TextView tvNumeBanca = view.findViewById(R.id.tvNumeBancaConfirmare);
        tvNumeBanca.setText(banca);

        TextView tvSold = view.findViewById(R.id.tvSoldConfirmare);
        tvSold.setText(String.format("%.2f RON", sold));

        Button btnMergi = view.findViewById(R.id.btnMergiLaFinMind);
        btnMergi.setOnClickListener(v -> {
            // Salvam contul in Firestore
            salveazaContInFirestore(banca, iban, sold, culoare, titular, nrCard);

            // Generam tranzactii demo pentru acest cont
            genereazaTranzactiiDemo(banca);

            // Salvam veniturile in Firestore
            salveazaVenituri();

            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).onboardingFinalizat();
            }
        });
    }

    private void salveazaContInFirestore(String banca, String iban, double sold,
                                         String culoare, String titular, String nrCard) {
        Map<String, Object> cont = new HashMap<>();
        cont.put("numeBanca", banca);
        cont.put("iban", iban);
        cont.put("sold", sold);
        cont.put("valuta", "RON");
        cont.put("culoareBanca", culoare);
        cont.put("titular", titular);
        cont.put("tipCard", "Visa");
        cont.put("tipCont", "CURENT");
        cont.put("numeCont", banca + " Visa");
        cont.put("iconita", "💳");
        cont.put("inghetat", false);
        cont.put("obiectiv", 0.0);
        cont.put("dobanda", 0.0);
        cont.put("dataTinta", "");
        cont.put("nrCard", nrCard);
        cont.put("cvv", String.valueOf(new Random().nextInt(900) + 100));
        cont.put("expirare", "05/28");

        FirestoreManager.getInstance().conturi().add(cont)
                .addOnSuccessListener(ref -> {
                    // Adaugam si in repository local
                    ContBancar contLocal = new ContBancar(
                            banca, iban, sold, "RON", culoare, titular, "Visa");
                    contLocal.setId(ref.getId());
                    ContBancarRepository.getInstance().getConturi().add(contLocal);
                });
    }

    // -----------------------------------------------------------------------
    // Structura unui comerciant: nume, emoji, categorie, suma minima, suma maxima
    // -----------------------------------------------------------------------
    private static class Comerciant {
        String nume;
        String emoji;
        String categorie;
        int sumaMin;
        int sumaMax;

        Comerciant(String nume, String emoji, String categorie, int sumaMin, int sumaMax) {
            this.nume = nume;
            this.emoji = emoji;
            this.categorie = categorie;
            this.sumaMin = sumaMin;
            this.sumaMax = sumaMax;
        }
    }

    private void genereazaTranzactiiDemo(String banca) {
        Random random = new Random();

        // Lista comerciantilor per categorie, cu sume realiste
        Comerciant[] mancare = {
                new Comerciant("Lidl", "🍔", "Mâncare", 25, 120),
                new Comerciant("Kaufland", "🍔", "Mâncare", 30, 150),
                new Comerciant("Mega Image", "🍔", "Mâncare", 15, 80),
                new Comerciant("McDonald's", "🍔", "Mâncare", 25, 55),
                new Comerciant("KFC", "🍔", "Mâncare", 30, 60),
                new Comerciant("Glovo", "🍔", "Mâncare", 35, 80)
        };

        Comerciant[] transport = {
                new Comerciant("Uber", "🚗", "Transport", 12, 45),
                new Comerciant("Bolt", "🚗", "Transport", 10, 40),
                new Comerciant("OMV", "🚗", "Transport", 150, 350),
                new Comerciant("Rompetrol", "🚗", "Transport", 150, 320),
                new Comerciant("CFR Calatori", "🚗", "Transport", 30, 120)
        };

        Comerciant[] shopping = {
                new Comerciant("eMAG", "🛍️", "Shopping", 50, 600),
                new Comerciant("Zara", "🛍️", "Shopping", 80, 400),
                new Comerciant("H&M", "🛍️", "Shopping", 60, 250),
                new Comerciant("Decathlon", "🛍️", "Shopping", 50, 300),
                new Comerciant("Pepco", "🛍️", "Shopping", 20, 80)
        };

        Comerciant[] divertisment = {
                new Comerciant("Netflix", "🎬", "Divertisment", 45, 45),   // fix lunar
                new Comerciant("Spotify", "🎬", "Divertisment", 25, 25),   // fix lunar
                new Comerciant("HBO Max", "🎬", "Divertisment", 35, 35),   // fix lunar
                new Comerciant("Cinema City", "🎬", "Divertisment", 30, 60),
                new Comerciant("Steam", "🎬", "Divertisment", 20, 150)
        };

        Comerciant[] sanatate = {
                new Comerciant("Catena", "💊", "Sănătate", 20, 80),
                new Comerciant("Sensiblu", "💊", "Sănătate", 15, 70),
                new Comerciant("Regina Maria", "💊", "Sănătate", 80, 200),
                new Comerciant("MedLife", "💊", "Sănătate", 100, 250)
        };

        Comerciant[] utilitati = {
                new Comerciant("Enel", "💡", "Utilități", 80, 250),
                new Comerciant("Digi", "💡", "Utilități", 40, 40),         // fix lunar
                new Comerciant("Orange", "💡", "Utilități", 50, 80),
                new Comerciant("E.ON", "💡", "Utilități", 60, 180)
        };

        // Intrari: bani primiti
        Comerciant[] intrari = {
                new Comerciant("Salariu", "💸", "Venit", 3000, 3500),
                new Comerciant("Transfer Revolut", "💸", "Venit", 50, 300),
                new Comerciant("Cashback ING", "💸", "Venit", 10, 50),
                new Comerciant("Rambursare", "💸", "Venit", 50, 150)
        };

        // Grupam toate categoriile de cheltuieli intr-un array pentru a itera usor
        Comerciant[][] toateCategoriileCheltuieli = {
                mancare, transport, shopping, divertisment, sanatate, utilitati
        };

        // Generam tranzactii pentru ultimele 6 luni
        for (int luna = 0; luna < 6; luna++) {

            // Generam 2 tranzactii de cheltuiala per categorie per luna
            for (Comerciant[] categorie : toateCategoriileCheltuieli) {
                for (int t = 0; t < 2; t++) {
                    // Alegem un comerciant random din categorie
                    Comerciant comerciant = categorie[random.nextInt(categorie.length)];

                    // Suma random in intervalul comerciantului
                    float suma = comerciant.sumaMin +
                            random.nextInt(comerciant.sumaMax - comerciant.sumaMin + 1);

                    // Data random in luna respectiva
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, luna - 5);
                    cal.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(27));

                    // Suma negativa pentru cheltuieli
                    salveazaTranzactie(banca, comerciant.nume, comerciant.emoji,
                            comerciant.categorie, -suma, cal);
                }
            }

            // Generam 1-2 intrari per luna (salariu + optional altceva)
            // Salariul apare fix in fiecare luna
            Comerciant salariu = intrari[0];
            float sumaSalariu = salariu.sumaMin +
                    random.nextInt(salariu.sumaMax - salariu.sumaMin + 1);
            Calendar calSalariu = Calendar.getInstance();
            calSalariu.add(Calendar.MONTH, luna - 5);
            calSalariu.set(Calendar.DAY_OF_MONTH, 10); // salariul vine pe 10

            salveazaTranzactie(banca, salariu.nume, salariu.emoji,
                    salariu.categorie, sumaSalariu, calSalariu);

            // 50% sansa de o intrare extra (transfer, cashback etc.)
            if (random.nextBoolean()) {
                Comerciant intraraExtra = intrari[1 + random.nextInt(intrari.length - 1)];
                float sumaExtra = intraraExtra.sumaMin +
                        random.nextInt(intraraExtra.sumaMax - intraraExtra.sumaMin + 1);
                Calendar calExtra = Calendar.getInstance();
                calExtra.add(Calendar.MONTH, luna - 5);
                calExtra.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(27));

                salveazaTranzactie(banca, intraraExtra.nume, intraraExtra.emoji,
                        intraraExtra.categorie, sumaExtra, calExtra);
            }
        }
    }

    // Metoda helper: salveaza o tranzactie in Firestore si local
    private void salveazaTranzactie(String banca, String nume, String emoji,
                                    String categorie, float suma, Calendar cal) {
        Map<String, Object> tranzactie = new HashMap<>();
        tranzactie.put("numeBanca", banca);
        tranzactie.put("ultimeleCifre", "4119");
        tranzactie.put("descriere", nume);
        tranzactie.put("categorie", categorie);
        tranzactie.put("emoji", emoji);
        tranzactie.put("suma", suma);
        tranzactie.put("data", cal.getTime());

        FirestoreManager.getInstance().tranzactii().add(tranzactie)
                .addOnSuccessListener(ref -> {
                    Tranzactie t = new Tranzactie(
                            banca, "4119",
                            categorie,
                            emoji,
                            cal.getTime(),
                            suma);
                    t.setId(ref.getId());
                    TranzactieRepository.getInstance().getTranzactii().add(t);
                });
    }

    private void salveazaVenituri() {
        // Citim numele din SharedPreferences local, la fel ca in genereazaTitular()
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_data_" + userId, android.content.Context.MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "");
        String lastName = prefs.getString("lastName", "");
        String phone = prefs.getString("phone", "");

        Map<String, Object> date = new HashMap<>();
        date.put("firstName", firstName);
        date.put("lastName", lastName);
        date.put("phone", phone);
        date.put("venit_0", 2800);
        date.put("venit_1", 3000);
        date.put("venit_2", 2900);
        date.put("venit_3", 3100);
        date.put("venit_4", 3050);
        date.put("venit_5", 3200);

        // Salvam numele si veniturile intr-un singur set(), ca sa nu se suprascrie unul pe altul
        FirestoreManager.getInstance().userDoc().set(date);
    }

    private double genereazaSold(String banca) {
        switch (banca) {
            case "BCR": return 2450.75;
            case "ING": return 1830.20;
            case "BRD": return 3200.00;
            case "Raiffeisen": return 980.50;
            case "Banca Transilvania": return 5100.30;
            case "CEC Bank": return 1250.00;
            default: return 1000.00;
        }
    }

    private String genereazaIban(String banca) {
        Random r = new Random();
        String cifre = "";
        for (int i = 0; i < 16; i++) cifre += r.nextInt(10);
        switch (banca) {
            case "BCR": return "RO49RNCB" + cifre;
            case "ING": return "RO15INGB" + cifre;
            case "BRD": return "RO98BRDE" + cifre;
            case "Raiffeisen": return "RO12RZBR" + cifre;
            case "Banca Transilvania": return "RO07BTRL" + cifre;
            case "CEC Bank": return "RO19CECE" + cifre;
            default: return "RO00XXXX" + cifre;
        }
    }

    private String genereazaNrCard(String banca) {
        Random r = new Random();
        String cifre = "";
        for (int i = 0; i < 15; i++) cifre += r.nextInt(10);
        return "4" + cifre;
    }

    private String genereazaTitular() {
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_data_" + userId, android.content.Context.MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "");
        String lastName  = prefs.getString("lastName", "");

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        return "Utilizator FinMind";
    }
}