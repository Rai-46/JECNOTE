package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.databinding.ActivitySchoolGuideBinding;

public class SchoolGuideActivity extends AppCompatActivity {

    private ActivitySchoolGuideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}