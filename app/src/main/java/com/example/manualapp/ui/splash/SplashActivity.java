package com.example.manualapp.ui.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.MainActivity;
import com.example.manualapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#0E1626"));
        window.setNavigationBarColor(Color.parseColor("#0E1626"));

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        ImageView icon  = findViewById(R.id.splashIcon);
        TextView  title = findViewById(R.id.splashTitle);
        TextView  sub   = findViewById(R.id.splashSub);
        android.view.View progress = findViewById(R.id.splashProgress);

        Handler h = new Handler(Looper.getMainLooper());

        // Phase 1 — rosette pops in with overshoot
        icon.setScaleX(0f);
        icon.setScaleY(0f);
        icon.animate()
                .scaleX(1.1f).scaleY(1.1f).alpha(1f)
                .setDuration(420)
                .setInterpolator(new OvershootInterpolator(2.0f))
                .start();

        // Phase 2 — settle
        h.postDelayed(() -> icon.animate().scaleX(1f).scaleY(1f).setDuration(200).start(), 420);

        // Phase 3 — title slides up
        title.setTranslationY(30f);
        title.animate()
                .translationY(0f).alpha(1f)
                .setDuration(350)
                .setStartDelay(560)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Phase 4 — subtitle + loader
        sub.animate().alpha(1f).setDuration(300).setStartDelay(820).start();
        progress.animate().alpha(1f).setDuration(300).setStartDelay(960).start();

        // Phase 5 — enter
        h.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
            finish();
        }, 1900);
    }
}
