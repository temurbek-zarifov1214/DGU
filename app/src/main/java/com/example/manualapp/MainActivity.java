package com.example.manualapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.list.ShowItemsActivity;
import com.example.manualapp.ui.particle.ParticleView;

public class MainActivity extends AppCompatActivity {

    // ── Menu data ─────────────────────────────────────────────────────────────
    private static final int[] ICONS = {
            R.drawable.ic_book,
            R.drawable.ic_clock,
            R.drawable.ic_folder,
            R.drawable.ic_book_open,
            R.drawable.ic_question,
            R.drawable.ic_check_circle,
            R.drawable.ic_document,
            R.drawable.ic_person
    };
    private static final int[] ICON_BG_COLORS = {
            0x33FFEB3B,  // yellow  — Maruzalar
            0x2281D4FA,  // light blue — Amaliy
            0x22A5D6A7,  // green  — Tarqatma
            0x22CE93D8,  // purple — Glossary
            0x22FFB74D,  // orange — Oraliq
            0x224DD0E1,  // teal   — Yakuniy
            0x22FF8A80,  // red    — Hujjat
            0x22B2EBF2,  // cyan   — Mualliflar
    };
    private static final String[] LABELS1 = {
            "Maruzalar", "Amaliy", "Tarqatma", "Glossary",
            "Oraliq",    "Yakuniy", "Ilova",   "Mualliflar"
    };
    private static final String[] LABELS2 = {
            "matni", "mashg'ulotlar", "materiallar", "",
            "savollar", "savollar",   "hujjati",     "haqida"
    };
    private static final String[] SUBLABELS = {
            "16 mavzu", "Topshiriqlar", "Fayllar", "Atamalar lug'ati",
            "Test",     "Imtihon",      "PDF",      "Ma'lumot"
    };
    private static final ContentType[] TYPES = {
            ContentType.MARUZA, ContentType.AMALIYOT, ContentType.TARQATMA,
            ContentType.GLOSSARY, ContentType.ORALIQ,  ContentType.YAKUNIY,
            ContentType.DGU,    ContentType.MALUMOTNOMA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        buildGrid();
    }

    private void buildGrid() {
        GridLayout grid = findViewById(R.id.menuGrid);
        int gap = dpToPx(6);

        for (int i = 0; i < 8; i++) {
            final int index = i;
            View card = LayoutInflater.from(this).inflate(R.layout.item_menu_card, grid, false);

            // Icon container — colored rounded square
            FrameLayout iconContainer = card.findViewById(R.id.iconContainer);
            GradientDrawable iconBg = new GradientDrawable();
            iconBg.setColor(ICON_BG_COLORS[i]);
            iconBg.setCornerRadius(dpToPx(12));
            iconContainer.setBackground(iconBg);

            ((ImageView) card.findViewById(R.id.ivIcon)).setImageResource(ICONS[i]);
            ((TextView)  card.findViewById(R.id.tvLabel1)).setText(LABELS1[i]);

            TextView tvLabel2 = card.findViewById(R.id.tvLabel2);
            if (LABELS2[i].isEmpty()) {
                tvLabel2.setVisibility(View.GONE);
            } else {
                tvLabel2.setVisibility(View.VISIBLE);
                tvLabel2.setText(LABELS2[i]);
            }
            ((TextView) card.findViewById(R.id.tvSublabel)).setText(SUBLABELS[i]);

            // Glass background on card
            GradientDrawable cardBg = new GradientDrawable();
            cardBg.setColor(0x1AFFFFFF);
            cardBg.setStroke(dpToPx(1), 0x33FFFFFF);
            cardBg.setCornerRadius(dpToPx(16));
            card.setBackground(cardBg);

            // Grid layout params — equal column weights
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width       = 0;
            params.height      = 0;
            params.columnSpec  = GridLayout.spec(i % 2, 1, GridLayout.FILL, 1f);
            params.rowSpec     = GridLayout.spec(i / 2, 1, GridLayout.FILL, 1f);
            params.setMargins(
                    i % 2 == 0 ? 0      : gap,
                    i < 2      ? 0      : gap,
                    i % 2 == 0 ? gap    : 0,
                    0
            );
            card.setLayoutParams(params);

            // Press scale animation
            card.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            });

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ShowItemsActivity.class);
                intent.putExtra(ContentType.KEY, TYPES[index]);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            grid.addView(card);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        ParticleView pv = findViewById(R.id.particleView);
        if (pv != null) pv.startAnimation();
    }

    @Override protected void onPause() {
        super.onPause();
        ParticleView pv = findViewById(R.id.particleView);
        if (pv != null) pv.stopAnimation();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
