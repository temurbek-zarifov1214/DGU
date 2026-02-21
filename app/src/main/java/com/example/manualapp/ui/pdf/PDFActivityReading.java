package com.example.manualapp.ui.pdf;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.util.MovingBackgroundHelper;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFActivityReading extends AppCompatActivity {

    private PDFView pdfView;
    private TextView chapterNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreading);

        MovingBackgroundHelper.startMovingBackground(this, findViewById(R.id.movingBackground));

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

        String customFilePath = getIntent().getStringExtra("customFilePath");
        if (customFilePath != null && !customFilePath.isEmpty()) {
            String name = getIntent().getStringExtra("name");
            chapterNameView.setText(name != null ? name : "");
            File file = new File(customFilePath);
            if (file.exists()) {
                pdfView.fromFile(file).load();
            } else {
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        MovingBackgroundHelper.stopMovingBackground(this);
    }
}
