package com.stefi.licentamultibankingapp.model;

import com.stefi.licentamultibankingapp.utils.FirestoreManager;
import java.util.ArrayList;
import java.util.List;

public class ContBancarRepository {

    private static ContBancarRepository instance;
    private List<ContBancar> conturi = new ArrayList<>();

    private ContBancarRepository() {}

    public static ContBancarRepository getInstance() {
        if (instance == null) {
            instance = new ContBancarRepository();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    // Incarca conturile din Firestore si apeleaza callback cand e gata
    public void incarcaConturi(OnIncarcat callback) {
        FirestoreManager.getInstance().conturi().get()
                .addOnSuccessListener(snapshot -> {
                    conturi.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
                        ContBancar cont = documentToContBancar(doc);
                        conturi.add(cont);
                    }
                    if (callback != null) callback.onIncarcat();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onIncarcat();
                });
    }

    // Returneaza datele deja incarcate in memorie
    public List<ContBancar> getConturi() {
        return conturi;
    }

    public List<ContBancar> getConturiCurente() {
        List<ContBancar> curente = new ArrayList<>();
        for (ContBancar cont : conturi) {
            if (cont.getTipCont() == ContBancar.TipCont.CURENT) {
                curente.add(cont);
            }
        }
        return curente;
    }

    public List<ContBancar> getConturiEconomii() {
        List<ContBancar> economii = new ArrayList<>();
        for (ContBancar cont : conturi) {
            if (cont.getTipCont() == ContBancar.TipCont.ECONOMII) {
                economii.add(cont);
            }
        }
        return economii;
    }

    public List<ContBancar> getDepozite() {
        List<ContBancar> depozite = new ArrayList<>();
        for (ContBancar cont : conturi) {
            if (cont.getTipCont() == ContBancar.TipCont.DEPOZIT) {
                depozite.add(cont);
            }
        }
        return depozite;
    }

    public void adaugaCont(ContBancar cont, Runnable callback) {
        java.util.Map<String, Object> map = contBancarToMap(cont);
        FirestoreManager.getInstance().conturi().add(map)
                .addOnSuccessListener(ref -> {
                    cont.setId(ref.getId());
                    conturi.add(cont);
                    if (callback != null) callback.run();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.run();
                });
    }

    public void stergeContDinFirestore(String id, Runnable callback) {
        FirestoreManager.getInstance().conturi().document(id).delete()
                .addOnSuccessListener(v -> {
                    conturi.removeIf(c -> id.equals(c.getId()));
                    if (callback != null) callback.run();
                });
    }

    public void actualizeazaSold(String id, double soldNou) {
        FirestoreManager.getInstance().conturi().document(id)
                .update("sold", soldNou);
        for (ContBancar c : conturi) {
            if (id.equals(c.getId())) {
                c.setSold(soldNou);
                break;
            }
        }
    }

    // Converteste DocumentSnapshot in ContBancar
    private ContBancar documentToContBancar(com.google.firebase.firestore.DocumentSnapshot doc) {
        String tipContStr = doc.getString("tipCont");
        ContBancar.TipCont tipCont = ContBancar.TipCont.CURENT;
        if ("ECONOMII".equals(tipContStr)) tipCont = ContBancar.TipCont.ECONOMII;
        else if ("DEPOZIT".equals(tipContStr)) tipCont = ContBancar.TipCont.DEPOZIT;

        Double sold = doc.getDouble("sold");
        Double obiectiv = doc.getDouble("obiectiv");
        Double dobanda = doc.getDouble("dobanda");

        ContBancar cont = new ContBancar(
                doc.getString("numeBanca"),
                doc.getString("iban"),
                sold != null ? sold : 0.0,
                doc.getString("valuta"),
                doc.getString("culoareBanca"),
                doc.getString("titular"),
                doc.getString("tipCard"),
                tipCont,
                doc.getString("numeCont"),
                doc.getString("iconita"),
                obiectiv != null ? obiectiv : 0.0,
                dobanda != null ? dobanda : 0.0,
                doc.getString("dataTinta")
        );
        cont.setId(doc.getId());

        Boolean inghetat = doc.getBoolean("inghetat");
        if (inghetat != null) cont.setInghetat(inghetat);

        return cont;
    }

    // Converteste ContBancar in Map pentru Firestore
    private java.util.Map<String, Object> contBancarToMap(ContBancar cont) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("numeBanca", cont.getNumeBanca());
        map.put("iban", cont.getIban());
        map.put("sold", cont.getSold());
        map.put("valuta", cont.getValuta());
        map.put("culoareBanca", cont.getCuloareBanca());
        map.put("titular", cont.getTitular());
        map.put("tipCard", cont.getTipCard());
        map.put("tipCont", cont.getTipCont().name());
        map.put("numeCont", cont.getNumeCont());
        map.put("iconita", cont.getIconita());
        map.put("obiectiv", cont.getObiectiv());
        map.put("dobanda", cont.getDobanda());
        map.put("dataTinta", cont.getDataTinta());
        map.put("inghetat", cont.isInghetat());
        return map;
    }

    public interface OnIncarcat {
        void onIncarcat();
    }
}