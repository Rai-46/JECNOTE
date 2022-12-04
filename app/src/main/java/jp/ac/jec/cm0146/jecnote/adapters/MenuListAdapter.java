package jp.ac.jec.cm0146.jecnote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.activities.MainActivity;
import jp.ac.jec.cm0146.jecnote.models.MenuItems;

public class MenuListAdapter extends ArrayAdapter<MenuItems> {

    private MainActivity mainActivity;

    public MenuListAdapter(@NonNull Context context, MainActivity activity) {
        super(context, R.layout.main_activity_menu_item);
        mainActivity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MenuItems item = getItem(position);
        if (convertView == null ) {
            LayoutInflater inflater = mainActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.main_activity_menu_item, null);
        }
        if (item != null){
            TextView menuTitle = (TextView) convertView.findViewById(R.id.menuTitle);
            menuTitle.setText(item.getMenuTitle());

            if(item.getMenuImage() != 0) {
                ImageView bgImage = (ImageView) convertView.findViewById(R.id.menuBackgroundImage);
                bgImage.setImageResource(item.getMenuImage());
            }

            convertView.setOnClickListener(v -> mainActivity.onTapItem(item));

        }

        return convertView;

    }
}
