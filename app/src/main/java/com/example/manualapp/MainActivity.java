package com.example.manualapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.list.ShowItemsActivity;
import com.example.manualapp.ui.video.VideoListActivity;
import com.example.manualapp.ui.widget.HeritageNav;

/**
 * Heritage home — greeting header, hero featured card, an "O'rganish" grid,
 * a Video darslar entry, and "Ma'lumot" rows, over the girih-dome background.
 */
public class MainActivity extends AppCompatActivity {

    private static final int TURQ  = Color.parseColor("#3EC6C0");
    private static final int GOLD  = Color.parseColor("#D9A441");
    private static final int TERRA = Color.parseColor("#C75B39");
    private static final int SAGE  = Color.parseColor("#7FA88B");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#0E1626"));
        window.setNavigationBarColor(Color.parseColor("#0E1626"));

        setContentView(R.layout.activity_main);

        // Hero → Naqshlar tarix tilsimi
        findViewById(R.id.heroCard).setOnClickListener(v -> openList(ContentType.TARIX));

        // O'rganish grid
        configLearn(R.id.cardUslublar, R.drawable.bg_tile_turq,  R.drawable.nq_uslublar, TURQ,
                "Naqqoshlik uslublari",   "5 maktab",  ContentType.USLUBLAR);
        configLearn(R.id.cardChizish,  R.drawable.bg_tile_gold,  R.drawable.nq_chizish,  GOLD,
                "Naqsh chizish asoslari", "Asoslar",   ContentType.ASOSLAR);
        configLearn(R.id.cardIlhom,    R.drawable.bg_tile_terra, R.drawable.nq_ilhom,    TERRA,
                "Naqshlar ilhom manbai",  "Namunalar", ContentType.ILHOM);
        configLearn(R.id.cardAsbob,    R.drawable.bg_tile_sage,  R.drawable.nq_asbob,    SAGE,
                "Asbob va materiallar",   "9 vosita",  ContentType.ASBOB);

        // Video entry
        findViewById(R.id.cardVideo).setOnClickListener(v -> {
            startActivity(new Intent(this, VideoListActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Ma'lumot rows
        configInfo(R.id.rowMuallif, R.drawable.nq_muallif, "Muallif haqida",
                "Xudoyberdiyeva Xulkar", ContentType.MUALLIF);
        configInfo(R.id.rowYoriq,   R.drawable.nq_yoriq,   "Foydalanish yoʻriqnomasi",
                "Qoʻllanma", ContentType.YORIQNOMA);
        configInfo(R.id.rowDgu,     R.drawable.nq_pdf,     "Ilova hujjati",
                "PDF hujjat", ContentType.DGU);

        HeritageNav.setup(this, 0);
    }

    private void configLearn(int cardId, int tileBg, int iconRes, int accent,
                             String title, String count, ContentType type) {
        View card = findViewById(cardId);
        card.findViewById(R.id.learnTile).setBackgroundResource(tileBg);
        ImageView icon = card.findViewById(R.id.learnIcon);
        icon.setImageResource(iconRes);
        icon.setColorFilter(accent);
        ((ImageView) card.findViewById(R.id.learnOrnament)).setColorFilter(accent);
        ((TextView) card.findViewById(R.id.learnTitle)).setText(title);
        ((TextView) card.findViewById(R.id.learnCount)).setText(count);
        card.setOnClickListener(v -> openList(type));
    }

    private void configInfo(int rowId, int iconRes, String title, String sub, ContentType type) {
        View row = findViewById(rowId);
        ((ImageView) row.findViewById(R.id.infoIcon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.infoTitle)).setText(title);
        ((TextView) row.findViewById(R.id.infoSub)).setText(sub);
        row.setOnClickListener(v -> openList(type));
    }

    private void openList(ContentType type) {
        Intent intent = new Intent(this, ShowItemsActivity.class);
        intent.putExtra(ContentType.KEY, type);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
