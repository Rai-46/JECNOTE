package jp.ac.jec.cm0146.jecnote.listener;

import jp.ac.jec.cm0146.jecnote.models.NewsListItem;

public interface SchoolNewsActivityListener {
    void onTapNews(NewsListItem newsItems);
    void parseJson(String json);
}
