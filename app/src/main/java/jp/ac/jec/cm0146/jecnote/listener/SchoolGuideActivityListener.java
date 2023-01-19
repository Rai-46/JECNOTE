package jp.ac.jec.cm0146.jecnote.listener;

import jp.ac.jec.cm0146.jecnote.models.GuideItem;

public interface SchoolGuideActivityListener {
    void onTapItem(GuideItem item);
    void parseJson(String json);
}
