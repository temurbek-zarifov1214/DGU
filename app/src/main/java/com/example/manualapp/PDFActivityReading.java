package com.example.manualapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivityReading extends AppCompatActivity {

    PDFView pdfView;
    TextView chaptername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreading);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);
        getSupportActionBar().setTitle("");



        pdfView = findViewById(R.id.pdfView);
        chaptername = findViewById(R.id.chapterNames);

        String pdfFileName = getIntent().getStringExtra("pdfFileName");
        String chapterName = getIntent().getStringExtra("chapterName");

        // Set chapter name in the TextView
        chaptername.setText(chapterName);

        // Load PDF based on filename
        loadPdfFromAsset(pdfFileName);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadPdfFromAsset(String pdfFileName) {
        pdfView.fromAsset(pdfFileName).load();
    }
}
