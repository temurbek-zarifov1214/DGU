package com.example.manualapp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Signature heritage divider — a 1px turquoise→gold gradient hairline with a
 * centred 8-point gold star. Ported from the design's GirihDivider.
 */
public class GirihDividerView extends View {

    private static final int TURQ = 0xFF3EC6C0;
    private static final int GOLD = 0xFFD9A441;

    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint star = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path starPath = new Path();
    private float density;

    public GirihDividerView(Context c) { super(c); init(); }
    public GirihDividerView(Context c, AttributeSet a) { super(c, a); init(); }
    public GirihDividerView(Context c, AttributeSet a, int s) { super(c, a, s); init(); }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        star.setStyle(Paint.Style.STROKE);
        star.setColor(GOLD);
        star.setStrokeWidth(1f * density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float cy = h / 2f;
        float starR = 6.5f * density;
        float gap = starR + 3f * density;
        float cx = w / 2f;

        // left gradient line (transparent → turq → gold)
        line.setStrokeWidth(1f * density);
        line.setShader(new LinearGradient(0, cy, cx - gap, cy,
                new int[]{Color.TRANSPARENT, withAlpha(TURQ, 0.7f), withAlpha(GOLD, 0.7f)},
                new float[]{0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawLine(0, cy, cx - gap, cy, line);

        // right gradient line (gold → turq → transparent)
        line.setShader(new LinearGradient(cx + gap, cy, w, cy,
                new int[]{withAlpha(GOLD, 0.7f), withAlpha(TURQ, 0.7f), Color.TRANSPARENT},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawLine(cx + gap, cy, w, cy, line);
        line.setShader(null);

        // centre 8-point star
        buildStar(starPath, cx, cy, 8, starR, 2.8f * density);
        canvas.drawPath(starPath, star);
    }

    private void buildStar(Path p, float cx, float cy, int points, float R, float r) {
        p.reset();
        float phase = (float) (-Math.PI / 2);
        for (int i = 0; i < points * 2; i++) {
            float a = phase + i * (float) Math.PI / points;
            float rad = (i % 2 == 1) ? r : R;
            float x = cx + rad * (float) Math.cos(a);
            float y = cy + rad * (float) Math.sin(a);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.close();
    }

    private static int withAlpha(int color, float a) {
        return (color & 0x00FFFFFF) | ((int) (a * 255) << 24);
    }
}
