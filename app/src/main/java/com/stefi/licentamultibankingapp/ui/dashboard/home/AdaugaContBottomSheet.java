package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.utils.FirestoreManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AdaugaContBottomSheet extends BottomSheetDialogFragment {

    // Callback pentru a anunta HomeFragment ca s-au adaugat conturi noi
    public interface OnConturiAdaugate {
        void onConturiAdaugate();
    }

    private OnConturiAdaugate callback;
    private ViewSwitcher viewSwitcher;
    private String bancaSelectata;
    private String culoareBanca;

    public void setCallback(OnConturiAdaugate callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_adauga_cont, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Previne freeze-ul cand apare tastatura
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        viewSwitcher = view.findViewById(R.id.viewSwitcher);

        // Pasul 1 — click pe banca
        view.findViewById(R.id.optionBCR).setOnClickListener(v ->
                treciLaPasul2("BCR", "#E53935"));
        view.findViewById(R.id.optionBRD).setOnClickListener(v ->
                treciLaPasul2("BRD", "#1565C0"));
        view.findViewById(R.id.optionING).setOnClickListener(v ->
                treciLaPasul2("ING", "#FF6D00"));
        view.findViewById(R.id.optionRaiffeisen).setOnClickListener(v ->
                treciLaPasul2("Raiffeisen", "#FFD600"));
        view.findViewById(R.id.optionAnuleazaCont).setOnClickListener(v -> dismiss());

        // Pasul 2 — butoane
        view.findViewById(R.id.btnConecteaza).setOnClickListener(v -> conecteaza(view));
        view.findViewById(R.id.tvInapoi).setOnClickListener(v ->
                viewSwitcher.showPrevious());
    }

    // Trece la pasul 2 si actualizeaza header-ul cu banca aleasa
    private void treciLaPasul2(String numeBanca, String culoare) {
        bancaSelectata = numeBanca;
        culoareBanca = culoare;

        View view = getView();
        if (view == null) return;

        TextView tvLogo = view.findViewById(R.id.tvLogoBanca);
        TextView tvTitlu = view.findViewById(R.id.tvTitluAutentificare);

        tvLogo.setText(numeBanca.length() > 3 ? numeBanca.substring(0, 3).toUpperCase() : numeBanca.toUpperCase());
        tvLogo.setBackgroundColor(Color.parseColor(culoare));
        tvTitlu.setText("Conectare " + numeBanca);

        viewSwitcher.showNext();
    }

    // Logica de conectare simulata
    private void conecteaza(View view) {
        EditText etUtilizator = view.findViewById(R.id.etUtilizator);
        EditText etParola = view.findViewById(R.id.etParola);

        String utilizator = etUtilizator.getText().toString().trim();
        String parola = etParola.getText().toString().trim();

        if (utilizator.isEmpty() || parola.isEmpty()) {
            Toast.makeText(getContext(), "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
            return;
        }

        // Arata loading, ascunde buton
        view.findViewById(R.id.layoutLoading).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btnConecteaza).setVisibility(View.GONE);
        view.findViewById(R.id.tvInapoi).setVisibility(View.GONE);

        TextView tvStatus = view.findViewById(R.id.tvStatusConectare);

        // Simulare etape conectare cu Handler
        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(() -> tvStatus.setText("Verificăm identitatea..."), 600);
        handler.postDelayed(() -> tvStatus.setText("Importăm conturile..."), 1400);
        handler.postDelayed(() -> tvStatus.setText("Salvăm în siguranță..."), 2200);
        handler.postDelayed(() -> {
            // Genereaza si salveaza conturile
            List<ContBancar> conturiNoi = genereazaConturi(bancaSelectata, culoareBanca);
            salveazaInFirestore(conturiNoi);
        }, 3000);
    }

    // Genereaza random 1-3 conturi pentru banca aleasa
    private List<ContBancar> genereazaConturi(String banca, String culoare) {
        List<ContBancar> lista = new ArrayList<>();
        Random random = new Random();

        // Intotdeauna generam cont curent
        lista.add(creeazaCont(banca, culoare, "Cont curent", "RON",
                2000 + random.nextInt(4000)));

        // 60% sansa de cont economii
        if (random.nextFloat() < 0.6f) {
            lista.add(creeazaCont(banca, culoare, "Economii", "RON",
                    1000 + random.nextInt(9000)));
        }

        // 40% sansa de depozit
        if (random.nextFloat() < 0.4f) {
            lista.add(creeazaCont(banca, culoare, "Depozit 12 luni", "RON",
                    5000 + random.nextInt(15000)));
        }

        return lista;
    }

    // Construieste un obiect ContBancar
    private ContBancar creeazaCont(String banca, String culoare, String tip,
                                   String valuta, double sold) {
        ContBancar.TipCont tipCont;
        double dobanda = 0.0;
        double obiectiv = 0.0;
        String dataTinta = null;

        switch (tip) {
            case "Economii":
                tipCont = ContBancar.TipCont.ECONOMII;
                dobanda = getDobandaEconomii(banca);
                obiectiv = 10000 + new Random().nextInt(20000);
                dataTinta = genereazaDataTinta(24);
                break;
            case "Depozit 12 luni":
                tipCont = ContBancar.TipCont.DEPOZIT;
                dobanda = 5.5 + new Random().nextInt(2);
                obiectiv = sold + (sold * dobanda / 100);
                dataTinta = genereazaDataTinta(12);
                break;
            default:
                tipCont = ContBancar.TipCont.CURENT;
                break;
        }

        return new ContBancar(
                banca,
                genereazaIban(banca),
                sold,
                valuta,
                culoare,
                getTitularDinPrefs(),
                "Visa",
                tipCont,
                tip,
                "💳",
                obiectiv,
                dobanda,
                dataTinta
        );
    }

    // Dobanda per banca pentru economii
    private double getDobandaEconomii(String banca) {
        switch (banca) {
            case "BCR":        return 4.0;
            case "ING":        return 3.5;
            case "BRD":        return 3.8;
            case "Raiffeisen": return 4.2;
            default:           return 3.5;
        }
    }

    // Genereaza data tinta peste N luni
    private String genereazaDataTinta(int luni) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, luni);
        return new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(cal.getTime());
    }

    // Genereaza un IBAN realist pentru fiecare banca
    private String genereazaIban(String banca) {
        String prefix;
        switch (banca) {
            case "BCR":     prefix = "RNCB"; break;
            case "BRD":     prefix = "BRDE"; break;
            case "ING":     prefix = "INGB"; break;
            case "Raiffeisen": prefix = "RZBR"; break;
            default:        prefix = "BANK"; break;
        }
        int numar = 1000 + new Random().nextInt(8999);
        return "RO49 " + prefix + " 0000 0000 0000 " + numar;
    }

    // Citeste numele titularului din SharedPreferences
    private String getTitularDinPrefs() {
        if (getContext() == null) return "Utilizator";
        android.content.SharedPreferences prefs = getContext()
                .getSharedPreferences("finmind_prefs", android.content.Context.MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "");
        String lastName = prefs.getString("lastName", "");
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        return "Utilizator";
    }

    // Salveaza fiecare cont in Firestore si notifica HomeFragment
    private void salveazaInFirestore(List<ContBancar> conturi) {
        ContBancarRepository repo = ContBancarRepository.getInstance();
        int[] salvate = {0};

        for (ContBancar cont : conturi) {
            repo.adaugaCont(cont, () -> {
                salvate[0]++;
                if (salvate[0] == conturi.size()) {
                    // Toate salvate — notificam si inchidem
                    if (callback != null) callback.onConturiAdaugate();

                    Toast.makeText(getContext(),
                            conturi.size() + " conturi conectate cu succes!",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        }
    }
}