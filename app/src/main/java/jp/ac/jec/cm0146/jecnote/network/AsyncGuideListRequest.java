package jp.ac.jec.cm0146.jecnote.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.activities.SchoolGuideActivity;
import jp.ac.jec.cm0146.jecnote.models.GuideItem;
import jp.ac.jec.cm0146.jecnote.models.NewsDataItem;
import jp.ac.jec.cm0146.jecnote.utilities.JsonHelper;

public class AsyncGuideListRequest extends AsyncTask<Uri.Builder, Void, String> {

    private SchoolGuideActivity activity;


    public AsyncGuideListRequest(SchoolGuideActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Uri.Builder... params) {
        String resStr = "";
        HttpURLConnection con = null;

        try {
            URL url = new URL(params[0].toString());
            con = (HttpURLConnection) url.openConnection();

            resStr = inputStreamToString(con.getInputStream());


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
        return resStr;
    }

    @Override
    protected void onPostExecute(String json) {
        activity.parseJson(json);
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
}
