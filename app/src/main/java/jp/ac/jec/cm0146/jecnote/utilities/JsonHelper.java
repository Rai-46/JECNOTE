package jp.ac.jec.cm0146.jecnote.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.models.GuideItem;
import jp.ac.jec.cm0146.jecnote.models.NewsDataItem;
import jp.ac.jec.cm0146.jecnote.models.NewsListItem;

public class JsonHelper {
    public static ArrayList<NewsListItem> parseNewsListJson(String jsonStr) {
        ArrayList<NewsListItem> NewsItems = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray items = json.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                NewsItems.add(parseToNewsListItem(item));
            }
        } catch (Exception e) {
            Log.e("parseNewsListJson", e.getMessage());
        }
        return NewsItems;
    }

    private static NewsListItem parseToNewsListItem(JSONObject json) throws JSONException {
        NewsListItem item = new NewsListItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        return item;
    }

    public static NewsDataItem parseNewsDataJson(String jsonStr) {
        NewsDataItem dataItems = new NewsDataItem();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONObject data = json.getJSONObject("data");
            dataItems = parseToNewsDataItem(data);
        } catch (Exception e) {
            Log.e("parseNewsDataJson", e.getMessage());
        }
        return dataItems;
    }

    private static NewsDataItem parseToNewsDataItem(JSONObject json) throws JSONException {
        NewsDataItem item = new NewsDataItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        item.setDate(json.getString("date"));
        item.setPhoto(json.getString("photo"));
        item.setText(json.getString("text"));
        return item;
    }

    public static ArrayList<GuideItem> parseGuideListJSON(String jsonStr) {
        ArrayList<GuideItem> guideItems = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray items = json.getJSONArray("data");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                guideItems.add(parseToGuideList(item));
            }
        } catch (Exception e) {
            Log.e("parseGuideListJSON", e.getMessage());
        }
        return guideItems;
    }

    private static GuideItem parseToGuideList(JSONObject json) throws JSONException {
        GuideItem item = new GuideItem();
        item.setId(json.getString("id"));
        item.setTitle(json.getString("title"));
        item.setOrderNum(json.getString("orderNum"));
        item.setHit(json.getString("hit"));

        return item;
    }

    public static String parseHTML(String jsonStr) {
        String html = "";
        try {
            JSONObject json = new JSONObject(jsonStr);
            html = json.getString("contents");
            Log.i("jsonGetString", json.getString("contents"));
        } catch (Exception e) {
            Log.e("parseHTML", e.getMessage());
        }
        return html;
    }
}

