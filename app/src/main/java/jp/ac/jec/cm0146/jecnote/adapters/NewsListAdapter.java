package jp.ac.jec.cm0146.jecnote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.activities.SchoolNewsActivity;
import jp.ac.jec.cm0146.jecnote.models.NewsListItem;

public class NewsListAdapter extends ArrayAdapter<NewsListItem> {

    private SchoolNewsActivity activity;

    public NewsListAdapter(@NonNull Context context, SchoolNewsActivity activity) {
        super(context, R.layout.news_activity_item);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NewsListItem newsItems = getItem(position);
        if(convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.news_activity_item, null);
        }
        if(newsItems != null) {
            TextView title = convertView.findViewById(R.id.newsTitle);
            title.setText(newsItems.getTitle());

            convertView.setOnClickListener(v -> activity.onTapNews(newsItems));
        }

        return convertView;
    }
}
