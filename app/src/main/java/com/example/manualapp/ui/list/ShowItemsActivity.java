package com.example.manualapp.ui.list;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.pdf.PDFActivityReading;
import com.example.manualapp.SubjectModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);
            getSupportActionBar().setTitle("");
        }

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ContentType contentType = (ContentType) getIntent().getSerializableExtra(ContentType.KEY);
        if (contentType == null) {
            finish();
            return;
        }

        List<SubjectModel> list = new ArrayList<>();
        for (String name : contentType.getItemNames()) {
            list.add(new SubjectModel(name));
        }

        SubjectListAdapter adapter = new SubjectListAdapter(this, list, contentType);
        recyclerView.setAdapter(adapter);

        TextView headerText = findViewById(R.id.imageForList);
        headerText.setText(contentType.getScreenTitle());

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(contentType.getToolbarTitle());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
