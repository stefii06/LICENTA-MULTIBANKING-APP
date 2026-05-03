package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.ContactRepository;


import com.stefi.licentamultibankingapp.model.ContBancar;
import java.util.List;

public class HomeFragment extends Fragment {

    private boolean cardVisible = false;
    private View layoutCardRevelat;
    private View layoutSold;
    private ContacteAdapter contacteAdapter;
    private ContactRepository contactRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactRepository = ContactRepository.getInstance(getContext());

        layoutCardRevelat = view.findViewById(R.id.layoutCardRevelat);
        layoutSold = view.findViewById(R.id.layoutSold);

        // Card peek click
        view.findViewById(R.id.layoutCardPeek).setOnClickListener(v -> {
            if (!cardVisible) {
                layoutSold.setVisibility(View.GONE);
                layoutCardRevelat.setVisibility(View.VISIBLE);
                cardVisible = true;
            } else {
                layoutCardRevelat.setVisibility(View.GONE);
                layoutSold.setVisibility(View.VISIBLE);
                cardVisible = false;
            }
        });

        // Butoane contextuale card
        view.findViewById(R.id.btnDetaliuCard).setOnClickListener(v -> {
            DetaliiCardBottomSheet bottomSheet = new DetaliiCardBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "DetaliiCard");
        });

        view.findViewById(R.id.btnIngheataCard).setOnClickListener(v -> {
            IngheataCardBottomSheet bottomSheet = new IngheataCardBottomSheet();
            bottomSheet.setListener(inghetat -> {
                actualizareCardRevelat(view);
            });
            bottomSheet.show(getParentFragmentManager(), "IngheataCard");
        });

        view.findViewById(R.id.btnStergeCard).setOnClickListener(v -> {
            StergeCardBottomSheet bottomSheet = new StergeCardBottomSheet();
            bottomSheet.setListener(() -> {
                layoutCardRevelat.setVisibility(View.GONE);
                layoutSold.setVisibility(View.VISIBLE);
                cardVisible = false;
            });
            bottomSheet.show(getParentFragmentManager(), "StergeCard");
        });

        // Butoane rapide
        view.findViewById(R.id.btnTrimite).setOnClickListener(v -> {
            TrimiteBottomSheet bottomSheet = new TrimiteBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "TrimiteBottomSheet");
        });

        view.findViewById(R.id.btnPrimeste).setOnClickListener(v -> {
            PrimesteBaniBottomSheet bottomSheet = new PrimesteBaniBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "PrimesteBaniBottomSheet");
        });

        view.findViewById(R.id.btnPlateste).setOnClickListener(v -> {
            PlatesteBottomSheet bottomSheet = new PlatesteBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "PlatesteBottomSheet");
        });

        view.findViewById(R.id.btnAdaugaCont).setOnClickListener(v -> {
            AdaugaContBottomSheet bottomSheet = new AdaugaContBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "AdaugaContBottomSheet");
        });

        // Banner tranzactii
        view.findViewById(R.id.layoutBannerTranzactii).setOnClickListener(v ->
                Toast.makeText(getContext(), "Tranzactii - coming soon!", Toast.LENGTH_SHORT).show());

        // RecyclerView contacte
        setupContacte(view);
    }

    private void setupContacte(View view) {
        RecyclerView rvContacte = view.findViewById(R.id.rvContacte);
        rvContacte.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        contacteAdapter = new ContacteAdapter(
                contactRepository.getContacte(),
                () -> {
                    AdaugaContactBottomSheet bottomSheet = new AdaugaContactBottomSheet();
                    bottomSheet.setListener(() -> {
                        contacteAdapter.actualizeazaLista(contactRepository.getContacte());
                    });
                    bottomSheet.show(getParentFragmentManager(), "AdaugaContact");
                }
        );


        rvContacte.setAdapter(contacteAdapter);
    }


    private void actualizareCardRevelat(View view) {
        List<ContBancar> conturi = ContBancarRepository.getInstance().getConturi();
        if (conturi.isEmpty()) return;
        ContBancar cont = conturi.get(0);
        TextView tvStatus = view.findViewById(R.id.tvStatusInghetat);
        if (tvStatus != null) {
            tvStatus.setVisibility(cont.isInghetat() ? View.VISIBLE : View.GONE);
        }
    }


}