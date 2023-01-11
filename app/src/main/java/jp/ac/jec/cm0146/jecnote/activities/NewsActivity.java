package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityNewsBinding;
import jp.ac.jec.cm0146.jecnote.network.AsyncNewsDataRequest;
import jp.ac.jec.cm0146.jecnote.network.AsyncNewsListRequest;

public class NewsActivity extends AppCompatActivity {

    private ActivityNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        Intent intent = getIntent();
        String activityTitle = intent.getStringExtra("title");
        binding.activityTitle.setText(activityTitle);

        getNewsData(intent.getStringExtra("id"));
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void getNewsData(String id) {
        binding.progressBar.setVisibility(View.VISIBLE);

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("21cm0146.main.jp");
        uriBuilder.path("jecnote/outputData.php");
        uriBuilder.appendQueryParameter("id", id);

        AsyncNewsDataRequest req = new AsyncNewsDataRequest(this);
        req.execute(uriBuilder);
    }
}