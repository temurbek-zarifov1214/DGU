package com.example.manualapp.ui.particle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class ParticleView extends View {

    // ── Config matching super prompt spec ───────────────────────────────────────
    private static final int   PARTICLE_COUNT   = 55;
    private static final float CONNECTION_DIST  = 120f;  // px (scaled by density in onSizeChanged)
    private static final float REPEL_RADIUS     = 140f;
    private static final float REPEL_FORCE      = 2.2f;
    private static final int   SYMBOL_COUNT     = 10;
    private static final String[] SYMBOLS       = {"✦","◆","○","□","△","✧","◇"};

    // Particle colour palette (RGB)
    private static final int[][] PARTICLE_COLORS = {
            {30, 100, 220}, {60, 130, 255}, {100, 60, 200},
            {20, 180, 180}, {80, 160, 255}, {140, 80, 220}, {20, 120, 200}
    };

    // ── Data ────────────────────────────────────────────────────────────────────
    private float[] px, py, pvx, pvy, pRadius, pAlpha, pPulse, pPulseSpeed;
    private int[][] pColor;

    private float[] sx, sy, svy, sSize, sAlpha, sRotation, sRotSpeed;
    private String[] sSymbol;

    // ── Touch ────────────────────────────────────────────────────────────────────
    private float touchX = -1f, touchY = -1f;
    private float targetX = -1f, targetY = -1f;

    // ── Paint ────────────────────────────────────────────────────────────────────
    private final Paint paint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // ── Background gradient (set once on size change) ───────────────────────────
    private LinearGradient bgGradient;

    // ── Scale-adjusted radii ────────────────────────────────────────────────────
    private float connDist, repelRadius;

    // ── Handler animation loop ───────────────────────────────────────────────────
    private boolean running = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            update();
            invalidate();
            if (running) handler.postDelayed(this, 16L);
        }
    };

    private final Random rng = new Random();
    private boolean initialized = false;

    // ── Constructors ─────────────────────────────────────────────────────────────
    public ParticleView(Context context) { super(context); initPaint(); }
    public ParticleView(Context context, AttributeSet a) { super(context, a); initPaint(); }
    public ParticleView(Context context, AttributeSet a, int s) { super(context, a, s); initPaint(); }

    private void initPaint() {
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        isClickable();
    }

    // ── Size / init ──────────────────────────────────────────────────────────────
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        if (w == 0 || h == 0) return;

        float density = getResources().getDisplayMetrics().density;
        connDist    = CONNECTION_DIST * density;
        repelRadius = REPEL_RADIUS    * density;

        touchX  = w / 2f;  touchY  = h / 2f;
        targetX = w / 2f;  targetY = h / 2f;

        bgGradient = new LinearGradient(0, 0, w, h,
                new int[]{0xFF071535, 0xFF0D2560, 0xFF081840},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP);

        initParticles(w, h);
        initFloaters(w, h);
        initialized = true;
        startLoop();
    }

    private void initParticles(int w, int h) {
        px          = new float[PARTICLE_COUNT];
        py          = new float[PARTICLE_COUNT];
        pvx         = new float[PARTICLE_COUNT];
        pvy         = new float[PARTICLE_COUNT];
        pRadius     = new float[PARTICLE_COUNT];
        pAlpha      = new float[PARTICLE_COUNT];
        pPulse      = new float[PARTICLE_COUNT];
        pPulseSpeed = new float[PARTICLE_COUNT];
        pColor      = new int[PARTICLE_COUNT][];

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i]          = rng.nextFloat() * w;
            py[i]          = rng.nextFloat() * h;
            pvx[i]         = (rng.nextFloat() - 0.5f) * 0.7f;
            pvy[i]         = (rng.nextFloat() - 0.5f) * 0.7f;
            pRadius[i]     = 2f + rng.nextFloat() * 5f;
            pAlpha[i]      = 0.15f + rng.nextFloat() * 0.5f;
            pPulse[i]      = rng.nextFloat() * (float)(Math.PI * 2);
            pPulseSpeed[i] = 0.02f + rng.nextFloat() * 0.04f;
            pColor[i]      = PARTICLE_COLORS[i % PARTICLE_COLORS.length];
        }
    }

    private void initFloaters(int w, int h) {
        sx        = new float[SYMBOL_COUNT];
        sy        = new float[SYMBOL_COUNT];
        svy       = new float[SYMBOL_COUNT];
        sSize     = new float[SYMBOL_COUNT];
        sAlpha    = new float[SYMBOL_COUNT];
        sRotation = new float[SYMBOL_COUNT];
        sRotSpeed = new float[SYMBOL_COUNT];
        sSymbol   = new String[SYMBOL_COUNT];

        for (int i = 0; i < SYMBOL_COUNT; i++) {
            sx[i]        = rng.nextFloat() * w;
            sy[i]        = h + rng.nextFloat() * 120f;
            svy[i]       = -(0.6f + rng.nextFloat() * 1f);
            sSize[i]     = 16f + rng.nextFloat() * 20f;
            sAlpha[i]    = 0.1f + rng.nextFloat() * 0.25f;
            sRotation[i] = rng.nextFloat() * 360f;
            sRotSpeed[i] = (rng.nextFloat() - 0.5f) * 1.2f;
            sSymbol[i]   = SYMBOLS[i % SYMBOLS.length];
        }
    }

    // ── Loop ────────────────────────────────────────────────────────────────────
    public void startLoop() {
        if (!running) { running = true; handler.post(ticker); }
    }

    public void stopLoop() {
        running = false; handler.removeCallbacks(ticker);
    }

    @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); if (initialized) startLoop(); }
    @Override protected void onDetachedFromWindow() { super.onDetachedFromWindow(); stopLoop(); }

    // Keep these as public aliases for Activity lifecycle
    public void startAnimation() { startLoop(); }
    public void stopAnimation()  { stopLoop();  }

    // ── Update ───────────────────────────────────────────────────────────────────
    private void update() {
        if (!initialized) return;
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // Smooth pointer follow
        touchX += (targetX - touchX) * 0.06f;
        touchY += (targetY - touchY) * 0.06f;

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float dx = px[i] - touchX;
            float dy = py[i] - touchY;
            float d  = (float) Math.sqrt(dx * dx + dy * dy);
            if (d < repelRadius && d > 0) {
                float force = (repelRadius - d) / repelRadius * REPEL_FORCE;
                pvx[i] += (dx / d) * force;
                pvy[i] += (dy / d) * force;
            }
            pvx[i] *= 0.97f;
            pvy[i] *= 0.97f;
            px[i]  += pvx[i];
            py[i]  += pvy[i];
            pPulse[i] += pPulseSpeed[i];

            if (px[i] < 0) px[i] = w; if (px[i] > w) px[i] = 0;
            if (py[i] < 0) py[i] = h; if (py[i] > h) py[i] = 0;
        }

        for (int i = 0; i < SYMBOL_COUNT; i++) {
            sy[i]        += svy[i];
            sRotation[i] += sRotSpeed[i];
            if (sy[i] < -30f) {
                sy[i] = h + 20f;
                sx[i] = rng.nextFloat() * w;
            }
        }
    }

    // ── Draw ─────────────────────────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        if (!initialized) return;
        float w = getWidth(), h = getHeight();

        // 1. Background gradient
        paint.setShader(bgGradient);
        paint.setAlpha(255);
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // 2. Touch glow
        if (touchX > 0) {
            paint.setShader(new RadialGradient(touchX, touchY, 240f,
                    new int[]{Color.argb(46, 80, 140, 255), Color.TRANSPARENT},
                    new float[]{0f, 1f}, Shader.TileMode.CLAMP));
            paint.setAlpha(255);
            canvas.drawRect(0, 0, w, h, paint);
            paint.setShader(null);
        }

        // 3. Ambient orbs
        drawAmbientOrb(canvas, w * 0.2f, h * 0.2f, 180f, Color.argb(33, 30, 80, 200));
        drawAmbientOrb(canvas, w * 0.8f, h * 0.7f, 160f, Color.argb(26, 80, 30, 180));
        drawAmbientOrb(canvas, w * 0.5f, h * 0.5f, 120f, Color.argb(20, 0, 120, 200));

        // 4. Connection lines
        paint.setStrokeWidth(0.8f);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                float dx = px[i] - px[j];
                float dy = py[i] - py[j];
                float d  = (float) Math.sqrt(dx * dx + dy * dy);
                if (d < connDist) {
                    int[] c = pColor[i];
                    int a = (int) ((1f - d / connDist) * 46f);
                    paint.setColor(Color.argb(a, c[0], c[1], c[2]));
                    canvas.drawLine(px[i], py[i], px[j], py[j], paint);
                }
            }
        }
        paint.setStyle(Paint.Style.FILL);

        // 5. Particles + glow halo
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float pulse = (float)(Math.sin(pPulse[i]) * 0.3 + 0.7);
            int[] c = pColor[i];

            // Halo
            paint.setShader(new RadialGradient(px[i], py[i], pRadius[i] * 8f,
                    new int[]{Color.argb((int)(pAlpha[i] * 0.3f * pulse * 255), c[0], c[1], c[2]), Color.TRANSPARENT},
                    new float[]{0f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawCircle(px[i], py[i], pRadius[i] * 8f, paint);
            paint.setShader(null);

            // Core
            paint.setColor(Color.argb((int)(pAlpha[i] * pulse * 255), c[0], c[1], c[2]));
            canvas.drawCircle(px[i], py[i], Math.max(pRadius[i] * pulse, 0.5f), paint);
        }

        // 6. Floating symbols
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            canvas.save();
            canvas.translate(sx[i], sy[i]);
            canvas.rotate(sRotation[i]);
            textPaint.setTextSize(sSize[i]);
            textPaint.setColor(Color.argb((int)(sAlpha[i] * 255), 150, 190, 255));
            canvas.drawText(sSymbol[i], 0, sSize[i] / 2f, textPaint);
            canvas.restore();
        }
    }

    private void drawAmbientOrb(Canvas canvas, float cx, float cy, float r, int color) {
        paint.setShader(new RadialGradient(cx, cy, r,
                new int[]{color, Color.TRANSPARENT},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setShader(null);
    }

    // ── Touch ────────────────────────────────────────────────────────────────────
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                targetX = event.getX(); targetY = event.getY(); break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                targetX = getWidth() / 2f; targetY = getHeight() / 2f; break;
        }
        return true;
    }
}
