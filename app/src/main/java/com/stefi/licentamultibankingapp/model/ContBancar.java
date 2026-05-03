package com.stefi.licentamultibankingapp.model;

public class ContBancar {

    private String numeBanca;
    private String iban;
    private double sold;
    private String valuta;
    private String culoareBanca;
    private String titular;
    private String tipCard;
    private boolean inghetat = false;

    public ContBancar(String numeBanca, String iban, double sold, String valuta, String culoareBanca, String titular, String tipCard) {
        this.numeBanca = numeBanca;
        this.iban = iban;
        this.sold = sold;
        this.valuta = valuta;
        this.culoareBanca = culoareBanca;
        this.titular = titular;
        this.tipCard = tipCard;
    }

    public String getNumeBanca() { return numeBanca; }
    public String getIban() { return iban; }
    public double getSold() { return sold; }
    public String getValuta() { return valuta; }
    public String getCuloareBanca() { return culoareBanca; }
    public String getTitular() { return titular; }
    public String getTipCard() { return tipCard; }

    public boolean isInghetat() { return inghetat; }
    public void setInghetat(boolean inghetat) { this.inghetat = inghetat; }

}