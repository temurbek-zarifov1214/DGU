package com.example.manualapp.ui.video;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.data.VideoRepository;
import com.example.manualapp.domain.VideoLesson;
import com.example.manualapp.ui.widget.HeritageNav;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VideoListActivity extends AppCompatActivity {

    private static final int TURQ  = Color.parseColor("#3EC6C0");
    private static final int GOLD  = Color.parseColor("#D9A441");
    private static final int BG     = Color.parseColor("#0E1626");
    private static final int TEXT2 = Color.parseColor("#9AA5B8");

    private String tab = "all";
    private final List<Object> rows = new ArrayList<>();   // String header | VideoLesson
    private RowAdapter adapter;
    private View emptyView;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);

        setContentView(R.layout.activity_video_list);
        density = getResources().getDisplayMetrics().density;

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        emptyView = findViewById(R.id.emptyView);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RowAdapter();
        rv.setAdapter(adapter);
        attachSwipe(rv);

        findViewById(R.id.tabAll).setOnClickListener(v -> switchTab("all"));
        findViewById(R.id.tabHistory).setOnClickListener(v -> switchTab("hist"));

        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddSheet());

        HeritageNav.setup(this, 1);
        rebuild();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuild();   // reflect saves/history changes from the player
    }

    private void switchTab(String t) {
        tab = t;
        styleTabs();
        rebuild();
    }

    private void styleTabs() {
        styleTab(R.id.tabAll, R.id.tabAllIcon, R.id.tabAllLabel, "all".equals(tab));
        styleTab(R.id.tabHistory, R.id.tabHistoryIcon, R.id.tabHistoryLabel, "hist".equals(tab));
    }

    private void styleTab(int rowId, int iconId, int labelId, boolean on) {
        findViewById(rowId).setBackgroundResource(on ? R.drawable.bg_h_tab_active : R.drawable.bg_h_tab_inactive);
        ((ImageView) findViewById(iconId)).setColorFilter(on ? BG : TEXT2);
        ((TextView) findViewById(labelId)).setTextColor(on ? BG : TEXT2);
    }

    private void rebuild() {
        List<VideoLesson> items;
        if ("hist".equals(tab)) items = VideoRepository.getHistory(this);
        else items = VideoRepository.getAll(this);

        rows.clear();
        if ("hist".equals(tab)) {
            if (!items.isEmpty()) rows.add(new Header("Soʻnggi koʻrilgan", false));
            rows.addAll(items);
        } else {
            // group by topic, "Yangi qoʻshilgan" first
            Map<String, List<VideoLesson>> groups = new LinkedHashMap<>();
            for (VideoLesson v : items) {
                List<VideoLesson> g = groups.get(v.topic);
                if (g == null) { g = new ArrayList<>(); groups.put(v.topic, g); }
                g.add(v);
            }
            List<String> order = new ArrayList<>(groups.keySet());
            order.sort((a, b) -> a.equals("Yangi qoʻshilgan") ? -1 : b.equals("Yangi qoʻshilgan") ? 1 : 0);
            for (String topic : order) {
                rows.add(new Header(topic, topic.equals("Yangi qoʻshilgan")));
                rows.addAll(groups.get(topic));
            }
        }
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        if (items.isEmpty()) setEmptyText();
    }

    private void setEmptyText() {
        TextView title = findViewById(R.id.emptyTitle);
        TextView sub = findViewById(R.id.emptySub);
        if ("hist".equals(tab)) {
            title.setText("Tarix boʻsh");
            sub.setText("Koʻrilgan videolar shu yerda chiqadi.");
        } else {
            title.setText("Hozircha video yoʻq");
            sub.setText("YouTube havolasini qoʻshish uchun + tugmasini bosing.");
        }
    }

    // ── Add-link sheet ──────────────────────────────────────────────────────────
    private void showAddSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = LayoutInflater.from(this).inflate(R.layout.sheet_add_link, null);
        dialog.setContentView(sheet);

        EditText et = sheet.findViewById(R.id.etLink);
        Button add = sheet.findViewById(R.id.btnAdd);
        Button paste = sheet.findViewById(R.id.btnPaste);
        ImageView linkIcon = sheet.findViewById(R.id.linkIcon);
        View inputRow = sheet.findViewById(R.id.inputRow);

        Runnable refresh = () -> {
            boolean ok = et.getText().toString().trim().length() >= 6;
            add.setEnabled(ok);
            add.setBackgroundResource(ok ? R.drawable.bg_h_btn_primary : R.drawable.bg_h_btn_disabled);
            add.setTextColor(ok ? BG : TEXT2);
            inputRow.setBackgroundResource(ok ? R.drawable.bg_h_input_active : R.drawable.bg_h_input);
            linkIcon.setColorFilter(ok ? TURQ : TEXT2);
        };
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { refresh.run(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        paste.setOnClickListener(v -> {
            CharSequence clip = readClipboard();
            et.setText(clip != null ? clip.toString() : "https://youtu.be/Naqsh-Buxoro");
            et.setSelection(et.getText().length());
        });

        sheet.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            String url = et.getText().toString().trim();
            if (url.length() < 6) return;
            com.example.manualapp.domain.VideoLesson added = VideoRepository.addFromUrl(this, url);
            dialog.dismiss();
            tab = "all";
            styleTabs();
            rebuild();
            Toast.makeText(this, "Video roʻyxatga qoʻshildi", Toast.LENGTH_SHORT).show();
            // Fetch the real YouTube title/description in the background, then refresh.
            com.example.manualapp.data.YoutubeMeta.fetch(added, m -> {
                if (m.any()) {
                    VideoRepository.updateMeta(this, added.id, m.title, m.description);
                    rebuild();
                }
            });
        });

        refresh.run();
        dialog.show();
    }

    private CharSequence readClipboard() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip() != null
                && cm.getPrimaryClip().getItemCount() > 0) {
            return cm.getPrimaryClip().getItemAt(0).coerceToText(this);
        }
        return null;
    }

    // ── Swipe left to delete an added video ─────────────────────────────────────
    private void attachSwipe(RecyclerView rv) {
        androidx.recyclerview.widget.ItemTouchHelper helper = new androidx.recyclerview.widget.ItemTouchHelper(
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0,
                        androidx.recyclerview.widget.ItemTouchHelper.LEFT) {
                    @Override public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder a, @NonNull RecyclerView.ViewHolder b) { return false; }

                    @Override public int getSwipeDirs(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder vh) {
                        int pos = vh.getBindingAdapterPosition();
                        if (pos < 0 || pos >= rows.size() || !(rows.get(pos) instanceof VideoLesson)) return 0;
                        return ((VideoLesson) rows.get(pos)).isNew
                                ? androidx.recyclerview.widget.ItemTouchHelper.LEFT : 0;
                    }

                    @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                        int pos = vh.getBindingAdapterPosition();
                        if (pos < 0 || pos >= rows.size() || !(rows.get(pos) instanceof VideoLesson)) { rebuild(); return; }
                        VideoLesson v = (VideoLesson) rows.get(pos);
                        VideoRepository.removeAdded(VideoListActivity.this, v.id);
                        rebuild();
                    }
                });
        helper.attachToRecyclerView(rv);
    }

    private void openPlayer(VideoLesson v) {
        VideoRepository.addHistory(this, v.id);
        Intent i = new Intent(this, VideoPlayerActivity.class);
        i.putExtra(VideoPlayerActivity.EXTRA_VIDEO, v);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ── Adapter ────────────────────────────────────────────────────────────────
    static class Header {
        final String title; final boolean turq;
        Header(String title, boolean turq) { this.title = title; this.turq = turq; }
    }

    private class RowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int HEADER = 0, CARD = 1;

        @Override public int getItemViewType(int position) {
            return rows.get(position) instanceof Header ? HEADER : CARD;
        }

        @NonNull @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inf = LayoutInflater.from(parent.getContext());
            if (viewType == HEADER)
                return new HeaderVH(inf.inflate(R.layout.item_video_section, parent, false));
            return new CardVH(inf.inflate(R.layout.item_video_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Object row = rows.get(position);
            if (holder instanceof HeaderVH) {
                Header h = (Header) row;
                ((HeaderVH) holder).title.setText(h.title.toUpperCase());
                ((HeaderVH) holder).title.setTextColor(h.turq ? TURQ : GOLD);
            } else {
                bindCard((CardVH) holder, (VideoLesson) row);
            }
        }

        private void bindCard(CardVH vh, VideoLesson v) {
            vh.title.setText(v.title);
            vh.topic.setText(v.topic);
            vh.dur.setText(v.duration);
            vh.badgeNew.setVisibility(v.isNew ? View.VISIBLE : View.GONE);

            // Coloured fallback behind the (async) YouTube thumbnail
            int color = parse(v.colorHex);
            GradientDrawable fallback = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR, new int[]{color, BG});
            vh.thumbImg.setBackground(fallback);
            vh.thumbImg.setImageDrawable(null);
            vh.thumbImg.setTag(null);
            com.example.manualapp.data.ImageLoader.load(v.thumbUrl(), vh.thumbImg);

            vh.itemView.setOnClickListener(x -> openPlayer(v));
        }

        @Override public int getItemCount() { return rows.size(); }
    }

    private static int parse(String hex) {
        try { return Color.parseColor(hex); } catch (Exception e) { return TURQ; }
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        final TextView title;
        HeaderVH(@NonNull View v) { super(v); title = v.findViewById(R.id.tvSectionTitle); }
    }

    static class CardVH extends RecyclerView.ViewHolder {
        final TextView title, topic, dur, badgeNew;
        final ImageView thumbImg;
        CardVH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvVideoTitle);
            topic = v.findViewById(R.id.tvVideoTopic);
            dur = v.findViewById(R.id.tvVideoDur);
            badgeNew = v.findViewById(R.id.tvBadgeNew);
            thumbImg = v.findViewById(R.id.videoThumbImg);
        }
    }
}
