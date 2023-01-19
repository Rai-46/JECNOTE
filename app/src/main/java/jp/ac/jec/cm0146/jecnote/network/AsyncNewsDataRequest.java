package jp.ac.jec.cm0146.jecnote.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.activities.NewsActivity;
import jp.ac.jec.cm0146.jecnote.models.NewsDataItem;
import jp.ac.jec.cm0146.jecnote.utilities.JsonHelper;

public class AsyncNewsDataRequest extends AsyncTask<Uri.Builder, Void, NewsDataItem> {

    private NewsActivity activity;

    public AsyncNewsDataRequest(NewsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected NewsDataItem doInBackground(Uri.Builder... params) {
        String resStr = "";
        HttpURLConnection con = null;

        try {
            URL url = new URL(params[0].toString());
            con = (HttpURLConnection) url.openConnection();

            resStr = inputStreamToString(con.getInputStream());

            Log.i("resStr", String.valueOf(resStr.equals("null")));

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
        NewsDataItem item = JsonHelper.parseNewsDataJson(resStr);
        return item;
    }

    @Override
    protected void onPostExecute(NewsDataItem item) {
        if(!"".equals(item.getPhoto())) {
            decode(item.getPhoto());
        }
        TextView title = activity.findViewById(R.id.newsTitle);
        title.setText(item.getTitle());
        TextView date = activity.findViewById(R.id.newsDate);
        date.setText(item.getDate());
        TextView text = activity.findViewById(R.id.newsText);
        text.setText(replaceTextLine(item.getText()));
        activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    private String replaceTextLine(String str) {
        return str.replace("<br />", "\n");
    }

    private void decode(String imageString) {
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        ImageView iv = activity.findViewById(R.id.newsImage);
        iv.setImageBitmap(decodedImage);
    }
}
