package com.example.manualapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.manualapp.dgu.ShowItems6;
import com.example.manualapp.glossary.ShowItems5;
import com.example.manualapp.amaliyot.ShowItems4;
import com.example.manualapp.malumotnoma.ShowItems7;
import com.example.manualapp.oraliqtest.ShowItems8;
import com.example.manualapp.tarqatma.ShowItems3;
import com.example.manualapp.maruza.ShowItems;
import com.example.manualapp.yakuniytest.ShowItems9;

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
        // Tarqatma material
        ImageView imageView3 = findViewById(R.id.image5);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOtherPageLaboratoriya(chapterName);
            }
        });
//        Glossary
        ImageView imageView4 = findViewById(R.id.image7);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToGlossary3(chapterName);
            }
        });

//     Oraliq nazorat for imageView  imageMus
        ImageView oraliqnazorat = findViewById(R.id.imageMus);
        oraliqnazorat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOraliqNazorat(chapterName);
            }
        });
//      yakuniy nazorat for imageView  imageMus
        ImageView yakuniyNazorat = findViewById(R.id.imageTar);
        yakuniyNazorat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToYakuniyNazorat(chapterName);
            }
        });
//     DGU for imageView  imageTestQueiz
        ImageView dgu = findViewById(R.id.imageTestQueiz);
        dgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToDGU(chapterName);
            }
        });
//     maulmotnoma for imageView  imageTestQueiz
        ImageView malumotnoma = findViewById(R.id.imageObyekt);
        malumotnoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToMalumotnoma(chapterName);
            }
        });
//        //     Obyektivka for imageView  imageObyekt
//        imageView = findViewById(R.id.imageObyekt);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPdfActivity("obyektivka.pdf", "Dastur mualiflari");
//            }
//        });

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

    private void navigateToGlossary3(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems5.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }
    private void navigateToOraliqNazorat(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems8.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }
    private void navigateToYakuniyNazorat(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems9.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }
    private void navigateToDGU(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems6.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }
    private void navigateToMalumotnoma(String chapterName) {
        Intent intent = new Intent(MainActivity.this, ShowItems7.class);
        intent.putExtra("name", chapterName);
        startActivity(intent);
    }


}