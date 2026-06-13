package com.example.manualapp.ui.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.R;
import com.example.manualapp.data.ImageLoader;
import com.example.manualapp.data.VideoRepository;
import com.example.manualapp.data.YoutubeMeta;
import com.example.manualapp.domain.VideoLesson;

import java.net.URLEncoder;

/**
 * Online video lesson screen — a 16:9 in-app YouTube player on top, then the
 * real title, channel, and description fetched from YouTube. Online-only; no
 * download. Embedding-disabled videos open via the "YouTube ilovasida ochish"
 * button.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO = "video";

    private static final int TURQ = Color.parseColor("#3EC6C0");
    private static final int BG    = Color.parseColor("#0E1626");

    private VideoLesson video;
    private WebView web;
    private FrameLayout root;
    private boolean descOpen = true;
    private boolean fellBack = false;   // guards the embed→watch-page fallback

    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(BG);

        setContentView(R.layout.activity_video_player);

        video = (VideoLesson) getIntent().getSerializableExtra(EXTRA_VIDEO);
        if (video == null) { finish(); return; }

        root = findViewById(R.id.playerRoot);

        // Size the player to a real 16:9 of the screen width (big, like YouTube)
        FrameLayout playerFrame = findViewById(R.id.playerFrame);
        int screenW = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams lp = playerFrame.getLayoutParams();
        lp.height = Math.round(screenW * 9f / 16f);
        playerFrame.setLayoutParams(lp);

        // Thumbnail behind the play button
        ImageLoader.load(video.thumbUrl(), findViewById(R.id.overlayThumb));

        TextView titleView = findViewById(R.id.tvVideoTitle);
        TextView metaView  = findViewById(R.id.tvMeta);
        TextView descView  = findViewById(R.id.tvDesc);
        titleView.setText(video.title);
        metaView.setText(video.topic);
        if (video.description != null && !video.description.isEmpty()) descView.setText(video.description);

        web = findViewById(R.id.playerWeb);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        // Chrome-mobile UA so the watch-page fallback serves a playable page.
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 12; Pixel 5) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36");
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new FullscreenChrome());
        // JS calls this when the IFrame player errors (e.g. 101/150 = embedding
        // disabled) → fall back to the real watch page, which still plays.
        web.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onError(int code) { runOnUiThread(() -> fallbackToWatch()); }
        }, "AndroidVP");

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnPlay).setOnClickListener(v -> play());
        findViewById(R.id.btnOpenYoutube).setOnClickListener(v -> openExternal());
        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            findViewById(R.id.offlineOverlay).setVisibility(View.GONE);
            play();
        });
        findViewById(R.id.descHeader).setOnClickListener(v -> toggleDesc());

        // Fetch real YouTube title / channel / description
        YoutubeMeta.fetch(video, m -> {
            if (m.title != null) { video.title = m.title; titleView.setText(m.title); }
            if (m.author != null) metaView.setText(m.author);
            if (m.description != null) { video.description = m.description; descView.setText(m.description); }
            if (m.any()) VideoRepository.updateMeta(this, video.id, m.title, m.description);
        });
    }

    // ── Playback (clean 16:9 IFrame embed, auto-fallback to watch page) ─────────
    private void play() {
        if (!isOnline()) {
            findViewById(R.id.offlineOverlay).setVisibility(View.VISIBLE);
            return;
        }
        fellBack = false;
        findViewById(R.id.offlineOverlay).setVisibility(View.GONE);
        findViewById(R.id.playerOverlay).setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);

        if (video.youtubeId != null && !video.youtubeId.isEmpty()) {
            web.loadDataWithBaseURL("https://www.youtube.com", iframeHtml(video.youtubeId),
                    "text/html", "utf-8", null);
        } else if (video.url != null && !video.url.isEmpty()) {
            web.loadUrl(video.url);
        } else {
            String q = (video.query != null && !video.query.isEmpty()) ? video.query : video.title;
            try { q = URLEncoder.encode(q, "UTF-8"); } catch (Exception ignored) {}
            web.loadUrl("https://m.youtube.com/results?search_query=" + q);
        }
    }

    /** Embed failed (embedding disabled) → load the real watch page, which plays. */
    private void fallbackToWatch() {
        if (fellBack || web == null) return;
        fellBack = true;
        if (video.youtubeId != null && !video.youtubeId.isEmpty()) {
            web.loadUrl("https://m.youtube.com/watch?v=" + video.youtubeId);
        } else if (video.url != null && !video.url.isEmpty()) {
            web.loadUrl(video.url);
        }
    }

    /** IFrame Player API page — reports errors (101/150 = embed disabled) to Android. */
    private String iframeHtml(String id) {
        return "<!DOCTYPE html><html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>html,body{margin:0;height:100%;background:#000;overflow:hidden}"
                + "#p{width:100%;height:100%}</style></head><body><div id='p'></div>"
                + "<script>var t=document.createElement('script');"
                + "t.src='https://www.youtube.com/iframe_api';document.head.appendChild(t);"
                + "var player;function onYouTubeIframeAPIReady(){player=new YT.Player('p',{"
                + "videoId:'" + id + "',width:'100%',height:'100%',"
                + "playerVars:{playsinline:1,rel:0,autoplay:1,fs:1,modestbranding:1},"
                + "events:{onError:function(e){if(window.AndroidVP)AndroidVP.onError(e.data);}}});}"
                + "</script></body></html>";
    }

    /** Open the video in the external YouTube app / browser — guaranteed playback. */
    private void openExternal() {
        String url;
        if (video.youtubeId != null && !video.youtubeId.isEmpty()) {
            url = "https://www.youtube.com/watch?v=" + video.youtubeId;
        } else if (video.url != null && !video.url.isEmpty()) {
            url = video.url;
        } else {
            String q = (video.query != null && !video.query.isEmpty()) ? video.query : video.title;
            try { q = URLEncoder.encode(q, "UTF-8"); } catch (Exception ignored) {}
            url = "https://m.youtube.com/results?search_query=" + q;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "YouTube ilovasi topilmadi", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleDesc() {
        descOpen = !descOpen;
        findViewById(R.id.tvDesc).setVisibility(descOpen ? View.VISIBLE : View.GONE);
        findViewById(R.id.descChevron).setRotation(descOpen ? 0f : 180f);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
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
