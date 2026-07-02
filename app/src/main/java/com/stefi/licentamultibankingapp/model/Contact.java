package com.stefi.licentamultibankingapp.model;

public class Contact {

    private String nume;
    private String prenume;
    private String iban;
    private String nota;
    private String telefon;
    private String id;

    public Contact() {}

    public Contact(String nume, String prenume, String iban, String nota) {
        this.nume = nume;
        this.prenume = prenume;
        this.iban = iban;
        this.nota = nota;
        this.telefon = "";
    }

    public Contact(String nume, String prenume, String iban, String nota, String telefon) {
        this.nume = nume;
        this.prenume = prenume;
        this.iban = iban;
        this.nota = nota;
        this.telefon = telefon;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNume() { return nume; }
    public String getPrenume() { return prenume; }
    public String getIban() { return iban != null ? iban : ""; }
    public String getNota() { return nota != null ? nota : ""; }
    public String getTelefon() { return telefon != null ? telefon : ""; }

    public String getInitiale() {
        String initialaNume = nume == null || nume.isEmpty() ? "" :
                String.valueOf(nume.charAt(0)).toUpperCase();
        String initialaPrenume = prenume == null || prenume.isEmpty() ? "" :
                String.valueOf(prenume.charAt(0)).toUpperCase();
        return initialaNume + initialaPrenume;
    }

    public String getNumeComplet() {
        return (nume != null ? nume : "") + " " + (prenume != null ? prenume : "");
    }
}