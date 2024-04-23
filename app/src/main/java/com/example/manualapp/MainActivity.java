package com.example.manualapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.manualapp.adabiyot.ShowItems5;
import com.example.manualapp.amaliyot.ShowItems4;
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
//        adabiyot
        ImageView imageView4 = findViewById(R.id.image7);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chapterName = "Chapter 1"; // Replace with the actual chapter name
                navigateToOtherPageAdabiyot3(chapterName);
            }
        });
        //Ishchi oquv dastur for ImageView image6
        imageView = findViewById(R.id.image6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("Ishchi o’quv fan dastur .pdf", "Ishchi o'quv fan dasturi");
            }
        });
        //       Glosary for ImageView imageGlos
        imageView = findViewById(R.id.imageGlos);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("GLOSARIY.pdf", "Glosary");
            }
        });


//      Uquv fan dasturi for imageView  imageUq
        imageView = findViewById(R.id.imageUq);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("O'quv dasturi.pdf", "O'quv fan dasturi");
            }
        });
//     Mustaqil talim for imageView  imageMus
        imageView = findViewById(R.id.imageMus);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("MUSTAQIL TA’LIM.pdf", "Mustaqil ta'lim");
            }
        });

        //     Tarqatma materiallar for imageView  imageTar
        imageView = findViewById(R.id.imageTar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("TARQATMA MATERIALLAR.pdf", "Tarqatma materiallar");
            }
        });
        //     Baholash for imageView  imageBaholash
        imageView = findViewById(R.id.imageBaholash);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("Baholash va nazorat qilish mezonlari..pdf", "Baholash mezonlari");
            }
        });
        //     Oraliq for imageView  imageOral
        imageView = findViewById(R.id.imageOral);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("Valeologiya asoslari fanidan oraliq nazorat savollari.pdf", "Oraliq nazorat savollari");
            }
        });
        //     Test for imageView  imageTestQueiz
        imageView = findViewById(R.id.imageTestQueiz);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfActivity("test.pdf", "Test savollar");
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