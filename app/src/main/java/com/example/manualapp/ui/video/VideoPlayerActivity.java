package com.example.manualapp.ui.video;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.R;
import com.example.manualapp.data.VideoRepository;
import com.example.manualapp.domain.VideoLesson;

import java.net.URLEncoder;

/**
 * Video lesson screen — a 16:9 in-app YouTube player on top, then title, a
 * collapsible "Tavsif", and a download/offline pill at the bottom.
 *
 * Playback embeds the YouTube IFrame via loadDataWithBaseURL with an https base
 * origin, which avoids the "Error 153 / configuration" the bare /embed/ URL
 * produces in a WebView. YouTube's fullscreen button is supported via a custom
 * WebChromeClient.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO = "video";

    private static final int TURQ  = Color.parseColor("#3EC6C0");
    private static final int TEXT2 = Color.parseColor("#9AA5B8");
    private static final int BG    = Color.parseColor("#0E1626");

    private VideoLesson video;
    private WebView web;
    private FrameLayout root;
    private boolean descOpen = true;
    private boolean downloading = false;
    private float density;

    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);

        setContentView(R.layout.activity_video_player);
        density = getResources().getDisplayMetrics().density;

        video = (VideoLesson) getIntent().getSerializableExtra(EXTRA_VIDEO);
        if (video == null) { finish(); return; }

        root = findViewById(R.id.playerRoot);
        int color = parse(video.colorHex);

        // Thumbnail behind play button
        findViewById(R.id.playerOverlay).setBackground(
                new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{color, BG}));

        ((TextView) findViewById(R.id.tvVideoTitle)).setText(video.title);
        ((TextView) findViewById(R.id.tvMeta)).setText(video.topic);

        web = findViewById(R.id.playerWeb);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new FullscreenChrome());

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnPlay).setOnClickListener(v -> play());
        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            findViewById(R.id.offlineOverlay).setVisibility(View.GONE);
            play();
        });
        findViewById(R.id.descHeader).setOnClickListener(v -> toggleDesc());

        // Download pill (also the save control)
        GradientDrawable dlThumb = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{color, BG});
        dlThumb.setCornerRadius(7 * density);
        findViewById(R.id.dlThumb).setBackground(dlThumb);
        ((TextView) findViewById(R.id.dlTitle)).setText(video.title);
        findViewById(R.id.dlPill).setVisibility(View.VISIBLE);
        findViewById(R.id.dlPill).setOnClickListener(v -> { if (!video.saved) startDownload(); });
        refreshPill();
    }

    // ── Playback ────────────────────────────────────────────────────────────────
    private void play() {
        if (!isOnline() && !video.saved) {
            findViewById(R.id.offlineOverlay).setVisibility(View.VISIBLE);
            return;
        }
        findViewById(R.id.offlineOverlay).setVisibility(View.GONE);
        findViewById(R.id.playerOverlay).setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);
        web.loadUrl(playUrl());
    }

    /**
     * Real YouTube watch page (not the IFrame embed) — embed-disabled videos
     * ("Error 150/152") still play here, and at full width the player fills the
     * 16:9 frame. This is literally "watch on YouTube".
     */
    private String playUrl() {
        if (video.youtubeId != null && !video.youtubeId.isEmpty()) {
            return "https://m.youtube.com/watch?v=" + video.youtubeId;
        }
        if (video.url != null && !video.url.isEmpty()) {
            return video.url;
        }
        String q = (video.query != null && !video.query.isEmpty()) ? video.query : video.title;
        try { q = URLEncoder.encode(q, "UTF-8"); } catch (Exception ignored) {}
        return "https://m.youtube.com/results?search_query=" + q;
    }

    // ── Tavsif ──────────────────────────────────────────────────────────────────
    private void toggleDesc() {
        descOpen = !descOpen;
        findViewById(R.id.tvDesc).setVisibility(descOpen ? View.VISIBLE : View.GONE);
        findViewById(R.id.descChevron).setRotation(descOpen ? 0f : 180f);
    }

    // ── Download pill (offline save) ────────────────────────────────────────────
    private void startDownload() {
        if (downloading || video.saved) return;
        downloading = true;
        View fill = findViewById(R.id.dlFill);
        View rest = findViewById(R.id.dlRest);
        TextView pct = findViewById(R.id.dlPct);

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(2200);
        anim.addUpdateListener(a -> {
            float f = Math.max(0.01f, (float) a.getAnimatedValue());
            ((LinearLayout.LayoutParams) fill.getLayoutParams()).weight = f;
            ((LinearLayout.LayoutParams) rest.getLayoutParams()).weight = 1f - f;
            fill.requestLayout();
            pct.setText((int) (f * 100) + "%");
        });
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator a) {
                downloading = false;
                video.saved = true;
                VideoRepository.setSaved(VideoPlayerActivity.this, video.id, true);
                refreshPill();
                Toast.makeText(VideoPlayerActivity.this, "Video saqlandi (oflayn)", Toast.LENGTH_SHORT).show();
            }
        });
        anim.start();
    }

    private void refreshPill() {
        View fill = findViewById(R.id.dlFill);
        View rest = findViewById(R.id.dlRest);
        TextView pct = findViewById(R.id.dlPct);
        ImageView icon = findViewById(R.id.dlIcon);
        if (video.saved) {
            pct.setText("Saqlandi");
            icon.setImageResource(R.drawable.nq_check);
            ((LinearLayout.LayoutParams) fill.getLayoutParams()).weight = 1f;
            ((LinearLayout.LayoutParams) rest.getLayoutParams()).weight = 0f;
        } else {
            pct.setText("Yuklab olish");
            icon.setImageResource(R.drawable.nq_download);
            ((LinearLayout.LayoutParams) fill.getLayoutParams()).weight = 0.01f;
            ((LinearLayout.LayoutParams) rest.getLayoutParams()).weight = 0.99f;
        }
        fill.requestLayout();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static int parse(String hex) {
        try { return Color.parseColor(hex); } catch (Exception e) { return TURQ; }
    }

    // ── Fullscreen (YouTube fullscreen button) ──────────────────────────────────
    private class FullscreenChrome extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (customView != null) { callback.onCustomViewHidden(); return; }
            customView = view;
            customCallback = callback;
            root.addView(customView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            customView.bringToFront();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            applyImmersive(true);
        }

        @Override
        public void onHideCustomView() {
            if (customView == null) return;
            root.removeView(customView);
            customView = null;
            if (customCallback != null) customCallback.onCustomViewHidden();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            applyImmersive(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void applyImmersive(boolean on) {
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(on
                ? View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                : View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (customView != null) { web.getWebChromeClient().onHideCustomView(); return; }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override protected void onPause() {
        super.onPause();
        if (web != null) web.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
    }

    @Override protected void onDestroy() {
        if (web != null) {
            web.loadUrl("about:blank");
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
