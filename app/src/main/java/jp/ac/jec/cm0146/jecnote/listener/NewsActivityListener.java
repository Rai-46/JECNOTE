package jp.ac.jec.cm0146.jecnote.listener;

import jp.ac.jec.cm0146.jecnote.models.NewsItem;

public interface NewsActivityListener {
    void onTapNews(NewsItem newsItems);
    void parseJson(String json);
}
