package com.example.manualapp.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Heritage loader — an 8-point star that draws itself stroke-by-stroke in
 * turquoise with a trailing gold pass. Ported from the design's StarLoader.
 */
public class StarLoaderView extends View {

    private static final int BORDER = 0xFF2E3D5C;
    private static final int TURQ   = 0xFF3EC6C0;
    private static final int GOLD   = 0xFFD9A441;

    private final Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint turq = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gold = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path star = new Path();
    private final Path seg  = new Path();
    private final PathMeasure pm = new PathMeasure();

    private float density;
    private float fraction = 0f;
    private ValueAnimator animator;

    public StarLoaderView(Context c) { super(c); init(); }
    public StarLoaderView(Context c, AttributeSet a) { super(c, a); init(); }
    public StarLoaderView(Context c, AttributeSet a, int s) { super(c, a, s); init(); }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        for (Paint p : new Paint[]{base, turq, gold}) {
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeJoin(Paint.Join.ROUND);
        }
        base.setColor(BORDER); base.setAlpha(102); base.setStrokeWidth(1.3f * density);
        turq.setColor(TURQ);   turq.setStrokeWidth(1.6f * density);
        gold.setColor(GOLD);   gold.setStrokeWidth(1.2f * density); gold.setAlpha(180);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        float c = Math.min(w, h) / 2f;
        buildStar(star, c, c, 8, c * 0.92f, c * 0.4f);
        pm.setPath(star, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(star, base);
        float len = pm.getLength();
        if (len <= 0) return;

        drawPartial(canvas, len, fraction, turq);
        // gold pass trails slightly behind
        float gf = Math.max(0f, fraction - 0.12f);
        drawPartial(canvas, len, gf, gold);
    }

    private void drawPartial(Canvas canvas, float len, float frac, Paint p) {
        seg.reset();
        pm.getSegment(0, len * frac, seg, true);
        canvas.drawPath(seg, p);
    }

    private void buildStar(Path path, float cx, float cy, int points, float R, float r) {
        path.reset();
        float phase = (float) (-Math.PI / 2);
        for (int i = 0; i < points * 2; i++) {
            float a = phase + i * (float) Math.PI / points;
            float rad = (i % 2 == 1) ? r : R;
            float x = cx + rad * (float) Math.cos(a);
            float y = cy + rad * (float) Math.sin(a);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
    }

    public void start() {
        if (animator != null) return;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1900);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(a -> { fraction = (float) a.getAnimatedValue(); invalidate(); });
        animator.start();
    }

    public void stop() {
        if (animator != null) { animator.cancel(); animator = null; }
    }

    @Override
    protected void onAttachedToWindow() { super.onAttachedToWindow(); start(); }

    @Override
    protected void onDetachedFromWindow() { super.onDetachedFromWindow(); stop(); }
}
