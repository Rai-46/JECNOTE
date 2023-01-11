package jp.ac.jec.cm0146.jecnote.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.models.NewsDataItem;
import jp.ac.jec.cm0146.jecnote.models.NewsListItem;

public class JsonHelper {
    public static ArrayList<NewsListItem> parseListJson(String jsonStr) {
        ArrayList<NewsListItem> NewsItems = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray items = json.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                NewsItems.add(parseToListItem(item));
            }
        } catch (Exception e) {
            Log.e("JsonListHelper", e.getMessage());
        }
        return NewsItems;
    }

    private static NewsListItem parseToListItem(JSONObject json) throws JSONException {
        NewsListItem item = new NewsListItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        return item;
    }

    public static NewsDataItem parseDataJson(String jsonStr) {
        NewsDataItem dataItems = new NewsDataItem();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONObject data = json.getJSONObject("data");
            dataItems = parseToDataItem(data);
        } catch (Exception e) {
            Log.e("JsonDataHelper", e.getMessage());
        }
        return dataItems;
    }

    private static NewsDataItem parseToDataItem(JSONObject json) throws JSONException {
        NewsDataItem item = new NewsDataItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        item.setDate(json.getString("date"));
        item.setPhoto(json.getString("photo"));
        item.setText(json.getString("text"));
        return item;
    }
}

