package com.example.manualapp.ui.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.SubjectModel;
import com.example.manualapp.ui.pdf.PDFActivityReading;

import java.util.List;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolder> {

    private final Context context;
    private final List<SubjectModel> list;
    private final ContentType contentType;

    public SubjectListAdapter(Context context, List<SubjectModel> list, ContentType contentType) {
        this.context = context;
        this.list = list;
        this.contentType = contentType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SubjectModel model = list.get(position);
        holder.subjectName.setText(model.getSubjectName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PDFActivityReading.class);
            intent.putExtra(ContentType.KEY, contentType);
            intent.putExtra("name", model.getSubjectName());
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView subjectName;

        ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.chapName);
        }
    }
}
