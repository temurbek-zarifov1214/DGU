package com.example.manualapp.ui.particle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;

import com.example.manualapp.R;

/**
 * Heritage girih-dome background.
 *
 * Draws a 12-fold "dome ceiling" — radial spokes, concentric 12-point star
 * polygons and rings in turquoise + gold over the lacquer background — ported
 * from the Naqqoshlik design system's GirihDome. Edges fade into the background
 * via a radial vignette.
 *
 * The class name and lifecycle surface (resume/pause/setPointerPosition/
 * clearPointer/startAnimation/stopAnimation) are preserved so existing layouts
 * and activities keep working unchanged.
 */
public class ParticleView extends View {

    // Heritage palette
    private static final int BG    = 0xFF0E1626;
    private static final int TURQ  = 0xFF3EC6C0;
    private static final int GOLD  = 0xFFD9A441;

    // Configurable dome geometry
    private float domeSizeDp = 480f;
    private float cxFraction = 0.5f;
    private float cyFraction = 0.42f;
    private float baseOpacity = 1f;
    private boolean animated = false;

    private float density = 1f;
    private float rotation = 0f;

    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint vignette = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path tmp = new Path();

    private boolean running = false;
    private final Choreographer.FrameCallback frameCallback = frameTimeNanos -> {
        if (running) {
            rotation += 0.04f;            // very slow drift
            if (rotation > 360f) rotation -= 360f;
            invalidate();
            Choreographer.getInstance().postFrameCallback(this.frameCallback);
        }
    };

    public ParticleView(Context c) { super(c); init(c, null); }
    public ParticleView(Context c, AttributeSet a) { super(c, a); init(c, a); }
    public ParticleView(Context c, AttributeSet a, int s) { super(c, a, s); init(c, a); }

    private void init(Context c, AttributeSet a) {
        setWillNotDraw(false);
        density = getResources().getDisplayMetrics().density;
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeCap(Paint.Cap.ROUND);
        fill.setStyle(Paint.Style.FILL);

        if (a != null) {
            TypedArray t = c.obtainStyledAttributes(a, R.styleable.ParticleView);
            domeSizeDp  = t.getDimension(R.styleable.ParticleView_domeSize, domeSizeDp * density) / density;
            cxFraction  = t.getFloat(R.styleable.ParticleView_cxFraction, cxFraction);
            cyFraction  = t.getFloat(R.styleable.ParticleView_cyFraction, cyFraction);
            baseOpacity = t.getFloat(R.styleable.ParticleView_domeOpacity, baseOpacity);
            animated    = t.getBoolean(R.styleable.ParticleView_domeAnimated, animated);
            t.recycle();
        }
    }

    // ── geometry helpers ──────────────────────────────────────────────────────
    private void starPath(Path p, float cx, float cy, int points, float R, float r, float phase) {
        p.reset();
        for (int i = 0; i < points * 2; i++) {
            float angle = phase + i * (float) Math.PI / points;
            float rad = (i % 2 == 1) ? r : R;
            float x = cx + rad * (float) Math.cos(angle);
            float y = cy + rad * (float) Math.sin(angle);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.close();
    }

    private void ringPath(Path p, float cx, float cy, int n, float R, float phase) {
        p.reset();
        for (int i = 0; i < n; i++) {
            float angle = phase + i * 2f * (float) Math.PI / n;
            float x = cx + R * (float) Math.cos(angle);
            float y = cy + R * (float) Math.sin(angle);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.close();
    }

    private static int withAlpha(int color, float a) {
        a = Math.max(0f, Math.min(1f, a));
        return (color & 0x00FFFFFF) | ((int) (a * 255) << 24);
    }

    // ── draw ──────────────────────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        float size = domeSizeDp * density;
        float c = size / 2f;
        float cx = w * cxFraction;
        float cy = h * cyFraction;
        float HALF_PI = (float) (Math.PI / 2);

        canvas.save();
        canvas.translate(cx, cy);
        if (animated) canvas.rotate(rotation);
        canvas.translate(-c, -c);   // local space where dome center is at (c,c)

        // spokes (24)
        stroke.setStrokeWidth(1f * density);
        stroke.setColor(withAlpha(TURQ, 0.09f * baseOpacity));
        for (int i = 0; i < 24; i++) {
            float angle = i * (float) Math.PI / 12f;
            canvas.drawLine(c, c, c + size * 0.46f * (float) Math.cos(angle),
                    c + size * 0.46f * (float) Math.sin(angle), stroke);
        }

        float[] radiiF = {0.16f, 0.27f, 0.39f, 0.52f, 0.66f, 0.80f};
        float[] radii = new float[radiiF.length];
        for (int i = 0; i < radii.length; i++) radii[i] = radiiF[i] * c;

        // rings (skip innermost — matches design slice(1))
        stroke.setStrokeWidth(0.75f * density);
        for (int idx = 0; idx < radii.length - 1; idx++) {
            ringPath(tmp, c, c, 12, radii[idx + 1], -HALF_PI);
            stroke.setColor(withAlpha(idx % 2 == 1 ? TURQ : GOLD, 0.16f * baseOpacity));
            canvas.drawPath(tmp, stroke);
        }

        // star polygons
        stroke.setStrokeWidth(1f * density);
        for (int idx = 0; idx < radii.length; idx++) {
            float R = radii[idx];
            float phase = -HALF_PI + (idx % 2 == 1 ? (float) Math.PI / 12f : 0f);
            starPath(tmp, c, c, 12, R, R * 0.6f, phase);
            float op = Math.max(0.12f, 0.5f - idx * 0.05f) * baseOpacity;
            stroke.setColor(withAlpha(idx % 2 == 1 ? GOLD : TURQ, op));
            canvas.drawPath(tmp, stroke);
        }

        // centre gold star
        starPath(tmp, c, c, 8, c * 0.12f, c * 0.05f, -HALF_PI);
        fill.setColor(withAlpha(GOLD, 0.55f * baseOpacity));
        canvas.drawPath(tmp, fill);

        canvas.restore();

        // edge vignette — fade dome into the lacquer background
        float vr = size * 0.62f;
        vignette.setShader(new RadialGradient(cx, cy, Math.max(vr, 1f),
                new int[]{0x000E1626, 0x000E1626, BG},
                new float[]{0f, 0.62f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, vignette);
        vignette.setShader(null);
    }

    // ── lifecycle (preserved surface) ──────────────────────────────────────────
    public void resume() {
        if (animated && !running) {
            running = true;
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    public void pause() {
        running = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    public void startAnimation() { resume(); }
    public void stopAnimation()  { pause();  }

    // No-op touch hooks kept so host activities compile unchanged.
    public void setPointerPosition(float x, float y) { }
    public void clearPointer() { }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resume();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
    }
}
