package com.example.manualapp.ui.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manualapp.MainActivity;
import com.example.manualapp.R;
import com.example.manualapp.domain.ContentType;
import com.example.manualapp.ui.list.ShowItemsActivity;
import com.example.manualapp.ui.video.VideoListActivity;

/**
 * Wires the shared floating bottom nav (view_floating_nav.xml): highlights the
 * active tab in turquoise and routes the other two. activeIndex: 0=Home,
 * 1=Video, 2=Ro'yxat.
 */
public final class HeritageNav {

    private static final int TURQ = Color.parseColor("#3EC6C0");
    private static final int TEXT2 = Color.parseColor("#9AA5B8");

    private HeritageNav() {}

    public static void setup(Activity a, int activeIndex) {
        bind(a, R.id.navHome,  R.id.navHomeIcon,  R.id.navHomeLabel,  activeIndex == 0);
        bind(a, R.id.navVideo, R.id.navVideoIcon, R.id.navVideoLabel, activeIndex == 1);
        bind(a, R.id.navList,  R.id.navListIcon,  R.id.navListLabel,  activeIndex == 2);

        a.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (activeIndex == 0) return;
            Intent i = new Intent(a, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            a.startActivity(i);
            a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        a.findViewById(R.id.navVideo).setOnClickListener(v -> {
            if (activeIndex == 1) return;
            a.startActivity(new Intent(a, VideoListActivity.class));
            a.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        a.findViewById(R.id.navList).setOnClickListener(v -> {
            if (activeIndex == 2) return;
            Intent i = new Intent(a, ShowItemsActivity.class);
            i.putExtra(ContentType.KEY, ContentType.USLUBLAR);
            a.startActivity(i);
            a.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private static void bind(Activity a, int rowId, int iconId, int labelId, boolean active) {
        int color = active ? TURQ : TEXT2;
        ((ImageView) a.findViewById(iconId)).setColorFilter(color);
        TextView label = a.findViewById(labelId);
        label.setTextColor(color);
        label.setTypeface(label.getTypeface(), active ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }
}
