package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import jp.ac.jec.cm0146.jecnote.databinding.ActivitySchoolNewsBinding;

public class SchoolNewsActivity extends AppCompatActivity {

    private ActivitySchoolNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}