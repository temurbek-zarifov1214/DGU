package com.example.manualapp.ui.pdf;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.R;
import com.example.manualapp.ui.particle.ParticleView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class PDFActivityReading extends AppCompatActivity {

    private int currentPage = 0;
    private int totalPages  = 1;
    private PDFView      pdfView;
    private TextView     tvPageInfo;
    private ParticleView particleView;

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

        setContentView(R.layout.activity_pdfreading);

        particleView = findViewById(R.id.particleView);

        String assetPath  = getIntent().getStringExtra("assetPath");
        String fileName   = getIntent().getStringExtra("fileName");

        // Display name (strip extension)
        String displayName = (fileName != null) ? fileName : "PDF";
        int dotIdx = displayName.lastIndexOf('.');
        if (dotIdx > 0) displayName = displayName.substring(0, dotIdx);
        ((TextView) findViewById(R.id.tvTitle)).setText(displayName + ".pdf");

        // Back button
        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Page info
        tvPageInfo = findViewById(R.id.tvPageInfo);
        tvPageInfo.setText("1 / ? bet");

        // PDF viewer
        pdfView = findViewById(R.id.pdfView);

        if (assetPath != null) {
            pdfView.fromAsset(assetPath)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(false)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .onPageChange((page, count) -> {
                        currentPage = page + 1;
                        totalPages  = count;
                        tvPageInfo.setText(currentPage + " / " + totalPages + " bet");
                    })
                    .onError(t -> { /* silently ignore */ })
                    .load();
        }

        // Prev / Next
        ((ImageButton) findViewById(R.id.btnPrev)).setOnClickListener(v -> {
            if (currentPage > 1) pdfView.jumpTo(currentPage - 2, true);
        });
        ((ImageButton) findViewById(R.id.btnNext)).setOnClickListener(v -> {
            if (currentPage < totalPages) pdfView.jumpTo(currentPage, true);
        });
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

    @Override
    public void onBackPressed() {
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
}
