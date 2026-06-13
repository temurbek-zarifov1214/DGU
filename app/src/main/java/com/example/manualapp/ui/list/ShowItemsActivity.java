package com.example.manualapp.ui.list;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.pdf.PDFActivityReading;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    private static final int TURQ  = Color.parseColor("#3EC6C0");
    private static final int GOLD  = Color.parseColor("#D9A441");
    private static final int TERRA = Color.parseColor("#C75B39");
    private static final int SAGE  = Color.parseColor("#7FA88B");
    private static final int LAPIS = Color.parseColor("#3D6FB4");

    // Per-content accent + header icon (enum order: TARIX, USLUBLAR, ASOSLAR,
    // ILHOM, ASBOB, MUALLIF, YORIQNOMA, DGU)
    private static final int[] ACCENT = { GOLD, TURQ, SAGE, TERRA, GOLD, LAPIS, TURQ, GOLD };
    private static final int[] CONTENT_ICON = {
            R.drawable.nq_tarix, R.drawable.nq_uslublar, R.drawable.nq_chizish, R.drawable.nq_ilhom,
            R.drawable.nq_asbob, R.drawable.nq_muallif, R.drawable.nq_yoriq, R.drawable.nq_pdf
    };

    // The schools list (USLUBLAR) gets distinct naqsh thumbnails + colors
    private static final int[] SCHOOL_THUMBS = {
            R.drawable.thumb_buxoro, R.drawable.thumb_fargona, R.drawable.thumb_samarqand,
            R.drawable.thumb_toshkent, R.drawable.thumb_xiva
    };
    private static final int[] SCHOOL_COLORS = { TURQ, GOLD, LAPIS, TERRA, SAGE };

    private static final String[][] SUBTITLES = {
            // TARIX (3)
            {"Meʼmoriy yodgorliklar", "Davrlar silsilasi", "Abadiy chiziqlar tahlili"},
            // USLUBLAR (5) — school traits
            {"Islimiy gullar uslubi", "Nafis oʻsimlik naqshi", "Geometrik girih uslubi",
             "Zamonaviy uygʻunlik", "Yogʻoch oʻymakorligi girihi"},
            // ASOSLAR (1)
            {"Chizish asoslari"},
            // ILHOM (1)
            {"Kompozitsiya namunalari"},
            // ASBOB (9)
            {"Boʻyoq materiallari", "Asbob-uskuna", "Oʻlchov asboblari", "Material",
             "Chizish asboblari", "Yozuv asboblari", "Material", "Oʻlchov asboblari", "Material"},
            // MUALLIF (1)
            {"Dastur muallifi"},
            // YORIQNOMA (1)
            {"Foydalanish qoʻllanmasi"},
            // DGU (1)
            {"Ilova hujjati"},
    };

    private ContentType contentType;
    private FileAdapter adapter;
    private View emptyView;
    private final List<FileItem> allItems = new ArrayList<>();
    private final List<FileItem> shown    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#0E1626"));
        window.setNavigationBarColor(Color.parseColor("#0E1626"));

        setContentView(R.layout.activity_show_items);

        contentType = (ContentType) getIntent().getSerializableExtra(ContentType.KEY);
        if (contentType == null) { finish(); return; }
        int ordinal = contentType.ordinal();
        boolean isSchools = contentType == ContentType.USLUBLAR;

        ((TextView) findViewById(R.id.tvTitle)).setText(contentType.getToolbarTitle());
        emptyView = findViewById(R.id.emptyView);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Build items
        String[] subtitleSet = SUBTITLES[ordinal];
        List<String> names = contentType.getItemNames();
        for (int i = 0; i < names.size(); i++) {
            String sub = (i < subtitleSet.length) ? subtitleSet[i] : "PDF hujjat";
            FileItem item = new FileItem(names.get(i), sub, contentType.getPdfPath(i));
            if (isSchools && i < SCHOOL_THUMBS.length) {
                item.thumbRes = SCHOOL_THUMBS[i];
                item.accent   = SCHOOL_COLORS[i];
            } else {
                item.iconRes = CONTENT_ICON[ordinal];
                item.accent  = ACCENT[ordinal];
            }
            allItems.add(item);
        }
        shown.addAll(allItems);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter(shown, this::openPdf);
        rv.setAdapter(adapter);

        ((EditText) findViewById(R.id.etSearch)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // FAB only makes sense on the schools list (add a school)
        ImageButton fab = findViewById(R.id.fabAdd);
        if (isSchools) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> android.widget.Toast.makeText(this,
                    "Maktablar ilova ichida joylashgan", android.widget.Toast.LENGTH_SHORT).show());
        }
    }

    private void filter(String query) {
        shown.clear();
        String q = query.toLowerCase();
        for (FileItem item : allItems) {
            if (q.isEmpty() || item.name.toLowerCase().contains(q)
                    || item.subtitle.toLowerCase().contains(q)) {
                shown.add(item);
            }
        }
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openPdf(FileItem item) {
        Intent intent = new Intent(this, PDFActivityReading.class);
        intent.putExtra("assetPath", item.assetPath);
        intent.putExtra("fileName",  item.name);
        intent.putExtra("subtitle",  item.subtitle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ── Data model ─────────────────────────────────────────────────────────────
    static class FileItem {
        final String name, subtitle, assetPath;
        int thumbRes = 0, iconRes = 0, accent = Color.parseColor("#3EC6C0");
        FileItem(String name, String subtitle, String assetPath) {
            this.name = name; this.subtitle = subtitle; this.assetPath = assetPath;
        }
    }

    interface OnItemClick { void onClick(FileItem item); }

    // ── Adapter ────────────────────────────────────────────────────────────────
    static class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

        private final List<FileItem> items;
        private final OnItemClick listener;

        FileAdapter(List<FileItem> items, OnItemClick listener) {
            this.items = items; this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_file_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            FileItem item = items.get(position);
            holder.tvName.setText(item.name);
            holder.tvSubtitle.setText(item.subtitle);
            holder.strip.setBackgroundColor(item.accent);

            if (item.thumbRes != 0) {
                holder.thumb.setImageResource(item.thumbRes);
                holder.thumb.setVisibility(View.VISIBLE);
                holder.iconWrap.setVisibility(View.GONE);
            } else {
                holder.thumb.setVisibility(View.GONE);
                holder.iconWrap.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(item.iconRes);
                holder.icon.setColorFilter(item.accent);
            }

            holder.itemView.setOnClickListener(v -> listener.onClick(item));

            holder.itemView.setTranslationX(160f);
            holder.itemView.setAlpha(0f);
            holder.itemView.animate()
                    .translationX(0f).alpha(1f)
                    .setDuration(300)
                    .setStartDelay(position * 40L)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvName, tvSubtitle;
            final View strip;
            final ImageView thumb, icon;
            final View iconWrap;
            VH(@NonNull View v) {
                super(v);
                tvName     = v.findViewById(R.id.tvName);
                tvSubtitle = v.findViewById(R.id.tvSubtitle);
                strip      = v.findViewById(R.id.cardStrip);
                thumb      = v.findViewById(R.id.cardThumb);
                icon       = v.findViewById(R.id.cardIcon);
                iconWrap   = v.findViewById(R.id.cardIconWrap);
            }
        }
    }
}
