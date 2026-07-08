package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.ContactRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private boolean cardVisible = false;
    private View layoutCardRevelat;
    private View layoutSold;
    private ContacteAdapter contacteAdapter;

    // Wallet
    private List<ContBancar> conturiCurente = new ArrayList<>();
    private int activeCard = 0;
    private float dragStartY = 0;
    private float dragBaseY = 0;
    private List<View> cardViews = new ArrayList<>();

    // Cat de mult se vede din cardul din spate, sus si jos, simetric.
    // Egal cu padding-ul vertical al cardului, ca zona expusa sa fie doar culoare, fara text.
    private static final int WALLET_PEEK_DP = 22;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutCardRevelat = view.findViewById(R.id.layoutCardRevelat);
        layoutSold = view.findViewById(R.id.layoutSold);

        // Peek click — toggle wallet
        view.findViewById(R.id.layoutCardPeek).setOnClickListener(v -> {
            if (!cardVisible) {
                layoutSold.setVisibility(View.GONE);
                layoutCardRevelat.setVisibility(View.VISIBLE);
                cardVisible = true;
                TextView tvPeek = view.findViewById(R.id.tvPeekLabel);
                tvPeek.setText("▼  ascunde cardurile");
                setupWallet(view);
            } else {
                layoutCardRevelat.setVisibility(View.GONE);
                layoutSold.setVisibility(View.VISIBLE);
                cardVisible = false;
                TextView tvPeek = view.findViewById(R.id.tvPeekLabel);
                tvPeek.setText("▲  vezi cardurile");
            }
        });

        // Butoane contextuale card
        view.findViewById(R.id.btnDetaliuCard).setOnClickListener(v -> {
            if (conturiCurente.isEmpty()) return;
            String iban = conturiCurente.get(activeCard).getIban();
            DetaliiCardBottomSheet bottomSheet = DetaliiCardBottomSheet.newInstance(iban);
            bottomSheet.show(getParentFragmentManager(), "DetaliiCard");
        });

        view.findViewById(R.id.btnIngheataCard).setOnClickListener(v -> {
            if (conturiCurente.isEmpty()) return;
            String iban = conturiCurente.get(activeCard).getIban();
            IngheataCardBottomSheet bottomSheet = IngheataCardBottomSheet.newInstance(iban);
            bottomSheet.setListener(inghetat -> actualizareCardRevelat(view));
            bottomSheet.show(getParentFragmentManager(), "IngheataCard");
        });

        view.findViewById(R.id.btnStergeCard).setOnClickListener(v -> {
            if (conturiCurente.isEmpty()) return;
            String iban = conturiCurente.get(activeCard).getIban();
            StergeCardBottomSheet bottomSheet = StergeCardBottomSheet.newInstance(iban);
            bottomSheet.setListener(() -> {
                conturiCurente.clear();
                layoutCardRevelat.setVisibility(View.GONE);
                layoutSold.setVisibility(View.VISIBLE);
                cardVisible = false;
                TextView tvPeek = view.findViewById(R.id.tvPeekLabel);
                tvPeek.setText("▲  vezi cardurile");
                actualizeazaSoldTotal(view);
            });
            bottomSheet.show(getParentFragmentManager(), "StergeCard");
        });

        // Butoane rapide
        view.findViewById(R.id.btnTrimite).setOnClickListener(v -> {
            TrimiteBottomSheet bottomSheet = new TrimiteBottomSheet();
            bottomSheet.setCallback(() -> {
                actualizeazaSoldTotal(view);
                if (cardVisible) updateInfoText(view);
            });
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
            bottomSheet.setCallback(() -> actualizeazaSoldTotal(view));
            bottomSheet.show(getParentFragmentManager(), "AdaugaContBottomSheet");
        });



        actualizeazaHeader(view);
        actualizeazaSoldTotal(view);

        ContactRepository.getInstance().incarcaContacte(() -> {
            if (getView() != null) setupContacte(view);
        });
    }

    // ─── WALLET ────────────────────────────────────────────────────────────────

    private void setupWallet(View view) {
        conturiCurente.clear();
        List<ContBancar> toate = ContBancarRepository.getInstance().getConturi();
        for (ContBancar c : toate) {
            if (c.getTipCont() == ContBancar.TipCont.CURENT) {
                conturiCurente.add(c);
            }
        }

        if (conturiCurente.isEmpty()) return;

        activeCard = 0;
        buildCardViews(view);
        renderWallet(view, false);
        setupWalletTouch(view);
    }

    private void buildCardViews(View view) {
        FrameLayout frameWallet = view.findViewById(R.id.frameWallet);
        frameWallet.removeAllViews();
        cardViews.clear();

        // Citim numele utilizatorului o singura data, inainte de loop
        String userId = com.google.firebase.auth.FirebaseAuth
                .getInstance().getCurrentUser().getUid();
        android.content.SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_data_" + userId, android.content.Context.MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "");
        String lastName = prefs.getString("lastName", "");
        String numeTitular = (firstName + " " + lastName).trim();

        for (ContBancar cont : conturiCurente) {

            LinearLayout card = new LinearLayout(getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            // Padding sus/jos marit la WALLET_PEEK_DP — zona expusa cand cardul e in spate
            // ramane goala (doar culoare), fara sa "muste" din text
            card.setPadding(dp(14), dp(WALLET_PEEK_DP), dp(14), dp(WALLET_PEEK_DP));

            if (cont.isInghetat()) {
                card.setBackground(creazaFundalCard("#1C1C2E"));
            } else {
                try {
                    card.setBackground(creazaFundalCard(cont.getCuloareBanca()));
                } catch (Exception e) {
                    card.setBackground(creazaFundalCard("#1A3C6E"));
                }
            }

            // lpFill creat nou pentru fiecare card — NU se reutilizeaza
            LinearLayout.LayoutParams lpFill = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

            // Rand sus: banca + VISA
            LinearLayout rowTop = new LinearLayout(getContext());
            rowTop.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvBanca = new TextView(getContext());
            tvBanca.setText(cont.getNumeBanca() + " · " + cont.getTipCard());
            tvBanca.setTextColor(Color.parseColor("#CCFFFFFF"));
            tvBanca.setTextSize(10f);
            // lpFill nou pentru tvBanca
            tvBanca.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvNetwork = new TextView(getContext());
            tvNetwork.setText("VISA");
            tvNetwork.setTextColor(Color.WHITE);
            tvNetwork.setTextSize(12f);
            tvNetwork.setTypeface(null, android.graphics.Typeface.BOLD);

            rowTop.addView(tvBanca);
            rowTop.addView(tvNetwork);

            if (cont.isInghetat()) {
                TextView tvBadge = new TextView(getContext());
                tvBadge.setText("❄ Îngheațat");
                tvBadge.setTextColor(Color.parseColor("#4A90D9"));
                tvBadge.setTextSize(8f);
                tvBadge.setBackgroundColor(Color.parseColor("#1A2A4A"));
                tvBadge.setPadding(dp(6), dp(2), dp(6), dp(2));
                LinearLayout.LayoutParams lpBadge = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lpBadge.topMargin = dp(4);
                tvBadge.setLayoutParams(lpBadge);
                card.addView(tvBadge);
            }

            // Nr card
            TextView tvNr = new TextView(getContext());
            tvNr.setText("•••• •••• •••• " +
                    cont.getIban().substring(cont.getIban().length() - 4));
            tvNr.setTextColor(Color.WHITE);
            tvNr.setTextSize(14f);
            tvNr.setTypeface(null, android.graphics.Typeface.BOLD);
            tvNr.setLetterSpacing(0.1f);
            tvNr.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams lpNr = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpNr.topMargin = dp(14);
            lpNr.bottomMargin = dp(14);
            tvNr.setLayoutParams(lpNr);

            // Rand jos: titular + sold
            LinearLayout rowBot = new LinearLayout(getContext());
            rowBot.setOrientation(LinearLayout.HORIZONTAL);

            // Coloana titular
            LinearLayout colTitular = new LinearLayout(getContext());
            colTitular.setOrientation(LinearLayout.VERTICAL);
            // lpFill nou pentru colTitular
            colTitular.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView lblTitular = new TextView(getContext());
            lblTitular.setText("Titular");
            lblTitular.setTextColor(Color.parseColor("#88FFFFFF"));
            lblTitular.setTextSize(8f);

            TextView tvTitular = new TextView(getContext());
            // Daca SharedPreferences are numele, il folosim; altfel fallback pe cont.getTitular()
            String numeDeAfisat = numeTitular.isEmpty() ? cont.getTitular() : numeTitular;
            tvTitular.setText(numeDeAfisat);
            tvTitular.setTextColor(Color.WHITE);
            tvTitular.setTextSize(10f);
            tvTitular.setTypeface(null, android.graphics.Typeface.BOLD);

            colTitular.addView(lblTitular);
            colTitular.addView(tvTitular);

            // Coloana sold
            LinearLayout colSold = new LinearLayout(getContext());
            colSold.setOrientation(LinearLayout.VERTICAL);
            colSold.setGravity(android.view.Gravity.END);

            TextView lblSold = new TextView(getContext());
            lblSold.setText("Sold");
            lblSold.setTextColor(Color.parseColor("#88FFFFFF"));
            lblSold.setTextSize(8f);

            TextView tvSold = new TextView(getContext());
            tvSold.setText(String.format("%,.0f %s", cont.getSold(), cont.getValuta()));
            tvSold.setTextColor(Color.WHITE);
            tvSold.setTextSize(13f);
            tvSold.setTypeface(null, android.graphics.Typeface.BOLD);

            colSold.addView(lblSold);
            colSold.addView(tvSold);

            rowBot.addView(colTitular);
            rowBot.addView(colSold);

            card.addView(rowTop);
            card.addView(tvNr);
            card.addView(rowBot);

            // LayoutParams pentru card in FrameLayout — inaltime marita la 165dp
            // ca sa compenseze padding-ul mai mare, fara sa strangem textul
            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, dp(165));
            card.setLayoutParams(fp);

            frameWallet.addView(card);
            cardViews.add(card);

            // Click pe card din spate — aduce in fata
            final int idx = cardViews.size() - 1;
            card.setOnClickListener(v -> {
                if (idx != activeCard) {
                    activeCard = idx;
                    renderWallet(view, true);
                }
            });
        }
    }

    private void renderWallet(View view, boolean animate) {
        int n = cardViews.size();
        for (int i = 0; i < n; i++) {
            View card = cardViews.get(i);
            int offset = i - activeCard;
            int absOff = Math.abs(offset);

            // Offset simetric: WALLET_PEEK_DP e atat gap-ul de sus al activului
            // cat si fasia expusa a vecinilor — sus si jos se vede la fel de mult
            float targetY = dp(WALLET_PEEK_DP + offset * WALLET_PEEK_DP);
            float targetScale = 1f - absOff * 0.055f;
            float targetAlpha = absOff == 0 ? 1f : absOff == 1 ? 0.7f : 0.3f;
            int targetZ = n - absOff;

            card.setZ(targetZ);

            if (animate) {
                animateCard(card, targetY, targetScale, targetAlpha);
            } else {
                card.setTranslationY(targetY);
                card.setScaleX(targetScale);
                card.setScaleY(targetScale);
                card.setAlpha(targetAlpha);
            }
        }

        updateDots(view);
        updateInfoText(view);
    }

    private void animateCard(View card, float toY, float toScale, float toAlpha) {
        float fromY = card.getTranslationY();
        float fromScale = card.getScaleX();
        float fromAlpha = card.getAlpha();

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(380);
        anim.setInterpolator(new android.view.animation.DecelerateInterpolator(2f));
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            float tBounce = t < 0.8f ? t / 0.8f :
                    1f + (float) Math.sin((t - 0.8f) / 0.2f * Math.PI) * 0.04f;
            card.setTranslationY(fromY + (toY - fromY) * tBounce);
            card.setScaleX(fromScale + (toScale - fromScale) * t);
            card.setScaleY(fromScale + (toScale - fromScale) * t);
            card.setAlpha(fromAlpha + (toAlpha - fromAlpha) * t);
        });
        anim.start();
    }

    private void updateDots(View view) {
        LinearLayout dotsLayout = view.findViewById(R.id.layoutDotsWallet);
        dotsLayout.removeAllViews();
        for (int i = 0; i < cardViews.size(); i++) {
            View dot = new View(getContext());
            int w = i == activeCard ? dp(18) : dp(5);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, dp(5));
            lp.setMargins(dp(3), 0, dp(3), 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundColor(i == activeCard ?
                    Color.parseColor("#4CAF50") : Color.parseColor("#2a5c30"));
            final int idx = i;
            dot.setOnClickListener(v -> {
                activeCard = idx;
                renderWallet(view, true);
            });
            dotsLayout.addView(dot);
        }
    }

    private void updateInfoText(View view) {
        if (conturiCurente.isEmpty()) return;
        ContBancar cont = conturiCurente.get(activeCard);

        TextView tv = view.findViewById(R.id.tvInfoCardWallet);

        tv.animate().alpha(0f).setDuration(100).withEndAction(() -> {
            String soldText = String.format("%,.0f %s disponibili",
                    cont.getSold(), cont.getValuta());

            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            bg.setCornerRadius(dp(50));
            bg.setColor(Color.parseColor("#1e4a26"));

            tv.setBackground(bg);
            tv.setText("● " + soldText);
            tv.setTextColor(Color.parseColor("#81C784"));
            tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setPadding(dp(16), dp(7), dp(16), dp(7));
            tv.setGravity(android.view.Gravity.CENTER);

            tv.animate().alpha(1f).setDuration(150).start();
        }).start();
    }

    private void setupWalletTouch(View view) {
        FrameLayout frame = view.findViewById(R.id.frameWallet);
        frame.setOnTouchListener((v, event) -> {
            if (cardViews.isEmpty()) return true;
            View cardActiv = cardViews.get(activeCard);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dragStartY = event.getY();
                    dragBaseY = cardActiv.getTranslationY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float delta = event.getY() - dragStartY;
                    cardActiv.setTranslationY(dragBaseY + delta);
                    break;

                case MotionEvent.ACTION_UP:
                    float diff = dragStartY - event.getY();
                    if (Math.abs(diff) > dp(20)) {
                        if (diff > 0 && activeCard < conturiCurente.size() - 1) {
                            activeCard++;
                        } else if (diff < 0 && activeCard > 0) {
                            activeCard--;
                        }
                    }
                    renderWallet(view, true);
                    break;
            }
            return true;
        });
    }

    // ─── HELPER ────────────────────────────────────────────────────────────────

    private int dp(int val) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(val * density);
    }

    // Creeaza un fundal rotunjit pentru cardul bancar, colorat cu culoarea bancii
    private android.graphics.drawable.GradientDrawable creazaFundalCard(String culoareHex) {
        android.graphics.drawable.GradientDrawable fundal = new android.graphics.drawable.GradientDrawable();
        fundal.setColor(Color.parseColor(culoareHex));
        fundal.setCornerRadius(dp(16));
        return fundal;
    }

    // ─── RESTUL METODELOR NEATINSE ─────────────────────────────────────────────

    private void setupContacte(View view) {
        RecyclerView rvContacte = view.findViewById(R.id.rvContacte);
        rvContacte.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        contacteAdapter = new ContacteAdapter(
                ContactRepository.getInstance().getContacte(),
                () -> {
                    AdaugaContactBottomSheet bottomSheet = new AdaugaContactBottomSheet();
                    bottomSheet.setListener(() -> contacteAdapter.actualizeazaLista(
                            ContactRepository.getInstance().getContacte()));
                    bottomSheet.show(getParentFragmentManager(), "AdaugaContact");
                },
                contact -> {
                    ContactDetailsBottomSheet bottomSheet = new ContactDetailsBottomSheet();
                    bottomSheet.setContact(contact);
                    bottomSheet.setListener(() -> contacteAdapter.actualizeazaLista(
                            ContactRepository.getInstance().getContacte()));
                    bottomSheet.show(getParentFragmentManager(), "ContactDetails");
                }
        );
        rvContacte.setAdapter(contacteAdapter);
    }

    private void actualizeazaHeader(View view) {
        com.google.firebase.auth.FirebaseUser user =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        String userId = user != null ? user.getUid() : "";
        android.content.SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_data_" + userId, android.content.Context.MODE_PRIVATE);

        String firstName = prefs.getString("firstName", "");
        String lastName = prefs.getString("lastName", "");

        String numeAfisat;
        if (!firstName.isEmpty() || !lastName.isEmpty()) {
            numeAfisat = (firstName + " " + lastName).trim();
        } else if (user != null && user.getEmail() != null) {
            numeAfisat = user.getEmail();
        } else {
            numeAfisat = "Utilizator";
        }

        TextView tvUserName = view.findViewById(R.id.tvUserName);
        tvUserName.setText(numeAfisat);

        TextView tvAvatar = view.findViewById(R.id.tvAvatar);
        String initiale = "";
        if (!firstName.isEmpty()) initiale += firstName.charAt(0);
        if (!lastName.isEmpty()) initiale += lastName.charAt(0);
        if (initiale.isEmpty()) initiale = "U";
        tvAvatar.setText(initiale.toUpperCase());
    }

    private void actualizeazaSoldTotal(View view) {
        List<ContBancar> conturiCurente = ContBancarRepository.getInstance().getConturiCurente();

        double totalRon = 0, totalEur = 0, totalUsd = 0;
        for (ContBancar cont : conturiCurente) {
            switch (cont.getValuta()) {
                case "RON": totalRon += cont.getSold(); break;
                case "EUR": totalEur += cont.getSold(); break;
                case "USD": totalUsd += cont.getSold(); break;
            }
        }

        TextView tvSoldTotal = view.findViewById(R.id.tvSoldTotal);
        tvSoldTotal.setText(String.format("%,.2f RON", totalRon));

        TextView tvAlteCurrency = view.findViewById(R.id.tvAlteCurrency);
        StringBuilder alteCurrency = new StringBuilder();
        if (totalEur > 0) alteCurrency.append(String.format("+%,.2f EUR", totalEur));
        if (totalUsd > 0) {
            if (alteCurrency.length() > 0) alteCurrency.append("  ");
            alteCurrency.append(String.format("+%,.2f USD", totalUsd));
        }

        if (alteCurrency.length() > 0) {
            tvAlteCurrency.setText(alteCurrency.toString());
            tvAlteCurrency.setVisibility(View.VISIBLE);
        } else {
            tvAlteCurrency.setVisibility(View.GONE);
        }
    }

    private void actualizareCardRevelat(View view) {
        if (conturiCurente.isEmpty()) return;
        ContBancar cont = conturiCurente.get(activeCard);

        TextView tvLabel = view.findViewById(R.id.tvLabelIngheata);
        if (tvLabel == null) return;

        if (cont.isInghetat()) {
            tvLabel.setText("Dezgheață");
            tvLabel.setTextColor(Color.parseColor("#4A90D9"));
        } else {
            tvLabel.setText("Îngheață");
            tvLabel.setTextColor(Color.parseColor("#81C784"));
        }

        // Rebuild carduri ca sa reflecte noua stare vizuala
        buildCardViews(view);
        renderWallet(view, false);
    }
}