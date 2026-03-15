package com.example.manualapp.ui.list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.particle.ParticleView;
import com.example.manualapp.ui.pdf.PDFActivityReading;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    private static final String[][] SUBTITLES = {
            // MARUZA (16)
            {"Pedagogikaga kirish","Ta'lim tarixi","Xalqaro taqqoslama","Zamonaviy yondashuvlar",
             "Pedagogika tizimlari","Jahon tajribasi","O'qitish metodlari","Baholash tizimi",
             "Yevropadagi ta'lim","AQSh ta'lim tizimi","Osiyo modellari","Maktabgacha ta'lim",
             "Oliy ta'lim","Kasbiy ta'lim","Maxsus ta'lim","Kelajak istiqboli"},
            // AMALIYOT (20)
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

    private ParticleView particleView;
    private ContentType  contentType;
    private FileAdapter  adapter;
    private final List<FileItem> allItems = new ArrayList<>();
    private final List<FileItem> shown    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#071535"));
        window.setNavigationBarColor(Color.parseColor("#071535"));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#0D2560")));
        }

        setContentView(R.layout.activity_show_items);

        particleView = findViewById(R.id.particleView);

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
            String sub = (i < subtitleSet.length) ? subtitleSet[i] : "PDF hujjat";
            allItems.add(new FileItem(names.get(i), sub, contentType.getPdfPath(i)));
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

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                android.widget.Toast.makeText(this,
                        "Bu bo'limga fayllar assets'dan yuklanadi",
                        android.widget.Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (particleView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    particleView.setPointerPosition(ev.getX(), ev.getY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    particleView.clearPointer();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (particleView != null) particleView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (particleView != null) particleView.pause();
    }

    // ── Data model ─────────────────────────────────────────────────────────────
    static class FileItem {
        final String name, subtitle, assetPath;
        FileItem(String name, String subtitle, String assetPath) {
            this.name = name; this.subtitle = subtitle; this.assetPath = assetPath;
        }
    }

    interface OnItemClick { void onClick(FileItem item); }

    // ── Adapter ────────────────────────────────────────────────────────────────
    static class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

        private final List<FileItem> items;
        private final OnItemClick    listener;

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
            holder.itemView.setOnClickListener(v -> listener.onClick(item));

            // Slide in from right with stagger
            holder.itemView.setTranslationX(200f);
            holder.itemView.setAlpha(0f);
            holder.itemView.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay(position * 40L)
                    .setInterpolator(new DecelerateInterpolator())
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
