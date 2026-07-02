package com.stefi.licentamultibankingapp.model;

public class ContBancar {

    public enum TipCont {
        CURENT, ECONOMII, DEPOZIT
    }

    private String numeBanca;
    private String iban;
    private double sold;
    private String valuta;
    private String culoareBanca;
    private String titular;
    private String tipCard;
    private boolean inghetat = false;
    private TipCont tipCont;
    private String numeCont;
    private String iconita;
    private double obiectiv;
    private double dobanda;
    private String dataTinta;


    private String id;

    // Constructor gol necesar pentru Firestore
    public ContBancar() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ContBancar(String numeBanca, String iban, double sold, String valuta,
                      String culoareBanca, String titular, String tipCard) {
        this.numeBanca = numeBanca;
        this.iban = iban;
        this.sold = sold;
        this.valuta = valuta;
        this.culoareBanca = culoareBanca;
        this.titular = titular;
        this.tipCard = tipCard;
        this.tipCont = TipCont.CURENT;
        this.numeCont = numeBanca + " " + tipCard;
        this.iconita = "💳";
    }

    public ContBancar(String numeBanca, String iban, double sold, String valuta,
                      String culoareBanca, String titular, String tipCard,
                      TipCont tipCont, String numeCont, String iconita,
                      double obiectiv, double dobanda, String dataTinta) {
        this.numeBanca = numeBanca;
        this.iban = iban;
        this.sold = sold;
        this.valuta = valuta;
        this.culoareBanca = culoareBanca;
        this.titular = titular;
        this.tipCard = tipCard;
        this.tipCont = tipCont;
        this.numeCont = numeCont;
        this.iconita = iconita;
        this.obiectiv = obiectiv;
        this.dobanda = dobanda;
        this.dataTinta = dataTinta;
    }

    public String getNumeBanca() { return numeBanca; }
    public String getIban() { return iban; }
    public double getSold() { return sold; }
    public void setSold(double sold) { this.sold = sold; }
    public String getValuta() { return valuta; }
    public String getCuloareBanca() { return culoareBanca; }
    public String getTitular() { return titular; }
    public String getTipCard() { return tipCard; }
    public boolean isInghetat() { return inghetat; }
    public void setInghetat(boolean inghetat) { this.inghetat = inghetat; }

    //getteri noi
    public TipCont getTipCont() { return tipCont; }
    public String getNumeCont() { return numeCont; }
    public String getIconita() { return iconita; }
    public double getObiectiv() { return obiectiv; }
    public double getDobanda() { return dobanda; }
    public String getDataTinta() { return dataTinta; }
}