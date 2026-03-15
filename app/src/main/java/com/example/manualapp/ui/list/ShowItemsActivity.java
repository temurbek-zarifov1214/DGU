package com.example.manualapp.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.particle.ParticleView;
import com.example.manualapp.ui.pdf.PDFActivityReading;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    // Static topic subtitles — shown as the second line under each file name
    // These are mapped by section; extra items default to "PDF hujjat"
    private static final String[][] SUBTITLES = {
            // MARUZA (16 items)
            {"Pedagogikaga kirish","Ta'lim tarixi","Xalqaro taqqoslama","Zamonaviy yondashuvlar",
             "Pedagogika tizimlari","Jahon tajribasi","O'qitish metodlari","Baholash tizimi",
             "Yevropadagi ta'lim","AQSh ta'lim tizimi","Osiyo modellari","Maktabgacha ta'lim",
             "Oliy ta'lim","Kasbiy ta'lim","Maxsus ta'lim","Kelajak istiqboli"},
            // AMALIYOT (20 items)
            {"Amaliy topshiriq","Amaliy topshiriq","Amaliy topshiriq","Amaliy topshiriq",
             "Amaliy topshiriq","Amaliy topshiriq","Amaliy topshiriq","Amaliy topshiriq",
             "Amaliy topshiriq","Amaliy topshiriq","Mavzu bo'yicha","Mavzu bo'yicha",
             "Mavzu bo'yicha","Mavzu bo'yicha","Mavzu bo'yicha","Mavzu bo'yicha",
             "Mavzu bo'yicha","Mavzu bo'yicha","Mavzu bo'yicha","Mavzu bo'yicha"},
            // TARQATMA (1)
            {"Fan dasturi"},
            // GLOSSARY (1)
            {"Pedagogika atamalari"},
            // ORALIQ (1)
            {"200 ta test savoli"},
            // YAKUNIY (1)
            {"200 ta test savoli"},
            // DGU (1)
            {"Mobil ilova haqida"},
            // MALUMOTNOMA (2)
            {"Mualliflar haqida","Mualliflar haqida"},
    };

    private ContentType contentType;
    private FileAdapter adapter;
    private final List<FileItem> allItems  = new ArrayList<>();
    private final List<FileItem> shown     = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_show_items);

        contentType = (ContentType) getIntent().getSerializableExtra(ContentType.KEY);
        if (contentType == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvTitle)).setText(contentType.getToolbarTitle());
        ((TextView) findViewById(R.id.tvNavLabel)).setText(contentType.getToolbarTitle());

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Build item list
        String[] subtitleSet = SUBTITLES[contentType.ordinal()];
        List<String> names = contentType.getItemNames();
        for (int i = 0; i < names.size(); i++) {
            String subtitle = (i < subtitleSet.length) ? subtitleSet[i] : "PDF hujjat";
            allItems.add(new FileItem(names.get(i), subtitle, contentType.getPdfPath(i)));
        }
        shown.addAll(allItems);

        // RecyclerView
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter(shown, this::openPdf);
        rv.setAdapter(adapter);

        // Search filter
        ((EditText) findViewById(R.id.etSearch)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // FAB — currently just shows a toast (per spec, could show dialog)
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                android.widget.Toast.makeText(this,
                        "Bu bo'limga fayllar assets'dan yuklanadi", android.widget.Toast.LENGTH_SHORT).show()
        );
    }

    private void filter(String query) {
        shown.clear();
        for (FileItem item : allItems) {
            if (query.isEmpty() || item.name.toLowerCase().contains(query.toLowerCase())) {
                shown.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void openPdf(FileItem item) {
        Intent intent = new Intent(this, PDFActivityReading.class);
        intent.putExtra("assetPath", item.assetPath);
        intent.putExtra("fileName",  item.name);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    // ── Data model ────────────────────────────────────────────────────────────
    static class FileItem {
        final String name, subtitle, assetPath;
        FileItem(String name, String subtitle, String assetPath) {
            this.name = name; this.subtitle = subtitle; this.assetPath = assetPath;
        }
    }

    // ── Adapter ───────────────────────────────────────────────────────────────
    interface OnItemClick { void onClick(FileItem item); }

    static class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

        private final List<FileItem> items;
        private final OnItemClick    listener;

        FileAdapter(List<FileItem> items, OnItemClick listener) {
            this.items    = items;
            this.listener = listener;
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
            holder.itemView.setOnClickListener(v -> listener.onClick(item));

            // Staggered entry animation (slide up + fade in)
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(40f);
            holder.itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(position * 50L)
                    .setDuration(280)
                    .start();
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvName, tvSubtitle;
            VH(@NonNull View v) {
                super(v);
                tvName     = v.findViewById(R.id.tvName);
                tvSubtitle = v.findViewById(R.id.tvSubtitle);
            }
        }
    }
}
