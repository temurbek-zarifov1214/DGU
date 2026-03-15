package com.example.manualapp.ui.splash;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Draws the expanding ring animation on the Splash screen. */
public class SplashRingView extends View {

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float ringCX, ringCY;
    private float ringRadius = 0f;
    private float ringAlpha  = 0f;
    private float ringMaxR   = 0f;

    public SplashRingView(Context context) { super(context); init(); }
    public SplashRingView(Context context, AttributeSet a) { super(context, a); init(); }
    public SplashRingView(Context context, AttributeSet a, int s) { super(context, a, s); init(); }

    private void init() {
        setWillNotDraw(false);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(3f);
        ringPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        ringCX   = w / 2f;
        ringCY   = h / 2f;
        ringMaxR = (float) Math.sqrt(w * w + h * h);
    }

    /** Start the ring burst from the icon's approximate radius. */
    public void startRingAnim(float iconR) {
        ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
        va.setDuration(700);
        va.addUpdateListener(anim -> {
            float t   = (float) anim.getAnimatedValue();
            ringRadius = iconR + ringMaxR * t;
            ringAlpha  = 0.6f * (1f - t);
            invalidate();
        });
        va.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (ringAlpha > 0.01f) {
            ringPaint.setAlpha((int)(ringAlpha * 255));
            canvas.drawCircle(ringCX, ringCY, ringRadius, ringPaint);
        }
    }
}
