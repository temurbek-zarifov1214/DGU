package com.example.manualapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.manualapp.domain.VideoLesson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Source of the Video darslar dataset: bundled sample lessons plus user-added
 * YouTube links, "saved for offline" state, and view history — all persisted in
 * SharedPreferences.
 */
public final class VideoRepository {

    private static final String PREFS = "video_prefs";
    private static final String K_ADDED = "added";
    private static final String K_SAVED = "saved_overrides";
    private static final String K_HISTORY = "history";

    private static final String[] COLORS = {"#3EC6C0", "#D9A441", "#3D6FB4", "#C75B39", "#7FA88B"};

    private VideoRepository() {}

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // ── Bundled samples ─────────────────────────────────────────────────────────
    // No bundled videos — the list starts empty and is filled by the user via the
    // "+" YouTube-link button.
    private static List<VideoLesson> bundled() {
        return new ArrayList<>();
    }

    // ── Queries ─────────────────────────────────────────────────────────────────
    public static List<VideoLesson> getAll(Context c) {
        Map<String, Boolean> overrides = readSavedOverrides(c);
        List<VideoLesson> out = new ArrayList<>(readAdded(c));   // newest first
        for (VideoLesson v : out) v.isNew = true;
        out.addAll(bundled());
        for (VideoLesson v : out) {
            if (overrides.containsKey(v.id)) v.saved = overrides.get(v.id);
        }
        return out;
    }

    public static List<VideoLesson> getSaved(Context c) {
        List<VideoLesson> out = new ArrayList<>();
        for (VideoLesson v : getAll(c)) if (v.saved) out.add(v);
        return out;
    }

    public static List<VideoLesson> getHistory(Context c) {
        List<String> ids = readHistory(c);
        Map<String, VideoLesson> byId = new LinkedHashMap<>();
        for (VideoLesson v : getAll(c)) byId.put(v.id, v);
        List<VideoLesson> out = new ArrayList<>();
        for (String id : ids) if (byId.containsKey(id)) out.add(byId.get(id));
        return out;
    }

    // ── Mutations ───────────────────────────────────────────────────────────────
    public static VideoLesson addFromUrl(Context c, String url) {
        List<VideoLesson> added = readAdded(c);
        int seq = added.size() + 1;
        String ytId = extractId(url);
        String pretty = prettify(ytId.isEmpty() ? lastSegment(url) : ytId);
        int mins = 4 + (seq * 3) % 11, secs = 10 + (seq * 17) % 49;
        VideoLesson v = new VideoLesson(
                "add" + System.currentTimeMillis(),
                "YouTube: " + cap(pretty, 34),
                "Yangi qoʻshilgan",
                COLORS[seq % COLORS.length],
                pad(mins) + ":" + pad(secs),
                ytId,
                pretty);
        v.url = url == null ? "" : url.trim();
        v.isNew = true;
        added.add(0, v);
        writeAdded(c, added);
        return v;
    }

    public static void setSaved(Context c, String id, boolean saved) {
        // persist override for any item (bundled or added)
        Map<String, Boolean> overrides = readSavedOverrides(c);
        overrides.put(id, saved);
        JSONObject o = new JSONObject();
        try { for (Map.Entry<String, Boolean> e : overrides.entrySet()) o.put(e.getKey(), e.getValue()); } catch (Exception ignored) {}
        prefs(c).edit().putString(K_SAVED, o.toString()).apply();
        // also update the added record if applicable
        List<VideoLesson> added = readAdded(c);
        boolean changed = false;
        for (VideoLesson v : added) if (v.id.equals(id)) { v.saved = saved; changed = true; }
        if (changed) writeAdded(c, added);
    }

    public static void removeAdded(Context c, String id) {
        List<VideoLesson> added = readAdded(c);
        for (int i = added.size() - 1; i >= 0; i--) if (added.get(i).id.equals(id)) added.remove(i);
        writeAdded(c, added);
    }

    public static void addHistory(Context c, String id) {
        List<String> ids = readHistory(c);
        ids.remove(id);
        ids.add(0, id);
        while (ids.size() > 20) ids.remove(ids.size() - 1);
        JSONArray arr = new JSONArray();
        for (String s : ids) arr.put(s);
        prefs(c).edit().putString(K_HISTORY, arr.toString()).apply();
    }

    // ── Storage helpers ─────────────────────────────────────────────────────────
    private static List<VideoLesson> readAdded(Context c) {
        List<VideoLesson> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs(c).getString(K_ADDED, "[]"));
            for (int i = 0; i < arr.length(); i++) list.add(VideoLesson.fromJson(arr.getJSONObject(i)));
        } catch (Exception ignored) {}
        return list;
    }

    private static void writeAdded(Context c, List<VideoLesson> list) {
        JSONArray arr = new JSONArray();
        try { for (VideoLesson v : list) arr.put(v.toJson()); } catch (Exception ignored) {}
        prefs(c).edit().putString(K_ADDED, arr.toString()).apply();
    }

    private static Map<String, Boolean> readSavedOverrides(Context c) {
        Map<String, Boolean> m = new LinkedHashMap<>();
        try {
            JSONObject o = new JSONObject(prefs(c).getString(K_SAVED, "{}"));
            for (java.util.Iterator<String> it = o.keys(); it.hasNext(); ) {
                String k = it.next();
                m.put(k, o.getBoolean(k));
            }
        } catch (Exception ignored) {}
        return m;
    }

    private static List<String> readHistory(Context c) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs(c).getString(K_HISTORY, "[]"));
            for (int i = 0; i < arr.length(); i++) list.add(arr.getString(i));
        } catch (Exception ignored) {}
        return list;
    }

    // ── URL parsing ─────────────────────────────────────────────────────────────
    private static final Pattern YT = Pattern.compile("(?:v=|youtu\\.be/|/embed/|/shorts/)([\\w-]{4,})");

    private static String extractId(String url) {
        if (url == null) return "";
        Matcher m = YT.matcher(url);
        return m.find() ? m.group(1) : "";
    }

    private static String lastSegment(String url) {
        if (url == null) return "video";
        String[] parts = url.trim().split("[/?=&]");
        for (int i = parts.length - 1; i >= 0; i--) if (!parts[i].isEmpty()) return parts[i];
        return "video";
    }

    private static String prettify(String s) {
        if (s == null || s.isEmpty()) return "Video";
        String t = s.replaceAll("[-_]+", " ").trim();
        StringBuilder sb = new StringBuilder();
        boolean up = true;
        for (char ch : t.toCharArray()) {
            sb.append(up ? Character.toUpperCase(ch) : ch);
            up = ch == ' ';
        }
        return sb.toString();
    }

    private static String cap(String s, int n) { return s.length() > n ? s.substring(0, n) : s; }
    private static String pad(int n) { return n < 10 ? "0" + n : String.valueOf(n); }
}
