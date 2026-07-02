package com.stefi.licentamultibankingapp.model;

import com.stefi.licentamultibankingapp.utils.FirestoreManager;

import java.util.Calendar;
import java.util.List;

public class MockDataGenerator {

    public interface OnDateIncarcate {
        void onIncarcate();
    }

    // Nu mai genereaza date - doar asigura ca sunt incarcate din Firestore
    public static void genereazaDateMock() {
        // Gol intentionat - datele vin din Firestore prin SignInActivity
    }

    // Cheltuieli totale per categorie pentru o luna din datele reale
    public static float[] getCheltuieliPerCategorie(int lunaOffset) {
        List<Tranzactie> toate = TranzactieRepository.getInstance().getTranzactii();
        float[] rezultat = new float[6];

        String[] categorii = {"Mâncare", "Transport", "Shopping",
                "Divertisment", "Sănătate", "Utilități"};

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, lunaOffset);
        int lunaTarget = cal.get(Calendar.MONTH);
        int anTarget   = cal.get(Calendar.YEAR);

        for (Tranzactie t : toate) {
            if (t.getData() == null) continue;

            // Excludem intrarile (Venit) — nu sunt cheltuieli
            if ("Venit".equals(t.getCategorie())) continue;

            Calendar calT = Calendar.getInstance();
            calT.setTime(t.getData());

            if (calT.get(Calendar.MONTH) == lunaTarget &&
                    calT.get(Calendar.YEAR) == anTarget) {
                for (int i = 0; i < categorii.length; i++) {
                    if (categorii[i].equals(t.getCategorie())) {
                        // Folosim valoarea absoluta — sumele cheltuielilor sunt negative
                        rezultat[i] += Math.abs(t.getSuma());
                        break;
                    }
                }
            }
        }
        return rezultat;
    }

    // Total cheltuieli per luna — ultimele 6 luni
    public static float[] getTotalPerLuna() {
        float[] totaluri = new float[6];
        for (int i = 0; i < 6; i++) {
            float[] perCat = getCheltuieliPerCategorie(i - 5);
            for (float suma : perCat) {
                totaluri[i] += suma;
            }
        }
        return totaluri;
    }

    // Venituri per luna din memorie — incarcate din Firestore la login
    public static float[] venituriMemorie = {2800f, 3000f, 2900f, 3100f, 3050f, 3200f};

    public static float[] getVenituriPerLuna() {
        return venituriMemorie;
    }

    // Incarca veniturile din Firestore
    public static void incarcaVenituri(OnDateIncarcate callback) {
        FirestoreManager.getInstance().userDoc().get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        for (int i = 0; i < 6; i++) {
                            Long v = doc.getLong("venit_" + i);
                            if (v != null) venituriMemorie[i] = v.floatValue();
                        }
                    }
                    if (callback != null) callback.onIncarcate();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onIncarcate();
                });
    }

    // Economii per luna = venituri - cheltuieli
    public static float[] getEconomiiPerLuna() {
        float[] venituri   = getVenituriPerLuna();
        float[] cheltuieli = getTotalPerLuna();
        float[] economii   = new float[6];
        for (int i = 0; i < 6; i++) {
            economii[i] = venituri[i] - cheltuieli[i];
        }
        return economii;
    }
}