package com.example.manualapp.adabiyot;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.manualapp.R;
import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivityReading5 extends AppCompatActivity {

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

        int postion = getIntent().getIntExtra("position", 0);
        String chapter = getIntent().getStringExtra("name");

        if (postion==0)
        {
            pdfView.fromAsset("qollamma/Янги дарслик Валеология 2021.pdf").load();
            chaptername.setText(chapter);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
