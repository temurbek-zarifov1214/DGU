package com.example.manualapp.maruza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.manualapp.R;
import com.example.manualapp.SubjectModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class ShowItems extends AppCompatActivity {
    ArrayList<SubjectModel> list;
    SubjectAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        if (getSupportActionBar() != null)
        {
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


        list.add(new SubjectModel("1-Maruza"));
        list.add(new SubjectModel("2-Maruza"));
        list.add(new SubjectModel("3-Maruza"));
        list.add(new SubjectModel("4-Maruza"));
        list.add(new SubjectModel("5-Maruza"));
        list.add(new SubjectModel("6-Maruza"));
        list.add(new SubjectModel("7-Maruza"));
        list.add(new SubjectModel("8-Maruza"));


        adapter = new SubjectAdapter(this, list);

        recyclerView.setAdapter(adapter);

        // Set the manual name to the TextView in the Toolbar
        TextView textView = findViewById(R.id.imageForList);
        textView.setText("Maruzadan matnlarssi");
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("Maruzadan matnlari");
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