package com.example.manualapp.amaliyot;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.manualapp.R;
import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivityReading4 extends AppCompatActivity {

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
            pdfView.fromAsset("amaliyot/amaliy-1.pdf").load();
            chaptername.setText(chapter);
        }
        if (postion==1)
        {
            pdfView.fromAsset("amaliyot/amaliy-2.pdf").load();
            chaptername.setText(chapter);
        } if (postion==2)
        {
            pdfView.fromAsset("amaliyot/amaliy-3.pdf").load();
            chaptername.setText(chapter);
        }
        if (postion==3)
        {
            pdfView.fromAsset("amaliyot/amaliy-4.pdf").load();
            chaptername.setText(chapter);
        }
        if (postion==4)
        {
            pdfView.fromAsset("amaliyot/amaliy-5.pdf").load();
            chaptername.setText(chapter);
        } if (postion==5)
        {
            pdfView.fromAsset("amaliyot/amaliy-6.pdf").load();
            chaptername.setText(chapter);
        }
        if (postion==6)
        {
            pdfView.fromAsset("amaliyot/amaliy-7.pdf").load();
            chaptername.setText(chapter);
        }
        if (postion==7)
        {
            pdfView.fromAsset("amaliyot/amaliy-8.pdf").load();
            chaptername.setText(chapter);
        } if (postion==8)
        {
            pdfView.fromAsset("amaliyot/amaliy-9.pdf").load();
            chaptername.setText(chapter);
        } if (postion==9)
        {
            pdfView.fromAsset("amaliyot/amaliy-10.pdf").load();
            chaptername.setText(chapter);
        } if (postion==10)
        {
            pdfView.fromAsset("amaliyot/amaliy-11.pdf").load();
            chaptername.setText(chapter);
        } if (postion==11)
        {
            pdfView.fromAsset("amaliyot/amaliy-12.pdf").load();
            chaptername.setText(chapter);
        } if (postion==12)
        {
            pdfView.fromAsset("amaliyot/amaliy-13.pdf").load();
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
