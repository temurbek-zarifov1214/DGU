package com.example.manualapp.ui.particle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Full-screen animated particle background.
 *
 * Rules:
 *  - All Paint objects pre-allocated (no allocations in onDraw).
 *  - Symbol paths pre-computed once in initPaths().
 *  - Choreographer drives the 60fps loop.
 *  - onTouchEvent returns FALSE; caller must call setPointerPosition().
 *  - pause() / resume() control the loop from Activity lifecycle.
 */
public class ParticleView extends View {

    // ── Constants ────────────────────────────────────────────────────────────────
    private static final int   PARTICLE_COUNT = 55;
    private static final int   FLOATER_COUNT  = 10;
    private static final int[] PARTICLE_COLORS = {
            0xFF1E64DC, 0xFF3C82FF, 0xFF6428C8,
            0xFF14B4B4, 0xFF50A0FF, 0xFF8C50DC, 0xFF1478C8
    };

    // ── Inner data classes ───────────────────────────────────────────────────────
    private static final class Particle {
        float x, y, vx, vy;
        float baseRadius;   // dp units, converted on init
        int   color;
        float alpha;
        float pulsePhase, pulseSpeed;
    }

    private static final class Floater {
        float x, y;
        float vy;           // upward speed (negative)
        float rotation, rotSpeed;
        float sizePx;       // pre-converted to pixels
        float alpha;
        int   type;         // 0-9 symbol type
    }

    // ── Particles & floaters ─────────────────────────────────────────────────────
    private final Particle[] particles = new Particle[PARTICLE_COUNT];
    private final Floater[]  floaters  = new Floater[FLOATER_COUNT];

    // ── Pre-computed symbol paths (unit space: fits -1..1) ───────────────────────
    // Atom needs 3 rotated paths; others are single paths.
    private final Path[] symbolPath   = new Path[10];  // one per symbol type
    private final Path   atomEllipse  = new Path();    // single ellipse, rotated at draw time

    // ── Pre-allocated Paint objects ──────────────────────────────────────────────
    private final Paint bgPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint orbPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint particlePaint= new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint floaterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix floaterMatrix = new Matrix();  // reused each frame

    // ── Background gradient (rebuilt on size change) ─────────────────────────────
    private LinearGradient bgGradient;

    // ── Pointer smoothing ────────────────────────────────────────────────────────
    private float touchX, touchY;
    private float smoothTouchX, smoothTouchY;

    // ── Density ──────────────────────────────────────────────────────────────────
    private float density;

    // ── Choreographer loop ────────────────────────────────────────────────────────
    private boolean running = false;
    private final Choreographer.FrameCallback frameCallback = frameTimeNanos -> {
        if (running) {
            updateFrame();
            invalidate();
            Choreographer.getInstance().postFrameCallback(this.frameCallback);
        }
    };

    private final Random rng = new Random();
    private boolean initialized = false;

    // ── Constructors ─────────────────────────────────────────────────────────────
    public ParticleView(Context context) {
        super(context); setup();
    }
    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs); setup();
    }
    public ParticleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); setup();
    }

    private void setup() {
        setWillNotDraw(false);
        density = getResources().getDisplayMetrics().density;

        // Configure pre-allocated paints
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(0.5f * density);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        floaterPaint.setStyle(Paint.Style.STROKE);
        floaterPaint.setStrokeWidth(1.5f * density);
        floaterPaint.setStrokeCap(Paint.Cap.ROUND);
        floaterPaint.setStrokeJoin(Paint.Join.ROUND);

        initPaths();
    }

    // ── Path initialization (called once) ─────────────────────────────────────────
    private void initPaths() {

        // 0 ── Open Book ──────────────────────────────────────────────────────────
        {
            Path p = new Path();
            // Left page
            p.moveTo(-0.85f, -0.65f);
            p.lineTo(-0.05f, -0.55f);
            p.lineTo(-0.05f,  0.75f);
            p.lineTo(-0.85f,  0.65f);
            p.close();
            // Right page
            p.moveTo( 0.05f, -0.55f);
            p.lineTo( 0.85f, -0.65f);
            p.lineTo( 0.85f,  0.65f);
            p.lineTo( 0.05f,  0.75f);
            p.close();
            // Page lines left
            p.moveTo(-0.65f,  0.0f); p.lineTo(-0.10f,  0.05f);
            p.moveTo(-0.65f,  0.3f); p.lineTo(-0.10f,  0.35f);
            // Page lines right
            p.moveTo( 0.10f,  0.05f); p.lineTo( 0.65f,  0.0f);
            p.moveTo( 0.10f,  0.35f); p.lineTo( 0.65f,  0.3f);
            symbolPath[0] = p;
        }

        // 1 ── Graduation Cap ─────────────────────────────────────────────────────
        {
            Path p = new Path();
            // Diamond top
            p.moveTo( 0.00f, -0.90f);
            p.lineTo( 0.80f, -0.25f);
            p.lineTo( 0.00f,  0.05f);
            p.lineTo(-0.80f, -0.25f);
            p.close();
            // Head cylinder (small rectangle below diamond)
            p.addRect(-0.18f, 0.05f, 0.18f, 0.55f, Path.Direction.CW);
            // Tassel cord + bob
            p.moveTo( 0.80f, -0.25f);
            p.lineTo( 0.80f,  0.20f);
            p.lineTo( 0.62f,  0.42f);
            p.addCircle(0.62f, 0.48f, 0.09f, Path.Direction.CW);
            symbolPath[1] = p;
        }

        // 2 ── Pencil ──────────────────────────────────────────────────────────────
        {
            Path p = new Path();
            // Body
            p.addRect(-0.22f, -0.70f, 0.22f, 0.45f, Path.Direction.CW);
            // Tip
            p.moveTo(-0.22f, 0.45f);
            p.lineTo( 0.22f, 0.45f);
            p.lineTo( 0.00f, 0.90f);
            p.close();
            // Eraser band
            p.moveTo(-0.22f, -0.55f);
            p.lineTo( 0.22f, -0.55f);
            // Lead line
            p.moveTo(-0.07f, 0.55f);
            p.lineTo( 0.07f, 0.80f);
            symbolPath[2] = p;
        }

        // 3 ── Five-pointed Star ───────────────────────────────────────────────────
        {
            Path p = new Path();
            for (int i = 0; i < 10; i++) {
                double angle = Math.PI * i / 5.0 - Math.PI / 2.0;
                float  r     = (i % 2 == 0) ? 0.90f : 0.38f;
                float  x     = (float)(r * Math.cos(angle));
                float  y     = (float)(r * Math.sin(angle));
                if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
            }
            p.close();
            symbolPath[3] = p;
        }

        // 4 ── Atom Ring ───────────────────────────────────────────────────────────
        // Store a single unit ellipse; rotate canvas 0°/60°/120° at draw time.
        {
            atomEllipse.addOval(new RectF(-0.90f, -0.28f, 0.90f, 0.28f), Path.Direction.CW);
            // Centre nucleus
            atomEllipse.addCircle(0, 0, 0.14f, Path.Direction.CW);
            // symbolPath[4] not used for atom — handled specially in drawFloater()
            symbolPath[4] = null;
        }

        // 5 ── Diploma Scroll ──────────────────────────────────────────────────────
        {
            Path p = new Path();
            p.addRect(-0.65f, -0.38f, 0.65f, 0.38f, Path.Direction.CW);
            p.addOval(new RectF(-0.82f, -0.38f, -0.48f, 0.38f), Path.Direction.CW);
            p.addOval(new RectF( 0.48f, -0.38f,  0.82f, 0.38f), Path.Direction.CW);
            p.moveTo(-0.40f,  0.00f); p.lineTo(0.40f,  0.00f);
            p.moveTo(-0.30f, -0.14f); p.lineTo(0.30f, -0.14f);
            p.moveTo(-0.30f,  0.14f); p.lineTo(0.30f,  0.14f);
            symbolPath[5] = p;
        }

        // 6 ── Magnifying Glass ────────────────────────────────────────────────────
        {
            Path p = new Path();
            p.addCircle(-0.18f, -0.18f, 0.52f, Path.Direction.CW);
            p.moveTo( 0.27f,  0.27f);
            p.lineTo( 0.82f,  0.82f);
            // Handle crossbar
            p.moveTo( 0.58f,  0.68f);
            p.lineTo( 0.78f,  0.50f);
            symbolPath[6] = p;
        }

        // 7 ── Light Bulb ─────────────────────────────────────────────────────────
        {
            Path p = new Path();
            p.addArc(new RectF(-0.45f, -0.85f, 0.45f, 0.25f), 180f, -180f);
            p.lineTo( 0.32f, 0.25f);
            p.lineTo( 0.32f, 0.62f);
            p.lineTo(-0.32f, 0.62f);
            p.lineTo(-0.32f, 0.25f);
            p.close();
            // Filament lines
            p.moveTo(-0.20f, 0.36f); p.lineTo(0.20f, 0.36f);
            p.moveTo(-0.20f, 0.50f); p.lineTo(0.20f, 0.50f);
            symbolPath[7] = p;
        }

        // 8 ── Globe ──────────────────────────────────────────────────────────────
        {
            Path p = new Path();
            p.addCircle(0, 0, 0.82f, Path.Direction.CW);
            // Latitude lines as ovals
            p.addOval(new RectF(-0.82f, -0.24f, 0.82f, 0.24f), Path.Direction.CW);
            p.addOval(new RectF(-0.60f, -0.60f, 0.60f, -0.06f), Path.Direction.CW);
            p.addOval(new RectF(-0.60f,  0.06f, 0.60f,  0.60f), Path.Direction.CW);
            // Vertical centre meridian
            p.moveTo(0, -0.82f); p.lineTo(0, 0.82f);
            symbolPath[8] = p;
        }

        // 9 ── Chess King ──────────────────────────────────────────────────────────
        {
            Path p = new Path();
            // Cross: vertical bar
            p.addRect(-0.07f, -0.90f,  0.07f, -0.40f, Path.Direction.CW);
            // Cross: horizontal bar
            p.addRect(-0.26f, -0.74f,  0.26f, -0.56f, Path.Direction.CW);
            // Body trapezoid
            p.moveTo(-0.35f, -0.40f);
            p.lineTo( 0.35f, -0.40f);
            p.lineTo( 0.52f,  0.72f);
            p.lineTo(-0.52f,  0.72f);
            p.close();
            // Base
            p.moveTo(-0.58f, 0.72f); p.lineTo(0.58f, 0.72f);
            symbolPath[9] = p;
        }
    }

    // ── Size change — re-init particles ──────────────────────────────────────────
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        if (w == 0 || h == 0) return;

        bgGradient = new LinearGradient(0, 0, w, h,
                new int[]{ 0xFF071535, 0xFF0D2560, 0xFF081840 },
                new float[]{ 0f, 0.5f, 1f },
                Shader.TileMode.CLAMP);

        touchX = smoothTouchX = w / 2f;
        touchY = smoothTouchY = h / 2f;

        initParticles(w, h);
        initFloaters(w, h);
        initialized = true;
        resume();
    }

    private void initParticles(int w, int h) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle p = new Particle();
            p.x          = rng.nextFloat() * w;
            p.y          = rng.nextFloat() * h;
            p.vx         = (rng.nextFloat() - 0.5f) * 0.8f;
            p.vy         = (rng.nextFloat() - 0.5f) * 0.8f;
            p.baseRadius = (3f + rng.nextFloat() * 5f) * density;
            p.color      = PARTICLE_COLORS[i % PARTICLE_COLORS.length];
            p.alpha      = 0.15f + rng.nextFloat() * 0.50f;
            p.pulsePhase = rng.nextFloat() * (float)(2 * Math.PI);
            p.pulseSpeed = 0.015f + rng.nextFloat() * 0.020f;
            particles[i] = p;
        }
    }

    private void initFloaters(int w, int h) {
        for (int i = 0; i < FLOATER_COUNT; i++) {
            Floater f = new Floater();
            f.x        = rng.nextFloat() * w;
            f.y        = h * rng.nextFloat();
            f.vy       = -(0.6f + rng.nextFloat() * 0.8f);
            f.rotation = rng.nextFloat() * 360f;
            f.rotSpeed = (rng.nextFloat() - 0.5f) * 0.016f;
            f.sizePx   = (18f + rng.nextFloat() * 18f) * density;
            f.alpha    = 0.08f + rng.nextFloat() * 0.14f;
            f.type     = i % 10;
            floaters[i] = f;
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────────
    public void resume() {
        if (!running) {
            running = true;
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    public void pause() {
        running = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    // Keep backward-compat aliases
    public void startAnimation() { resume(); }
    public void stopAnimation()  { pause();  }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (initialized) resume();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) { if (initialized) resume(); }
        else pause();
    }

    // ── Touch — returns false; caller uses setPointerPosition() ─────────────────
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;   // don't consume; Activity dispatches via setPointerPosition
    }

    /** Called by the host Activity's dispatchTouchEvent to forward pointer position. */
    public void setPointerPosition(float x, float y) {
        touchX = x;
        touchY = y;
    }

    /** Called when finger lifts — recentre smoothly to middle. */
    public void clearPointer() {
        touchX = getWidth()  / 2f;
        touchY = getHeight() / 2f;
    }

    // ── Frame update ──────────────────────────────────────────────────────────────
    private void updateFrame() {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // Smooth pointer interpolation
        smoothTouchX += (touchX - smoothTouchX) * 0.06f;
        smoothTouchY += (touchY - smoothTouchY) * 0.06f;

        float repelRadius = 100f * density;

        for (Particle p : particles) {
            // Pulse
            p.pulsePhase += p.pulseSpeed;

            // Touch repulsion
            float dx   = p.x - smoothTouchX;
            float dy   = p.y - smoothTouchY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < repelRadius && dist > 0.1f) {
                float force = (repelRadius - dist) / repelRadius * 1.2f;
                p.vx += (dx / dist) * force;
                p.vy += (dy / dist) * force;
            }

            // Damping + move
            p.vx *= 0.97f;
            p.vy *= 0.97f;
            p.x  += p.vx;
            p.y  += p.vy;

            // Edge wrap
            if (p.x < 0)  p.x = w;
            if (p.x > w)  p.x = 0;
            if (p.y < 0)  p.y = h;
            if (p.y > h)  p.y = 0;
        }

        for (Floater f : floaters) {
            f.y        += f.vy;
            f.rotation += f.rotSpeed;
            if (f.y < -50 * density) {
                f.y = h + rng.nextFloat() * 80 * density;
                f.x = rng.nextFloat() * w;
            }
        }
    }

    // ── Draw ─────────────────────────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        if (!initialized) return;
        float w = getWidth(), h = getHeight();

        // ── LAYER 1: Background gradient ──────────────────────────────────────────
        bgPaint.setShader(bgGradient);
        canvas.drawRect(0, 0, w, h, bgPaint);
        bgPaint.setShader(null);

        // ── LAYER 2: Touch glow ───────────────────────────────────────────────────
        float glowR = 200f * density;
        orbPaint.setShader(new RadialGradient(
                smoothTouchX, smoothTouchY, glowR,
                new int[]{ Color.argb(51, 80, 140, 255), Color.TRANSPARENT },
                new float[]{ 0f, 1f },
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, orbPaint);
        orbPaint.setShader(null);

        // ── LAYER 3: Ambient orbs ─────────────────────────────────────────────────
        drawOrb(canvas, w * 0.15f, h * 0.20f, w * 0.35f, Color.argb(33, 30,  80, 200));
        drawOrb(canvas, w * 0.85f, h * 0.70f, w * 0.30f, Color.argb(26, 80,  30, 180));
        drawOrb(canvas, w * 0.50f, h * 0.50f, w * 0.25f, Color.argb(20,  0, 120, 200));

        // ── LAYER 4: Connection lines ─────────────────────────────────────────────
        float connR = 80f * density;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                float dx = particles[i].x - particles[j].x;
                float dy = particles[i].y - particles[j].y;
                float d  = (float) Math.sqrt(dx * dx + dy * dy);
                if (d < connR) {
                    int baseColor = particles[i].color;
                    int a = (int)((1f - d / connR) * 45);
                    linePaint.setColor(Color.argb(a,
                            Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)));
                    canvas.drawLine(particles[i].x, particles[i].y,
                                    particles[j].x, particles[j].y, linePaint);
                }
            }
        }

        // ── LAYERS 5 + 6: Halos + solid particles ────────────────────────────────
        for (Particle p : particles) {
            float pulse = (float)(Math.sin(p.pulsePhase) * 0.3 + 0.7);
            int   r     = Color.red(p.color);
            int   g     = Color.green(p.color);
            int   b     = Color.blue(p.color);

            // Glow halo
            float haloR = p.baseRadius * 5f * pulse;
            haloPaint.setShader(new RadialGradient(p.x, p.y, Math.max(haloR, 1f),
                    new int[]{ Color.argb((int)(p.alpha * 0.35f * pulse * 255), r, g, b), Color.TRANSPARENT },
                    new float[]{ 0f, 1f },
                    Shader.TileMode.CLAMP));
            canvas.drawCircle(p.x, p.y, haloR, haloPaint);
            haloPaint.setShader(null);

            // Solid core
            particlePaint.setColor(Color.argb((int)(p.alpha * pulse * 255), r, g, b));
            canvas.drawCircle(p.x, p.y, Math.max(p.baseRadius * pulse, 0.5f), particlePaint);
        }

        // ── LAYER 7: Floating education symbols ───────────────────────────────────
        for (Floater f : floaters) {
            drawFloater(canvas, f);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private void drawOrb(Canvas canvas, float cx, float cy, float r, int color) {
        orbPaint.setShader(new RadialGradient(cx, cy, r,
                new int[]{ color, Color.TRANSPARENT },
                new float[]{ 0f, 1f },
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, getWidth(), getHeight(), orbPaint);
        orbPaint.setShader(null);
    }

    private void drawFloater(Canvas canvas, Floater f) {
        int alpha = (int)(f.alpha * 255);
        floaterPaint.setColor(Color.argb(alpha, 180, 210, 255));

        canvas.save();
        canvas.translate(f.x, f.y);
        canvas.rotate(f.rotation);
        float s = f.sizePx / 2f;    // unit-path uses -1..1, so scale = half-size

        if (f.type == 4) {
            // Atom: draw the pre-computed ellipse at 3 rotations
            canvas.scale(s, s);
            canvas.drawPath(atomEllipse, floaterPaint);
            canvas.rotate(60f);
            canvas.drawPath(atomEllipse, floaterPaint);
            canvas.rotate(60f);
            canvas.drawPath(atomEllipse, floaterPaint);
        } else if (symbolPath[f.type] != null) {
            canvas.scale(s, s);
            canvas.drawPath(symbolPath[f.type], floaterPaint);
        }

        canvas.restore();
    }
}
