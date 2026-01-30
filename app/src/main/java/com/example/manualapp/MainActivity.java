package com.example.manualapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.manualapp.adabiyot.ShowItems5;
import com.example.manualapp.amaliyot.ShowItems4;
import com.example.manualapp.labaratoriya.ShowItems3;
import com.example.manualapp.maruza.ShowItems;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        maruza
        ImageView imageView1 = findViewById(R.id.image1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Maruzadan mavzular"; // Replace with the actual chapter name
                navigateToOtherPageMaruza(chapterName);
            }
        });
// amaliyot
        ImageView imageView2 = findViewById(R.id.image3);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOtherPageAmaliyot(chapterName);
            }
        });
        // labarotaoriya
        ImageView imageView3 = findViewById(R.id.image5);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOtherPageLaboratoriya(chapterName);
            }
        });
//        adabiyot
        ImageView imageView4 = findViewById(R.id.image7);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOtherPageAdabiyot3(chapterName);
            }
        });
//        //Ishchi oquv dastur for ImageView image6
//        imageView = findViewById(R.id.image6);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPdfActivity("Ishchi o’quv fan dastur .pdf", "Ishchi o'quv fan dasturi");
//            }
//        });
//     Mustaqil talim for imageView  imageMus
        imageView = findViewById(R.id.imageMus);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("Gidravlikamasalalar.pdf", "Gidravlik masalalar");
            }
        });

        //     Tarqatma materiallar for imageView  imageTar
        imageView = findViewById(R.id.imageTar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("SILLABUSgidravlika.pdf", "SILLABUS");
            }
        });

        //     Test for imageView  imageTestQueiz
        imageView = findViewById(R.id.imageTestQueiz);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("Gidravlik_savollar.pdf", "Gidravlik savollar");
            }
        });
        //     Obyektivka for imageView  imageObyekt
        imageView = findViewById(R.id.imageObyekt);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("obyektivka.pdf", "Dastur mualiflari");
            }
        });

    }

    private void openPdfActivity(String pdfFileName, String chapterName) {
        Intent intent = new Intent(MainActivity.this, PDFActivityReading.class);
        intent.putExtra("pdfFileName", pdfFileName);
        intent.putExtra("chapterName", chapterName);
        startActivity(intent);
    }

    private void navigateToOtherPageMaruza(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }
    private void navigateToOtherPageLaboratoriya(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems3.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }

    private void navigateToOtherPageAmaliyot(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems4.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }

    private void navigateToOtherPageAdabiyot3(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems5.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }

}