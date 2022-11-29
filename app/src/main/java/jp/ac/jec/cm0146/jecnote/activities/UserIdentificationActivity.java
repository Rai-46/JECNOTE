package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityUserIdentificationBinding;

// ユーザが教員か学生かを見分けるView
public class UserIdentificationActivity extends AppCompatActivity {

    private ActivityUserIdentificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserIdentificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}