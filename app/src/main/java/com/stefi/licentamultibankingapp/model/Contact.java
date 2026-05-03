package com.stefi.licentamultibankingapp.model;

public class Contact {

    private String nume;
    private String prenume;
    private String iban;
    private String nota;

    public Contact(String nume, String prenume, String iban, String nota) {
        this.nume = nume;
        this.prenume = prenume;
        this.iban = iban;
        this.nota = nota;
    }

    public String getNume() { return nume; }
    public String getPrenume() { return prenume; }
    public String getIban() { return iban; }
    public String getNota() { return nota; }

    public String getInitiale() {
        String initialaNumе = nume.isEmpty() ? "" : String.valueOf(nume.charAt(0)).toUpperCase();
        String initialaPrenume = prenume.isEmpty() ? "" : String.valueOf(prenume.charAt(0)).toUpperCase();
        return initialaNumе + initialaPrenume;
    }

    public String getNumeComplet() {
        return nume + " " + prenume;
    }
}