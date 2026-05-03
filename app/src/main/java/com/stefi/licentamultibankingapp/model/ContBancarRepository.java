package com.stefi.licentamultibankingapp.model;

import java.util.ArrayList;
import java.util.List;

public class ContBancarRepository {

    private static ContBancarRepository instance;
    private List<ContBancar> conturi = new ArrayList<>();

    private ContBancarRepository() {
        // Date mock initiale
        conturi.add(new ContBancar("BCR", "RO49AAAA1B31007593840000", 5230.00, "RON", "#E53935", "Ioana Stefania", "Visa"));
        conturi.add(new ContBancar("ING", "RO49INGB1B31007593840001", 120.00, "EUR", "#FF6D00", "Ioana Stefania", "Mastercard"));
    }

    public static ContBancarRepository getInstance() {
        if (instance == null) {
            instance = new ContBancarRepository();
        }
        return instance;
    }

    public List<ContBancar> getConturi() {
        return conturi;
    }

    public void adaugaCont(ContBancar cont) {
        conturi.add(cont);
    }
}