package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.adapters.NewsListAdapter;
import jp.ac.jec.cm0146.jecnote.databinding.ActivitySchoolNewsBinding;
import jp.ac.jec.cm0146.jecnote.listener.SchoolNewsActivityListener;
import jp.ac.jec.cm0146.jecnote.models.NewsListItem;
import jp.ac.jec.cm0146.jecnote.network.AsyncNewsListRequest;
import jp.ac.jec.cm0146.jecnote.utilities.JsonHelper;

public class SchoolNewsActivity extends AppCompatActivity implements SchoolNewsActivityListener {

    private ActivitySchoolNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();
    }

    private void init() {
        // ここでHttpリクエストしてくる
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("21cm0146.main.jp");
        uriBuilder.path("jecnote/outputList.php");

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.listNews.setVisibility(View.GONE);

        AsyncNewsListRequest req = new AsyncNewsListRequest(this);
        req.execute(uriBuilder);
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void setList(String json) {

        ArrayList<NewsListItem> newsItems = JsonHelper.parseNewsListJson(json);
        NewsListAdapter adapter = new NewsListAdapter(getApplicationContext(), this);
        for(NewsListItem newsItem : newsItems) {
            adapter.add(newsItem);
        }
        binding.listNews.setAdapter(adapter);

        if(!newsItems.isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.listNews.setVisibility(View.VISIBLE);
        }

    }

    /// NewsActivityListener
    @Override
    public void onTapNews(NewsListItem newsItems) {
        Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
        intent.putExtra("id", newsItems.getId());
        intent.putExtra("title", newsItems.getTitle());
        startActivity(intent);
    }

    @Override
    public void parseJson(String json) {
        setList(json);
    }
}