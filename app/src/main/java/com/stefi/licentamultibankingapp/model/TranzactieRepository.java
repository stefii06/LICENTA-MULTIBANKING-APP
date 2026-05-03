package com.stefi.licentamultibankingapp.model;

import java.util.ArrayList;
import java.util.List;

public class TranzactieRepository {

    private static TranzactieRepository instance;
    private List<Tranzactie> tranzactii = new ArrayList<>();

    private TranzactieRepository() {}

    public static TranzactieRepository getInstance() {
        if (instance == null) {
            instance = new TranzactieRepository();
        }
        return instance;
    }

    public List<Tranzactie> getTranzactii() {
        return tranzactii;
    }

    public void adaugaTranzactie(Tranzactie tranzactie) {
        tranzactii.add(0, tranzactie); // adaugam la inceput, cel mai recent primul
    }
}