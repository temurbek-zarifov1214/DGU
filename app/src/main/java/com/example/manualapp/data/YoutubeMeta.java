package com.example.manualapp.data;

import android.os.Handler;
import android.os.Looper;

import com.example.manualapp.domain.VideoLesson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches a YouTube video's real title and description without an API key:
 *   • title + author via the public oEmbed endpoint (reliable JSON)
 *   • description scraped from the watch page's "shortDescription" (best effort)
 * Runs off the main thread and posts the result back on it.
 */
public final class YoutubeMeta {

    private static final String UA = "Mozilla/5.0 (Linux; Android 12; Pixel 5) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36";

    public static class Meta {
        public String title;
        public String author;
        public String description;
        public boolean any() { return title != null || description != null; }
    }

    public interface Cb { void onResult(Meta m); }

    private YoutubeMeta() {}

    public static void fetch(VideoLesson v, Cb cb) {
        final String id = v.youtubeId;
        final String watch = (id != null && !id.isEmpty())
                ? "https://www.youtube.com/watch?v=" + id
                : (v.url != null ? v.url : "");
        new Thread(() -> {
            Meta m = new Meta();
            // 1) oEmbed → title + author
            try {
                if (!watch.isEmpty()) {
                    String j = http("https://www.youtube.com/oembed?format=json&url="
                            + URLEncoder.encode(watch, "UTF-8"), false);
                    JSONObject o = new JSONObject(j);
                    String t = o.optString("title", "");
                    String a = o.optString("author_name", "");
                    if (!t.isEmpty()) m.title = t;
                    if (!a.isEmpty()) m.author = a;
                }
            } catch (Exception ignored) {}
            // 2) watch page → shortDescription (best effort)
            try {
                if (!watch.isEmpty()) {
                    String html = http(watch, true);
                    Matcher mm = Pattern.compile("\"shortDescription\":\"((?:\\\\.|[^\"\\\\])*)\"").matcher(html);
                    if (mm.find()) {
                        String d = jsonUnescape(mm.group(1));
                        if (d != null && !d.trim().isEmpty()) m.description = d.trim();
                    }
                }
            } catch (Exception ignored) {}
            new Handler(Looper.getMainLooper()).post(() -> cb.onResult(m));
        }).start();
    }

    private static String http(String urlStr, boolean large) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
        c.setRequestProperty("User-Agent", UA);
        c.setRequestProperty("Accept-Language", "uz,en;q=0.8");
        c.setRequestProperty("Accept-Encoding", "identity");
        c.setConnectTimeout(8000);
        c.setReadTimeout(12000);
        c.setInstanceFollowRedirects(true);
        InputStream is = c.getInputStream();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n, total = 0, cap = large ? 3_000_000 : 200_000;
        while ((n = is.read(buf)) > 0) {
            bo.write(buf, 0, n);
            total += n;
            if (total > cap) break;
        }
        is.close();
        c.disconnect();
        return bo.toString("UTF-8");
    }

    private static String jsonUnescape(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\' && i + 1 < s.length()) {
                char nx = s.charAt(++i);
                switch (nx) {
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    case 't': out.append('\t'); break;
                    case '"': out.append('"'); break;
                    case '\\': out.append('\\'); break;
                    case '/': out.append('/'); break;
                    case 'u':
                        if (i + 4 < s.length()) {
                            try { out.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16)); }
                            catch (Exception e) { /* skip */ }
                            i += 4;
                        }
                        break;
                    default: out.append(nx);
                }
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }
}
