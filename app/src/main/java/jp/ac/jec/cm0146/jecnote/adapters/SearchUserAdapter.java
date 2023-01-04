package jp.ac.jec.cm0146.jecnote.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.activities.SearchUserActivity;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;

public class SearchUserAdapter extends ArrayAdapter<StudentUser> {

    private SearchUserActivity searchUserActivity;

    public SearchUserAdapter(@NonNull Context context, SearchUserActivity activity) {
        super(context, R.layout.studentuser_list_item);
        searchUserActivity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StudentUser user = getItem(position);
        if(convertView == null) {
            LayoutInflater inflater = searchUserActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.studentuser_list_item, null);
        }
        if(user != null) {
            ImageView userImage = (ImageView) convertView.findViewById(R.id.userImage);
            new DownloadUserImage(userImage).execute(user.userImage);

            TextView userName = (TextView) convertView.findViewById(R.id.userDisplayName);
            userName.setText(user.userDisplayName);

            // タップされた時のリスナー(Activityのimplementsメソッドの呼び出し)
            convertView.setOnClickListener(v -> searchUserActivity.onTapListener(user));

        }
        return convertView;
    }

    // URLから画像をImageViewに追加する
    // FIXME AsyncTaskが非推奨だから、余裕があれば直す！
    public class DownloadUserImage extends AsyncTask<String, Void, Bitmap> {
        protected ImageView bmImage;

        // ImageViewの参照先を受け取る
        public DownloadUserImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        @Override
        public Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
