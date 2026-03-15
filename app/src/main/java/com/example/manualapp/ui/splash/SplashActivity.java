package com.example.manualapp.ui.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.MainActivity;
import com.example.manualapp.R;
import com.example.manualapp.ui.particle.ParticleView;

public class SplashActivity extends AppCompatActivity {

    private ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge: transparent status + nav bars
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#071535"));
        window.setNavigationBarColor(Color.parseColor("#071535"));

        // Hide action bar for full-screen splash
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        particleView = findViewById(R.id.splashParticleView);
        SplashRingView ringView   = findViewById(R.id.splashRingView);
        ImageView      icon       = findViewById(R.id.splashIcon);
        TextView       title      = findViewById(R.id.splashTitle);
        TextView       sub        = findViewById(R.id.splashSub);
        ProgressBar    progress   = findViewById(R.id.splashProgress);

        Handler h = new Handler(Looper.getMainLooper());

        // ── Phase 1 (0 ms): Icon pops in with overshoot ────────────────────────
        icon.setScaleX(0f);
        icon.setScaleY(0f);
        icon.setAlpha(0f);
        icon.animate()
                .scaleX(1.15f).scaleY(1.15f).alpha(1f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator(2.0f))
                .start();

        // ── Phase 2 (400 ms): Icon settles + ring burst ─────────────────────────
        h.postDelayed(() -> {
            icon.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
            float iconR = icon.getWidth() / 2f;
            ringView.startRingAnim(Math.max(iconR, 44f));
        }, 400);

        // ── Phase 3 (600 ms): Title slides up ───────────────────────────────────
        title.setTranslationY(30f);
        title.animate()
                .translationY(0f).alpha(1f)
                .setDuration(350)
                .setStartDelay(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // ── Phase 4 (900 ms): Subtitle + spinner ────────────────────────────────
        sub.animate().alpha(1f).setDuration(300).setStartDelay(900).start();
        progress.animate().alpha(1f).setDuration(300).setStartDelay(1000).start();

        // ── Phase 5 (1800 ms): Navigate to MainActivity ─────────────────────────
        h.postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
            finish();
        }, 1800);
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
}
