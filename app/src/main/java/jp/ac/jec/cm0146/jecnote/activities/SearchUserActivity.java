package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import jp.ac.jec.cm0146.jecnote.databinding.ActivitySearchUserBinding;

public class SearchUserActivity extends AppCompatActivity {

    private ActivitySearchUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}