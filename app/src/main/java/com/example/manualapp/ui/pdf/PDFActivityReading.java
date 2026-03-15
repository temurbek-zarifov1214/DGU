package com.example.manualapp.ui.pdf;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.manualapp.R;
import com.example.manualapp.ui.particle.ParticleView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class PDFActivityReading extends AppCompatActivity {

    private int currentPage = 0;
    private int totalPages  = 1;
    private PDFView pdfView;
    private TextView tvPageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_pdfreading);

        String assetPath = getIntent().getStringExtra("assetPath");
        String fileName  = getIntent().getStringExtra("fileName");

        // Display name in navbar
        String displayName = (fileName != null) ? fileName : "PDF";
        // Remove extension for display
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

        // Prev / Next buttons
        ((ImageButton) findViewById(R.id.btnPrev)).setOnClickListener(v -> {
            if (currentPage > 1) pdfView.jumpTo(currentPage - 2, true);
        });
        ((ImageButton) findViewById(R.id.btnNext)).setOnClickListener(v -> {
            if (currentPage < totalPages) pdfView.jumpTo(currentPage, true);
        });
    }

    @Override
    public void onBackPressed() {
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
}
