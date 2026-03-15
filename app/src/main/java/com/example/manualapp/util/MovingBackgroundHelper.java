package com.example.manualapp.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.example.manualapp.R;

import java.util.WeakHashMap;

public final class MovingBackgroundHelper {

    private static final WeakHashMap<Activity, ValueAnimator> animators = new WeakHashMap<>();

    public static void startMovingBackground(Activity activity, View container) {
        if (container == null) return;
        stopMovingBackground(activity);

        View movingElement = container.findViewById(R.id.movingElement);
        if (movingElement == null) return;

        container.post(() -> {
            ViewGroup parent = (ViewGroup) container.getParent();
            if (parent == null) return;
            int width = parent.getWidth();
            if (width <= 0) return;

            float startX = -movingElement.getWidth() - 50f;
            float endX = width + 50f;

            movingElement.setTranslationX(startX);

            ValueAnimator a = ObjectAnimator.ofFloat(movingElement, View.TRANSLATION_X, startX, endX);
            a.setDuration(14000);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.RESTART);
            a.start();
            animators.put(activity, a);
        });
    }

    public static void stopMovingBackground(Activity activity) {
        ValueAnimator a = animators.remove(activity);
        if (a != null) a.cancel();
    }
}
