package com.example.manualapp.adabiyot;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.maruza.SubjectAdapter;
import com.example.manualapp.SubjectModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class ShowItems5 extends AppCompatActivity {
    ArrayList<SubjectModel> list;
    SubjectAdapter5 adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);

// Enable the back button in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

// Set a custom icon for the back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);

// Set the title to an empty string or any desired title
        getSupportActionBar().setTitle("");

        recyclerView = findViewById(R.id.recycle);

        list = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);


        list.add(new SubjectModel("Янги дарслик Валеология 2021"));



        adapter = new SubjectAdapter5(this, list);

        recyclerView.setAdapter(adapter);

        // Set the manual name to the TextView in the Toolbar
//        TextView textView = findViewById(R.id.imageForList);
//        textView.setText("Adabiyotlar ro'yhati");
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("Adabiyotlar ro'yhati");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}