package com.example.manualapp.ui.list;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.data.CustomSectionRepository;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.SubjectModel;
import com.example.manualapp.util.FileUtils;
import com.example.manualapp.util.MovingBackgroundHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PDF = 100;

    private ContentType contentType;
    private List<SubjectModel> list = new ArrayList<>();
    private SubjectListAdapter adapter;
    private CustomSectionRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        MovingBackgroundHelper.startMovingBackground(this, findViewById(R.id.movingBackground));

        repository = new CustomSectionRepository(this);

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

        contentType = (ContentType) getIntent().getSerializableExtra(ContentType.KEY);
        if (contentType == null) {
            finish();
            return;
        }

        buildList();
        adapter = new SubjectListAdapter(this, list, contentType);
        recyclerView.setAdapter(adapter);

        TextView headerText = findViewById(R.id.imageForList);
        headerText.setText(contentType.getScreenTitle());

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(contentType.getToolbarTitle());

        FloatingActionButton fab = findViewById(R.id.fabAddFile);
        fab.setOnClickListener(v -> openPdfPicker());
    }

    private void buildList() {
        list.clear();
        for (String name : contentType.getItemNames()) {
            list.add(new SubjectModel(name));
        }
        int defaultCount = list.size();
        List<CustomSectionRepository.CustomItem> customItems = repository.getCustomItems(contentType);
        for (int i = 0; i < customItems.size(); i++) {
            CustomSectionRepository.CustomItem item = customItems.get(i);
            String displayName = item.displayName != null && !item.displayName.isEmpty()
                    ? item.displayName
                    : getDefaultCustomName(defaultCount + i);
            list.add(new SubjectModel(displayName, item.localPath));
        }
    }

    private String getDefaultCustomName(int oneBasedIndex) {
        if (contentType == ContentType.MARUZA) {
            return oneBasedIndex + "-Maruza";
        }
        return getString(R.string.custom_item_name, oneBasedIndex);
    }

    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pdf)), REQUEST_PICK_PDF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_PDF || resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
        }
        try {
            String path = FileUtils.copyPdfToAppStorage(this, uri, contentType.name());
            int nextIndex = list.size() + 1;
            String displayName = contentType == ContentType.MARUZA ? (nextIndex + "-Maruza") : getString(R.string.custom_item_name, nextIndex);
            repository.addCustomItem(contentType, displayName, path);
            buildList();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, getString(R.string.file_added, displayName), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MovingBackgroundHelper.stopMovingBackground(this);
    }
}
