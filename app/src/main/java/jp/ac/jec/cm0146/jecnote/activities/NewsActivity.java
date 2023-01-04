package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityNewsBinding;

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
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}