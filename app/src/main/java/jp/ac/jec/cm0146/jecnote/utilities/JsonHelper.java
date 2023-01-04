package jp.ac.jec.cm0146.jecnote.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.models.NewsItem;

public class JsonHelper {
    public static ArrayList<NewsItem> parseJson(String jsonStr) {
        ArrayList<NewsItem> NewsItems = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray items = json.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                NewsItems.add(parseToItem(item));
            }
        } catch (Exception e) {
            Log.e("JsonHelper", e.getMessage());
        }
        return NewsItems;
    }

    public static NewsItem parseToItem (JSONObject json) throws JSONException {
        NewsItem item = new NewsItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        return item;
    }
}

