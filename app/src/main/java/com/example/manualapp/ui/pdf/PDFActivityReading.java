package com.example.manualapp.ui.pdf;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivityReading extends AppCompatActivity {

    private PDFView pdfView;
    private TextView chapterNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreading);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);
            getSupportActionBar().setTitle("");
        }

        pdfView = findViewById(R.id.pdfView);
        chapterNameView = findViewById(R.id.chapterNames);

        ContentType contentType = (ContentType) getIntent().getSerializableExtra(ContentType.KEY);
        if (contentType != null) {
            int position = getIntent().getIntExtra("position", 0);
            String name = getIntent().getStringExtra("name");
            chapterNameView.setText(name != null ? name : "");
            loadPdfFromAsset(contentType.getPdfPath(position));
        } else {
            String pdfFileName = getIntent().getStringExtra("pdfFileName");
            String chapterName = getIntent().getStringExtra("chapterName");
            chapterNameView.setText(chapterName != null ? chapterName : "");
            if (pdfFileName != null) {
                loadPdfFromAsset(pdfFileName);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPdfFromAsset(String path) {
        pdfView.fromAsset(path).load();
    }
}
