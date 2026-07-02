package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PrimesteBaniBottomSheet extends BottomSheetDialogFragment {

    private ViewFlipper viewFlipper;
    private ContBancar contSelectat;
    private Bitmap qrBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_primeste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFlipper = view.findViewById(R.id.viewFlipperPrimeste);

        setupPas1(view);
        setupPas2(view);
    }

    // PAS 1 — alege contul curent
    private void setupPas1(View view) {
        // Filtram doar conturile curente
        List<ContBancar> toateConturile = ContBancarRepository.getInstance().getConturi();
        List<ContBancar> conturiCurente = new ArrayList<>();
        for (ContBancar c : toateConturile) {
            if (c.getTipCont() == ContBancar.TipCont.CURENT) {
                conturiCurente.add(c);
            }
        }

        if (!conturiCurente.isEmpty()) {
            contSelectat = conturiCurente.get(0);
        }

        RecyclerView rv = view.findViewById(R.id.rvCarduriPrimeste);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        CardSelectAdapter adapter = new CardSelectAdapter(conturiCurente, cont -> {
            contSelectat = cont;
        });
        rv.setAdapter(adapter);

        view.findViewById(R.id.btnContinuaPrimeste).setOnClickListener(v -> {
            if (contSelectat == null) {
                Toast.makeText(getContext(), "Alege un cont!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Actualizeaza subtitlul din pasul 2
            TextView tvSub = view.findViewById(R.id.tvSubtitluMetoda);
            tvSub.setText(contSelectat.getNumeBanca() + " · ···· " +
                    contSelectat.getIban().substring(contSelectat.getIban().length() - 4));
            viewFlipper.showNext();
        });

        view.findViewById(R.id.tvAnuleazaPrimeste).setOnClickListener(v -> dismiss());
    }

    // PAS 2 — alege metoda
    private void setupPas2(View view) {
        // IBAN — deschide IbanBottomSheet existent
        view.findViewById(R.id.optionIbanPrimeste).setOnClickListener(v -> {
            if (contSelectat == null) return;
            dismiss();
            IbanBottomSheet ibanSheet = new IbanBottomSheet();
            Bundle args = new Bundle();
            args.putString("iban", contSelectat.getIban());
            args.putString("titular", contSelectat.getTitular());
            args.putString("numeBanca", contSelectat.getNumeBanca());
            args.putString("sold", String.format("%.2f %s",
                    contSelectat.getSold(), contSelectat.getValuta()));
            args.putString("culoare", contSelectat.getCuloareBanca());
            ibanSheet.setArguments(args);
            ibanSheet.show(getParentFragmentManager(), "IbanSheet");
        });

        // QR — genereaza si trece la pasul 3
        view.findViewById(R.id.optionQRPrimeste).setOnClickListener(v -> {
            if (contSelectat == null) return;
            genereazaSiAfiseazaQr(view);
        });

        view.findViewById(R.id.tvInapoiPrimeste).setOnClickListener(v ->
                viewFlipper.showPrevious());
    }

    // Genereaza QR si trece la pasul 3
    private void genereazaSiAfiseazaQr(View view) {
        String continutQr = "IBAN:" + contSelectat.getIban() +
                ";TITULAR:" + contSelectat.getTitular() +
                ";BANCA:" + contSelectat.getNumeBanca();

        qrBitmap = genereazaQrBitmap(continutQr, 400);

        ImageView ivQr = view.findViewById(R.id.ivQrCode);
        ivQr.setImageBitmap(qrBitmap);

        TextView tvInfo = view.findViewById(R.id.tvQrInfo);
        tvInfo.setText(contSelectat.getNumeBanca() + " · ···· " +
                contSelectat.getIban().substring(contSelectat.getIban().length() - 4) +
                " · " + contSelectat.getTitular());

        // Buton salveaza QR
        Button btnSalveaza = view.findViewById(R.id.btnSalveazaQr);
        btnSalveaza.setOnClickListener(v -> salveazaQr());

        view.findViewById(R.id.tvInapoiQr).setOnClickListener(v ->
                viewFlipper.showPrevious());

        viewFlipper.showNext();
    }

    // Genereaza QR ca Bitmap simplu fara librarie externa
    private Bitmap genereazaQrBitmap(String continut, int dimensiune) {
        // Generam un QR simplu vizual bazat pe hash-ul continutului
        // In productie s-ar folosi ZXing sau similar
        Bitmap bitmap = Bitmap.createBitmap(dimensiune, dimensiune, Bitmap.Config.RGB_565);

        // Fundal alb
        for (int x = 0; x < dimensiune; x++) {
            for (int y = 0; y < dimensiune; y++) {
                bitmap.setPixel(x, y, Color.WHITE);
            }
        }

        int celula = dimensiune / 25;
        int hash = Math.abs(continut.hashCode());

        // Cele 3 colturi de pozitionare — obligatorii in orice QR
        desenPatratQr(bitmap, 0, 0, 7, celula);
        desenPatratQr(bitmap, 18, 0, 7, celula);
        desenPatratQr(bitmap, 0, 18, 7, celula);

        // Date pseudo-random bazate pe continut
        java.util.Random random = new java.util.Random(hash);
        for (int i = 8; i < 17; i++) {
            for (int j = 8; j < 17; j++) {
                if (random.nextBoolean()) {
                    int px = i * celula;
                    int py = j * celula;
                    for (int dx = 0; dx < celula; dx++) {
                        for (int dy = 0; dy < celula; dy++) {
                            if (px + dx < dimensiune && py + dy < dimensiune) {
                                bitmap.setPixel(px + dx, py + dy, Color.BLACK);
                            }
                        }
                    }
                }
            }
        }

        return bitmap;
    }

    private void desenPatratQr(Bitmap bitmap, int startX, int startY,
                               int marime, int celula) {
        for (int i = startX; i < startX + marime; i++) {
            for (int j = startY; j < startY + marime; j++) {
                boolean margine = (i == startX || i == startX + marime - 1 ||
                        j == startY || j == startY + marime - 1);
                boolean interior = (i >= startX + 2 && i <= startX + marime - 3 &&
                        j >= startY + 2 && j <= startY + marime - 3);
                int culoare = (margine || interior) ? Color.BLACK : Color.WHITE;
                int px = i * celula;
                int py = j * celula;
                for (int dx = 0; dx < celula; dx++) {
                    for (int dy = 0; dy < celula; dy++) {
                        if (px + dx < bitmap.getWidth() && py + dy < bitmap.getHeight()) {
                            bitmap.setPixel(px + dx, py + dy, culoare);
                        }
                    }
                }
            }
        }
    }

    // Salveaza QR in galerie
    private void salveazaQr() {
        if (qrBitmap == null || getContext() == null) return;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_FinMind_" +
                    System.currentTimeMillis() + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/FinMind");

            Uri uri = requireContext().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                OutputStream os = requireContext().getContentResolver()
                        .openOutputStream(uri);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                if (os != null) os.close();
                Toast.makeText(getContext(), "QR salvat in Galerie!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Eroare la salvare!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}