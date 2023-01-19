package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityGuideViewBinding;
import jp.ac.jec.cm0146.jecnote.network.AsyncHTMLRequest;

public class GuideView extends AppCompatActivity {

    private ActivityGuideViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();
    }

    private void init() {
        // title
        Intent intent = getIntent();

        String titleStr = intent.getStringExtra("title");
        TextView title = binding.title;
        title.setText(titleStr);

        // httprequest
        int id = intent.getIntExtra("id", 0);
        String keyword = intent.getStringExtra("keyword");
        request(id, keyword);

    }

    private void request(int id, String keyword) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("21cm0146.main.jp");
        uriBuilder.path("jecGuide/outputContents.php");
        uriBuilder.appendQueryParameter("id", String.valueOf(id));


        AsyncHTMLRequest req = new AsyncHTMLRequest(binding, keyword);
        req.execute(uriBuilder);
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}