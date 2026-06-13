package com.example.manualapp.ui.pdf;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

/**
 * Heritage manuscript reader — gold reading-progress bar, dark/light reading
 * mode toggle, and a floating page-nav pill over the lacquer background.
 */
public class PDFActivityReading extends AppCompatActivity {

    private int currentPage = 0;
    private int totalPages  = 1;
    private boolean darkMode = false;
    private String assetPath;

    private PDFView  pdfView;
    private TextView tvPageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#0E1626"));
        window.setNavigationBarColor(Color.parseColor("#0E1626"));

        setContentView(R.layout.activity_pdfreading);

        assetPath = getIntent().getStringExtra("assetPath");
        String fileName = getIntent().getStringExtra("fileName");
        String subtitle = getIntent().getStringExtra("subtitle");

        String displayName = (fileName != null) ? fileName : "PDF";
        ((TextView) findViewById(R.id.tvTitle)).setText(displayName);
        TextView sub = findViewById(R.id.tvSubLabel);
        sub.setText(subtitle != null ? subtitle.toUpperCase() : "PDF HUJJAT");

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton btnTheme = findViewById(R.id.btnTheme);
        btnTheme.setOnClickListener(v -> {
            darkMode = !darkMode;
            btnTheme.setImageResource(darkMode ? R.drawable.nq_sun : R.drawable.nq_moon);
            loadPdf(currentPage > 0 ? currentPage - 1 : 0);
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, displayName + " — Amaliy bezaklar sanati");
            startActivity(Intent.createChooser(share, "Ulashish"));
        });

        tvPageInfo = findViewById(R.id.tvPageInfo);
        pdfView = findViewById(R.id.pdfView);
        loadPdf(0);

        findViewById(R.id.btnPrev).setOnClickListener(v -> {
            if (currentPage > 1) pdfView.jumpTo(currentPage - 2, true);
        });
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentPage < totalPages) pdfView.jumpTo(currentPage, true);
        });
    }

    private void loadPdf(int defaultPage) {
        if (assetPath == null) return;
        pdfView.setBackgroundColor(Color.parseColor(darkMode ? "#131C30" : "#F7F3EA"));
        pdfView.fromAsset(assetPath)
                .defaultPage(defaultPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .nightMode(darkMode)
                .enableAnnotationRendering(false)
                .scrollHandle(new DefaultScrollHandle(this))
                .onPageChange((page, count) -> {
                    currentPage = page + 1;
                    totalPages  = count;
                    tvPageInfo.setText(currentPage + " / " + totalPages);
                    setProgress(count > 0 ? (float) currentPage / count : 0f);
                })
                .onError(t -> { /* silently ignore */ })
                .load();
    }

    private void setProgress(float frac) {
        frac = Math.max(0.02f, Math.min(1f, frac));
        View fill = findViewById(R.id.progressFill);
        View rest = findViewById(R.id.progressRest);
        ((LinearLayout.LayoutParams) fill.getLayoutParams()).weight = frac;
        ((LinearLayout.LayoutParams) rest.getLayoutParams()).weight = 1f - frac;
        fill.requestLayout();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
