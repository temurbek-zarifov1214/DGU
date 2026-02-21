package com.example.manualapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.list.ShowItemsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindCard(R.id.card1, ContentType.MARUZA);
        bindCard(R.id.card2, ContentType.AMALIYOT);
        bindCard(R.id.card3, ContentType.TARQATMA);
        bindCard(R.id.card4, ContentType.GLOSSARY);
        bindCard(R.id.card5, ContentType.ORALIQ);
        bindCard(R.id.card6, ContentType.YAKUNIY);
        bindCard(R.id.card7, ContentType.DGU);
        bindCard(R.id.card8, ContentType.MALUMOTNOMA);
    }

    private void bindCard(int cardId, ContentType contentType) {
        View card = findViewById(cardId);
        card.setOnClickListener(v -> navigateTo(contentType));
    }

    private void navigateTo(ContentType contentType) {
        Intent intent = new Intent(this, ShowItemsActivity.class);
        intent.putExtra(ContentType.KEY, contentType);
        startActivity(intent);
    }
}
