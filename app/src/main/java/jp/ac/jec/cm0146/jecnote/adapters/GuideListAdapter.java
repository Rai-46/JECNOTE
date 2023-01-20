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
import jp.ac.jec.cm0146.jecnote.activities.SchoolGuideActivity;
import jp.ac.jec.cm0146.jecnote.activities.SchoolNewsActivity;
import jp.ac.jec.cm0146.jecnote.models.GuideItem;
import jp.ac.jec.cm0146.jecnote.models.NewsListItem;

public class GuideListAdapter extends ArrayAdapter<GuideItem> {

    private SchoolGuideActivity activity;

    public GuideListAdapter(@NonNull Context context, SchoolGuideActivity activity) {
        super(context, R.layout.guide_activity_item); // レイアウトはニュースのを借りる
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GuideItem guideItems = getItem(position);
        if(convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.guide_activity_item, null);
        }
        if(guideItems != null) {
            TextView title = convertView.findViewById(R.id.newsTitle);
            title.setText(guideItems.getTitle());

            if(guideItems.getHit() != 0) {
                TextView hitText = convertView.findViewById(R.id.hit);
                hitText.setText("件数: " + guideItems.getHit());
                hitText.setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.hit).setVisibility(View.INVISIBLE);
            }

            convertView.setOnClickListener(v -> activity.onTapItem(guideItems));
        }

        return convertView;
    }

}
