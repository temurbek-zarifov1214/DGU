package com.example.manualapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.manualapp.domain.ContentType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomSectionRepository {

    private static final String PREFS_NAME = "custom_sections";
    private static final String KEY_PREFIX = "custom_";

    private final SharedPreferences prefs;

    public CustomSectionRepository(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void addCustomItem(ContentType type, String displayName, String localPath) {
        List<CustomItem> items = getCustomItems(type);
        items.add(new CustomItem(displayName, localPath));
        saveCustomItems(type, items);
    }

    public List<CustomItem> getCustomItems(ContentType type) {
        String json = prefs.getString(KEY_PREFIX + type.name(), "[]");
        List<CustomItem> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new CustomItem(o.optString("name", ""), o.optString("path", "")));
            }
        } catch (JSONException ignored) {
        }
        return list;
    }

    private void saveCustomItems(ContentType type, List<CustomItem> items) {
        JSONArray arr = new JSONArray();
        for (CustomItem item : items) {
            JSONObject o = new JSONObject();
            try {
                o.put("name", item.displayName);
                o.put("path", item.localPath);
                arr.put(o);
            } catch (JSONException ignored) {
            }
        }
        prefs.edit().putString(KEY_PREFIX + type.name(), arr.toString()).apply();
    }

    public static class CustomItem {
        public final String displayName;
        public final String localPath;

        public CustomItem(String displayName, String localPath) {
            this.displayName = displayName;
            this.localPath = localPath;
        }
    }
}
