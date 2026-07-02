package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.Contact;
import com.stefi.licentamultibankingapp.model.ContactRepository;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.Tranzactie;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;

import java.util.ArrayList;
import java.util.List;

public class TrimiteBottomSheet extends BottomSheetDialogFragment {

    public interface OnTransferFacut {
        void onTransferFacut();
    }

    private OnTransferFacut callback;
    private ViewFlipper viewFlipper;
    private ContBancar contSelectat;
    private String destinatar = "";
    private String identificatorDestinatar = "";
    private final ContacteAdapter[] adapter2Ref = new ContacteAdapter[1];

    public void setCallback(OnTransferFacut callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_trimite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        viewFlipper = view.findViewById(R.id.viewSwitcherTrimite);

        setupPas1(view);
        setupPas2(view);
        setupPas3(view);
        setupPas4(view);
    }

    // PAS 1 — doar conturile CURENTE
    private void setupPas1(View view) {
        List<ContBancar> toateConturile = ContBancarRepository.getInstance().getConturi();
        List<ContBancar> conturiCurente = new ArrayList<>();
        for (ContBancar c : toateConturile) {
            if (c.getTipCont() == ContBancar.TipCont.CURENT && !c.isInghetat()) {
                conturiCurente.add(c);
            }
        }

        RecyclerView rv = view.findViewById(R.id.rvCarduriTrimite);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        CardSelectAdapter adapter = new CardSelectAdapter(conturiCurente, cont -> contSelectat = cont);
        rv.setAdapter(adapter);

        if (!conturiCurente.isEmpty()) {
            contSelectat = conturiCurente.get(0);
        }

        view.findViewById(R.id.btnContinuaPas1).setOnClickListener(v -> {
            if (contSelectat == null) {
                Toast.makeText(getContext(), "Alege un cont!", Toast.LENGTH_SHORT).show();
                return;
            }
            viewFlipper.showNext();
        });

        view.findViewById(R.id.tvAnuleazaPas1).setOnClickListener(v -> dismiss());
    }

    // PAS 2 — contacte scroll orizontal + optiuni
    private void setupPas2(View view) {
        List<Contact> contacte = ContactRepository.getInstance(requireContext()).getContacte();

        RecyclerView rv = view.findViewById(R.id.rvContacteTrimite);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        // Fara buton "+" — doar contactele existente
        ContacteAdapter adapter = new ContacteAdapter(
                contacte,
                null, // null = fara buton adauga in lista
                contact -> {
                    destinatar = contact.getNumeComplet();
                    identificatorDestinatar = contact.getTelefon().isEmpty()
                            ? contact.getIban() : contact.getTelefon();
                    treciLaPas3(view);
                }
        );
        adapter2Ref[0] = adapter;
        rv.setAdapter(adapter);

        // Numar nou — adaugi contactul si CONTINUA direct la suma
        view.findViewById(R.id.optionNrNou).setOnClickListener(v -> {
            AdaugaContactBottomSheet sheet = new AdaugaContactBottomSheet();
            sheet.setListener(() -> {
                List<Contact> contacteNoi = ContactRepository
                        .getInstance(requireContext()).getContacte();
                if (!contacteNoi.isEmpty()) {
                    Contact ultim = contacteNoi.get(contacteNoi.size() - 1);
                    destinatar = ultim.getNumeComplet();
                    identificatorDestinatar = ultim.getTelefon().isEmpty()
                            ? ultim.getIban() : ultim.getTelefon();
                    // Refresh lista si continua la pas 3
                    adapter2Ref[0].actualizeazaLista(contacteNoi);
                    treciLaPas3(view);
                }
            });
            sheet.show(getParentFragmentManager(), "AdaugaContactNou");
        });

        view.findViewById(R.id.optionIbanTrimite).setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Introdu IBAN destinatar");
            final android.widget.EditText etIban = new android.widget.EditText(requireContext());
            etIban.setHint("RO49 XXXX ...");
            etIban.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            etIban.setPadding(32, 24, 32, 24);
            builder.setView(etIban);
            builder.setPositiveButton("Continua", (dialog, which) -> {
                String iban = etIban.getText().toString().trim();
                if (iban.isEmpty()) {
                    Toast.makeText(getContext(), "Introdu IBAN-ul!", Toast.LENGTH_SHORT).show();
                    return;
                }
                destinatar = "Transfer bancar";
                identificatorDestinatar = iban;
                treciLaPas3(view);
            });
            builder.setNegativeButton("Anuleaza", null);
            builder.show();
        });

        view.findViewById(R.id.tvInapoiPas2).setOnClickListener(v ->
                viewFlipper.showPrevious());
    }

    // PAS 3 — suma + motiv
    private void treciLaPas3(View view) {
        TextView tvTitlu = view.findViewById(R.id.tvTitluDestinatarPas3);
        TextView tvSub = view.findViewById(R.id.tvSubtitluDestinatarPas3);
        tvTitlu.setText("Trimite catre " + destinatar);
        tvSub.setText(identificatorDestinatar);
        viewFlipper.showNext();
    }

    private void setupPas3(View view) {
        view.findViewById(R.id.btnContinuaPas3).setOnClickListener(v -> {
            EditText etSuma = view.findViewById(R.id.etSumaTrimite);
            String sumaStr = etSuma.getText().toString().trim();

            if (sumaStr.isEmpty()) {
                Toast.makeText(getContext(), "Introdu suma!", Toast.LENGTH_SHORT).show();
                return;
            }

            float suma;
            try {
                suma = Float.parseFloat(sumaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Suma invalida!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (suma <= 0) {
                Toast.makeText(getContext(), "Suma trebuie sa fie mai mare decat 0!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (contSelectat != null && suma > contSelectat.getSold()) {
                Toast.makeText(getContext(), "Sold insuficient!", Toast.LENGTH_SHORT).show();
                return;
            }

            TextView tvDest = view.findViewById(R.id.tvConfirmareDestinatarTrimite);
            TextView tvSumaConf = view.findViewById(R.id.tvConfirmareSumaTrimite);
            TextView tvCont = view.findViewById(R.id.tvConfirmareContTrimite);

            tvDest.setText(destinatar + " · " + identificatorDestinatar);
            tvSumaConf.setText(String.format("%.2f RON", suma));
            tvCont.setText(contSelectat.getNumeBanca() + " · ···· " +
                    contSelectat.getIban().substring(contSelectat.getIban().length() - 4));

            viewFlipper.showNext();
        });

        view.findViewById(R.id.tvInapoiPas3).setOnClickListener(v ->
                viewFlipper.showPrevious());
    }

    // PAS 4 — confirmare + Firestore + sold actualizat
    private void setupPas4(View view) {
        view.findViewById(R.id.btnConfirmaTrimite).setOnClickListener(v -> {
            EditText etSuma = view.findViewById(R.id.etSumaTrimite);
            float suma = Float.parseFloat(etSuma.getText().toString().trim());

            view.findViewById(R.id.layoutLoadingTrimite).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnConfirmaTrimite).setVisibility(View.GONE);
            view.findViewById(R.id.tvInapoiPas4).setVisibility(View.GONE);

            TextView tvStatus = view.findViewById(R.id.tvStatusTrimite);
            Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(() -> tvStatus.setText("Procesam transferul..."), 600);
            handler.postDelayed(() -> tvStatus.setText("Verificam contul..."), 1400);
            handler.postDelayed(() -> {
                Tranzactie t = new Tranzactie(
                        contSelectat.getNumeBanca(),
                        contSelectat.getIban().substring(contSelectat.getIban().length() - 4),
                        "Transfer",
                        "💸",
                        new java.util.Date(),
                        -suma
                );
                TranzactieRepository.getInstance().adaugaTranzactie(t);

                double soldNou = contSelectat.getSold() - suma;
                ContBancarRepository.getInstance()
                        .actualizeazaSold(contSelectat.getId(), soldNou);

                Toast.makeText(getContext(),
                        String.format("%.2f RON trimisi catre %s!", suma, destinatar),
                        Toast.LENGTH_SHORT).show();

                if (callback != null) callback.onTransferFacut();
                dismiss();
            }, 2200);
        });

        view.findViewById(R.id.tvInapoiPas4).setOnClickListener(v ->
                viewFlipper.showPrevious());
    }
}