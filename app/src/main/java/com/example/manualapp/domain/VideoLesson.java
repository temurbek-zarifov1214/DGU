package com.example.manualapp.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * A single video lesson. {@code youtubeId} drives in-app playback; when empty,
 * {@code query} is used to open a YouTube search inside the player WebView.
 */
public class VideoLesson implements Serializable {

    public String id;
    public String title;
    public String topic;
    public String colorHex;
    public String duration;
    public String youtubeId;   // may be empty for bundled samples
    public String query;       // search fallback
    public String url;         // original pasted URL (used for playback)
    public boolean saved;      // "yuklab olingan" (offline-marked)
    public boolean isNew;      // user-added

    public VideoLesson() {}

    public VideoLesson(String id, String title, String topic, String colorHex,
                       String duration, String youtubeId, String query) {
        this.id = id; this.title = title; this.topic = topic; this.colorHex = colorHex;
        this.duration = duration; this.youtubeId = youtubeId; this.query = query;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("title", title);
        o.put("topic", topic);
        o.put("colorHex", colorHex);
        o.put("duration", duration);
        o.put("youtubeId", youtubeId == null ? "" : youtubeId);
        o.put("query", query == null ? "" : query);
        o.put("url", url == null ? "" : url);
        o.put("saved", saved);
        o.put("isNew", isNew);
        return o;
    }

    public static VideoLesson fromJson(JSONObject o) {
        VideoLesson v = new VideoLesson();
        v.id = o.optString("id");
        v.title = o.optString("title");
        v.topic = o.optString("topic");
        v.colorHex = o.optString("colorHex", "#3EC6C0");
        v.duration = o.optString("duration");
        v.youtubeId = o.optString("youtubeId", "");
        v.query = o.optString("query", "");
        v.url = o.optString("url", "");
        v.saved = o.optBoolean("saved", false);
        v.isNew = o.optBoolean("isNew", false);
        return v;
    }
}
