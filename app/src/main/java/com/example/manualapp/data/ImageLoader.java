package com.example.manualapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tiny async image loader (no external deps) for YouTube thumbnails. Caches in
 * memory and guards against RecyclerView view recycling via the target's tag.
 */
public final class ImageLoader {

    private static final LruCache<String, Bitmap> CACHE = new LruCache<>(40);

    private ImageLoader() {}

    public static void load(final String url, final ImageView iv) {
        if (url == null || url.isEmpty()) return;
        iv.setTag(url);
        Bitmap cached = CACHE.get(url);
        if (cached != null) { iv.setImageBitmap(cached); return; }
        new Thread(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setConnectTimeout(8000);
                c.setReadTimeout(10000);
                c.setInstanceFollowRedirects(true);
                c.setRequestProperty("User-Agent", "Mozilla/5.0");
                final Bitmap bmp = BitmapFactory.decodeStream(c.getInputStream());
                c.disconnect();
                if (bmp != null) {
                    CACHE.put(url, bmp);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (url.equals(iv.getTag())) iv.setImageBitmap(bmp);
                    });
                }
            } catch (Exception ignored) {}
        }).start();
    }
}
