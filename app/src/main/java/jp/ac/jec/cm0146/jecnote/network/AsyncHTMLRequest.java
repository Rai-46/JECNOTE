package jp.ac.jec.cm0146.jecnote.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityGuideViewBinding;
import jp.ac.jec.cm0146.jecnote.utilities.JsonHelper;

public class AsyncHTMLRequest extends AsyncTask<Uri.Builder, Void, String> {

    private ActivityGuideViewBinding binding;
    private String keyword;

    public AsyncHTMLRequest (ActivityGuideViewBinding binding, String keyword) {
        this.binding = binding;
        this.keyword = keyword;
    }

    @Override
    protected String doInBackground(Uri.Builder... builders) {
        String resStr = "";
        HttpURLConnection con = null;

        try {
            URL url = new URL(builders[0].toString());
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
        // JSON
        String html = JsonHelper.parseHTML(resStr);
        Log.i("getHTML", html);
        return html;
    }

    @Override
    protected void onPostExecute(String s) {
        String replace = s.replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&apos;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("\\n", "")
                .replace("\\t", "")
                .replace("<head>", "<head><meta name=\"viewport\" content=\"width=device-width\">");

        if(!"".equals(keyword)) {
            replace = replace
                    .replace("<html>", "<html><style>span { color: red; }</style>")
                    .replace(keyword, "<span><b>" + keyword + "</b></span>");
        }


        WebView wv = binding.webView;
        WebSettings settings = wv.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        wv.loadDataWithBaseURL(null, replace, "text/html", "UTF8", null);

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